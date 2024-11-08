package ru.yandex.practicum.filmorate.storage.db.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<EventDto> {
    @Override
    public EventDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        EventDto event = new EventDto();
        event.setTimestamp(rs.getTimestamp("timestamp").getTime());
        event.setUserId(rs.getLong("user_id"));
        event.setEventType(rs.getString("event_type"));
        event.setOperation(rs.getString("operation"));
        event.setEventId(rs.getLong("event_id"));
        event.setEntityId(rs.getLong("entity_id"));
        return event;
    }
}
