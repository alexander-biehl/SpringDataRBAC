package com.alexbiehl.demo.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role extends DBItemBase {

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Role parent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy = "parent")
    private Set<Role> children;

    private Role() {
        super();
    }

    public Role(long id, String name) {
        super(id);
        this.id = id;
        this.name = name;
    }

    public Role(String name) {
        super();
        this.name = name;
    }

    public Role(String name, Role parent) {
        this.name = name;
        setParent(parent);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getParent() {
        return parent;
    }

    public void setParent(Role parent) {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public Set<Role> getChildren() {
        return children;
    }

    public void removeChild(Role child) {
        if (children != null) {
            children.remove(child);
        }
    }

    public void addChild(Role child) {
        if (this.children == null) {
            this.children = new HashSet<>();
        }
        this.children.add(child);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;

        return getId() == role.getId() && getName().equals(role.getName());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + getName().hashCode();
        return result;
    }

    public static GrantedAuthority convert(Role role) {
        return new SimpleGrantedAuthority(role.getName());
    }
}
