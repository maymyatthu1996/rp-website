package com.planetway.relyingpartyapp.repository;

import com.planetway.relyingpartyapp.model.PlanetIdEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlanetIdRepository extends CrudRepository<PlanetIdEntity, String> {
    PlanetIdEntity findByRpUserId(Long rpUserId);

    @Modifying
    @Query("DELETE FROM PlanetIdEntity pid WHERE pid.rpUserId = :rpUserid")
    void deleteByRpUserId(@Param("rpUserid") Long rpUserid);
}
