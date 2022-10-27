package com.planetway.relyingpartyapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.planetway.relyingpartyapp.model.SignedDocumentEntity;

import java.util.List;


@Repository
public interface SignedDocumentRepository extends CrudRepository<SignedDocumentEntity, Long> {
    List<SignedDocumentEntity> getAllByUserId(Long userId);
    SignedDocumentEntity findByUserIdAndUuid(Long userId, String uuid);
    SignedDocumentEntity findByConsentUuid(String consentUuid);
}
