package io.quarkiverse.cxf.deployment.wsdlgen;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class WsdlConflictGenTest {

    @RegisterExtension
    public static final QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(GreeterService.class)
                    .addClass(FruitWebService.class)
                    .addClass(Fruit.class))
            .overrideConfigKey("quarkus.cxf.codegen.wsdl2java.enabled", "false")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.enabled", "true")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.output-dir", "target/WsdlGenTest")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.includes", ".*")
            .overrideConfigKey("quarkus.cxf.wsdlgen.java2wsdl.\"group_01\".includes", ".*")
            .setExpectedException(IllegalStateException.class);

    @Test
    public void generationTest() throws IOException {
        Assertions.fail("Extension should not start");
    }
}
