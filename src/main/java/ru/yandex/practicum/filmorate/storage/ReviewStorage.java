package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface ReviewStorage extends BaseStorage<Review> {

    List<Review> findByFilmId(Long filmId, Long count);

    void delete(Review review);

    void likeReview(Review review, User user);

    void dislikeReview(Review review, User user);

    void deleteLikeReview(Review review, User user);

    void deleteDislikeReview(Review review, User user);
}
