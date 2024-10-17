package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

//@RequiredArgsConstructor
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final BaseStorage<User> userStorage;
    private final FriendStorage friendStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя с ID: {} заменено логином", user.getId());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        User savedUser = userStorage.findById(user.getId());

        if (user.getEmail() != null) savedUser.setEmail(user.getEmail());
        if (user.getName() != null) savedUser.setName(user.getName());
        if (user.getLogin() != null) savedUser.setLogin((user.getLogin()));
        if (user.getBirthday() != null) savedUser.setBirthday((user.getBirthday()));

        userStorage.update(user);
        return savedUser;
    }

    public void deleteAll() {
        userStorage.deleteAll();
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        userStorage.findById(friendId);
        friendStorage.addFriend(userId, friendId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        userStorage.findById(friendId);
        friendStorage.deleteFriend(userId, friendId);
        return user;
    }

    public List<User> getFriends(long userId) {
        userStorage.findById(userId);
        return friendStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        userStorage.findById(userId);
        userStorage.findById(otherId);
        return friendStorage.getCommonFriends(userId, otherId);
    }
}
