package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id) {
        log.info("Получен запрос на получение пользователя с ID {}", id);
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с ID {}", user.getId());
        return userService.update(user);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        log.info("Получен запрос на удаление всех пользователей");
        userService.deleteAll();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос на добавление пользователю с ID {} друга с ID {}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос на удаление у пользователя с ID {} друга с ID {}", id, friendId);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable long id) {
        log.info("Получен запрос на получение друзей пользователя с ID {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получен запрос на получение общих друзей пользователей с ID {} и ID {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя с ID {}", id);
        userService.deleteUser(id);
    }

    @GetMapping("{id}/feed")
    public List<Event> getEvents(@PathVariable long id) {
        log.info("Получен запрос на получение события с ID {}", id);
        return userService.getEvents(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable long id) {
        log.info("Получен запрос на получение рекомендаций по фильмам пользователю с ID {}", id);
        return filmService.getRecommendation(id);
    }
}
