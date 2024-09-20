package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    void create(User user);

    User findById(Long id);

    void update(User user);

    void deleteAll();

    User addFriend(long userId, long friendId);

    User removeFriend(long userId, long friendId);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long userId, long otherId);
}
