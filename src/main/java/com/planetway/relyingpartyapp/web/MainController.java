package com.planetway.relyingpartyapp.web;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

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

import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.service.UserServiceImpl;
import  com.planetway.relyingpartyapp.util.SecureRandomUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final UserServiceImpl userService;

	@GetMapping("/login")
	public String login(HttpServletRequest request) {
		return "login";
	}

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/contract-success-page")
	public String callingContractSuccessPage() {
		return "contract-success-page";
	}
	
	@GetMapping("/setting")
	public String setting() {
		return "setting";
	}

	@GetMapping("/linking/planetid")
	public void redirectToPlanetIDForLink(HttpServletRequest request, HttpServletResponse response) {
		// These values most probably are red from a configuration file
		String planetIdUrl = "https://api.poc.planet-id.me/v2/openid/auth";
		String scope = "openid";
		String clientId = "JP.0170678746892";
		String redirectUri = "http://localhost:8080/callback/linking";
		String action = "authenticate";
		String level = "1";
		String language = "en";
		// the value must be set according to user preferences
		String responseType = "code";

		// These values must be calculated, the implementation of a random string
		// generator is left for the developer
		String state = SecureRandomUtil.randomBase64(20);
		//String state = SecureRandomUtil.randomString(9);

		String nonce = SecureRandomUtil.randomBase64(20);

		// This example saves nonce and state to the session, but the values can be
		// stored to cookie as well

		HttpSession session = request.getSession();
		session.setAttribute("nonce", nonce);
		session.setAttribute("state", state);

		String charset = StandardCharsets.UTF_8.toString();
		String url = planetIdUrl + "?" + toQueryParam("scope", scope, charset) 
				+ "&" + toQueryParam("client_id", clientId, charset) 
				+ "&" + toQueryParam("state", state, charset) 
				+ "&" + toQueryParam("nonce", sha256Hex(nonce), charset) 
				+ "&" + toQueryParam("redirect_uri", redirectUri, charset) 
				+ "&" + toQueryParam("action", action, charset)
				+ "&" + toQueryParam("level", level, charset) 
				+ "&" + toQueryParam("language", language, charset) 
				+ "&" + toQueryParam("response_type", responseType, charset);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@GetMapping("/login/planetid")
	public void redirectToPlanetIDForLogin(HttpServletRequest request, HttpServletResponse response) {
		// These values most probably are red from a configuration file
		String planetIdUrl = "https://api.poc.planet-id.me/v2/openid/auth";
		String scope = "openid";
		String clientId = "JP.0170678746892";
		String redirectUri = "http://localhost:8080/callback/login";
		String action = "authenticate";
		String level = "1";
		String language = "en";
		// the value must be set according to user preferences
		String responseType = "code";

		// These values must be calculated, the implementation of a random string
		// generator is left for the developer
		String state = SecureRandomUtil.randomBase64(20);
		//String state = SecureRandomUtil.randomString(9);

		String nonce = SecureRandomUtil.randomBase64(20);

		// This example saves nonce and state to the session, but the values can be
		// stored to cookie as well

		HttpSession session = request.getSession();
		session.setAttribute("nonce", nonce);
		session.setAttribute("state", state);

		String charset = StandardCharsets.UTF_8.toString();
		String url = planetIdUrl + "?" + toQueryParam("scope", scope, charset) 
				+ "&" + toQueryParam("client_id", clientId, charset) 
				+ "&" + toQueryParam("state", state, charset) 
				+ "&" + toQueryParam("nonce", sha256Hex(nonce), charset) 
				+ "&" + toQueryParam("redirect_uri", redirectUri, charset) 
				+ "&" + toQueryParam("action", action, charset)
				+ "&" + toQueryParam("level", level, charset) 
				+ "&" + toQueryParam("language", language, charset) 
				+ "&" + toQueryParam("response_type", responseType, charset);
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
			result =  key + "=" + URLEncoder.encode(value, charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
    @GetMapping("/unlink")
    public String unlinkPlanetId(@AuthenticationPrincipal UserInfo userInfo) {
        userService.unlinkPlanetId(userInfo);
        return "redirect:/setting";
    }
    
    @GetMapping("/bank-select")
	public String bankSelect() {
		return "bank-select";
	}

}
