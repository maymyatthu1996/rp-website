package com.planetway.relyingpartyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.planetway.relyingpartyapp.model.PurchaseInfoEntity;

@Repository
public interface PurchaseInfoRepository extends JpaRepository<PurchaseInfoEntity, Long>{
	
	PurchaseInfoEntity findByEmail(String email);

}
