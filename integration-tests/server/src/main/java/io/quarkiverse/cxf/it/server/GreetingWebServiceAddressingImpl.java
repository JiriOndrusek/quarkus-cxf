package io.quarkiverse.cxf.it.server;

import jakarta.inject.Inject;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.soap.Addressing;

@WebService(serviceName = "GreetingWebServiceAddressingImpl")
@BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
@Addressing(required = true)
public class GreetingWebServiceAddressingImpl {

    @Inject
    HelloBean helloResource;

    public String reply(@WebParam(name = "text") String text) {
        return helloResource.getHello() + text;
    }

    public String ping(@WebParam(name = "text") String text) throws GreetingException {
        if (text.equals("error")) {
            throw new GreetingException("foo", "bar");
        }
        return helloResource.getHello() + text;
    }

}
