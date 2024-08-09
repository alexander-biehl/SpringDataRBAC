package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.Grant;
import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.repository.GrantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantService {

    @Autowired
    private GrantRepository repo;

    public Grant getOrCreateGrantForRole(Role role) {
        Grant existingGrant = repo.findByRole(role);
        if (existingGrant != null) {
            return existingGrant;
        }
        Grant newGrant = readAccess();
        newGrant.setRole(role);
        return createOrUpdateGrant(newGrant);
    }

    public Grant createOrUpdateGrant(Grant grant) {
        return repo.save(grant);
    }

    public void deleteGrant(Grant grant) {
        repo.delete(grant);
    }

    public Grant allAccess() {
        return new Grant(true, true, true, true, true);
    }

    public Grant readAccess() {
        return new Grant(false, true, false, false, false);
    }

    public Grant writeAccess() {
        return new Grant(false, true, true, false, false);
    }

    public Grant adminAccess() {
        return new Grant(false, false, false, false, true);
    }
}
