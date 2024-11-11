package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
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
    private final EventService eventService;

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
        eventService.addEvent(review.getUserId(), REVIEW, ADD, review.getReviewId());
    }

    public void deleteById(Long id) {
        Review review = reviewStorage.findById(id);
        reviewStorage.deleteById(id);
        eventService.addEvent(review.getUserId(), REVIEW, REMOVE, review.getReviewId());
    }

    public Review update(Review review) {
        Review savedReview = reviewStorage.findById(review.getReviewId());

        if (review.getContent() != null) savedReview.setContent(review.getContent());
        if (review.getIsPositive() != null) savedReview.setIsPositive(review.getIsPositive());

        reviewStorage.update(savedReview);
        eventService.addEvent(savedReview.getUserId(), REVIEW, UPDATE, savedReview.getReviewId());
        return savedReview;
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
