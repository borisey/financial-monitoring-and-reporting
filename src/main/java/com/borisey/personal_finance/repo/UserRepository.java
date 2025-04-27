package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username) throws UsernameNotFoundException;
}
