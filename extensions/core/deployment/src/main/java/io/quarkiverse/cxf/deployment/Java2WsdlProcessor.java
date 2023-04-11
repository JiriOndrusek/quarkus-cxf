package io.quarkiverse.cxf.deployment;

import io.quarkiverse.cxf.deployment.codegen.Wsdl2JavaCodeGen;
import io.quarkus.deployment.annotations.Produce;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.pkg.builditem.ArtifactResultBuildItem;
import io.quarkus.paths.DirectoryPathTree;
import io.quarkus.paths.PathFilter;
import jakarta.jws.WebService;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.java2ws.JavaToWS;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import org.apache.cxf.tools.wsdlto.WSDLToJava;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

class Java2WsdlProcessor {

    private static final Logger log = Logger.getLogger(Java2WsdlProcessor.class);
    public static final String JAVA2WSDL_CONFIG_KEY_PREFIX = "quarkus.cxf.codegen.java2wsdl";
    private static final Path SRC_MAIN_RESOURCES = Paths.get("src/main/resources");
    private static final Path SRC_TEST_RESOURCES = Paths.get("src/test/resources");

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


        final Path outDir = Path.of(cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir);
        final Map<String, String> processedClasses = new HashMap<>();
        boolean result = false;
        result |= java2wsdl(services, cxfBuildTimeConfig.wsdlgen.java2wsdl.rootParameterSet, outDir, JAVA2WSDL_CONFIG_KEY_PREFIX, processedClasses);

     resources.produce(new NativeImageResourceBuildItem("target/WsdlGenTest/GreeterService.wsdl"));
     reflectiveClass.produce(ReflectiveClassBuildItem.builder("org.glassfish.jaxb.core.marshaller.MinimumEscapeHandler")
             .build());
    }


    static boolean java2wsdl(String[] serviceClasses, CxfBuildTimeConfig.Java2WsdlParameterSet params, Path outDir, String prefix,
                             Map<String, String> processedClasses) {

        return scan(serviceClasses, params.includes, params.excludes, prefix, processedClasses, (String serviceClass) -> {
            final Java2WsdlParams java2WsdlParams = new Java2WsdlParams(serviceClass, outDir,
                    params.additionalParams.orElse(Collections.emptyList()));
            if (log.isInfoEnabled()) {
                log.info(java2WsdlParams.appendLog(new StringBuilder("Running wsdl2java")).toString());
            }
            final ToolContext ctx = new ToolContext();
            try {
                new JavaToWS(java2WsdlParams.toParameterArray()).run();
            } catch (Exception e) {
                throw new RuntimeException(java2WsdlParams.appendLog(new StringBuilder("Could not run wsdl2Java")).toString(),
                        e);
            }
        });
    }


    public static boolean scan(
            String[] classes,
            Optional<String> includes,
            Optional<String> excludes,
            String prefix,
            Map<String, String> processedClasses,
            Consumer<String> serviceClassConsumer) {

        final String selectors = "    " + prefix + ".includes = "
                + includes.get()
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
                .filter(cl -> includePatter.isEmpty() || includePatter.get().matcher(cl).matches())
                .filter(cl -> excludePatter.isEmpty() || excludePatter.get().matcher(cl).matches())
                .forEach(chainedConsumer::accept);

        return !processedClasses.isEmpty();
    }

    static Path absModuleRoot(final Path inputDir) {
        if (inputDir.endsWith(SRC_MAIN_RESOURCES) || inputDir.endsWith(SRC_TEST_RESOURCES)) {
            return inputDir.getParent().getParent().getParent();
        } else {
            return inputDir.getParent().getParent();
            //todo
//            throw new IllegalStateException(
//                    "inputDir '" + inputDir + "' expected to end with " + SRC_MAIN_RESOURCES + " or " + SRC_TEST_RESOURCES);
        }
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
            final Path moduleRoot = absModuleRoot(outDir);
            render(path -> moduleRoot.relativize(path).toString(), value -> sb.append(' ').append(value));
            return sb;
        }

        public String[] toParameterArray() {
            final String[] result = new String[additionalParams.size() + 7];
            final AtomicInteger i = new AtomicInteger(0);
            render(Path::toString, value -> result[i.getAndIncrement()] = value);
            return result;
        }

        void render(Function<Path, String> pathTransformer, Consumer<String> paramConsumer) {
            paramConsumer.accept("-wsdl");
            paramConsumer.accept("-V");
            paramConsumer.accept( "-o");
            paramConsumer.accept( inputClass.substring(inputClass.lastIndexOf(".")+1)+".wsdl");
            paramConsumer.accept("-d");
            paramConsumer.accept(pathTransformer.apply(outDir));
            additionalParams.forEach(paramConsumer);
            paramConsumer.accept(inputClass);
        }

    }

}
