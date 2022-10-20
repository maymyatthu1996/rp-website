package com.planetway.relyingpartyapp.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.planetway.relyingpartyapp.model.CheckOutFormDto;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.service.DataBankService;
import org.apache.poi.ss.usermodel.DateUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("property")
public class PropertyController {

	private static final String PROPERTY_INDEX_HTML = "property";
	private final DataBankService dataBankService;
	private static final String SOAP_FLAG = "1";
	
	@GetMapping
	public String get() {
		return PROPERTY_INDEX_HTML;
	}

	@GetMapping("contract-review")
	public Object getContractReviewPage(@AuthenticationPrincipal UserInfo userInfo, @RequestParam String dataBankName,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> responseData = dataBankService.retrieveData(userInfo.getPlanetId(), dataBankName);
		if (responseData == null) {
			// no consent, will not handle further
			return "redirect:/error";
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("checkoutpage");
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
		String soapDate=(String)responseData.get("dob_jp");
		double d = Double.parseDouble(soapDate);
		Date javaDate = DateUtil.getJavaDate(d);
		String dateResult = fmt.format(javaDate);
		
		CheckOutFormDto checkoutform = new CheckOutFormDto();
		checkoutform.setName(responseData.get("name_jp"));
		checkoutform.setAddress(responseData.get("address_jp"));
		checkoutform.setDob(dateResult);
		checkoutform.setPhone(responseData.get("phone"));
		checkoutform.setJob(responseData.get("employer_jp"));
		checkoutform.setSoapFlg(1);
		
		modelAndView.addObject("checkoutform", checkoutform);
		HttpSession session = request.getSession();
		session.setAttribute("checkoutform", checkoutform);
		session.setAttribute("soapFlg", SOAP_FLAG);
		return modelAndView;
	}

}
