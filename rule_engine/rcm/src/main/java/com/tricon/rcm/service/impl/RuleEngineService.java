package com.tricon.rcm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmInsuranceMainRootDto;
import com.tricon.rcm.dto.RcmRemoteLiteMainRootDto;
import com.tricon.rcm.dto.ClaimSourceDto;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Service
public class RuleEngineService {

	private final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

	@Autowired
	Environment ev;

	@Autowired
	RestTemplate restTemplate;

	HttpHeaders headers = null;

	@PostConstruct
	public void init() {

		headers = new HttpHeaders() {
			{
				// set("Accept-Language", "en");
				// set("Content-Type", "application/json");
				set("x-api-key", ev.getProperty("rmc.auth.token"));

			}
		};

	}

	public boolean pullClaimFromRE(ClaimSourceDto dto) {

		logger.info(" In pullClaimFromRE");

		dto.setPassword(ev.getProperty("rcm.ruleengine.password"));

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		String param = "?password=" + dto.getPassword();
		if (dto.getOfficeuuid() != null)
			param = param + "&office=" + dto.getOfficeuuid();

		// Call Rule Engine API..
		ResponseEntity<RcmClaimMainRootDto> result = restTemplate.exchange(ev.getProperty("rmc.pullclaimurl") + param,
				HttpMethod.GET, entity, RcmClaimMainRootDto.class);

		System.out.println(result.getBody());
		RcmClaimMainRootDto rootDto = result.getBody();
		System.out.println(rootDto.getData().getDatas().get(0).getOfficeName());
		System.out.println(rootDto.getData().getDatas().get(0).getData().get(0).getPatientId());

		return true;
	}

	public boolean pullInsuranceFromRE(ClaimSourceDto dto) {

		logger.info(" In pull Insurance From RE");
		dto.setPassword(ev.getProperty("rcm.ruleengine.password"));

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		String param = "?password=" + dto.getPassword();
		if (dto.getOfficeuuid() != null)
			param = param + "&office=" + dto.getOfficeuuid();

		// Call Rule Engine API..
		ResponseEntity<RcmInsuranceMainRootDto> result = restTemplate.exchange(
				ev.getProperty("rmc.pullInsuranceUrl") + param, HttpMethod.GET, entity, RcmInsuranceMainRootDto.class);

		// ResponseEntity<RcmClaimMainRootDto> result1 = restTemplate
		// .getForEntity(ev.getProperty("rmc.pullclaimurl")+param,request,
		// RcmClaimMainRootDto.class);

		System.out.println(result.getBody());
		RcmInsuranceMainRootDto rootDto = result.getBody();
		System.out.println(rootDto.getData().getDatas().get(0).getData().get(0).getName());
		//System.out.println(rootDto.getData().getDatas().get(0).getName());

		return true;
	}

	public boolean pullRemoteLiteDate(ClaimSourceDto dto) {

		logger.info(" In pullRemoteLiteDate");
		dto.setPassword(ev.getProperty("rcm.ruleengine.password"));

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		String param = "?password=" + dto.getPassword();
		if (dto.getOfficeuuid() != null)
			param = param + "&office=" + dto.getOfficeuuid();

		// Call Rule Engine API..
		ResponseEntity<RcmRemoteLiteMainRootDto> result = restTemplate.exchange(
				ev.getProperty("rcm.pullremoteliteurl") + param, HttpMethod.GET, entity,
				RcmRemoteLiteMainRootDto.class);

		System.out.println(result.getBody());
		RcmRemoteLiteMainRootDto rootDto = result.getBody();
		System.out.println(rootDto.getData().get(0).getOfficeId());
		System.out.println(rootDto.getData().get(0).getGoogleSubId());

		return true;
	}

}
