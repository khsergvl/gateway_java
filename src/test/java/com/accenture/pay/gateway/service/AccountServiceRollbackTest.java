package com.accenture.pay.gateway.service;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.exception.UnprocessableStateException;
import com.accenture.pay.gateway.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class AccountServiceRollbackTest {
    private static final Runnable ASSERT_TRANSACTION_STATE = () -> assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

    @Autowired
    private AccountService accountService;

    @Autowired
    @SpyBean
    private AccountRepository accountRepository;

    @Test
    public void rollback_OnPersistFailure() {
        var firstAccount = new Account(5, 0.10);
        var secondAccount = new Account(6, 0.10);
        var amount = 0.01;
        accountRepository.saveAll(List.of(firstAccount, secondAccount));
        var modifiedAccount = new Account(6, 0.11);
        doThrow(new RuntimeException()).when(accountRepository).save(eq(modifiedAccount));

        assertThrows(UnprocessableStateException.class, () -> accountService.transfer(firstAccount.getId(), secondAccount.getId(), amount, ASSERT_TRANSACTION_STATE));

        var updateSecondAccount = accountService.getAccount(secondAccount.getId());
        var updatedFirstAccount = accountService.getAccount(firstAccount.getId());
        assertEquals(secondAccount.getBalance(), updateSecondAccount.getBalance());
        assertEquals(firstAccount.getBalance(), updatedFirstAccount.getBalance());
    }

}
