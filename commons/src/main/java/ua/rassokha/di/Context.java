package ua.rassokha.di;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.reflections.Reflections;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import ua.rassokha.di.anotations.SentiBean;
import ua.rassokha.di.anotations.SentiComponent;
import ua.rassokha.di.anotations.SentiConfiguration;
import ua.rassokha.di.anotations.SentiInject;
import ua.rassokha.exeption.NoXMLConfigurationFile;
import ua.rassokha.exeption.SentiBeanNotFoundExeption;
import ua.rassokha.exeption.SentiReadPropertyException;
import ua.rassokha.util.Configuration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Context {//check all before init

    private static final Logger logger = LogManager.getLogger();
    private static Map<Class, Method> protypeMethdMap = new HashMap<>();
    private static Map<Class, Class> prototypeMap = new HashMap<>();
    private static Map<Class, Object> singletonMap = new HashMap<>();
    private static Map<Class, Class> prototypeXmlMap = new HashMap<>();
    private static Map<Class, Object> singletonXmlMap = new HashMap<>();
    private static Graph<Bean, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

    protected Context() {
    }

    public static void initContext() {
        Reflections reflections;
        try {
            reflections = new Reflections(Configuration.getConfiguration().getaPackage());
        } catch (SentiReadPropertyException e) {
            logger.error("Invalid package, use package.scan property to set up your configuration packge.");
            throw new ExceptionInInitializerError(e);
        }
        loadDependenciesToDirectedGraph(reflections);
    }

    private static void loadDependenciesToDirectedGraph(Reflections reflections) {
        Set<Class<?>> configurationTypes = reflections.getTypesAnnotatedWith(SentiConfiguration.class);
        loadConfigurationClassesToDirectedGraph(configurationTypes);
        loadComponentClassesToDirectedGraph(reflections, SentiComponent.class);
        loadXmlFileToDirectedGraph();
        initBeansFromdirectedGraph();
    }

    public static Object getBean(Class interfaceClass) {
        if (singletonXmlMap.containsKey(interfaceClass)) {
            return singletonXmlMap.get(interfaceClass);
        }
        if (singletonMap.containsKey(interfaceClass)) {
            return singletonMap.get(interfaceClass);
        } else {
            Class clazz = prototypeXmlMap.get(interfaceClass);
            if (clazz == null) {
                clazz = prototypeMap.get(interfaceClass);
                if (clazz == null) {

                    Method methd = protypeMethdMap.get(interfaceClass);
                    if (methd == null) {
                        throw new SentiBeanNotFoundExeption(
                                "No bean declaration found for " + interfaceClass.getName());
                    }
                    return getObjectFromPrototype(interfaceClass, methd);
                }
            }
            return getObjectFromPrototype(interfaceClass, clazz);
        }    }

    private static Object getObjectFromPrototype(Class interfaceClass, Class clazz) {
        Object impl;
        try {
            impl = clazz.newInstance();
            injectField(impl);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SentiBeanNotFoundExeption(
                    "Can not init dependency for " + interfaceClass.getName(), e);
        }
        return impl;
    }
    private static Object getObjectFromPrototype(Class interfaceClass, Method method) {
        Object impl;
        try {
            impl = method.invoke(method.getDeclaringClass().newInstance());
            injectField(impl);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new SentiBeanNotFoundExeption(
                    "Can not init dependency for " + interfaceClass.getName(), e);
        }
        return impl;    }
    private static void initBeansFromdirectedGraph() {
        while (!directedGraph.vertexSet().isEmpty()){
            Set<Bean> leafs = new HashSet<>();
            for(Bean bean :directedGraph.vertexSet()){
                if(directedGraph.degreeOf(bean)==1){
                    System.out.println(bean);
                    loadDependencies(bean);
                    leafs.add(bean);
                }
            }
            directedGraph.removeAllVertices(leafs);
        }
    }

    private static void loadDependencies(Bean bean) {
        switch (bean.getInitMethod()){
            case XML:loadDependenciesFromComponents(bean);break;
            case COMPONENT:loadDependenciesFromComponents(bean);break;
            case ANNOTATION:loadDependenciesFromMetods(bean);break;
        }
    }

    private static void loadDependenciesFromComponents(Bean bean) {
        if (bean.getScope() == Scopes.PROTOTYPE) {
            prototypeMap.put(bean.getType(), bean.getaClass());
        }
        if (bean.getScope() == Scopes.SINGLETON) {
            try {
                Object invoke = injectField(bean.getaClass().newInstance());
                singletonMap.put(bean.getType(), invoke);
            } catch (IllegalAccessException | InstantiationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    private static Object injectField(Object objToInject) {
        Field[] fields = objToInject.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(SentiInject.class) != null) {
                field.setAccessible(true);
                try {
                    field.set(objToInject, getBean(field.getType()));
                } catch (IllegalAccessException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        }
        return objToInject;
    }
    private static void loadDependenciesFromMetods(Bean bean) {
                if (bean.getScope() == Scopes.PROTOTYPE) {
                    protypeMethdMap.put(bean.getType(), bean.getMethod());
                }
                if (bean.getScope() == Scopes.SINGLETON) {
                    try {
                        Object invoke = injectField(bean.getMethod().invoke(bean.getMethod()
                                        .getDeclaringClass()
                                        .newInstance()));
                        singletonMap.put(bean.getType(), invoke);
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        throw new ExceptionInInitializerError(e);
                    }
                }
    }

    private static void loadComponentClassesToDirectedGraph(Reflections reflections, Class annotation) {
        Set<Class<?>> componentTypes = reflections.getTypesAnnotatedWith(annotation);
        for (Class<?> aClass : componentTypes) {
            Bean current = null;
            if (aClass.isAnnotation()) {
                loadComponentClassesToDirectedGraph(reflections, aClass);
            } else {
                if (!aClass.isAnnotationPresent(SentiComponent.class)) {
                    current = new Bean(aClass,
                            aClass,
                            Scopes.SINGLETON);
                    directedGraph.addVertex(current);
                } else {
                    current = new Bean(aClass,
                            aClass,
                            aClass.getAnnotation(SentiComponent.class).scope());
                    directedGraph.addVertex(current);
                }
            }
            loadDependenciesOfBeanToDirectedGraph(aClass, current);
        }
    }

    private static void loadXmlFileToDirectedGraph() {
        loadXmlFile("beans2.xml");
    }

    private static void loadConfigurationClassesToDirectedGraph(Set<Class<?>> configurationTypes) {
        for (Class<?> aClass : configurationTypes) {
            Method[] methods = aClass.getDeclaredMethods();
            Object invocation;
            try {
                invocation = aClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExceptionInInitializerError(e);
            }
            loadDependenciesFromMetodsToDirectedGraph(methods, invocation);
        }
    }

    private static void loadDependenciesFromMetodsToDirectedGraph(Method[] methods, Object invocation) {//thinkkk
        Object impl = null;
        Bean current = null;
        for (Method method : methods) {
            if (method.isAnnotationPresent(SentiBean.class)) {
                try {
                    impl = method.invoke(invocation);
                    current = new Bean(method,
                            method.getReturnType(),
                            method.getAnnotation(SentiBean.class).scope());
                    directedGraph.addVertex(current);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                loadDependenciesOfBeanToDirectedGraph(impl.getClass(), current);
            }
        }
    }

    protected static void loadXmlFile(String file) {
        try {
            FileInputStream inputFile = new FileInputStream(Context.class.getClassLoader().getResource(file).getPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            Element element = doc.getDocumentElement();
            loadFile(element.getElementsByTagName("bean"));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new NoXMLConfigurationFile(e);
        }
    }

    private static void loadFile(NodeList rootNode) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            Bean bean = new Bean();
            Class clazz = null;
            Class type = null;
            bean.setInitMethod(InitMethod.XML);
            NamedNodeMap attributes = rootNode.item(i).getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                if (attributes.item(j).getNodeName().equals("name")) {
                    bean.setName(attributes.item(j).getNodeValue());
                }
                if (attributes.item(j).getNodeName().equals("class")) {
                    try {
                        clazz = Class.forName(attributes.item(j).getNodeValue());
                        type = clazz.getInterfaces()[0];
                        bean.setaClass(clazz);
                        bean.setType(type);
                    } catch (ClassNotFoundException e) {
                        throw new SentiBeanNotFoundExeption(
                                "Can not init dependency for " + clazz, e);
                    }
                }
                if (attributes.item(j).getNodeName().equals("scope")) {
                    bean.setScope(Scopes.valueOf(attributes.item(j).getNodeValue().toUpperCase()));
                }
            }
            directedGraph.addVertex(bean);
            NodeList properties = rootNode.item(i).getChildNodes();
            for (int j = 0; j < properties.getLength(); j++) {
                Bean dependent = new Bean();
                /*if (attributes.item(j).getNodeName().equals("name")) {
                    dependent.setName(attributes.item(j).getNodeValue());
                }*/
                if (properties.item(j).getNextSibling() != null) {
                    NamedNodeMap propertyAttributes = properties.item(j).getNextSibling().getAttributes();
                    if (propertyAttributes != null) {
                        for (int k = 0; k < propertyAttributes.getLength(); k++) {
                            if (propertyAttributes.item(k).getNodeName().equals("value")) {
                                try {
                                    dependent.setType(Class.forName(propertyAttributes.item(k).getNodeValue()).getInterfaces()[0]);
                                    if (directedGraph.containsVertex(dependent)) {
                                        for (Bean bean1 : directedGraph.vertexSet()) {
                                            if (bean1.equals(dependent)) {
                                                directedGraph.addEdge(bean, bean1);
                                            }
                                        }
                                    } else {
                                        directedGraph.addVertex(dependent);
                                        directedGraph.addEdge(bean, dependent);
                                    }                                } catch (ClassNotFoundException e) {
                                    throw new SentiBeanNotFoundExeption(
                                            "Can not init dependency for " + clazz, e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void loadDependenciesOfBeanToDirectedGraph(Class<?> aClass, Bean bean) {
        for (Field field : aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(SentiInject.class)) {
                Bean dependent = new Bean((field.getType()));
                if (directedGraph.containsVertex(dependent)) {
                    for (Bean bean1 : directedGraph.vertexSet()) {
                        if (bean1.equals(dependent)) {
                            directedGraph.addEdge(bean, bean1);
                        }
                    }
                } else {
                    directedGraph.addVertex(dependent);
                    directedGraph.addEdge(bean, dependent);
                }
            }
        }
    }

    enum InitMethod {
        ANNOTATION, COMPONENT, XML;
    }

    private static class Bean {
        String name;
        Class aClass;
        Class type;
        Scopes scope;
        InitMethod initMethod;
        Method method;

        public Bean() {
        }

        public Bean(Class aClass, Class type, Scopes scope) {
            this.aClass = aClass;
            this.type = type;
            this.scope = scope;
            this.initMethod = InitMethod.COMPONENT;
        }

        public Bean(Method method, Class type, Scopes scope) {
            this.type = type;
            this.scope = scope;
            this.initMethod = InitMethod.ANNOTATION;
            this.method = method;
        }

        public Bean(Class<?> type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class getaClass() {
            return aClass;
        }

        public void setaClass(Class aClass) {
            this.aClass = aClass;
        }

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }

        public Scopes getScope() {
            return scope;
        }

        public void setScope(Scopes scope) {
            this.scope = scope;
        }

        public InitMethod getInitMethod() {
            return initMethod;
        }

        public void setInitMethod(InitMethod initMethod) {
            this.initMethod = initMethod;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public boolean isFullyLoaded() {
            return initMethod != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bean bean = (Bean) o;
            return Objects.equals(type, bean.type);
        }

        @Override
        public int hashCode() {

            return Objects.hash(type);
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "name='" + name + '\'' +
                    ", aClass=" + aClass +
                    ", type=" + type +
                    ", scope=" + scope +
                    ", initMethod=" + initMethod +
                    ", method=" + method +
                    '}';
        }
    }
}
