package com.microservices.banking.account.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.microservices.banking.account.dto.AccountDTO;
import com.microservices.banking.account.entity.Account;
import com.microservices.banking.account.repository.AccountRepository;
import com.microservices.banking.account.util.AccountNumberGenerator;

public class AccountServiceImplTest {

	@InjectMocks
	private AccountServiceImpl service;
	
	@Mock
	private AccountRepository repository;
	
	@Mock
	private AccountNumberGenerator generator;
	
	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testCreateAccount() {

	    AccountDTO dto = new AccountDTO();
	    dto.setAccountHolderName("Ram");
	    dto.setEmail("ram@gmail.com");
	    dto.setBalance(BigDecimal.valueOf(5000));

	    when(repository.getNextAccountNumber()).thenReturn(1001L);
	    when(generator.generate(anyString(), anyLong())).thenReturn("123400010001");
	    when(repository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

	    Account result = service.create(dto);

	    assertNotNull(result);
	    assertEquals("123400010001", result.getAccountNumber());

	    verify(repository).save(any(Account.class)); // ✅ important
	}
	
	@Test
	void testGetAccountSuccess() {

	    Account acc = new Account();
	    acc.setAccountNumber("123");

	    when(repository.findByAccountNumber("123")).thenReturn(Optional.of(acc));

	    Account result = service.getAccount("123");

	    assertEquals("123", result.getAccountNumber());
	}
	
	@Test
	void testGetAccountNotFound() {

	    when(repository.findByAccountNumber("123"))
	            .thenReturn(Optional.empty());

	    assertThrows(RuntimeException.class,
	            () -> service.getAccount("123"));
	}
	
	@Test
	void testDeposit() {

	    Account acc = new Account();
	    acc.setBalance(BigDecimal.valueOf(1000));

	    when(repository.findByAccountNumberForUpdate("123"))
	            .thenReturn(Optional.of(acc));
	    
	    when(repository.save(any(Account.class)))
	                     .thenAnswer(i -> i.getArguments()[0]);

	    Account result = service.deposit("123", BigDecimal.valueOf(500));

	    assertEquals(BigDecimal.valueOf(1500), result.getBalance());
	}
	
	@Test
	void testWithdrawInsufficientBalance() {

	    Account acc = new Account();
	    acc.setBalance(BigDecimal.valueOf(100));

	    when(repository.findByAccountNumberForUpdate("123"))
	            .thenReturn(Optional.of(acc));
	    
	    when(repository.save(any(Account.class)))
        .thenAnswer(i -> i.getArguments()[0]);

	    assertThrows(RuntimeException.class,
	            () -> service.withdraw("123", BigDecimal.valueOf(500)));
	}
	
	@Test
	void testTransfer() {

	    Account from = new Account();
	    from.setAccountNumber("111");
	    from.setBalance(BigDecimal.valueOf(1000));

	    Account to = new Account();
	    to.setAccountNumber("222");
	    to.setBalance(BigDecimal.valueOf(500));

	    when(repository.findByAccountNumberForUpdate("111"))
	            .thenReturn(Optional.of(from));
	    when(repository.findByAccountNumberForUpdate("222"))
	            .thenReturn(Optional.of(to));

	    service.transfer("111", "222", BigDecimal.valueOf(200));

	    assertEquals(BigDecimal.valueOf(800), from.getBalance());
	    assertEquals(BigDecimal.valueOf(700), to.getBalance());
	}
}
