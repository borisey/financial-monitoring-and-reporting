package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.Bank;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BankRepository extends CrudRepository<Bank, Long> {
    Iterable<Bank> findAll(Sort colName);
    Iterable<Bank> findByUserId(Long userId, Sort colName);
    Optional<Bank> findByIdAndUserId(Long id, Long userId);
}
