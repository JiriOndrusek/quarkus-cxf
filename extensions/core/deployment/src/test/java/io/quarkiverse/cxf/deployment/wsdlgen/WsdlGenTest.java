package io.quarkiverse.cxf.deployment.wsdlgen;

import java.io.IOException;
import java.nio.file.Path;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class WsdlGenTest {

    @RegisterExtension
    public static final QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(GreeterService.class))
            //                    .addClass(FruitWebService.class)
            //                    .addClass(Fruit.class))
            .withConfigurationResource("application-cxf-wsdlgen.properties");

    @Test
    public void simpleGenerationTest() throws IOException {
        Assertions.assertTrue(Path.of("target/WsdlGenTest").resolve("GreeterService.wsdl").toFile().exists());
    }
}
