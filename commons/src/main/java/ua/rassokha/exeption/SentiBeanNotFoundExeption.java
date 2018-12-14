package ua.rassokha.exeption;

public class SentiBeanNotFoundExeption extends RuntimeException {
    public SentiBeanNotFoundExeption(String can_not_init_dependency, Exception e) {
        super(can_not_init_dependency, e);
    }

    public SentiBeanNotFoundExeption(String can_not_init_dependency) {
        super((can_not_init_dependency));
    }
}
