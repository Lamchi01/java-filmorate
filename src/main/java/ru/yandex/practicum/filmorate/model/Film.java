package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.validator.Marker;
import ru.yandex.practicum.filmorate.model.validator.MinimumDate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Valid
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film implements Serializable {
    @Null(groups = Marker.OnCreate.class, message = "id должно быть null")
    @NotNull(groups = Marker.OnUpdate.class, message = "id - null")
    private Long id; // id фильма

    @NotBlank(groups = Marker.OnCreate.class, message = "Название не может быть пустым")
    private String name; // название фильма

    @NotNull(groups = Marker.OnCreate.class, message = "description - null")
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description; // описание фильма

    @NotNull(groups = Marker.OnCreate.class, message = "releaseDate - null")
    @MinimumDate(value = "1895-12-28", message = "Дата релиза должна быть на раньше 28.12.1895 года")
    private LocalDate releaseDate; // дата релиза

    @NotNull(groups = Marker.OnCreate.class, message = "duration - null")
    @Min(value = 0, message = "Продолжительность фильма должна быть положительным числом")
    private Long duration; // продолжительность фильма в секундах

    @Valid
    private Mpa mpa; // рейтинг фильма по категории MPA

    @Valid
    private List<Genre> genres;

    @JsonIgnore
    private Set<Long> likes = new HashSet<>(); // список id пользователей, которые поставили лайк

    private int countLikes; // количество лайков

    // добавление лайка к фильму, инкрементируя счетчик лайков
    public void addLike(Long userId) {
        if (likes.contains(userId)) {
            return;
        }
        likes.add(userId);
        countLikes++;
    }

    // удаление лайка в фильму, декрементируя счетчкаи лайка
    public void deleteLike(Long userId) {
        if (!likes.contains(userId)) {
            return;
        }
        likes.remove(userId);
        if (countLikes > 0) { // на всякий случай, вдруг так получится, что countLikes == 0
            countLikes--;
        }
    }
}
