package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;
    @Past
    @NotNull
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public String getName() {
        if (name == null || name.isBlank()) {
            return login;
        }
        return name;
    }
}