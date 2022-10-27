package com.planetway.relyingpartyapp.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "signatureInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsentRevokeDocument {
    private String signRequestType = "consent_revoke";
    private String requestUUID;
    private String consentUUID;
    private String targetUserId;
}
