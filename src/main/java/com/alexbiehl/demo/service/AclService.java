package com.alexbiehl.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AclService {

    @Autowired
    private MutableAclService aclService;


    /**
     * Return list of permissions in order that we like
     * @return - list of permissions in order
     */
    public Permission[] getPermissions() {
        return new Permission[]{
                BasePermission.CREATE,
                BasePermission.READ,
                BasePermission.WRITE,
                BasePermission.DELETE,
                BasePermission.ADMINISTRATION
        };
    }

    private AuditableAcl getOrCreateAcl(String type, Serializable id) {
        AuditableAcl acl = getAcl(type, id);
        if (acl == null) {
            acl = createAcl(type, id);
        }
        return acl;
    }

    private AuditableAcl createAcl(String type, Serializable id) {
        return (AuditableAcl) aclService.createAcl(new ObjectIdentityImpl(type, id));
    }

    private AuditableAcl getAcl(String type, Serializable id) {
        try {
            return (AuditableAcl) aclService.readAclById(new ObjectIdentityImpl(type, id));
        } catch (NotFoundException ex) {
            return null;
        }
    }

    public void grantPermissionsToPrincipal(String type, Serializable id, String name,
                                            Permission[] permissions, Boolean[] grants) {
        grantPermissions(type, id, new PrincipalSid(name), permissions, grants);
    }

    public void grantPermissionsToSid(String type, Serializable id, String sidName,
                                      Permission[] permissions, Boolean[] grants) {
        grantPermissions(type, id, new GrantedAuthoritySid(sidName), permissions, grants);
    }

    private AuditableAcl grantPermissions(String type, Serializable id, Sid sid, Permission[] permissions, Boolean[] grants) {
        AuditableAcl acl = getOrCreateAcl(type, id);
        Set<Integer> indices = new HashSet<>();

        int grantIndex = 0;
        for (Permission permission : permissions) {
            int index = acl.getEntries().size();
            boolean granting = grants[grantIndex];
            acl.insertAce(index, permission, sid, granting);
            indices.add(index);
            ++grantIndex;
        }

        for (Integer index : indices) {
            acl.updateAuditing(index, true, true);
        }

        return (AuditableAcl) aclService.updateAcl(acl);
    }

    public void deleteAcl(String type, Serializable id) {
        aclService.deleteAcl(new ObjectIdentityImpl(type, id), true);
    }

    private AuditableAcl removePermissions(String type, Serializable id, Sid sid, Permission[] permissions) {
        AuditableAcl acl = getAcl(type, id);
        int index = 0;

        for (AccessControlEntry entry : acl.getEntries()) {
            boolean deletedEntry = false;
            for (Permission permission : permissions) {
                if (entry.isGranting() &&
                        entry.getSid().equals(sid) &&
                        entry.getPermission().equals(permission)) {
                    acl.deleteAce(index);
                    deletedEntry = true;
                }
            }

            index = deletedEntry ? index : index + 1;
        }
        return (AuditableAcl) aclService.updateAcl(acl);
    }
}
