package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.Type;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TypeRepository extends CrudRepository<Type, Long> {
    Optional<Type> findById(Byte id);
}
