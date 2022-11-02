package com.planetway.relyingpartyapp.xml.sign;

import lombok.Builder;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "signatureInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileToSignDocument {
    private String signRequestType;
    private String hashToSign;
    private String fileURI;
    private RequestData data;

    @Data
    @Builder
    public static class RequestData {
        private String fileName;
    }

}
