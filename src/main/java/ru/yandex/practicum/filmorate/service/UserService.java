package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(int id, int friendId) {
        // User
        userStorage.getUserById(id).getFriends().add(friendId);
        log.info("User {} added friend {}", id, friendId);
        //Friend
        userStorage.getUserById(friendId).getFriends().add(id);
        log.info("User {} added friend {}", friendId, id);
    }

    public void deleteFriend(int id, int friendId) {
        //User
        userStorage.getUserById(id).getFriends().remove(friendId);
        log.info("User {} deleted friend {}", id, friendId);
        //Friend
        userStorage.getUserById(friendId).getFriends().remove(id);
        log.info("User {} deleted friend {}", friendId, id);
    }

    public Collection<User> getCommonFriends(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        return user.getFriends().stream()
                .filter(userId -> friend.getFriends().contains(userId))
                .map(userStorage::getUserById)
                .toList();
    }

    public Collection<User> getAllFriends(int id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(int id) {
        userStorage.delete(id);
    }
}