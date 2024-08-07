package com.alexbiehl.demo.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "roles")
public class Role extends DBItemBase {

    @Column(nullable = false)
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
