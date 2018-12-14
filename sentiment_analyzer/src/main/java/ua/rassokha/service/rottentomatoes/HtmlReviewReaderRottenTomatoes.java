package ua.rassokha.service.rottentomatoes;

import ua.rassokha.domain.HtmlPage;
import ua.rassokha.exeption.HtmlReviewPageNotFound;
import ua.rassokha.service.HtmlReviewReader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class HtmlReviewReaderRottenTomatoes implements HtmlReviewReader<String> {
    private static String url = "https://www.rottentomatoes.com/";

    public HtmlPage getHtmlPage(String movieName) {
        return getHtml(url + "m/" + movieName.toLowerCase() + "/reviews");
    }

    public HtmlPage getPaginatedHtmlPage(String movieName, int i) {
        return getHtml(url + "m/" + movieName.toLowerCase() + "/reviews" + "?page=" + i);
    }

    private HtmlPage getHtml(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            String content = scanner.next();
            scanner.close();
            return new HtmlPage(content);
        } catch (IOException e) {
            throw new HtmlReviewPageNotFound("Can not load html", e);
        }
    }

}
