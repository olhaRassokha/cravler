package ua.rassokha.service;

import ua.rassokha.domain.Review;

import java.util.List;

public interface HTMLReviewParser {
    List<Review> parseReviews(String content);
}
