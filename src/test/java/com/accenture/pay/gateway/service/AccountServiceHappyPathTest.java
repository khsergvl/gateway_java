package com.accenture.pay.gateway.service;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.repository.AccountRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static com.accenture.pay.gateway.model.Status.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountServiceHappyPathTest {
    private static final Runnable ASSERT_TRANSACTION_STATE = () -> assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void happyPathMoneyTransfer() {
        var firstAccount = new Account(5, 0.10);
        var secondAccount = new Account(6, 0.10);
        var amount = 0.01;

        accountRepository.saveAll(List.of(firstAccount, secondAccount));

        var status = accountService.transfer(firstAccount.getId(), secondAccount.getId(), amount, ASSERT_TRANSACTION_STATE);
        assertEquals(SUCCESS, status);
        var updateSecondAccount = accountService.getAccount(secondAccount.getId());
        var updatedFirstAccount = accountService.getAccount(firstAccount.getId());
        assertEquals(secondAccount.getBalance() + amount, updateSecondAccount.getBalance());
        assertEquals(firstAccount.getBalance() - amount, updatedFirstAccount.getBalance());
    }

}
