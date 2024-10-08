package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        return users.get(id);
    }

    @Override
    public User create(@Valid User user) {
        user.setId(getNextId());
        user.setName(user.getName());
        users.put(user.getId(), user);
        log.info("User created: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должно быть указано");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User oldUser = users.get(user.getId());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!user.checkEmail()) {
                throw new ValidationException("Email не соответствет стандарту");
            } else {
                oldUser.setEmail(user.getEmail());
                log.debug("User email updated: {}", user.getEmail());
            }
        }
        if (user.getLogin() != null) {
            if (user.getLogin().isBlank()) {
                throw new ValidationException("Логин не может быть пустым");
            }
            oldUser.setLogin(user.getLogin());
            log.debug("User login updated: {}", user.getLogin());
        }
        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            oldUser.setBirthday(user.getBirthday());
            log.debug("User birthday updated: {}", user.getBirthday());
        }
        if (user.getName() != null || !user.getName().isBlank()) {
            oldUser.setName(user.getName());
            log.debug("User name updated: {}", user.getName());
        }
        users.put(oldUser.getId(), oldUser);
        log.info("User updated: {}", oldUser);
        return user;
    }

    @Override
    public void delete(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        users.remove(id);
        log.info("User deleted: {}", id);
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}