package com.tricon.ruleengine.api.controller;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.RcmClaimDataDto;
import com.tricon.ruleengine.dto.RcmClaimRootDto;
import com.tricon.ruleengine.dto.RcmEnv;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDto;
import com.tricon.ruleengine.dto.TreatmentClaimDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Company;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.service.CompanyService;
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.service.ScrappingFullDataService;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.service.UserService;
import com.tricon.ruleengine.utils.Constants;

/**
 * For Accessing from RCM TOOL
 * 
 * @author Deepak.Dogra
 *
 */

@CrossOrigin
@RestController
public class RcmController {

	@Autowired
	GoogleReportsController googleReportsController;

	@Autowired
	Environment env;

	@Autowired
	CompanyService companyservice;

	@Autowired
	private UserService userService;

	@Autowired
	OfficeDao od;

	RcmEnv rcmEnvPrimaryClaim = null;
	RcmEnv rcmEnvSecondaryClaim = null;
	RcmEnv rcmEnvInsurance = null;

	@Autowired
	ScrappingFullDataService fullService;
	
	@Autowired
	TreatmentPlanService tPService;

	@Autowired
	CaplineIVFGoogleFormService civf;

	static Class<?> clazz = RcmController.class;

	@Autowired
	EagleSoftDBAccessService es;

	@PostConstruct
	public void init() {

		rcmEnvPrimaryClaim = new RcmEnv(env.getProperty("claim.unbilled.primary.query"),
				env.getProperty("claim.unbilled.primary.query.count"),
				env.getProperty("claim.unbilled.primary.query.selectcolumns"), env.getProperty("rmc.auth.token"));

		rcmEnvSecondaryClaim = new RcmEnv(env.getProperty("claim.unbilled.secondary.query"),
				env.getProperty("claim.unbilled.secondary.query.count"),
				env.getProperty("claim.unbilled.secondary.query.selectcolumns"), env.getProperty("rmc.auth.token"));

		rcmEnvInsurance = new RcmEnv(env.getProperty("claim.insurance.query"),
				env.getProperty("claim.insurance.query.count"), env.getProperty("claim.insurance.query.selectcolumns"),
				env.getProperty("rmc.auth.token"));

	}

