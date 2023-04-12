package io.quarkiverse.cxf.deployment;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import jakarta.jws.WebService;

import org.apache.cxf.tools.java2ws.JavaToWS;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.builditem.ArtifactResultBuildItem;

class Java2WsdlProcessor {

    private static final Logger log = Logger.getLogger(Java2WsdlProcessor.class);
    public static final String JAVA2WSDL_CONFIG_KEY_PREFIX = "quarkus.cxf.codegen.java2wsdl";

    @BuildStep
    void java2wsdl(CxfBuildTimeConfig cxfBuildTimeConfig, BuildProducer<NativeImageResourceBuildItem> resources,
            BuildProducer<ArtifactResultBuildItem> artifactResultProducer, CombinedIndexBuildItem combinedIndex,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {

        IndexView index = combinedIndex.getIndex();

        if (!cxfBuildTimeConfig.wsdlgen.java2wsdl.enabled) {
            log.info("Skipping " + this.getClass() + " invocation on user's request");
            return;
        }

        String[] services = index.getAnnotations(DotName.createSimple(WebService.class.getName()))
                .stream()
                .map(AnnotationInstance::target)
                .map(annotationTarget -> {
                    if (annotationTarget.kind().equals(AnnotationTarget.Kind.CLASS)) {
                        return annotationTarget.asClass();
                    }
                    return null;
                })
                .filter(ci -> ci != null)
                .map(classInfo -> classInfo.name().toString())
                .toArray(String[]::new);

        final Path outDir = Path.of(cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir.orElse("target"));
        final Map<String, String> processedClasses = new HashMap<>();
        boolean result = false;
        //root
        result |= java2wsdl(services, cxfBuildTimeConfig.wsdlgen.java2wsdl.rootParameterSet, outDir,
                JAVA2WSDL_CONFIG_KEY_PREFIX, processedClasses);

        //named
        final Set<String> names = cxfBuildTimeConfig.wsdlgen.java2wsdl.namedParameterSets.keySet();
        for (String name : names) {
            CxfBuildTimeConfig.Java2WsdlParameterSet params = cxfBuildTimeConfig.wsdlgen.java2wsdl.namedParameterSets.get(name);
            result |= java2wsdl(services, params, outDir, JAVA2WSDL_CONFIG_KEY_PREFIX + "." + name, processedClasses);
        }

        if (!result) {
            log.warn("java2wsdl processed 0 classes");
        }
    }

    static boolean java2wsdl(String[] serviceClasses, CxfBuildTimeConfig.Java2WsdlParameterSet params, Path outDir,
            String prefix,
            Map<String, String> processedClasses) {

        return scan(serviceClasses, params.includes, params.excludes, prefix, processedClasses, (String serviceClass) -> {
            final Java2WsdlParams java2WsdlParams = new Java2WsdlParams(serviceClass, outDir,
                    params.additionalParams.orElse(Collections.emptyList()));
            if (log.isInfoEnabled()) {
                log.info(java2WsdlParams.appendLog(new StringBuilder("Running wsdl2java")).toString());
            }
            try {
                new JavaToWS(java2WsdlParams.toParameterArray()).run();
            } catch (Exception e) {
                throw new RuntimeException(java2WsdlParams.appendLog(new StringBuilder("Could not run wsdl2Java")).toString(),
                        e);
            }
        });
    }

    static Set<String> findParamSetNames(Iterable<String> propertyNames) {
        final Set<String> result = new TreeSet<>();
        for (String key : propertyNames) {
            if (key.startsWith(JAVA2WSDL_CONFIG_KEY_PREFIX)) {
                Stream.of(".includes", ".excludes", ".additional-params")
                        .filter(suffix -> key.endsWith(suffix))
                        .findFirst()
                        .ifPresent(suffix -> {
                            if (JAVA2WSDL_CONFIG_KEY_PREFIX.length() + suffix.length() < key.length()) {
                                /* this is a named param set key */
                                final String name = key.substring(JAVA2WSDL_CONFIG_KEY_PREFIX.length(),
                                        key.length() - suffix.length());
                                result.add(name);
                            }
                        });
            }
        }
        return result;
    }

    static boolean scan(
            String[] classes,
            Optional<String> includes,
            Optional<String> excludes,
            String prefix,
            Map<String, String> processedClasses,
            Consumer<String> serviceClassConsumer) {

        if (includes.isEmpty()) {
            return false;
        }

        final String selectors = "    " + prefix + ".includes = " + includes.get()
                + (excludes.isPresent()
                        ? "\n    " + prefix + ".excludes = " + excludes.get()
                        : "");

        final Consumer<String> chainedConsumer = serviceClass -> {
            final String oldSelectors = processedClasses.get(serviceClass.toString());
            if (oldSelectors != null) {
                throw new IllegalStateException("Service class " + serviceClass + " was already selected by\n\n"
                        + oldSelectors
                        + "\n\nand therefore it cannot once again be selected by\n\n" + selectors
                        + "\n\nPlease make sure that the individual include/exclude sets are mutually exclusive.");
            }
            processedClasses.put(serviceClass, selectors);
            serviceClassConsumer.accept(serviceClass);
        };

        Optional<Pattern> includePatter = includes.map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE));
        Optional<Pattern> excludePatter = excludes.map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE));

        Arrays.stream(classes)
                .filter(cl -> includePatter.get().matcher(cl).matches())
                .filter(cl -> excludePatter.isEmpty() || excludePatter.get().matcher(cl).matches())
                .forEach(chainedConsumer::accept);

        return !processedClasses.isEmpty();
    }

    static class Java2WsdlParams {
        private final String inputClass;
        private final Path outDir;
        private final List<String> additionalParams;

        public Java2WsdlParams(String inputClass, Path outDir, List<String> additionalParams) {
            super();
            this.inputClass = inputClass;
            this.outDir = outDir;
            this.additionalParams = additionalParams;
        }

        public StringBuilder appendLog(StringBuilder sb) {
            render(value -> sb.append(' ').append(value));
            return sb;
        }

        public String[] toParameterArray() {
            final String[] result = new String[additionalParams.size() + 6];
            final AtomicInteger i = new AtomicInteger(0);
            render(value -> result[i.getAndIncrement()] = value);
            return result;
        }

        void render(Consumer<String> paramConsumer) {
            paramConsumer.accept("-wsdl");
            paramConsumer.accept("-o");
            paramConsumer.accept(inputClass.substring(inputClass.lastIndexOf(".") + 1) + ".wsdl");
            paramConsumer.accept("-d");
            paramConsumer.accept(outDir.toString());
            additionalParams.forEach(paramConsumer);
            paramConsumer.accept(inputClass);
        }

    }

}
