package com.planetway.relyingpartyapp.exception;

import org.springframework.security.core.AuthenticationException;

public class PlanetIdNotLinkedException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

	public PlanetIdNotLinkedException(String explanation) {
        super(explanation);
    }
}

