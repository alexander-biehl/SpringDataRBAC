package com.alexbiehl.demo.repository.eventhandler;

import com.alexbiehl.demo.model.Widget;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

@RepositoryEventHandler
public class WidgetEventHandler {

    @HandleBeforeCreate
    public void handleWidgetCreate(Widget w) {

    }
}
