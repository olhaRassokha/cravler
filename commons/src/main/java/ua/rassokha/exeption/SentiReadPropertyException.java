package ua.rassokha.exeption;


public class SentiReadPropertyException extends RuntimeException {

    public SentiReadPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SentiReadPropertyException(String massage) {
        super(massage);
    }
}
