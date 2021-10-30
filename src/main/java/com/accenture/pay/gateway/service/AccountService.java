package com.accenture.pay.gateway.service;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.model.Status;

public interface AccountService {
    Account getAccount(Integer id);
    Status transfer(Integer from, Integer to, Double amount, Runnable runnable);
}
