package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.TransactionStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface TransactionStatusRepository extends CrudRepository<TransactionStatus, Long> {
    Iterable<TransactionStatus> findAll(Sort sort);
}
