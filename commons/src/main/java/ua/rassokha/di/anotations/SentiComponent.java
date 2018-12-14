package ua.rassokha.di.anotations;


import ua.rassokha.di.Scopes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface SentiComponent {
    Scopes scope() default Scopes.SINGLETON;
}
