package io.quarkiverse.cxf.it.server.provider;

import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceProvider;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.staxutils.StaxUtils;

@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(value = "http://cxf.apache.org/bindings/xformat")
public class StreamSourcePayloadProvider implements Provider<StreamSource> {

    public StreamSourcePayloadProvider() {
    }

    public StreamSource invoke(StreamSource request) {
        String payload = StaxUtils.toString(request);
        payload = payload.replace("<text>Hello</text>", "<text>Hello from StreamSourcePayloadProvider</text>");
        StreamSource response = new StreamSource(new StringReader(payload));
        return response;
    }
}
