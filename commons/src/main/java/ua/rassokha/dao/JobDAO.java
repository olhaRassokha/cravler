package ua.rassokha.dao;

import ua.rassokha.domain.Job;
import ua.rassokha.domain.Status;

import java.sql.Connection;
import java.util.List;

public interface JobDAO {

    List<Job> getAllByStatus(Status status);

    Job update(Job job);

    Connection getConnection();
}
