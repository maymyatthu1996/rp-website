package com.planetway.relyingpartyapp.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TokenResponse {

	private String payload;
    private String idToken;
    private String accessToken;

    // parsed variables
    @JsonIgnore
    private String planetId;
    @JsonIgnore
    private byte[] signedContainer;
}
