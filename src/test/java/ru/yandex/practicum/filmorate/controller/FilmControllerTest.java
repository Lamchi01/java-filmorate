package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    private final String url = "/films";
    private Film film1;
    private Film film2;
    private Film film3;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void beforeEach() throws Exception {
        film1 = new Film(null, "Film1", "desc1", LocalDate.of(1991, 1, 1), 110L, new Mpa(1L, "G"),
                new LinkedHashSet<>(), new LinkedHashSet<>(), new HashSet<>(), 0L);
        film1 = postFilm(film1);
        film2 = new Film(null, "Film2", "desc2", LocalDate.of(1992, 1, 1), 110L, new Mpa(2L, "PG"),
                new LinkedHashSet<>(), new LinkedHashSet<>(), new HashSet<>(), 0L);
        film2 = postFilm(film2);
        film3 = new Film(null, "Film3", "desc3", LocalDate.of(1993, 1, 1), 110L, new Mpa(3L, "PG-13"),
                new LinkedHashSet<>(), new LinkedHashSet<>(), new HashSet<>(), 0L);
        film3 = postFilm(film3);
    }

    @AfterEach
    public void afterEach() throws Exception {
        mvc.perform(delete(url));
    }

    @Test
    public void findAllShouldBeReturnAllFilms() throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(film1, film2, film3))));
    }

    @Test
    public void findAllShouldBeReturnEmptyListWhenNotFilms() throws Exception {
        mvc.perform(delete(url));

        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void createValidFilmShouldBeReturnFilmAndValidFilmId() throws Exception {
        afterEach();
        Film newFilm = new Film();
        newFilm.setName("Film1");
        newFilm.setDescription("desc1");
        newFilm.setReleaseDate(LocalDate.of(1994, 4, 4));
        newFilm.setDuration(140L);
        newFilm.setMpa(new Mpa(1L, "G"));

        MvcResult res = mvc.perform(post(url)
                        .content(mapper.writeValueAsString(newFilm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Film1"))
                .andExpect(jsonPath("$.description").value("desc1"))
                .andExpect(jsonPath("$.releaseDate").value("1994-04-04"))
                .andExpect(jsonPath("$.duration").value(140L))
                .andReturn();

        newFilm = updateFilmFromResponse(res);
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(newFilm))));
    }

    @Test
    public void updateFilmShouldBeReturnFilm() throws Exception {
        Film filmToUpdate = new Film(film2.getId(), "Film2 updated", "desc4 updated", LocalDate.of(2000, 1, 1), 30L, film2.getMpa(),
                new LinkedHashSet<>(), new LinkedHashSet<>(), null, 0L);

        mvc.perform(put(url)
                        .content(mapper.writeValueAsString(filmToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(filmToUpdate)));
    }

    @Test
    public void updateFilmWithNoChangesShouldBeReturnOldFilm() throws Exception {
        mvc.perform(put(url)
                        .content(mapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(film2)));
    }

    private Film postFilm(Film film) throws Exception {
        MvcResult res = mvc.perform(post(url).content(mapper.writeValueAsString(film)).contentType(MediaType.APPLICATION_JSON)).andReturn();
        String json = res.getResponse().getContentAsString();
        return mapper.readValue(json, Film.class);
    }

    private Film updateFilmFromResponse(MvcResult res) throws UnsupportedEncodingException, JsonProcessingException {
        String json = res.getResponse().getContentAsString();
        return mapper.readValue(json, Film.class);
    }
}
