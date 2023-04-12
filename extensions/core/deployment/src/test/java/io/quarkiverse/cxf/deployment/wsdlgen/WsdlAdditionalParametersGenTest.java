package io.quarkiverse.cxf.deployment.wsdlgen;

import java.io.IOException;
import java.nio.file.Path;

import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class WsdlAdditionalParametersGenTest {

    @RegisterExtension
    public static final QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(GreeterService.class)
                    .addClass(FruitWebService.class)
                    .addClass(Fruit.class))
            .overrideConfigKey("quarkus.cxf.codegen.wsdl2java.enabled", "false")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.enabled", "true")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.output-dir", "target/WsdlAdditionalParametersGenTest")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.includes", ".*")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.additional-params", "-h")
            .setLogRecordPredicate(lr -> lr.getMessage().contains("Running wsdl2java"))
            .assertLogRecords(
                    lrs -> {
                        if (!lrs.stream()
                                .anyMatch(logRecord -> logRecord.getMessage().contains("-h io.quarkiverse.cxf.deployment.wsdlgen.GreeterService"))) {
                            Assertions.fail("There is no help message in the log.");
                        }
                    });

    @Test
    public void generationTest() throws IOException {
        //tool will show help and will not generate wsdl
        Assertions
                .assertThat(Path.of("target/WsdlAdditionalParametersGenTest").resolve("GreeterService.wsdl").toFile().exists())
                .as("check Greeterservice.wsdl existence").isFalse();
        Assertions
                .assertThat(Path.of("target/WsdlAdditionalParametersGenTest").resolve("FruitWebService.wsdl").toFile().exists())
                .as("check FruitWebService.wsdl existence").isFalse();

    }

}
