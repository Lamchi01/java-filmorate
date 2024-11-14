package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.model.Event.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.Event.Operation.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final BaseStorage<User> userStorage;
    private final EventStorage eventStorage;

    public List<Review> findAll() {
        List<Review> reviews = reviewStorage.findAll();
        log.info("Обработан запрос на получение всех отзывов");
        return reviews;
    }

    public Review findById(Long id) {
        Review review = reviewStorage.findById(id);
        log.info("Обработан запрос на получение отзыва с ID {}", id);
        return review;
    }

    public List<Review> findByFilmId(Long id, Long count) {
        List<Review> reviews = reviewStorage.findByFilmId(id, count);
        log.info("Обработан запрос на получение отзывов фильма с ID {}, количество отзывов {}", id, count);
        return reviews;
    }

    public void create(Review review) {
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());
        reviewStorage.create(review);
        log.info("Создан отзыв к фильму с ID {} от пользователя с ID {}", review.getFilmId(), review.getUserId());
        eventStorage.addEvent(review.getUserId(), REVIEW, ADD, review.getReviewId());
    }

    public void deleteById(Long id) {
        Review review = reviewStorage.findById(id);
        reviewStorage.deleteById(id);
        log.info("Удален отзыв с ID {}", id);
        eventStorage.addEvent(review.getUserId(), REVIEW, REMOVE, review.getReviewId());
    }

    public Review update(Review review) {
        Review savedReview = reviewStorage.findById(review.getReviewId());

        if (review.getContent() != null) savedReview.setContent(review.getContent());
        if (review.getIsPositive() != null) savedReview.setIsPositive(review.getIsPositive());

        reviewStorage.update(savedReview);
        log.info("Обновлен отзыв c ID {} к фильму с ID {} от пользователя с ID {}",
                review.getReviewId(), review.getFilmId(), review.getUserId());
        eventStorage.addEvent(savedReview.getUserId(), REVIEW, UPDATE, savedReview.getReviewId());
        return savedReview;
    }

    public void deleteAllReviews() {
        reviewStorage.deleteAll();
        log.info("Удалены все отзывы");
    }

    public void likeDislikeReview(Long reviewId, Long userId, boolean isLike) {
        Review review = reviewStorage.findById(reviewId);
        User user = userStorage.findById(userId);
        reviewStorage.likeDislikeReview(review, user, isLike);
        reviewStorage.updateReviewUseful(review);
        log.info("Поставлен {} отзыву с ID {} от пользователя с ID {}", isLike ? "лайк" : "дизлайк", reviewId, userId);
    }

    public void deleteLikeDislikeReview(Long reviewId, Long userId, boolean isLike) {
        Review review = reviewStorage.findById(reviewId);
        User user = userStorage.findById(userId);
        reviewStorage.deleteLikeDislikeReview(review, user, isLike);
        reviewStorage.updateReviewUseful(review);
        log.info("Удален {} у отзыва с ID {} от пользователя с ID {}", isLike ? "лайк" : "дизлайк", reviewId, userId);
    }
}
