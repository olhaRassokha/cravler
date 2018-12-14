package ua.rassokha.dao;

import ua.rassokha.dao.mysql.MySqlDbDAOFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static ua.rassokha.util.Constants.MY_SQL;

public abstract class DAOFactory {

    // List of DAO types supported by the factory

    // There will be a method for each DAO that can be
    // created. The concrete factories will have to

    public static DAOFactory getDAOFactory(
            int whichFactory) {
        switch (whichFactory) {
            case MY_SQL:
                return new MySqlDbDAOFactory();
            default:
                throw new IllegalArgumentException("No such factory realization.");
        }
    }

    public abstract ReviewDAO getReviewDAO();

    public abstract JobDAO getJobDAO();

    public abstract MovieDAO getMovieDAO();

    protected abstract Connection getConnection() throws IOException, SQLException;

    public abstract void closeConnection(Connection connection) throws SQLException;
}

