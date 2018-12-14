package ua.rassokha.servlets;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.rassokha.di.Context;
import ua.rassokha.servlets.SentiContainer.ControllerFactory;
import ua.rassokha.servlets.SentiContainer.anotations.SentiPathVariable;
import ua.rassokha.servlets.SentiContainer.anotations.SentiRequestVariable;
import ua.rassokha.servlets.SentiContainer.anotations.SentyMapping;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;


public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void init() throws ServletException {
        super.init();
        Context.initContext();
        ControllerFactory.initControllerFactory();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
        Method controller = ControllerFactory.getControlleer(req.getRequestURI(), req.getMethod());
        Annotation[][] annotations = controller.getParameterAnnotations();
        Class[] types = controller.getParameterTypes();
        Object[] oo = new Object[annotations.length];
        if (req.getParameterMap().size() > 0) {
            for (int i = 0; i < annotations.length; i++) {
                Object o = null;
                if (annotations[i][0].annotationType().equals(SentiRequestVariable.class)) {
                    String s = ((SentiRequestVariable) annotations[i][0]).value();
                    o = req.getParameter(s);
                }
                if (annotations[i][0].annotationType().equals(SentiPathVariable.class)) {
                    String s = ((SentiPathVariable) annotations[i][0]).value();
                    o = ControllerFactory.getPathParametr(controller, s, req.getRequestURI());
                }
                Class clazz = types[i];
                if (clazz.getGenericSuperclass().getTypeName().equals(Number.class.getName())) {
                    try {
                        oo[i] = types[i].cast(NumberFormat.getInstance().parse((String) o).intValue());
                    } catch (ParseException e) {
                        logger.error(e);
                    }
                } else {
                    oo[i] = types[i].cast(o);
                }
            }
        }
        Object requested;
        try {
            Object invoke = Context.getBean(controller.getDeclaringClass());
            requested = controller.invoke(invoke, oo);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException();
        }
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String res = JSON.toJSONString(requested);
        out.print(res);
        out.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}