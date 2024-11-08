package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable long id) {
        return reviewService.findById(id);
    }

    @GetMapping
    public List<Review> findByFilmId(@RequestParam Optional<Long> filmId, @RequestParam(defaultValue = "10") Long count) {
        if (filmId.isPresent()) {
            return reviewService.findByFilmId(filmId.get(), count);
        }
        return reviewService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@Valid @RequestBody Review review) {
        reviewService.create(review);
        return review;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        reviewService.deleteById(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addReviewLike(@PathVariable Long id, @PathVariable long userId) {
        reviewService.likeReview(id, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void addReviewDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.dislikeReview(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteLikeReview(id, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteDislikeReview(id, userId);
    }
}
