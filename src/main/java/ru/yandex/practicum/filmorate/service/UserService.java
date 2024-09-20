package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User findById(long id) {
        return storage.findById(id);
    }

    public void create(User user) {
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя с ID: {} заменено логином", user.getId());
        }
        storage.create(user);
    }

    public User update(User user) {
        Long id = user.getId();
        User savedUser = storage.findById(id);

        if (user.getEmail() != null) savedUser.setEmail(user.getEmail());
        if (user.getName() != null) savedUser.setName(user.getName());
        if (user.getLogin() != null) savedUser.setLogin((user.getLogin()));
        if (user.getBirthday() != null) savedUser.setBirthday((user.getBirthday()));

        storage.update(user);
        return savedUser;
    }

    public void deleteAll() {
        storage.deleteAll();
    }

    public User addFriend(long userId, long friendId) {
        return storage.addFriend(userId, friendId);
    }

    public User deleteFriend(long userId, long friendId) {
        User user = storage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        User friend = storage.findById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с ID: " + friendId + " не найден");
        }
        return storage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        User user = storage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        return storage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        return storage.getCommonFriends(userId, otherId);
    }

    // вспомогательный метод для генерации нового идентификатора
    private long getNextId() {
        long currentMaxId = storage.findAll()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }
}
