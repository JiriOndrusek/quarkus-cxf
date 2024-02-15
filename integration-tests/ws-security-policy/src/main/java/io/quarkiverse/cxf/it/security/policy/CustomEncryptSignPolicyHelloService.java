package io.quarkiverse.cxf.it.security.policy;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

import org.apache.cxf.annotations.Policy;

/**
 * A service with a WS-SecurityPolicy attached
 */
// tag::quarkus-cxf-rt-ws-security.adoc[]
@WebService(serviceName = "CustomEncryptSignPolicyHelloService")
@Policy(placement = Policy.Placement.BINDING, uri = "custom-encrypt-sign-policy.xml")
public interface CustomEncryptSignPolicyHelloService extends AbstractHelloService {
    // end::quarkus-cxf-rt-ws-security.adoc[]
    @WebMethod
    @Override
    String hello(String text);
}
