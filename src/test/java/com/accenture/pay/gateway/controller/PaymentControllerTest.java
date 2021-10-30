package com.accenture.pay.gateway.controller;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.repository.AccountRepository;
import com.accenture.pay.gateway.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class PaymentControllerTest {
    private static final String PATH = "/transfer";
    private static final String REQUEST_BODY_TEMPLATE = "{\"from\":%s,\"to\":%s, \"amount\":%s}";
    private static final Account FIRST_ACCOUNT = new Account(3, 0.5);
    private static final Account SECOND_ACCOUNT = new Account(4, 0.25);
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp(@Autowired AccountService accountService) {
        mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController(accountService)).build();
    }

    @Test
    public void happyPath() throws Exception {
        var amount = 0.2;
        assertTrue(amount < FIRST_ACCOUNT.getBalance());
        accountRepository.saveAll(List.of(FIRST_ACCOUNT, SECOND_ACCOUNT));
        var requestBody = String.format(REQUEST_BODY_TEMPLATE, FIRST_ACCOUNT.getId(), SECOND_ACCOUNT.getId(), amount);
        mockMvc.perform((post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("SUCCESS"));
    }

    @Test
    public void invalidAmount() throws Exception {
        var amount = 0.002;
        accountRepository.saveAll(List.of(FIRST_ACCOUNT, SECOND_ACCOUNT));
        var requestBody = String.format(REQUEST_BODY_TEMPLATE, FIRST_ACCOUNT.getId(), SECOND_ACCOUNT.getId(), amount);
        mockMvc.perform((post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void notEnoughFunds() throws Exception {
        var amount = 0.6;
        assertTrue(amount > FIRST_ACCOUNT.getBalance());
        accountRepository.saveAll(List.of(FIRST_ACCOUNT, SECOND_ACCOUNT));


        var requestBody = String.format(REQUEST_BODY_TEMPLATE, FIRST_ACCOUNT.getId(), SECOND_ACCOUNT.getId(), amount);
        mockMvc.perform((post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("FAILED"));
    }

    @Test
    public void accountNotFound() throws Exception {
        var amount = 0.01;
        assertTrue(amount < FIRST_ACCOUNT.getBalance());
        accountRepository.save(FIRST_ACCOUNT);
        assertTrue(accountRepository.findById(SECOND_ACCOUNT.getId()).isEmpty());

        var requestBody = String.format(REQUEST_BODY_TEMPLATE, FIRST_ACCOUNT.getId(), SECOND_ACCOUNT.getId(), amount);
        mockMvc.perform((post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @AfterEach
    public void tearDown() {
        accountRepository.deleteAll();
    }
}
