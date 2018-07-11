package com.tricon.ruleengine.utils;

import java.text.ParseException;
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
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;


/**
 * @author Deepak.Dogra
 *
 */

public class RuleBook {

   private static Locale locale=new Locale("en");

	@Autowired
	MessageSource messageSource;
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
				System.out.println("RRR"+tp.getTreatmentPlanDetails().getDateLastUpdated());
				tpDate= Constants.SIMPLE_DATE_FORMAT.parse(tp.getTreatmentPlanDetails().getDateLastUpdated());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return  new TPValidationResponseDto(Constants.RULE_ID_1,rule.getName(),
						messageSource.getMessage("rule1.error.message.date", new Object[] {tp.getTreatmentPlanDetails().getDateLastUpdated()}, locale),Constants.FAIL);		
				//throw new RuleEngineException("");
			}
			try {
				System.out.println("RRRQQ"+ivf.getPlanEffectiveDate());
				ivfDate=  Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate());
			} catch (ParseException e) {
				return new TPValidationResponseDto(Constants.RULE_ID_1,rule.getName(),
						messageSource.getMessage("rule1.error.message.date", new Object[] {ivf.getPlanEffectiveDate()}, locale),Constants.FAIL);	
				
			}
			// DOS > Effective Date then error
			System.out.println("DateUtils.compareDates(tpDate,ivfDate)"+DateUtils.compareDates(tpDate,ivfDate));
			if (tpDate!=null && ivfDate!=null && DateUtils.compareDates(tpDate,ivfDate)) {
				// pass --RULE_ID_1
				return new TPValidationResponseDto(Constants.RULE_ID_1,rule.getName(),
						messageSource.getMessage("rule1.message.pass", null, locale),Constants.PASS);
             
				
				
			}else {
				return new TPValidationResponseDto(Constants.RULE_ID_1,rule.getName(),
						messageSource.getMessage("rule1.error.message", new Object[] {tp.getTreatmentPlanDetails().getDateLastUpdated(),
								ivf.getPlanEffectiveDate()}, locale),Constants.FAIL);	
				
			}
			}
		return null;//see this
		}
	
		
	
}
