package com.microservices.banking.account.controller;

import java.math.BigDecimal;

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
import com.microservices.banking.account.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService service;
	
	@PostMapping
	public Account create(@RequestBody AccountDTO dto) {
		return service.create(dto);
	}
	
	@GetMapping("/{accNo}")
	public Account get(@PathVariable String accNo) {
		return service.getAccount(accNo);
	}
	
	@PostMapping("/{accNo}/deposit")
    public Account deposit(@PathVariable String accNo,
                           @RequestParam BigDecimal amount) {
        return service.deposit(accNo, amount);
    }

    @PostMapping("/{accNo}/withdraw")
    public Account withdraw(@PathVariable String accNo,
                            @RequestParam BigDecimal amount) {
        return service.withdraw(accNo, amount);
    }
    
    @PostMapping("/transfer")
    public Account transfer(@RequestBody TransferRequest request) {
        return service.transfer(
                request.getFromAccNo(),
                request.getToAccNo(),
                request.getAmount()
        );
    }
    
}
