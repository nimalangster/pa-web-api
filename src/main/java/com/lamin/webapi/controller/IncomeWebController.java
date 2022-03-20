package com.lamin.webapi.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lamin.webapi.dto.PagingResponse;
import com.lamin.webapi.dto.PaymentSlotDTO;
import com.lamin.webapi.dto.TimePeriod;
import com.lamin.webapi.exception.ClientDataException;
import com.lamin.webapi.service.client.IncomeClientService;

import com.lamin.webapi.service.client.PaymentslotApis;
import com.lamin.webapi.service.client.PaymentslotWebClientApis;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class IncomeWebController {

	private static final String HEADER = "Authorization";

	private static final CharSequence PREFIX = "Bearer ";

	private IncomeClientService incomeService;

	private PaymentslotApis paymentslotApis;

	private PaymentslotWebClientApis paymentslotWebClientApis;

	public IncomeWebController(IncomeClientService incomeService, PaymentslotApis paymentslotApis,
			PaymentslotWebClientApis paymentslotWebClientApis) {
		super();
		this.incomeService = incomeService;
		this.paymentslotApis = paymentslotApis;
		this.paymentslotWebClientApis = paymentslotWebClientApis;
	}

	// @RolesAllowed({"gold-user","silver-user","bronze-user","realm-role-admin","app-user"})
	@GetMapping(path = "/income/paymentslot-home")
	public String paymentSlotHome(Principal principal, Model model,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> startDate, @RequestParam("endDate")  @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> endDate,
			HttpServletRequest request) {
		
		log.info("In paymentSlotHome controller method with from request parameters start date: "+ startDate + " and end date:  "+ endDate);
		
		model.addAttribute("title", "PaymentSlotHome");
		model.addAttribute("username", principal.getName());

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);		
		
		LocalDate today = LocalDate.now();
		LocalDate fromDate = startDate.orElse(today.withDayOfMonth(1));		
		LocalDate toDate = endDate.orElse(today.withDayOfMonth(today.lengthOfMonth()));

		model.addAttribute("timePeriod", new TimePeriod(fromDate, toDate));		

		PagingResponse<PaymentSlotDTO> allWithPaging;
		try {
			allWithPaging = paymentslotWebClientApis.getAllWithPaging(currentPage, pageSize,
					fromDate, toDate);
		} catch (ClientDataException e) {
			allWithPaging = null;
		}	
		if(allWithPaging != null)
			log.info("In paymentSlotHome controller method with got results from income service with Page no :" + allWithPaging.getPageNumber() + " Page total :" + allWithPaging.getPageTotal());
		model.addAttribute("paymentslots", allWithPaging);
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_gold-user"))) {
			System.out.println("contains gold role");
			model.addAttribute("membership", "GOld");
			return "income/paymentslothome";
		}
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_silver-user"))) {
			System.out.println("contains silver role");
			model.addAttribute("membership", "SILVER");
			return "income/paymentslothome";
		}
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_bronze-user"))) {
			System.out.println("contains bronze role");
			model.addAttribute("membership", "BRONZE");
			return "income/paymentslothome";
		}
		return "income/paymentslothome";

	}

	@PostMapping("/income/view-PaymentSlot")
	public String viewPaymentSlot(TimePeriod timePeriod, BindingResult result, Model model,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size,
			Principal principal) {

		log.info("Receiving Post request to get all payment slots with page: " + page + " size : " + size
				+ " Date from : " + timePeriod.getStartDate() + " Date To : " + timePeriod.getEndDate());

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);

		if (result.hasErrors() && result.getErrorCount() > 0) {
			log.error("Validation errors");
			return "income/paymentslothome";
		}
		PagingResponse<PaymentSlotDTO> allWithPaging = null;
		try {
			 allWithPaging = paymentslotWebClientApis.getAllWithPaging(currentPage,
					pageSize, timePeriod.getStartDate(), timePeriod.getEndDate());

		} catch (Exception e) {
			log.info("In the controller viewPaymentSlot method client exception captured and the messeage is: " + e.getMessage());
			String serverError = new String("Validation error: " + e.getMessage());
			ObjectError error = new ObjectError("serverError", serverError);
			result.addError(error);
		}

		if (result.hasErrors() && result.getErrorCount() > 0) {
			System.out.println("Validation errors");
			return "income/paymentslothome";
		}

		model.addAttribute("paymentslots", allWithPaging);

		return "income/paymentslothome";

	}

	@GetMapping(path = "/income/addpaymentslot")
	public String showPaymentSlotAddForm(PaymentSlotDTO paymentSlotDTO, Principal principal, Model model) {

		model.addAttribute("title", "Add PaymentSlot");
		model.addAttribute("username", principal.getName());
		model.addAttribute("paymentslotdto", new PaymentSlotDTO());

		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_gold-user"))) {
			return "income/addpaymentslot";
		}
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_silver-user"))) {
			System.out.println("contains silver role");
			return "income/addpaymentslot";
		}
		if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_bronze-user"))) {
			System.out.println("contains bronze role");
			return "income/addpaymentslot";
		}
		return null;
	}

	@PostMapping("/income/addPaymentSlot")
	public String addPaymentSlot(@Valid PaymentSlotDTO paymentSlotDTO, BindingResult result, Model model,
			Principal principal) {

		String userId = null;

		int noOfHours = ((int) ChronoUnit.HOURS.between(paymentSlotDTO.getStartTime(), paymentSlotDTO.getEndTime()));
		if (!(noOfHours == 2) && !(noOfHours == 4) && !(noOfHours == 6)) {

			String durationError = new String("Shift duration should be 2 or 4 or 6!");
			ObjectError error = new ObjectError("durationError", durationError);
			result.addError(error);
		}

		userId = getUserId(principal);

		if (userId == null) {

			String autherror = new String("Could not extract user id from the principal!");
			ObjectError error = new ObjectError("globalError", autherror);
			result.addError(error);
		}

		if (result.hasErrors() && result.getErrorCount() > 0) {
			System.out.println("Validation errors");
			return "income/addPaymentSlot";
		}

		paymentSlotDTO.setUserId(userId);
		try {
			paymentslotWebClientApis.addNewPaymentSlotDTO(paymentSlotDTO);
		} catch (Exception e) {
			log.info("In the controller client exeption captured and the messeage is: " + e.getMessage());
			String serverError = new String("Validation error: " + e.getMessage());
			ObjectError error = new ObjectError("serverError", serverError);
			result.addError(error);
		}

		if (result.hasErrors() && result.getErrorCount() > 0) {
			System.out.println("Validation errors");
			return "income/addPaymentSlot";
		}
		return "redirect:/income/paymentslot-home";

	}

	@GetMapping("/income/update-paymentSlot/{id}")
	public String showUpdateForm(@Valid @PathVariable("id") long id, @Valid PaymentSlotDTO paymentSlotDTO1,
			BindingResult result, Model model, HttpServletRequest request, Principal principal) {

		log.info("Getting payment slot details for id: " + id + " to update.");
		Optional<PaymentSlotDTO> paymentSlotDTO = paymentslotWebClientApis.retrievePaymentSlotDTOById(id);

		if (!paymentSlotDTO.isPresent()) {
			log.info("payment slot with id is not found!");
			String missingIdError = new String("Could not find the record to update!");
			ObjectError error = new ObjectError("globalError", missingIdError);
			result.addError(error);
			return "income/updatepaymentslot";
		}
		model.addAttribute("username", principal.getName());
		model.addAttribute("paymentSlotDTO", paymentSlotDTO.get());

		return "income/updatepaymentslot";
	}

	@PutMapping("/income/update-paymentSlot/{id}")
	public String updatePaymentSlot(@PathVariable("id") long id, @Valid PaymentSlotDTO paymentSlot,
			BindingResult result, Model model, HttpServletRequest request) {

		log.info("Updating payment slot details for id: " + id + ".");
		if (result.hasErrors()) {
			paymentSlot.setId(id);
			return "income/updatepaymentslot";
		}
		paymentSlot.setId(id);
		paymentslotWebClientApis.updatePaymentSlotDTO(id, paymentSlot);

		return "redirect:/income/paymentslot-home";
	}

	@GetMapping("/income/delete-paymentSlot/{id}")
	public String showDeletePaymentSlotForm(@PathVariable("id") long id, @Valid PaymentSlotDTO paymentSlotDTO1,
			BindingResult result, Model model, HttpServletRequest request, Principal principal) {

		log.info("Getting payment slot details for id: " + id + " to delete.");
		Optional<PaymentSlotDTO> paymentSlotDTO = paymentslotWebClientApis.retrievePaymentSlotDTOById(id);

		if (!paymentSlotDTO.isPresent()) {

			log.info("payment slot with id: " + id + " is not found!");
			String missingIdError = new String("Could not find the record to delete!");
			ObjectError error = new ObjectError("globalError", missingIdError);
			result.addError(error);
			return "income/deletepaymentslot";
		}
		model.addAttribute("username", principal.getName());
		model.addAttribute("paymentSlotDTO", paymentSlotDTO.get());

		return "/income/deletepaymentslot";
	}

	@DeleteMapping("/income/delete-paymentSlot/{id}")
	public String deletePaymentSlot(@PathVariable("id") long id, @Valid PaymentSlotDTO paymentSlotDTO1,
			BindingResult result, Model model, HttpServletRequest request) {

		log.info("Deleting payment slot details for id: " + id + ".");
		Optional<PaymentSlotDTO> paymentSlotDTO = paymentslotWebClientApis.retrievePaymentSlotDTOById(id);

		if (!paymentSlotDTO.isPresent()) {

			log.info("payment slot with id: " + id + " is not found!");
			String missingIdError = new String("Could not find the record to delete!");
			ObjectError error = new ObjectError("globalError", missingIdError);
			result.addError(error);
			return "income/deletepaymentslot";
		}

		paymentslotWebClientApis.deletePaymentSlotDTOById(id);

		return "redirect:/income/paymentslot-home";
	}

	private String getUserId(Principal principal) {

		KeycloakAuthenticationToken kp = (KeycloakAuthenticationToken) principal;

		SimpleKeycloakAccount simpleKeycloakAccount = (SimpleKeycloakAccount) kp.getDetails();

		AccessToken token = simpleKeycloakAccount.getKeycloakSecurityContext().getToken();

		return token.getSubject();
	}

}