package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Repository
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String QUERY_FOR_ALL_USERS = "SELECT * FROM USERS";
    private static final String QUERY_FOR_USER_BY_ID = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";

    private static final String QUERY_FOR_USER_FRIENDS = "SELECT * FROM USERS WHERE USER_ID IN" +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
    private static final String QUERY_FOR_COMMON_FRIENDS = "SELECT * FROM USERS WHERE USER_ID IN " +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?) AND USER_ID IN " +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";


    private final FriendshipRepository friendshipRepository;

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper,
                          @Autowired FriendshipRepository friendshipRepository) {
        super(jdbc, mapper);
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public Collection<User> getUsers() {
        Collection<User> users = findMany(QUERY_FOR_ALL_USERS);
        return friendToUser(users);
    }

    @Override
    public User getUserById(Integer id) {
        User user = findOne(QUERY_FOR_USER_BY_ID, id);
        if (user != null) {
            user.getFriends().addAll(friendshipRepository.getFriendsByUserId(id)
                    .stream()
                    .map(Friendship::getFriendId)
                    .toList());
        }
        return user;
    }

    @Override
    public User create(User user) {
        int id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void delete(Integer id) {
        delete(DELETE_QUERY, id);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        Collection<User> users = findMany(QUERY_FOR_COMMON_FRIENDS, id, otherId);
        return friendToUser(users);
    }

    public void addFriend(Integer id, Integer friendId) {
        friendshipRepository.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        friendshipRepository.deleteFriend(id, friendId);
    }

    public Collection<User> getAllUserFriends(Integer id) {
        Collection<User> users = findMany(QUERY_FOR_USER_FRIENDS, id);
        return friendToUser(users);
    }

    private Collection<User> friendToUser(Collection<User> users) {
        if (!users.isEmpty()) {
            Collection<Friendship> friendships = friendshipRepository.getAllFriends();
            for (User user : users) {
                user.getFriends().addAll(friendships
                        .stream()
                        .filter(friendship -> friendship.getUserId().equals(user.getId()))
                        .map(Friendship::getFriendId)
                        .toList());
            }
        }
        return users;
    }
}