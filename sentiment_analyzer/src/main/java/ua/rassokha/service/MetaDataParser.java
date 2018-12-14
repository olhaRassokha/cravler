package ua.rassokha.service;

import ua.rassokha.domain.HtmlPage;

public interface MetaDataParser {

    String parseName(String content);

    int getPageQuontiy(HtmlPage htmlPage);

    String parseDuration(String content);

}
