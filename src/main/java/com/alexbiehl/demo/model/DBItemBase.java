package com.alexbiehl.demo.model;

import jakarta.persistence.*;

import java.util.Date;

@MappedSuperclass
public abstract class DBItemBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "sys_created_by")
    private String createdBy;
    @Column(name = "sys_updated_by")
    private String updatedBy;
    @Column(name = "sys_created_on")
    private Date createdOn;
    @Column(name = "sys_updated_on")
    private Date updatedOn;


}
