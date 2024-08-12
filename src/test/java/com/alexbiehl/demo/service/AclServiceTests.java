package com.alexbiehl.demo.service;

import com.alexbiehl.demo.config.TestDataSourceLoader;
import com.alexbiehl.demo.model.Location;
import com.alexbiehl.demo.model.Widget;
import com.alexbiehl.demo.model.security.AclClass;
import com.alexbiehl.demo.model.security.AclEntry;
import com.alexbiehl.demo.model.security.AclObjectIdentity;
import com.alexbiehl.demo.model.security.AclSid;
import com.alexbiehl.demo.repository.LocationRepository;
import com.alexbiehl.demo.repository.WidgetRepository;
import com.alexbiehl.demo.repository.security.AclClassRepository;
import com.alexbiehl.demo.repository.security.AclEntryRepository;
import com.alexbiehl.demo.repository.security.AclObjectIdentityRepository;
import com.alexbiehl.demo.repository.security.AclSidRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {TestDataSourceLoader.class})
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test-no-security-load")
public class AclServiceTests {


    private static final Logger log = LoggerFactory.getLogger(AclServiceTests.class);

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
    @Autowired
    private AclCache aclCache;

    @BeforeEach
    public void setup() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
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

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
        aclCache.clearCache();
    }

    @Test
    // @WithMockUser(roles = {"ADMIN"})
    public void testGrantPermissionsToSid() {
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

        Iterable<AclSid> sids = sidRepository.findAll();
        log.info("SIDS: {}", sids);
        boolean foundUserSid = false;
        AclSid sid = null;
        for (AclSid id : sids) {
            if (id.getSid().equals("ROLE_USER")) {
                foundUserSid = true;
                sid = id;
            }
        }
        assertTrue(foundUserSid, "'ROLE_USER' was not found in the list of SIDs.");

        Iterable<AclObjectIdentity> oids = objectRepository.findAll();
        List<AclObjectIdentity> oidList = new ArrayList<>();
        oids.forEach(oidList::add);
        log.info("OID List: {}", oidList);

        AclObjectIdentity oid = oids.iterator().next();
        assertEquals(clazz, oid.getObjectIdClass());
        assertNotNull(sid);
        // AclObjectIdentity SID will always be the name of the logged in user/authentication
        AclSid adminId = sidRepository.findBySid("admin");
        assertEquals(String.valueOf(adminId.getId()), oid.getObjectIdIdentity());

        Iterable<AclEntry> entries = aclEntryRepository.findAll();
        List<AclEntry> entryList = new ArrayList<>();
        entries.forEach(entryList::add);
        log.info("ACL Entries: {}", entries);

        assertEquals(aclService.getPermissions().length, entryList.size());
        for (AclEntry entry : entryList) {
            assertEquals(oid, entry.getAclObjectIdentity());
            assertEquals("ROLE_USER", entry.getAclSid().getSid());
            assertFalse(entry.getAclSid().isPrincipal());
            assertTrue(entry.isGranting());
        }
    }
}
