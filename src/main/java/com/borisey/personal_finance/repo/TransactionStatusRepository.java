package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.TransactionStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransactionStatusRepository extends CrudRepository<TransactionStatus, Long> {
    Iterable<TransactionStatus> findAll(Sort sort);
    Optional<TransactionStatus> findByTitle(String title);
}
