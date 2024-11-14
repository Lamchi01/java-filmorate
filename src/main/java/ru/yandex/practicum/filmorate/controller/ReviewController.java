package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review findById(@PathVariable long id) {
        log.info("Получен запрос на получение отзыва с ID {}", id);
        return reviewService.findById(id);
    }

    @GetMapping
    public List<Review> findByFilmId(@RequestParam Optional<Long> filmId, @RequestParam(defaultValue = "10") Long count) {
        log.info("Получен запрос на отзывов фильма с ID {}, количество отзывов {}", filmId, count);
        if (filmId.isPresent()) {
            return reviewService.findByFilmId(filmId.get(), count);
        }
        return reviewService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на создание отзыва к фильму с ID {} от пользователя с ID {}", review.getFilmId(), review.getUserId());
        reviewService.create(review);
        return review;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва c ID {} к фильму с ID {} от пользователя с ID {}",
                review.getReviewId(), review.getFilmId(), review.getUserId());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        log.info("Получен запрос на удаление отзыва с ID {}", id);
        reviewService.deleteById(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addReviewLike(@PathVariable Long id, @PathVariable long userId) {
        log.info("Получен запрос на добавление лайка к отзыву с ID {} от пользователя с ID {}", id, userId);
        reviewService.likeDislikeReview(id, userId, true);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void addReviewDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос на добавление дизлайка к отзыву с ID {} от пользователя с ID {}", id, userId);
        reviewService.likeDislikeReview(id, userId, false);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос на удаление лайка к отзыву с ID {} от пользователя с ID {}", id, userId);
        reviewService.deleteLikeDislikeReview(id, userId, true);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос на удаление дизлайка к отзыву с ID {} от пользователя с ID {}", id, userId);
        reviewService.deleteLikeDislikeReview(id, userId, false);
    }
}
