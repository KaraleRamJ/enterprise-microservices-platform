package com.microservices.banking.account.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransferRequest {
    private String fromAccNo;
    private String toAccNo;
    private BigDecimal amount;
}