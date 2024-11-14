package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@Component
public class UserDbStorage extends BaseDbStorage<User> implements BaseStorage<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM users";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE user_id = ?";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User findById(Long id) {
        log.info("Получен запрос на получение пользовтале с IDL {}", id);
        User user = findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
        if (user == null) {
            log.warn("Пользователь с ID: {} не найден", id);
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public User create(User user) {
        long id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        log.info("Добавлен новый пользователь с ID: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Обновлен пользователь с ID: {}", user.getId());
        return user;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.info("Удалены все пользователи");
    }


    @Override
    public void deleteById(long id) {
        if (!removeOne(DELETE_BY_ID_QUERY, id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }
        log.info("Пользователь с ID: {} успешно удалён", id);
    }
}
