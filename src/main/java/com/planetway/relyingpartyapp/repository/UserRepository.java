package com.planetway.relyingpartyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.planetway.relyingpartyapp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{	
	User findByEmail(String email);
	
    @Query(
            value = "SELECT u.* FROM planet_id pid INNER JOIN user u  ON u.id = pid.rp_user_id WHERE pid.id = ?",
            nativeQuery = true
    )
    User findByPlanetId(String planetId);
}
