package com.microservices.banking.account.serviceimpl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.banking.account.dto.AccountDTO;
import com.microservices.banking.account.entity.Account;
import com.microservices.banking.account.repository.AccountRepository;
import com.microservices.banking.account.service.AccountService;
import com.microservices.banking.account.util.AccountNumberGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
	
	private final AccountRepository accRepo;
	private final AccountNumberGenerator generator;
	

	@Override
	public Account create(AccountDTO dto) {
		
		// Step 1: get sequence
        Long seq = accRepo.getNextAccountNumber();
        
        // Step 2: generate account number
        String branchCode = "1234"; // later from DB / request
        String accountNumber = generator.generate(branchCode, seq);
		
		Account account = Account.builder()
				.accountNumber(accountNumber)
				.accountHolderName(dto.getAccountHolderName())
				.email(dto.getEmail())
				.balance(dto.getBalance())
				.build();
		
		return accRepo.save(account);
	}

	@Override
	public Account getAccount(String accNo) {
		
		return accRepo.findByAccountNumber(accNo)
				.orElseThrow(() -> new RuntimeException());
	}

	@Override
	@Transactional
	public Account deposit(String accNo, BigDecimal amount) {
		
		Account acc = accRepo.findByAccountNumberForUpdate(accNo)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		
		acc.setBalance(acc.getBalance().add(amount));
		
		return accRepo.save(acc);
	}

	@Override
	@Transactional
	public Account withdraw(String accNo, BigDecimal amount) {
		
		Account acc = accRepo.findByAccountNumberForUpdate(accNo)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		
		if(acc.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}
		
		acc.setBalance(acc.getBalance().subtract(amount));
		
		return accRepo.save(acc);
	}

	@Override
	@Transactional
	public Account transfer(String fromAccNo, String toAccNo, BigDecimal amount) {
		
		// 🔒 ALWAYS lock in SAME ORDER to avoid deadlock
	    Account fromAcc;
	    Account toAcc;

	    if(fromAccNo.compareTo(toAccNo) < 0) {
	    	fromAcc = accRepo.findByAccountNumber(fromAccNo)
	    			.orElseThrow(() -> new RuntimeException("From Account not found"));
	    	
	    	toAcc = accRepo.findByAccountNumberForUpdate(toAccNo)
	    			.orElseThrow(() -> new RuntimeException("To Account not found"));
	    }else {
	    	
	    	toAcc = accRepo.findByAccountNumberForUpdate(toAccNo)
	                .orElseThrow(() -> new RuntimeException("To account not found"));
	    	
	        fromAcc = accRepo.findByAccountNumberForUpdate(fromAccNo)
	                .orElseThrow(() -> new RuntimeException("From account not found"));
	    }
		
		//Check balance
		if(fromAcc.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient Balance");
		}
		
		//Debit
		fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
		
		//Credit
		toAcc.setBalance(toAcc.getBalance().add(amount));
		
		accRepo.save(fromAcc);
		accRepo.save(toAcc);
		
		return fromAcc;
	}
}
