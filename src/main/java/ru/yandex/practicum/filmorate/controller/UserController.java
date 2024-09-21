package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getAllFriends(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id,
                                             @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }
}