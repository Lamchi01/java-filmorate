package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class Event {
    @NotNull
    private Timestamp timestamp;
    @NotNull
    private Long userId;
    @NotNull
    private String eventType;
    @NotNull
    private String operation;
    private Long eventId;
    @NotNull
    private Long entityId;
}