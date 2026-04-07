package com.microservices.banking.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservices.banking.account.entity.Account;

import jakarta.persistence.LockModeType;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

	
	Optional<Account> findByAccountNumber(String accountNumber);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Account a WHERE a.accountNumber = :accNo")
	Optional<Account> findByAccountNumberForUpdate(String accNo);
	
	@Query(value = "SELECT account_seq.NEXTVAL FROM dual", nativeQuery = true)
	Long getNextAccountNumber();
}
