
//package com.lamin.webapi.service.client;
//
//@Component
//public class IncomeClientService {
//	
//	@Autowired
//	RestTemplate restTemplate;
//
//	@Autowired
//	RedisCustomerInfoRedisRepository redisRepository;
//	
//	@Autowired
//	Tracer tracer;
//
//	private static final Logger logger = LoggerFactory.getLogger(IncomeClientService.class);
//	
//	@CircuitBreaker(fallbackMethod = "buildFallbackIncome", name = "IncomeBreaker")
//	public IncomeInfo getIncome(String phoneNo) throws TimeoutException {
//		
//		CustomerInfo customerInfo = null;
//
//		logger.info("Searching for existing Customer by phone no thro Customer client Service with phone No: {}", phoneNo);	
//		customerInfo = checkRedisCache(phoneNo);
//
//		if (customerInfo != null) {
//			logger.debug("Successfully retrieved an customer with phone No : {} from the redis cache: {}", phoneNo,
//					customerInfo);
//			return customerInfo;
//		}
//		logger.debug("Unable to locate organization from the redis cache with phone No: {}. So calling Customer serice", phoneNo);
//				
//		CustomerMobileInfo mobileNoInfo = new CustomerMobileInfo();
//		mobileNoInfo.setMobileNo(phoneNo);
//		ResponseEntity<CustomerInfo> restExchange = restTemplate.exchange(
//				"http://rms-gateway-service/authorization/api/v1/public/customers/searches", HttpMethod.POST, new HttpEntity <>(mobileNoInfo),
//				CustomerInfo.class, phoneNo);
//		customerInfo = (CustomerInfo) restExchange.getBody();
//		logger.info("Customer client Service found Customer. Customer Name is : {}", customerInfo.getFullName());
//		if (customerInfo != null) {
//			cacheCustomerInfo(customerInfo);
//		}
//		//randomlyRunLong();
//		return customerInfo;
//	}	
//	
//	@CircuitBreaker(fallbackMethod = "buildFallbackSaveCustomer", name = "CustomerSaveBreaker")
//	public IncomeInfo saveIncome(IncomeInfo incomeInfo) {
//		
//		logger.debug("Customer client Service create new Customer with phone no: {}", customerInfo.getUser().getMobileNo());			
//		ResponseEntity<CustomerInfo> restExchange = restTemplate.exchange(
//				"http://rms-gateway-service/authorization/api/v1/public/customers", HttpMethod.POST, new HttpEntity <>(customerInfo),
//				CustomerInfo.class);	
//		if (customerInfo != null) {
//			cacheCustomerInfo(customerInfo);
//		}
//		return (CustomerInfo) restExchange.getBody();
//	}
//	
//	private void cacheCustomerInfo(CustomerInfo customerInfo) {
//		
//		RedisCustomerInfo redisCustomerInfo = CustomerMapper.customerInfoToRedisCustomerInfo(customerInfo);		
//		redisRepository.save(redisCustomerInfo);		
//	}	
//
//	private CustomerInfo checkRedisCache(String phoneNo) {
//		
//		ScopedSpan newSpan = tracer.startScopedSpan("readLicensingDataFromRedis");
//		
//		try {
//			RedisCustomerInfo redisCustomerInfo = redisRepository.findRedisCustomerInfoByMobileNo(phoneNo);
//			if(redisCustomerInfo != null)
//				return CustomerMapper.redisCustomerInfoToCustomerInfo(redisCustomerInfo);
//			else
//				return null;
//		} catch (Exception ex) {
//			logger.error("Error encountered while trying to retrieve Customer with phone no: {} from Redis Cache.",
//					phoneNo);
//			ex.printStackTrace();
//			return null;
//		}finally {
//			 newSpan.tag("peer.service", "redis"); 
//			 newSpan.annotate("Client received");
//			 newSpan.finish(); 
//			 logger.debug("New span added for : " + tracer.toString());
//			 } 
//	}	
//
//	private CustomerInfo buildFallbackCustomer(String phoneNo, Throwable t){
//		
//		CustomerInfo customer = new CustomerInfo();
//		customer.setCustomerCode("0000000-00-00000");
//		customer.setFullName("Sorry no licensing information currently available");
//		
//		return customer;
//	}	
//	
//	private void randomlyRunLong() throws TimeoutException{
//		Random rand = new Random();
//		int randomNum = rand.nextInt((3 - 1) + 1) + 1;
//		if (randomNum==3) sleep();
//	}
//	
//	private void sleep() throws TimeoutException{
//		try {
//			System.out.println("Sleep");
//			Thread.sleep(5000);
//			throw new java.util.concurrent.TimeoutException();
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage());
//		}
//	}	
//}

package com.lamin.webapi.service.client;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lamin.webapi.dto.PaymentSlotDTO;
import com.lamin.webapi.dto.TimePeriod;
import com.lamin.webapi.repository.PaymentSlotRepository;

@Component
public class IncomeClientService {
	
		
	PaymentSlotRepository repo;	

	public IncomeClientService(PaymentSlotRepository repo) {
		super();
		this.repo = repo;
	}


	public void save(PaymentSlotDTO paymentSlot) {
		// TODO Auto-generated method stub
		System.out.println("User id of the logged in user is: "+ paymentSlot.getUserId());
		paymentSlot.setId(repo.getIndex());
		repo.setIndex(repo.getIndex()+1);
		repo.getList().add(paymentSlot);
		
	}

	public Optional<PaymentSlotDTO> findById(long id) {
		
		for (PaymentSlotDTO paymentSlotDTO : repo.getList()) {			
			if (paymentSlotDTO.getId() == id)
				return Optional.of(paymentSlotDTO);	
		}
		return null;
	}


	public void delete(PaymentSlotDTO paymentSlot) {
		
		repo.getList().remove(paymentSlot);
		
	}


	public void update(PaymentSlotDTO paymentSlot) {
		
		PaymentSlotDTO existingSlot = null;
		
		for (PaymentSlotDTO paymentSlotDTO : repo.getList()) {			
			if (paymentSlotDTO.getId() == paymentSlot.getId())
				existingSlot = paymentSlotDTO;				
		}
		repo.getList().remove(existingSlot);
		
		repo.getList().add(paymentSlot);
		
	}


	public Object getAll(String jwtToken) {
		
		return repo.getList();
	}


	public void generateReport(TimePeriod timePeriod, String userId) {
		// TODO Auto-generated method stub
		
	}
}
