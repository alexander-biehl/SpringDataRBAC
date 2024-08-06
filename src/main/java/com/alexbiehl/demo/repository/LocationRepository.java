package com.alexbiehl.demo.repository;

import com.alexbiehl.demo.model.Location;
import com.alexbiehl.demo.model.Widget;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends PagingAndSortingRepository<Location, Long> {

    @PostAuthorize("hasPermission(returnObject, 'READ') or hasRole('ADMIN')")
    Location findById(Long id);

    @PreAuthorize("hasPermission(#entity, 'DELETE')")
    <S extends Location> void delete(S entity);

    @PreFilter("hasPermission(filterObject, 'DELETE')")
    void deleteAll(Iterable<? extends Location> entities);

    @PostAuthorize("hasPermission(#id, 'com.alexbiehl.demo.model.Location', 'DELETE')")
    void deleteById(Long id);

    @PreAuthorize("hasPermission(#entity, 'WRITE') or hasRole('ADMIN')")
    <S extends Location> S save(S entity);

    @PreFilter("hasPermission(filterObject, 'WRITE') or hasRole('ADMIN')")
    <S extends Location> Iterable <S> saveAll(Iterable<S> entities);

    @PostFilter("hasPermission(filterObject, 'READ')")
    <S extends Location> List<S> findAll();
}
