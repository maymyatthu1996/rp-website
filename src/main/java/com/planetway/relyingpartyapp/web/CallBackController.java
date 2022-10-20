package com.planetway.relyingpartyapp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.planetway.relyingpartyapp.model.CheckOutFormDto;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.oauth.AuthResponse;
import com.planetway.relyingpartyapp.oauth.OpenIdSupport;
import com.planetway.relyingpartyapp.oauth.TokenResponse;
import com.planetway.relyingpartyapp.service.PurchaseService;
import com.planetway.relyingpartyapp.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("callback")
public class CallBackController {

	private final UserServiceImpl userService;
	private final OpenIdSupport openIdSupport;
	private final PurchaseService purchaseService;

	@GetMapping("/linking")
	public String linkingCallback(@AuthenticationPrincipal UserInfo userInfo, @RequestParam String code,
			@RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
		TokenResponse tokenResponse = handleCallback(request, response, code, state, "/callback/linking");

		try {
			userService.linkPlanetId(userInfo, tokenResponse.getPlanetId());
		} catch (Exception e) {
			return "redirect:/callback/error-linked/" + tokenResponse.getPlanetId();
		}

		return "redirect:/setting";
	}

	@GetMapping("/login")
	public String loginCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam String code,
			@RequestParam String state) {
		TokenResponse tokenResponse = handleCallback(request, response, code, state, "/callback/login");
		userService.loginUserWithPlanetId(tokenResponse.getPlanetId());
		return "redirect:/";
	}

	@GetMapping("/signing")
	public String callbackSigning(@AuthenticationPrincipal UserInfo userInfo, @RequestParam String code,
			@RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
		try {
			handleCallback(request, response, code, state, "/callback/signing");

			CheckOutFormDto checkOutFormDto = (CheckOutFormDto) request.getSession().getAttribute("checkoutform");
			String email = userInfo.getUsername();
			purchaseService.save(checkOutFormDto, email);
		} catch (Exception e) {
			return "redirect:/error";
		}
		request.setAttribute("checkoutform", null);
		return "redirect:/contract-success-page";
	}

	@GetMapping("/consent")
	public String callbackConsent(@AuthenticationPrincipal UserInfo userInfo, @RequestParam String code,
			@RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
		try {
			handleCallback(request, response, code, state, "/callback/consent");
		} catch (Exception e) {
			return "redirect:/error";
		}
           return "redirect:/property/contract-review?dataBankName=" + "suginamirealestate";
	}

	private TokenResponse handleCallback(HttpServletRequest request, HttpServletResponse response, String code,
			String state, String redirectPath) {
		String redirectUri = "http://localhost:8080" + redirectPath;

		AuthResponse authorization = new AuthResponse();
		authorization.setCode(code);
		authorization.setState(state);
		authorization.setCallback(redirectUri);

		return openIdSupport.handleCallback(request, response, authorization);
	}

	@GetMapping("error-linked/{planetId}")
	public ModelAndView getErrorPage(@PathVariable(name = "planetId") String planetId) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error-linked");
		modelAndView.addObject("planetId", planetId);
		return modelAndView;
	}

}
