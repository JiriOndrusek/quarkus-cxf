package io.quarkiverse.cxf.ws.security;

import java.util.function.Consumer;

import org.apache.cxf.Bus;
import org.apache.wss4j.policy.model.AlgorithmSuite;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class CxfWssRecorder {

    public RuntimeValue<Consumer<Bus>> customizedAlgorithmSuite(String name,
            String digest,
            String encryption,
            String symmetricKeyEncryption,
            String asymmetricKeyEncryption,
            String encryptionKeyDerivation,
            String signatureKeyDerivation,
            int encryptionDerivedKeyLength,
            int signatureDerivedKeyLength,
            int minimumSymmetricKeyLength,
            int maximumSymmetricKeyLength,
            int minimumAsymmetricKeyLength,
            int maximumAsymmetricKeyLength) {
        return new RuntimeValue((Consumer<Bus>) bus -> new CustomAlgSuiteLoader(bus, new AlgorithmSuite.AlgorithmSuiteType(
                name,
                digest,
                encryption,
                symmetricKeyEncryption,
                asymmetricKeyEncryption,
                encryptionKeyDerivation,
                signatureKeyDerivation,
                encryptionDerivedKeyLength,
                signatureDerivedKeyLength,
                minimumSymmetricKeyLength,
                maximumSymmetricKeyLength,
                minimumAsymmetricKeyLength,
                maximumAsymmetricKeyLength)));
    }
}
