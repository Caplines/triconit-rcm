package com.tricon.rcm.interceptor;


import com.tricon.rcm.security.service.impl.LoggingServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class InterceptLog implements HandlerInterceptor {

	@Autowired
	LoggingServiceImpl loggingService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getMethod().equals(HttpMethod.GET.name()) || request.getMethod().equals(HttpMethod.DELETE.name())
				|| request.getMethod().equals(HttpMethod.POST.name())
				|| request.getMethod().equals(HttpMethod.PUT.name())) {
			loggingService.displayReq(request, null);
		}
		return true;
	}
}
