package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class Event {
    @NotNull
    private Long timestamp;
    @NotNull
    private Long userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private Operation operation;
    private Long eventId;
    @NotNull
    private Long entityId;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        REMOVE,
        ADD, UPDATE
    }
}