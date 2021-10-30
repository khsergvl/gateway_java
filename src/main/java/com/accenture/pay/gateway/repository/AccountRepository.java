package com.accenture.pay.gateway.repository;

import com.accenture.pay.gateway.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {
}
