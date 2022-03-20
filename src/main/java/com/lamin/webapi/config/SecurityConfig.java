package com.lamin.webapi.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
	
	
	
    // Submits the KeycloakAuthenticationProvider to the AuthenticationManager
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    // Specifies the session authentication strategy
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
            .antMatchers("/user-portal*")
            .hasAnyRole("gold-user","silver-user","web-api-admin", "realm-role-admin")
            .antMatchers("/income/paymentslot-home*")
            .hasAnyRole("gold-user","silver-user","web-api-admin", "realm-role-admin")
            .antMatchers("/users-info*")
            .hasAnyRole("gold-user","silver-user","web-api-admin", "realm-role-admin")
            .anyRequest()
            .authenticated();
    }
    
    
//    @Bean
//    WebClient webClient(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
//        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
//        oauth2.setDefaultOAuth2AuthorizedClient(true);
//        return WebClient.builder()
//            .apply(oauth2.oauth2Configuration())
//            .build();
//    }
    
//    
//    @Bean 
//	public OAuth2RestTemplate oauth2RestTemplate( OAuth2ClientContext oauth2ClientContext, OAuth2ProtectedResourceDetails details) {
//	    
//		return new OAuth2RestTemplate(details, oauth2ClientContext);    
//	}
//    
//    @Bean
//    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository
//    															,OAuth2AuthorizedClientService authorizedClientService) {
//    
//        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
//                                                                  .clientCredentials()
//                                                                  .build();
//
//        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = 
//        													 new AuthorizedClientServiceOAuth2AuthorizedClientManager(
//        													 clientRegistrationRepository, authorizedClientService);
//        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//    
//        
//        return authorizedClientManager;
//    }
//    
//    
//    @Bean
//    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
//        
//        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = 
//        													new ServletOAuth2AuthorizedClientExchangeFilterFunction(
//        													authorizedClientManager);
//        			
//        oauth2Client.setDefaultClientRegistrationId("studentservice");
//        return WebClient.builder()
//                .apply(oauth2Client.oauth2Configuration())
//                .filter(this.logRequest())
//                .build();
//    }
}