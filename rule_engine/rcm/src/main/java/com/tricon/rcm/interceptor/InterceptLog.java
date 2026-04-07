package com.tricon.rcm.interceptor;

import com.tricon.rcm.security.service.impl.LoggingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class InterceptLog implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("HTTP");
    private static final String START_TIME_ATTR = "reqStartTime";

    @Autowired
    LoggingServiceImpl loggingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        loggingService.saveRequestToDb(request);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long start = (Long) request.getAttribute(START_TIME_ATTR);
        double elapsed = start != null ? (System.currentTimeMillis() - start) : 0;

        String query = request.getQueryString();
        String path = request.getRequestURI() + (query != null ? "?" + query : "");

        logger.info("{} {} {} {}", request.getMethod(), path, response.getStatus(),
                String.format("%.3f ms", elapsed));
    }
}
