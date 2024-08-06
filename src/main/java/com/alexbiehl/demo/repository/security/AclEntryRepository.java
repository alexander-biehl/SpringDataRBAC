package com.alexbiehl.demo.repository.security;

import com.alexbiehl.demo.model.security.AclEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(exported = false)
public interface AclEntryRepository extends CrudRepository<AclEntry, Long> {
}
