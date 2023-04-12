package io.quarkiverse.cxf.deployment.wsdlgen;

import java.io.IOException;
import java.nio.file.Path;

import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class WsdlNamedGenTest {

    @RegisterExtension
    public static final QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(GreeterService.class)
                    .addClass(FruitWebService.class)
                    .addClass(Fruit.class))
            .overrideConfigKey("quarkus.cxf.codegen.wsdl2java.enabled", "false")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.enabled", "true")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.output-dir", "target/WsdlNamedGenTest")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.\"GS\".includes", ".*GreeterService");

    @Test
    public void generationTest() throws IOException {
        Assertions.assertThat(Path.of("target/WsdlNamedGenTest").resolve("GreeterService.wsdl").toFile().exists())
                .as("check Greeterservice.wsdl existence").isTrue();
        Assertions.assertThat(Path.of("target/WsdlNamedGenTest").resolve("FruitWebService.wsdl").toFile().exists())
                .as("check FruitWebService.wsdl existence").isFalse();
    }
}
