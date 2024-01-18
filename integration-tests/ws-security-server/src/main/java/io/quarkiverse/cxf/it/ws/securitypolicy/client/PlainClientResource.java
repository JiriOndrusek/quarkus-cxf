package io.quarkiverse.cxf.it.ws.securitypolicy.client;

import java.io.IOException;
import java.util.Map;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.ws.BindingProvider;

import org.apache.cxf.ws.security.SecurityConstants;

import io.quarkiverse.cxf.annotation.CXFClient;
import io.quarkiverse.cxf.it.ws.securitypolicy.server.PasswordCallbackHandler;

@Path("/cxf/securityServer/plainClient")
public class PlainClientResource {

    //    @GET
    //    @Path("/endpointUrl")
    //    @Produces(MediaType.TEXT_PLAIN)
    //    public String getEndpointUrl()
    //            throws IOException {
    //        return (String) ((BindingProvider) createPlainClient()).getRequestContext()
    //                .get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
    //    }

    @CXFClient("securityPolicyHelloService")
    WssSecurityPolicyHelloService client;

    @POST
    @Path("/sayHello")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(boolean securityConfig)
            throws IOException {

        //todo move to applicationProperties
        if (securityConfig) {
            Map<String, Object> ctx = ((BindingProvider) client).getRequestContext();
            ctx.put(SecurityConstants.CALLBACK_HANDLER, new PasswordCallbackHandler());
            ctx.put(SecurityConstants.SIGNATURE_PROPERTIES,
                    Thread.currentThread().getContextClassLoader().getResource("alice.properties"));
            ctx.put(SecurityConstants.SIGNATURE_USERNAME, "alice");
            ctx.put(SecurityConstants.ENCRYPT_USERNAME, "bob");
            ctx.put(SecurityConstants.ENCRYPT_PROPERTIES,
                    Thread.currentThread().getContextClassLoader().getResource("alice.properties"));
        }

        return client.sayHello("foo");
    }

    //    WssSecurityPolicyHelloService createPlainClient() throws MalformedURLException {
    //        final URL serviceUrl = new URL(getServerUrl() + "/soap/security-policy-hello?wsdl");
    //        final QName qName = new QName("https://quarkiverse.github.io/quarkiverse-docs/quarkus-cxf/ws-securitypolicy",
    //                WssSecurityPolicyHelloService.class.getSimpleName());
    //        final Service service = Service.create(serviceUrl, qName);
    //        return service.getPort(WssSecurityPolicyHelloService.class);
    //    }
    //
    //    static String getServerUrl() {
    //        final Config config = ConfigProvider.getConfig();
    //        final int port = LaunchMode.current().equals(LaunchMode.TEST) ? config.getValue("quarkus.http.test-port", Integer.class)
    //                : config.getValue("quarkus.http.port", Integer.class);
    //        return String.format("http://localhost:%d", port);
    //    }
}
