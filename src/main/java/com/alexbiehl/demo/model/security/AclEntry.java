package com.alexbiehl.demo.model.security;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "acl_entry",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "acl_object_identity", "ace_order" })
    })
public class AclEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "acl_object_identity", nullable = false, referencedColumnName = "id")
    private AclObjectIdentity aclObjectIdentity;

    @Column(name = "ace_order", nullable = false)
    private int aceOrder;

    @ManyToOne
    @JoinColumn(name = "sid", nullable = false, referencedColumnName = "id")
    private AclSid aclSid;

    @Column(nullable = false)
    private int mask;

    @Column(nullable = false)
    private boolean granting;

    @Column(nullable = false, name = "audit_success")
    private boolean auditSuccess;

    @Column(nullable = false, name = "audit_failure")
    private boolean auditFailure;

    public AclEntry() {}

    public AclEntry(long id, AclObjectIdentity aclObjectIdentity, int aceOrder, AclSid aclSid, int mask, boolean granting, boolean auditSuccess, boolean auditFailure) {
        this.id = id;
        this.aclObjectIdentity = aclObjectIdentity;
        this.aceOrder = aceOrder;
        this.aclSid = aclSid;
        this.mask = mask;
        this.granting = granting;
        this.auditSuccess = auditSuccess;
        this.auditFailure = auditFailure;
    }

    public AclEntry(AclObjectIdentity aclObjectIdentity, int aceOrder, AclSid aclSid, int mask, boolean granting, boolean auditSuccess, boolean auditFailure) {
        this.aclObjectIdentity = aclObjectIdentity;
        this.aceOrder = aceOrder;
        this.aclSid = aclSid;
        this.mask = mask;
        this.granting = granting;
        this.auditSuccess = auditSuccess;
        this.auditFailure = auditFailure;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return aclObjectIdentity;
    }

    public void setAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
        this.aclObjectIdentity = aclObjectIdentity;
    }

    public int getAceOrder() {
        return aceOrder;
    }

    public void setAceOrder(int aceOrder) {
        this.aceOrder = aceOrder;
    }

    public AclSid getAclSid() {
        return aclSid;
    }

    public void setAclSid(AclSid aclSid) {
        this.aclSid = aclSid;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public boolean isGranting() {
        return granting;
    }

    public void setGranting(boolean granting) {
        this.granting = granting;
    }

    public boolean isAuditSuccess() {
        return auditSuccess;
    }

    public void setAuditSuccess(boolean auditSuccess) {
        this.auditSuccess = auditSuccess;
    }

    public boolean isAuditFailure() {
        return auditFailure;
    }

    public void setAuditFailure(boolean auditFailure) {
        this.auditFailure = auditFailure;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AclEntry aclEntry)) return false;

        return getId() == aclEntry.getId() && getAceOrder() == aclEntry.getAceOrder() && getMask() == aclEntry.getMask() && isGranting() == aclEntry.isGranting() && isAuditSuccess() == aclEntry.isAuditSuccess() && isAuditFailure() == aclEntry.isAuditFailure() && Objects.equals(getAclObjectIdentity(), aclEntry.getAclObjectIdentity()) && Objects.equals(getAclSid(), aclEntry.getAclSid());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAclObjectIdentity());
        result = 31 * result + getAceOrder();
        result = 31 * result + Objects.hashCode(getAclSid());
        result = 31 * result + getMask();
        result = 31 * result + Boolean.hashCode(isGranting());
        result = 31 * result + Boolean.hashCode(isAuditSuccess());
        result = 31 * result + Boolean.hashCode(isAuditFailure());
        return result;
    }

    @Override
    public String toString() {
        return "AclEntry{" +
                "id=" + id +
                ", aclObjectIdentity=" + aclObjectIdentity +
                ", aceOrder=" + aceOrder +
                ", aclSid=" + aclSid +
                ", mask=" + mask +
                ", granting=" + granting +
                '}';
    }
}
