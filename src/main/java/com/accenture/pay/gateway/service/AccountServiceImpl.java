package com.accenture.pay.gateway.service;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.exception.NotFoundException;
import com.accenture.pay.gateway.exception.UnprocessableStateException;
import com.accenture.pay.gateway.model.Status;
import com.accenture.pay.gateway.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final LockRegistry lockRegistry;

    public AccountServiceImpl(AccountRepository accountRepository, LockRegistry lockRegistry) {
        this.accountRepository = accountRepository;
        this.lockRegistry = lockRegistry;
    }

    @Override
    public Account getAccount(Integer id) {
        return accountRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Account with id = %s not found", id)));
    }

    /**
     * From author - for sure this locks, repository and declarative transactions
     * here is a point for long discussion, as well as design in
     * compare to regular debit / credit / transactions logs
     *
     * @param from account id to transfer from
     * @param to account id to transfer to
     * @param amount amount to transfer
     * @param runnable is used for test purposes
     * @return status of fund transfer
     */
    @Transactional
    @Override
    public Status transfer(Integer from, Integer to, Double amount, Runnable runnable) {
        if (runnable !=null) runnable.run();

        var accountTransferFrom = this.getAccount(from);
        var accountTransferTo = this.getAccount(to);

        var lockFromAccount = lockRegistry.obtain(accountTransferFrom.getId());
        lockFromAccount.lock();
        var lockToAccount = lockRegistry.obtain(accountTransferTo.getId());
        lockToAccount.lock();
        try {
            var transferFromBalance = accountTransferFrom.getBalance();
            var transferToBalance = accountTransferTo.getBalance();

            if (transferFromBalance < amount) return Status.FAILED;

            accountTransferFrom.setBalance(transferFromBalance - amount);
            accountTransferTo.setBalance(transferToBalance + amount);

            accountRepository.save(accountTransferFrom);
            accountRepository.save(accountTransferTo);
            return Status.SUCCESS;
        } catch (Exception ex) {
            LOGGER.warn("Unable to make a fund transfer due exception", ex);
            throw new UnprocessableStateException("Unable to make a fund transfer");
        } finally{
            lockFromAccount.unlock();
            lockToAccount.unlock();
        }
    }
}
