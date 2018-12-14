import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.rassokha.di.Context;
import ua.rassokha.service.runnable.SentimentReviewAnalyzeApplication;
import ua.rassokha.dao.DAOFactory;
import ua.rassokha.exeption.NoXMLConfigurationFile;
import ua.rassokha.domain.Job;
import ua.rassokha.domain.Status;
import ua.rassokha.util.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = LogManager.getLogger();
    private static String site = "rottentomatoes";

    public static void main(String... args) {
        SentiContext.initContext();
        Context.initContext();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("Checking site " + site);
                List<Job> jobs = chekForNew();
                if (jobs.size() > 0) {
                    logger.info("Submitted jobs are found " + jobs);
                    for (Job job : jobs) {
                        logger.info(job);
                        if (!job.getSiteName().equals(site)) {
                            logger.info("Reloading contecst for site " + job.getSiteName());
                            try {
                                SentiContext.reloadDependenciesForSite(job.getSiteName());
                                site = job.getSiteName();
                            }catch (NoXMLConfigurationFile e){
                                logger.error("No such site dependencies: " + job.getSiteName());
                                job.setStatus(Status.ABANDONED);
                                DAOFactory.getDAOFactory(Constants.MY_SQL).getJobDAO().update(job);
                                return;
                            }
                        }
                            job.setStatus(Status.PROCESSING);
                            run(job);
                    }
                } else {
                    logger.info("No jobs submitted");
                }
            }

            private List<Job> chekForNew() {
                return DAOFactory.getDAOFactory(Constants.MY_SQL).getJobDAO().getAllByStatus(Status.SUBMITED);
            }
            void run(Job... args) {
                Arrays.stream(args).forEach(e -> {
                    try {
                        SentimentReviewAnalyzeApplication sentimentReviewAnalyzeApplication =
                                (SentimentReviewAnalyzeApplication) Context.getBean(SentimentReviewAnalyzeApplication.class);
                        sentimentReviewAnalyzeApplication.setJob(e);
                        Thread movie = new Thread(sentimentReviewAnalyzeApplication);
                        movie.start();
                    }catch (Exception e1){
                        logger.error(" ", e1);
                    }
                });
            }
        }, 0, Constants.EXECUTION_PERIOD, TimeUnit.SECONDS);

    }

}
