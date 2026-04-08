package com.microservices.banking.account.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.banking.account.dto.AccountDTO;
import com.microservices.banking.account.dto.TransferRequest;
import com.microservices.banking.account.entity.Account;
import com.microservices.banking.account.exception.GlobalExceptionHandler;
import com.microservices.banking.account.response.ApiResponse;
import com.microservices.banking.account.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final GlobalExceptionHandler globalExceptionHandler;

	private static final Logger log = LoggerFactory.getLogger(AccountController.class);
	
	private final AccountService service;

	
	@PostMapping
	public ApiResponse<Account> create(@RequestBody AccountDTO dto) {
		
		log.info("Creating account for name: {}, email: {}",
				dto.getAccountHolderName(),
				dto.getEmail()
				);
		Account account = service.create(dto);
		
		log.info("Account created successfully with accountNumber: {}",
				account.getAccountNumber()
				);
		
		return ApiResponse.<Account>builder()
				.success(true)
				.message("Account created successfully")
				.data(account)
				.build();
	}
	
	@GetMapping("/{accNo}")
	public ApiResponse<Account> get(@PathVariable String accNo) {
		
		log.info("Fetching account details for accountNumber: {}", mask(accNo));
		
		Account account = service.getAccount(accNo);
		
		log.info("Account fetched successfully: {}", mask(accNo));
		
		return ApiResponse.<Account>builder()
				.success(true)
				.message("Account fetched successfully")
				.data(account)
				.build();
	}
	
	@PostMapping("/{accNo}/deposit")
    public ApiResponse<Account> deposit(@PathVariable String accNo,
                           @RequestParam BigDecimal amount) {
		
		log.info("Deposit request: accountNumber={}, amount={}", mask(accNo), amount);
		
		Account account = service.deposit(accNo, amount);
		
		log.info("Deposit successful: accountNumber={}, newBalance={}", mask(accNo), account.getBalance());
		
        return ApiResponse.<Account>builder()
        		.success(true)
        		.message(amount+" Amount deposited successfully")
        		.data(account)
        		.build();
    }

    @PostMapping("/{accNo}/withdraw")
    public ApiResponse<Account> withdraw(@PathVariable String accNo,
                            @RequestParam BigDecimal amount) {
    	
    	log.info("Withdraw request: accountNumber={}, amount={}", mask(accNo), amount);
    	
    	Account account = service.withdraw(accNo, amount);
    	
    	log.info("Withdraw successfull: accountNumber={}, newBalance={}", mask(accNo), account.getBalance());
    	
        return ApiResponse.<Account>builder()
        		.success(true)
        		.message(amount+" Amount withdraw successfully")
        		.data(account)
        		.build();
    }
    
    @PostMapping("/transfer")
    public ApiResponse<Account> transfer(@RequestBody TransferRequest request) {
    	
    	log.info("Transfer request: from={}, to={}, amount={}", 
    			request.getFromAccNo(), 
    			mask(request.getToAccNo()), 
    			request.getAmount());
    	
    	Account account =  service.transfer(
                request.getFromAccNo(),
                request.getToAccNo(),
                request.getAmount()
        );
    	
    	log.info("Transfer successful: from={}, to={}, amount={}",
    			request.getFromAccNo(),
    			mask(request.getToAccNo()),
    			request.getAmount());
    	
        return ApiResponse.<Account>builder()
        		.success(true)
        		.message(request.getAmount()+" amount transfer successfully")
        		.data(account)
        		.build();
    }
    
    private String mask(String accNo) {
        if (accNo == null || accNo.length() < 4) return "XXXX";
        return "XXXXXX" + accNo.substring(accNo.length() - 4);
    }
    
}
