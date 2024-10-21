package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Likes {
    private Integer filmId;
    private Integer userId;
}
