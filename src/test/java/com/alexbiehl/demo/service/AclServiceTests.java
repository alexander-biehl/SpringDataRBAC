package com.alexbiehl.demo.service;

import com.alexbiehl.demo.model.Location;
import com.alexbiehl.demo.model.Widget;
import com.alexbiehl.demo.model.security.AclClass;
import com.alexbiehl.demo.model.security.AclObjectIdentity;
import com.alexbiehl.demo.model.security.AclSid;
import com.alexbiehl.demo.repository.LocationRepository;
import com.alexbiehl.demo.repository.UserRepository;
import com.alexbiehl.demo.repository.WidgetRepository;
import com.alexbiehl.demo.repository.security.AclClassRepository;
import com.alexbiehl.demo.repository.security.AclEntryRepository;
import com.alexbiehl.demo.repository.security.AclObjectIdentityRepository;
import com.alexbiehl.demo.repository.security.AclSidRepository;
import com.alexbiehl.demo.security.AclContext;
import com.alexbiehl.demo.security.UserDetailsServiceImpl;
import com.alexbiehl.demo.security.WebSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@EnableAutoConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AclServiceTests {

    private static Widget testWidget;
    private static Location testLoc;

    @Autowired
    private AclService aclService;
    @Autowired
    private AclClassRepository aclClassRepository;
    @Autowired
    private AclEntryRepository aclEntryRepository;
    @Autowired
    private AclObjectIdentityRepository objectRepository;
    @Autowired
    private AclSidRepository sidRepository;
    @Autowired
    private WidgetRepository widgetRepository;
    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    public void setup() {
        testWidget = widgetRepository.save(new Widget("test", "test"));
        testLoc = locationRepository.save(new Location(
                "Test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test"
        ));
    }

    @Test
    public void testCreateAcl() {
        aclService.grantPermissionsToSid(
                testWidget.getClass().toString(),
                testWidget.getId(),
                "ROLE_USER",
                aclService.getPermissions(),
                new Boolean[]{true, true, true, true, true}
        );

        AclClass clazz = aclClassRepository.findAll().iterator().next();
        assertNotNull(clazz);
        assertEquals(testWidget.getClass().toString(), clazz.getClazz());

        AclSid sid = sidRepository.findAll().iterator().next();
        assertEquals("ROLE_USER", sid.getSid());

        AclObjectIdentity oids = objectRepository.findAll().iterator().next();
        assertEquals(clazz, oids.getObjectIdClass());
        assertEquals(sid.getSid(), oids.getObjectIdIdentity());
    }
}
