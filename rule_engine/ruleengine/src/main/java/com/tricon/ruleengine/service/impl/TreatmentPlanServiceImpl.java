package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
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
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.sheet.EagleSoftCB;
import com.tricon.ruleengine.model.sheet.EagleSoftFSName;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.MappingTableCodeMaster;
import com.tricon.ruleengine.model.sheet.MappingTableFeeSN;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.RuleBook;

/**
 * @author Deepak.Dogra
 *
 */
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
	UserDao userDao;

	@Autowired
	MessageSource messageSource;

	@Override
	public List<TPValidationResponseDto> validateTreatmentPlan(TreatmentPlanValidationDto dto) {

		// Read Treatment Plan Sheet from google Drive.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<Rules> rules = tvd.getAllActiveRules();
		List<GoogleSheets> sheets = tvd.getAllGoogleSheet();
		Rules rule=null;
		TPValidationResponseDto dtoR=null;
		List<TPValidationResponseDto> list=new ArrayList<TPValidationResponseDto>();
		if (rules != null && rules.size() == 0) {
		   dtoR=new TPValidationResponseDto(0, "Generic", "Generic- No Active Rules Found",
					Constants.FAIL);
			list.add(dtoR);
			return list;
		}
		GoogleSheets tpsheet =  getGoogleSheetFromList(sheets, Constants.treatmentPlanSheetID);
		//GoogleSheets mapping = tvd.getSheetByAppSheetId(Constants.mappingSheetID);
		

		try {
		// Treatment Plan Sheet
			List<Object> tList = ConnectAndReadSheets.readSheet(tpsheet.getSheetId(), tpsheet.getSheetName(),
					dto.getTreatmentPlanId(), Constants.SHEET_TYPE_TP, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);

			if (tList == null) {
				rule= getRulesFromList(rules, Constants.RULE_ID_2);
				dtoR= new TPValidationResponseDto(rule.getId(), rule.getName(), "Invalid Treatment Plan", Constants.FAIL);
				saveReports(authentication, rule, dtoR,dto,null);
				list.add(dtoR);
			}
			else {

				// Read IVF DATA
				 GoogleSheets ivsheet =  getGoogleSheetFromList(sheets,Constants.ivTableDataSheetID);
				 
				List<Object> ivfList = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(), ivsheet.getSheetName(),
						((TreatmentPlan) (tList.get(0))).getUniqueId(), Constants.SHEET_TYPE_IVF_DATA,
						CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
				if (ivfList != null) {

					// RULE_ID_1
					
					rule= getRulesFromList(rules, Constants.RULE_ID_1);
					RuleBook rb = new RuleBook();
					dtoR=rb.Rule1(tList, ivfList.get(0), messageSource, rule);
					list.add(dtoR);
					dtoR= new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(), dtoR.getResultType());
					
					saveReports(authentication, rule, dtoR, dto,(IVFTableSheet)(ivfList.get(0)));
					//END RULE 1
					
					//start --Read Mapping Table && Eagle Soft table
					GoogleSheets mappingSheet =  getGoogleSheetFromList(sheets,Constants.mappingSheetID_CM);
					List<MappingTableCodeMaster>  mappings =(List<MappingTableCodeMaster>) (List<?>)ConnectAndReadSheets.readSheet(mappingSheet.getSheetId(), mappingSheet.getSheetName(), null, Constants.SHEET_TYPE_MappingTable_CM, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					GoogleSheets mappingSheetFee =  getGoogleSheetFromList(sheets,Constants.mappingSheetID_FEE);
					List<MappingTableFeeSN>  mappingsfee =(List<MappingTableFeeSN>) (List<?>)ConnectAndReadSheets.readSheet(mappingSheet.getSheetId(), mappingSheet.getSheetName(), null, Constants.SHEET_TYPE_MappingTable_FEESN, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					//Read Eagle Soft sheet
					GoogleSheets escb =  getGoogleSheetFromList(sheets,Constants.eagleSoftCoverageSheetID);
					List<EagleSoftCB>  escbs =(List<EagleSoftCB>) (List<?>) ConnectAndReadSheets.readSheet(escb.getSheetId(), escb.getSheetName(), null, Constants.SHEET_TYPE_ES_COVERAGE, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					GoogleSheets esfees =  getGoogleSheetFromList(sheets,Constants.eagleSoftFSANDFEESheetID);
					List<EagleSoftFeeShedule>  esfeess =(List<EagleSoftFeeShedule>) (List<?>)ConnectAndReadSheets.readSheet(esfees.getSheetId(), esfees.getSheetName(), null, Constants.SHEET_TYPE_FS_FEE, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					GoogleSheets esfsname =  getGoogleSheetFromList(sheets,Constants.eagleSoftFSNAMESheetID);
					List<EagleSoftFSName>  esfsnames = (List<EagleSoftFSName>) (List<?>)ConnectAndReadSheets.readSheet(esfsname.getSheetId(), esfsname.getSheetName(), null, Constants.SHEET_TYPE_FS_NAME, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					
					//End --Read Mapping Table && Eagle Soft table
					
					/*
					// RULE_ID_4 "Coverage Book"
   				    rule= getRulesFromList(rules, Constants.RULE_ID_4);
					rb = new RuleBook();
					dtoR=rb.Rule4(tList, ivfList.get(0), messageSource, rule,mappings,mappingsfee,escbs,esfeess,esfsnames);
					list.add(dtoR);
					dtoR= new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(),dtoR.getResultType());
					saveReports(authentication, rule, dtoR, dto);
					//END
					*/
					// RULE_ID_4 "Remaining Deductible, Remaining Balance and Benefit Max as per IV form"
   				    rule= getRulesFromList(rules, Constants.RULE_ID_5);
					rb = new RuleBook();
					dtoR=rb.Rule5(tList, ivfList.get(0), messageSource, rule,mappings,mappingsfee,escbs,esfeess,esfsnames);
					list.add(dtoR);
					dtoR= new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(), dtoR.getResultType());
					saveReports(authentication, rule, dtoR, dto,(IVFTableSheet)(ivfList.get(0)));
					//END
					/*
					// RULE_ID_4 "Remaining Deductible, Remaining Balance and Benefit Max as per IV form"
   				    rule= getRulesFromList(rules, Constants.RULE_ID_6);
					rb = new RuleBook();
					dtoR=rb.Rule6(tList, ivfList.get(0), messageSource, rule,mappings,mappingsfee,escbs,esfeess,esfsnames);
					list.add(dtoR);
					dtoR= new TPValidationResponseDto(rule.getId(), rule.getName(), dtoR.getMessage(), dtoR.getResultType());
					//END
					 */
					// RULE_ID_4 "Remaining Deductible, Remaining Balance and Benefit Max as per IV form"
   				    rule= getRulesFromList(rules, Constants.RULE_ID_7);
					rb = new RuleBook();
					List<TPValidationResponseDto> dtoRL=rb.Rule7( ivfList.get(0), messageSource,rule);
					
					if (dtoRL!=null) { list.addAll(dtoRL);
					for (TPValidationResponseDto t:dtoRL) {
					dtoR= new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(), t.getResultType());
					saveReports(authentication, rule, t, dto,(IVFTableSheet)(ivfList.get(0)));
					}
					}
					//END
					

				} else {
					rule= getRulesFromList(rules, Constants.RULE_ID_3);
					dtoR= new TPValidationResponseDto(rule.getId(),
							rule.getName(),
							"No Data Found in IVF Sheet.", Constants.FAIL);
					list.add(dtoR);
					saveReports(authentication, rule,dtoR, dto,(IVFTableSheet)(ivfList.get(0)));
				}

			}

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return list;
	}

	private Rules getRulesFromList(List<Rules> rules, String name) {
		Rules r = null;
		Collection<Rules> ruleGen = Collections2.filter(rules, rule -> rule.getShortName().equals(name));
		for (Rules rule : ruleGen) {
			r = rule;
		}

		return r;
	}
	
	private GoogleSheets getGoogleSheetFromList(List<GoogleSheets> sheets, int id) {
		GoogleSheets s = null;
		Collection<GoogleSheets> ruleGen = Collections2.filter(sheets, sh -> sh.getId()==id);
		for (GoogleSheets gs : ruleGen) {
			s = gs;
		}

		return s;
	}

	private void saveReports(Authentication authentication,Rules rules,TPValidationResponseDto dtoR, TreatmentPlanValidationDto dto,IVFTableSheet ivfSheet) {
		String email="admin@admin.com";
		User user=null;
		boolean update=false;
		if (authentication!=null) {
			user= userDao.findUserByEmail(authentication.getName());
			//Hibernate.initialize(user.getOffice());
		}else {
			user= userDao.findUserByEmail(email);
		}
	     //get Data from Reports by Treatment Plan ID..get
		Reports reports= tvd.getReportsByTreamentPlanId(dto.getTreatmentPlanId());
		if (reports==null) {
			reports= new Reports();
			reports.setCreatedBy(user);
			if (ivfSheet!=null) {reports.setPatientName(ivfSheet.getPatientName());
			reports.setPatientDob(ivfSheet.getPatientDOB());
			}
			reports.setTreatementPlanId(dto.getTreatmentPlanId());
			//rep.setOfficeId(user.getOffice().getUuid());
			Serializable id= tvd.saveReports(reports);
			reports.setId((int)id);
		}else {
			update=true;
		}
		//save Report Details
		ReportDetail rd=new ReportDetail();
		rd.setErrorMessage(dtoR.getMessage());
		rd.setRules(rules);
		rd.setCreatedBy(user);
		rd.setReports(reports);
		tvd.saveReportDestail(rd);
		//update reports
		if (update) {
			reports.setUpdatedBy(user);
		   tvd.updateReportDate(reports);
		}
		//
	}

}
