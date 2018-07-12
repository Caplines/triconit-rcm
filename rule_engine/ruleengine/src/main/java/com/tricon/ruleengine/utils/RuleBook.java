package com.tricon.ruleengine.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.exception.RuleEngineException;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.EagleSoftCB;
import com.tricon.ruleengine.model.sheet.EagleSoftFSName;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.MappingTableCodeMaster;
import com.tricon.ruleengine.model.sheet.MappingTableFeeSN;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;


/**
 * @author Deepak.Dogra
 *
 */

public class RuleBook {

   private static Locale locale=new Locale("en");

	/**
	 * Eligibility of the patient'
	 * Compare Effective Date in IV and DOS in TP. 
       If DOS > Effective Date Then eligible, continue with processing, 
        Else Output Error
	 * @param tpList (Treatment Plan - DOS)
	 * @param ivfSheet (IV - Effective Date)
	 */
	public TPValidationResponseDto Rule1(List<Object> tpList,Object ivfSheet,MessageSource messageSource,Rules rule ) {
        Date tpDate=null;
        Date ivfDate=null;//
		IVFTableSheet ivf=(IVFTableSheet)ivfSheet;
		for(Object obj:tpList) {
			TreatmentPlan tp=(TreatmentPlan) obj;
			try {
				DateUtils.CheckForStringInDate(tp.getTreatmentPlanDetails().getDateLastUpdated());
			}catch(RuleEngineDateException e){
				return new TPValidationResponseDto(rule.getId(),rule.getName(),
						messageSource.getMessage("rule1.error.message.date", new Object[] {ivf.getPlanEffectiveDate()}, locale),Constants.FAIL);	
				
			}
			try {
				System.out.println("RRR"+tp.getTreatmentPlanDetails().getDateLastUpdated());
				tpDate= Constants.SIMPLE_DATE_FORMAT.parse(tp.getTreatmentPlanDetails().getDateLastUpdated());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return  new TPValidationResponseDto(rule.getId(),rule.getName(),
						messageSource.getMessage("rule1.error.message.date", new Object[] {tp.getTreatmentPlanDetails().getDateLastUpdated()}, locale),Constants.FAIL);		
				//throw new RuleEngineException("");
			}
			try {
				DateUtils.CheckForStringInDate(ivf.getPlanEffectiveDate());
			}catch(RuleEngineDateException e){
				return new TPValidationResponseDto(rule.getId(),rule.getName(),
						messageSource.getMessage("rule1.error.message.date", new Object[] {ivf.getPlanEffectiveDate()}, locale),Constants.FAIL);	
			}			
     			try {
				System.out.println("RRRQQ"+ivf.getPlanEffectiveDate());
				ivfDate=  Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate());
			} catch (ParseException e) {
				return new TPValidationResponseDto(rule.getId(),rule.getName(),
						messageSource.getMessage("rule1.error.message.date", new Object[] {ivf.getPlanEffectiveDate()}, locale),Constants.FAIL);	
				
			}
			// DOS > Effective Date then error
			System.out.println("DateUtils.compareDates(tpDate,ivfDate)"+DateUtils.compareDates(tpDate,ivfDate));
			if (tpDate!=null && ivfDate!=null && DateUtils.compareDates(tpDate,ivfDate)) {
				// pass --RULE_ID_1
				return new TPValidationResponseDto(rule.getId(),rule.getName(),
						messageSource.getMessage("rule1.message.pass", null, locale),Constants.PASS);
             
				
				
			}else {
				return new TPValidationResponseDto(rule.getId(),rule.getName(),
						messageSource.getMessage("rule1.error.message", new Object[] {tp.getTreatmentPlanDetails().getDateLastUpdated(),
								ivf.getPlanEffectiveDate()}, locale),Constants.FAIL);	
				
			}
			}
		return null;//see this
		}
	
	
	public TPValidationResponseDto Rule4(List<Object> tpList,Object ivfSheet,MessageSource messageSource,Rules rule,
			List<MappingTableCodeMaster>  mappings,List<MappingTableFeeSN>  mappingsfee,List<EagleSoftCB>  escbs,List<EagleSoftFeeShedule>  esfeess,List<EagleSoftFSName>  esfsnames) {

		IVFTableSheet ivf=(IVFTableSheet)ivfSheet;
		String coverageBook =ivf.getPlanCoverageBook();
		String fs =ivf.getPlanFeeScheduleName();
		
		boolean case1=false;
		for(EagleSoftCB ebCoverage:escbs) {
			if (ebCoverage.getCovBbookHeaderName()!=null  && ebCoverage.getCovBbookHeaderName().equals(coverageBook)){
				case1=true;
				break;
			}
		}
		return null;
	
	}
	

	public TPValidationResponseDto Rule5(List<Object> tpList,Object ivfSheet,MessageSource messageSource,Rules rule,
			List<MappingTableCodeMaster>  mappings,List<MappingTableFeeSN>  mappingsfee,List<EagleSoftCB>  escbs,List<EagleSoftFeeShedule>  esfeess,List<EagleSoftFSName>  esfsnames) {

		IVFTableSheet ivf=(IVFTableSheet)ivfSheet;
		String coverageBook =ivf.getPlanCoverageBook();
		String fs =ivf.getPlanFeeScheduleName();
		//IV - Remaining Deductible, Remaining Balance, Benefit Max
		//Eaglesoft - Remaining Deductible, Remaining Balance, Benefit Max
		ivf.getPlanIndividualDeductible();//Remaining Deductible
		ivf.getPlanIndividualDeductible();//Remaining Deductible
		ivf.getPlanAnnualMaxUsed();//Benefit Max
		//ivf.
		//boolean case1=false;
		for(EagleSoftCB ebCoverage:escbs) {
			if (ebCoverage.getCovBbookHeaderName()!=null  && ebCoverage.getCovBbookHeaderName().equals(coverageBook)){
				//case1=true;
				break;
			}
		}
		return null;
	
	}
	
	public TPValidationResponseDto Rule6(List<Object> tpList,Object ivfSheet,MessageSource messageSource,Rules rule,
			List<MappingTableCodeMaster>  mappings,List<MappingTableFeeSN>  mappingsfee,List<EagleSoftCB>  escbs,List<EagleSoftFeeShedule>  esfeess,List<EagleSoftFSName>  esfsnames) {

		IVFTableSheet ivf=(IVFTableSheet)ivfSheet;
		String coverageBook =ivf.getPlanCoverageBook();
		String fs =ivf.getPlanFeeScheduleName();
		
		boolean case1=false;
		for(EagleSoftCB ebCoverage:escbs) {
			if (ebCoverage.getCovBbookHeaderName()!=null  && ebCoverage.getCovBbookHeaderName().equals(coverageBook)){
				case1=true;
				break;
			}
		}
		return null;
	
	}
	
	public List<TPValidationResponseDto> Rule7(Object ivfSheet,MessageSource messageSource,Rules rule) {

		IVFTableSheet ivf=(IVFTableSheet)ivfSheet;
		List<TPValidationResponseDto> li=null;
		if (ivf.getPlanNonDuplicateClause().equalsIgnoreCase("yes")) {
			if (li==null) li=new ArrayList<>();
			li.add(new TPValidationResponseDto(rule.getId(),rule.getName(),
					messageSource.getMessage("rule7.error.message_duplicate", null, locale),Constants.FAIL));
		}
		if (ivf.getPlanPreDMandatory().equalsIgnoreCase("yes")) {
			if (li==null) li=new ArrayList<>();
			li.add(new TPValidationResponseDto(rule.getId(),rule.getName(),
					messageSource.getMessage("rule7.error.message_preman",null, locale),Constants.FAIL));
		}
		if (ivf.getPlanFullTimeStudentStatus().equalsIgnoreCase("yes")) {
			if (li==null) li=new ArrayList<>();
			li.add(new TPValidationResponseDto(rule.getId(),rule.getName(),
					messageSource.getMessage("rule7.error.message_fulltime", null, locale),Constants.FAIL));
		}
		if (ivf.getPlanAssignmentofBenefits().equalsIgnoreCase("yes")) {
			if (li==null) li=new ArrayList<>();
			li.add(new TPValidationResponseDto(rule.getId(),rule.getName(),
					messageSource.getMessage("rule7.error.message_benefit", null, locale),Constants.FAIL));
		}
		return li;
	
	}
	
}
