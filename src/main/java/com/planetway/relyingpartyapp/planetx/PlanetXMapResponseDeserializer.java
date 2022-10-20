package com.planetway.relyingpartyapp.planetx;

import org.niis.xrd4j.client.deserializer.AbstractResponseDeserializer;
import org.niis.xrd4j.common.util.SOAPHelper;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Map;

public class PlanetXMapResponseDeserializer extends AbstractResponseDeserializer<String, Map<String, String>> {
    private String responseNodeName;

    public PlanetXMapResponseDeserializer(String responseNodeName) {
        this.responseNodeName = responseNodeName;
    }

    protected String deserializeRequestData(Node requestNode) {
        return null;
    }

    protected Map<String, String> deserializeResponseData(Node responseNode, SOAPMessage message) throws SOAPException {
        for (int i = 0; i < responseNode.getChildNodes().getLength(); i++) {
            if (responseNode.getChildNodes().item(i).getLocalName().equals(responseNodeName)) {
                return SOAPHelper.nodesToMap(responseNode.getChildNodes().item(i).getChildNodes());
            }
        }
        return null;
    }
}
