package ua.rassokha.service.rottentomatoes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ua.rassokha.domain.HtmlPage;
import ua.rassokha.service.MetaDataParser;
import ua.rassokha.util.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MataDataParserImpl implements MetaDataParser {
    private static final String DURATION_SELECTOR = "div.bottom_divider:nth-child(2) > ul:nth-child(1) > li:nth-child(1)";
    private static Pattern namePattern = Pattern.compile("<meta name=\"movieTitle\" content=[\\s\\S]+/> <script>");
    private static Pattern infoPattern = Pattern.compile("<div class=\"bottom_divider\"[\\s\\S]+</li></p></ul> </div>");
    private static Pattern pageQuontityPattern = Pattern.compile("span class=\"pageInfo\">[\\s\\S]+<\\/span>");


    @Override
    public String parseDuration(String content) {
        String infoSection = getInfoSection(content);
        String duration = "N/A";
        Document document = Jsoup.parse(content);
        Elements elements = document.select(DURATION_SELECTOR);
        return elements.text();

    }


    @Override
    public String parseName(String content) {
        String nameSection = getName(content);
        String name;
        Document document = Jsoup.parse(content);
        return document.title().split(" -")[0];

    }

    private String getName(String content) {
        Matcher m = namePattern.matcher(content);
        if (m.find()) {
            return m.group().replaceAll("&", "&amp;");
        }
        return "N/A";
    }

    private String getInfoSection(String content) {
        Matcher m = infoPattern.matcher(content);
        if (m.find()) {
            return m.group().replaceAll("&", "&amp;");
        }
        return "N/A";
    }


    @Override
    public int getPageQuontiy(HtmlPage htmlPage) {
        Matcher m = pageQuontityPattern.matcher(htmlPage.getContent());
        if (m.find()) {
            return Integer.parseInt(m.group().replaceAll("<", " ").split(" ")[4]);
        }
        return Constants.DEFAULT_PAGE_QUONTITY;
    }
}
