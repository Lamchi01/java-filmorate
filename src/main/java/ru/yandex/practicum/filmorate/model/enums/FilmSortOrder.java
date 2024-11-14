package ru.yandex.practicum.filmorate.model.enums;

public enum FilmSortOrder {
    YEAR, LIKES;

    public static FilmSortOrder from(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "year":
                return YEAR;
            case "likes":
                return LIKES;
            default:
                return null;
        }
    }
}
