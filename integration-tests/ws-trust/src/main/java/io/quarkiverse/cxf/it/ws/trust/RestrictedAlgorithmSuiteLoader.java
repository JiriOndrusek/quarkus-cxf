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
package io.quarkiverse.cxf.it.ws.trust;

import java.security.Provider;
import java.security.Security;
import java.util.Set;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.policy.custom.AlgorithmSuiteLoader;
import org.apache.neethi.Policy;
import org.apache.wss4j.policy.SPConstants;
import org.apache.wss4j.policy.model.AbstractSecurityAssertion;
import org.apache.wss4j.policy.model.AlgorithmSuite;

/**
 * This class retrieves a custom AlgorithmSuite for use with restricted security policies
 */
public class RestrictedAlgorithmSuiteLoader implements AlgorithmSuiteLoader {

    public RestrictedAlgorithmSuiteLoader(Bus bus) {
        bus.setExtension(this, AlgorithmSuiteLoader.class);
    }

    public AlgorithmSuite getAlgorithmSuite(Bus bus, SPConstants.SPVersion version, Policy nestedPolicy) {
        return new CustomAlgorithmSuite(version, nestedPolicy);
    }

    private static class CustomAlgorithmSuite extends AlgorithmSuite {

        CustomAlgorithmSuite(SPConstants.SPVersion version, Policy nestedPolicy) {

            super(version, nestedPolicy);

//                        printProviders();

            AlgorithmSuiteType algSuite = ALGORITHM_SUITE_TYPES.get("Basic256");
            AlgorithmSuiteType newAlgSuite = new AlgorithmSuiteType(
                    algSuite.getName(),
                    "http://www.w3.org/2001/04/xmlenc#sha256",
                    "http://www.w3.org/2001/04/xmlenc#aes256-cbc", //Encryption(), should be ok
                   // "http://www.w3.org/2001/04/xmlenc#kw-aes256",
                    SPConstants.KW_TRIPLE_DES,
                    "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p",
                    "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256",
                    "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256",
                    algSuite.getEncryptionDerivedKeyLength(),
                    algSuite.getSignatureDerivedKeyLength(),
                    algSuite.getMinimumSymmetricKeyLength(),
                    algSuite.getMaximumSymmetricKeyLength(), 512,
                    algSuite.getMaximumAsymmetricKeyLength());
            ALGORITHM_SUITE_TYPES.put("Basic256", newAlgSuite);
        }

        @Override
        protected AbstractSecurityAssertion cloneAssertion(Policy nestedPolicy) {
            return new CustomAlgorithmSuite(getVersion(), nestedPolicy);
        }

    }

    private static void printProviders() {
        Provider[] providerList = Security.getProviders();
        for (Provider provider : providerList) {
            System.out.println("Name: " + provider.getName());
            System.out.println("Information:\n" + provider.getInfo());

            Set<Provider.Service> serviceList = provider.getServices();
            for (Provider.Service service : serviceList) {
                System.out.println("Service Type: " + service.getType() + " Algorithm " + service.getAlgorithm());
            }
        }
    }
}
