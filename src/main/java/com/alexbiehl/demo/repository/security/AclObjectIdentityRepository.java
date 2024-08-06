package com.alexbiehl.demo.repository.security;

import com.alexbiehl.demo.model.security.AclObjectIdentity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AclObjectIdentityRepository extends CrudRepository<AclObjectIdentity, Long> {
}
