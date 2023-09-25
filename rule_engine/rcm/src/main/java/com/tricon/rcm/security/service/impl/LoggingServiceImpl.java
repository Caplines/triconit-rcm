package com.tricon.rcm.security.service.impl;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmLogs;
import com.tricon.rcm.jpa.repository.RcmLogRepository;
import com.tricon.rcm.util.Constants;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class LoggingServiceImpl {

	@Autowired
	RcmLogRepository rcmLogRepository;
	
    Logger logger = LoggerFactory.getLogger("LoggingServiceImpl");

    public void displayReq(HttpServletRequest request, Object body) {
        StringBuilder reqMessage = new StringBuilder();
        CharSequence[] ignoreExtensions = {".jpg", ".png", ".js",".css",".jpeg"};
        Map<String,String> parameters = getParameters(request);
        String ipAddress = request.getHeader("X-FORWARDED-FOR");  
        String requestUri = request.getRequestURI();
        final List<String>skipUrls=Constants.SKIP_URL_FROM_RCM_LOGS;
        if (ipAddress == null) {  
            ipAddress = request.getRemoteAddr();  
        }
        
		if (!StringUtils.endsWithAny(request.getRequestURI(),ignoreExtensions)) {
        reqMessage.append("REQUEST ");
        reqMessage.append("method = [").append(request.getMethod()).append("]");
        reqMessage.append(" path = [").append(request.getRequestURI()).append("] ");

        if(!parameters.isEmpty()) {
            reqMessage.append(" parameters = [").append(parameters).append("] ");
        }
        boolean shouldSkip=skipUrls.stream().anyMatch(x->x.equals(requestUri));
        if(!Objects.isNull(body) && !shouldSkip) {
            reqMessage.append(" body = [").append(body).append("]");
        }
        RcmLogs logs= new RcmLogs();
        logs.setRequestData(reqMessage.toString());
        logs.setRequestType(request.getMethod());
        logs.setIp(ipAddress);
        rcmLogRepository.save(logs);
        logger.info("log Request: {}", reqMessage);
        
    }
    }

    /*
    public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
        StringBuilder respMessage = new StringBuilder();
        Map<String,String> headers = getHeaders(response);
        respMessage.append("RESPONSE ");
        respMessage.append(" method = [").append(request.getMethod()).append("]");
        if(!headers.isEmpty()) {
            respMessage.append(" ResponseHeaders = [").append(headers).append("]");
        }
        respMessage.append(" responseBody = [").append(body).append("]");

        logger.info("logResponse: {}",respMessage);
    }
    */
    private Map<String,String> getHeaders(HttpServletResponse response) {
        Map<String,String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for(String str : headerMap) {
            headers.put(str,response.getHeader(str));
        }
        return headers;
    }

    private Map<String,String> getParameters(HttpServletRequest request) {
        Map<String,String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName,paramValue);
        }
        return parameters;
    }


}
