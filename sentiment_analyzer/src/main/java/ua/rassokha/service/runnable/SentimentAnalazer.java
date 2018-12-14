package ua.rassokha.service.runnable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.rassokha.di.Scopes;

import ua.rassokha.di.anotations.SentiComponent;
import ua.rassokha.di.anotations.SentiInject;
import ua.rassokha.domain.PoisonHtmlPage;
import ua.rassokha.domain.PoisonReview;
import ua.rassokha.domain.Review;
import ua.rassokha.domain.collections.BlockingQueue;
import ua.rassokha.service.SentiWordNetAnalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SentiComponent(scope = Scopes.PROTOTYPE)
public class SentimentAnalazer implements Callable<List<Review>> {
    private static final Logger logger = LogManager.getLogger();
    @SentiInject
    private SentiWordNetAnalyser sentiWordNetAnalyser;
    private BlockingQueue<List<Review>> review;


    public SentimentAnalazer() {

    }

    public SentimentAnalazer(BlockingQueue<List<Review>> review) {
        this.review = review;
    }

    public void setReview(BlockingQueue<List<Review>> review) {
        this.review = review;
    }

    @Override
    public List<Review> call() {
        logger.info("SentiWordNetAnalyser is running");
        List<Review> reviews = new ArrayList<>();
        while (true) {
            try {
                reviews.addAll(consume());
            } catch (InterruptedException e) {
                logger.error("interapt");
                System.out.println(reviews);
                return reviews;
            }
            logger.warn("running");
        }
    }

    private List<Review> consume() throws InterruptedException {
        List<Review> reviews = review.take();
        if(reviews instanceof PoisonReview){
            while ((reviews instanceof PoisonReview)) {
                if(review.isEmpty()){
                    throw new InterruptedException();
                }
                reviews = review.take();
                if (review.isPoisoned()) {
                    review.put(reviews);
                    logger.warn("poison");
                    throw new InterruptedException();
                }
            }
        }
        logger.info("SentiWordNetAnalyser consumed");
        return sentiWordNetAnalyser.analyzeReviews(reviews);
    }
}
