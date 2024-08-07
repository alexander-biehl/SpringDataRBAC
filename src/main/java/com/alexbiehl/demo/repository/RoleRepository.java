package com.alexbiehl.demo.repository;

import com.alexbiehl.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(final String name);

    Set<Role> findByParentId(final long id);

    Set<Role> findByParent(Role parent);

    @Query(
            value = "SELECT * from roles r0 where r0.`id` IN (SELECT parent_id from roles r1 WHERE r1.parent_id IS NOT NULL)",
            nativeQuery = true)
    Set<Role> findParentRoles();
}
