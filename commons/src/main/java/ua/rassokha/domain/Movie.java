package ua.rassokha.domain;

import java.util.List;

public class Movie {
    private Integer id;
    private String duration;
    private String name;
    private Integer job;
    private List<Review> reviews;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Integer getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", duration='" + duration + '\'' +
                ", name='" + name + '\'' +
                ", job=" + job +
                ", reviews=" + reviews +
                '}';
    }
}
