package ua.rassokha.service.rottentomatoes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.rassokha.domain.Review;
import ua.rassokha.service.HTMLReviewParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTMLReviewParserImpl implements HTMLReviewParser {

    private static final String REVIEW_SELECTOR = "div#reviews div.content div.review_table div.row.review_table_row " +
            "div.col-xs-16.review_container div.review_area div.review_desc div.the_review";
    private static Pattern reviewSectionPattern = Pattern.compile("<section id=\"content\" class=\"panel panel-rt panel-box \">[\\s\\S]+<\\/section>");

    @Override
    public List<Review> parseReviews(String content) {
        String reviewSection = getReviewSection(content);
        List<Review> reviews = new ArrayList<>();
        Document document = Jsoup.parse(reviewSection);
        Elements elements = document.select(REVIEW_SELECTOR);
        for (Element element : elements) {
            reviews.add(new Review(element.text()));
        }
        return reviews;
    }

    private String getReviewSection(String content) {
        Matcher m = reviewSectionPattern.matcher(content);
        m.find();
        return m.group();
    }


}
