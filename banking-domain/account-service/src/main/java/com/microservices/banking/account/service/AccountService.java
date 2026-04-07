package com.microservices.banking.account.service;

import java.math.BigDecimal;

import com.microservices.banking.account.dto.AccountDTO;
import com.microservices.banking.account.entity.Account;

public interface AccountService {

	public Account create(AccountDTO dto);
	
	public Account getAccount(String accNo);
	
	public Account deposit(String accNo, BigDecimal amount);
	
	public Account withdraw(String accNo, BigDecimal amount);
	
	public Account transfer(String fromAccNo, String toAccNo, BigDecimal amount);
	
}
