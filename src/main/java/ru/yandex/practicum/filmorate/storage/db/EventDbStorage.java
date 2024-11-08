package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventDto;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.Collection;

@Slf4j
@Repository
public class EventDbStorage extends BaseDbStorage<EventDto> implements EventStorage {
    private static final String FIND_ALL_EVENTS_BY_USER = "SELECT * FROM EVENTS WHERE USER_ID = ?";
    private static final String ADD_QUERY = "INSERT INTO EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, " +
            "ENTITY_ID) VALUES (?, ?, ?, ?, ?)";

    public EventDbStorage(JdbcTemplate jdbc, RowMapper<EventDto> mapper) {
        super(jdbc, mapper);
    }

    public void addEvent(Event event) {
        insert(
                ADD_QUERY,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId()
        );
    }

    public Collection<EventDto> getEvents(long id) {
        return findMany(FIND_ALL_EVENTS_BY_USER, id);
    }
}