	@RequestMapping(value = "/fetch-claims", method = RequestMethod.GET)
	public ResponseEntity<?> fetchClaims(@RequestHeader("x-api-key") String apiKey,
			@RequestParam(value = "office", required = false) String officeUuid,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "primarySecondary", required = true) String primarySecondary)
			throws JSONException, MalformedURLException, ClassNotFoundException, InterruptedException {

		String ids = null;
		// Office office = null;
		RuleEngineLogger.generateLogs(clazz, "ENTER fetch-claims From  Rule Engine" + new Date(), " INFO", null);

		if (!checkForKey(apiKey)) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report No Created Successfully", "Key Error"));
		}
		RcmClaimDataDto dd = null;
		RcmClaimRootDto rootDto = new RcmClaimRootDto();
		ArrayList<RcmClaimDataDto> datas = new ArrayList<>();

		rootDto.setDatas(datas);
		rootDto.setMessage("Claim Data Fetched Successfully");
		if (officeUuid != null) {
			Company cmp = companyservice.getCompanyByName(Constants.COMPANY_NAME);
			Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
			String officeName = "";
			if (offices.isPresent() && offices.get() != null) {
				officeName = offices.get().stream().filter(n -> n.getUuid().equals(officeUuid)).findFirst().get()
						.getName();

			}
			GenericResponse data = null;
			if (primarySecondary.equalsIgnoreCase("Primary")) {
				data = (GenericResponse) googleReportsController
						.fethESGoogleresponse(rcmEnvPrimaryClaim.getQuerySelectcolumns(), rcmEnvPrimaryClaim.getQuery(),
								ids, Integer.parseInt(rcmEnvPrimaryClaim.getQueryCount()), password, officeName, null,
								null)
						.getBody();
			} else {

				data = (GenericResponse) googleReportsController.fethESGoogleresponse(
						rcmEnvSecondaryClaim.getQuerySelectcolumns(), rcmEnvSecondaryClaim.getQuery(), ids,
						Integer.parseInt(rcmEnvSecondaryClaim.getQueryCount()), password, officeName, null, null)
						.getBody();
			}
			dd = new RcmClaimDataDto();
			dd.setOfficeName(officeUuid);
			dd.setData(data.getData());
			datas.add(dd);
		} else {
			Company cmp = companyservice.getCompanyByName(Constants.COMPANY_NAME);
			Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
			if (offices.isPresent() && offices.get() != null) {
				offices.get().forEach((n) -> {
					GenericResponse data = null;
					try {
						RcmClaimDataDto ddd = null;
						if (primarySecondary.equalsIgnoreCase("Primary")) {
							data = (GenericResponse) googleReportsController
									.fethESGoogleresponse(rcmEnvPrimaryClaim.getQuerySelectcolumns(),
											rcmEnvPrimaryClaim.getQuerySelectcolumns(), ids,
											Integer.parseInt(rcmEnvPrimaryClaim.getQueryCount()), password, n.getName(),
											null, null)
									.getBody();
						} else {
							data = (GenericResponse) googleReportsController
									.fethESGoogleresponse(rcmEnvSecondaryClaim.getQuerySelectcolumns(),
											rcmEnvSecondaryClaim.getQuerySelectcolumns(), ids,
											Integer.parseInt(rcmEnvSecondaryClaim.getQueryCount()), password,
											n.getName(), null, null)
									.getBody();
						}
						ddd = new RcmClaimDataDto();
						ddd.setOfficeName(n.getUuid());
						ddd.setData(data.getData());
						datas.add(ddd);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				});
			}
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Claim Data Fetched Successfully", rootDto));
	}

	@RequestMapping(value = "/fetch-insurance", method = RequestMethod.GET)
	public ResponseEntity<?> fetchInsurance(@RequestHeader("x-api-key") String apiKey,
			@RequestParam(value = "office", required = false) String officeUuid,
			@RequestParam(value = "password", required = true) String password)
			throws JSONException, MalformedURLException, ClassNotFoundException, InterruptedException {

		String ids = null;
		// Office office = null;
		RuleEngineLogger.generateLogs(clazz, "ENTER fetch-insurance From  Rule Engine" + new Date(), " INFO", null);

		if (!checkForKey(apiKey)) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report No Created Successfully", "Key Error"));
		}
		RcmClaimDataDto dd = null;
		RcmClaimRootDto rootDto = new RcmClaimRootDto();
		ArrayList<RcmClaimDataDto> datas = new ArrayList<>();

		rootDto.setDatas(datas);
		rootDto.setMessage("Insurance Data Fetched Successfully");
		if (officeUuid != null) {
			Company cmp = companyservice.getCompanyByName(Constants.COMPANY_NAME);
			Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
			String officeName = "";
			if (offices.isPresent() && offices.get() != null) {
				officeName = offices.get().stream().filter(n -> n.getUuid().equals(officeUuid)).findFirst().get()
						.getName();

			}
			GenericResponse data = (GenericResponse) googleReportsController
					.fethESGoogleresponse(rcmEnvInsurance.getQuerySelectcolumns(), rcmEnvInsurance.getQuery(), ids,
							Integer.parseInt(rcmEnvInsurance.getQueryCount()), password, officeName, null, null)
					.getBody();
			dd = new RcmClaimDataDto();
			dd.setOfficeName(officeUuid);
			dd.setData(data.getData());
			datas.add(dd);
		} else {
			Company cmp = companyservice.getCompanyByName(Constants.COMPANY_NAME);
			Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
			if (offices.isPresent() && offices.get() != null) {
				offices.get().forEach((n) -> {
					GenericResponse data = null;
					try {
						RcmClaimDataDto ddd = null;
						data = (GenericResponse) googleReportsController.fethESGoogleresponse(
								rcmEnvInsurance.getQuerySelectcolumns(), rcmEnvInsurance.getQuerySelectcolumns(), ids,
								Integer.parseInt(rcmEnvInsurance.getQueryCount()), password, n.getName(), null, null)
								.getBody();

						ddd = new RcmClaimDataDto();
						ddd.setOfficeName(n.getUuid());
						ddd.setData(data.getData());
						datas.add(ddd);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				});
			}
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Insurance Data Fetched Successfully", rootDto));
	}

	@GetMapping("/diagnostic_check_for_rcm")
	public ResponseEntity<Object> diagnosticCheckForRCM(@RequestHeader("x-api-key") String apiKey,
			@RequestParam(value = "office", required = true) String officeUuid) {
		String mess = null;
		try {

			if (!checkForKey(apiKey))
				return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Diagnostic Not Run", "Key Error"));
			Company cmp = companyservice.getCompanyByName(Constants.COMPANY_NAME);
			mess = es.doDiagnosticCheck(officeUuid, cmp.getUuid())[0];
		} catch (Exception n) {

		}

		return ResponseEntity.ok(mess);
	}

	@GetMapping("/remote-lite-details")
	public ResponseEntity<Object> getRemoteLiteSiteDetails(@RequestHeader("x-api-key") String apiKey,
			@RequestParam(value = "office", required = false) String officeUuid) {

		List<ScrappingFullDataDetailDto> remoteList = new ArrayList<>();
		ScrappingFullDataDetailDto d = null;
		try {

			if (!checkForKey(apiKey))
				return ResponseEntity
						.ok(new GenericResponse(HttpStatus.OK, "Remote Lite Data not fetched", "Key Error"));

			List<ScrappingFullDataDto> list = fullService.getSiteNamesBySiteType("LITE");

			ScrappingFullDataDto scrap = list.get(0);

			String email = "admin_admin";
			if (officeUuid != null) {
				Company cmp = companyservice.getCompanyByName(Constants.COMPANY_NAME);
				Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
				// Office office =null;
				if (offices.isPresent() && offices.get() != null) {
					OfficeDto od = offices.get().stream().filter(n -> n.getUuid().equals(officeUuid)).findFirst().get();
					d = fullService.getScrappingDetailsForRcm(scrap.getId(), od.getUuid(), email);
					d.setOfficeId(od.getUuid());
					d.setGoogleSheetIdDb("1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw");
					d.setPassword(od.getName());
					remoteList.add(d);
				}
			} else {

				Company cmp = companyservice.getCompanyByName(Constants.COMPANY_NAME);
				Optional<List<OfficeDto>> offices = userService.getAllOffices(cmp.getUuid());
				for (OfficeDto off : offices.get()) {
					d = fullService.getScrappingDetailsForRcm(scrap.getId(), off.getUuid(), email);
					d.setGoogleSheetIdDb("1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw");
					d.setOfficeId(off.getUuid());
					d.setPassword(off.getName());
					remoteList.add(d);
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RuleEngineLogger.generateLogs(clazz, "ScrappingController", Constants.rule_log_debug, null);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "remote-lite-details", remoteList));

	}

	@RequestMapping(value = "/ivf-data-for-rcm", method = RequestMethod.GET)
	public ResponseEntity<?> claimData(@RequestHeader("x-api-key") String apiKey,
			@RequestParam(value = "office", required = true) String officeUuid,
			@RequestParam(value = "cmpId", required = true) String cmpId,
			@RequestParam(value = "ivId", required = true) String ivId,
			@RequestParam(value = "patientId", required = true) String patientId)
			throws JSONException, MalformedURLException, ClassNotFoundException, InterruptedException {

		List<CaplineIVFFormDto> d = null;
		// Office office = null;
		RuleEngineLogger.generateLogs(clazz, "ENTER IVF DATA From  Rule Engine" + new Date(), " INFO", null);

		if (!checkForKey(apiKey)) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Not Created", "Key Error"));
		}

		CaplineIVFQueryFormDto dto = new CaplineIVFQueryFormDto();
		Office office = od.getOfficeByUuid(officeUuid, cmpId);
		dto.setUniqueID(ivId);
		dto.setPatientIdDB(patientId);
		try {
			d = (List<CaplineIVFFormDto>) civf.searchIVFData(dto, office);
		} catch (Exception n) {

		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "IVF DATA Fetched Successfully", d));
	}

	@RequestMapping(value = "/claim-data-form-es", method = RequestMethod.GET)
	public ResponseEntity<?> claimDataFromES(@RequestHeader("x-api-key") String apiKey,
			@RequestParam(value = "office", required = true) String officeUuid,
			@RequestParam(value = "cmpId", required = true) String cmpId,
			@RequestParam(value = "claimId", required = true) String claimId)
			throws JSONException, MalformedURLException, ClassNotFoundException, InterruptedException {

		RuleEngineLogger.generateLogs(clazz, "ENTER Claim Data From ES" + new Date(), " INFO", null);

		if (!checkForKey(apiKey)) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Not Created", "Key Error"));
		}

		TreatmentClaimDto dto = new TreatmentClaimDto();
		Office office = od.getOfficeByUuid(officeUuid, cmpId);
		dto.setOfficeId(office.getUuid());
		dto.setDataId(claimId);
		dto.setType(Constants.userType_CL);
		Object data=null;
		try {
			data =tPService.getTreatmentClaimDataForRCM(dto,cmpId);
			
		} catch (Exception n) {

		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Claim DATA Fetched Successfully", data));
	}
	
	private boolean checkForKey(String apiKey) {

		if (apiKey == null || !apiKey.equals(rcmEnvPrimaryClaim.getApiKey())) {
			RuleEngineLogger.generateLogs(clazz, "ERROR -in KEY" + new Date(), " INFO", null);
			return false;

		} else
			return true;

	}
}
