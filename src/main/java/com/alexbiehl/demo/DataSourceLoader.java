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

import javax.swing.plaf.basic.BasicEditorPaneUI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        User manager = new User("manager", passwordEncoder.encode("password"), true);
        User admin = new User("admin", passwordEncoder.encode("password"), true);

        Role userRole = new Role("ROLE_USER");
        userRole = roleRepository.save(userRole);
        Role managerRole = new Role("ROLE_MANAGER", userRole);
        // Role managerRole = new Role("ROLE_MANAGER");
        managerRole = roleRepository.save(managerRole);
        Role adminRole = new Role("ROLE_ADMIN", managerRole);
        // Role adminRole = new Role("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);


        user.setRoles(Collections.singleton(userRole));
        manager.setRoles(Collections.singleton(managerRole));
        admin.setRoles(Collections.singleton(adminRole));

        user = userRepository.save(user);
        manager = userRepository.save(manager);
        admin = userRepository.save(admin);

        // create Security IDentifiers (ACL SID)
        AclSid userRoleSid = new AclSid(false, userRole.getName());
        AclSid managerRoleSid = new AclSid(false, managerRole.getName());
        AclSid adminRoleSid = new AclSid(false, adminRole.getName());

        userRoleSid = aclSidRepository.save(userRoleSid);
        managerRoleSid = aclSidRepository.save(managerRoleSid);
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

        widgetAdminIdentity = aclObjectIdentityRepository.save(widgetAdminIdentity);
        locationAdminIdentity = aclObjectIdentityRepository.save(locationAdminIdentity);

        // Create the ACL entries for each domain object, Sid, and permission
        // user role has read access for now, admin will have all access
        AtomicInteger widgetOrder = new AtomicInteger(0);
        AtomicInteger locOrder = new AtomicInteger(0);

        List<Integer> pList = Arrays.asList(
                BasePermission.READ.getMask(),
                BasePermission.CREATE.getMask(),
                BasePermission.WRITE.getMask(),
                BasePermission.DELETE.getMask(),
                BasePermission.ADMINISTRATION.getMask());

        // user role permissions
        persistPermissions(
                widgetAdminIdentity,
                widgetOrder,
                userRoleSid,
                pList,
                List.of(true, false, false, false, false)
        );
        persistPermissions(
                locationAdminIdentity,
                locOrder,
                userRoleSid,
                pList,
                List.of(true, false, false, false, false)
        );

        // manager role permissions
        persistPermissions(
                widgetAdminIdentity,
                widgetOrder,
                managerRoleSid,
                pList,
                List.of(true, true, true, false, false)
        );
        persistPermissions(
                locationAdminIdentity,
                locOrder,
                managerRoleSid,
                pList,
                List.of(true, true, true, false, false)
        );

        // admin acl entries

        persistPermissions(
                widgetAdminIdentity,
                widgetOrder,
                adminRoleSid,
                pList,
                List.of(true, true, true, true, true)
        );
        persistPermissions(
                locationAdminIdentity,
                locOrder,
                adminRoleSid,
                pList,
                List.of(true, true, true, true, true)
        );

        LOGGER.info("DB Load Complete");
        SecurityContextHolder.clearContext();
    }

    private void persistPermissions(
            AclObjectIdentity oid,
            AtomicInteger order,
            AclSid sid,
            List<Integer> permissions,
            List<Boolean> grants) {
        Iterator<Integer> pIter = permissions.listIterator();
        Iterator<Boolean> gIter = grants.listIterator();

        while (pIter.hasNext() && gIter.hasNext()) {
            saveEntry(
                    createEntry(
                            oid,
                            order.getAndIncrement(),
                            sid,
                            pIter.next(),
                            gIter.next()
                    )
            );
        }
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
