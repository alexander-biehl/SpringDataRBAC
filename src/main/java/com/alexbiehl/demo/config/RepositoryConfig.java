package com.alexbiehl.demo.config;

import com.alexbiehl.demo.repository.eventhandler.LocationEventHandler;
import com.alexbiehl.demo.repository.eventhandler.WidgetEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public WidgetEventHandler widgetEventHandler() {
        return new WidgetEventHandler();
    }

    @Bean
    public LocationEventHandler locationEventHandler() {
        return new LocationEventHandler();
    }
}
