package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    User user1;
    User user2;
    User user3;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void beforeEach() throws Exception {
        user1 = new User(null, "email1@mail.ru", "login1", "user1", LocalDate.of(1980, 1, 1));
        mvc.perform(post("/users").content(mapper.writeValueAsString(user1)).contentType(MediaType.APPLICATION_JSON));
        user2 = new User(null, "email2@mail.ru", "login2", "user2", LocalDate.of(1980, 1, 2));
        mvc.perform(post("/users").content(mapper.writeValueAsString(user2)).contentType(MediaType.APPLICATION_JSON));
        user3 = new User(null, "email3@mail.ru", "login3", "user3", LocalDate.of(1980, 1, 3));
        mvc.perform(post("/users").content(mapper.writeValueAsString(user3)).contentType(MediaType.APPLICATION_JSON));
        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);
    }

    @AfterEach
    public void afterEach() throws Exception {
        mvc.perform(delete("/users"));
    }

    @Test
    public void findAllShouldBeReturnAllUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(user1, user2, user3))));
    }

    @Test
    public void addUserShouldBeReturnUserAndValidUserId() throws Exception {
        afterEach();
        User newUser = new User();
        newUser.setName("User1");
        newUser.setEmail("email1@mail.ru");
        newUser.setLogin("login1");
        newUser.setBirthday(LocalDate.of(1980, 2, 2));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("email1@mail.ru"))
                .andExpect(jsonPath("$.login").value("login1"))
                .andExpect(jsonPath("$.birthday").value("1980-02-02"));

        newUser.setId(1L);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(newUser))));
    }

    @Test
    public void updateUserShouldBeReturnUser() throws Exception {
        User userToUpdate = new User(2L, "email2@mail.ru", "login1", "user1", LocalDate.of(1980, 1, 1));

        mvc.perform(put("/users")
                        .content(mapper.writeValueAsString(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userToUpdate)));
    }
}
