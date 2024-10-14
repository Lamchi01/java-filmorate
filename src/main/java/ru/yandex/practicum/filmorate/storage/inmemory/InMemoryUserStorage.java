package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.trace("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @Override
    public void create(User user) {
        users.put(user.getId(), user);
        log.trace("Добавлен новый пользователь с ID: {}", user.getId());
    }

    @Override
    public User findById(Long id) {
        log.trace("Получен запрос на получение пользовтале с IDL {}", id);
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с ID: {} не найден", id);
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public void update(User user) {
        if (findById(user.getId()) == null) {
            return;
        }
        users.replace(user.getId(), user);
        log.trace("Обновлен пользователь с ID: {}", user.getId());
    }

    @Override
    public void deleteAll() {
        users.clear();
        log.trace("Удалены все пользователи");
    }
}
