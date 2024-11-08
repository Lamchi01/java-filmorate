package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Long reviewId; // id ревью

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content; // содержание отзыва

    @NotNull(message = "Должен быть указан тип отзыва")
    private Boolean isPositive; // тип отзыва — негативный (false) /положительный (true)

    @NotNull(message = "Должен быть указан id пользователя, который оставил отзыв")
    private Long userId; // id пользователя, который поставил отзыв

    @NotNull(message = "Должен быть указан id фильма, которому ставится отзыв")
    private Long filmId; // id фильма, которому поставили отзыв

    private Long useful = 0L; // рейтинг полезности
}
