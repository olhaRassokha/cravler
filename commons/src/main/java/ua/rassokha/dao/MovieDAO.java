package ua.rassokha.dao;

import ua.rassokha.domain.Movie;

import java.sql.Connection;
import java.util.List;

public interface MovieDAO {
    List<Movie> getAll();

    Movie getById(int id);

    Movie getByJobId(int id);

    Movie getPaginatedByJobId(int id, int limit, int offset);

    void update(Movie movie);

    default Movie delete(Movie movie) {
        throw new UnsupportedOperationException("No realization of this method.");

    }

    default Movie deleteById(int id) {
        throw new UnsupportedOperationException("No realization of this method.");

    }

    Connection getConnection();
}
