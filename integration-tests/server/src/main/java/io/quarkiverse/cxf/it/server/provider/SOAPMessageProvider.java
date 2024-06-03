package io.quarkiverse.cxf.it.server.provider;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceProvider;
import java.io.StringReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.staxutils.StaxUtils;

@WebServiceProvider
@ServiceMode(value = Service.Mode.MESSAGE)
public class SOAPMessageProvider implements Provider<SOAPMessage> {

    public SOAPMessageProvider() {
    }

    public SOAPMessage invoke(SOAPMessage request) throws WebServiceException {
        try {
            String payload = StaxUtils.toString(request.getSOAPBody().extractContentAsDocument());
            payload = payload.replace("<text>Hello</text>", "<text>Hello from SOAPMessageProvider</text>");

            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage response = mf.createMessage();
            response.getSOAPBody().addDocument(StaxUtils.read(new StreamSource(new StringReader(payload))));
            response.saveChanges();
            return response;

        } catch (SOAPException | XMLStreamException e) {
            throw new WebServiceException(e);
        }
    }
}
