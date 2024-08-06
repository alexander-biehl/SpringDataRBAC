package com.alexbiehl.demo.repository.security;

import com.alexbiehl.demo.model.security.AclClass;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AclClassRepository extends CrudRepository<AclClass, Long> {
}
