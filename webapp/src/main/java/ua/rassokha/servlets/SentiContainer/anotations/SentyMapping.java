package ua.rassokha.servlets.SentiContainer.anotations;


import ua.rassokha.servlets.SentiContainer.Methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SentyMapping {
    String path() default "/";

    Methods method() default Methods.GET;
}
