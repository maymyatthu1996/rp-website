package com.planetway.relyingpartyapp.planetx;

import org.niis.xrd4j.client.serializer.AbstractServiceRequestSerializer;
import org.niis.xrd4j.common.message.ServiceRequest;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

class PlanetXRequestSerializer extends AbstractServiceRequestSerializer {

    private static final String PX_NAMESPACE_URI = "http://xsd.planetcross.net/planetcross.xsd";

    protected void serializeRequest(ServiceRequest serviceRequest, SOAPElement soapRequest, SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration("px", PX_NAMESPACE_URI);
        SOAPHeaderElement she = envelope.getHeader().addHeaderElement(new QName(PX_NAMESPACE_URI, "targetUserId", "px"));
        String targetUserId = (String) serviceRequest.getRequestData();
        she.addTextNode(targetUserId);
    }
}
