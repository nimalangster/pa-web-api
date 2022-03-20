package com.lamin.webapi.controller;

import java.security.Principal;
import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.lamin.webapi.dto.PaymentSlotDTO;
import com.lamin.webapi.service.client.IncomeClientService;

@Controller
public class WebController {
    
	
	private IncomeClientService incomeService;	

    public WebController(IncomeClientService incomeService) {
		super();
		this.incomeService = incomeService;
	}

	@GetMapping(path = "/")
    public String index() {
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "redirect:/";
    }

    @RolesAllowed({"gold-user","silver-user","bronze-user"})
    @PreAuthorize("hasRole('ROLE_VIEWER') or hasRole('ROLE_EDITOR')")
    @GetMapping(path = "home/user-portal")
    public String userportal(Principal principal, Model model) {
    			
    	System.out.println("In the User Portal");
        model.addAttribute("title", "User Poral");
        model.addAttribute("username", principal.getName());
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)
        		  SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
			System.out.println(grantedAuthority);
		}
        if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_gold-user"))) {
        	System.out.println("contains gold role");
        	return "home/user-portal-gold";}
        if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_silver-user"))){
        	System.out.println("contains silver role");
        	return "home/user-portal-silver";}
        if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_bronze-user"))) {
        	System.out.println("contains bronze role");
        	return "home/user-portal-bronze";}
        if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_app-admin"))) {
        	System.out.println("contains admin role");
        	return "home/user-portal-admin";}
        if (authorities != null && authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_app-user"))) {
        	System.out.println("contains user role");
        	return "home/user-portal-user";}
       
		return "home/user-portal";
    }
    
    
    
    
}