package io.quarkiverse.cxf.it.ws.securitypolicy.client;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(targetNamespace = "https://quarkiverse.github.io/quarkiverse-docs/quarkus-cxf/ws-securitypolicy", serviceName = "WssSecurityPolicyHelloService")
public interface WssSecurityPolicyHelloService {

    @WebMethod
    String sayHello(String name);
}
