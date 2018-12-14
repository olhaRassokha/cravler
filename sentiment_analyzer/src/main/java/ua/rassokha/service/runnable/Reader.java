package ua.rassokha.service.runnable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.rassokha.di.Scopes;
import ua.rassokha.di.anotations.SentiComponent;
import ua.rassokha.di.anotations.SentiInject;
import ua.rassokha.domain.HtmlPage;
import ua.rassokha.domain.collections.BlockingQueue;
import ua.rassokha.service.HtmlReviewReader;

import java.io.IOException;

@SentiComponent(scope = Scopes.PROTOTYPE)
public class Reader implements Runnable {
    private static final Logger logger = LogManager.getLogger();

    @SentiInject
    private HtmlReviewReader htmlReviewReader;

    private BlockingQueue<HtmlPage> queue;
    private String movieName;
    private int page;

    public Reader() {
    }

    public Reader(BlockingQueue<HtmlPage> queue, String movieName, int page) {
        this.queue = queue;
        this.movieName = movieName;
        this.page = page;
    }

    public HtmlReviewReader getHtmlReviewReader() {
        return htmlReviewReader;
    }

    public void setHtmlReviewReader(HtmlReviewReader htmlReviewReader) {
        this.htmlReviewReader = htmlReviewReader;
    }

    public BlockingQueue<HtmlPage> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<HtmlPage> queue) {
        this.queue = queue;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void run() {
        logger.info("Reader is running");
        try {
            produce(movieName, page);
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void produce(String movieName, int page) throws IOException, InterruptedException {
        queue.put(htmlReviewReader.getPaginatedHtmlPage(movieName, page));
        logger.info("Reader consumed");
    }
}
