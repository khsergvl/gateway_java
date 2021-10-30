package com.accenture.pay.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
public class DbConfig {

    @Bean
    public DefaultLockRegistry lockRegistry() {
        return new DefaultLockRegistry();
    }
}
