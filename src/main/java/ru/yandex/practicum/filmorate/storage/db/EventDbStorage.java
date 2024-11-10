package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Slf4j
@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {
    private static final String FIND_ALL_EVENTS_BY_USER = "SELECT * FROM EVENTS WHERE USER_ID = ?";
    private static final String ADD_QUERY = "INSERT INTO EVENTS (timestamp, user_id, event_type, operation, " +
            "entity_id) VALUES (?, ?, ?, ?, ?)";

    public EventDbStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public void addEvent(Event event) {
        insert(
                ADD_QUERY,
                Timestamp.from(Instant.ofEpochMilli(event.getTimestamp())),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );
    }

    public Collection<Event> getEvents(long id) {
        return findMany(FIND_ALL_EVENTS_BY_USER, id);
    }
}