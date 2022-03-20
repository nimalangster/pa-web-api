package com.lamin.webapi.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.lamin.webapi.dto.EarningByWeek;
import com.lamin.webapi.dto.PagingResponse;
import com.lamin.webapi.dto.PaymentSlotDTO;
import com.lamin.webapi.dto.TimePeriod;
import com.lamin.webapi.service.client.IncomeClientService;
import com.lamin.webapi.service.client.PaymentslotApis;
import com.lamin.webapi.service.client.PaymentslotWebClientApis;


import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ReportController {
	
	private static final String HEADER = "Authorization";

	private static final CharSequence PREFIX = "Bearer ";

	private IncomeClientService incomeService;

	private PaymentslotApis paymentslotApis;

	private PaymentslotWebClientApis paymentslotWebClientApis;

	public ReportController(IncomeClientService incomeService, PaymentslotApis paymentslotApis,
			PaymentslotWebClientApis paymentslotWebClientApis) {
		super();
		this.incomeService = incomeService;
		this.paymentslotApis = paymentslotApis;
		this.paymentslotWebClientApis = paymentslotWebClientApis;
	}
	
	@GetMapping(path = "/income/report/report-form")
	public String showPaymentSlotReportForm(TimePeriod timePeriod, Principal principal, Model model) {

		model.addAttribute("title", "Show report Form");
		model.addAttribute("username", principal.getName());		

		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_gold-user"))) {
			model.addAttribute("userType", "gold-user");
			return "income/report/reportform";
		}
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_silver-user"))) {
			model.addAttribute("userType", "silver-user");
			return "income/report/reportformt";
		}
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_bronze-user"))) {
			model.addAttribute("userType", "bronze-user");
			return "income/report/reportform";
		}
		return "income/report/reportform";
	}
	
	
	@PostMapping(path = "/income/report/generate-report")
	public String generatePaymentSlotReport(TimePeriod timePeriod, BindingResult result, Principal principal, Model model) {
		
		log.info("In generatePaymentSlotReport Receiving Post request to get reports  Date from : " + timePeriod.getStartDate() + " Date To : " + timePeriod.getEndDate());

		
		if (result.hasErrors() && result.getErrorCount() > 0) {
			log.error("Validation errors");
			return "income/paymentslothome";
		}
		List<EarningByWeek> reports = null;
		try {
			reports = paymentslotWebClientApis.generateReport(timePeriod, getUserId(principal));

		} catch (Exception e) {
			log.info("In generatePaymentSlotReport method client exception captured and the messeage is: " + e.getMessage());
			String serverError = new String("Validation error: " + e.getMessage());
			ObjectError error = new ObjectError("serverError", serverError);
			result.addError(error);
		}

		if (result.hasErrors() && result.getErrorCount() > 0) {
			System.out.println("Validation errors");
			return "income/report/reportform";
		}
		log.info("In generatePaymentSlotReport successfully  get reports  with size : " + reports.size());
		
		model.addAttribute("reports", reports);		
		
		return "income/report/reportform";
	}
	
	private String getUserId(Principal principal) {

		KeycloakAuthenticationToken kp = (KeycloakAuthenticationToken) principal;

		SimpleKeycloakAccount simpleKeycloakAccount = (SimpleKeycloakAccount) kp.getDetails();

		AccessToken token = simpleKeycloakAccount.getKeycloakSecurityContext().getToken();

		return token.getSubject();
	}


}
