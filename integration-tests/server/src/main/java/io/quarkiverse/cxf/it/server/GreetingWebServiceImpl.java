package io.quarkiverse.cxf.it.server;

import jakarta.inject.Inject;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;

@WebService(endpointInterface = "io.quarkiverse.cxf.it.server.GreetingWebService", serviceName = "GreetingWebService")
@BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class GreetingWebServiceImpl implements GreetingWebService {

    @Inject
    HelloBean helloResource;

    @Override
    public String reply(@WebParam(name = "text") String text) {
        return helloResource.getHello() + text;
    }

    @Override
    public String ping(@WebParam(name = "text") String text) throws GreetingException {
        if (text.equals("error")) {
            throw new GreetingException("foo", "bar");
        }
        return helloResource.getHello() + text;
    }

}
