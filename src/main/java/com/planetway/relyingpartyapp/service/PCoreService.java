package com.planetway.relyingpartyapp.service;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import com.planetway.relyingpartyapp.oauth.TokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PCoreService {
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

	public String checkConsentStatus(String planetId,String subsysId,String serviceId) {
		return callConsentStsAPI(planetId,subsysId,serviceId);
	}

	private String callConsentStsAPI(String planetId,String subsysId,String serviceId) {
        String url = UriComponentsBuilder.fromHttpUrl(CONSENTSTATUS_URL)
                .queryParam("targetUserId", planetId)
                .queryParam("consumer", subsysId)
                .queryParam("service", serviceId)
                .toUriString();
        System.out.println(url);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENTID, CLIENTSECRET);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = null;
        String statusCode = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            System.out.println("Success: " + response.getBody());
            System.out.println(response.getStatusCodeValue());
            statusCode = Integer.toString(response.getStatusCodeValue());
        } catch (HttpClientErrorException clientException) {
            System.out.println("Status code 4xx: " + clientException);
            statusCode = Integer.toString(clientException.getRawStatusCode());
        } catch (HttpServerErrorException serverException) {
            System.out.println("Status code 5xx: " + serverException);
        } catch (UnknownHttpStatusCodeException e) {
            System.out.println("Something else: " + e);
        }
		return statusCode;
	}

	private HttpHeaders prepareHeaders(String authorizationValue) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		headers.add("charset", "utf-8");
		headers.add("Authorization", authorizationValue);
		return headers;
	}
}
