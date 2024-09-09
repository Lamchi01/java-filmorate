package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public String getName() {
        if (name == null || name.isBlank()) {
            return login;
        }
        return name;
    }
}