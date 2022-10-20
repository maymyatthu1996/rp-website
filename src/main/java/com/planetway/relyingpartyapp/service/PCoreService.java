package com.planetway.relyingpartyapp.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.planetway.relyingpartyapp.oauth.TokenResponse;
import static org.springframework.http.HttpMethod.GET;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PCoreService {

	// private final TokenRepository tokenRepository;
	private final String CLIENTID = "JP.0170678746892";
	private final String CLIENTSECRET = "123qwe";
	private final String URL = "https://api.poc.planet-id.me";

	private final String CONSENTSTATUS_URL = "https://consent.poc.planet-id.me/v2/relying-parties/consent-status";

	private RestTemplate restTemplate = new RestTemplate();

	public TokenResponse exchangeAccessCode(String accessCode, String redirectUri) {
		String urlParameters = "code=" + accessCode + "" + "&grant_type=authorization_code&" + "redirect_uri="
				+ redirectUri;
		HttpHeaders headers = prepareHeaders(
				"Basic " + Base64Utils.encodeToString((CLIENTID + ":" + CLIENTSECRET).getBytes()));

		HttpEntity<String> entity = new HttpEntity<>(urlParameters, headers);
		return restTemplate.postForObject(URL + "/v2/openid/token", entity, TokenResponse.class);

	}

	public String verifyAccessCode(String accessCode, String redirectUri) {
		String urlParameters = "code=" + accessCode + "" + "&grant_type=authorization_code&" + "redirect_uri="
				+ redirectUri;

		return callPlanetId(urlParameters);
	}

	private String callPlanetId(String urlParameters) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = prepareHeaders(
				"Basic " + Base64Utils.encodeToString((CLIENTID + ":" + CLIENTSECRET).getBytes()));
		HttpEntity<String> entity = new HttpEntity<>(urlParameters, headers);
		return restTemplate.postForObject(URL + "/v2/openid/token", entity, String.class);
	}
	
	public String checkLinkStatus(String id) {
		return linkStsCheck(id);
	}
	
	private String linkStsCheck(String id) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = prepareHeaders(
				"Basic " + Base64Utils.encodeToString((CLIENTID + ":" + CLIENTSECRET).getBytes()));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> exchange = restTemplate.exchange("https://api.poc.planet-id.me/v2/relying-parties/identities/" +id, GET, entity, String.class);
		System.out.print(exchange.getBody() + exchange.getStatusCodeValue());
		return exchange.getBody();
	}

	public String checkConsentStatus(String urlParameters) {
		return callConsentStsAPI(urlParameters);
	}

	private String callConsentStsAPI(String urlParameters) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = prepareHeaders(
				"Basic " + Base64Utils.encodeToString((CLIENTID + ":" + CLIENTSECRET).getBytes()));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		System.out.println(CONSENTSTATUS_URL + urlParameters);
		ResponseEntity<String> exchange = restTemplate.exchange(CONSENTSTATUS_URL + urlParameters, GET, entity, String.class);
		System.out.println(exchange.getBody() + exchange.getStatusCodeValue());
		return null;
	}

	private HttpHeaders prepareHeaders(String authorizationValue) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		headers.add("charset", "utf-8");
		headers.add("Authorization", authorizationValue);
		return headers;
	}
}
