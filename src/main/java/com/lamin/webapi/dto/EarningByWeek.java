package com.lamin.webapi.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarningByWeek {
	
	int weekDay;
	BigDecimal sumOfEarning;
	BigDecimal sumOfTipsWithTax;
	BigDecimal sumOfTipsInHand;
	BigDecimal avgSumOfEarning;
	BigDecimal avgTipsWithTax;
	BigDecimal avgTipsInHand;
	Long count;
	BigDecimal avgEarning;

}
