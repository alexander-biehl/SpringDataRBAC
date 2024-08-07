package com.alexbiehl.demo.repository;

import com.alexbiehl.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(final String name);
}
