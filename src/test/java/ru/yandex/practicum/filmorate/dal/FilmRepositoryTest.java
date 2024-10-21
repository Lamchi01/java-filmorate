package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class,
        FilmRowMapper.class,
        LikesRepository.class,
        LikesRowMapper.class,
        GenresRepository.class,
        GenresRowMapper.class,
        MpaRepository.class,
        MpaRowMapper.class,
        GenreRepository.class,
        GenreRowMapper.class})
class FilmRepositoryTest {
    private final FilmRepository filmRepository;

    @Test
    void getFilms() {
        assertThat(filmRepository.getFilms()).isNotEmpty();
    }

    @Test
    void getFilmById() {
        assertThat(filmRepository.getFilmById(1)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void getTopFilms() {
        assertThat(filmRepository.getTopFilms(10)).isNotEmpty();
    }

    @Test
    void create() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .genres(Set.of(new Genre(1, "Комедия")))
                .build();

        filmRepository.create(film);

        assertThat(film).hasFieldOrPropertyWithValue("id", 6);
    }

    @Test
    void update() {
Film updatedFilm = Film.builder()
                .id(1)
                .name("Updated Film")
                .description("Updated description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .genres(Set.of(new Genre(1, "Комедия")))
                .likes(Set.of(1,2))
                .build();
        assertThat(filmRepository.getFilmById(1)).hasFieldOrPropertyWithValue("name", "Фильм 1");
        filmRepository.update(updatedFilm);
        assertThat(filmRepository.getFilmById(1)).hasFieldOrPropertyWithValue("name", "Updated Film");
    }

    @Test
    void delete() {
        filmRepository.delete(1);
        assertThat(filmRepository.getFilmById(1)).isNull();
    }

    @Test
    void addLike() {
        filmRepository.addLike(1, 3);
        assertThat(filmRepository.getFilmById(1).getLikes()).contains(3);
    }

    @Test
    void deleteLike() {
        filmRepository.deleteLike(1, 2);
        assertThat(filmRepository.getFilmById(1).getLikes().contains(2)).isFalse();
    }
}