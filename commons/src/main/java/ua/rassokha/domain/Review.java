package ua.rassokha.domain;

import java.util.Objects;

public class Review {
    private Integer id;
    private String reviewText;
    private Sentiment sentiment;
    private String reviewDate;

    public Review(String reviewText) {
        this.reviewText = reviewText;
    }

    public Review(String reviewText, String reviewDate) {
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
    }

    public Review() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {

        this.reviewText = reviewText;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        return reviewText + ", sentiment=" + sentiment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(reviewText, review.reviewText) &&
                sentiment == review.sentiment;
    }

    @Override
    public int hashCode() {

        return Objects.hash(reviewText, sentiment);
    }
}
