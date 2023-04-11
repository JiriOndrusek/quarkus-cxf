package io.quarkiverse.cxf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.cxf.tools.java2ws.JavaToWS;
import org.jboss.logging.Logger;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class Java2WsdRecorder {

    private static final Logger log = Logger.getLogger(Java2WsdRecorder.class);
    public static final String JAVA2WSDL_CONFIG_KEY_PREFIX = "quarkus.cxf.codegen.java2wsdl";
    private static final Path SRC_MAIN_RESOURCES = Paths.get("src/main/resources");
    private static final Path SRC_TEST_RESOURCES = Paths.get("src/test/resources");

    public RuntimeValue<String> generateWsdl(String serviceClass, List<String> additionalParams,
            String outDir) {

        return new RuntimeValue(java2wsdl(serviceClass, additionalParams, outDir));
    }

    static boolean java2wsdl(String serviceClass, List<String> additionalParams,
            String outDir) {

        final Java2WsdlParams java2WsdlParams = new Java2WsdlParams(serviceClass, outDir, additionalParams);
        if (log.isInfoEnabled()) {
            log.info(java2WsdlParams.appendLog(new StringBuilder("Running wsdl2java")).toString());
        }
        try {
            JavaToWS jtw = new JavaToWS(java2WsdlParams.toParameterArray());
            jtw.run();
        } catch (Exception e) {
            throw new RuntimeException(java2WsdlParams.appendLog(new StringBuilder("Could not run wsdl2Java")).toString(),
                    e);
        }
        return false;
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
        private final String outDir;
        private final List<String> additionalParams;

        public Java2WsdlParams(String inputClass, String outDir, List<String> additionalParams) {
            super();
            this.inputClass = inputClass;
            this.outDir = outDir;
            this.additionalParams = additionalParams;
        }

        public StringBuilder appendLog(StringBuilder sb) {
            //            final Path moduleRoot = absModuleRoot(outDir);
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
