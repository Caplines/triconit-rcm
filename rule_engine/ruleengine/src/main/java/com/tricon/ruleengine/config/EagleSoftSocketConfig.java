package com.tricon.ruleengine.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.tricon.ruleengine.utils.EagleSoftFetchData;

/**
 * Applies socket read timeout for EagleSoft agent connections (see {@code EagleSoftFetchData}).
 */
@Configuration
public class EagleSoftSocketConfig {

	@Value("${eagle.soft.socket.read-timeout-millis:120000}")
	private int readTimeoutMillis;

	@PostConstruct
	public void applySocketReadTimeout() {
		EagleSoftFetchData.setSocketReadTimeoutMs(readTimeoutMillis);
	}
}
