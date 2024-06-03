package io.quarkiverse.cxf.it.server.provider;

import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceProvider;
import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.staxutils.StaxUtils;

@WebServiceProvider
@ServiceMode(value = Service.Mode.MESSAGE)
public class SourceMessageProvider implements Provider<Source> {

    public SourceMessageProvider() {
    }

    public Source invoke(Source request) {
        String payload = StaxUtils.toString(request);
        payload = payload.replace("<text>Hello</text>", "<text>Hello from SourceMessageProvider</text>");
        Source response = new StreamSource(new StringReader(payload));
        return response;
    }
}
