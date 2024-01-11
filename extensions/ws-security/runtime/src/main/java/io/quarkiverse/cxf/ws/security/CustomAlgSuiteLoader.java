/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.quarkiverse.cxf.ws.security;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.policy.custom.AlgorithmSuiteLoader;
import org.apache.neethi.Policy;
import org.apache.wss4j.policy.SPConstants;
import org.apache.wss4j.policy.model.AbstractSecurityAssertion;
import org.apache.wss4j.policy.model.AlgorithmSuite;
import org.eclipse.microprofile.config.Config;

/**
 * This class retrieves a custom AlgorithmSuite for use with restricted security policies
 */
public class CustomAlgSuiteLoader implements AlgorithmSuiteLoader {

    public static final String DIGEST_ALGORITHM = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String ENCRYPTION_ALGHORITM = "http://www.w3.org/2009/xmlenc11#aes256-gcm";
    public static final String SYMMETRIC_KEY_ENCRYPTION_ALGORITHM = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
    public static final String ASYMMETRIC_KEY_ENCRYPTION_ALGORITHM = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    public static final String ENCRYPTION_KEY_DERIVATION = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    public static final String SIGNATURE_KEY_DERIVATION = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    public static final int ENCRYPTION_KEY_DERIVED_LENGTH = 256;
    public static final int SIGNATURE_KEY_DERIVED_LENGTH = 192;
    public static final int MINIMUM_SYMMETRIC_KEY_LENGTH = 256;
    public static final int MAXIMUM_SYMMETRIC_KEY_LENGTH = 256;
    public static final int MINIMUM_ASYMMETRIC_KEY_LENGTH = 1024;
    public static final int MAXIMUM_ASYMMETRIC_KEY_LENGTH = 4096;

    private AlgorithmSuite.AlgorithmSuiteType customAlgSuiteType;

    public CustomAlgSuiteLoader(Bus bus, Config config) {

        if (config.getValue("quarkus.cxf.customizedAlgorithmSuite.enabled", Boolean.class)) {
            customAlgSuiteType = new AlgorithmSuite.AlgorithmSuiteType("CustomizedAlgorithmSuite",
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.digest-algorithm", String.class)
                            .orElse(DIGEST_ALGORITHM),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.encryption-algorithm", String.class)
                            .orElse(ENCRYPTION_ALGHORITM),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.symmetric-key-encryption-algorithm",
                            String.class).orElse(SYMMETRIC_KEY_ENCRYPTION_ALGORITHM),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.asymmetric-key-encryption-algorithm",
                            String.class).orElse(ASYMMETRIC_KEY_ENCRYPTION_ALGORITHM),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.encryption-key-derivation", String.class)
                            .orElse(ENCRYPTION_KEY_DERIVATION),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.signature-key-derivation", String.class)
                            .orElse(SIGNATURE_KEY_DERIVATION),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.encryption-key-derived-length", Integer.class)
                            .orElse(ENCRYPTION_KEY_DERIVED_LENGTH),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.signature-key-derived-length", Integer.class)
                            .orElse(SIGNATURE_KEY_DERIVED_LENGTH),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.minimum-symmetric-key-length", Integer.class)
                            .orElse(MINIMUM_SYMMETRIC_KEY_LENGTH),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.maximum-symmetric-key-length", Integer.class)
                            .orElse(MAXIMUM_SYMMETRIC_KEY_LENGTH),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.minimum-asymmetric-key-length", Integer.class)
                            .orElse(MINIMUM_ASYMMETRIC_KEY_LENGTH),
                    config.getOptionalValue("quarkus.cxf.customizedAlgorithmSuite.maximum-asymmetric-key-length", Integer.class)
                            .orElse(MAXIMUM_ASYMMETRIC_KEY_LENGTH));

            CustomAlgorithmSuite.register(customAlgSuiteType);
            bus.setExtension(this, AlgorithmSuiteLoader.class);
        }
    }

    public CustomAlgSuiteLoader(Bus bus, AlgorithmSuite.AlgorithmSuiteType customAlgSuiteType) {
        CustomAlgorithmSuite.register(customAlgSuiteType);
        bus.setExtension(this, AlgorithmSuiteLoader.class);
        this.customAlgSuiteType = customAlgSuiteType;
    }

    @Override
    public AlgorithmSuite getAlgorithmSuite(Bus bus, SPConstants.SPVersion version, Policy nestedPolicy) {
        return new CustomAlgorithmSuite(version, nestedPolicy, customAlgSuiteType);
    }

    private class CustomAlgorithmSuite extends AlgorithmSuite {

        static void register(AlgorithmSuiteType customAlgSuiteType) {
            ALGORITHM_SUITE_TYPES.put(customAlgSuiteType.getName(), customAlgSuiteType);
        }

        CustomAlgorithmSuite(SPConstants.SPVersion version, Policy nestedPolicy, AlgorithmSuiteType customAlgSuiteType) {
            super(version, nestedPolicy);
        }

        @Override
        protected AbstractSecurityAssertion cloneAssertion(Policy nestedPolicy) {
            return new CustomAlgorithmSuite(getVersion(), nestedPolicy, customAlgSuiteType);
        }

    }

}
