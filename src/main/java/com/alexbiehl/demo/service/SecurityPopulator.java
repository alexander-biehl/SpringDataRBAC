package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.DBItemBase;
import com.alexbiehl.demo.model.Grant;
import com.alexbiehl.demo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SecurityPopulator {

    @Autowired
    private AclService aclService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private GrantService grantService;


    public <T extends DBItemBase> void grantDefaultAccess(T element) {
        Map<Role, List<Role>> sortedHierarchy = roleService.getSortedParentHierarchy();

        sortedHierarchy.forEach((role, roleList) -> {
            roleList.forEach(this::getAndSaveGrants);
        });
    }

    @Transactional
    public <T extends DBItemBase> void grantAccess(Role role, T element, Permission[] permissions, Boolean[] grants) {
        Grant existingGrant = grantService.getOrCreateGrantForRole(role);
        existingGrant.updateGrants(grants);

        grantService.createOrUpdateGrant(existingGrant);
        aclService.grantPermissionsToSid(
                element.getClass().toString(),
                element.getId(),
                role.getName(),
                permissions,
                grants
        );
    }

    @Transactional
    public <T extends DBItemBase> void grantAccess(UserDetails userDetails, T element, Permission[] permissions,
                                                   Boolean[] grants) {
        Grant adminGrant = grantService.adminAccess();
        aclService.grantPermissionsToPrincipal(
                element.getClass().toString(),
                element.getId(),
                userDetails.getUsername(),
                permissions,
                adminGrant.getGrantList()
        );
    }

    @Transactional
    private void getAndSaveGrants(Role role) {
        Grant grant = grantService.getOrCreateGrantForRole(role);
        aclService.grantPermissionsToSid(
                role.getClass().toString(),
                role.getId(),
                role.getName(),
                aclService.getPermissions(),
                grant.getGrantList()
        );
    }
}
