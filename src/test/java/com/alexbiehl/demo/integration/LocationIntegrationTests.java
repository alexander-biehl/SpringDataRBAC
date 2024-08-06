package com.alexbiehl.demo.integration;

import com.alexbiehl.demo.TestConstants;
import com.alexbiehl.demo.model.Location;
import com.alexbiehl.demo.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableAutoConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocationIntegrationTests {

    @Autowired
    private LocationRepository locRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdminAndLoc_get_andOk() {
        List<Location> locs = locRepository.findAll();
        assertNotNull(locs);
        assertEquals(1, locs.size());
        assertEquals(1, locs.getFirst().getId());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdmin_postLoc_andOk() {
        Location loc = new Location(
                "Test Location",
                "description",
                "123 main st",
                "Anytown",
                "CA",
                "00000",
                "USA"
        );
        loc = locRepository.save(loc);
        assertNotNull(loc.getId());
        assertNotNull(locRepository.findById(loc.getId()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdmin_updateLoc_andOk() {
        Location loc = locRepository.findById(TestConstants.TEST_LOC_ID);
        loc.setDescription("updated description");
        locRepository.save(loc);
        Location updatedLoc = locRepository.findById(TestConstants.TEST_LOC_ID);
        assertEquals("updated description", updatedLoc.getDescription());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdminAndLoc_delete_andOk() {
        locRepository.delete(locRepository.findById(TestConstants.TEST_LOC_ID));
        assertNull(locRepository.findById(TestConstants.TEST_LOC_ID));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdminAndLoc_deleteById_andOk() {
        locRepository.deleteById(TestConstants.TEST_LOC_ID);
        assertNull(locRepository.findById(TestConstants.TEST_LOC_ID));
    }

    @Test
    @WithMockUser
    public void givenUserAndLoc_get_andOk() {
        List<Location> locs = locRepository.findAll();
        assertNotNull(locs);
        assertEquals(1, locs.size());
        assertEquals(1, locs.getFirst().getId());
    }

    @Test
    @WithMockUser
    public void givenUser_postLoc_andFail() {
        final Location loc = new Location(
                "Test Location",
                "description",
                "123 main st",
                "Anytown",
                "CA",
                "00000",
                "USA"
        );

        assertThrows(
                AccessDeniedException.class,
                () -> locRepository.save(loc),
                "AccessDenied Exception was not thrown when ROLE_USER creates a location"
        );
    }

    @Test
    @WithMockUser
    public void givenUser_updateLoc_andFail() {
        Location loc = locRepository.findById(TestConstants.TEST_LOC_ID);
        loc.setDescription("updated description");
        assertThrows(
                AccessDeniedException.class,
                () -> locRepository.save(loc),
                "AccessDeniedException not thrown when ROLE_USER updates a location"
        );
    }

    @Test
    @WithMockUser
    public void givenUser_deleteLoc_andFail() {
        Location loc = locRepository.findById(TestConstants.TEST_LOC_ID);
        assertThrows(
                AccessDeniedException.class,
                () -> locRepository.delete(loc),
                "AccessDeniedException not thrown when ROLE_USER deletes a location"
        );
    }

    @Test
    @WithMockUser
    public void givenUser_deleteById_andFail() {
        assertThrows(
                AccessDeniedException.class,
                () -> locRepository.deleteById(TestConstants.TEST_LOC_ID),
                "AccessDeniedException not thrown when ROLE_USER deletes location by id"
        );
    }
}
