package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Valid
@Data
public class User {
    Long id = 0L; // id пользоватеоя

    @NotNull(message = "Email - null")
    @NotBlank(message = "Электронная почта пустая")
    @Email(message = "Не правильный формат электронной почты")
    String email; // электронная почта

    @Pattern(regexp = "^\\S+$", message = "Логин не может пустым и содержать пробелов")
    String login; // логин пользовтеля

    String name; // имя пользователя для отображения

    @Past
    LocalDate birthday; // ДР пользователя
}
