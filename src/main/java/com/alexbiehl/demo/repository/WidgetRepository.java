package com.alexbiehl.demo.repository;

import com.alexbiehl.demo.model.Widget;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.List;


public interface WidgetRepository extends PagingAndSortingRepository<Widget, Long> {

    @PreAuthorize("hasPermission(#id, 'com.alexbiehl.demo.model.Widget', 'READ') or hasRole('ADMIN')")
    Widget findById(Long id);

    @PreAuthorize("hasPermission(#widget, 'DELETE')")
    void delete(Widget widget);

    @PreFilter("hasPermission(filterObject, 'DELETE')")
    void deleteAll(Iterable<? extends Widget> entities);

    @PostAuthorize("hasPermission(#id, 'com.alexbiehl.demo.model.Widget', 'DELETE')")
    void deleteById(Long id);

    @PreAuthorize("hasPermission(#entity, 'WRITE') or hasRole('ADMIN')")
    <S extends Widget> S save(S entity);

    @PreFilter("hasPermission(filterObject, 'WRITE') or hasRole('ADMIN')")
    <S extends Widget> Iterable<S> saveAll(Iterable<S> entities);

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Widget> findAll();
}
