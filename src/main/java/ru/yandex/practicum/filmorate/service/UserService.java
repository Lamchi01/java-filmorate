package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Autowired @Qualifier("userRepository") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer friendId) {
        checkUnknownUser(userStorage.getUserById(id));
        checkUnknownUser(userStorage.getUserById(friendId));
        userStorage.addFriend(id, friendId);
        log.info("User {} added friend {}", id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        checkUnknownUser(userStorage.getUserById(id));
        checkUnknownUser(userStorage.getUserById(friendId));
        userStorage.deleteFriend(id, friendId);
        log.info("User {} deleted friend {}", id, friendId);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public Collection<User> getAllUserFriends(Integer id) {
        checkUnknownUser(userStorage.getUserById(id));
        return userStorage.getAllUserFriends(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        User user = userStorage.getUserById(id);
        checkUnknownUser(user);
        return user;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(Integer id) {
        checkUnknownUser(userStorage.getUserById(id));
        userStorage.delete(id);
    }

    private void checkUnknownUser(User user) {
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}