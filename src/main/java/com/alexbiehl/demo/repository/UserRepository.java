package com.alexbiehl.demo.repository;

import com.alexbiehl.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(final String username);
}
