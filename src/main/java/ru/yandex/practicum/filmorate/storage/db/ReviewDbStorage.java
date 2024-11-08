package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
    private static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews " +
            "SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? WHERE review_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM reviews";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String UPDATE_REVIEW_LIKE_QUERY = "MERGE INTO reviews_likes (review_id, user_id, is_like)" +
            "VALUES (?, ?, ?)";
    private static final String DELETE_REVIEW_LIKE_QUERY = "DELETE FROM reviews_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = ?";
    private static final String FIND_REVIEW_USEFUL_QUERY = "SELECT SUM(CASE WHEN is_like = TRUE THEN 1 ELSE -1 END) useful " +
            "FROM reviews_likes WHERE review_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Review> findAll() {
        log.trace("Получен запрос на получение всех отзывов");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Review findById(Long id) {
        log.trace("Получен запрос на получение отзыва с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Review with ID " + id + " not found"));
    }

    @Override
    public Review create(Review review) {
        long id = insert(INSERT_QUERY, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful());
        review.setReviewId(id);
        log.trace("Добавлен новый отзыв с ID: {}", review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        update(UPDATE_QUERY, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful(), review.getReviewId());
        log.trace("Обновлен отзыв с ID: {}", review.getReviewId());
        return review;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.trace("Удалены все отзывы");
    }

    @Override
    public void delete(Review review) {
        removeOne(DELETE_BY_ID_QUERY, review.getReviewId());
        log.trace("Удален отзыв с ID: {}", review.getReviewId());
    }

    @Override
    public List<Review> findByFilmId(Long filmId, Long count) {
        log.trace("Получен запрос на получение отзывов фильма с ID {}", filmId);
        return findMany(FIND_BY_FILM_ID_QUERY, filmId, count);
    }

    @Override
    public void likeReview(Review review, User user) {
        update(UPDATE_REVIEW_LIKE_QUERY, review.getReviewId(), review.getUserId(), Boolean.TRUE);
        log.trace("Добавлен лайк на отзыв с ID: {}", review.getReviewId());
        updateReviewUseful(review);
    }

    @Override
    public void dislikeReview(Review review, User user) {
        update(UPDATE_REVIEW_LIKE_QUERY, review.getReviewId(), review.getUserId(), Boolean.FALSE);
        log.trace("Добавлен дизлайк на отзыв с ID: {}", review.getReviewId());
        updateReviewUseful(review);
    }

    @Override
    public void deleteLikeReview(Review review, User user) {
        removeOne(DELETE_REVIEW_LIKE_QUERY, review.getReviewId(), review.getUserId(), Boolean.TRUE);
        log.trace("Удален лайк у отзыва с ID: {}", review.getReviewId());
        updateReviewUseful(review);
    }

    @Override
    public void deleteDislikeReview(Review review, User user) {
        removeOne(DELETE_REVIEW_LIKE_QUERY, review.getReviewId(), review.getUserId(), Boolean.FALSE);
        log.trace("Удален дизлайк у отзыва с ID: {}", review.getReviewId());
        updateReviewUseful(review);
    }

    private void updateReviewUseful(Review review) {
        Long useful = jdbc.queryForObject(FIND_REVIEW_USEFUL_QUERY, Long.class, review.getReviewId());
        if (useful == null) useful = 0L;
        if (!useful.equals(review.getUseful())) {
            review.setUseful(useful);
            update(review);
            log.trace("Обновлен рейтинг полезности на отзыв с ID: {}", review.getReviewId());
        }
    }
}
