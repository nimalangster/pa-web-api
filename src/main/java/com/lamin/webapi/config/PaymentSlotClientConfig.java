package com.lamin.webapi.config;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

import com.lamin.webapi.service.client.PaymentslotApis;
import com.lamin.webapi.service.client.PaymentslotWebClientApis;

@Configuration
public class PaymentSlotClientConfig {

  @Bean
  @RequestScope
  public PaymentslotApis paymentslotApis(HttpServletRequest httpServletRequest) {
	  
	  String accessToken = null;
	  
	  KeycloakSecurityContext k = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName()); 
     
	  accessToken = k.getTokenString();
	  return new PaymentslotApis(accessToken);
  }
  
  
  @Bean
  @RequestScope
  public PaymentslotWebClientApis paymentslotWebClientApis(HttpServletRequest httpServletRequest) {

	  	String accessToken = null;
	  
	  	KeycloakSecurityContext k = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName()); 
     
	  	accessToken = k.getTokenString();
	  	return new PaymentslotWebClientApis(accessToken);
  }

}