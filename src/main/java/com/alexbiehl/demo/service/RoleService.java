package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

    /**
     * Returns a map of parent roles to sets of their children
     * @return - map of parent roles and children roles
     */
    public Map<Role, Set<Role>> getRoleHierarchy() {
        Map<Role, Set<Role>> roles = new HashMap<>();

        for (Role role : getParentRoles()) {
            Set<Role> children = repository.findByParent(role);
            roles.put(role, children);
        }

        return roles;
    }

    /**
     * Get all the Roles that are parents but not children.
     * @return - set of top level parent roles
     */
    public Set<Role> getParentRoles() {
        return repository.findParentRoles();
    }

    /**
     * Returns a Map of parent roles to a list of their children in parent -> child order.
     */
    @Transactional
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

    /**
     * Recursive method to reverse child -> parent chain to be parent -> child
     * @param roleList - the list of child - > parent roles
     * @param role - the current role that is being inspected
     * @return - list of roles in parent -> child order
     */
    private List<Role> getHierarchy(List<Role> roleList, Role role) {
        if (role == null) {
            return roleList;
        }
        roleList = getHierarchy(
                roleList,
                role.getParent() != null ? repository.getReferenceById(role.getParent().getId()) : null
        );
        roleList.add(role);
        return roleList;
    }
}
