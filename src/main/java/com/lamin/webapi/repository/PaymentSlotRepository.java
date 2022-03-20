package com.lamin.webapi.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.lamin.webapi.dto.PaymentSlotDTO;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class PaymentSlotRepository implements CommandLineRunner {

	boolean alreadySetup = false;

	private List<PaymentSlotDTO> list = new ArrayList<PaymentSlotDTO>();
	private Long index;

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentSlotRepository.class);

	@Override
	public void run(String... args) throws Exception {

		if (alreadySetup)
			return;

		createTestPaymentSlots();
		index = 6L;
		alreadySetup = true;
	}

	public List<PaymentSlotDTO> createTestPaymentSlots() {

		PaymentSlotDTO testData1 = createTestData(1L, new BigDecimal(46.5), new BigDecimal(10.5), new BigDecimal(5.5),
				LocalDate.of(2021, 2, 13), LocalDateTime.of(2021, 2, 13, 18, 00), LocalDateTime.of(2021, 2, 13, 20, 00),
				11, 21.0f);
		list.add(testData1);

		PaymentSlotDTO testData2 = createTestData(2L, new BigDecimal(53.5), new BigDecimal(12.5), new BigDecimal(6.5),
				LocalDate.of(2021, 2, 14), LocalDateTime.of(2021, 2, 14, 18, 00), LocalDateTime.of(2021, 2, 14, 20, 00),
				13, 41.0f);
		list.add(testData2);

		PaymentSlotDTO testData3 = createTestData(3L, new BigDecimal(53.5), new BigDecimal(12.5), new BigDecimal(6.5),
				LocalDate.of(2021, 2, 15), LocalDateTime.of(2021, 2, 15, 18, 00), LocalDateTime.of(2021, 2, 15, 20, 00),
				13, 41.0f);
		list.add(testData3);

		PaymentSlotDTO testData4 = createTestData(4L, new BigDecimal(53.5), new BigDecimal(12.5), new BigDecimal(6.5),
				LocalDate.of(2021, 2, 16), LocalDateTime.of(2021, 2, 16, 18, 00), LocalDateTime.of(2021, 2, 16, 20, 00),
				13, 41.0f);
		list.add(testData4);

		PaymentSlotDTO testData5 = createTestData(5L, new BigDecimal(53.5), new BigDecimal(12.5), new BigDecimal(6.5),
				LocalDate.of(2021, 2, 17), LocalDateTime.of(2021, 2, 17, 18, 00), LocalDateTime.of(2021, 2, 17, 20, 00),
				13, 41.0f);
		list.add(testData5);		

		return list;
	}

	private PaymentSlotDTO createTestData(Long id, BigDecimal earning, BigDecimal tipsWithTax, BigDecimal tipsInHand,
			LocalDate date, LocalDateTime startTime, LocalDateTime endTime, Integer noOfDelivery, Float distance) {

		PaymentSlotDTO paymentSlotDTO = new PaymentSlotDTO(id, null, earning, tipsWithTax, tipsInHand, date, startTime,
				endTime, noOfDelivery, distance);

		return paymentSlotDTO;

	}

}