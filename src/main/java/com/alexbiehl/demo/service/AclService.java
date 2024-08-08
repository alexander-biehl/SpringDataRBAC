package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.DBItemBase;
import com.alexbiehl.demo.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private MutableAclService aclService;

    public <T extends DBItemBase> void handleObjectCreation(T element) {

    }

    private <T extends DBItemBase> void addPermission(T element, Sid recipient, Permission permission, boolean granting) {
        MutableAcl acl;
        ObjectIdentity oid = new ObjectIdentityImpl(element);

        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException ex) {
            acl = aclService.createAcl(oid);
        }

        acl.insertAce(acl.getEntries().size(), permission, recipient, granting);
        aclService.updateAcl(acl);
    }

    private <T extends DBItemBase> void handleDelete(T element) {
        ObjectIdentity oid = new ObjectIdentityImpl(element);
        aclService.deleteAcl(oid, false);
    }

    private <T extends DBItemBase> void deletePermission(T element, Sid recipient, Permission permission) {
        ObjectIdentity oid = new ObjectIdentityImpl(element);
        MutableAcl acl = (MutableAcl) aclService.readAclById(oid);

        // remove all permissions for this recipient
        List<AccessControlEntry> entries = acl.getEntries();

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getSid().equals(recipient) &&
            entries.get(i).getPermission().equals(permission)) {
                acl.deleteAce(i);
            }
        }

        aclService.updateAcl(acl);
    }
}
