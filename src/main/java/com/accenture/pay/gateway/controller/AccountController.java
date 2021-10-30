package com.accenture.pay.gateway.controller;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Integer id) {
        var account = accountService.getAccount(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}
