package com.alexbiehl.demo.repository;

import com.alexbiehl.demo.model.Grant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrantRepository extends JpaRepository<Grant, Long> {
}
