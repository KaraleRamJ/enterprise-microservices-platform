package com.microservices.banking.account.serviceimpl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.banking.account.dto.AccountDTO;
import com.microservices.banking.account.entity.Account;
import com.microservices.banking.account.exception.InsufficientBalanceException;
import com.microservices.banking.account.exception.NotFoundException;
import com.microservices.banking.account.repository.AccountRepository;
import com.microservices.banking.account.service.AccountService;
import com.microservices.banking.account.util.AccountNumberGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
	
	private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
	
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
				.orElseThrow(() -> {
					log.error("Account not found: accNo={}", mask(accNo));
					return new NotFoundException("Account not found with number: " + accNo);
				});
	}

	@Override
	@Transactional
	public Account deposit(String accNo, BigDecimal amount) {
		
		Account acc = accRepo.findByAccountNumberForUpdate(accNo)
				.orElseThrow(() -> {
					
				log.error("Account not found for deposit: accNo={]", mask(accNo));
				return new NotFoundException("Account not found with number: " + accNo);
		});
		
		acc.setBalance(acc.getBalance().add(amount));
		
		return accRepo.save(acc);
	}

	@Override
	@Transactional
	public Account withdraw(String accNo, BigDecimal amount) {
		
		Account acc = accRepo.findByAccountNumberForUpdate(accNo)
				.orElseThrow(() -> {
					log.error("Account not found for withdraw: accNo={}", mask(accNo));
					return new NotFoundException("Account not found with number: " + accNo);
				});
		
		if(acc.getBalance().compareTo(amount) < 0) {
			throw new InsufficientBalanceException("Insufficient balance in account: " + accNo);
		}
		
		acc.setBalance(acc.getBalance().subtract(amount));
		
		return accRepo.save(acc);
	}

	@Override
	@Transactional
	public Account transfer(String fromAccNo, String toAccNo, BigDecimal amount) {

	    try {

	        Account fromAcc;
	        Account toAcc;

	        if (fromAccNo.compareTo(toAccNo) < 0) {
	        	
	        	log.debug("Locking order: from -> to");

	            fromAcc = accRepo.findByAccountNumberForUpdate(fromAccNo)
	            	
	                    .orElseThrow(() -> {
	                    	log.error("Account not found for transfer: fromAcc={}", mask(fromAccNo));
	                    	return new NotFoundException("From account not found with number: " + fromAccNo);
	                    });

	            toAcc = accRepo.findByAccountNumberForUpdate(toAccNo)
	            		.orElseThrow(() -> {
	            			log.error("Account not found for transfer: toAcc={}", mask(toAccNo));
	            			return new NotFoundException("To account not found with number: " + toAccNo);
	                    });


	        } else {
	        	
	        	log.debug("Locking order: to -> from");

	            toAcc = accRepo.findByAccountNumberForUpdate(toAccNo)
	            		.orElseThrow(() -> {
	            			log.error("Account not found for transfer: toAcc={}", mask(toAccNo));
	            			return new NotFoundException("To account not found with number: " + toAccNo);
	                    });


	            fromAcc = accRepo.findByAccountNumberForUpdate(fromAccNo)
	            		.orElseThrow(() -> {
	            			log.error("Account not found for transfer: fromAcc={}", mask(fromAccNo));
	            			return new NotFoundException("From account not found with number: " + fromAccNo);
	                    });

	        }

	        if (fromAcc.getBalance().compareTo(amount) < 0) {
	        	
	        	log.warn("Transfer failed (insufficient balance): from={}, balance={}, amount={}",
                        mask(fromAccNo), fromAcc.getBalance(), amount);
	        	throw new InsufficientBalanceException("Insufficient balance in account: " + fromAcc);
	        }

	        fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
	        toAcc.setBalance(toAcc.getBalance().add(amount));

	        return fromAcc;

	    } catch (Exception e) {

	        log.error("Transfer failed: from={}, to={}, amount={}, error={}",
	                fromAccNo, toAccNo, amount, e.getMessage(), e);

	        throw e;
	    }
	}
	
	 // 🔒 Mask account number
    private String mask(String accNo) {
        if (accNo == null || accNo.length() < 4) return "XXXX";
        return "XXXXXX" + accNo.substring(accNo.length() - 4);
    }
}
