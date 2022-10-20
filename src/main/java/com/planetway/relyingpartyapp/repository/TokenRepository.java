package com.planetway.relyingpartyapp.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class TokenRepository {

    private static final Map<String, String> database = new HashMap<>();

    public void add(String planetId, String accessToken) {
        database.put(planetId, accessToken);
    }

    public String accessToken(String planetId) {
        return database.get(planetId);
    }
}
