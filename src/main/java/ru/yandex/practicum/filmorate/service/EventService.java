package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.db.EventDbStorage;

import java.time.Instant;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventDbStorage storage;

    public void addEvent(long userId, Event.EventType eventType, Event.Operation operation, long entityId) {
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();
        storage.addEvent(event);
        log.info("Создано событие - {}", event);
    }

    public Collection<Event> getEvents(long userId) {
        return storage.getEvents(userId);
    }
}
