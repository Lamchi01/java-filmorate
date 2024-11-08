package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.db.EventDbStorage;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final BaseStorage<User> userStorage;
    private final EventDbStorage eventStorage;

    public List<Review> findAll() {
        return reviewStorage.findAll();
    }

    public Review findById(Long id) {
        return reviewStorage.findById(id);
    }

    public List<Review> findByFilmId(Long id, Long count) {
        return reviewStorage.findByFilmId(id, count);
    }

    public void create(Review review) {
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());
        reviewStorage.create(review);
        Event event = Event.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("ADD")
                .entityId(review.getReviewId())
                .build();
        eventStorage.addEvent(event);
        log.trace("Создано событие добавления review - {}", event);
    }

    public void deleteById(Long id) {
        Review review = reviewStorage.findById(id);
        reviewStorage.deleteById(id);
        Event event = Event.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("REMOVE")
                .entityId(review.getReviewId())
                .build();
        eventStorage.addEvent(event);
        log.trace("Создано событие удаления review - {}", event);
    }

    public Review update(Review review) {
        Review savedReview = reviewStorage.findById(review.getReviewId());

        if (review.getContent() != null) savedReview.setContent(review.getContent());
        if (review.getIsPositive() != null) savedReview.setIsPositive(review.getIsPositive());
        if (review.getUserId() != null) {
            userStorage.findById(review.getUserId());
            savedReview.setUserId(review.getUserId());
        }
        if (review.getFilmId() != null) {
            filmStorage.findById(review.getFilmId());
            savedReview.setFilmId(review.getFilmId());
        }
        if (review.getUseful() != null) savedReview.setUseful(review.getUseful());

        reviewStorage.update(review);
        Event event = Event.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("UPDATE")
                .entityId(review.getReviewId())
                .build();
        eventStorage.addEvent(event);
        log.trace("Создано событие обновления review - {}", event);
        return review;
    }

    public void deleteAllReviews() {
        reviewStorage.deleteAll();
    }

    public void likeReview(Long reviewId, Long userId) {
        Review review = reviewStorage.findById(reviewId);
        User user = userStorage.findById(userId);
        reviewStorage.likeReview(review, user);
    }

    public void dislikeReview(Long reviewId, Long userId) {
        Review review = reviewStorage.findById(reviewId);
        User user = userStorage.findById(userId);
        reviewStorage.dislikeReview(review, user);
    }

    public void deleteLikeReview(Long reviewId, Long userId) {
        Review review = reviewStorage.findById(reviewId);
        User user = userStorage.findById(userId);
        reviewStorage.deleteLikeReview(review, user);
    }

    public void deleteDislikeReview(Long reviewId, Long userId) {
        Review review = reviewStorage.findById(reviewId);
        User user = userStorage.findById(userId);
        reviewStorage.deleteDislikeReview(review, user);
    }
}
