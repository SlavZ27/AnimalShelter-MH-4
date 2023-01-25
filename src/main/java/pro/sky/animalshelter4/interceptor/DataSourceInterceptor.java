package pro.sky.animalshelter4.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pro.sky.animalshelter4.configuration.DataSourceType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataSourceInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String contextPath = request.getRequestURI().toUpperCase();

        System.out.println("URI:" + contextPath);

        if (contextPath.contains(DataSourceType.DOG.name())) {
            request.setAttribute("database", DataSourceType.DOG);
        } else if (contextPath.contains(DataSourceType.CAT.name())) {
            request.setAttribute("database", DataSourceType.CAT);
        } else if (contextPath.contains(DataSourceType.PUBLIC.name())) {
            request.setAttribute("database", DataSourceType.PUBLIC);
        }
        return true;
    }
}
