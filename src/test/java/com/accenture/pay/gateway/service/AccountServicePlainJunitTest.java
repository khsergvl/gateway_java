package com.accenture.pay.gateway.service;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.exception.NotFoundException;
import com.accenture.pay.gateway.repository.AccountRepository;
import com.accenture.pay.gateway.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServicePlainJunitTest {

    @Test
    public void accountExists() {
        var account = new Account(1, 0.5);
        var accountRepository = mock(AccountRepository.class);
        var lockRegistry = mock(LockRegistry.class);
        var accountService = new AccountServiceImpl(accountRepository, lockRegistry);

        when(accountRepository.findById(eq(account.getId()))).thenReturn(Optional.of(account));

        assertEquals(account, accountService.getAccount(account.getId()));
    }

    @Test
    public void accountNotExists() {
        var account = new Account(1, 0.5);
        var accountRepository = mock(AccountRepository.class);
        var lockRegistry = mock(LockRegistry.class);
        var accountService = new AccountServiceImpl(accountRepository, lockRegistry);

        when(accountRepository.findById(eq(account.getId()))).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> accountService.getAccount(account.getId()));
        assertEquals(String.format("Account with id = %s not found", account.getId()), exception.getMessage());
    }
}
