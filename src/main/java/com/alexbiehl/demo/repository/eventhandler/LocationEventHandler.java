package com.alexbiehl.demo.repository.eventhandler;

import com.alexbiehl.demo.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

@RepositoryEventHandler
public class LocationEventHandler {


    private static final Logger log = LoggerFactory.getLogger(LocationEventHandler.class);

    @HandleBeforeCreate
    public void handleBeforeCreate(Location l) {
        log.info("handleBeforeCreate for Location: {}", l.toString());
    }

    @HandleAfterCreate
    public void handleAfterCreate(Location l) {
        log.info("handleAfterCreate for Location: {}", l.toString());
    }

    @HandleAfterDelete
    public void handleAfterDelete(Location l) {
        log.info("handleAfterDelete for Location: {}", l.toString());
    }
}
