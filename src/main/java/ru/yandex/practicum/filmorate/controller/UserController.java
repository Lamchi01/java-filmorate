package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
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

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        checkEmail(user);
        Long id = user.getId();
        if (id == null) {
            log.warn("Должен быть указан ID пользователя");
            throw new ValidationException("Должен быть указан ID пользователя");
        }

        if (users.get(id) == null) {
            log.warn("Пользователь с ID: {} не найден", id);
            throw new ValidationException("Пользователь с ID " + id + " не найден");
        }

        users.replace(id, user);
        log.trace("Обновлен пользователь с ID: {}", id);
        return user;
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
        if (users.values().stream().anyMatch(usr -> usr.getEmail().equals(user.getEmail()))) {
            log.warn("Email {} уже используется", user.getEmail());
            throw new ValidationException("Email: " + user.getEmail() + " уже используется");
        }
        log.debug("Email: {} проверку прошел", user.getEmail());
    }
}
