import ua.rassokha.service.*;
import ua.rassokha.service.rottentomatoes.HTMLReviewParserImpl;
import ua.rassokha.service.rottentomatoes.HtmlReviewReaderRottenTomatoes;
import ua.rassokha.service.rottentomatoes.SentiWordNetAnalyserImpl;
import ua.rassokha.service.rottentomatoes.MataDataParserImpl;
import ua.rassokha.di.Scopes;
import ua.rassokha.di.anotations.SentiBean;
import ua.rassokha.di.anotations.SentiConfiguration;

@SentiConfiguration
public class Config {
    @SentiBean(scope = Scopes.PROTOTYPE)
    public HTMLReviewParser htmlReviewParser() {
        return new HTMLReviewParserImpl();
    }

    @SentiBean(scope = Scopes.SINGLETON)
    public HtmlReviewReader htmlReviewReader() {
        return new HtmlReviewReaderRottenTomatoes();
    }

    @SentiBean(scope = Scopes.PROTOTYPE)
    public SentiWordNetAnalyser sentiWordNetAnalyser() {
        return new SentiWordNetAnalyserImpl();
    }

    @SentiBean(scope = Scopes.PROTOTYPE)
    public MetaDataParser metaDataParser() {
        return new MataDataParserImpl();
    }
}
