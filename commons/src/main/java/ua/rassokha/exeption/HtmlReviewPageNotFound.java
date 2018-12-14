package ua.rassokha.exeption;

import java.io.IOException;

public class HtmlReviewPageNotFound extends RuntimeException {
    public HtmlReviewPageNotFound(String massage, IOException e) {
        super(massage, e);
    }
}
