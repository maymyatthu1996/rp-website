package com.planetway.relyingpartyapp.planetx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.niis.xrd4j.client.SOAPClient;
import org.niis.xrd4j.client.SOAPClientImpl;
import org.niis.xrd4j.client.deserializer.ServiceResponseDeserializer;
import org.niis.xrd4j.client.serializer.ServiceRequestSerializer;
import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.springframework.stereotype.Service;

import com.planetway.relyingpartyapp.config.AppProperties;
import com.planetway.relyingpartyapp.model.PlanetXService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlanetXSoapTemplate {

    private final AppProperties appProperties;

    public PlanetXSoapTemplate(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * @param planetId the identifier of the end user whose consent for data access must be present
     * @return hashmap of attributes in the PlanetX response or
     * null in case of ANY Fault PlanetX response (both the Unauthorized case where there is no consent and various error situations like wrong provider/service/etc)
     */
    public Map<String, String> execute(String planetId, PlanetXService planetXService) {
        try {
            ProducerMember producer = producerMember(planetXService);
            String requestId = UUID.randomUUID().toString();
            ServiceRequest<String> request = new ServiceRequest<>(consumerMember(), producer, requestId);
            //request.setRequestData(planetId);
            ServiceRequestSerializer serializer = new PlanetXRequestSerializer();
            ServiceResponseDeserializer deserializer = new PlanetXMapResponseDeserializer("row");
            request.setRequestData(planetId);
            serializer.serialize(request);
            SOAPClient client = new SOAPClientImpl();
            SOAPMessage soapRequest = request.getSoapMessage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapRequest.writeTo(out);
            String strMsg = new String(out.toByteArray());
            System.out.println(strMsg);
            SOAPMessage soapResponse = client.send(soapRequest, appProperties.getPlanetXSecurityServerUrl());
            if (soapResponse.getSOAPBody().hasFault()) {
                log.info("SOAP response returned failure: " + soapResponse.getSOAPBody().getFault().getFaultCode() +
                        "; " + soapResponse.getSOAPBody().getFault().getFaultString() +
                        "; pid=" + planetId + ", producer=" + producer
                );
                if ("Internal Server Error".equalsIgnoreCase(soapResponse.getSOAPBody().getFault().getFaultString())) {
                    throw new RuntimeException("Data server returned catastrophic error: " + soapResponse.getSOAPBody().getFault().getFaultString());
                }
                return null;
            } else {
                ServiceResponse<String, Map<String, String>> serviceResponse = deserializer.deserialize(soapResponse);
                return serviceResponse.getResponseData();
            }

        } catch (XRd4JException | SOAPException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    private ConsumerMember consumerMember() {
        try {
            return new ConsumerMember(
                    appProperties.getPlanetXSubsystem().getInstance(),
                    appProperties.getPlanetXSubsystem().getMemberClass(),
                    appProperties.getPlanetXSubsystem().getMemberCode(),
                    appProperties.getPlanetXSubsystem().getSubsystemCode()
            );
        } catch (XRd4JException e) {
            throw new RuntimeException("Failed to create consumer member from "
                    + appProperties.getPlanetXSubsystem());
        }
    }

    private ProducerMember producerMember(PlanetXService planetXService) {
        try {
            ProducerMember producer = new ProducerMember(
                    planetXService.getInstance(),
                    planetXService.getMemberClass(),
                    planetXService.getMemberCode(),
                    planetXService.getSubsystemCode(),
                    planetXService.getServiceCode()
            );
            producer.setServiceVersion("v1"); // default

//            producer.setNamespacePrefix("ns5");
//            producer.setNamespaceUrl("http://producer.x-road.eu");
            
            producer.setNamespacePrefix("ss");
            producer.setNamespaceUrl("http://example.com/sampleservice");

            return producer;
        } catch (XRd4JException e) {
            throw new RuntimeException("Failed to create consumer member from "
                    + appProperties.getPlanetXSubsystem());
        }
    }
}
