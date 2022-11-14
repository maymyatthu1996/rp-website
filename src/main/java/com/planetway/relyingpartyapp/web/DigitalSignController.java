package com.planetway.relyingpartyapp.web;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.planetway.relyingpartyapp.model.CheckOutFormDto;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.service.DocumentContainerService;
import com.planetway.relyingpartyapp.util.SecureRandomUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class DigitalSignController {

	private final DocumentContainerService documentContainerService;

	@PostMapping("/digitalSign")
	public ResponseEntity<String> redirectToPlanetID(@AuthenticationPrincipal UserInfo userInfo,
			@ModelAttribute("checkoutform") CheckOutFormDto checkoutformdto, HttpServletRequest request,
			HttpServletResponse response) {
		String planetIdUrl = "https://api.poc.planet-id.me/v2/openid/auth";
		String scope = "openid";
		String clientId = "JP.0170678746892";
		String redirectUri = "http://localhost:8080/callback/signing";
		String action = "sign";
		String level = "1";
		String language = "en";
		String responseType = "code";
		String state = SecureRandomUtil.randomBase64(20);
		String planetId = userInfo.getPlanetId();
		String payload = documentContainerService.createFileSignContainer();
		String nonce = SecureRandomUtil.randomBase64(20);
		
		HttpSession session = request.getSession();
		session.setAttribute("nonce", nonce);
		session.setAttribute("state", state);

		System.out.println(payload);
		String url = UriComponentsBuilder.fromHttpUrl(planetIdUrl).queryParam("planet_id", planetId)
				.queryParam("scope", scope).queryParam("client_id", clientId).queryParam("state", state)
				.queryParam("nonce", sha256Hex(nonce)).queryParam("redirect_uri", redirectUri)
				.queryParam("action", action).queryParam("level", level).queryParam("language", language)
				.queryParam("payload", payload).queryParam("response_type", responseType).toUriString();
		System.out.println(url);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", url);
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

	@GetMapping("/returnCheckoutPage")
	public String returnToCheckoutPage() {
		return "redirect:/checkoutpage";
	}
}
