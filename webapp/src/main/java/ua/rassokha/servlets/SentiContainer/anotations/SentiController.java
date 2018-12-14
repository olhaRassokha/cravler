package ua.rassokha.servlets.SentiContainer.anotations;

import ua.rassokha.di.anotations.SentiComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SentiComponent
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SentiController {
    String path() default "";

}
