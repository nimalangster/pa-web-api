//package com.lamin.webapi.service.client;
//
//import java.net.URI;
//import java.security.Principal;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.ui.Model;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//import com.lamin.webapi.dto.PaymentSlotDTO;
//import com.lamin.webapi.exception.ClientDataException;
//import com.lamin.webapi.exception.PaymentSlotDTOServiceException;
//
//import reactor.core.publisher.Mono;
//@Service
//public class IncomeWebClient {
//
//    private static final String GET_ALL_PAYMENTSLOTDTO_V1 = "http://localhost:9072/api/v1/private/payment-slots";
//	private static final String PAYMENTSLOTDTO_BY_ID_V1 = "http://localhost:9072/api/v1/private/payment-slots/{id}";
//	private static final String ADD_PAYMENTSLOTDTO_V1 = "http://localhost:9072/api/v1/private/payment-slots";
//	
//	private WebClient webClient;
//	private Logger log = LoggerFactory.getLogger(IncomeWebClient.class);
//	private String paymentSlotDTOApiUrl = "http://localhost:9072/api/v1/private/payment-slots/";
//    
//    public IncomeWebClient(WebClient webClient) {
//		this.webClient = webClient;
//	}
//    
//    public String getPaymentSlots(Model model) {
//        List<PaymentSlotDTO> paymentSlots = this.webClient.get()
//            .uri(paymentSlotDTOApiUrl)
//            .retrieve()
//            .bodyToMono(new ParameterizedTypeReference<List<PaymentSlotDTO>>() {
//            })
//            .block();
//        model.addAttribute("paymentslots", paymentSlots);
//        return "paymentslots";
//    }
//
////    public static Retry<?> fixedRetry = Retry.anyOf(WebClientResponseException .class)
////            .fixedBackoff(Duration.ofSeconds(2))
////            .retryMax(3)
////            .doOnRetry((exception) -> {
////                log.info("The exception is : " + exception);
////
////            });
////
////
////    public static Retry<?> fixedRetry5xx = Retry.anyOf(PaymentSlotDTOServiceException .class)
////            .fixedBackoff(Duration.ofSeconds(2))
////            .retryMax(3)
////            .doOnRetry((exception) -> {
////                log.info("The exception is : " + exception);
////
////            });
////
//    public Mono<RuntimeException> handle4xxErrorResponse(ClientResponse clientResponse) {
//        Mono<String> errorResponse = clientResponse.bodyToMono(String.class);
//        return errorResponse.flatMap((message) -> {
//            log.error("ErrorResponse Code is " + clientResponse.rawStatusCode() + " and the exception message is : " + message);
//            throw new ClientDataException(message);
//        });
//    }
//
//    public Mono<PaymentSlotDTOServiceException> handle5xxErrorResponse(ClientResponse clientResponse) {
//        Mono<String> errorResponse = clientResponse.bodyToMono(String.class);
//        return errorResponse.flatMap((message) -> {
//            log.error("ErrorResponse Code is " + clientResponse.rawStatusCode() + " and the exception message is : " + message);
//            throw new PaymentSlotDTOServiceException(message);
//        });
//    }
//    
//   
//
//    public List<PaymentSlotDTO> retrieveAllPaymentSlotDTO(String jwtToken) {
//        try {
//            return webClient.get().uri(GET_ALL_PAYMENTSLOTDTO_V1).headers(h -> h.setBearerAuth(jwtToken))
//                    .retrieve()
//                    .bodyToFlux(PaymentSlotDTO.class)
//                    .collectList()
//                    .block();
//        } catch (WebClientResponseException ex) {
//            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
//            log.error("WebClientResponseException in retrieveAllPaymentSlotDTOs", ex);
//            throw ex;
//        } catch (Exception ex) {
//            log.error("Exception in retrieveAllPaymentSlots ", ex);
//            throw ex;
//        }
//    }
//
//
//    public PaymentSlotDTO retrievePaymentSlotDTOById(Long id) {
//
//        try {
//            return webClient.get().uri(PAYMENTSLOTDTO_BY_ID_V1, id)
//                    .retrieve()
//                    .bodyToMono(PaymentSlotDTO.class)
//                    .block();
//        } catch (WebClientResponseException ex) {
//            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
//            log.error("WebClientResponseException in retrievePaymentSlotDTOById", ex);
//            throw ex;
//        } catch (Exception ex) {
//            log.error("Exception in retrievePaymentSlotDTOById ", ex);
//            throw ex;
//        }
//    }
//
//    public PaymentSlotDTO retrievePaymentSlotDTOById_Custom_Error_Handling(int PaymentSlotDTOId) {
//
//        return webClient.get().uri(PAYMENTSLOTDTO_BY_ID_V1, PaymentSlotDTOId)
//                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
//                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
//                .bodyToMono(PaymentSlotDTO.class)
//                .block();
//    }
//
////    public PaymentSlotDTO retrievePaymentSlotDTOById_WithRetry(int PaymentSlotDTOId) {
////
////        try {
////            return webClient.get().uri(PaymentSlotDTO_BY_ID_V1, PaymentSlotDTOId)
////                    .retrieve()
////                    .bodyToMono(PaymentSlotDTO.class)
////                    .retryWhen(fixedRetry)
////                    .block();
////        } catch (WebClientResponseException ex) {
////            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
////            log.error("WebClientResponseException in retrievePaymentSlotDTOById", ex);
////            throw ex;
////        } catch (Exception ex) {
////            log.error("Exception in retrievePaymentSlotDTOById ", ex);
////            throw new PaymentSlotDTOServiceException(ex.getMessage());
////        }
////    }
////
////    public List<PaymentSlotDTO> retrievePaymentSlotDTOByName(String PaymentSlotDTOName) {
////
////        String uri = UriComponentsBuilder.fromUriString(GET_PaymentSlotDTO_BY_NAME_V1)
////                .queryParam("PaymentSlotDTO_name", PaymentSlotDTOName)
////                .build().toUriString();
////        try {
////            return webClient.get().uri(uri)
////                    .retrieve()
////                    .bodyToFlux(PaymentSlotDTO.class)
////                    .collectList()
////                    .block();
////        } catch (WebClientResponseException ex) {
////            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
////            log.error("WebClientResponseException in retrievePaymentSlotDTOByName", ex);
////            throw ex;
////        } catch (Exception ex) {
////            log.error("Exception in retrievePaymentSlotDTOByName ", ex);
////            throw ex;
////        }
////    }
//
//    public PaymentSlotDTO addNewPaymentSlotDTO(PaymentSlotDTO PaymentSlotDTO) {
//        try {
//            return webClient.post().uri(ADD_PAYMENTSLOTDTO_V1)
//                    .syncBody(PaymentSlotDTO)
//                    .retrieve()
//                    .bodyToMono(PaymentSlotDTO.class)
//                    .block();
//        } catch (WebClientResponseException ex) {
//            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
//            log.error("WebClientResponseException in addNewPaymentSlotDTO", ex);
//            throw ex;
//        } catch (Exception ex) {
//            log.error("Exception in addNewPaymentSlotDTO ", ex);
//            throw ex;
//        }
//    }
//
//    public PaymentSlotDTO addNewPaymentSlotDTO_custom_Error_Handling(PaymentSlotDTO PaymentSlotDTO) {
//            return webClient.post().uri(ADD_PAYMENTSLOTDTO_V1)
//                    .syncBody(PaymentSlotDTO)
//                    .retrieve()
//                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
//                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
//                    .bodyToMono(PaymentSlotDTO.class)
//                    .block();
//    }
//
//    public PaymentSlotDTO updatePaymentSlotDTO(int id, PaymentSlotDTO PaymentSlotDTO) {
//
//        try {
//            return webClient.put().uri(PAYMENTSLOTDTO_BY_ID_V1, id)
//                    .syncBody(PaymentSlotDTO)
//                    .retrieve()
//                    .bodyToMono(PaymentSlotDTO.class)
//                    .block();
//        } catch (WebClientResponseException ex) {
//            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
//            log.error("WebClientResponseException in updatePaymentSlotDTO", ex);
//            throw ex;
//        } catch (Exception ex) {
//            log.error("Exception in updatePaymentSlotDTO ", ex);
//            throw ex;
//        }
//    }
//
//    public String deletePaymentSlotDTOById(int id) {
//        try {
//            return webClient.delete().uri(PAYMENTSLOTDTO_BY_ID_V1, id)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//        } catch (WebClientResponseException ex) {
//            log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
//            log.error("WebClientResponseException in updatePaymentSlotDTO", ex);
//            throw ex;
//        } catch (Exception ex) {
//            log.error("Exception in updatePaymentSlotDTO ", ex);
//            throw ex;
//        }
//
//    }
//
////    public String errorEndpoint(){
////
////        return webClient.get().uri(ERROR_PAYMENTSLOTDTO_V1)
////                .retrieve()
////                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
////                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
////                .bodyToMono(String.class)
////                .block();
////    }
//
////    public String errorEndpoint_fixedRetry(){
////
////        return webClient.get().uri(ERROR_PaymentSlotDTO_V1)
////                .retrieve()
////                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
////                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
////                .bodyToMono(String.class)
////                .retryWhen(fixedRetry5xx)
////                .block();
////    }
//}