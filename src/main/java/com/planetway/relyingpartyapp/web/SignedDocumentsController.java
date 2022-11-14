package com.planetway.relyingpartyapp.web;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
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
        List<SignedDocumentEntity> docs = signedDocumentRepository.findAll((Sort.by(Order.desc("id"))));
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
    @GetMapping("{uuid}/download")
    public ResponseEntity<byte[]> download(@AuthenticationPrincipal UserInfo userInfo, @PathVariable String uuid) {
    	
    	byte[] document = pCoreService.downloadSignedDocument(uuid);
    	//System.out.println(document);
        //SignedDocumentEntity doc = signedDocumentRepository.findByUserIdAndUuid(userInfo.getId(), uuid);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + uuid + ".asice");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        //byte[] asice = doc.getData();
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(document.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(document);
    }
}
