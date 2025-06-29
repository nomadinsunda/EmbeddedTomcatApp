package com.intheeast.controller;

import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.intheeast.entity.UserMessage;
import com.intheeast.service.ContactService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/contact")
public class ContactController {
	private static final Logger log = LoggerFactory.getLogger(ContactController.class);
	
	@Inject private ContactService contactService;

	/**
	 * @param binder binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAllowedFields(new String[] { "name", "email", "text", "referer" });
		
		// Converts empty string to null, which is nice since most validation rules fire only if the field isn't null.
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
	/**
	 * Handle GET requests for /contact by redirecting to the contact form.
	 * 
	 * @return logical view name
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getContactRedirect() {
	    return "redirect:/contact/new";
	}

	
	/**
	 * @param req request
	 * @param model model
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getContactForm(HttpServletRequest req, Model model) {
		UserMessage userMsg = new UserMessage();
		userMsg.setReferer(req.getHeader("Referer"));
		model.addAttribute(userMsg);
		return getFullViewName("contactForm");
	}
	
	/**
	 * @param req request
	 * @param userMessage user message form bean
	 * @param result result
	 * @return logical view name
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String postContactForm(
			HttpServletRequest req,
			@ModelAttribute @Valid UserMessage userMessage,
			BindingResult result) {
		
		if (result.hasErrors()) {
			result.reject("error.global");
			return getFullViewName("contactForm");
		}
		
		log.debug("userMessage={}", userMessage);
		
		userMessage.setIpAddress(req.getRemoteAddr());
		userMessage.setAcceptLanguage(req.getHeader("Accept-Language"));
		userMessage.setUserAgent(req.getHeader("User-Agent"));
		userMessage.setDateCreated(new Date());
		
		contactService.saveUserMessage(userMessage);
		
		// Use the redirect-after-post pattern to reduce double-submits.
		return "redirect:/contact/thanks";
	}
	
	/**
	 * Returns a page thanking the user for their submission.
	 */
	@RequestMapping(value = "/thanks", method = RequestMethod.GET)
	public String getThanksPage() {
		return getFullViewName("thanks");
	}
	
	private String getFullViewName(String viewName) {
		return "contact/" + viewName;
	}
}