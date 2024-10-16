package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.User;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private final String url = "/users";
    private User user1;
    private User user2;
    private User user3;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void beforeEach() throws Exception {
        user1 = new User(null, "email1@mail.ru", "login1", "user1", LocalDate.of(1980, 1, 1), null);
        user1 = postUser(user1);
        user2 = new User(null, "email2@mail.ru", "login2", "user2", LocalDate.of(1980, 1, 2), null);
        user2 = postUser(user2);
        user3 = new User(null, "email3@mail.ru", "login3", "user3", LocalDate.of(1980, 1, 3), null);
        user3 = postUser(user3);
    }

    @AfterEach
    public void afterEach() throws Exception {
        mvc.perform(delete(url));
    }

    @Test
    public void findAllShouldBeReturnAllUsers() throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(user1, user2, user3))));
    }

    @Test
    public void findAllShouldBeReturnEmptyListWhenNotUsers() throws Exception {
        mvc.perform(delete(url));

        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void createUserShouldBeReturnUserAndValidUserId() throws Exception {
        afterEach();
        User newUser = new User();
        newUser.setName("User1");
        newUser.setEmail("email1@mail.ru");
        newUser.setLogin("login1");
        newUser.setBirthday(LocalDate.of(1980, 2, 2));

        MvcResult res = mvc.perform(post(url)
                        .content(mapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("email1@mail.ru"))
                .andExpect(jsonPath("$.login").value("login1"))
                .andExpect(jsonPath("$.birthday").value("1980-02-02"))
                .andReturn();

        newUser.setId(parseId(res));
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(newUser))));
    }

    @Test
    public void updateUserShouldBeReturnUser() throws Exception {
        User userToUpdate = new User(user2.getId(), "email2@mail.ru", "login1", "user1", LocalDate.of(1980, 1, 1), null);

        mvc.perform(put(url)
                        .content(mapper.writeValueAsString(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userToUpdate)));
    }

    @Test
    public void updateUserWithNoChangesIdShouldBeReturnOldUser() throws Exception {
        mvc.perform(put(url)
                        .content(mapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user2)));
    }

    public User postUser(User user) throws Exception {
        MvcResult res = mvc.perform(post(url).content(mapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON)).andReturn();
        String json = res.getResponse().getContentAsString();
        return mapper.readValue(json, User.class);
    }

    public long parseId(MvcResult res) throws UnsupportedEncodingException {
        return JsonPath.parse(res.getResponse().getContentAsString()).read("$.id", Long.class);
    }
}
