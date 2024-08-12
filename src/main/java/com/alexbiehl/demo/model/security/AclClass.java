package com.alexbiehl.demo.model.security;

import jakarta.persistence.*;

@Entity
@Table(name = "acl_class")
public class AclClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "class", unique = true)
    private String clazz;

    public AclClass() {}

    public AclClass(long id, String clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public AclClass(String clazz) {
        this.clazz = clazz;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AclClass aclClass)) return false;

        return getId() == aclClass.getId() && getClazz().equals(aclClass.getClazz());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + getClazz().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AclClass{" +
                "id=" + id +
                ", clazz='" + clazz + '\'' +
                '}';
    }
}
