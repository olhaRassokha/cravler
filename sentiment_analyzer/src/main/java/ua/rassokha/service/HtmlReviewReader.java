package ua.rassokha.service;

import ua.rassokha.domain.HtmlPage;

public interface HtmlReviewReader<S> {
    HtmlPage getHtmlPage(S sourse);

    HtmlPage getPaginatedHtmlPage(S movieName, int i);
}
