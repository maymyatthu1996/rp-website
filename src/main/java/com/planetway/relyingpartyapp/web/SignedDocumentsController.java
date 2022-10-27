package com.planetway.relyingpartyapp.web;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.planetway.relyingpartyapp.config.AppProperties;
import com.planetway.relyingpartyapp.model.DataBank;
import com.planetway.relyingpartyapp.model.SignedDocumentEntity;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.repository.SignedDocumentRepository;
import com.planetway.relyingpartyapp.service.PCoreService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/signed-documents")
public class SignedDocumentsController {
	
	private final PCoreService pCoreService;
    private final SignedDocumentRepository signedDocumentRepository;
    private final AppProperties appProperties;

	private final String HTTPSTATUS_404 = "404";
    
    @GetMapping("")
    public ModelAndView getSignedDocuments(@AuthenticationPrincipal UserInfo userInfo) {
        ModelAndView modelAndView = new ModelAndView("signed-document-list");       
        List<SignedDocumentEntity> docs = signedDocumentRepository.getAllByUserId(userInfo.getId());
        modelAndView.addObject("docs", docs);
        modelAndView.addObject("planetId",userInfo.getPlanetId());
        return modelAndView;
    }
    
    @GetMapping("{uuid}/revoke-check")
    public ModelAndView getRevokePage(@AuthenticationPrincipal UserInfo userInfo, @PathVariable String uuid, HttpServletResponse response) {
    	ModelAndView modelAndView = new ModelAndView("revoke-check");
    	SignedDocumentEntity sde = signedDocumentRepository.findByUserIdAndUuid(userInfo.getId(), uuid);
    	DataBank dataBank = appProperties.getDataBanks().get("suginamirealestate");
    	String fullProviderPxService = dataBank.getPlanetXService().toString();
    	String consentSts = pCoreService.checkConsentStatus(userInfo.getPlanetId(), appProperties.getPlanetXSubsystem().toString(), fullProviderPxService);
        System.out.print(consentSts);
        
		if (consentSts.equals(HTTPSTATUS_404)) {
			// System.out.println(consentStatus);
			sde.setRevokeDocumentUuid("revoked");
			SignedDocumentEntity doc = signedDocumentRepository.findByConsentUuid(sde.getConsentUuid());
            doc.setRevokeDocumentUuid("revoked");
            signedDocumentRepository.save(doc);
		}
    	modelAndView.addObject("sde", sde);
    	modelAndView.addObject("pxService", fullProviderPxService);    	
    	return modelAndView;
    }
}
