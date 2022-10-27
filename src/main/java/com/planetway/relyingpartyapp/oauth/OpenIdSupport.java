package com.planetway.relyingpartyapp.oauth;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.planetway.relyingpartyapp.service.KeyService;
import com.planetway.relyingpartyapp.service.PCoreService;

import jp.planetway.planetid.validator.ContainerValidator;

@Component
public class OpenIdSupport {

	public static final String STATE = "state";
	public static final String NONCE = "nonce";
	private static final String CLIENTID = "JP.0170678746892";
	private static final String URL = "https://api.poc.planet-id.me";
	private final PCoreService pCoreService;
	private final KeyService keyService;

	public OpenIdSupport(PCoreService pCoreService, KeyService keyService) {
		this.pCoreService = pCoreService;
		this.keyService = keyService;
	}

	public TokenResponse handleCallback(HttpServletRequest request, HttpServletResponse response,
			AuthResponse authResponse) {
		isValidState(request, authResponse.getState());
		TokenResponse tokenResponse = pCoreService.exchangeAccessCode(authResponse.getCode(),
				authResponse.getCallback());

		String planetId = parsePlanetId(request, tokenResponse.getIdToken());
		tokenResponse.setPlanetId(planetId);
		// tokenResponse.getPayload() is signed asice container. MIME type is
		// 'application/vnd.etsi.asic-e+zip'
		byte[] signedContainer = Base64.getDecoder().decode(tokenResponse.getPayload());
		ContainerValidator.validate(new ByteArrayInputStream(signedContainer));
		tokenResponse.setSignedContainer(signedContainer);
		request.setAttribute("state", null);
		request.setAttribute("nonce", null);
		return tokenResponse;
	}

	public boolean isValidState(HttpServletRequest request, String state) {
		String stateInCookie = (String) request.getSession().getAttribute("state");
		if (!stateInCookie.equals(state)) {
			throw new RuntimeException("State mismatch");
		}

		return true;
	}

	public String parsePlanetId(HttpServletRequest request, String token) {
		String nonce = (String) request.getSession().getAttribute("nonce");
		return parsePlanetId(token, nonce);
	}

	public String parsePlanetId(String token, String nonce) {
		assert token != null : "Token is null";
		assert nonce != null : "Nonce is null";

		if (keyService.getPCorePublicKey() == null) {
			// log.warn("Issuer public key missing, jwt left unverified.");

			return JWT.decode(token).getSubject();
		} else {
			DecodedJWT jwt = JWT.require(Algorithm.RSA256(keyService.getPCorePublicKey(), null)).withIssuer(URL)
					.withAudience(CLIENTID).withClaim(NONCE, sha256Hex(nonce)).build().verify(token);
			return jwt.getSubject();
		}
	}


}
