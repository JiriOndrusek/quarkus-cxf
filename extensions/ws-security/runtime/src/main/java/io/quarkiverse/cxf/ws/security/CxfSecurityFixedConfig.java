package io.quarkiverse.cxf.ws.security;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.cxf")
@ConfigDocFilename("quarkus-cxf-rt-ws-security.adoc")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface CxfSecurityFixedConfig {
    /**
     * Parameters for fully customizable algorithm suite.
     */
    @WithName("customAlgorithmSuite")
    public CustomAlgorithmSuite customAlgorithmSuite();

    @ConfigGroup
    public interface CustomAlgorithmSuite {

        /**
         * If algorithm suite with the identifier is loaded into the cxf bus
         * <i>CustomizedAlgorithmSuite</i> it can be fully customized.
         * Suggested usage is for scenarios for the non-standard security requirements (like FIPS).
         *
         * <p>
         * Default values are derived from the algorithm suite <i>Basic256Sha256Rsa15</i> and are FIPS compliant.
         * <p>
         * </p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         *
         * <ul>
         * <li>Asymmetric Signature: http://www.w3.org/2001/04/xmldsig-more#rsa-sha256</li>
         * <li>Symmetric Signature: http://www.w3.org/2000/09/xmldsig#hmac-sha1</li>
         * <li>Digest Algorithm: http://www.w3.org/2001/04/xmlenc#sha256</li>
         * <li>Encryption Algorithm: http://www.w3.org/2009/xmlenc11#aes256-gcm (differs from <i>Basic256Sha256Rsa15</i>)</li>
         * <li>Symmetric Key Encryption Algorithm: http://www.w3.org/2001/04/xmlenc#kw-aes256</li>
         * <li>Asymmetric Key Encryption Algorithm: http://www.w3.org/2001/04/xmlenc#rsa-1_5</li>
         * <li>Encryption Key Derivation: http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1</li>
         * <li>Signature Key Derivation: http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1</li>
         * <li>Encryption Derived Key Length: 256</li>
         * <li>Signature Derived Key Length: 192</li>
         * <li>Minimum Symmetric Key Length: 256</li>
         * <li>Maximum Symmetric Key Length: 1024</li>
         * <li>Minimum Asymmetric Key Length: 256</li>
         * <li>Maximum Asymmetric Key Length: 4096</li>
         * </ul>
         * </p>
         */
        public static final String CUSTOM_ALGORITHM_SUITE_NAME = "CustomAlgorithmSuite";

        /**
         * Digest Algorithm.
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("http://www.w3.org/2001/04/xmlenc#sha256")
        public String digestAlgorithm();

        /**
         * Encryption Algorithm.
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("http://www.w3.org/2009/xmlenc11#aes256-gcm")
        public String encryptionAlgorithm();

        /**
         * Symmetric Key Encryption Algorithm.
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("http://www.w3.org/2001/04/xmlenc#kw-aes256")
        public String symmetricKeyEncryptionAlgorithm();

        /**
         * Asymmetric Key Encryption Algorithm.
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("http://www.w3.org/2001/04/xmlenc#rsa-1_5")
        public String asymmetricKeyEncryptionAlgorithm();

        /**
         * Encryption Key Derivation. For more information about algorithms, see WS-SecurityPolicy 1.2 and security algorithms
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1")
        public String encryptionKeyDerivation();

        /**
         * Signature Key Derivation.
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1")
        public String signatureKeyDerivation();

        /**
         * Encryption Derived Key Length (number of bits).
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("256")
        public Integer encryptionDerivedKeyLength();

        /**
         * Signature Derived Key Length (number of bits).
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("192")
        public Integer signatureDerivedKeyLength();

        /**
         * Minimum Symmetric Key Length (number of bits).
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("256")
        public Integer minimumSymmetricKeyLength();

        /**
         * Maximum Symmetric Key Length (number of bits).
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("256")
        public Integer maximumSymmetricKeyLength();

        /**
         * Minimum Symmetric Key Length (number of bits).
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("1024")
        public Integer minimumAsymmetricKeyLength();

        /**
         * Maximum Symmetric Key Length (number of bits).
         * <p>
         * For more information about algorithms, see
         * <a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">WS-SecurityPolicy
         * 1.2</a:href="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.2/errata01/os/ws-securitypolicy-1.2-errata01-os-complete.html#_Toc325572744">
         * and <a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">security
         * algorithms</a></a:href="https://www.w3.org/TR/xmlenc-core1/#sec-Algorithms">
         * </p>
         */
        @WithDefault("4096")
        public Integer maximumAsymmetricKeyLength();
    }
}
