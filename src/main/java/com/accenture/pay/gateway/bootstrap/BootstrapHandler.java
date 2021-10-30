package com.accenture.pay.gateway.bootstrap;

import com.accenture.pay.gateway.entity.Account;
import com.accenture.pay.gateway.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class designed to pre-initialize application
 * with test data to simplify manual testing
 */
@Component
public class BootstrapHandler implements ApplicationListener<ApplicationStartedEvent> {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        accountRepository.saveAll(List.of(new Account(1, 0.15), new Account(2, 0.20)));
    }
}
