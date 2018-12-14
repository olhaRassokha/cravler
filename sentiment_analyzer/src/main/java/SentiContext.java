import ua.rassokha.di.Context;

public class SentiContext extends Context {
    private SentiContext() {
        super();
    }
    public static void reloadDependenciesForSite(String site) {
        loadXmlFile(site + ".xml");
    }

}
