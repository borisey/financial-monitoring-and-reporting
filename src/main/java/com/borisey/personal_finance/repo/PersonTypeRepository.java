package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.Category;
import com.borisey.personal_finance.models.PersonType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonTypeRepository extends CrudRepository<PersonType, Long> {
    Iterable<PersonType> findAll(Sort id);
    Optional<PersonType> findById(Byte id);
}
