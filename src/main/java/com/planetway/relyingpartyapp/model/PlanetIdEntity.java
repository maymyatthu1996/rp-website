package com.planetway.relyingpartyapp.model;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "planet_id")
public class PlanetIdEntity {
    @Id
    private String id;
    private Long rpUserId;
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
