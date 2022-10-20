package com.planetway.relyingpartyapp.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.planetway.relyingpartyapp.model.CheckOutFormDto;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.util.SecureRandomUtil;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Controller
public class DigitalSignController {

	@PostMapping("/digitalSign")
	public void redirectToPlanetID(@AuthenticationPrincipal UserInfo userInfo,@ModelAttribute("checkoutform") CheckOutFormDto checkoutformdto, HttpServletRequest request,
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

		String nonce = SecureRandomUtil.randomBase64(20);
		HttpSession session = request.getSession();
		session.setAttribute("nonce", nonce);
		session.setAttribute("state", state);

		String planetId = userInfo.getPlanetId();

		// This xml is actual container for the signature, the rest of data is needed to
		// construct the query to PlanetID
		String payload = getXMLDocument();

		String charset = StandardCharsets.UTF_8.toString();
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

	private String getXMLDocument() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<signatureInput xmlns=\"https://www.planetway.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
				+ "    <signRequestType>file_sign</signRequestType>\n"
				+ "    <hashToSign>2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae</hashToSign>\n"
				+ "    <fileURI>https://aaa.example.com/files/662e8fe3-f0c0-4ab2-a483-03265a69f4d6</fileURI>\n"
				+ "    <data>\n" 
				+ "        <fileName>digitalsign.txt</fileName>\n" 
				+ "    </data>\n"
				+ "</signatureInput>";

		return xml;
	}

	@GetMapping("/returnCheckoutPage")
	public String returnToCheckoutPage() {
		return "redirect:/checkoutpage";
	}
}
