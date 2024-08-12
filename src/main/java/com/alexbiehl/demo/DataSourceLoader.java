package com.alexbiehl.demo;

import com.alexbiehl.demo.model.*;
import com.alexbiehl.demo.repository.*;
import com.alexbiehl.demo.repository.security.AclClassRepository;
import com.alexbiehl.demo.repository.security.AclEntryRepository;
import com.alexbiehl.demo.repository.security.AclObjectIdentityRepository;
import com.alexbiehl.demo.repository.security.AclSidRepository;
import com.alexbiehl.demo.service.GrantService;
import com.alexbiehl.demo.service.SecurityMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component
@Transactional
// excludes this class from tests annotated with 'test-no-security-load' profile
@Profile("!test-no-security-load")
public class DataSourceLoader implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceLoader.class);

    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GrantService grantService;

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private WidgetRepository widgetRepository;

    @Autowired
    private JdbcMutableAclService mutableAclService;

    @Autowired
    private AclClassRepository aclClassRepository;
    @Autowired
    private AclEntryRepository aclEntryRepository;
    @Autowired
    private AclObjectIdentityRepository aclObjectIdentityRepository;
    @Autowired
    private AclSidRepository aclSidRepository;

    @Autowired
    private SecurityMetaService metaService;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Starting DB load");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create roles and users

        User user = new User("user", passwordEncoder.encode("password"), true);
        User manager = new User("manager", passwordEncoder.encode("password"), true);
        User admin = new User("admin", passwordEncoder.encode("password"), true);

        Role userRole = new Role("ROLE_USER");
        userRole = roleRepository.save(userRole);

        Role managerRole = new Role("ROLE_MANAGER", userRole);
        managerRole = roleRepository.save(managerRole);

        Role adminRole = new Role("ROLE_ADMIN", managerRole);
        adminRole = roleRepository.save(adminRole);

        // Permission Grants
        Grant userGrant = grantService.readAccess();
        userGrant.setRole(userRole);
        Grant managerGrant = grantService.writeAccess();
        managerGrant.setRole(managerRole);
        Grant adminGrant = grantService.allAccess();
        adminGrant.setRole(adminRole);

        grantService.createOrUpdateGrant(userGrant);
        grantService.createOrUpdateGrant(managerGrant);
        grantService.createOrUpdateGrant(adminGrant);

        user.setRoles(Collections.singleton(userRole));
        manager.setRoles(Collections.singleton(managerRole));
        admin.setRoles(Collections.singleton(adminRole));

        user = userRepository.save(user);
        manager = userRepository.save(manager);
        admin = userRepository.save(admin);

        Widget widget = new Widget(-1L, "testWidget", "desc");

        Location loc = new Location(1L, "Home", "the pad", "100 Some st.", "Anytown", "NY", "00000", "USA");
        widget.setAvailableLocations(Collections.singleton(loc));

        loc = locationRepository.save(loc);
        widget = widgetRepository.save(widget);

        metaService.grantDefaultAccess(widget);
        metaService.grantDefaultAccess(loc);

        LOGGER.info("DB Load Complete");
        SecurityContextHolder.clearContext();
    }
}
