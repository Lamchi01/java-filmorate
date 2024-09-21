package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    void create(User user);

    User findById(Long id);

    void update(User user);

    void deleteAll();
}
