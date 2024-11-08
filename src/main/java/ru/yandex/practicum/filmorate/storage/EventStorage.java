package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventDto;

import java.util.Collection;

public interface EventStorage {
    void addEvent(Event event);

    Collection<EventDto> getEvents(long id);
}
