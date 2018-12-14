package ua.rassokha.servlets.SentiContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import ua.rassokha.servlets.SentiContainer.anotations.SentiController;
import ua.rassokha.servlets.SentiContainer.anotations.SentyMapping;
import ua.rassokha.exeption.DuplicatedMappingForOnePathExeption;
import ua.rassokha.exeption.SentiReadPropertyException;

import ua.rassokha.util.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerFactory {
    private static final Logger logger = LogManager.getLogger();
    private static TreeMap<String, Method> getControlleers = new TreeMap<>();
    private static TreeMap<String, Method> putControlleers = new TreeMap<>();
    private static TreeMap<String, Method> getPathVariableControlleers = new TreeMap<>();
    private static TreeMap<String, Method> putPathVariableControlleers = new TreeMap<>();
    private static Pattern  pathVariableControlleerPattern = Pattern.compile("/?/\\{\\w+}");
    private static Pattern  pathVariablePattern = Pattern.compile("(/?\\{?\\w*}?)");
    private static Map<Method, Map<String, Integer>> pathVariables = new  HashMap<>();

    public static void  initControllerFactory() {
        Reflections reflections;
        try {
            reflections = new Reflections(Configuration.getConfiguration().getaPackage());
        } catch (SentiReadPropertyException e) {
            logger.error("Invalid package, use package.scan property to set up your configuration packge.");
            throw new ExceptionInInitializerError(e);
        }
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(SentiController.class);
        for (Class<?> aClass : types) {
            String aditionalPath = aClass.getAnnotation(SentiController.class).path();
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(SentyMapping.class)) {
                    if(method.getAnnotation(SentyMapping.class).method().equals(Methods.GET)) {
                        if(pathVariableControlleerPattern.matcher(method.getAnnotation(SentyMapping.class).path()).find()){
                            putWithVariablesMethod(aditionalPath, method, getPathVariableControlleers);
                        }
                        putMethod(aditionalPath, method, getControlleers);
                    }
                    if(method.getAnnotation(SentyMapping.class).method().equals(Methods.PUT)) {
                        if(pathVariableControlleerPattern.matcher(method.getAnnotation(SentyMapping.class).path()).find()){
                            putWithVariablesMethod(aditionalPath, method, putPathVariableControlleers);
                        }
                        putMethod(aditionalPath, method, putControlleers);
                    }
                }

            }
        }
    }

    private static void putWithVariablesMethod(String aditionalPath, Method method, TreeMap<String, Method> controlleers) {
        String fullPath =aditionalPath + method.getAnnotation(SentyMapping.class).path();
        Map<String, Integer> param = new HashMap<>();
        Matcher matcher = pathVariablePattern.matcher(fullPath);
        int i = 1;
        while (matcher.find()){
            if(pathVariableControlleerPattern.matcher(matcher.group()).find()){
                System.out.println(matcher.group());
                param.put(matcher.group().replaceAll("/\\{", "").replaceAll("}", ""), i);
            }
            i++;
        }
        pathVariables.put(method, param);
        controlleers.put(fullPath, method);
    }

    private static void putMethod(String aditionalPath, Method method, TreeMap<String, Method> controlleers) {
        try {
            controlleers.put(aditionalPath + method.getAnnotation(SentyMapping.class).path(), method);
        } catch (IllegalArgumentException e) {
            throw new DuplicatedMappingForOnePathExeption
                    (aditionalPath + method.getAnnotation(SentyMapping.class).path(),
                            controlleers.get(aditionalPath + method.getAnnotation(SentyMapping.class).path()), method);
        }
    }

    public static Method getControlleer(String requestURI, String method) {
        //\/?{\w+} -> \/?\w+
        if (method.equals(Methods.GET.name())) {
            if(getControlleers.containsKey(requestURI)) {
                return getControlleers.get(requestURI);
            }else {
                for(Map.Entry<String, Method> entry: getPathVariableControlleers.entrySet()){
                    if(requestURI.matches(entry.getKey()
                            .replaceAll("/?/\\{\\w+}", "\\/?\\\\\\w+")
                            .replaceAll("/", "\\\\\\/"))){

                        return entry.getValue();
                    }
                }
            }
        }
        if (method.equals(Methods.PUT.name())) {
            if(putControlleers.containsKey(requestURI)) {
                return putControlleers.get(requestURI);
            }else {
                for(Map.Entry<String, Method> entry: putPathVariableControlleers.entrySet()){
                    if(requestURI.matches(entry.getKey()
                            .replaceAll("/?/\\{\\w+}", "\\/?\\\\\\w+")
                            .replaceAll("/", "\\\\\\/"))){

                        return entry.getValue();
                    }
                }
            }
        }

        throw new UnsupportedOperationException("Method " + method + " isn't supported");
    }
    public static Object getPathParametr(Method method, String parametr, String requestURI){ // /movie/1, id
        String[] r = requestURI.split("/");
        return requestURI.split("/")[pathVariables.get(method).get(parametr)];
    }

}
