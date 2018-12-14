package ua.rassokha.di.anotations;


import ua.rassokha.di.Scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SentiBean {
    Scopes scope() default Scopes.SINGLETON;
}
