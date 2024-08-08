package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

    public Map<Role, Set<Role>> getRoleHierarchy() {
        Map<Role, Set<Role>> roles = new HashMap<>();

        for (Role role : getParentRoles()) {
            Set<Role> children = repository.findByParent(role);
            roles.put(role, children);
        }

        return roles;
    }

    public Set<Role> getParentRoles() {
        return repository.findParentRoles();
    }

    public Map<Role, List<Role>> getSortedParentHierarchy() {
        // get all leaf nodes, for each node, recursively walk up the chain
        // and DepthFirst then add them to the list
        Map<Role, List<Role>> hierarchy = new HashMap<>();
        Set<Role> leafRoles = repository.findLeafRoles();
        leafRoles.forEach(role -> {
            List<Role> roleHierarchy = new ArrayList<>();
            roleHierarchy = getHierarchy(roleHierarchy, role);
            hierarchy.put(roleHierarchy.getFirst(), roleHierarchy);
        });
        return hierarchy;
    }

    private List<Role> getHierarchy(List<Role> roleList, Role role) {
        roleList = getHierarchy(roleList, repository.getReferenceById(role.getParent().getId()));
        roleList.add(role);
        return roleList;
    }
}
