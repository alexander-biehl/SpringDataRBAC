package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.DBItemBase;
import com.alexbiehl.demo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityPopulator {

    @Autowired
    private AclService aclService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private GrantService grantService;

    public <T extends DBItemBase> void grantDefaultAccess(T element) {
        Set<Role> roles = roleService.getParentRoles();

    }

    public static List<Role> addRole(List<Role> roleSet, Role role) {
        if (!roleSet.contains(role)) {
            roleSet.add(role);
        }
        return roleSet;
    }
}
