package ua.rassokha.exeption;

import java.lang.reflect.Method;

public class DuplicatedMappingForOnePathExeption extends RuntimeException {
    public DuplicatedMappingForOnePathExeption(String path, Method first, Method second) {
        super("Mapping" + path + " is found in " + first.getName() + " and " + second.getName());
    }

}
