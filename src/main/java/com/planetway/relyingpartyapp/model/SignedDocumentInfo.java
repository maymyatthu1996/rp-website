package com.planetway.relyingpartyapp.model;

import lombok.Data;

import java.util.Map;

@Data
public class SignedDocumentInfo {
    private String signedDocumentUUID;
    private String signatureType;
    private long createdAtTimestamp;
    private String fileName;
    private Map<String, String> dataProvider;
    private Map<String, String> dataConsumer;
    boolean asiceContainerTimestampedExists;
}
