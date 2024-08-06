package com.alexbiehl.demo.integration;

import com.alexbiehl.demo.TestConstants;
import com.alexbiehl.demo.model.Widget;
import com.alexbiehl.demo.repository.WidgetRepository;
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
public class WidgetIntegrationTests {


    @Autowired
    private WidgetRepository widgetRepository;

    @Test
    @WithMockUser(roles = {"USER"})
    public void givenUserAndWidget_get_andOk() {
        List<Widget> widgets = widgetRepository.findAll();
        assertNotNull(widgets);
        assertEquals(1, widgets.size());
        assertEquals(1, widgets.getFirst().getId());
    }

    @Test
    @WithMockUser
    public void givenUser_postWidget_andFail() {
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> widgetRepository.save(new Widget("test Widget", "test description")),
                "Expected AccessDeniedException"
        );
        assertNotNull(ex);
    }

    @Test
    @WithMockUser
    public void givenUserAndNote_delete_andFail() {
        Widget widget = widgetRepository.findById(TestConstants.TEST_WIDGET_ID);

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> widgetRepository.delete(widget),
                "Expected AccessDeniedException"
        );
        assertNotNull(ex);
    }

    @Test
    @WithMockUser
    public void givenUserAndNote_deleteById_andFail() {
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> widgetRepository.deleteById(TestConstants.TEST_WIDGET_ID),
                "Expected AccessDeniedException"
        );
        assertNotNull(ex);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdminAndNote_get_andOk() {
        List<Widget> widgets = widgetRepository.findAll();
        assertNotNull(widgets);
        assertEquals(1, widgets.size());
        assertEquals(1, widgets.getFirst().getId());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdmin_postNote_andOk() {
        Widget newWidget = new Widget("testWidget", "test");
        newWidget = widgetRepository.save(newWidget);
        assertNotNull(newWidget.getId());
        assertNotNull(widgetRepository.findById(newWidget.getId()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdmin_update_note_andOk() {
        Widget w = widgetRepository.findById(TestConstants.TEST_WIDGET_ID);
        w.setDescription("Updated description");
        widgetRepository.save(w);
        Widget updated = widgetRepository.findById(TestConstants.TEST_WIDGET_ID);
        assertEquals("Updated description", updated.getDescription());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdmin_deleteNote_andOk() {
        widgetRepository.delete(widgetRepository.findById(TestConstants.TEST_WIDGET_ID));
        assertNull(widgetRepository.findById(TestConstants.TEST_WIDGET_ID));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenAdminAndWidget_deleteById_andOk() {
        widgetRepository.deleteById(TestConstants.TEST_WIDGET_ID);
        assertNull(widgetRepository.findById(TestConstants.TEST_WIDGET_ID));
    }
}
