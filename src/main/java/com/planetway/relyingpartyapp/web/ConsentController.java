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

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.planetway.relyingpartyapp.model.SignedDocumentEntity;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.repository.SignedDocumentRepository;
import com.planetway.relyingpartyapp.service.PCoreService;
import com.planetway.relyingpartyapp.util.SecureRandomUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConsentController {

	private final PCoreService pCoreService;
	private final SignedDocumentRepository signedDocumentRepository;

	private final String HTTPSTATUS_200 = "200";
	private final String HTTPSTATUS_404 = "404";

	@PostMapping("/consent-request")
	public String redirectToPlanetID(@AuthenticationPrincipal UserInfo userInfo, HttpServletRequest request,
			HttpServletResponse response) {
		String planetId = userInfo.getPlanetId();

		String subsysId = "JP-TEST/COM/0170678746892/clientService01";
		String serviceId = "JP-TEST/COM/0170000000000/data-bank-poc/suginamirealestate";

		String consentStatus = pCoreService.checkConsentStatus(planetId, subsysId, serviceId);
		// System.out.println(consentStatus);

		if (consentStatus.equals(HTTPSTATUS_200)) {
			return "redirect:/property/contract-review?dataBankName=" + "suginamirealestate";
		} else if (consentStatus.equals(HTTPSTATUS_404)) {
			// System.out.println(consentStatus);
			return "redirect:/consent-request-call?pId=" + planetId;
		} else {
			return "redirect:/error";
		}
	}

	@GetMapping("/consent-request-call")
	public void redirectToPlanetID(@RequestParam String pId, HttpServletRequest req, HttpServletResponse res) {
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
		HttpSession session = req.getSession();
		session.setAttribute("nonce", nonce);
		session.setAttribute("state", state);
		String charset = StandardCharsets.UTF_8.toString();

		// This xml is actual container for the signature, the rest of data is needed to
		// construct the query to PlanetID
		String payload = getXMLDocument(pId, clientId);

		String url = planetIdUrl + "?" + toQueryParam("scope", scope, charset) + "&"
				+ toQueryParam("client_id", clientId, charset) + "&" + toQueryParam("state", state, charset) + "&"
				+ toQueryParam("nonce", sha256Hex(nonce), charset) + "&"
				+ toQueryParam("redirect_uri", redirectUri, charset) + "&" + toQueryParam("action", action, charset)
				+ "&" + toQueryParam("level", level, charset) + "&" + toQueryParam("language", language, charset) + "&"
				+ toQueryParam("response_type", responseType, charset) + "&" + toQueryParam("planet_id", pId, charset)
				+ "&" + toQueryParam("payload", payload, charset);
		try {
			res.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//    @GetMapping("{uuid}/revoke")
//    public ResponseEntity<String> revokeConsent(@AuthenticationPrincipal UserInfo userInfo, @PathVariable String uuid, HttpServletResponse response) {
//        String redirectUri = appProperties.getBaseUrl() + "/callback/consent-revoke";
//
//        SignedDocumentEntity sde = signedDocumentRepository.findByUserIdAndUuid(userInfo.getId(), uuid);
//        String redirectUrl = "/signed-documents";
//        if (userInfo.getPlanetId().equals(sde.getPlanetId())) {
//            String consentRevokeDocument = consentContainerService.createConsentRevokeDocument(sde.getConsentUuid(), userInfo.getPlanetId());
//            AuthRequest authRequest = openIdSupport.createRequestForConsentRevoke(response, redirectUri, userInfo.getPlanetId(), consentRevokeDocument);
//            redirectUrl = authRequest.toLocation();
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Location", redirectUrl);
//        return new ResponseEntity<>(headers, HttpStatus.FOUND);
//    }

	@GetMapping("{uuid}/revoke")
	public ResponseEntity<String> revokeConsent(@AuthenticationPrincipal UserInfo userInfo, @PathVariable String uuid,
			HttpServletRequest req, HttpServletResponse res) {
		SignedDocumentEntity sde = signedDocumentRepository.findByUserIdAndUuid(userInfo.getId(), uuid);
		String url = null;
		if (userInfo.getPlanetId().equals(sde.getPlanetId())) {
			String consentRevokeDocument = createConsentRevokeDocument(sde.getConsentUuid(), userInfo.getPlanetId());
			String planetIdUrl = "https://api.poc.planet-id.me/v2/openid/auth";
			String scope = "openid";
			String clientId = "JP.0170678746892";
			String redirectUri = "http://localhost:8080/callback/consent-revoke";
			String action = "consent-revoke";
			String level = "1";
			String language = "en";
			String responseType = "code";
			String state = SecureRandomUtil.randomBase64(20);
			String nonce = SecureRandomUtil.randomBase64(20);

			HttpSession session = req.getSession();
			session.setAttribute("nonce", nonce);
			session.setAttribute("state", state);
			
			url = UriComponentsBuilder.fromHttpUrl(planetIdUrl)
	                .queryParam("planet_id", userInfo.getPlanetId())
	                .queryParam("scope", scope)
	                .queryParam("client_id", clientId)
	                .queryParam("state", state)
	                .queryParam("nonce", sha256Hex(nonce))
	                .queryParam("redirect_uri", redirectUri)
	                .queryParam("action", action)
	                .queryParam("level", level)
	                .queryParam("language", language)
	                .queryParam("payload", consentRevokeDocument)
	                .queryParam("response_type", responseType)
	                .toUriString();

		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", url);
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

	private String getXMLDocument(String planetId, String clientId) {
		String uuidToString = generateUUID();
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

	public String createConsentRevokeDocument(String consentUUID, String planetId) {
		String uuid = generateUUID();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<signatureInput xmlns=\"https://www.planetway.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
				+ "    <signRequestType>consent_revoke</signRequestType>\n" + "    <requestUUID>" + uuid
				+ "</requestUUID>\n" + "    <consentUUID>" + consentUUID + "</consentUUID>\n" + "    <targetUserId>"
				+ planetId + "</targetUserId>\n" + "</signatureInput>";

		return xml;
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

	public String generateUUID() {
		return UUID.randomUUID().toString();
	}

}
