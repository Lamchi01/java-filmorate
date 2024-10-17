package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "true")
public class UserInMemoryStorage implements BaseStorage<User> {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        log.trace("Получен запрос на получение всех пользователей");
        return users.values().stream().toList();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.trace("Добавлен новый пользователь с ID: {}", user.getId());
        return user;
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
    public User update(User user) {
        findById(user.getId());
        users.replace(user.getId(), user);
        log.trace("Обновлен пользователь с ID: {}", user.getId());
        return user;
    }

    @Override
    public void deleteAll() {
        users.clear();
        log.trace("Удалены все пользователи");
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
}
