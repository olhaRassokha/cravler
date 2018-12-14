package ua.rassokha.dao.mysql;

import ua.rassokha.dao.ConnectionPool;
import ua.rassokha.dao.DAOFactory;
import ua.rassokha.dao.MovieDAO;
import ua.rassokha.dao.ReviewDAO;
import ua.rassokha.domain.Movie;
import ua.rassokha.exeption.SenliSQLExeption;
import ua.rassokha.util.Constants;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDAOMySqlImpl implements MovieDAO {
    ConnectionPool connectionPool;
    private ReviewDAO reviewDAO = DAOFactory.getDAOFactory(Constants.MY_SQL).getReviewDAO();

    MovieDAOMySqlImpl() throws IOException {
    }

    public MovieDAOMySqlImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Connection getConnection() {
        return connectionPool.retrieve();
    }

    @Override
    public List<Movie> getAll() {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        List<Movie> movies = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.SELECT_FROM_MOVIE);
            ResultSet result = preparedStatement.executeQuery();

            Movie movie = new Movie();
            while (result.next()) {
                movie.setName(result.getString("name"));
                movie.setDuration(result.getString("duration"));
                movie.setReviews(reviewDAO.getAllByMovieId(result.getInt("id")));
                movies.add(movie);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return movies;
    }

    @Override
    public Movie getById(int id) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        Movie movie = null;
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.SELECT_FROM_MOVIE_WHERE_ID);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                movie = new Movie();
                movie.setName(result.getString("name"));
                movie.setDuration(result.getString("duration"));
                movie.setReviews(reviewDAO.getAllByMovieId(result.getInt("id")));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return movie;
    }

    @Override
    public Movie getByJobId(int id) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        Movie movie = null;
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.SELECT_FROM_MOVIE_WHERE_JOB_ID);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                movie = new Movie();
                movie.setName(result.getString("name"));
                movie.setDuration(result.getString("duration"));
                movie.setReviews(reviewDAO.getAllByMovieId(result.getInt("id")));
                movie.setJob(id);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return movie;
    }
    @Override
    public Movie getPaginatedByJobId(int id, int limit, int offset) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        Movie movie = null;
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.SELECT_FROM_MOVIE_WHERE_JOB_ID);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                movie = new Movie();
                movie.setName(result.getString("name"));
                movie.setDuration(result.getString("duration"));
                movie.setReviews(reviewDAO.getPaginatedByMovieId(result.getInt("id"), limit, offset));
                movie.setJob(id);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return movie;
    }

    @Override
    public void update(Movie movie) {
        if (movie.getId() == null) {
            save(movie);
        }
    }

    private void save(Movie movie) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.INSERT_INTO_MOVIE_NAME_DURATION_DIRECTOR_VALUES);
            preparedStatement.setString(1, movie.getName());
            preparedStatement.setString(2, movie.getDuration());
            preparedStatement.setString(3, "N/A");
            preparedStatement.setInt(4, movie.getJob());
            int affectedRows = preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    movie.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating failed, no ID obtained.");
                }
            }
            reviewDAO.updateAll(movie.getReviews(), movie.getId());
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
    }

    @Override
    public Movie delete(Movie movie) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.DELETE_FROM_MOVIE_WHERE_ID);
            preparedStatement.setInt(1, movie.getId());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return movie;
    }

    @Override
    public Movie deleteById(int id) {
        Connection connection = getConnection();
        Movie movie = getById(id);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.DELETE_FROM_MOVIE_WHERE_ID);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return movie;
    }
}
