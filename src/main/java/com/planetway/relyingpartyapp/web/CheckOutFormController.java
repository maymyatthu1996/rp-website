package com.planetway.relyingpartyapp.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.planetway.relyingpartyapp.model.CheckOutFormDto;

@Controller
@RequestMapping("/checkoutpage")
public class CheckOutFormController {

	@ModelAttribute("checkoutform")
	public CheckOutFormDto checkoutformdto() {
		return new CheckOutFormDto();
	}

	@GetMapping
	public String showCheckOutForm(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("soapFlg", 0);
		return "checkoutpage";
	}

	@PostMapping
	public ModelAndView registerUserAccount(@ModelAttribute("checkoutform") CheckOutFormDto checkoutformdto,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("confirmPage");

		if (request.getSession().getAttribute("soapFlg").equals("1")) {
			checkoutformdto.setSoapFlg(1);
		} else {
			checkoutformdto.setSoapFlg(0);
		}
		modelAndView.addObject("checkoutform", checkoutformdto);
		HttpSession session = request.getSession();
		session.setAttribute("soapFlg", null);
		session.setAttribute("checkoutform", checkoutformdto);
		return modelAndView;
	}

}
