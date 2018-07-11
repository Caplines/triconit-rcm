package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Collections2;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.RuleBook;

@Transactional
@Service
public class TreatmentPlanServiceImpl implements TreatmentPlanService {

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@Autowired
	TreatmentValidationDao tvd;

	@Autowired
	MessageSource messageSource;

	@Override
	public List<TPValidationResponseDto> validateTreatmentPlan(TreatmentPlanValidationDto dto) {

		// Read Treatment Plan Sheet from google Drive.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(authentication.getName());
		List<Rules> rules = tvd.getAllActiveRules();
		Rules rule=null;
		List<TPValidationResponseDto> list=new ArrayList<TPValidationResponseDto>();
		if (rules != null && rules.size() == 0) {
			list.add(new TPValidationResponseDto(0, "Generic- No Active Rules Found", "Generic- No Active Rules Found",
					""));
			return list;
		}
		GoogleSheets tpsheet = tvd.getSheetByAppSheetId(Constants.treatmentPlanSheetID);

		try {
			// Treatment Plan Sheet
			List<Object> tList = ConnectAndReadSheets.readSheet(tpsheet.getSheetId(), tpsheet.getSheetName(),
					dto.getTreatmentPlanId(), Constants.SHEET_TYPE_TP, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);

			if (tList == null)
				list.add( new TPValidationResponseDto(0, "Generic- Invalid Treatment Plan", "Invalid Treatment Plan", ""));
			else {

				// Read IVF DATA
				GoogleSheets ivsheet = tvd.getSheetByAppSheetId(Constants.ivTableDataSheetID);
				List<Object> ivfList = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(), ivsheet.getSheetName(),
						((TreatmentPlan) (tList.get(0))).getUniqueId(), Constants.SHEET_TYPE_IVF_DATA,
						CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
				if (ivfList != null) {

					// RULE_ID_1
					rule= getRulesFromList(rules, Constants.RULE_ID_1);
					RuleBook rb = new RuleBook();
					list.add(rb.Rule1(tList, ivfList.get(0), messageSource, rule));
					// RULE_ID_2

				} else {
					list.add( new TPValidationResponseDto(0,
							"Generic- No Data Found in IVF sheet for " + dto.getTreatmentPlanId(),
							"No Data Found in IVF Sheet.", ""));
				}

			}

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return list;
	}

	private Rules getRulesFromList(List<Rules> rules, int id) {
		Rules r = null;
		Collection<Rules> ruleGen = Collections2.filter(rules, rule -> rule.getId() == id);
		for (Rules rule : ruleGen) {
			r = rule;
		}

		return r;
	}
	
	private void saveReports() {
		
	
	}

}
