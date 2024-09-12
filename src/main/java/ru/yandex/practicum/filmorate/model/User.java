package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.validator.Marker;

import java.time.LocalDate;

/**
 * User
 */
@AllArgsConstructor
@NoArgsConstructor
@Valid
@Data
public class User {
    @Null(groups = Marker.OnCreate.class, message = "id должно быть null")
    @NotNull(groups = Marker.OnUpdate.class, message = "id - null")
    private Long id; // id пользоватеоя

    @NotBlank(groups = Marker.OnCreate.class, message = "Электронная почта пустая")
    @Email(message = "Не правильный формат электронной почты")
    private String email; // электронная почта

    @NotNull(groups = Marker.OnCreate.class, message = "login - null")
    @Pattern(regexp = "^\\S+$", message = "Логин не может пустым и содержать пробелов")
    private String login; // логин пользовтеля

    private String name; // имя пользователя для отображения

    @NotNull(groups = Marker.OnCreate.class, message = "birthday - null")
    @Past
    private LocalDate birthday; // ДР пользователя
}
