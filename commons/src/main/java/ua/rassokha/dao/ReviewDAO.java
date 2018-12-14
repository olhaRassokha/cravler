package ua.rassokha.dao;


import ua.rassokha.domain.Review;

import java.util.List;

public interface ReviewDAO {
    default List<Review> getAll() {
        throw new UnsupportedOperationException("No realization of this method.");

    }

    default Review getById(int id) {
        throw new UnsupportedOperationException("No realization of this method.");
    }

    default void update(Review movie) {
        throw new UnsupportedOperationException("No realization of this method.");
    }

    default void delete(Review movie) {
        throw new UnsupportedOperationException("No realization of this method.");
    }

    List<Review> getAllByMovieId(int movieId);

    void updateAll(List<Review> reviews, int movie);

    List<Review> getPaginatedByMovieId(int movieId, int limit, int offset);
}
