package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.Account;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Iterable<Account> findAll(Sort colName);
    Iterable<Account> findByUserId(Long userId, Sort colName);
    Optional<Account> findByIdAndUserId(Long id, Long userId);
    @Query(value = "SELECT sum(amount) FROM Account a WHERE a.userId = ?1")
    Iterable<Account> findSumByUserId(Long userId);
}
