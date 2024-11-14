package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void addEvent(long userId, Event.EventType eventType, Event.Operation operation, long entityId);

    List<Event> getEvents(long id);
}
