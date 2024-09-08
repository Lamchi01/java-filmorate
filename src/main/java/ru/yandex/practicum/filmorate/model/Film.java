package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.MinimumDate;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Film.
 */
@Valid
@Data
public class Film implements Serializable {
    Long id; // id фильма

    @NotNull(message = "Нет названия")
    @NotBlank(message = "Название не может быть пустым")
    String name; // название фильма

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    String description; // описание фильма

    @MinimumDate(value = "1895-12-28", message = "Дата релиза должна быть на раньше 28.12.1895 года")
    LocalDate releaseDate; // дата релиза

    @Min(value = 0, message = "Продолжительность фильма должна быть положительным числом")
    Long duration; // продолжительность фильма в секундах
}
