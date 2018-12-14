package ua.rassokha.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.rassokha.di.Context;
import ua.rassokha.servlets.SentiContainer.Methods;
import ua.rassokha.servlets.SentiContainer.anotations.SentiController;
import ua.rassokha.servlets.SentiContainer.anotations.SentiPathVariable;
import ua.rassokha.servlets.SentiContainer.anotations.SentiRequestVariable;
import ua.rassokha.servlets.SentiContainer.anotations.SentyMapping;
import ua.rassokha.dao.DAOFactory;
import ua.rassokha.domain.Job;
import ua.rassokha.domain.Movie;
import ua.rassokha.domain.Status;
import ua.rassokha.util.Constants;


@SentiController
public class SentymnetRestController {
    private static final Logger logger = LogManager.getLogger();

    @SentyMapping(path = "/get")
    public Movie getByJobId(@SentiRequestVariable("jobId") Integer id) {
        return DAOFactory.getDAOFactory(Constants.MY_SQL).getMovieDAO().getByJobId(id);
    }

    @SentyMapping(path = "/put", method = Methods.PUT)
    public int putJob(@SentiRequestVariable("site") String site, @SentiRequestVariable("movie") String movie) {
        Job job = DAOFactory.getDAOFactory(Constants.MY_SQL).getJobDAO().update(new Job(site, movie, Status.SUBMITED));
        return job.getId();
    }
    @SentyMapping(path = "/get/paginated")
    public Movie getPaginatedByJobId(@SentiRequestVariable("jobId") Integer id, @SentiRequestVariable("limit") Integer limit, @SentiRequestVariable("offset") Integer offset) {
        System.out.println(Context.getBean(SentymnetRestController.class));
        return DAOFactory.getDAOFactory(Constants.MY_SQL).getMovieDAO().getPaginatedByJobId(id, limit, offset);
    }
    @SentyMapping(path = "/movie/{id}")
    public Movie getMovie(@SentiPathVariable("id") Integer id) {
        return DAOFactory.getDAOFactory(Constants.MY_SQL).getMovieDAO().getById(id);
    }

}
