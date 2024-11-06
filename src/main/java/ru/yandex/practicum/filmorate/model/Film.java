package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.validator.MinimumDate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Film.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film implements Serializable {
    private Long id; // id фильма

    @NotBlank(message = "Название не может быть пустым")
    private String name; // название фильма

    @NotNull(message = "description - null")
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description; // описание фильма

    @NotNull(message = "releaseDate - null")
    @MinimumDate(value = "1895-12-28", message = "Дата релиза должна быть на раньше 28.12.1895 года")
    private LocalDate releaseDate; // дата релиза

    @NotNull(message = "duration - null")
    @Min(value = 0, message = "Продолжительность фильма должна быть положительным числом")
    private Long duration; // продолжительность фильма в секундах

    private Mpa mpa = new Mpa(); // рейтинг фильма по категории MPA

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    private LinkedHashSet<Director> directors = new LinkedHashSet<>();

    @JsonIgnore
    private Set<Long> likes = new HashSet<>(); // список id пользователей, которые поставили лайк

    private Long countLikes = 0L; // количество лайков
}
