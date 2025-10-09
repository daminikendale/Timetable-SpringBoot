package com.rspc.timetable.services;

import com.rspc.timetable.entities.Timetable;
import com.rspc.timetable.events.DatabaseChangeEvent;
import com.rspc.timetable.repositories.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimetableInvalidationService {

    private static final Logger logger = LoggerFactory.getLogger(TimetableInvalidationService.class);
    private final TimetableRepository timetableRepository;

    @EventListener
    @Transactional
    public void handleDatabaseChange(DatabaseChangeEvent event) {
        logger.info("Received database change event for {}. Invalidating timetable.", event.getSource().getClass().getSimpleName());
        
        timetableRepository.findAllByStatus(Timetable.TimetableStatus.PUBLISHED)
            .stream()
            .filter(Timetable::isValid)
            .forEach(timetable -> {
                timetable.setValid(false);
                timetableRepository.save(timetable);
                logger.warn("Timetable ID {} has been marked as INVALID due to the change.", timetable.getId());
            });
    }
}
