package com.alexbiehl.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "grants")
public class Grant extends DBItemBase {

    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    private boolean create;
    private boolean read;
    private boolean write;
    private boolean delete;
    private boolean admin;

    public Grant() {
        super();
    }

    public Grant(boolean create, boolean read, boolean write, boolean delete, boolean admin) {
        this.create = create;
        this.read = read;
        this.write = write;
        this.delete = delete;
        this.admin = admin;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Boolean[] getGrantList() {
        return new Boolean[]{
                isCreate(),
                isRead(),
                isWrite(),
                isDelete(),
                isAdmin()
        };
    }

    public void updateGrants(Boolean[] grants) {
        setCreate(grants[0]);
        setRead(grants[1]);
        setWrite(grants[2]);
        setDelete(grants[3]);
        setAdmin(grants[4]);
    }
}
