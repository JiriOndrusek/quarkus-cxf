package io.quarkiverse.cxf.it.logging;

import io.quarkiverse.cxf.annotation.CXFClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.eap.quickstarts.wscalculator.calculator.CalculatorService;

@Path("/cxf/logging")
public class CxfLoggingResource {

    @Inject
    @CXFClient("logging-client") // name used in application.properties
    CalculatorService calculator;

    @GET
    @Path("/calculator/multiply")
    @Produces(MediaType.TEXT_PLAIN)
    public Response multiplyDefault(@QueryParam("a") int a, @QueryParam("b") int b) {
        try {
            return Response.ok(calculator.multiply(a, b)).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}
