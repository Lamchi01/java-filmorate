package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(getNextId());
        user.setName(user.getName());
        users.put(user.getId(), user);
        log.info("User created: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должно быть указано");
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id не найден");
        }
        User oldUser = users.get(user.getId());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!checkEmail(user.getEmail())) {
                throw new ValidationException("Email не соответствет стандарту");
            } else {
                oldUser.setEmail(user.getEmail());
            }
        }
        if (user.getLogin() != null) {
            if (user.getLogin().isBlank()) {
                throw new ValidationException("Логин не может быть пустым");
            }
            oldUser.setLogin(user.getLogin());
        }
        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            oldUser.setBirthday(user.getBirthday());
        }
        if (user.getName() != null || !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        users.put(oldUser.getId(), oldUser);
        log.info("User updated: {}", oldUser);
        return user;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public static boolean checkEmail(String email) {
        return email.matches("^[\\w-.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$");
    }
}