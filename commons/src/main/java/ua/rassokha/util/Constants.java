package ua.rassokha.util;

public class Constants {
    public static final int N_THREADS = 6;
    public static final int N_DIV_THREADS = 3;
    public static final int MY_SQL = 1;
    public static final int DEFAULT_PAGE_QUONTITY = 1;
    public static final double BAD_STEP = 0.001;
    public static final double NORMAL_STEP = 0.2;
    public static final int EXECUTION_PERIOD = 10;

    public static final String CONFIG_FILE = "config.properties";

    public static final String DICTIONARY_LOCATION = "file.location.dictionary";
    public static final String STOP_WORDS_LOCATION = "file.location.stopwords";

    public static final String PACKAGE_SCAN = "package.scan";

    public static final String DATASOURCE_DRIVER = "datasource.driver";
    public static final String DATASOURCE_PASSWORD = "datasource.password";
    public static final String DATASOURCE_URL = "datasource.url";
    public static final String DATASOURCE_USER = "datasource.user";


    public static final String SELECT_FROM_MOVIE = "SELECT * FROM movie";
    public static final String SELECT_FROM_MOVIE_WHERE_ID = "SELECT * FROM movie WHERE id =?";
    public static final String INSERT_INTO_MOVIE_NAME_DURATION_DIRECTOR_VALUES = "INSERT INTO movie (name, duration, director, job_id) VALUES (?,?,?,?)";
    public static final String DELETE_FROM_MOVIE_WHERE_ID = "DELETE FROM movie WHERE id =?";
    public static final String SELECT_FROM_REVIEW_WHERE_MOVIE_ID = "SELECT * FROM review WHERE movie_id = ?";
    public static final String INSERT_INTO_REVIEW_MOVIE_ID_TEXT_SENTIMENT_VALUES = "INSERT INTO review (movie_id, text, sentiment) VALUES (?, ?, ?)";
    public static final String SELECT_BY_STATUS = "SELECT * FROM job where status = ? GROUP BY site_name, id;";
    public static final String INSERT_INTO_JOB = "INSERT into job (movie_name, site_name, status) values (?,?,?)";
    public static final String SELECT_FROM_MOVIE_WHERE_JOB_ID = "SELECT * FROM movie WHERE job_id =?";
    public static final String SELECT_FROM_REVIEW_WHERE_MOVIE_ID_ORDER_BY_ID_LIMIT_OFFSET = "SELECT   *  FROM review WHERE movie_id=? ORDER BY Id LIMIT ? OFFSET ?";
}
