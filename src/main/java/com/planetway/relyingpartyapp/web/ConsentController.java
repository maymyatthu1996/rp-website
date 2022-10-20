package com.planetway.relyingpartyapp.web;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.service.PCoreService;
import com.planetway.relyingpartyapp.util.SecureRandomUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConsentController {
	
	private final PCoreService pCoreService;

	@PostMapping("/consent-request")
	public void redirectToPlanetID(@AuthenticationPrincipal UserInfo userInfo, HttpServletRequest request,
			HttpServletResponse response) {
		String planetId = userInfo.getPlanetId();
		String charset = StandardCharsets.UTF_8.toString();
	    String subsysId= "JP-TEST/COM/0170678746892/clientService01";
	    String serviceId="JP-TEST/COM/0170000000000/data-bank-poc/suginamirealestate";
	    
		String urlParam = "?" + toQueryParam("targetUserId", planetId, charset) 
				+ "&" + toQueryParam("consumer", subsysId, charset)
				+ "&" + toQueryParam("service", serviceId, charset);
		
		String consentStatus = pCoreService.checkConsentStatus(urlParam);
		System.out.println(consentStatus);
		
		
		String planetIdUrl = "https://api.poc.planet-id.me/v2/openid/auth";
		String scope = "openid";
		String clientId = "JP.0170678746892";
		String redirectUri = "http://localhost:8080/callback/consent";
		String action = "consent";
		String level = "1";
		String language = "en";
		String responseType = "code";
		String state = SecureRandomUtil.randomBase64(20);

		String nonce = SecureRandomUtil.randomBase64(20);
		HttpSession session = request.getSession();
		session.setAttribute("nonce", nonce);
		session.setAttribute("state", state);

		

		// This xml is actual container for the signature, the rest of data is needed to
		// construct the query to PlanetID
		String payload = getXMLDocument(planetId,clientId);

		
		String url = planetIdUrl + "?" + toQueryParam("scope", scope, charset) 
				+ "&" + toQueryParam("client_id", clientId, charset) 
				+ "&" + toQueryParam("state", state, charset) 
				+ "&" + toQueryParam("nonce", sha256Hex(nonce), charset) 
				+ "&" + toQueryParam("redirect_uri", redirectUri, charset) 
				+ "&" + toQueryParam("action", action, charset)
				+ "&" + toQueryParam("level", level, charset) 
				+ "&" + toQueryParam("language", language, charset) 
				+ "&" + toQueryParam("response_type", responseType, charset) 
				+ "&" + toQueryParam("planet_id", planetId, charset) 
				+ "&" + toQueryParam("payload", payload, charset);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String toQueryParam(String key, String value, String charset) {
		String result = null;
		try {
			result = key + "=" + URLEncoder.encode(value, charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String getXMLDocument(String planetId, String clientId) {
		UUID uuid = UUID.randomUUID();
		String uuidToString = uuid.toString();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<signatureInput xmlns=\"https://www.planetway.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
				+ "    <signRequestType>consent_give</signRequestType>\n" + "    <requestUUID>" + uuidToString
				+ "</requestUUID>\n" + "    <requestURI>https://api.planetway.com/signrequests/?uuid=" + uuidToString
				+ "</requestURI>\n" + "    <validTill>" + OffsetDateTime.now().plus(1, ChronoUnit.YEARS)
				+ "</validTill>\n" + "    <revokable>true</revokable>\n" + "    <data>\n" + "        <planetId>"
				+ planetId + "</planetId>\n" + "        <dataProvider>\n"
				+ "           <relyingPartyCode>JP.0170000000000</relyingPartyCode>\n"
				+ "           <planetXCode>JP-TEST/COM/0170000000000/data-bank-poc</planetXCode>\n"
				+ "        </dataProvider>\n"
				+ "        <dataService>JP-TEST/COM/0170000000000/data-bank-poc/suginamirealestate</dataService>\n"
				+ "        <dataConsumer>\n" + "          <relyingPartyCode>JP.0170678746892</relyingPartyCode>\n"
				+ "           <planetXCode>JP-TEST/COM/0170678746892/clientService01</planetXCode>\n"
				+ "        </dataConsumer>\n" + "    </data>\n" + "</signatureInput>";

		return xml;
	}

}
