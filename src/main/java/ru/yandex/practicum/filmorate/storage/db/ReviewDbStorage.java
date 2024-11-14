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
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ? ORDER BY useful DESC";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews " +
            "SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM reviews";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String UPDATE_REVIEW_LIKE_QUERY = "MERGE INTO reviews_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
    private static final String DELETE_REVIEW_LIKE_QUERY = "DELETE FROM reviews_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = ?";
    private static final String FIND_REVIEW_USEFUL_QUERY = "SELECT SUM(CASE WHEN is_like = TRUE THEN 1 ELSE -1 END) useful " +
            "FROM reviews_likes WHERE review_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Review> findAll() {
        log.info("Получение всех отзывов");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Review findById(Long id) {
        log.info("Получение отзыва с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Review with ID " + id + " not found"));
    }

    @Override
    public Review create(Review review) {
        log.info("Добавление нового отзывы");
        long id = insert(INSERT_QUERY, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful());
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        log.info("Обновление отзывы с ID: {}", review.getReviewId());
        update(UPDATE_QUERY, review.getContent(), review.getIsPositive(), review.getUseful(), review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public void deleteAll() {
        log.info("Удаление всех отзывов");
        removeAll(DELETE_ALL_QUERY);
    }

    @Override
    public void deleteById(long id) {
        log.info("Удаление отзыва с ID: {}", id);
        removeOne(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public List<Review> findByFilmId(Long filmId, Long count) {
        log.info("Получение отзывов фильма с ID {}", filmId);
        return findMany(FIND_BY_FILM_ID_QUERY, filmId, count);
    }

    @Override
    public void likeDislikeReview(Review review, User user, boolean isLike) {
        log.info("Добавление {} на отзыв с ID: {}", isLike ? "лайк" : "дизлайк", review.getReviewId());
        update(UPDATE_REVIEW_LIKE_QUERY, review.getReviewId(), review.getUserId(), isLike);
    }

    @Override
    public void deleteLikeDislikeReview(Review review, User user, boolean isLike) {
        log.info("Удаление {} у отзыва с ID: {}", isLike ? "лайк" : "дизлайк", review.getReviewId());
        removeOne(DELETE_REVIEW_LIKE_QUERY, review.getReviewId(), review.getUserId(), isLike);
    }

    @Override
    public void updateReviewUseful(Review review) {
        log.info("Обновление рейтинга полезности на отзыв с ID: {}", review.getReviewId());
        List<Long> useful = jdbc.queryForList(FIND_REVIEW_USEFUL_QUERY, Long.class, review.getReviewId());
        if (useful.isEmpty() || useful.getFirst() == null) {
            review.setUseful(0L);
        } else {
            review.setUseful(useful.getFirst());
        }
        update(review);
    }
}
