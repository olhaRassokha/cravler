package ua.rassokha.dao.mysql;

import ua.rassokha.dao.ConnectionPool;
import ua.rassokha.dao.JobDAO;
import ua.rassokha.domain.Job;
import ua.rassokha.domain.Status;
import ua.rassokha.exeption.SenliSQLExeption;
import ua.rassokha.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobDAOMySqlImpl implements JobDAO {
    ConnectionPool connectionPool;

    public JobDAOMySqlImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Job> getAllByStatus(Status status) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        List<Job> jobs = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.SELECT_BY_STATUS);
            preparedStatement.setString(1, status.name());
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Job job = new Job();
                job.setId(result.getInt("id"));
                job.setMovieName(result.getString("movie_name"));
                job.setSiteName(result.getString("site_name"));
                job.setStatus(status);
                jobs.add(job);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return jobs;
    }

    @Override
    public Job update(Job job) {
        if (job.getId() == null) {
            return save(job);
        } else {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        "UPDATE job SET status=? WHERE id = ?");
                preparedStatement.setString(1, job.getStatus().name());
                preparedStatement.setInt(2, job.getId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                throw new SenliSQLExeption(e);
            } finally {
                connectionPool.putback(connection);
            }
        }

        return job;
    }

    private Job save(Job job) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    Constants.INSERT_INTO_JOB);
            preparedStatement.setString(1, job.getMovieName());
            preparedStatement.setString(2, job.getSiteName());
            preparedStatement.setString(3, job.getStatus().name());
            int affectedRows = preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    job.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating failed, no ID obtained.");
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new SenliSQLExeption(e);
        } finally {
            connectionPool.putback(connection);
        }
        return job;
    }

    @Override
    public Connection getConnection() {
        return connectionPool.retrieve();
    }

}
