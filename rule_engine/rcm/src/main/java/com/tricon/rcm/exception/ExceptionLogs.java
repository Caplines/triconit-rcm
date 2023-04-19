package com.tricon.rcm.exception;

import java.sql.Timestamp;
import java.time.Instant;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tricon.rcm.db.entity.RcmExceptionLogs;
import com.tricon.rcm.jpa.repository.RcmExceptionLogsRepo;

@Aspect
@Component
public class ExceptionLogs {
	
	@Autowired
	RcmExceptionLogsRepo exceptionLogsRepo;

	private final Logger logger = LoggerFactory.getLogger(ExceptionLogs.class);

	@Pointcut("within(com.tricon.rcm..*) && execution(* *(..))")
	private void anyMethod() {
	}

	@AfterThrowing(value = "anyMethod()", throwing = "exception")
	public void afterThrowingExceptionFromApiController(JoinPoint joinPoint, Exception exception) {
		RcmExceptionLogs exceptionLogs=new RcmExceptionLogs();
		logger.error(exception.getMessage());
		exception.printStackTrace();
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String occursIn="Exeption occurs in " + className + " And method name is:" + methodName;
		logger.error(occursIn);
		exceptionLogs.setCreatedDate(Timestamp.from(Instant.now()));
		exceptionLogs.setStackTrace(exception.getLocalizedMessage());
		exceptionLogs.setOccursIn(occursIn);
		exceptionLogsRepo.save(exceptionLogs);

	}

}
