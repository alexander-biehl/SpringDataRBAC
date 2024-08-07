package com.alexbiehl.demo.data;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
public class RoleDataTests {

    @Autowired
    private RoleRepository repo;

    @Test
    public void testUserRole() {
        Role userRole = new Role("ROLE_USER");
        userRole = repo.saveAndFlush(userRole);

        Role savedRole = repo.findByName("ROLE_USER");
        assertNotNull(savedRole);
        assertEquals(userRole.getName(), savedRole.getName());
        assertNull(savedRole.getParent());
        assertNotNull(savedRole.getId());
    }

    @Test
    public void testRoleParent() {
        Role userRole = new Role("ROLE_USER");
        userRole = repo.saveAndFlush(userRole);
        Role managerRole = new Role("ROLE_MANAGER", userRole);
        managerRole = repo.saveAndFlush(managerRole);

        Role savedRole = repo.findByName("ROLE_MANAGER");
        assertNotNull(savedRole);
        assertEquals(managerRole.getName(), savedRole.getName());
        assertNotNull(savedRole.getParent());
        assertEquals(savedRole.getParent(), userRole);

        Role savedUserRole = repo.findByName("ROLE_USER");
        Set<Role> children = savedUserRole.getChildren();
        assertNotNull(children);
        assertEquals(1, children.size());
        assertEquals(managerRole, children.iterator().next());
    }
}
