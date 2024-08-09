package com.alexbiehl.demo.integration;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.repository.RoleRepository;
import com.alexbiehl.demo.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableAutoConfiguration
@Transactional
public class RoleServiceIntegrationTests {


    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    @Test
    public void testGetRoleHierarchy() {
        Role parent1Role = new Role("ROLE_PARENT1");
        Role parent2Role = new Role("ROLE_PARENT2");
        Role parent3Role = new Role("ROLE_PARENT3");

        Role child1 = new Role("ROLE_CHILD1", parent1Role);
        Role child2 = new Role("ROLE_CHILD2", parent2Role);
        Role child1_2 = new Role("ROLE_CHILD1_2", child1);

        roleRepository.saveAllAndFlush(List.of(parent1Role, parent2Role, child1, child2, child1_2, parent3Role));

        Map<Role, List<Role>> sortedHierarchy = roleService.getSortedParentHierarchy();
        Set<Role> entries = sortedHierarchy.keySet();

        assertTrue(entries.contains(parent1Role));
        assertTrue(entries.contains(parent2Role));
        assertFalse(entries.contains(parent3Role));

        List<Role> childSet1 = sortedHierarchy.get(parent1Role);
        assertEquals(parent1Role, childSet1.getFirst());
        assertEquals(child1, childSet1.get(1));
        assertEquals(child1_2, childSet1.getLast());

        List<Role> childSet2 = sortedHierarchy.get(parent2Role);
        assertEquals(parent2Role, childSet2.getFirst());
        assertEquals(child2, childSet2.getLast());
    }
}
