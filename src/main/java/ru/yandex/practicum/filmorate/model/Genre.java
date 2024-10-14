package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Valid
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Genre {
    @NotNull
    private Integer id;

    private String name;
}
