package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

@Repository
public class FriendshipRepository extends BaseRepository<Friendship> {
    private static final String QUERY_FOR_ALL_FRIENDS = "SELECT * FROM FRIENDS";
    private static final String QUERY_DOR_BY_USER_ID = "SELECT * FROM FRIENDS WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM FRIENDS WHERE FRIEND_ID = ? AND USER_ID = ?";

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Friendship> getAllFriends() {
        return findMany(QUERY_FOR_ALL_FRIENDS);
    }

    public Collection<Friendship> getFriendsByUserId(Integer id) {
        return findMany(QUERY_DOR_BY_USER_ID, id);
    }

    public void addFriend(Integer id, Integer friendId) {
        update(INSERT_QUERY, id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        delete(DELETE_QUERY, friendId, id);
    }
}