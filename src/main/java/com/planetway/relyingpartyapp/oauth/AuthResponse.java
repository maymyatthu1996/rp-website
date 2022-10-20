package com.planetway.relyingpartyapp.oauth;

import lombok.Data;

@Data
public class AuthResponse {

    private String code;

    private String state;

    private String callback;
}
