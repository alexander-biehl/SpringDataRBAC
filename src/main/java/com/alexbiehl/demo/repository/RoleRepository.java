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
            value = """
                    SELECT * FROM roles r0 WHERE r0.`id` IN (
                        SELECT `parent_id` from roles r1 WHERE r1.`parent_id` IS NOT NULL)
                    """,
            nativeQuery = true)
    Set<Role> findParentRoles();

    @Query(
            value = """
                    SELECT * FROM roles r0 WHERE r0.`parent_id` IS NOT NULL AND r0.`id` NOT IN (
                        SELECT DISTINCT `parent_id` FROM roles r1 WHERE r1.`parent_id` IS NOT NULL)
                    """,
            nativeQuery = true
    )
    Set<Role> findLeafRoles();
}
