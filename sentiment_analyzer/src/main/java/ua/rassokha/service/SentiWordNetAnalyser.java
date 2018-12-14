package ua.rassokha.service;

import ua.rassokha.domain.Review;
import ua.rassokha.domain.Sentiment;

import java.util.List;

public interface SentiWordNetAnalyser {
    double analyseText(String text);

    Sentiment analyzereviewText(String text);

    List<Review> analyzeReviews(List<Review> reviews);
}
