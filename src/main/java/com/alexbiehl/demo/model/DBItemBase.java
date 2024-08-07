package com.alexbiehl.demo.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;

@MappedSuperclass
public abstract class DBItemBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @CreatedBy
    @Column(name = "sys_created_by")
    protected String createdBy;

    @LastModifiedBy
    @Column(name = "sys_updated_by")
    protected String updatedBy;

    @CreatedDate
    @Column(name = "sys_created_on")
    protected Timestamp createdOn;

    @LastModifiedDate
    @Column(name = "sys_updated_on")
    protected Timestamp updatedOn;

    DBItemBase() {}

    DBItemBase(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    // for testing
    public void setId(long id) {
        this.id = id;
    }
}
