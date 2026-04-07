package com.microservices.banking.account.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountDTO {

	 private String accountHolderName;
	 private String email;
	 private BigDecimal balance;
}
