package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
public class FriendshipRepository extends BaseRepository<User> {
    private static final String INSERT_QUERY = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM FRIENDS WHERE FRIEND_ID = ? AND USER_ID = ?";
    private static final String QUERY_FOR_USER_FRIENDS = "SELECT * FROM USERS WHERE USER_ID IN" +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
    private static final String QUERY_FOR_COMMON_FRIENDS = "SELECT * FROM USERS WHERE USER_ID IN " +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?) AND USER_ID IN " +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public void addFriend(Integer id, Integer friendId) {
        update(INSERT_QUERY, id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        delete(DELETE_QUERY, friendId, id);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        return findMany(QUERY_FOR_COMMON_FRIENDS, id, otherId);
    }

    public Collection<User> getAllUserFriends(Integer id) {
        return findMany(QUERY_FOR_USER_FRIENDS, id);
    }
}