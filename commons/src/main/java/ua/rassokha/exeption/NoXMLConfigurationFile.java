package ua.rassokha.exeption;

public class NoXMLConfigurationFile extends RuntimeException {
    public NoXMLConfigurationFile(Throwable throwable) {
        super(throwable);
    }
}
