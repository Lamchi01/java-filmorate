package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface ReviewStorage extends BaseStorage<Review> {

    List<Review> findByFilmId(Long filmId, Long count);

    void likeDislikeReview(Review review, User user, boolean isLike);

    void deleteLikeDislikeReview(Review review, User user, boolean isLike);

    void updateReviewUseful(Review review);
}
