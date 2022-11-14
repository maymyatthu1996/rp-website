package com.planetway.relyingpartyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.planetway.relyingpartyapp.model.RpUserEntity;

@Repository
public interface UserRepository extends JpaRepository<RpUserEntity, Long>{	
	RpUserEntity findByEmail(String email);
	
    @Query(
            value = "SELECT u.* FROM planet_id pid INNER JOIN rp_user u  ON u.id = pid.rp_user_id WHERE pid.id = ?",
            nativeQuery = true
    )
    RpUserEntity findByPlanetId(String planetId);
}
