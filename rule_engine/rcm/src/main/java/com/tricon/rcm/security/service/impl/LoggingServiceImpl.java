package com.tricon.rcm.security.service.impl;

import com.tricon.rcm.db.entity.RcmLogs;
import com.tricon.rcm.jpa.repository.RcmLogRepository;
import com.tricon.rcm.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class LoggingServiceImpl {

    @Autowired
    RcmLogRepository rcmLogRepository;

    /**
     * Persists a summary of the incoming request to the DB log table.
     * Console logging is handled by InterceptLog (one compact line per request).
     */
    public void saveRequestToDb(HttpServletRequest request) {
        CharSequence[] ignoreExtensions = {".jpg", ".png", ".js", ".css", ".jpeg"};
        if (StringUtils.endsWithAny(request.getRequestURI(), ignoreExtensions)) {
            return;
        }

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        StringBuilder reqMessage = new StringBuilder();
        reqMessage.append("REQUEST ");
        reqMessage.append("method=[").append(request.getMethod()).append("]");
        reqMessage.append(" path=[").append(request.getRequestURI()).append("]");

        Map<String, String> parameters = getParameters(request);
        if (!parameters.isEmpty()) {
            reqMessage.append(" parameters=").append(parameters);
        }

        final List<String> skipUrls = Constants.SKIP_URL_FROM_RCM_LOGS;
        boolean shouldSkip = skipUrls.stream().anyMatch(x -> x.equals(request.getRequestURI()));

        RcmLogs logs = new RcmLogs();
        logs.setRequestData(reqMessage.toString());
        logs.setRequestType(request.getMethod());
        logs.setIp(ipAddress);
        rcmLogRepository.save(logs);
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            parameters.put(paramName, request.getParameter(paramName));
        }
        return parameters;
    }
}
