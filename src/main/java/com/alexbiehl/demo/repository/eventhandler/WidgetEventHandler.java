package com.alexbiehl.demo.repository.eventhandler;

import com.alexbiehl.demo.model.Widget;
import com.alexbiehl.demo.service.AclService;
import com.alexbiehl.demo.service.SecurityMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@RepositoryEventHandler
public class WidgetEventHandler {

    @Autowired
    private SecurityMetaService secService;

    private static final Logger log = LoggerFactory.getLogger(WidgetEventHandler.class);

    @HandleBeforeCreate
    public void handleBeforeCreate(Widget w) {
        log.info("@BeforeCreate for Widget: {}", w.toString());
    }

    @HandleAfterCreate
    public void handleAfterCreate(Widget w) {
        log.info("@AfterCreate for Widget: {}", w.toString());
        secService.grantDefaultAccess(w);
    }

    @HandleAfterDelete
    public void handleAfterDelete(Widget w) {
        log.info("@AfterDelete for Widget: {}", w.toString());
        secService.removeDefaultAccess(w);
    }
}
