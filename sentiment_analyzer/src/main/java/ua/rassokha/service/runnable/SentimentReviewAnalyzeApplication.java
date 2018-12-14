package ua.rassokha.service.runnable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.rassokha.dao.DAOFactory;
import ua.rassokha.di.Context;
import ua.rassokha.di.Scopes;
import ua.rassokha.di.anotations.SentiComponent;
import ua.rassokha.di.anotations.SentiInject;
import ua.rassokha.domain.*;
import ua.rassokha.domain.collections.BlockingQueue;
import ua.rassokha.exeption.HtmlReviewPageNotFound;
import ua.rassokha.service.MetaDataParser;
import ua.rassokha.service.rottentomatoes.HtmlReviewReaderRottenTomatoes;
import ua.rassokha.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SentiComponent(scope = Scopes.PROTOTYPE)
public class SentimentReviewAnalyzeApplication implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    Job job;
    @SentiInject
    private MetaDataParser metaDataParser;

    public void setJob(Job job) {
        this.job = job;
    }

    private Movie start(int pool) throws InterruptedException, ExecutionException {
        BlockingQueue<HtmlPage> htmlPageBlockingQueue = new BlockingQueue<>();
        BlockingQueue<List<Review>> linkedReviewListBlockingQueue = new BlockingQueue<>();
        List<Review> reviews = new ArrayList<>();
        Movie movie = new Movie();
        movie.setJob(job.getId());
        int pageQuontity = 0;

        //TODO:chek info about film set N/A
        HtmlPage content = new HtmlReviewReaderRottenTomatoes().getHtmlPage(job.getMovieName());
        movie.setDuration(metaDataParser.parseDuration(content.getContent()));
        movie.setName(metaDataParser.parseName(content.getContent()));
        pageQuontity = metaDataParser.getPageQuontiy(content);

        ExecutorService readerExecutor = Executors.newFixedThreadPool(pool);
        for (int i = 1; i <= pageQuontity; i++) {
            Reader reader = (Reader) Context.getBean(Reader.class);
            reader.setQueue(htmlPageBlockingQueue);
            reader.setPage(i);
            reader.setMovieName(job.getMovieName());
            readerExecutor.submit(reader);
        }

        for (int i = 1; i <= pageQuontity / Constants.N_DIV_THREADS; i++) {
            Parser parser = (Parser) Context.getBean(Parser.class);
            parser.setQueue(htmlPageBlockingQueue);
            parser.setReview(linkedReviewListBlockingQueue);
            Thread parserThread = new Thread(parser);
            parserThread.start();
        }

        ExecutorService sentiExecutor = Executors.newFixedThreadPool(pool);
        List<Future<List<Review>>> taskList = new ArrayList<>();
        for (int i = 1; i <= pageQuontity / Constants.N_DIV_THREADS; i++) {
            SentimentAnalazer sentimentAnalazer = (SentimentAnalazer) Context.getBean(SentimentAnalazer.class);
            sentimentAnalazer.setReview(linkedReviewListBlockingQueue);
            taskList.add(sentiExecutor.submit(sentimentAnalazer));
        }

        readerExecutor.shutdown();
        readerExecutor.awaitTermination(5, TimeUnit.SECONDS);
        htmlPageBlockingQueue.put(new PoisonHtmlPage(" "));

        for (Future<List<Review>> future : taskList) {//whli true
            reviews.addAll(future.get());
        }
        sentiExecutor.shutdown();
        sentiExecutor.awaitTermination(5, TimeUnit.SECONDS);
        movie.setReviews(reviews);
        return movie;
    }



    @Override
    public void run() {
        try {
            DAOFactory.getDAOFactory(Constants.MY_SQL).getMovieDAO().update(this.start(Constants.N_THREADS));
            job.setStatus(Status.DONE);
        } catch (HtmlReviewPageNotFound e) {
            logger.error(job.getMovieName(), e);
            job.setStatus(Status.ABANDONED);
        } catch (Exception ex) {
            job.setStatus(Status.ABANDONED);
            logger.error(" ", ex);
        }
        DAOFactory.getDAOFactory(Constants.MY_SQL).getJobDAO().update(job);
    }
}
