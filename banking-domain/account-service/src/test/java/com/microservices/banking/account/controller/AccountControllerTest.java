package com.microservices.banking.account.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.microservices.banking.account.entity.Account;
import com.microservices.banking.account.service.AccountService;

@WebMvcTest(AccountController.class) // ✅ FIXED
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Test
    void testCreateAccount() throws Exception {

        Account acc = new Account();
        acc.setAccountNumber("123456");

        Mockito.when(service.create(Mockito.any()))
                .thenReturn(acc);

        String json = """
            {
              "accountHolderName": "Ram",
              "email": "ram@gmail.com",
              "balance": 5000
            }
            """;

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accountNumber").value("123456"));
    }

    @Test
    void testTransfer() throws Exception {

        Account acc = new Account();
        acc.setAccountNumber("111");
        acc.setBalance(BigDecimal.valueOf(800));

        Mockito.when(service.transfer(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(acc);

        String json = """
            {
              "fromAccNo": "111",
              "toAccNo": "222",
              "amount": 200
            }
            """;

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}