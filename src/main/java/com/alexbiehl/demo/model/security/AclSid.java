package com.alexbiehl.demo.model.security;

import jakarta.persistence.*;

@Entity
@Table(name = "acl_sid",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"principal", "sid"})
    })
public class AclSid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean principal;

    @Column(nullable = false)
    private String sid;

    public AclSid() {}

    public AclSid(Long id, boolean principal, String sid) {
        this.id = id;
        this.principal = principal;
        this.sid = sid;
    }

    public AclSid(boolean principal, String sid) {
        this.principal = principal;
        this.sid = sid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AclSid aclSid)) return false;

        return getId() == aclSid.getId() && isPrincipal() == aclSid.isPrincipal() && getSid().equals(aclSid.getSid());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + Boolean.hashCode(isPrincipal());
        result = 31 * result + getSid().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AclSid{" +
                "id=" + id +
                ", principal=" + principal +
                ", sid='" + sid + '\'' +
                '}';
    }
}
