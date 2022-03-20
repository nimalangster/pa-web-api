package com.lamin.webapi.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomUserAttrController {

	@GetMapping(path = "/users-info")
	public String getUserInfo(Model model, HttpServletRequest httpServletRequest) {

		KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();
		
		KeycloakSecurityContext k = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
		
		System.out.println(Optional.of(k.getTokenString()));
		   

		final Principal principal = (Principal) authentication.getPrincipal();

		String dob = "";
		Integer userId = null;
		String username = null ;
        String emailID = null ;
        String lastname = null ;
        String firstname = null ;
        String realmName = null ;
        String id = null;

		if (principal instanceof KeycloakPrincipal) {

			KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
			IDToken token = kPrincipal.getKeycloakSecurityContext().getIdToken();

			Map<String, Object> customClaims = token.getOtherClaims();
			
			Set<Entry<String, Object>> entrySet = customClaims.entrySet();
			
			for (Entry<String, Object> entry : entrySet) {
				
				System.out.println("Printing other claims; "+entry);
			}
			username = token.getPreferredUsername();
	        emailID = token.getEmail();
	        lastname = token.getFamilyName();
	        firstname = token.getGivenName();
	        realmName = token.getIssuer();
	        id = token.getSubject();      
			
	        System.out.println("THE ID IS :"+id);
	        System.out.println("Token.getPreferredUsername() : "+ token.getPreferredUsername());
	        System.out.println("token.getFamilyName(): "+token.getFamilyName());
	        System.out.println("token.getGivenName():"+token.getGivenName());
	        System.out.println("token.getSubject():"+token.getSubject());
	        
			//userId = Integer.parseInt(token.getOtherClaims().get("user_id").toString());
			
			if (customClaims.containsKey("DOB")) {
				dob = String.valueOf(customClaims.get("DOB"));				
			}
			model.addAttribute("realmname", token.getIssuer());
			model.addAttribute("username", token.getPreferredUsername());
			model.addAttribute("email", emailID);
			model.addAttribute("firstname", firstname);
			model.addAttribute("familyname", lastname);
			model.addAttribute("userid", id);
		}

		
		
		return "home/userInfo";

	}
}
