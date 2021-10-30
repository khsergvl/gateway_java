package com.accenture.pay.gateway.controller;

import com.accenture.pay.gateway.model.Payment;
import com.accenture.pay.gateway.model.Status;
import com.accenture.pay.gateway.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PaymentController {

    public PaymentController(AccountService accountService) {
        this.accountService = accountService;
    }

    private final AccountService accountService;

    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Status> transfer(@Valid @RequestBody Payment payment) {
        var transferStatus = accountService.transfer(payment.getFrom(), payment.getTo(), payment.getAmount(), null);
        var httpsStatus = transferStatus.equals(Status.SUCCESS) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(transferStatus, httpsStatus);
    }
}
