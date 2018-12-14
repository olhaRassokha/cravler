package ua.rassokha.domain;

public class Job {
    private Integer id;
    private String siteName;
    private String movieName;
    private Status status;

    public Job() {
    }

    public Job(String siteName, String movieName, Status status) {
        this.siteName = siteName;
        this.movieName = movieName;
        this.status = status;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", siteName='" + siteName + '\'' +
                ", movieName='" + movieName + '\'' +
                ", status=" + status +
                '}';
    }
}
