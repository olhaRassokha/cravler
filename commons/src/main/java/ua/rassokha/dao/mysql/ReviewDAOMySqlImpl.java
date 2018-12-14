package ua.rassokha.dao.mysql;

import ua.rassokha.dao.ConnectionPool;
import ua.rassokha.dao.ReviewDAO;
import ua.rassokha.domain.Review;
import ua.rassokha.domain.Sentiment;
import ua.rassokha.exeption.SenliSQLExeption;
import ua.rassokha.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOMySqlImpl implements ReviewDAO {
    private ConnectionPool connectionPool;

    ReviewDAOMySqlImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Connection getConnection() {
        return connectionPool.retrieve();
    }

    @Override
    public List<Review> getAllByMovieId(int movieId) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        List<Review> reviewList = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(
                    Constants.SELECT_FROM_REVIEW_WHERE_MOVIE_ID);

            preparedStatement.setInt(1, movieId);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Review current = new Review();
                current.setReviewText(result.getString("text"));
                current.setSentiment(Sentiment.valueOf(result.getString("sentiment")));
                reviewList.add(current);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return reviewList;
    }

    @Override
    public void updateAll(List<Review> reviews, int movie) {
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(Constants.INSERT_INTO_REVIEW_MOVIE_ID_TEXT_SENTIMENT_VALUES);
            for (Review review : reviews) {
                if (review.getId() == null) {
                    preparedStatement.setInt(1, movie);
                    preparedStatement.setString(2, review.getReviewText());
                    preparedStatement.setString(3, review.getSentiment().name());
                    preparedStatement.addBatch();
                }
            }
            preparedStatement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
    }

    @Override
    public List<Review> getPaginatedByMovieId(int movieId, int limit, int offset) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        List<Review> reviewList = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(
                    Constants.SELECT_FROM_REVIEW_WHERE_MOVIE_ID_ORDER_BY_ID_LIMIT_OFFSET);

            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, offset);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Review current = new Review();
                current.setReviewText(result.getString("text"));
                current.setSentiment(Sentiment.valueOf(result.getString("sentiment")));
                reviewList.add(current);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return reviewList;

    }
}
