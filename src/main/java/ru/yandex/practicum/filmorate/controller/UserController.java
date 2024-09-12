package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validator.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.trace("Выполнен запрос на получение всех пользователей");
        return users.values();
    }

    @Validated(Marker.OnCreate.class)
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        checkEmail(user);
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя с ID: {} заменено логином", user.getId());
        }
        users.put(user.getId(), user);
        log.trace("Добавлен новый пользователь с ID: {}", user.getId());
        return user;
    }

    @Validated({Marker.OnUpdate.class})
    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {
        Long id = user.getId();
        if (id == null) {
            log.warn("Должен быть указан ID пользователя");
            throw new ValidationException("Должен быть указан ID пользователя");
        }

        User savedUser = users.get(id);
        if (savedUser == null) {
            log.warn("Пользователь с ID: {} не найден", id);
            throw new ValidationException("Пользователь с ID " + id + " не найден");
        }

        checkEmail(user);

        if (user.getEmail() != null) savedUser.setEmail(user.getEmail());
        if (user.getName() != null) savedUser.setName(user.getName());
        if (user.getLogin() != null) savedUser.setLogin((user.getLogin()));
        if (user.getBirthday() != null) savedUser.setBirthday((user.getBirthday()));
        users.replace(id, user);
        log.trace("Обновлен пользователь с ID: {}", id);
        return savedUser;
    }

    @DeleteMapping
    public void deleteAllFilms() {
        log.trace("Удалены все пользователи");
        users.clear();
    }

    // вспомогательный метод для генерации нового идентификатора
    private long getNextId() {
        long currentMaxId = users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }

    // проверка email на дубликат
    private void checkEmail(User user) {
        if (users.values().stream()
                .anyMatch(usr -> usr.getEmail().equals(user.getEmail()) & !usr.getId().equals(user.getId()))) {
            log.warn("Email {} уже используется", user.getEmail());
            throw new ValidationException("Email: " + user.getEmail() + " уже используется");
        }
        log.debug("Email: {} проверку прошел", user.getEmail());
    }
}
