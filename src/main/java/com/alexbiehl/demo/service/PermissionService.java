package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.DBItemBase;
import com.alexbiehl.demo.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    private MutableAclService aclService;

    public void handleCreate(Object o, Class<?> clazz) {
        ObjectIdentityImpl oid = new ObjectIdentityImpl(clazz, ((DBItemBase)o).getId());

        MutableAcl acl = null;
        try {
            acl = (MutableAcl) this.aclService.readAclById(oid);
        } catch (NotFoundException ex) {
            acl = this.aclService.createAcl(oid);
        } finally {
            if (acl == null) {
                throw new RuntimeException("Unable to create ACL for Widget: " + ((DBItemBase) o).getId());
            }
        }

        // create required ACL entries for the Widget
        // TODO need to find a way to read the current config and apply it to Widget

    }

    public void handleCreate(Widget w) {
        handleCreate(w, w.getClass());
    }
}
