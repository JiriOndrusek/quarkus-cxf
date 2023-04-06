package io.quarkiverse.cxf.deployment;

import org.apache.cxf.tools.java2ws.JavaToWS;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

/**
 * {@link BuildStep}s related to {@code org.glassfish.jaxb:*}.
 */
class Java2WsdlProcessor {

    @BuildStep
    void java2wsdl(CxfBuildTimeConfig cxfBuildTimeConfig, BuildProducer<NativeImageResourceBuildItem> nativeResources,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {

        if (cxfBuildTimeConfig.wsdlgen.java2wsdl.enabled) {

            System.out.println("-----------------------------------");
            try {
                String[] p = new String[] { "-wsdl", "-V", "-o",
                        "GreeterService.wsdl",
                        //                        "-cp",
                        //                        "./tmp",
                        //                        //                        "/home/jondruse/git/community/camel-quarkus/integration-test-groups/cxf-soap/cxf-soap-client/src/resources/GreeterService.wsdl",
                        "-d",
                        cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir,
                        //                        "org.jboss.eap.quickstarts.wscalculator.calculator.CalculatorService" };
                        "io.quarkiverse.cxf.deployment.wsdlgen.GreeterService" };
                new JavaToWS(p).run();
                nativeResources.produce(new NativeImageResourceBuildItem(
                        cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir + "/GreeterService.wsdl"));
                System.out.println("-----------------------------------");
            } catch (Exception e) {
                throw new RuntimeException(new StringBuilder("Could not run wsdl2Java").toString(),
                        e);
            }
        }

        nativeResources.produce(new NativeImageResourceBuildItem(
                cxfBuildTimeConfig.wsdlgen.java2wsdl.outputDir + "/GreeterService.wsdl"));

        //todo remove, quickworkaround
        reflectiveClass.produce(ReflectiveClassBuildItem.builder(
                "org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl",
                "org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo").methods().build());
    }

}
