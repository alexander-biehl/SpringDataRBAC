package com.alexbiehl.demo.repository.eventhandler;

import com.alexbiehl.demo.model.Widget;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

@RepositoryEventHandler
public class WidgetEventHandler {

    @HandleAfterCreate
    public void handleAfterCreate(Widget w) {

    }

    @HandleAfterDelete
    public void handleAfterDelete(Widget w) {

    }
}
