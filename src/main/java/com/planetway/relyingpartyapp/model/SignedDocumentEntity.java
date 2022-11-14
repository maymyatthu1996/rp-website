package com.planetway.relyingpartyapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "signed_document")
public class SignedDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String signatureType;
    private Long userId;
//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    @Column(name = "data", columnDefinition = "BLOB", nullable = false)
//    private byte[] data;
    private String planetId;
    private boolean hasTimestamp;
    private String consentUuid;
    private String revokeDocumentUuid;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
