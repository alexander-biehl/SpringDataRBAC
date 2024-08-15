package com.alexbiehl.demo.security;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AclContext {


    @Autowired
    private DataSource dataSource;
    @Autowired
    private RoleService roleService;

    @Bean
    public RoleHierarchy roleHierarchy() {
//        return RoleHierarchyImpl.withDefaultRolePrefix()
//                .role("ADMIN").implies("MANAGER")
//                .role("MANAGER").implies("USER")
//                .build();
        RoleHierarchyImpl.Builder builder = RoleHierarchyImpl.withDefaultRolePrefix();
        Map<Role, Set<Role>> roleMap = roleService.getRoleHierarchy();
        roleMap.forEach((key, value) -> builder
                .role(key.getName())
                .implies(value
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.joining())));
        return builder.build();
    }

    // ACL-related beans

    @Bean
    public JdbcMutableAclService mutableAclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }

    @Bean
    public SpringCacheBasedAclCache aclCache() {
        final ConcurrentMapCache aclCache = new ConcurrentMapCache("acl_cache");
        return new SpringCacheBasedAclCache(aclCache, permissionStrategy(), aclAuthStrategy());
    }

    @Bean
    public PermissionGrantingStrategy permissionStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclAuthorizationStrategy aclAuthStrategy() {
        // This means that any user with the ROLE_ADMIN role will be able to make changes
        // to ACLs
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(mutableAclService());
        expressionHandler.setRoleHierarchy(roleHierarchy());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(mutableAclService()));
        return expressionHandler;
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthStrategy(), new ConsoleAuditLogger());
    }
}
