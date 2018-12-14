package ua.rassokha.dao.mysql;

import ua.rassokha.dao.*;
import ua.rassokha.util.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlDbDAOFactory extends DAOFactory {
    private ConnectionPool connectionPool;


    public MySqlDbDAOFactory() {
        connectionPool = new ConnectionPool(Configuration.getConfiguration().getDataSourse().getURL(),
                Configuration.getConfiguration().getDataSourse().getUSER(), Configuration.getConfiguration().getDataSourse().getPASSWORD(),
                Configuration.getConfiguration().getDataSourse().getDRIVER(), 1);
    }

    protected Connection getConnection() {
        return connectionPool.retrieve();
    }

    public MovieDAO getMovieDAO() {
        return new MovieDAOMySqlImpl(connectionPool);
    }

    public ReviewDAO getReviewDAO() {
        return new ReviewDAOMySqlImpl(connectionPool);
    }

    @Override
    public JobDAO getJobDAO() {
        return new JobDAOMySqlImpl(connectionPool);
    }

    public void closeConnection(Connection connection) {
        try {
            connectionPool.closeAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
