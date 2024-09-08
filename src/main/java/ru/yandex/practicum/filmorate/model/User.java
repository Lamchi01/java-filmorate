package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    Integer id;
    String email;
    String login;
    String name;
    LocalDate birthday;

    public String getName() {
        if (name == null || name.isBlank()) {
            return login;
        }
        return name;
    }
}