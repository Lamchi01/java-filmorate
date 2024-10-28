package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getUsers();

    User getUserById(Integer id);

    User create(User user);

    User update(User user);

    void delete(Integer id);
}