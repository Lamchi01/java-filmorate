package ru.yandex.practicum.filmorate.model.enums;

public enum FilmFilters {
    DIRECTOR, TITLE, DIRECTOR_AND_TITLE, TITLE_AND_DIRECTOR;

    public static FilmFilters from(String by) {
        return switch (by.toLowerCase()) {
            case "director" -> DIRECTOR;
            case "title" -> TITLE;
            case "director,title" -> DIRECTOR_AND_TITLE;
            case "title,director" -> TITLE_AND_DIRECTOR;
            default -> null;
        };
    }
}
