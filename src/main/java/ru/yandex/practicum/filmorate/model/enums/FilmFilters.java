package ru.yandex.practicum.filmorate.model.enums;

public enum FilmFilters {
    DIRECTOR, TITLE, DIRECTOR_AND_TITLE, TITLE_AND_DIRECTOR;

    public static FilmFilters from(String by) {
        switch (by.toLowerCase()) {
            case "director":
                return DIRECTOR;
            case "title":
                return TITLE;
            case "director,title":
                return DIRECTOR_AND_TITLE;
            case "title,director":
                return TITLE_AND_DIRECTOR;
            default:
                return null;
        }
    }
}
