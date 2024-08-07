package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

    public Map<Role, Set<Role>> getRoleHierarchy() {
        Map<Role, Set<Role>> roles = new HashMap<>();

        for (Role role : repository.findParentRoles()) {
            Set<Role> children = repository.findByParent(role);
            roles.put(role, children);
        }

        return roles;
    }
}
