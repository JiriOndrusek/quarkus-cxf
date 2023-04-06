package io.quarkiverse.cxf.deployment.wsdlgen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class WsdlGenTest {

    @RegisterExtension
    public static final QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(GreeterService.class))
            .withConfigurationResource("application-cxf-wsdlgen.properties");

    @Test
    public void simpleGenerationTest() throws IOException {
        Assertions.assertThat(Files.newDirectoryStream(Path.of("target/WsdlGenTest"))).containsExactlyInAnyOrder(
                Path.of("target/WsdlGenTest").resolve("GreeterService.wsdl"));
    }
}
