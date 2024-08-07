package com.alexbiehl.demo;

import com.alexbiehl.demo.model.Location;
import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.model.User;
import com.alexbiehl.demo.model.Widget;
import com.alexbiehl.demo.model.security.AclClass;
import com.alexbiehl.demo.model.security.AclEntry;
import com.alexbiehl.demo.model.security.AclObjectIdentity;
import com.alexbiehl.demo.model.security.AclSid;
import com.alexbiehl.demo.repository.LocationRepository;
import com.alexbiehl.demo.repository.RoleRepository;
import com.alexbiehl.demo.repository.UserRepository;
import com.alexbiehl.demo.repository.WidgetRepository;
import com.alexbiehl.demo.repository.security.AclClassRepository;
import com.alexbiehl.demo.repository.security.AclEntryRepository;
import com.alexbiehl.demo.repository.security.AclObjectIdentityRepository;
import com.alexbiehl.demo.repository.security.AclSidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Component
public class DataSourceLoader implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceLoader.class);

    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        User admin = new User("admin", passwordEncoder.encode("password"), true);
        Role userRole = new Role("ROLE_USER");
        Role managerRole = new Role("ROLE_MANAGER", userRole);
        Role adminRole = new Role("ROLE_ADMIN", managerRole);

        userRole = roleRepository.save(userRole);
        managerRole = roleRepository.save(managerRole);
        adminRole = roleRepository.save(adminRole);

        user.setRoles(Collections.singleton(userRole));
        // admin.setRoles(new HashSet<>(Arrays.asList(userRole, adminRole)));
        admin.setRoles(Collections.singleton(adminRole));

        user = userRepository.save(user);
        admin = userRepository.save(admin);

        // create Security IDentifiers (ACL SID)
        AclSid userRoleSid = new AclSid(false, userRole.getName());
        AclSid adminRoleSid = new AclSid(false, adminRole.getName());

        userRoleSid = aclSidRepository.save(userRoleSid);
        adminRoleSid = aclSidRepository.save(adminRoleSid);

        AclClass widgetClass = new AclClass("com.alexbiehl.demo.model.Widget");
        AclClass locationClass = new AclClass("com.alexbiehl.demo.model.Location");

        widgetClass = aclClassRepository.save(widgetClass);
        locationClass = aclClassRepository.save(locationClass);

        Widget widget = new Widget(-1L, "testWidget", "desc");
        // Widget widget2 = new Widget(2L, "widget2", "desc2");

        Location loc = new Location(1L, "Home", "the pad", "100 Some st.", "Anytown", "NY", "00000", "USA");
        widget.setAvailableLocations(Collections.singleton(loc));
        // widget2.setAvailableLocations(Collections.singleton(loc));

        loc = locationRepository.save(loc);
        widget = widgetRepository.save(widget);
        // widget2 = widgetRepository.save(widget2);

        // Create ACL Object Identities
        AclObjectIdentity widgetAdminIdentity = new AclObjectIdentity(
                widgetClass,
                String.valueOf(widget.getId()),
                null,
                adminRoleSid,
                false);
        AclObjectIdentity locationAdminIdentity = new AclObjectIdentity(
                locationClass,
                String.valueOf(loc.getId()),
                null,
                adminRoleSid,
                false
        );
//        AclObjectIdentity widgetUserIdentity = new AclObjectIdentity(
//                widgetClass,
//                String.valueOf(widget.getId()),
//                null,
//                userRoleSid,
//                false
//        );
//        AclObjectIdentity locationUserIdentity = new AclObjectIdentity(
//                locationClass,
//                String.valueOf(loc.getId()),
//                null,
//                userRoleSid,
//                false
//        );

        widgetAdminIdentity = aclObjectIdentityRepository.save(widgetAdminIdentity);
        locationAdminIdentity = aclObjectIdentityRepository.save(locationAdminIdentity);
//        widgetUserIdentity = aclObjectIdentityRepository.save(widgetUserIdentity);
//        locationUserIdentity = aclObjectIdentityRepository.save(locationUserIdentity);

        // Create the ACL entries for each domain object, Sid, and permission
        // user role has read access for now, admin will have all access
        int widgetOrder = 0;
        int locOrder = 0;
        // admin acl entries

        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        adminRoleSid,
                        BasePermission.READ.getMask()
                )
        );
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        adminRoleSid,
                        BasePermission.CREATE.getMask()
                )
        );
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        adminRoleSid,
                        BasePermission.WRITE.getMask()
                )
        );
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        adminRoleSid,
                        BasePermission.DELETE.getMask()
                )
        );
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        adminRoleSid,
                        BasePermission.ADMINISTRATION.getMask()
                )
        );

        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        adminRoleSid,
                        BasePermission.READ.getMask()
                )
        );
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        adminRoleSid,
                        BasePermission.CREATE.getMask()
                )
        );
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        adminRoleSid,
                        BasePermission.WRITE.getMask()
                )
        );
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        adminRoleSid,
                        BasePermission.DELETE.getMask()
                )
        );
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        adminRoleSid,
                        BasePermission.ADMINISTRATION.getMask()
                )
        );

        // user role permissions
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        userRoleSid,
                        BasePermission.READ.getMask()));
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        userRoleSid,
                        BasePermission.CREATE.getMask(),
                        false
                )
        );
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        userRoleSid,
                        BasePermission.WRITE.getMask(),
                        false
                )
        );
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        userRoleSid,
                        BasePermission.DELETE.getMask(),
                        false
                )
        );
        saveEntry(
                createEntry(
                        widgetAdminIdentity,
                        widgetOrder++,
                        userRoleSid,
                        BasePermission.ADMINISTRATION.getMask(),
                        false
                )
        );

        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        userRoleSid,
                        BasePermission.READ.getMask()));
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        userRoleSid,
                        BasePermission.CREATE.getMask(),
                        false
                )
        );
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        userRoleSid,
                        BasePermission.WRITE.getMask(),
                        false
                )
        );
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        userRoleSid,
                        BasePermission.DELETE.getMask(),
                        false
                )
        );
        saveEntry(
                createEntry(
                        locationAdminIdentity,
                        locOrder++,
                        userRoleSid,
                        BasePermission.ADMINISTRATION.getMask(),
                        false
                )
        );

        LOGGER.info("DB Load Complete");
        SecurityContextHolder.clearContext();
    }

    private void saveEntry(AclEntry entry) {
        aclEntryRepository.save(entry);
    }

    private AclEntry createEntry(AclObjectIdentity aclObjectIdentity, int aceOrder, AclSid aclSid, int mask) {
        return new AclEntry(
                aclObjectIdentity,
                aceOrder,
                aclSid,
                mask,
                true,
                true,
                true
        );
    }

    private AclEntry createEntry(AclObjectIdentity aclObjectIdentity, int aceOrder, AclSid aclSid, int mask, boolean granting) {
        return new AclEntry(
                aclObjectIdentity,
                aceOrder,
                aclSid,
                mask,
                granting,
                true,
                true
        );
    }
}
