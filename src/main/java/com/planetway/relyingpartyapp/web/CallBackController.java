package com.planetway.relyingpartyapp.web;

import java.util.List;

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
import com.planetway.relyingpartyapp.model.SignedDocumentEntity;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.oauth.AuthResponse;
import com.planetway.relyingpartyapp.oauth.OpenIdSupport;
import com.planetway.relyingpartyapp.oauth.TokenResponse;
import com.planetway.relyingpartyapp.repository.SignedDocumentRepository;
import com.planetway.relyingpartyapp.service.PurchaseService;
import com.planetway.relyingpartyapp.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("callback")
public class CallBackController {

	private final UserServiceImpl userService;
	private final OpenIdSupport openIdSupport;
	private final PurchaseService purchaseService;
	private final SignedDocumentRepository signedDocumentRepository;

	@GetMapping("/linking")
	public String linkingCallback(@AuthenticationPrincipal UserInfo userInfo, @RequestParam String code,
			@RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
		TokenResponse tokenResponse = handleCallback(userInfo, request, response, code, state, "/callback/linking");

		try {
			userService.linkPlanetId(userInfo, tokenResponse.getPlanetId());
		} catch (Exception e) {
			return "redirect:/callback/error-linked/" + tokenResponse.getPlanetId();
		}

		return "redirect:/setting";
	}

	@GetMapping("/login")
	public String loginCallback(@AuthenticationPrincipal UserInfo userInfo, HttpServletRequest request,
			HttpServletResponse response, @RequestParam String code, @RequestParam String state) {
		TokenResponse tokenResponse = handleCallback(userInfo, request, response, code, state, "/callback/login");
		userService.loginUserWithPlanetId(tokenResponse.getPlanetId());
		return "redirect:/";
	}

	@GetMapping("/signing")
	public String callbackSigning(@AuthenticationPrincipal UserInfo userInfo, @RequestParam String code,
			@RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
		try {
			handleCallback(userInfo, request, response, code, state, "/callback/signing");

			CheckOutFormDto checkOutFormDto = (CheckOutFormDto) request.getSession().getAttribute("checkoutform");
			String email = userInfo.getUsername();
			purchaseService.save(checkOutFormDto, email);
		} catch (Exception e) {
			log.error("Could not exchange code: {}", e.getMessage());
			return "redirect:/error";
		}
		request.setAttribute("checkoutform", null);
		return "redirect:/contract-success-page";
	}

	@GetMapping("/consent")
	public String callbackConsent(@AuthenticationPrincipal UserInfo userInfo, @RequestParam String code,
			@RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
		try {
			handleCallback(userInfo, request, response, code, state, "/callback/consent");
		} catch (Exception e) {
			log.error("Could not exchange code: {}", e.getMessage());
			return "redirect:/error";
		}
		return "redirect:/property/contract-review?dataBankName=" + "suginamirealestate";
	}

	@GetMapping("/consent-revoke")
	public String consentRevokeCallback(@AuthenticationPrincipal UserInfo userInfo, HttpServletRequest request,
			HttpServletResponse response, @RequestParam String code, @RequestParam String state) {
		// this callback is shared between getting info from data banks and LRA
		try {
			handleCallback(userInfo, request, response, code, state, "/callback/consent-revoke");
		} catch (Exception e) {
			log.error("Could not exchange code: {}", e.getMessage());
			return "redirect:/error";
		}
		return "redirect:/signed-documents";
	}

	private TokenResponse handleCallback(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response,
			String code, String state, String redirectPath) {
		String redirectUri = "http://localhost:8080" + redirectPath;

		AuthResponse authorization = new AuthResponse();
		authorization.setCode(code);
		authorization.setState(state);
		authorization.setCallback(redirectUri);

		TokenResponse tokenResponse = openIdSupport.handleCallback(request, response, authorization);

		if ("/callback/signing".equals(redirectPath)) {
			// in case of signing or consent, save the document to database
			SignedDocumentEntity doc = new SignedDocumentEntity();
			doc.setSignatureType("SIGNING");
			doc.setUserId(userInfo.getId());
			doc.setPlanetId(userInfo.getPlanetId());
			//doc.setData(tokenResponse.getSignedContainer());
			doc.setUuid(tokenResponse.getPayloadUuid());
			doc.setHasTimestamp(tokenResponse.isSignedContainerTimestamped());
			doc.setConsentUuid(tokenResponse.getConsentUuid());
			signedDocumentRepository.save(doc);
		} else if ("/callback/consent".equals(redirectPath)) {
			List<SignedDocumentEntity> docs = signedDocumentRepository.getAllByUserId(userInfo.getId());

			for (SignedDocumentEntity newDoc : docs) {
				if (newDoc.getRevokeDocumentUuid() == null && newDoc.getSignatureType().equals("CONSENT")) {
					newDoc.setRevokeDocumentUuid("revoke");
				}
			}
			signedDocumentRepository.saveAll(docs);
			SignedDocumentEntity doc = new SignedDocumentEntity();
			doc.setSignatureType("CONSENT");
			doc.setUserId(userInfo.getId());
			doc.setPlanetId(userInfo.getPlanetId());
			//doc.setData(tokenResponse.getSignedContainer());
			doc.setUuid(tokenResponse.getPayloadUuid());
			doc.setHasTimestamp(tokenResponse.isSignedContainerTimestamped());
			doc.setConsentUuid(tokenResponse.getConsentUuid());
			signedDocumentRepository.save(doc);
		} else if ("/callback/consent-revoke".equals(redirectPath)) {
			SignedDocumentEntity doc = signedDocumentRepository.findByConsentUuid(tokenResponse.getConsentUuid());
			doc.setRevokeDocumentUuid(tokenResponse.getPayloadUuid());
			signedDocumentRepository.save(doc);
		}

		return tokenResponse;
	}

	@GetMapping("error-linked/{planetId}")
	public ModelAndView getErrorPage(@PathVariable(name = "planetId") String planetId) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error-linked");
		modelAndView.addObject("planetId", planetId);
		return modelAndView;
	}

}
