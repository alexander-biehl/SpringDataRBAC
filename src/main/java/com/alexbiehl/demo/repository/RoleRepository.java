package com.alexbiehl.demo.repository;

import com.alexbiehl.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(final String name);

    Set<Role> findByParentId(final long id);

    Set<Role> findByParent(Role parent);
}
