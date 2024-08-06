package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

        @Autowired
        private MutableAclService aclService;

        public void handleCreate(Widget w) {
            ObjectIdentityImpl oid = new ObjectIdentityImpl(w);

            MutableAcl acl = null;
            try {
                acl = (MutableAcl) this.aclService.readAclById(oid);
            } catch (NotFoundException ex) {
                acl = this.aclService.createAcl(oid);
            } finally {
                if (acl == null) {
                    throw new RuntimeException("Unable to create ACL for Widget: " + w.getId());
                }
            }

            // create required ACL entries for the Widget
            // TODO need to find a way to read the current config and apply it to Widget

        }
}
