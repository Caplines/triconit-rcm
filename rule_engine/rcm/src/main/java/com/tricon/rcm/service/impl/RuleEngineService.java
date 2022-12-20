package com.tricon.rcm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmInsuranceMainRootDto;
import com.tricon.rcm.dto.RcmRemoteLiteMainRootDto;
import com.tricon.rcm.dto.RcmRemoteLiteSiteDetailsDto;
import com.tricon.rcm.dto.RemoteLiteDto;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.dto.ClaimSourceDto;

import java.util.HashMap;
import java.util.List;

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

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

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
		// System.out.println(rootDto.getData().getDatas().get(0).getName());

		return true;
	}

	public HashMap<String, List<RemoteLiteDto>> pullRemoteLiteDate(ClaimSourceDto dto) {

		logger.info(" In pullRemoteLiteDate");
		dto.setPassword(ev.getProperty("rcm.ruleengine.password"));

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		HashMap<String, List<RemoteLiteDto>> map = new HashMap<>();
		String param = "?password=" + dto.getPassword();
		if (dto.getOfficeuuid() != null)
			param = param + "&office=" + dto.getOfficeuuid();

		// Call Rule Engine API..
		ResponseEntity<RcmRemoteLiteMainRootDto> result = restTemplate.exchange(
				ev.getProperty("rcm.pullremoteliteurl") + param, HttpMethod.GET, entity,
				RcmRemoteLiteMainRootDto.class);

		System.out.println(result.getBody());
		RcmRemoteLiteMainRootDto rootDto = result.getBody();
		try {
			List<RcmRemoteLiteSiteDetailsDto> data = rootDto.getData();
			for (RcmRemoteLiteSiteDetailsDto dto1 : data) {

				map.put(dto1.getOfficeName(), ConnectAndReadSheets.readRemoteLiteSheet(dto1.getGoogleSheetIdDb(),
						dto1.getPassword(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER));

			}
			// System.out.println(rootDto.getData().get(0).getOfficeId());
			// System.out.println(rootDto.getData().get(0).getGoogleSubId());

		} catch (Exception n) {
			n.printStackTrace();

		}
		return map;
	}

}
