package com.tricon.rcm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmInsuranceDatas;
import com.tricon.rcm.dto.RcmInsuranceMainRootDto;
import com.tricon.rcm.dto.RcmRemoteLiteMainRootDto;
import com.tricon.rcm.dto.RcmRemoteLiteSiteDetailsDto;
import com.tricon.rcm.dto.RemoteLiteDto;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.ClaimsFromRuleEngine;
import com.tricon.rcm.dto.InsuranceFromRuleEngine;
import com.tricon.rcm.dto.RcmClaimDataDto;

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

	@Autowired
	RcmInsuranceRepo insuranceRepo;

	@Autowired
	RcmOfficeRepository officeRepo;

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

	public RcmClaimMainRootDto pullClaimFromRE(ClaimSourceDto dto) {

		logger.info(" In pullClaimFromRE");
		RcmClaimMainRootDto mainRoot=null;
		dto.setPassword(ev.getProperty("rcm.ruleengine.password"));
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + dto.getPassword();
			if (dto.getOfficeuuid() != null)
				param = param + "&office=" + dto.getOfficeuuid();

			// Call Rule Engine API..
			ResponseEntity<RcmClaimMainRootDto> result = restTemplate.exchange(
					ev.getProperty("rmc.pullclaimurl") + param, HttpMethod.GET, entity, RcmClaimMainRootDto.class);

			mainRoot = result.getBody();
			/*
			for (RcmClaimDataDto datas : rootDto.getData().getDatas()) {
				String OfficeUuid = datas.getOfficeName();
				try {
					for (ClaimsFromRuleEngine re : datas.getData()) {
						insurance = new RcmInsurance();
						insurance.setInsuranceId(re.getInsuranceCompanyId());
						insurance.setName(re.getName());
						insurance.setOffice(officeRepo.findByUuid(OfficeUuid));
						insuranceOld = insuranceRepo.findByInsuranceId(re.getInsuranceCompanyId());
						if (insuranceOld == null)
							insuranceRepo.save(insurance);
						else {
							insuranceOld.setName(insurance.getName());
							insuranceRepo.save(insuranceOld);
						}
					}
				} catch (Exception n) {

				}
				
			}*/
		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
		}
		return mainRoot;
	}

	public boolean pullIAndSaveInsuranceFromRE(ClaimSourceDto dto) {

		logger.info(" In pull Insurance From RE");
		dto.setPassword(ev.getProperty("rcm.ruleengine.password"));
		try {
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String param = "?password=" + dto.getPassword();
			if (dto.getOfficeuuid() != null)
				param = param + "&office=" + dto.getOfficeuuid();
			// Call Rule Engine API..
			ResponseEntity<RcmInsuranceMainRootDto> result = restTemplate.exchange(
					ev.getProperty("rmc.pullInsuranceUrl") + param, HttpMethod.GET, entity,
					RcmInsuranceMainRootDto.class);

			RcmInsuranceMainRootDto rootDto = result.getBody();
			RcmInsurance insurance = null;
			RcmInsurance insuranceOld = null;

			for (RcmInsuranceDatas datas : rootDto.getData().getDatas()) {
				String OfficeUuid = datas.getOfficeName();
				try {
					for (InsuranceFromRuleEngine re : datas.getData()) {
						insurance = new RcmInsurance();
						insurance.setInsuranceId(re.getInsuranceCompanyId());
						insurance.setName(re.getName());
						insurance.setOffice(officeRepo.findByUuid(OfficeUuid));
						insuranceOld = insuranceRepo.findByInsuranceId(re.getInsuranceCompanyId());
						if (insuranceOld == null)
							insuranceRepo.save(insurance);
						else {
							insuranceOld.setName(insurance.getName());
							insuranceRepo.save(insuranceOld);
						}
					}
				} catch (Exception n) {

				}
			}

		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
		}

		// System.out.println(rootDto.getData().getDatas().get(0).getName());

		return true;
	}

	public HashMap<String, List<RemoteLiteDto>> pullRemoteLiteDate(ClaimSourceDto dto) {

		logger.info(" In pullRemoteLiteDate");
		dto.setPassword(ev.getProperty("rcm.ruleengine.password"));
		HashMap<String, List<RemoteLiteDto>> map = new HashMap<>();
		try {
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
			try {
				List<RcmRemoteLiteSiteDetailsDto> data = rootDto.getData();
				for (RcmRemoteLiteSiteDetailsDto dto1 : data) {

					map.put(dto1.getOfficeName(), ConnectAndReadSheets.readRemoteLiteSheet(dto1.getGoogleSheetIdDb(),
							dto1.getPassword(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER));
					System.err.println(map.get(dto1.getOfficeName()).size());

				}
				// System.out.println(rootDto.getData().get(0).getOfficeId());
				// System.out.println(rootDto.getData().get(0).getGoogleSubId());

			} catch (Exception n) {
				n.printStackTrace();

			}
		} catch (Exception n) {
			logger.error("Error in " + dto.getOfficeuuid());
			logger.error(n.getMessage());
		}
		return map;
	}

}
