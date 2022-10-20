package com.planetway.relyingpartyapp.oauth;

import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AuthRequest {
    private String providerUri;
    private String clientId;
    private String state;
    private String nonce;
    private String redirectUri;
    private String action;
    private Integer level;
    private String language;
    private String scope;
    private String responseType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String planetId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String payload;

    public String toLocation() {
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(providerUri)
                .path("/v2/openid/auth")
                .queryParam("client_id", clientId)
                .queryParam("state", state)
                .queryParam("nonce", nonce)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("action", action)
                .queryParam("level", level)
                .queryParam("language", language)
                .queryParam("scope", scope)
                .queryParam("response_type", responseType);

        if (planetId != null) {
            ucb.queryParam("planet_id", planetId);
        }
        if (payload != null) {
            ucb.queryParam("payload", payload);
        }
        return ucb.toUriString();
    }
}
