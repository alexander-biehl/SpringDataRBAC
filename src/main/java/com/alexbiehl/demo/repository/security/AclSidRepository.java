package com.alexbiehl.demo.repository.security;

import com.alexbiehl.demo.model.security.AclSid;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AclSidRepository extends CrudRepository<AclSid, Long> {
}
