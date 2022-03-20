package com.lamin.webapi.service.client;

import java.util.Arrays;
import java.util.List;

import com.lamin.webapi.config.ApiBinding;
import com.lamin.webapi.dto.PaymentSlotDTO;

public class PaymentslotApis extends ApiBinding {

	 private static final String GET_ALL_PAYMENTSLOTDTO_V1 = "http://localhost:9072/api/v1/private/payment-slots";
		private static final String PAYMENTSLOTDTO_BY_ID_V1 = "http://localhost:9072/api/v1/private/payment-slots/{id}";
		private static final String ADD_PAYMENTSLOTDTO_V1 = "http://localhost:9072/api/v1/private/payment-slots";

	  public PaymentslotApis(String accessToken) {
	    super(accessToken);
	  }

	 

	  public List<PaymentSlotDTO> getAll() {
		  
	     PaymentSlotDTO[] array = restTemplate.getForEntity(
	    		GET_ALL_PAYMENTSLOTDTO_V1 , PaymentSlotDTO[].class).getBody();
	    
	     return Arrays.asList(array);
	  }

	}