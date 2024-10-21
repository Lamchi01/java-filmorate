package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class,
        UserRowMapper.class,
        FriendshipRepository.class,
        FriendshipRowMapper.class})
class UserRepositoryTest {
    private final UserRepository userRepository;

    @Test
    void getUsers() {

        assertThat(userRepository.getUsers()).isNotEmpty();
    }

    @Test
    void getUserById() {
        assertThat(userRepository.getUserById(1)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void create() {
        User user = User.builder()
                .email("test@example.com")
                .name("Test User")
                .login("TestUser")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userRepository.create(user);

        assertThat(userRepository.getUserById(1)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void update() {
        User updatedUser = User.builder()
                .id(1)
                .email("test@example.com")
                .login("TestUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThat(userRepository.getUserById(1)).hasFieldOrPropertyWithValue("name", "name1");
        userRepository.update(updatedUser);
        assertThat(userRepository.getUserById(1)).hasFieldOrPropertyWithValue("name", "Test User");
    }

    @Test
    void delete() {
        userRepository.delete(1);
        assertThat(userRepository.getUserById(1)).isNull();
    }

    @Test
    void getCommonFriends() {
        assertThat(userRepository.getCommonFriends(1, 2)).contains(userRepository.getUserById(3));
    }

    @Test
    void addFriend() {
        userRepository.addFriend(4, 5);
        assertThat(userRepository.getUserById(4).getFriends()).contains(userRepository.getUserById(5).getId());
        assertThat(userRepository.getUserById(5).getFriends()).doesNotContain(userRepository.getUserById(4).getId());
    }

    @Test
    void deleteFriend() {
        userRepository.deleteFriend(1, 2);
        assertThat(userRepository.getUserById(1).getFriends()).doesNotContain(userRepository.getUserById(2).getId());
    }

    @Test
    void getAllUserFriends() {
        Collection<User> users = userRepository.getAllUserFriends(1);
        assertThat(users).containsAll(List.of(userRepository.getUserById(2), userRepository.getUserById(3)));
    }
}