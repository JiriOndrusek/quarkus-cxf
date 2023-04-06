package io.quarkiverse.cxf.deployment;

import org.apache.cxf.tools.java2ws.JavaToWS;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

/**
 * {@link BuildStep}s related to {@code org.glassfish.jaxb:*}.
 */
class Java2WsdlProcessor {

    @BuildStep
    void java2wsdl(CxfBuildTimeConfig cxfBuildTimeConfig,
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
                System.out.println("-----------------------------------");
            } catch (Exception e) {
                throw new RuntimeException(new StringBuilder("Could not run wsdl2Java").toString(),
                        e);
            }
        }

        reflectiveClass.produce(ReflectiveClassBuildItem.builder(
                "org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl",
                "org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo").methods().build());
    }

}
