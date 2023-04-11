package io.quarkiverse.cxf.deployment;

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
import java.util.regex.Pattern;

import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.java2ws.JavaToWS;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

import io.quarkiverse.cxf.Java2WsdRecorder;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class Java2WsdlProcessor {

    private static final Logger log = Logger.getLogger(Java2WsdlProcessor.class);
    public static final String JAVA2WSDL_CONFIG_KEY_PREFIX = "quarkus.cxf.codegen.java2wsdl";
    private static final Path SRC_MAIN_RESOURCES = Paths.get("src/main/resources");
    private static final Path SRC_TEST_RESOURCES = Paths.get("src/test/resources");

    //    @BuildStep
    //    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> services, CombinedIndexBuildItem combinedIndex) {
    //        //        Stream.of("jakarta.mail.util.StreamProvider", "jakarta.mail.Provider");
    //        IndexView index = combinedIndex.getIndex();
    //        index.getKnownClasses().stream().peek(i -> System.out.println("" + i.name()));
    //
    //        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    //
    //        index.getKnownClasses()
    //                .stream()
    //                .filter(classInfo -> classInfo.name().prefix().toString().equals("jakarta.activation.spi.")
    //                        && classInfo.name().toString().endsWith("Provider"))
    //                .peek(ci -> System.out.println("---" + ci))
    //                .forEach(classInfo -> index.getKnownDirectImplementors(classInfo.name())
    //                        .stream()
    //                        .peek(ci -> System.out.println("-------" + ci))
    //                        .forEach(service -> services.produce(
    //                                new ServiceProviderBuildItem(classInfo.simpleName(),
    //                                        service.simpleName()))));
    //    }
    //
    //     index.getAnnotations(DotName.createSimple(Hl7Terser.class.getName()))
    //            .stream()
    //                .map(AnnotationInstance::target)
    //                .map(annotationTarget -> {
    //        if (annotationTarget.kind().equals(AnnotationTarget.Kind.FIELD)) {
    //            return annotationTarget.asType().asClass();
    //        } else if (annotationTarget.kind().equals(AnnotationTarget.Kind.METHOD)) {
    //            return annotationTarget.asMethod().declaringClass();
    //        } else if (annotationTarget.kind().equals(AnnotationTarget.Kind.METHOD_PARAMETER)) {
    //            return annotationTarget.asMethodParameter().method().declaringClass();
    //        }
    //        return null;
    //    })
    //            .filter(CamelSupport::isConcrete)
    //                .map(classInfo -> classInfo.name().toString())
    //            .toArray(String[]::new);
    //
    //
    //    @BuildStep
    //    void java2wsdl(CxfBuildTimeConfig cxfBuildTimeConfig, BuildProducer<NativeImageResourceBuildItem> nativeResources,
    //                   BuildProducer<ReflectiveClassBuildItem> reflectiveClass, CombinedIndexBuildItem combinedIndex) {
    //
    //        IndexView index = combinedIndex.getIndex();
    //
    //        if (!cxfBuildTimeConfig.wsdlgen.java2wsdl.enabled) {
    //            log.info("Skipping " + this.getClass() + " invocation on user's request");
    //            return;
    //        }
    //
    //        String[] services = index.getAnnotations(DotName.createSimple(WebService.class.getName()))
    //                .stream()
    //                .map(AnnotationInstance::target)
    //                .map(annotationTarget -> {
    //                    if (annotationTarget.kind().equals(AnnotationTarget.Kind.CLASS)) {
    //                        return annotationTarget.asClass();
    //                    }
    //                    return null;
    //                })
    //                .filter(ci -> ci != null)
    //                .map(classInfo -> classInfo.name().toString())
    //                .toArray(String[]::new);
    //
    //
    //        final Path outDir = Path.of(cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir);
    //        final Map<String, String> processedClasses = new HashMap<>();
    //        boolean result = false;
    //        result |= java2wsdl(services, cxfBuildTimeConfig.wsdlgen.java2wsdl.rootParameterSet, outDir, JAVA2WSDL_CONFIG_KEY_PREFIX, processedClasses);
    //
    //
    //
    //
    //
    //
    ////        if (cxfBuildTimeConfig.wsdlgen.java2wsdl.enabled) {
    ////
    ////            System.out.println("-----------------------------------");
    //        try {
    //            String[] p = new String[] { "-wsdl", "-V", "-o",
    //                    "GreeterService.wsdl",
    //                    //                        "-cp",
    //                    //                        "./tmp",
    //                    //                        //                        "/home/jondruse/git/community/camel-quarkus/integration-test-groups/cxf-soap/cxf-soap-client/src/resources/GreeterService.wsdl",
    //                    "-d",
    //                    cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir,
    //                    //                        "org.jboss.eap.quickstarts.wscalculator.calculator.CalculatorService" };
    //                    "io.quarkiverse.cxf.deployment.wsdlgen.GreeterService" };
    //            new JavaToWS(p).run();
    //            nativeResources.produce(new NativeImageResourceBuildItem(
    //                    cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir + "/GreeterService.wsdl"));
    //            System.out.println("-----------------------------------");
    //        } catch (Exception e) {
    //            throw new RuntimeException(new StringBuilder("Could not run wsdl2Java").toString(),
    //                    e);
    //        }
    ////        }
    //
    //        nativeResources.produce(new NativeImageResourceBuildItem(
    //                cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir + "/GreeterService.wsdl"));
    //
    //        //todo remove, quickworkaround
    //        reflectiveClass.produce(ReflectiveClassBuildItem.builder(
    //                "org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl",
    //                "org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo").methods().build());
    //    }

    @BuildStep
    void registerJava2Wsdl(CxfBuildTimeConfig cxfBuildTimeConfig, BuildProducer<CxfJava2WsdlBuildItem> java2WsdlBuildItems,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass, CombinedIndexBuildItem combinedIndex) {
        IndexView index = combinedIndex.getIndex();

        if (!cxfBuildTimeConfig.wsdlgen.java2wsdl.enabled) {
            log.info("Skipping " + this.getClass() + " invocation on user's request");
            return;
        }
        //
        //        String[] services = index.getAnnotations(DotName.createSimple(WebService.class.getName()))
        //                .stream()
        //                .map(AnnotationInstance::target)
        //                .map(annotationTarget -> {
        //                    if (annotationTarget.kind().equals(AnnotationTarget.Kind.CLASS)) {
        //                        return annotationTarget.asClass();
        //                    }
        //                    return null;
        //                })
        //                .filter(ci -> ci != null)
        //                .map(classInfo -> classInfo.name().toString())
        //                .toArray(String[]::new);

        java2WsdlBuildItems.produce(
                new CxfJava2WsdlBuildItem("io.quarkiverse.cxf.deployment.wsdlgen.GreeterService",
                        "target/WsdlGenTest",
                        Collections.emptyList()));

        java2WsdlBuildItems.produce(
                new CxfJava2WsdlBuildItem("io.quarkiverse.cxf.deployment.wsdlgen.FruitWebService",
                        "target/WsdlGenTest",
                        Collections.emptyList()));

        //        reflectiveClass.produce(ReflectiveClassBuildItem.builder(
        //                "org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl",
        //                "org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo").methods().build());
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void generateWsdls(Java2WsdRecorder recorder, CxfBuildTimeConfig cxfBuildTimeConfig,
            BuildProducer<NativeImageResourceBuildItem> nativeResources,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass, List<CxfJava2WsdlBuildItem> wsdlBuildItems) {

        final Map<String, String> processedClasses = new HashMap<>();

        for (CxfJava2WsdlBuildItem wsdlItem : wsdlBuildItems) {
            recorder.generateWsdl(wsdlItem.getInputClass(), wsdlItem.getAdditionalParams(), wsdlItem.getOutDir());
            //            java2wsdl(new String[] { wsdlItem.getInputClass() }, cxfBuildTimeConfig.wsdlgen.java2wsdl.rootParameterSet,
            //                    wsdlItem.getOutDir(),
            //                    JAVA2WSDL_CONFIG_KEY_PREFIX, processedClasses);
            nativeResources.produce(new NativeImageResourceBuildItem(
                    cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir + "/GreeterService.wsdl"));
        }
        //        reflectiveClass.produce(ReflectiveClassBuildItem.builder(
        //                "org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl",
        //                "org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo").methods().build());
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
            final ToolContext ctx = new ToolContext();
            try {
                JavaToWS jtw = new JavaToWS(java2WsdlParams.toParameterArray());
                jtw.run();
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
            render(value -> sb.append(' ').append(value));
            return sb;
        }

        public String[] toParameterArray() {
            final String[] result = new String[additionalParams.size() + 7];
            final AtomicInteger i = new AtomicInteger(0);
            render(value -> result[i.getAndIncrement()] = value);
            return result;
        }

        void render(Consumer<String> paramConsumer) {
            paramConsumer.accept("-wsdl");
            paramConsumer.accept("-V");
            paramConsumer.accept("-o");
            paramConsumer.accept(inputClass.substring(inputClass.lastIndexOf(".") + 1) + ".wsdl");
            paramConsumer.accept("-d");
            paramConsumer.accept(outDir.toString());
            additionalParams.forEach(paramConsumer);
            paramConsumer.accept(inputClass);
        }

    }

}
