package com.rspc.timetable.listeners;

import com.rspc.timetable.events.DatabaseChangeEvent;
import com.rspc.timetable.util.ApplicationContextHolder;
import jakarta.persistence.*;
import org.springframework.context.ApplicationEventPublisher;

public class TimetableInvalidationListener {

    @PostPersist
    @PostUpdate
    @PostRemove
    public void onDatabaseChange(Object entity) {
        // Get the ApplicationEventPublisher from the context
        ApplicationEventPublisher publisher = ApplicationContextHolder.getContext().getBean(ApplicationEventPublisher.class);
        
        // Publish an event, passing the changed entity as the source
        publisher.publishEvent(new DatabaseChangeEvent(entity));
    }
}
