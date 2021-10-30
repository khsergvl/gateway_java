package com.accenture.pay.gateway.controller;

import com.accenture.pay.gateway.controller.AccountController;
import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.repository.AccountRepository;
import com.accenture.pay.gateway.service.AccountService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class AccountControllerTest {
    private static final String PATH = "/account/%s";
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp(@Autowired AccountService accountService) {
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService)).build();
    }

    @Test
    public void accountExists() throws Exception {
        var account = new Account(1, 0.5);
        accountRepository.save(account);

        mockMvc.perform((get(String.format(PATH, account.getId()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.balance").value(account.getBalance()));
    }

    @Test
    public void accountNoExists() throws Exception {
        var id = 1;
        assertTrue(accountRepository.findById(id).isEmpty());
        mockMvc.perform((get(String.format(PATH, id))))
                .andExpect(status().isNotFound());
    }

    @AfterEach
    public void tearDown() {
        accountRepository.deleteAll();
    }
}
