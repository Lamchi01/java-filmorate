package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Long id; // id пользователя

    @NotBlank(message = "Электронная почта пустая")
    @Email(message = "Не правильный формат электронной почты")
    private String email; // электронная почта

    @NotNull(message = "login - null")
    @Pattern(regexp = "^\\S+$", message = "Логин не может пустым и содержать пробелов")
    private String login; // логин пользовтеля

    private String name; // имя пользователя для отображения

    @NotNull(message = "birthday - null")
    @Past(message = "Дата рождения должны быть в прошлом")
    private LocalDate birthday; // ДР пользователя

    @JsonIgnore
    private Set<Long> friends = new HashSet<>();
}
