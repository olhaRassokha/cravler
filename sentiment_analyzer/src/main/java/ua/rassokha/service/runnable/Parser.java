package ua.rassokha.service.runnable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.rassokha.di.Scopes;
import ua.rassokha.di.anotations.SentiComponent;
import ua.rassokha.di.anotations.SentiInject;
import ua.rassokha.domain.HtmlPage;
import ua.rassokha.domain.PoisonHtmlPage;
import ua.rassokha.domain.PoisonReview;
import ua.rassokha.domain.Review;
import ua.rassokha.domain.collections.BlockingQueue;
import ua.rassokha.service.HTMLReviewParser;

import java.util.List;

@SentiComponent(scope = Scopes.PROTOTYPE)
public class Parser implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    @SentiInject
    private HTMLReviewParser reviewParser;

    private BlockingQueue<HtmlPage> queue;
    private BlockingQueue<List<Review>> review;

    public Parser() {
    }

    public Parser(BlockingQueue<HtmlPage> queue, BlockingQueue<List<Review>> review) {
        this.queue = queue;
        this.review = review;

    }

    public void setQueue(BlockingQueue<HtmlPage> queue) {
        this.queue = queue;
    }

    public void setReview(BlockingQueue<List<Review>> review) {
        this.review = review;
    }

    @Override
    public void run() {
        logger.info("Parser is running");
        try {
            while (true) {
                consume();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void consume() throws InterruptedException {
        HtmlPage content = queue.take();
        if(content instanceof PoisonHtmlPage){
            logger.warn("poison");
            queue.put(content);
            review.put(new PoisonReview());
            logger.error("interapt");
            throw new InterruptedException();
        }
        review.put(reviewParser.parseReviews(content.getContent()));
        logger.info("Parser consumed");
    }
}
