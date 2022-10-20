package com.planetway.relyingpartyapp.service;

import com.planetway.relyingpartyapp.config.PCoreProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Slf4j
@Service
public class KeyService {

    private final PCoreProperties pCoreProperties;
    private final RestTemplate restTemplate;

    @Getter
    private RSAPublicKey pCorePublicKey;

    public KeyService(PCoreProperties pCoreProperties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.pCoreProperties = pCoreProperties;
    }

    @PostConstruct
    public void loadPCorePublicKey() {
        String json;
        try {
            json = restTemplate.getForObject(pCoreProperties.getUrl() + "/.well-known/jwks", String.class);
        } catch (Exception e) {
            log.error("Could not query JWK", e);
            return;
        }

        JsonWebKeySet jsonWebKeySet;
        try {
            jsonWebKeySet = new JsonWebKeySet(json);
        } catch (JoseException e) {
            log.error("Could not parse JWK", e);
            return;
        }

        List<JsonWebKey> keys = jsonWebKeySet.getJsonWebKeys();
        if (keys.isEmpty()) {
            log.error("No keys returned by PCORE");
            return;
        }

        JsonWebKey key = keys.get(0);
        if (key instanceof RsaJsonWebKey) {
            pCorePublicKey = ((RsaJsonWebKey) key).getRsaPublicKey();
        } else {
            log.error("JWK must by instance of RSA");
        }
    }
}
