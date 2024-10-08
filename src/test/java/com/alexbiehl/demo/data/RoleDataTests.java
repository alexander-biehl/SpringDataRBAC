package com.alexbiehl.demo.data;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
public class RoleDataTests {


    private static final Logger log = LoggerFactory.getLogger(RoleDataTests.class);

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
        Role managerRole = new Role("ROLE_MANAGER", userRole);
        repo.saveAllAndFlush(Arrays.asList(userRole, managerRole));

        Role savedManagerRole = repo.findByName("ROLE_MANAGER");
        Role savedUserRole = repo.findByName("ROLE_USER");
        assertNotNull(savedManagerRole.getParent());
        assertEquals(savedUserRole, savedManagerRole.getParent());

        Set<Role> children = repo.findByParent(savedUserRole);
        assertEquals(1, children.size());
        assertEquals(savedManagerRole, children.iterator().next());

        children = repo.findByParentId(savedUserRole.getId());
        assertEquals(1, children.size());
        assertEquals(savedManagerRole, children.iterator().next());
    }

    @Test
    public void testParentRemoval() {
        Role userRole = new Role("ROLE_USER");
        Role managerRole = new Role("ROLE_MANAGER", userRole);
        repo.saveAllAndFlush(Arrays.asList(userRole, managerRole));

        Role savedManagerRole = repo.findByName("ROLE_MANAGER");
        Role savedUserRole = repo.findByName("ROLE_USER");

        savedManagerRole.setParent(null);
        repo.saveAndFlush(savedManagerRole);

        Set<Role> children = repo.findByParent(savedUserRole);
        assertEquals(0, children.size());

        children = repo.findByParentId(savedUserRole.getId());
        assertEquals(0, children.size());
    }

    @Test
    public void testGetAllParentRoles() {
        Role userRole = new Role("ROLE_USER");
        Role adminRole = new Role("ROLE_ADMIN", userRole);
        Role singleRole = new Role("ROLE_SINGLE");
        repo.saveAllAndFlush(Arrays.asList(singleRole, userRole, adminRole));

        Set<Role> parentRoles = repo.findParentRoles();
        assertNotNull(parentRoles);
        assertEquals(1, parentRoles.size());
        assertEquals(userRole.getName(), parentRoles.iterator().next().getName());
    }

    @Test
    public void testGetLeafRoles() {
        Role treeRole = new Role("ROLE_TREE");
        Role branchRole = new Role("ROLE_BRANCH", treeRole);
        Role leafRole = new Role("ROLE_LEAF", branchRole);
        repo.saveAllAndFlush(List.of(leafRole, branchRole, treeRole));

        Set<Role> leaves = repo.findLeafRoles();
        assertNotNull(leaves);
        assertEquals(1, leaves.size());
        assertEquals(leafRole.getName(), leaves.iterator().next().getName());
    }
}
