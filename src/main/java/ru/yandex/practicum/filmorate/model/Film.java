package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.MinimumDate;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
}