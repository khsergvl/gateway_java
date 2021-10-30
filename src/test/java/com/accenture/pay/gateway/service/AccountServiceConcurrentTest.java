package com.accenture.pay.gateway.service;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.repository.AccountRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.accenture.pay.gateway.model.Status.FAILED;
import static com.accenture.pay.gateway.model.Status.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class AccountServiceConcurrentTest {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);
    private static final Runnable ASSERT_TRANSACTION_STATE = () -> assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void concurrentModification() throws InterruptedException {
        var firstAccount = new Account(5, 0.10);
        var secondAccount = new Account(6, 0.10);
        var amount = 0.01;
        var cdl = new CountDownLatch(2);
        accountRepository.saveAll(List.of(firstAccount, secondAccount));
        EXECUTOR.execute(() -> {
            var status = accountService.transfer(firstAccount.getId(), secondAccount.getId(), amount, ASSERT_TRANSACTION_STATE);
            assertEquals(SUCCESS, status);
            cdl.countDown();
        });
        EXECUTOR.execute(() -> {
            var status = accountService.transfer(firstAccount.getId(), secondAccount.getId(), 0.21, ASSERT_TRANSACTION_STATE);
            assertEquals(FAILED, status);

            cdl.countDown();
        });
        cdl.await();
        var updateSecondAccount = accountService.getAccount(secondAccount.getId());
        var updatedFirstAccount = accountService.getAccount(firstAccount.getId());
        assertEquals(secondAccount.getBalance() + amount, updateSecondAccount.getBalance());
        assertEquals(firstAccount.getBalance() - amount, updatedFirstAccount.getBalance());
    }

    @AfterEach
    public void testCleanup() {
        accountRepository.deleteAll();
    }

    @AfterAll
    public static void tearDown() {
        EXECUTOR.shutdown();
    }
}
