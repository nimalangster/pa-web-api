package com.lamin.webapi.config;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

public abstract class ReactiveApiBinding {
	
	  protected WebClient webClient;
	  private static final String baseUrl = "http://localhost:9072/api/v1/private/payment-slots";
	  
	  public ReactiveApiBinding(String accessToken) {
	    Builder builder = WebClient.builder().baseUrl(baseUrl);
	    if (accessToken != null) {
	      builder.defaultHeader("Authorization", "Bearer " + accessToken);
	    } else {
	      builder.exchangeFunction(
	          request -> {
	            throw new IllegalStateException(
	                    "Can't access the API without an access token");
	          });
	    }
	    this.webClient = builder.build();
	  }
}