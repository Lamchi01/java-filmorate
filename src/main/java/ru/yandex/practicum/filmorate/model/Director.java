package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Director.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Director {
    private Long id; // id режиссёра

    @NotBlank(message = "Имя не может быть пустым")
    private String name; // имя режиссёра
}
