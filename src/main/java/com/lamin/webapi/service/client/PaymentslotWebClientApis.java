package com.lamin.webapi.service.client;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lamin.webapi.config.ReactiveApiBinding;
import com.lamin.webapi.dto.EarningByWeek;
import com.lamin.webapi.dto.PagingResponse;
import com.lamin.webapi.dto.PaymentSlotDTO;
import com.lamin.webapi.dto.TimePeriod;
import com.lamin.webapi.exception.ClientDataException;
import com.lamin.webapi.exception.EmployeeServiceException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
public class PaymentslotWebClientApis extends ReactiveApiBinding {

	private static final String baseUrl = "http://localhost:9072/api/v1/private/payment-slots";

	private static final String GET_ALL_PAYMENTSLOTDTO_V1 = "/";
	private static final String PAYMENTSLOTDTO_BY_ID_V1 = "/{id}";
	private static final String ADD_PAYMENTSLOTDTO_V1 = "/";
	private static final String GENERATE_REPORT_V1 = "/by-weekday";

	public PaymentslotWebClientApis(String accessToken) {
		super(accessToken);
	}

	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public List<PaymentSlotDTO> getAll(int currentPage, int pageSize) {
		try {

			log.info("Getting all the payment slots through webclient with page: " + currentPage + " and size: "
					+ pageSize + ".");
			return webClient.get()
					.uri(uriBuilder -> uriBuilder.path(GET_ALL_PAYMENTSLOTDTO_V1).queryParam("page", currentPage)
							.queryParam("size", pageSize).build())
					.retrieve()
					.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
					.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
					.bodyToFlux(PaymentSlotDTO.class).collectList()
					.retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS))
							.onRetryExhaustedThrow((retryBackoffSpec,
									retrySignal) -> new ClientDataException(retrySignal.failure().getMessage())))
					.block();
		} catch (WebClientResponseException ex) {
			log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(),
					ex.getResponseBodyAsString());
			log.error("WebClientResponseException in retrieveAllPaymentSlotDTOs", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("Exception in retrieveAllPaymentSlots ", ex);
			throw ex;
		}
	}

	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param from
	 * @param to
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PagingResponse<PaymentSlotDTO> getAllWithPaging(int currentPage, int pageSize, LocalDate from,
			LocalDate to) {
		try {

			log.info("Getting all the payment slots through webclient with page: " + currentPage + " and size: "
					+ pageSize + "." + currentPage + " and From Date: " + from + "." + currentPage + " and To Date: "
					+ to + ".");
			return webClient.get()
					.uri(uriBuilder -> uriBuilder.path(GET_ALL_PAYMENTSLOTDTO_V1).queryParam("page", currentPage)
							.queryParam("size", pageSize).queryParam("from", from).queryParam("to", to).build())
					.retrieve()
					.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
					.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
					.bodyToMono(PagingResponse.class)
					.retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS))
							.onRetryExhaustedThrow((retryBackoffSpec,
									retrySignal) -> new ClientDataException(retrySignal.failure().getMessage())))
					.block();
		} catch (WebClientResponseException ex) {
			log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(),
					ex.getResponseBodyAsString());
			log.error("WebClientResponseException in retrieveAllPaymentSlotDTOs", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("Exception in retrieveAllPaymentSlots ", ex);
			throw ex;
		}
	}

	/**
	 * Service method to retrieve PaymentSlot By Id from Income microservice
	 * 
	 * @param id
	 * @return
	 */
	public Optional<PaymentSlotDTO> retrievePaymentSlotDTOById(Long id) {

		try {
			log.info(" executing retrievePaymentSlotDTOById method Getting  payment slots through webclient with id: "
					+ id);
			return Optional.of(webClient.get().uri(PAYMENTSLOTDTO_BY_ID_V1, id).retrieve()
					.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
					.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
					.bodyToMono(PaymentSlotDTO.class)
					.retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS))
							.onRetryExhaustedThrow((retryBackoffSpec,
									retrySignal) -> new ClientDataException(retrySignal.failure().getMessage())))
					.block());
		} catch (WebClientResponseException ex) {
			log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(),
					ex.getResponseBodyAsString());
			log.error("WebClientResponseException in retrievePaymentSlotDTOById", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("Exception in retrievePaymentSlotDTOById ", ex);
			throw ex;
		}
	}

	/**
	 * Service method to invoke update the payment slot in income microservice
	 * through web client
	 * 
	 * @param id
	 * @param PaymentSlotDTO
	 * @return
	 */
	public PaymentSlotDTO updatePaymentSlotDTO(Long id, PaymentSlotDTO PaymentSlotDTO) {

		try {
			log.info(" executing updatePaymentSlotDTO method to update  payment slot through webclient with id: " + id);
			return webClient.put().uri(PAYMENTSLOTDTO_BY_ID_V1, id).syncBody(PaymentSlotDTO).retrieve()
					.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
					.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
					.bodyToMono(PaymentSlotDTO.class)

					.onErrorResume(Mono::error)
					.retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS))
							.onRetryExhaustedThrow((retryBackoffSpec,
									retrySignal) -> new ClientDataException(retrySignal.failure().getMessage())))
					.block();
		} catch (WebClientResponseException ex) {
			log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(),
					ex.getResponseBodyAsString());
			log.error("WebClientResponseException in updatePaymentSlotDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("Exception in updatePaymentSlotDTO ", ex);
			throw ex;
		}
	}

	/**
	 * Service method to invoke the create payment slot in the income micro service
	 * through Webclient
	 * 
	 * @param PaymentSlotDTO
	 * @return
	 */
	public PaymentSlotDTO addNewPaymentSlotDTO(PaymentSlotDTO PaymentSlotDTO) {

		log.info("Executing addNewPaymentSlotDTO method to create payment slot through webclient. ");

		return webClient.post().uri(ADD_PAYMENTSLOTDTO_V1).syncBody(PaymentSlotDTO).retrieve()
				.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
				.bodyToMono(PaymentSlotDTO.class)

				.onErrorResume(Mono::error)
				.retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS)).onRetryExhaustedThrow(
						(retryBackoffSpec, retrySignal) -> new ClientDataException(retrySignal.failure().getMessage())))
				.block();

	}

	/**
	 * Service method to invoke the delete payment slot method in the income
	 * microservice through Webclient
	 * 
	 * @param id
	 * @return
	 */
	public String deletePaymentSlotDTOById(Long id) {

		try {
			log.info("Executing deletePaymentSlotDTOById method to delete payment slot through webclient with id : "
					+ id);

			return webClient.delete().uri(PAYMENTSLOTDTO_BY_ID_V1, id).retrieve().bodyToMono(String.class)
					.retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS))
							.onRetryExhaustedThrow((retryBackoffSpec,
									retrySignal) -> new ClientDataException(retrySignal.failure().getMessage())))
					.block();
		} catch (WebClientResponseException ex) {
			log.error("Error Response code is : {} and the message is {}", ex.getRawStatusCode(),
					ex.getResponseBodyAsString());
			log.error("WebClientResponseException in updatePaymentSlotDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("Exception in updatePaymentSlotDTO ", ex);
			throw ex;
		}

	}

	public List<EarningByWeek> generateReport(TimePeriod timePeriod, String userId) {

		log.info("Executing generateReport method to generate report through webclient with id : " + userId);

		List<EarningByWeek> reports = webClient.post().uri(GENERATE_REPORT_V1).syncBody(timePeriod).retrieve()
				.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
				.bodyToFlux(EarningByWeek.class)
				.collectList()
				.onErrorResume(Mono::error)
				.retryWhen(Retry.backoff(3, Duration.of(2, ChronoUnit.SECONDS)).onRetryExhaustedThrow(
						(retryBackoffSpec, retrySignal) -> new ClientDataException(retrySignal.failure().getMessage())))
				
				.block();
		log.info("In generateReport method successfully retrieved  report through webclient with size : " + reports.size());
		
		return reports;

	}

	public Mono<ClientDataException> handle4xxErrorResponse(ClientResponse clientResponse) {

		log.info("4xx error occured...............");
		Mono<String> errorResponse = clientResponse.bodyToMono(String.class);
		log.info("4xx error occured and retrieved message from the client..............");
		return errorResponse.flatMap((message) -> {
			log.error("ErrorResponse Code is " + clientResponse.rawStatusCode() + " and the exception message is : "
					+ message);
			throw new ClientDataException(message);
		});
	}

	public Mono<EmployeeServiceException> handle5xxErrorResponse(ClientResponse clientResponse) {

		log.info("5xx error occured...............");
		Mono<String> errorResponse = clientResponse.bodyToMono(String.class);
		log.info("5xx error occured and retrieved message from the client..............");
		return errorResponse.flatMap((message) -> {
			log.error("ErrorResponse Code is " + clientResponse.rawStatusCode() + " and the exception message is : "
					+ message);
			throw new EmployeeServiceException(message);
		});
	}

}
