package io.quarkiverse.cxf.it.server.provider;

import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceProvider;
import java.io.StringReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.staxutils.StaxUtils;

@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(value = "http://cxf.apache.org/bindings/xformat")
public class SourcePayloadProvider implements Provider<DOMSource> {

    public SourcePayloadProvider() {
    }

    public DOMSource invoke(DOMSource request) throws WebServiceException {
        try {
            String payload = StaxUtils.toString(request.getNode());
            payload = payload.replace("<text>Hello</text>", "<text>Hello from SourcePayloadProvider</text>");
            DOMSource response = new DOMSource(StaxUtils.read(new StreamSource(new StringReader(payload))));
            return response;
        } catch (XMLStreamException e) {
            throw new WebServiceException(e);
        }
    }
}
