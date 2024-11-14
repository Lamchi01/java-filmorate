package ru.yandex.practicum.filmorate.model.enums;

public enum FilmSortOrder {
    YEAR, LIKES;

    public static FilmSortOrder from(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "year" -> YEAR;
            case "likes" -> LIKES;
            default -> null;
        };
    }
}
