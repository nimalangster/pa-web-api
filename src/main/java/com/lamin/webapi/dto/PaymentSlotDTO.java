package com.lamin.webapi.dto;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PaymentSlotDTO {
	
	public interface CreateValidation {
	}
	
	public interface SearchValidation {
	}
	
	public interface UpdateValidation {
	}

	@JsonProperty(required = false)		
	private Long id;
	
	@JsonProperty(required = true)	
	private String userId;

	@JsonProperty(required = true)	
	@PositiveOrZero(message = "Earning must zero or positive")
	private BigDecimal earning;

	@PositiveOrZero(message = "Tips with tax must zero or positive")
	private BigDecimal tipsWithTax;

	@PositiveOrZero(message = "Tips in hand must zero or positive")
	private BigDecimal tipsInHand;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;	
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonProperty(required = true)	
	@PastOrPresent
	private LocalDateTime startTime;

	//@Temporal(TemporalType.TIMESTAMP)
	
	//@DateTimeFormat(pattern = "yyyy-MM-dd. HH:mm a")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)	
	@JsonProperty(required = true)	
	@PastOrPresent
	private LocalDateTime endTime;

	@JsonProperty(required = true)	
	@PositiveOrZero(message = "No of delivery must be positive or Zero")
	private Integer noOfDelivery;

	@JsonProperty(required = true)		
	@PositiveOrZero(message = "No of distance must be positive or Zero")
	private Float distance;
}