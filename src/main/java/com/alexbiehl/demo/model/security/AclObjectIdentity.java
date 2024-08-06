package com.alexbiehl.demo.model.security;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "acl_object_identity",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"object_id_class", "object_id_identity"})
        })
public class AclObjectIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(targetEntity = AclClass.class)
    @JoinColumn(name = "object_id_class", nullable = false, referencedColumnName = "id")
    private AclClass objectIdClass;

    @Column(name = "object_id_identity", nullable = false)
    private String objectIdIdentity;

    @ManyToOne
    @JoinColumn(name = "parent_object", referencedColumnName = "id", nullable = true)
    private AclObjectIdentity parentObject;

    @ManyToOne
    @JoinColumn(name = "owner_sid")
    private AclSid ownerSid;

    @Column(name = "entries_inheriting", nullable = false)
    private boolean entriesInheriting;

    public AclObjectIdentity() {
    }

    public AclObjectIdentity(long id, AclClass objectIdClass, String objectIdIdentity, AclObjectIdentity parentObject, AclSid ownerSid, boolean entriesInheriting) {
        this.id = id;
        this.objectIdClass = objectIdClass;
        this.objectIdIdentity = objectIdIdentity;
        this.parentObject = parentObject;
        this.ownerSid = ownerSid;
        this.entriesInheriting = entriesInheriting;
    }

    public AclObjectIdentity(AclClass objectIdClass, String objectIdIdentity, AclObjectIdentity parentObject, AclSid ownerSid, boolean entriesInheriting) {
        this.objectIdClass = objectIdClass;
        this.objectIdIdentity = objectIdIdentity;
        this.parentObject = parentObject;
        this.ownerSid = ownerSid;
        this.entriesInheriting = entriesInheriting;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AclClass getObjectIdClass() {
        return objectIdClass;
    }

    public void setObjectIdClass(AclClass objectIdClass) {
        this.objectIdClass = objectIdClass;
    }

    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    public AclObjectIdentity getParentObject() {
        return parentObject;
    }

    public void setParentObject(AclObjectIdentity parentObject) {
        this.parentObject = parentObject;
    }

    public AclSid getOwnerSid() {
        return ownerSid;
    }

    public void setOwnerSid(AclSid ownerSid) {
        this.ownerSid = ownerSid;
    }

    public boolean isEntriesInheriting() {
        return entriesInheriting;
    }

    public void setEntriesInheriting(boolean entriesInheriting) {
        this.entriesInheriting = entriesInheriting;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AclObjectIdentity that)) return false;

        return getId() == that.getId() && isEntriesInheriting() == that.isEntriesInheriting() && getObjectIdClass().equals(that.getObjectIdClass()) && getObjectIdIdentity().equals(that.getObjectIdIdentity()) && Objects.equals(getParentObject(), that.getParentObject()) && Objects.equals(getOwnerSid(), that.getOwnerSid());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + getObjectIdClass().hashCode();
        result = 31 * result + getObjectIdIdentity().hashCode();
        result = 31 * result + Objects.hashCode(getParentObject());
        result = 31 * result + Objects.hashCode(getOwnerSid());
        result = 31 * result + Boolean.hashCode(isEntriesInheriting());
        return result;
    }
}
