package com.microservices.banking.account.util;

import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {

    public String generate(String branchCode, Long sequence) {

        // Step 1: base number
        String base = branchCode + String.format("%08d", sequence);

        // Step 2: check digit
        int checkDigit = calculateCheckDigit(base);

        return base + checkDigit;
    }

    private int calculateCheckDigit(String number) {
        int sum = 0;
        for (char c : number.toCharArray()) {
            sum += Character.getNumericValue(c);
        }
        return sum % 10;
    }
}