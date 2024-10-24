package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;

    public UserService(@Autowired @Qualifier("userRepository") UserStorage userStorage,
                       @Autowired FriendshipRepository friendshipRepository) {
        this.userStorage = userStorage;
        this.friendshipRepository = friendshipRepository;
    }

    public void addFriend(Integer id, Integer friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipRepository.addFriend(id, friendId);
        log.info("User {} added friend {}", id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipRepository.deleteFriend(id, friendId);
        log.info("User {} deleted friend {}", id, friendId);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        getUserById(id);
        getUserById(otherId);
        return friendshipRepository.getCommonFriends(id, otherId);
    }

    public Collection<User> getAllUserFriends(Integer id) {
        getUserById(id);
        return friendshipRepository.getAllUserFriends(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        getUserById(user.getId());
        return userStorage.update(user);
    }

    public void delete(Integer id) {
        userStorage.delete(id);
    }
}