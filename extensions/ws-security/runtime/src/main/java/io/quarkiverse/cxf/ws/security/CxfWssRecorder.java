package io.quarkiverse.cxf.ws.security;

import java.util.Optional;
import java.util.function.Consumer;

import org.apache.cxf.Bus;

import io.quarkiverse.cxf.ws.security.fips.FipsAlgorithmSuiteCustomizer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class CxfWssRecorder {

    public RuntimeValue<Consumer<Bus>> addFipsAlgorithmSuiteLoader(String suiteToCustomize,
            Optional<String> encryption) {
        return new RuntimeValue((Consumer<Bus>) bus -> new FipsAlgorithmSuiteCustomizer(bus, suiteToCustomize, encryption));
    }
}
