package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.DBItemBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AclService {

    @Autowired
    private MutableAclService aclService;


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

    public AuditableAcl grantPermissionsToSid(String type, Serializable id, String sidName,
                                              Permission[] permissions) {
        return grantPermissions(type, id, new GrantedAuthoritySid(sidName), permissions);
    }

    private AuditableAcl grantPermissions(String type, Serializable id, Sid sid, Permission[] permissions) {
        AuditableAcl acl = getOrCreateAcl(type, id);
        Set<Integer> indices = new HashSet<>();

        for (Permission permission : permissions) {
            int index = acl.getEntries().size();
            boolean granting = true;
            acl.insertAce(index, permission, sid, granting);
            indices.add(index);
        }

        for (Integer index : indices) {
            acl.updateAuditing(index, true, true);
        }

        return (AuditableAcl) aclService.updateAcl(acl);
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
