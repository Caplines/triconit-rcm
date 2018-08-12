package com.tricon.ruleengine.utils;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.BufferedWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSource;

import com.google.common.collect.Collections2;
import com.tricon.ruleengine.api.controller.RuleEngineValidationController;
import com.tricon.ruleengine.dto.FreqencyDto;
import com.tricon.ruleengine.dto.HistoryMatcherDto;
import com.tricon.ruleengine.dto.Rule6Dto;
import com.tricon.ruleengine.dto.ServiceCodeDateDto;
import com.tricon.ruleengine.dto.ServiceCodeIvfTimesFreqFieldDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.EagleSoftEmployerMaster;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;

/**
 * @author Deepak.Dogra
 *
 */

public class RuleBook {

	private static Locale locale = new Locale("en");
	static Class<?> clazz = RuleBook.class;

	/**
	 * Eligibility of the patient' Compare Effective Date in IV and DOS in TP. If
	 * DOS > Effective Date Then eligible, continue with processing, Else Output
	 * Error
	 * 
	 * @param tpList
	 *            (Treatment Plan - DOS)
	 * @param ivfSheet
	 *            (IV - Effective Date)
	 */
	public TPValidationResponseDto Rule1(Object ivfSheet, MessageSource messageSource, Rules rule,BufferedWriter bw) {
		// Date tpDate = null;
		RuleEngineLogger.generateLogs(clazz,Constants.rule_log_enter + "-" + Constants.RULE_ID_1, Constants.rule_log_debug,bw);
        IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		Date currentDate = new Date();
		Date ivfDate = null;
		
		//
		try {
			ivfDate = Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate());
			RuleEngineLogger.generateLogs(clazz,"Current Date:"+currentDate+"- PlanEffective Date:"+ivfDate, Constants.rule_log_debug,bw);
	        if (ivfDate != null && DateUtils.compareDates(currentDate, ivfDate)) {
				// pass --RULE_ID_1
				
				return new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS);

			} else {
				
				return new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule1.error.message", new Object[] {
								Constants.SIMPLE_DATE_FORMAT.format(currentDate), ivf.getPlanEffectiveDate() }, locale),
						Constants.FAIL);

			}
		} catch (Exception ex) {
			return new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule1.error.message.date", new Object[] { ivf.getPlanEffectiveDate() }, locale), Constants.FAIL);

		}
		/*
		 * 
		 * for (Object obj : tpList) { TreatmentPlan tp = (TreatmentPlan) obj; try {
		 * System.out.println("0000000000"); System.out.println(tp);
		 * System.out.println(tp.getId());
		 * DateUtils.CheckForStringInDate(tp.getTreatmentPlanDetails().
		 * getDateLastUpdated()); } catch (Exception e) { e.printStackTrace(); return
		 * new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
		 * .getMessage("rule1.error.message.date", new Object[] {
		 * tp.getTreatmentPlanDetails().getDateLastUpdated() }, locale),
		 * Constants.FAIL);
		 * 
		 * } try { System.out.println("RRR" +
		 * tp.getTreatmentPlanDetails().getDateLastUpdated()); tpDate =
		 * Constants.SIMPLE_DATE_FORMAT.parse(tp.getTreatmentPlanDetails().
		 * getDateLastUpdated()); } catch (ParseException e) { // TODO Auto-generated
		 * catch block // e.printStackTrace(); return new
		 * TPValidationResponseDto(rule.getId(), rule.getName(),
		 * messageSource.getMessage("rule1.error.message.date", new Object[] {
		 * tp.getTreatmentPlanDetails().getDateLastUpdated() }, locale),
		 * Constants.FAIL); // throw new RuleEngineException(""); } try {
		 * DateUtils.CheckForStringInDate(ivf.getPlanEffectiveDate()); } catch
		 * (RuleEngineDateException e) { return new
		 * TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
		 * .getMessage("rule1.error.message.date", new Object[] {
		 * ivf.getPlanEffectiveDate() }, locale), Constants.FAIL); } try {
		 * System.out.println("RRRQQ" + ivf.getPlanEffectiveDate()); ivfDate =
		 * Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate()); } catch
		 * (ParseException e) { return new TPValidationResponseDto(rule.getId(),
		 * rule.getName(), messageSource .getMessage("rule1.error.message.date", new
		 * Object[] { ivf.getPlanEffectiveDate() }, locale), Constants.FAIL);
		 * 
		 * } // DOS > Effective Date then error
		 * System.out.println("DateUtils.compareDates(tpDate,ivfDate)" +
		 * DateUtils.compareDates(tpDate, ivfDate)); if (tpDate != null && ivfDate !=
		 * null && DateUtils.compareDates(tpDate, ivfDate)) { // pass --RULE_ID_1 return
		 * new TPValidationResponseDto(rule.getId(), rule.getName(),
		 * messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS);
		 * 
		 * } else { return new TPValidationResponseDto(rule.getId(), rule.getName(),
		 * messageSource.getMessage("rule1.error.message", new Object[] {
		 * tp.getTreatmentPlanDetails().getDateLastUpdated(), ivf.getPlanEffectiveDate()
		 * }, locale), Constants.FAIL);
		 * 
		 * } }
		 */
		// return null;// see this
	}

	// Compare Coverage Book, Fee Schedule and Fee in IV and Eaglesoft - B -- Normal
	// Mode
	public List<TPValidationResponseDto> Rule4_B(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings, List<EagleSoftFeeShedule> esfeess, List<EagleSoftPatient> espatients,BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz,Constants.rule_log_enter + "-" + Constants.RULE_ID_4, Constants.rule_log_debug,bw);
        		List<TPValidationResponseDto> dList = new ArrayList<>();
		if (tpList == null) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return dList;
		}
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		boolean pass = true;
		if (espatients != null && espatients.get(0) != null) {
			EagleSoftPatient pat = espatients.get(0);
			System.out.println(ivf.getPlanCoverageBook());
			System.out.println(pat.getCovBookHeaderName());

			// String esp_ = ivf.getPlanFeeScheduleName();
			RuleEngineLogger.generateLogs(clazz,"Coverage Book-"+ivf.getPlanCoverageBook()+" Coverage Book Header Name-"+ivf.getPlanCoverageBook(), Constants.rule_log_debug,bw);
	        
			if (!ivf.getPlanCoverageBook().trim().equalsIgnoreCase(pat.getCovBookHeaderName().trim())) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule4.error.message_1",
								new Object[] { ivf.getPlanCoverageBook(), pat.getCovBookHeaderName() }, locale),
						Constants.FAIL));
				pass = false;
			}
			RuleEngineLogger.generateLogs(clazz,"Plan Fee Schedule Name-"+ivf.getPlanFeeScheduleName()+" Patient Fee Schedule Name-"+pat.getFeeScheduleName(), Constants.rule_log_debug,bw);
	        
			if (!ivf.getPlanFeeScheduleName().trim().equalsIgnoreCase(pat.getFeeScheduleName().trim())) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule4.error.message_3",
								new Object[] { ivf.getPlanFeeScheduleName(), pat.getFeeScheduleName() }, locale),
						Constants.FAIL));
				pass = false;
			} else {
				// same value
				Set<String> missing_code = new HashSet<String>();
				Set<String> missing_name = new HashSet<String>();
				Set<String> missing_cp_EG = new HashSet<String>();

				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					Collection<EagleSoftFeeShedule> ruleGen = Collections2.filter(esfeess,
							name -> name.getFeesServiceCode().equals(tp.getServiceCode()));
					if (ruleGen != null) {
						for (EagleSoftFeeShedule fs : ruleGen) {
							RuleEngineLogger.generateLogs(clazz," FS FEE -"+fs.getFeesFee()+" Treatment Plan Fee-"+tp.getFee(), Constants.rule_log_debug,bw);
					        				
							if (!fs.getFeesFee().equals(tp.getFee())) {
								missing_code.add(tp.getServiceCode());
								missing_name.add(fs.getName());

							}
						}
					} else {
						RuleEngineLogger.generateLogs(clazz," Treatment Plan Service code is mssing in  Fee Schedule-"+tp.getServiceCode(), Constants.rule_log_debug,bw);
				        
						// Service Code not found..
						missing_cp_EG.add(tp.getServiceCode());
					}

				}

				if (missing_code.size() > 0 || missing_name.size() > 0 || missing_cp_EG.size() > 0) {
					pass = false;
					if (missing_code.size() > 0 && missing_name.size() > 0) {
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule4.error.message_2",
										new Object[] { String.join(",", missing_code), String.join(",", missing_name) }, locale),
								Constants.FAIL));

					} else {
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule.servicecode.notfound.esFeeSchedule",
										new Object[] { missing_cp_EG.toString() }, locale),
								Constants.FAIL));

					}

				} else {
					// PASS
				}

			}

		} else {
			RuleEngineLogger.generateLogs(clazz,"Patient Details not found in Patient Sheet -"+ivf.getPatientName() + "-" + ivf.getPatientDOB(), Constants.rule_log_debug,bw);
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule.patient.notfound.espatient",
					new Object[] { "Patient Details not found in Patient Sheet(" + Constants.errorMessOPen
							+ ivf.getPatientName() + "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
					locale), Constants.FAIL));
			pass = false;
		}
        
		if (pass)
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return dList;

	}

	// Compare Coverage Book, Fee Schedule and Fee in IV and Eaglesoft A- BATCH
	public List<TPValidationResponseDto> Rule4_A(IVFTableSheet ivfSheet, MessageSource messageSource, Rules rule,
			List<EagleSoftPatient> espatients) {

		List<TPValidationResponseDto> dList = new ArrayList<>();
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		boolean pass = true;
		if (espatients != null && espatients.get(0) != null) {
			EagleSoftPatient pat = espatients.get(0);

			// String esp_ = ivf.getPlanFeeScheduleName();
			if (!ivf.getPlanCoverageBook().equals(pat.getCovBookHeaderName())) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule4.error.message_1",
								new Object[] { ivf.getPlanCoverageBook(), pat.getCovBookHeaderName() }, locale),
						Constants.FAIL));
				pass = false;
			}
			if (!ivf.getPlanFeeScheduleName().equals(pat.getFeeScheduleName())) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule4.error.message_3",
								new Object[] { ivf.getPlanFeeScheduleName(), pat.getFeeScheduleName() }, locale),
						Constants.FAIL));
				pass = false;
			} else {
				// same value
			}

		} else {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule.patient.notfound.espatient",
					new Object[] { "Patient Details not found in Patient Sheet(" + Constants.errorMessOPen
							+ ivf.getPatientName() + "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
					locale), Constants.FAIL));
			pass = false;
		}

		if (pass)
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return dList;

	}

	// Remaining Deductible, Remaining Balance and Benefit Max as per IV form

	public List<TPValidationResponseDto> Rule5(Object ivfSheet, MessageSource messageSource, Rules rule,
			List<EagleSoftPatient> espatients) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> dList = new ArrayList<>();
		double planAnnualMax = 0;
		double planAnnualMaxRem = 0;
		double planIndDedRem = 0;

		try {
			planAnnualMax = Double.parseDouble(ivf.getPlanAnnualMax());
			planAnnualMaxRem = Double.parseDouble(ivf.getPlanAnnualMaxRemaining());
			planIndDedRem = Double.parseDouble(ivf.getPlanIndividualDeductibleRemaining());// Plan_IndividualDeductibleRemaining
		} catch (Exception e) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.message",
							new Object[] { "Annual Max-" + ivf.getPlanAnnualMax() + " Annual Max Remaining -"
									+ ivf.getPlanAnnualMaxRemaining() + " IndividualDeductible Met-"
									+ ivf.getPlanIndividualDeductibleRemaining() },
							locale),
					Constants.FAIL));
			return dList;
		}

		double primeMaxCov = 0;
		double primeBenefitRem = 0;
		double primeRemDed = 0;

		if (espatients != null && espatients.get(0) != null) {
			EagleSoftPatient pat = espatients.get(0);
			boolean pass = true;
			try {
				primeMaxCov = Double.parseDouble(pat.getPrimMaximumCcoverage());
				primeBenefitRem = Double.parseDouble(pat.getPrimBenefitsRemaining());
				primeRemDed = Double.parseDouble(pat.getPrimRemainingDeductible());

				/*
				 * Compare (Plan_AnnualMax with prim_maximum_coverage), (
				 * Plan_AnnualMaxRemaining with prim_benefits_remaining), and
				 * (Plan_IndividualDeductibleRemaining with prim_remaining_deductible)
				 * 
				 */
				if (planAnnualMax != primeMaxCov) {
					pass = false;
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule5.error.message_R",
									new Object[] { "Plan_AnnualMax", primeMaxCov, planAnnualMax }, locale),
							Constants.FAIL));
				}
				if (planAnnualMaxRem != primeBenefitRem) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule5.error.message_R",
									new Object[] { "Plan_AnnualMaxRemaining", primeBenefitRem, planAnnualMaxRem },
									locale),
							Constants.FAIL));
					pass = false;

				}
				if (planIndDedRem != primeRemDed) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule5.error.message_R",
									new Object[] { "Plan_IndividualDeductibleRemaining", primeRemDed, planIndDedRem },
									locale),
							Constants.FAIL));
					pass = false;

				}

				if (pass)
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));

			} catch (Exception e) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.error.message",
								new Object[] { " PrimMaximumCcoverage- " + pat.getPrimMaximumCcoverage()
										+ " PrimBenefitsRemaining-" + pat.getPrimBenefitsRemaining()
										+ " PrimRemainingDeductible-" + pat.getPrimRemainingDeductible() },
								locale),
						Constants.FAIL));
				return dList;
			}

		} else {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule5.error.message_no_data",
					new Object[] { "Patient Details not found in Patient Sheet(" + Constants.errorMessOPen
							+ ivf.getPatientName() + "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
					locale), Constants.FAIL));

		}

		return dList;
	}

	// Percentage Coverage ... Only DAta issue with Two
	// -Sub-GingivalIrrigation_D4921_%,
	public List<TPValidationResponseDto> Rule6(Object ivfSheet, MessageSource messageSource, Rules rule,
			List<EagleSoftEmployerMaster> esempmaster, List<EagleSoftPatient> espatients,BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz,Constants.rule_log_enter + "-" + Constants.RULE_ID_6, Constants.rule_log_debug,bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		// String coverageBook = ivf.getPlanCoverageBook();
		// String fs = ivf.getPlanFeeScheduleName();
		boolean pass = true;
		List<TPValidationResponseDto> li = new ArrayList<>();
		/*
		 * String paxrays = ivf.getpAXRaysPercentage(); String selantsD1351 =
		 * ivf.getSealantsD1351Percentage(); String basicp = ivf.getBasicPercentage();
		 * String endodon = ivf.getEndodonticsPercentage(); String periosurgery =
		 * ivf.getPerioSurgeryPercentage();// PerioSurgery_% String gingivD4346 =
		 * ivf.getGingivitisD4346Percentage();// Gingivitis_D4346_% String srpd4341 =
		 * ivf.getsRPD4341Percentage();// SRP_D4341_% String subginivIrraD4921 =
		 * "";//NOT PRESENT//ivf.getSub;//Sub-GingivalIrrigation_D4921_% String
		 * perioMainTenanceD4910 =ivf.getPerioMaintenanceD4910Percentage();//
		 * PerioMaintenance_D4910_% String gfmdd4355 = ivf.getFMDD4355Percentage();//
		 * FMD_D4355_% String crownLengthD4249 = ivf.getCrownLengthD4249Percentage();//
		 * CrownLength_D4249_% String postCompositeD2391 =
		 * ivf.getPostCompositesD2391Percentage();// PostComposites_D2391_% String
		 * ivSedationD9248 = ivf.getiVSedationD9248Percentage();// IVSedation_D9248_%
		 * String ivSedationD9243 = ivf.getiVSedationD9243Percentage();//
		 * IVSedation_D9243_% String nitrousD9230 = ivf.getNitrousD9230Percentage();//
		 * Nitrous_D9230_% String ortho = ivf.getOrthoPercentage();// Ortho_% String
		 * major = ivf.getMajorPercentage();// Major_% String estractionsMinor =
		 * ivf.getExtractionsMinorPercentage();// Extractions_Minor_% String
		 * estractionsMajor = ivf.getExtractionsMajorPercentage();// Extractions_Major_%
		 * String implantCovD6010 =
		 * ivf.getImplantCoverageD6010Percentage();//ImplantCoverage_D6010_% String
		 * implantCovD6057 =
		 * ivf.getImplantCoverageD6057Percentage();//ImplantCoverage_D6057_% String
		 * implantCovD6190 = ivf.getImplantCoverageD6190Percentage()
		 * ;//ImplantCoverage_D6190_% String implantSupportD6065 =
		 * ivf.getImplantSupportedPorcCeramicD6065Percentage();//
		 * ImplantSupportedPorcCeramic_D6065_% String crownD2750D2740 =
		 * ivf.getCrownsD2750D2740Percentage();// Crowns_D2750_D2740_% String d9310 =
		 * ivf.getD9310Percentage();// D9310_%
		 */
		List<Rule6Dto> druleList = new ArrayList<>();
		druleList.add(new Rule6Dto("Preventive_%", ivf.getPreventivePercentage(), "Preventative"));
		druleList.add(new Rule6Dto("Diagnostic_%", ivf.getDiagnosticPercentage(), "Diagnostic"));
		druleList.add(new Rule6Dto("PA_XRays_%", ivf.getpAXRaysPercentage(), "PAs/FMX"));
		druleList.add(new Rule6Dto("Sealants_D1351_%", ivf.getSealantsD1351Percentage(), "Preventative"));
		druleList.add(new Rule6Dto("Basic_%", ivf.getBasicPercentage(), "Basic"));
		druleList.add(new Rule6Dto("Endodontics_%", ivf.getEndodonticsPercentage(), "Endodontics"));
		druleList.add(new Rule6Dto("PerioSurgery_%", ivf.getPerioSurgeryPercentage(), "Periodontal Surgery"));
		druleList.add(new Rule6Dto("Gingivitis_D4346_%", ivf.getGingivitisD4346Percentage(), "Gingivitis TX"));
		//
		RuleEngineLogger.generateLogs(clazz,"Sub-GingivalIrrigation_D4921_% -- is missing", Constants.rule_log_debug,bw);
		druleList.add(new Rule6Dto("SRP_D4341_%", ivf.getsRPD4341Percentage(), "SRP"));
		// druleList.add(new Rule6Dto("Sub-GingivalIrrigation_D4921_%",
		// "NNNNNNNNNNNNNNNNNNNNNNNNNNNN",
		// "Subgingival Irrigation"));
		druleList.add(new Rule6Dto("PerioMaintenance_D4910_%", ivf.getPerioMaintenanceD4910Percentage(),
				"Perio Maintenance"));
		druleList.add(new Rule6Dto("FMD_D4355_%", ivf.getFMDD4355Percentage(), "Full Mouth Debridement"));
		druleList.add(new Rule6Dto("CrownLength_D4249_%", ivf.getPreventivePercentage(), "Periodontal Surgery"));
		druleList.add(
				new Rule6Dto("PostComposites_D2391_%", ivf.getPostCompositesD2391Percentage(), "Posterior Composites"));
		druleList.add(new Rule6Dto("IVSedation_D9248_%", ivf.getiVSedationD9248Percentage(), "Sedation 9248"));
		druleList.add(new Rule6Dto("IVSedation_D9243_%", ivf.getiVSedationD9243Percentage(), "Sedation 9243"));
		druleList.add(new Rule6Dto("Nitrous_D9230_%", ivf.getNitrousD9230Percentage(), "Nitrous"));
		druleList.add(new Rule6Dto("Ortho_%", ivf.getOrthoPercentage(), "Orthodontics"));
		druleList.add(new Rule6Dto("Major_%", ivf.getMajorPercentage(), "Major"));
		druleList.add(new Rule6Dto("Extractions_Minor_%", ivf.getExtractionsMinorPercentage(), "Minor Extractions"));
		druleList.add(new Rule6Dto("Extractions_Major_%", ivf.getExtractionsMajorPercentage(), "Major Extractions"));
		druleList.add(new Rule6Dto("ImplantCoverage_D6010_%", ivf.getImplantCoverageD6010Percentage(), "Implants"));
		druleList.add(new Rule6Dto("ImplantCoverage_D6057_%", ivf.getImplantCoverageD6057Percentage(), "Implants"));
		druleList.add(new Rule6Dto("ImplantCoverage_D6190_%", ivf.getImplantCoverageD6190Percentage(), "Implants"));
		druleList.add(new Rule6Dto("ImplantSupportedPorcCeramic_D6065_%",
				ivf.getImplantSupportedPorcCeramicD6065Percentage(), "Implant Supported Prosthetics"));
		druleList.add(new Rule6Dto("Crowns_D2750_D2740_%", ivf.getCrownsD2750D2740Percentage(), "Crowns"));
		druleList.add(new Rule6Dto("D9310_%", ivf.getD9310Percentage(), "Adjunctive General Services"));

		//
		if (espatients != null && esempmaster != null && espatients.get(0) != null && esempmaster.get(0) != null) {

			for (Rule6Dto d6 : druleList) {

				Collection<EagleSoftEmployerMaster> ivfESMap2 = Collections2.filter(esempmaster,
						x -> x.getServiceTypeDescription().trim().equalsIgnoreCase(d6.getFsName().trim()));
				if (ivfESMap2 != null) {
                     
					for (EagleSoftEmployerMaster y : ivfESMap2) {
						
						RuleEngineLogger.generateLogs(clazz,"Employer -"+y.getPercentage()+""+d6.getIvfName()+d6.getPercentage(), Constants.rule_log_debug,bw);
						
						if (y.getPercentage().trim().equalsIgnoreCase(d6.getPercentage().trim())) {
							// Pass
						} else {
							pass = false;
							li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule6.error.message",
											new Object[] { d6.getIvfName() + "(" + d6.getFsName() + ")" }, locale),
									Constants.FAIL));

						}
						break;// Only one value will be there
					}

				} else {
					// Name not found in Emp Master
					pass = false;
					li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule6.error.message_2", new Object[] { d6.getFsName() }, locale),
							Constants.FAIL));
				}

			}

		}
		if (pass) {
			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS));
		}
		return li;

	}

	// Alert
	public List<TPValidationResponseDto> Rule7(Object ivfSheet, MessageSource messageSource, Rules rule) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> li = new ArrayList<>();
		if (ivf.getPlanNonDuplicateClause().equalsIgnoreCase("yes")) {
			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule7.error.message_duplicate", null, locale), Constants.FAIL));
		}
		if (ivf.getPlanPreDMandatory().equalsIgnoreCase("yes")) {
			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule7.error.message_preman", null, locale), Constants.FAIL));
		}
		if (ivf.getPlanFullTimeStudentStatus().equalsIgnoreCase("yes")) {
			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule7.error.message_fulltime", null, locale), Constants.FAIL));
		}
		if (ivf.getPlanAssignmentofBenefits().equalsIgnoreCase("yes")) {
			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule7.error.message_benefit", null, locale), Constants.FAIL));
		}
		if (li.size() == 0)
			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS));

		return li;

	}

	// Age Limits
	public List<TPValidationResponseDto> Rule8(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String fl = ivf.getFlourideAgeLimit();

		String var = ivf.getVarnishD1206AgeLimit();
		String sel = ivf.getSealantsD1351AgeLimit();
		String ortho = ivf.getOrthoAgeLimit();
		String dob = ivf.getPatientDOB();
		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		int age = 0;
		boolean pass = true;
		try {
			age = DateUtils.calculateAge(dob);
		} catch (ParseException e) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule1.error.message.date", new Object[] { dob }, locale),
					Constants.FAIL));
			return d;
		}

		// As per requirement hard Code these
		/*
		 * List<String> codestoCheckFL = new ArrayList<>(); codestoCheckFL.add("D1208");
		 * List<String> codestoCheckV = new ArrayList<>(); codestoCheckV.add("D1206");
		 * 
		 * List<String> codestoCheckS = new ArrayList<>(); codestoCheckS.add("D1351");
		 * 
		 * List<String> codestoCheckO = new ArrayList<>(); codestoCheckO.add("D8010");
		 * codestoCheckO.add("D8020"); codestoCheckO.add("D8030");
		 * codestoCheckO.add("D8040"); codestoCheckO.add("D8050");
		 * codestoCheckO.add("D8060"); codestoCheckO.add("D8070");
		 * codestoCheckO.add("D8080"); codestoCheckO.add("D8090");
		 */
		// codestoCheck.add("D1110");//Remove ME

		for (Object obj : tpList) {
			TreatmentPlan tp = (TreatmentPlan) obj;
			if (tp.getServiceCode().equals("D1208")) {
				try {
					int f = Integer.parseInt(fl);
					if (age > f) {
						d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule8.error.message",
										new Object[] { tp.getServiceCode(), age, f }, locale),
								Constants.FAIL));
						pass = false;
					}
				} catch (NumberFormatException e) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule1.error.message.numberformat", new Object[] { fl }, locale),
							Constants.FAIL));
					pass = false;

				}
			} else if (tp.getServiceCode().equals("D1206")) {
				try {
					int v = Integer.parseInt(var);
					if (age > v) {
						d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule8.error.message",
										new Object[] { tp.getServiceCode(), age, v }, locale),
								Constants.FAIL));
						pass = false;
					}
				} catch (NumberFormatException e) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule1.error.message.numberformat", new Object[] { var }, locale),
							Constants.FAIL));
					pass = false;

				}
			} else if (tp.getServiceCode().equals("D1351")) {

				try {
					int s = Integer.parseInt(sel);
					if (age > s) {
						d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule8.error.message",
										new Object[] { tp.getServiceCode(), age, s }, locale),
								Constants.FAIL));
						pass = false;
					}
				} catch (NumberFormatException e) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule1.error.message.numberformat", new Object[] { sel }, locale),
							Constants.FAIL));
					pass = false;

				}
			} else if (tp.getServiceCode().equals("D8010") || tp.getServiceCode().equals("D8020")
					|| tp.getServiceCode().equals("D8030") || tp.getServiceCode().equals("D8040")
					|| tp.getServiceCode().equals("D8050") || tp.getServiceCode().equals("D8060")
					|| tp.getServiceCode().equals("D8070") || tp.getServiceCode().equals("D8080")
					|| tp.getServiceCode().equals("D8090")) {

				try {
					int o = Integer.parseInt(ortho);
					if (age > o) {
						d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule8.error.message",
										new Object[] { tp.getServiceCode(), age, o }, locale),
								Constants.FAIL));
						pass = false;
					}
				} catch (NumberFormatException e) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
							"rule1.error.message.numberformat", new Object[] { ortho }, locale), Constants.FAIL));
					pass = false;

				}
			}
			// }
		} // For LOOP end
		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Sealants work in Progress waiting Message
	public List<TPValidationResponseDto> Rule14(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String dob = ivf.getPatientDOB();
		// String sealantPer=ivf.getSealantsD1351Percentage();
		String sealantAL = ivf.getSealantsD1351AgeLimit();
		String primaryMolar = ivf.getSealantsD1351PrimaryMolarsCovered();
		String preMolar = ivf.getSealantsD1351PreMolarsCovered();
		String premanentMolar = ivf.getSealantsD1351PermanentMolarsCovered();
		// String insName = ivf.getInsName();

		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		int age = 0;
		boolean pass = true;
		try {
			age = DateUtils.calculateAge(dob);
		} catch (ParseException e) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule1.error.message.date", new Object[] { dob }, locale),
					Constants.FAIL));
			return d;
		}
		try {
			Integer.parseInt(sealantAL);
		} catch (NumberFormatException e) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.message", new Object[] { sealantAL }, locale),
					Constants.FAIL));
			return d;
		}

		// As per requirement hard Code these

		if (age <= Integer.parseInt(sealantAL)) {

			List<String> primaryMolarTCList = new ArrayList<>();
			primaryMolarTCList.add("A");
			primaryMolarTCList.add("B");
			primaryMolarTCList.add("I");
			primaryMolarTCList.add("J");
			primaryMolarTCList.add("K");
			primaryMolarTCList.add("L");
			primaryMolarTCList.add("S");
			primaryMolarTCList.add("T");

			List<String> permanentMolarTCList = new ArrayList<>();
			permanentMolarTCList.add("1");
			permanentMolarTCList.add("2");
			permanentMolarTCList.add("3");
			permanentMolarTCList.add("13");
			permanentMolarTCList.add("14");
			permanentMolarTCList.add("15");
			permanentMolarTCList.add("16");
			permanentMolarTCList.add("17");
			permanentMolarTCList.add("18");
			permanentMolarTCList.add("19");
			permanentMolarTCList.add("30");
			permanentMolarTCList.add("31");
			permanentMolarTCList.add("32");

			List<String> preMolarCList = new ArrayList<>();
			preMolarCList.add("4");
			preMolarCList.add("5");
			preMolarCList.add("11");
			preMolarCList.add("12");
			preMolarCList.add("20");
			preMolarCList.add("21");
			preMolarCList.add("28");
			preMolarCList.add("29");

			// InsCompany Logic ..Not Needed For Now..
			/*
			 * try { double x= Double.parseDouble(sealantPer); if (x>0) { //Then eligible }
			 * }catch(Exception e) {
			 * 
			 * }
			 */
			List<String> primaryMolarT = new ArrayList<>();
			List<String> permamentMolarT = new ArrayList<>();
			List<String> preMolarT = new ArrayList<>();
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				String tooths[] = ToothUtil.getToothsFromTooth(tp.getTooth());
				for (String tooth : tooths) {

					if (!primaryMolar.trim().equalsIgnoreCase("yes")) {
						Collection<String> prit = Collections2.filter(primaryMolarTCList, th -> th.equals(tooth));
						for (String x : prit) {
							primaryMolarT.add(x);
						}
					}

					if (!premanentMolar.trim().equalsIgnoreCase("yes")) {
						Collection<String> permat = Collections2.filter(permanentMolarTCList, th -> th.equals(tooth));
						for (String x : permat) {
							primaryMolarT.add(x);
						}
					}
					if (!preMolar.trim().equalsIgnoreCase("yes")) {
						Collection<String> perm = Collections2.filter(preMolarCList, th -> th.equals(tooth));
						for (String x : perm) {
							primaryMolarT.add(x);
						}
					}

				} // For loop end= Tooth

			} // For LOOP end
			if (primaryMolarT.size() > 0) {
				// String.join(", ", primaryMolarT)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule13.error.message",
								new Object[] { "Age(" + age + ")" + " Primary Molar " }, locale),
						Constants.FAIL));

				pass = false;
			}
			if (permamentMolarT.size() > 0) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule13.error.message",
								new Object[] { "Age(" + age + ")" + " Permanent Molar " }, locale),
						Constants.FAIL));
				pass = false;
			}
			if (preMolarT.size() > 0) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule13.error.message",
								new Object[] { "Age(" + age + ")" + " Pre-Molar " }, locale),
						Constants.FAIL));
				pass = false;
			}
		} // if end for age limit
		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// SRP Quads Per Day
	public List<TPValidationResponseDto> Rule15(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String srpperday = ivf.getsRPD4341QuadsPerDay();
		String datybetweenTr = ivf.getsRPD4341DaysBwTreatment();
		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		boolean pass = true;
		try {
			Integer.parseInt(srpperday);
		} catch (NumberFormatException e) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.message", new Object[] { srpperday }, locale),
					Constants.FAIL));
			return d;
		}
		try {
			Integer.parseInt(datybetweenTr);
		} catch (NumberFormatException e) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.message", new Object[] { srpperday }, locale),
					Constants.FAIL));
			return d;
		}

		List<String> srpCodeTCList = new ArrayList<>();
		srpCodeTCList.add("D4341");
		srpCodeTCList.add("D4342");
		int size = 0;
		for (Object obj : tpList) {
			TreatmentPlan tp = (TreatmentPlan) obj;
			Collection<String> prit = Collections2.filter(srpCodeTCList, th -> th.equals(tp.getServiceCode()));
			if (prit != null)
				size = size + prit.size();
		} // For LOOP end

		// Eligibility for Number of Quads

		if (size > Integer.parseInt(srpperday)) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule15.error.message1", new Object[] { srpperday, size }, locale),
					Constants.FAIL));
			pass = false;
		}

		// No. of Days Check
		if (Integer.parseInt(datybetweenTr) > 0 && size > 0) {
			pass = false;
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule15.error.message2", new Object[] { datybetweenTr }, locale),
					Constants.FAIL));
		}

		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Bundling - X-Rays
	public List<TPValidationResponseDto> Rule16(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String xray = ivf.getxRaysBundling();
		List<TPValidationResponseDto> d = new ArrayList<>();
		boolean pass = true;
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		if (xray != null && xray.trim().equalsIgnoreCase("yes")) {
			List<String> xrayCodeList = new ArrayList<>();
			xrayCodeList.add("D0220");
			xrayCodeList.add("D0230");
			xrayCodeList.add("D0272");
			xrayCodeList.add("D0274");
			xrayCodeList.add("D0330");

			List<String> paxrayCodeList = new ArrayList<>();
			paxrayCodeList.add("D0220");
			paxrayCodeList.add("D0230");

			int sizeXray = 0;
			int sizePAXray = 0;

			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				// X-Rays Code
				Collection<String> prit = Collections2.filter(xrayCodeList, cd -> cd.equals(tp.getServiceCode()));
				if (prit != null)
					sizeXray = sizeXray + prit.size();
				// 9+ PA into FMX
				Collection<String> prita = Collections2.filter(paxrayCodeList, cd -> cd.equals(tp.getServiceCode()));
				if (prita != null)
					sizePAXray = sizePAXray + prita.size();

			} // For LOOP end

			if (sizeXray > 0) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule16.error.message1", new Object[] {}, locale), Constants.FAIL));
				pass = false;
			}
			if (sizePAXray > 9) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule16.error.message2", new Object[] {}, locale), Constants.FAIL));
				pass = false;
			}
		}

		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Bundling - Fillings -
	public List<TPValidationResponseDto> Rule17(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String fillings = ivf.getFillingsBundling();
		List<TPValidationResponseDto> d = new ArrayList<>();
		boolean pass = true;
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		if (fillings != null && fillings.trim().equalsIgnoreCase("yes")) {
			List<String> fillingCodeList_1 = new ArrayList<>();
			fillingCodeList_1.add("D2330");
			fillingCodeList_1.add("D2330");
			fillingCodeList_1.add("D2330");
			fillingCodeList_1.add("D2330");

			List<String> fillingCodeList_2 = new ArrayList<>();
			fillingCodeList_2.add("D2330");
			fillingCodeList_2.add("D2330");
			fillingCodeList_2.add("D2330");

			List<String> fillingCodeList_3 = new ArrayList<>();
			fillingCodeList_3.add("D2330");
			fillingCodeList_2.add("D2330");

			List<String> fillingCodeList_4 = new ArrayList<>();
			fillingCodeList_4.add("D2330");
			fillingCodeList_4.add("D2331");

			List<String> fillingCodeList_5 = new ArrayList<>();
			fillingCodeList_5.add("D2330");
			fillingCodeList_5.add("D2332");

			List<String> fillingCodeList_6 = new ArrayList<>();
			fillingCodeList_6.add("D2330");
			fillingCodeList_6.add("D2330");
			fillingCodeList_6.add("D2331");

			List<String> fillingCodeList_7 = new ArrayList<>();
			fillingCodeList_7.add("D2331");
			fillingCodeList_7.add("D2331");

			List<String> fillingCodeList_8 = new ArrayList<>();
			fillingCodeList_8.add("D2391");
			fillingCodeList_8.add("D2391");
			fillingCodeList_8.add("D2391");
			fillingCodeList_8.add("D2391");

			List<String> fillingCodeList_9 = new ArrayList<>();
			fillingCodeList_9.add("D2391");
			fillingCodeList_9.add("D2391");
			fillingCodeList_9.add("D2391");

			List<String> fillingCodeList_10 = new ArrayList<>();
			fillingCodeList_10.add("D2391");
			fillingCodeList_10.add("D2391");

			List<String> fillingCodeList_11 = new ArrayList<>();
			fillingCodeList_11.add("D2391");
			fillingCodeList_11.add("D2392");

			List<String> fillingCodeList_12 = new ArrayList<>();
			fillingCodeList_12.add("D2391");
			fillingCodeList_12.add("D2393");

			List<String> fillingCodeList_13 = new ArrayList<>();
			fillingCodeList_13.add("D2391");
			fillingCodeList_13.add("D2391");
			fillingCodeList_13.add("D2392");

			List<String> fillingCodeList_14 = new ArrayList<>();
			fillingCodeList_14.add("D2392");
			fillingCodeList_14.add("D2392");
			//

			List<String> fillingCodeList = new ArrayList<>();
			fillingCodeList.addAll(fillingCodeList_1);
			fillingCodeList.addAll(fillingCodeList_2);
			fillingCodeList.addAll(fillingCodeList_3);
			fillingCodeList.addAll(fillingCodeList_4);
			fillingCodeList.addAll(fillingCodeList_5);
			fillingCodeList.addAll(fillingCodeList_6);
			fillingCodeList.addAll(fillingCodeList_7);
			fillingCodeList.addAll(fillingCodeList_8);
			fillingCodeList.addAll(fillingCodeList_9);
			fillingCodeList.addAll(fillingCodeList_10);
			fillingCodeList.addAll(fillingCodeList_11);
			fillingCodeList.addAll(fillingCodeList_12);
			fillingCodeList.addAll(fillingCodeList_13);
			fillingCodeList.addAll(fillingCodeList_14);

			Map<String, List<String>> filligToothMap = null;
			List<String> list = null;
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				// fillingCodes
				Collection<String> prit = Collections2.filter(fillingCodeList, cd -> cd.equals(tp.getServiceCode()));
				if (prit != null) {

					String[] tooths = ToothUtil.getToothsFromTooth(tp.getTooth());
					for (String tooth : tooths) {
						if (filligToothMap == null)
							filligToothMap = new HashMap<>();
						if (filligToothMap.containsKey(tooth)) {
							list = (List<String>) filligToothMap.get(tooth);
							list.add(tp.getServiceCode());
						} else {
							list = new ArrayList<>();
							list.add(tp.getServiceCode());
							filligToothMap.put(tooth, list);

						}

					}
				}

			} // For LOOP end

			if (filligToothMap != null) {
				List<String> btooths = null;
				/// can be optimized more
				boolean found = false;
				for (Map.Entry<String, List<String>> entry : filligToothMap.entrySet()) {

					List<String> serviceCodes = entry.getValue();
					int countD2330 = Collections.frequency(serviceCodes, "D2330");
					int countD2331 = Collections.frequency(serviceCodes, "D2331");
					int countD2332 = Collections.frequency(serviceCodes, "D2332");
					int countD2391 = Collections.frequency(serviceCodes, "D2391");
					int countD2392 = Collections.frequency(serviceCodes, "D2392");
					int countD2393 = Collections.frequency(serviceCodes, "D2393");

					if (countD2330 >= 4) {
						found = true;

					} else if (countD2330 >= 3) {
						found = true;

					} else if (countD2330 >= 2) {
						found = true;

					} else if (countD2330 >= 1 && countD2331 >= 1) {
						found = true;

					} else if (countD2330 >= 1 && countD2332 >= 1) {
						found = true;

					} else if (countD2330 >= 2 && countD2331 >= 1) {
						found = true;

					} else if (countD2331 >= 2) {
						found = true;

					} else if (countD2391 >= 4) {
						found = true;

					} else if (countD2391 >= 3) {
						found = true;

					} else if (countD2391 >= 2) {
						found = true;

					} else if (countD2391 >= 1 && countD2392 >= 1) {
						found = true;

					} else if (countD2391 >= 1 && countD2393 >= 1) {
						found = true;

					} else if (countD2391 >= 2 && countD2392 >= 1) {
						found = true;

					} else if (countD2392 >= 2) {
						found = true;

					}
					if (found) {
						if (btooths == null)
							btooths = new ArrayList<>();
						btooths.add(entry.getKey());
					}

				} // For loop outer

				if (found) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule17.error.message",
									new Object[] { "[" + String.join(", ", btooths) + "]" }, locale),
							Constants.FAIL));
					pass = false;
				}

			}

		}

		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Filling Codes based on Tooth No
	public List<TPValidationResponseDto> Rule9(List<Object> tpList, MessageSource messageSource, Rules rule,
			List<Mappings> mappings) {

		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		boolean pass = true;
		for (Object obj : tpList) {
			TreatmentPlan tp = (TreatmentPlan) obj;
			if (!tp.getTooth().equalsIgnoreCase("NA") && !tp.getTooth().equals("")) {
				String scode = tp.getServiceCode();
				String tooths[] = ToothUtil.getToothsFromTooth(tp.getTooth());
				// Mappings map = getMappingFromList(mappings, scode);dd
				Mappings map = getMappingFromListForToothFilling(mappings, scode);
				if (map != null) {
					for (String tooth : tooths) {
						boolean toothFound = false;
						String toothsm[] = map.getFillingToothNoMapping().split(",");
						List<String> ts = ToothUtil.findCommonTooth(tooths, toothsm);
						if (ts != null && ts.size() > 0) {
							toothFound = true;
						}
						/*
						 * for (String toothm : toothsm) { if (toothm.equals(tooth)) { toothFound =
						 * true; } }
						 */
						if (!toothFound) {
							pass = false;
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									"rule9.error.message", new Object[] { scode, tooth }, locale), Constants.FAIL));
						}
					} // End - for loop from tooth of Treatment Plan
				} else {
					/*
					 * pass = false; d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					 * messageSource.getMessage("rule.servicecode.notfound", new Object[] { scode },
					 * locale), Constants.FAIL));
					 */
				}
			}
		}
		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Pre Auth
	public List<TPValidationResponseDto> Rule10(List<Object> tpList, MessageSource messageSource, Rules rule,
			List<Mappings> mappings) {

		// IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		// String dob = ivf.getPatientDOB();
		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		// int age = 0;
		boolean pass = true;
		/*
		 * Age Logic removed in updated Document try { age =
		 * DateUtils.calculateAge(dob); } catch (ParseException e) { d.add(new
		 * TPValidationResponseDto(rule.getId(), rule.getName(),
		 * messageSource.getMessage("rule1.error.message.date", new Object[] { dob },
		 * locale), Constants.FAIL)); return d; }
		 */
		for (Object obj : tpList) {
			TreatmentPlan tp = (TreatmentPlan) obj;
			Mappings mapA = getMappingFromListAdditionalInformationNeeded(mappings, tp.getServiceCode());
			Mappings mapP = getMappingFromListPreAuth(mappings, tp.getServiceCode());

			// 100
			if (mapA != null) {
				pass = false;
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule10.error.message1",
								new Object[] { tp.getServiceCode(), mapA.getAdditionalInformationNeeded() }, locale),
						Constants.FAIL));

			}
			if (mapP != null) {
				pass = false;
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule10.error.message2", new Object[] { tp.getServiceCode() }, locale),
						Constants.FAIL));

			}
		}
		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Waiting Period
	public List<TPValidationResponseDto> Rule11(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String eff = ivf.getPlanEffectiveDate();
		String waitb = ivf.getBasicWaitingPeriod();
		String waitm = ivf.getMajorWaitingPeriod();
		Date effD = null;
		Date dos = null;
		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		boolean pass = true;
		try {
			DateUtils.CheckForStringInDate(eff);
			effD = Constants.SIMPLE_DATE_FORMAT.parse(eff);
			Integer.parseInt(waitb);
			Integer.parseInt(waitm);

		} catch (RuleEngineDateException e) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule1.error.message.date", new Object[] { eff }, locale),
					Constants.FAIL));
			return d;
		} catch (Exception e) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.message",
							new Object[] { "(" + waitb + ")(" + waitm + ")(" + effD + ")" }, locale),
					Constants.FAIL));
			return d;
		}
		for (Object obj : tpList) {
			TreatmentPlan tp = (TreatmentPlan) obj;
			try {
				DateUtils.CheckForStringInDate(tp.getTreatmentPlanDetails().getDateLastUpdated());
				dos = Constants.SIMPLE_DATE_FORMAT.parse(tp.getTreatmentPlanDetails().getDateLastUpdated());
			} catch (RuleEngineDateException e) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule1.error.message.date",
								new Object[] { tp.getTreatmentPlanDetails().getDateLastUpdated() }, locale),
						Constants.FAIL));
				return d;
			} catch (ParseException e) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule1.error.message.date",
								new Object[] { tp.getTreatmentPlanDetails().getDateLastUpdated() }, locale),
						Constants.FAIL));
				return d;
			}
			Mappings map = getMappingFromListWaiting(mappings, tp.getServiceCode());

			if (map != null && map.getServiceCodeCategory().getName().equalsIgnoreCase("Major")
					|| map.getServiceCodeCategory().getName().equalsIgnoreCase("Basic")) {
				int wt = Integer.parseInt(waitb);
				if (map.getServiceCodeCategory().getName().equalsIgnoreCase("Major"))
					wt = Integer.parseInt(waitm);

				if (DateUtils.daysBetweenDates(effD, dos).intValue() < wt) {
					pass = false;
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule11.error.message",
									new Object[] { waitb, waitm, tp.getServiceCode(), eff, dos }, locale),
							Constants.FAIL));
				}

			}
		}
		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Missing Tooth Clause
	public List<TPValidationResponseDto> Rule18(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		boolean pass = true;
		if (ivf.getMissingToothClause() != null && ivf.getMissingToothClause().trim().equalsIgnoreCase("yes")) {
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				Collection<Mappings> mL = Collections2.filter(mappings,
						y -> y.getAdaCodes().getCode().equals((tp).getServiceCode()));
				if (mL == null) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
							.getMessage("rule.servicecode.notfound", new Object[] { tp.getServiceCode() }, locale),
							Constants.FAIL));
					pass = false;
					return d;
				} else {
					for (Mappings m : mL) {
						if (m.getMissingToothClauseApplicable() != null
								&& m.getMissingToothClauseApplicable().trim().equalsIgnoreCase("yes")) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
									.getMessage("rule18.error.message", new Object[] { tp.getServiceCode() }, locale),
									Constants.FAIL));
							pass = false;
						}
					}

				}

			}
		}
		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Build-Ups & Crown Same Day
	public List<TPValidationResponseDto> Rule13(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule) {

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String samedayCr = ivf.getBuildUpsD2950SameDayCrown();
		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		boolean pass = true;

		if (samedayCr != null && samedayCr.equalsIgnoreCase("yes")) {
			// Nothing for now
		} else {
			boolean found = false;
			String tooth = "";
			String toothD2740 = "";
			String toothD2750 = "";
			// Assumption that Only One Tooth no is present in One Row for D2950,D2740,D2750
			// Email Dated & Phone Call: 1 August 2018
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				if (tp.getServiceCode().equalsIgnoreCase("D2950")) {
					tooth = tp.getTooth();
					found = true;
				}
				if (tp.getServiceCode().equals("D2740")) {
					toothD2740 = tp.getTooth();
				}
				if (tp.getServiceCode().equals("D2750")) {
					toothD2750 = tp.getTooth();
				}

			} // For end
			if (found && tooth != "" && toothD2740 != "" && toothD2750 != "") {
				// Logic here
				if (tooth.trim().equals(toothD2740.trim()) && tooth.trim().equals(toothD2750.trim())) {
					pass = false;
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule13.error.message", new Object[] { tooth }, locale),
							Constants.FAIL));
				}

			}
		}

		if (pass)
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return d;

	}

	// Downgrading
	public List<TPValidationResponseDto> Rule19(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings, List<EagleSoftFeeShedule> esfeess, List<EagleSoftPatient> espatients) {

		List<TPValidationResponseDto> dList = new ArrayList<>();
		if (tpList == null) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return dList;
		}
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		boolean pass = true;

		if (ivf.getPosteriorCompositesD2391Downgrade() != null && ivf.getCrownsD2750D2740Downgrade() != null
				&& (ivf.getPosteriorCompositesD2391Downgrade().trim().equalsIgnoreCase("yes")
						|| ivf.getCrownsD2750D2740Downgrade().trim().equalsIgnoreCase("yes"))) {
			// If Yes then pull all the applicable codes and tooth numbers using the
			// Downgrading Mapping tables...
			List<Mappings> mapps = getMappingDownGradeFromList(mappings);

			for (Object t : tpList) {
				TreatmentPlan tp = (TreatmentPlan) t;
				Collection<Mappings> mL = Collections2.filter(mapps,
						y -> y.getAdaCodes().getCode().equals((tp).getServiceCode()));
				// Now Downgrading is applicable
				if (mL != null && mL.size() > 0) {

					for (Mappings m : mL) {

						String toothMa[] = ToothUtil.getToothsFromTooth(m.getToothNoForDowngrading());
						String toothTR[] = ToothUtil.getToothsFromTooth(tp.getTooth());
						List<String> th = ToothUtil.findCommonTooth(toothMa, toothTR);
						if (th != null && th.size() > 0) {
							// Tooth Matched
							if (m.getDowngrading() != null && !m.getDowngrading().trim().equals("")) {
								// Go Fee Schedule goto Check Fee
								if (esfeess != null && esfeess.size() > 0) {
									Collection<EagleSoftFeeShedule> ruleGen = Collections2.filter(esfeess,
											name -> name.getFeesServiceCode().equals(m.getDowngrading().trim()));
									if (ruleGen != null) {
										for (EagleSoftFeeShedule fs : ruleGen) {
											if (!fs.getFeesFee().equals(tp.getFee())) {
												pass = false;
												dList.add(
														new TPValidationResponseDto(rule.getId(), rule.getName(),
																messageSource.getMessage("rule19.error.message2",
																		new Object[] { tp.getServiceCode(),
																				fs.getFeesFee(), m.getDowngrading() },
																		locale),
																Constants.FAIL));

											} else {
												// Unique case
												dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
														messageSource.getMessage("rule19.pass.message1",
																new Object[] {}, locale),
														Constants.PASS));

											}
										}
									}
								} // if end
								else {
									pass = false;
									dList.add(
											new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule19.error.message3",
															new Object[] { m.getDowngrading() }, locale),
													Constants.FAIL));
								}
							} // if END

						}
						break;// Because only one row needed to checked
					}
				}
			}
		}

		if (pass)
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return dList;

	}

	// Frequency Limitations
	public List<TPValidationResponseDto> Rule21(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings,BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz,Constants.rule_log_enter + "-" + Constants.RULE_ID_21, Constants.rule_log_debug,bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		if (tpList == null) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return dList;
		}
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> mapFlIVF = new HashMap<>();
		ServiceCodeIvfTimesFreqFieldDto scivftff=null;
		List<ServiceCodeIvfTimesFreqFieldDto> dL=null;
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D1208", "Flouride_D1208_FL", ivf.getFlourideD1208FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D1208",dL);
		//mapFlIVF.put("D1208", new String[] { "FlourideD1208FL", ivf.getFlourideD1208FL() });// 1 Flouride_D1208_FL
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D1206", "Varnish_D1206_FL", ivf.getVarnishD1206FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D1206",dL);
		//mapFlIVF.put("D1206", new String[] { "Varnish_D1206_FL", ivf.getVarnishD1206FL() });// 2

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0270", "XRaysBWS_FL", ivf.getxRaysBWSFL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0270",dL);
		//mapFlIVF.put("D0270", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 3
	
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0272", "getxRaysBWSFL", ivf.getxRaysBWSFL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0272",dL);
		//mapFlIVF.put("D0272", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 4
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0273", "XRaysBWS_FL", ivf.getxRaysBWSFL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0273",dL);
		//mapFlIVF.put("D0273", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 5
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0274", "XRaysBWS_FL", ivf.getxRaysBWSFL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0274",dL);
		//mapFlIVF.put("D0274", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 6

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0220", "XRaysPA_D0220_FL", ivf.getxRaysPAD0220FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0220",dL);
		//mapFlIVF.put("D0220", new String[] { "XRaysPA_D0220_FL", ivf.getxRaysPAD0220FL() });// 7
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0230", "XRaysPA_D0230_FL", ivf.getxRaysPAD0230FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0230",dL);
		//mapFlIVF.put("D0230", new String[] { "XRaysPA_D0230_FL", ivf.getxRaysPAD0230FL() });// 8
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0210", "XRaysFMX_FL", ivf.getxRaysFMXFL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0210",dL);
		//mapFlIVF.put("D0210", new String[] { "XRaysFMX_FL", ivf.getxRaysFMXFL() });// 9
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0150", "ExamsD0150_FL", ivf.getExamsD0150FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0150",dL);
		//mapFlIVF.put("D0150", new String[] { "ExamsD0150_FL", ivf.getExamsD0150FL() });// 10
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0145", "ExamsD0145_FL", ivf.geteExamsD0145FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0145",dL);
		//mapFlIVF.put("D0145", new String[] { "ExamsD0145_FL", ivf.geteExamsD0145FL() });// 11
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0140", "ExamsD0140_FL", ivf.getExamsD0140FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0140",dL);
		//mapFlIVF.put("D0140", new String[] { "ExamsD0140_FL", ivf.getExamsD0140FL() });// 12
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D0120", "ExamsD0120_FL", ivf.getExamsD0120FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D0120",dL);
		//mapFlIVF.put("D0120", new String[] { "ExamsD0120_FL", ivf.getExamsD0120FL() });// 13
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D2930", "SSC_D2930_FL", ivf.getsSCD2930FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D2930",dL);
		//mapFlIVF.put("D2930", new String[] { "SSC_D2930_FL", ivf.getsSCD2930FL() });// 14
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D2931", "SSC_D2931_FL", ivf.getsSCD2931FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D2931",dL);
		//mapFlIVF.put("D2931", new String[] { "SSC_D2931_FL", ivf.getsSCD2931FL() });// 15
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D1351", "Sealants_D1351_FL", ivf.getSealantsD1351FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D1351",dL);
		//mapFlIVF.put("D1351", new String[] { "Sealants_D1351_FL", ivf.getSealantsD1351FL() });// 16
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D4346", "Gingivitis_D4346_FL", ivf.getGingivitisD4346FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D4346",dL);
		//mapFlIVF.put("D4346", new String[] { "Gingivitis_D4346_FL", ivf.getGingivitisD4346FL() });// 17
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D4341", "SRP_D4341_FL", ivf.getsRPD4341FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D4341",dL);
		//mapFlIVF.put("D4341", new String[] { "SRP_D4341_FL", ivf.getsRPD4341FL() });// 18
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D9940", "NightGuards_D9940_FL", ivf.getNightGuardsD9940FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D9940",dL);
		//mapFlIVF.put("D9940", new String[] { "NightGuards_D9940_FL", ivf.getNightGuardsD9940FL() });// 19
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D4910", "PerioMaintenance_D4910_FL", ivf.getPerioMaintenanceD4910FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D4910",dL);
		//mapFlIVF.put("D4910", new String[] { "PerioMaintenance_D4910_FL", ivf.getPerioMaintenanceD4910FL() });// 20
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D4355", "FMD_D4355_FL", ivf.getfMDD4355FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D4355",dL);
		//mapFlIVF.put("D4355", new String[] { "FMD_D4355_FL", ivf.getfMDD4355FL() });// 21
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D4249", "CrownLength_D4249_FL", ivf.getCrownLengthD4249FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D4249",dL);
		//mapFlIVF.put("D4249", new String[] { "CrownLength_D4249_FL", ivf.getCrownLengthD4249FL() });// 22
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D2950", "BuildUps_D2950_FL", ivf.getBuildUpsD2950FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D2950",dL);
		//mapFlIVF.put("D2950", new String[] { "BuildUps_D2950_FL", ivf.getBuildUpsD2950FL() });// 23
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D7953", "BoneGrafts_D7953_FL", ivf.getBoneGraftsD7953FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D7953",dL);
		//mapFlIVF.put("D7953", new String[] { "BoneGrafts_D7953_FL", ivf.getBoneGraftsD7953FL() });// 24
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D7311", "Alveo_D7311_FL", ivf.getAlveoD7311FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D7311",dL);
		//mapFlIVF.put("D7311", new String[] { "Alveo_D7311_FL", ivf.getAlveoD7311FL() });// 25

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D7310", "Alveo_D7310_FL", ivf.getAlveoD7310FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D7310",dL);
		//mapFlIVF.put("D7310", new String[] { "Alveo_D7310_FL", ivf.getAlveoD7310FL() });// 26

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D2750", "Crowns_D2750_D2740_FL", ivf.getCrownsD2750D2740FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D2750",dL);
		//mapFlIVF.put("D2750", new String[] { "Crowns_D2750_D2740_FL", ivf.getCrownsD2750D2740FL() });// 27

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D2740", "Crowns_D2750_D2740_FL", ivf.getCrownsD2750D2740FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D2740",dL);
		//mapFlIVF.put("D2740", new String[] { "Crowns_D2750_D2740_FL", ivf.getCrownsD2750D2740FL() });// 28

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D5110", "CompleteDentures_D5110_D5120_FL", ivf.getCompleteDenturesD5110D5120FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D5110",dL);
		//mapFlIVF.put("D5110", new String[] { "CompleteDentures_D5110_D5120_FL", ivf.getCompleteDenturesD5110D5120FL() });// 29

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D5120", "CompleteDentures_D5110_D5120_FL", ivf.getCompleteDenturesD5110D5120FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D5120",dL);
		//mapFlIVF.put("D5120", new String[] { "CompleteDentures_D5110_D5120_FL", ivf.getCompleteDenturesD5110D5120FL() });// 30

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D5130", "ImmediateDentures_D5130_D5140_FL", ivf.getImmediateDenturesD5130D5140FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D5130",dL);
		//mapFlIVF.put("D5130", new String[] { "ImmediateDentures_D5130_D5140_FL", ivf.getImmediateDenturesD5130D5140FL() });// 31
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D5140", "ImmediateDentures_D5130_D5140_FL", ivf.getImmediateDenturesD5130D5140FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D5140",dL);
		//mapFlIVF.put("D5140", new String[] { "ImmediateDentures_D5130_D5140_FL", ivf.getImmediateDenturesD5130D5140FL() });// 32

		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D5213", "PartialDentures_D5213_D5214_FL", ivf.getPartialDenturesD5213D5214FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D5213",dL);
		//mapFlIVF.put("D5213", new String[] { "PartialDentures_D5213_D5214_FL", ivf.getPartialDenturesD5213D5214FL() });// 33
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D5214", "PartialDentures_D5213_D5214_FL", ivf.getPartialDenturesD5213D5214FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D5214", "InterimPartialDentures_D5214_FL", ivf.getInterimPartialDenturesD5214FL(),0,0);
		dL.add(scivftff);
		mapFlIVF.put("D5214",dL);
		//mapFlIVF.put("D5214", new String[] { "PartialDentures_D5213_D5214_FL", ivf.getPartialDenturesD5213D5214FL(),
//
	//			"InterimPartialDentures_D5214_FL", ivf.getInterimPartialDenturesD5214FL() });// 34
		
		
		scivftff=new ServiceCodeIvfTimesFreqFieldDto("D9310", "D9310_FL", ivf.getD9310FL(),0,0);
		dL=new ArrayList<>();
		dL.add(scivftff);
		mapFlIVF.put("D9310",dL);
        //mapFlIVF.put("D9310", new String[] { "D9310_FL", ivf.getD9310FL() });// 35
		RuleEngineLogger.generateLogs(clazz,"Create Map with Key as Service Code..and values as Given in the Document ..-mapFlIVF", Constants.rule_log_debug,bw);
		
		boolean pass = true;
		int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
		Date planDate = null;
		try {
			// Calendar calendar = new GregorianCalendar();
			// calendar.setTime(Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate()));
			planDate = Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate());

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, List<ToothHistoryDto>> mapHistoryTooth = new HashMap<>();
        Date TP_Date=null;
		ToothHistoryDto hdto = null;
		boolean historPresent = false;
		int noOFhistory = 9;
		List<ToothHistoryDto> list1 = null;
		for (int i = 1; i <= noOFhistory; i++) {
			if (i == 1)
				hdto = new ToothHistoryDto(ivf.getHistory1Code(), ivf.getHistory1DOS(), ivf.getHistory1Tooth());
			else if (i == 2)
				hdto = new ToothHistoryDto(ivf.getHistory2Code(), ivf.getHistory2DOS(), ivf.getHistory2Tooth());
			else if (i == 3)
				hdto = new ToothHistoryDto(ivf.getHistory3Code(), ivf.getHistory3DOS(), ivf.getHistory3Tooth());
			else if (i == 4)
				hdto = new ToothHistoryDto(ivf.getHistory4Code(), ivf.getHistory4DOS(), ivf.getHistory4Tooth());
			else if (i == 5)
				hdto = new ToothHistoryDto(ivf.getHistory5Code(), ivf.getHistory5DOS(), ivf.getHistory5Tooth());
			else if (i == 6)
				hdto = new ToothHistoryDto(ivf.getHistory6Code(), ivf.getHistory6DOS(), ivf.getHistory6Tooth());
			else if (i == 7)
				hdto = new ToothHistoryDto(ivf.getHistory7Code(), ivf.getHistory7DOS(), ivf.getHistory7Tooth());
			else if (i == 8)
				hdto = new ToothHistoryDto(ivf.getHistory8Code(), ivf.getHistory8DOS(), ivf.getHistory8Tooth());
			else if (i == 9)
				hdto = new ToothHistoryDto(ivf.getHistory9Code(), ivf.getHistory9DOS(), ivf.getHistory9Tooth());
			if (hdto.getHistoryCode() != null && !hdto.getHistoryCode().equals("")
					&& !hdto.getHistoryCode().equalsIgnoreCase("blank")) {
				for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry : mapFlIVF.entrySet()) {
					if (entry.getKey().equals(hdto.getHistoryCode())) {
						historPresent = true;
						String toothTR[] = ToothUtil.getToothsFromTooth(hdto.getHistoryTooth());
						for (String tooth : toothTR) {
							if (mapHistoryTooth.containsKey(tooth)) {
								list1 = mapHistoryTooth.get(tooth);
								list1.addAll(list1);
							} else {
								list1 = new ArrayList<>();
								list1.add(hdto);
								mapHistoryTooth.put(tooth, list1);

							}

						}
					}

				}

			}

		} // For end
		
		RuleEngineLogger.generateLogs(clazz,"Create Map with History Data From IVF Data--- with Key as Totoh Number and Values as History,Serice Code ..", Constants.rule_log_debug,bw);

		Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> mapFlIVFFinal = new HashMap<>();//Now key will be tooth

		if (historPresent) {
			RuleEngineLogger.generateLogs(clazz,"History is present  now proceed Further..", Constants.rule_log_debug,bw);
			
			Map<String, List<String>> tpToothMap = null;
			List<String> list = null;
			for (Object t : tpList) {
				TreatmentPlan tp = (TreatmentPlan) t;
				try {
					TP_Date = Constants.SIMPLE_DATE_FORMAT
							.parse(tp.getTreatmentPlanDetails().getDateLastUpdated());
				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry : mapFlIVF.entrySet()) {
					if (entry.getKey().equals(tp.getServiceCode())) {
						String toothTR[] = ToothUtil.getToothsFromTooth(tp.getTooth());
						for (String tooth : toothTR) {
							if (tpToothMap == null)
								tpToothMap = new HashMap<>();
							if (tpToothMap.containsKey(tooth)) {
								list = (List<String>) tpToothMap.get(tooth);
								list.add(tp.getServiceCode());
							} else {
								list = new ArrayList<>();
								list.add(tp.getServiceCode());
								tpToothMap.put(tooth, list);

							}

						}

					}

				}//FoR

			} // for end
			RuleEngineLogger.generateLogs(clazz,"Create a Map with Keys as Tooth Number -tpToothMap - "
					+ "This is created by Using Tooth Number from Treatment Plan, if that tooth No's service code"
					+ "is found in the Fields where we have to Check for Frequncy Limits ..Rest codes are ignored.-tpToothMap "
					+ "", Constants.rule_log_debug,bw);

			if (mapFlIVF != null) {
				
				for (Map.Entry<String, List<String>> entry : tpToothMap.entrySet()) {
					String tooth = entry.getKey();
					List<ToothHistoryDto> hd = mapHistoryTooth.get(tooth);
					if (hd!=null && hd.size()>0) {
					List<String> fromTreatmentPlanCode = entry.getValue();
					for (String tpCode : fromTreatmentPlanCode) {
						for (ToothHistoryDto historyD : hd) {
							RuleEngineLogger.generateLogs(clazz,"Treatment Plan code- "+tpCode+" History Code-"+historyD.getHistoryCode()
									+ "", Constants.rule_log_debug,bw);
                             if (tpCode.equalsIgnoreCase(historyD.getHistoryCode().trim())) {
                            	 
                            	 List<ServiceCodeIvfTimesFreqFieldDto> dataIVF=mapFlIVF.get(tpCode);
                            		 for(ServiceCodeIvfTimesFreqFieldDto scivfTFD:dataIVF) {
                            			 	String freq = scivfTFD.getFreqency();
											
											ServiceCodeIvfTimesFreqFieldDto  scivfTFDFinal = new ServiceCodeIvfTimesFreqFieldDto(tpCode, 
													scivfTFD.getFieldName(), scivfTFD.getFreqency(),0,0); 
											scivfTFDFinal.setTooth(tooth);
											scivfTFDFinal.setDos(historyD.getHistoryDos());
											RuleEngineLogger.generateLogs(clazz,"HISTORY CODE- "+historyD.getHistoryCode(), Constants.rule_log_debug,bw);
											RuleEngineLogger.generateLogs(clazz,"HISTORY DOS- "+historyD.getHistoryDos(), Constants.rule_log_debug,bw);
											
											RuleEngineLogger.generateLogs(clazz,"Frequency- "+freq, Constants.rule_log_debug,bw);
											FreqencyDto FDTO = FreqencyUtils.parseFrequecy(freq);
											Date[] datesFIS = DateUtils.getFiscalYear(FDTO.getFy());
											int ti = FDTO.getTimes();
											scivfTFDFinal.setTimes(ti);
											RuleEngineLogger.generateLogs(clazz,"Tooth Number-"+tooth, Constants.rule_log_debug,bw);
															
											Date dos=null;
											try {
												dos = Constants.SIMPLE_DATE_FORMAT
														.parse(historyD.getHistoryDos());
												RuleEngineLogger.generateLogs(clazz,"History DOS-"+historyD.getHistoryDos(), Constants.rule_log_debug,bw);
																} catch (ParseException e2) {
												// TODO Auto-generated catch block
												e2.printStackTrace();
											}
											RuleEngineLogger.generateLogs(clazz,"TIMES:"+ti, Constants.rule_log_debug,bw);
											if (FDTO.getFy() > 0) {// Fiscal Year
												// isFiscalpresent=true;
												RuleEngineLogger.generateLogs(clazz,"Fiscal Year:"+FDTO.getFy(), Constants.rule_log_debug,bw);
												RuleEngineLogger.generateLogs(clazz,"Fiscal Year:"+datesFIS[0]+"-"+datesFIS[1], Constants.rule_log_debug,bw);
												boolean fiscal = DateUtils.isDatesBetweenDates(datesFIS[0],
													datesFIS[1], dos);
												RuleEngineLogger.generateLogs(clazz,"Fiscal Year:"+fiscal, Constants.rule_log_debug,bw);
																	if (fiscal) {
													// fiscalCount=fiscalCount+1;
													scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);
													//expCount = expCount + 1;
												}
											} else if (FDTO.getCy() > 0) {// Calendar Year
												RuleEngineLogger.generateLogs(clazz,"Calendar Year:"+FDTO.getCy(), Constants.rule_log_debug,bw);
												RuleEngineLogger.generateLogs(clazz,"CurrentYear:"+CurrentYear, Constants.rule_log_debug,bw);
												// isCalPresent=true;
												// CurrentYear
												Calendar calendar = new GregorianCalendar();
												calendar.setTime(dos);
												RuleEngineLogger.generateLogs(clazz,"CurrentYear From DOS:"+calendar.get(Calendar.YEAR), Constants.rule_log_debug,bw);
												if (calendar.get(Calendar.YEAR) == CurrentYear) {
													scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);
												}

											} else if (FDTO.getLt() > 0) {// Life Time
												RuleEngineLogger.generateLogs(clazz,"Life Time:"+FDTO.getLt(), Constants.rule_log_debug,bw);
												// isLifeTimePresent=true;
												scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);

											} else if (FDTO.getPy() > 0) {// Plan Year
												RuleEngineLogger.generateLogs(clazz,"Plan Year:"+FDTO.getPy(), Constants.rule_log_debug,bw);
												// isPlanYearPresent=true;
												Calendar calendar = new GregorianCalendar();
												Date nextDate = DateUtils.getNextYear(planDate);
												// calendar.set(calendar.get(Calendar.YEAR)+1,calendar.get(Calendar.MONTH),
												// calendar.get(Calendar.DATE)+1);
												calendar.setTime(dos);
												boolean fiscal = DateUtils.isDatesBetweenDates(planDate,
														nextDate, dos);
												if (fiscal) {
													scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);
												}
											} else if (FDTO.getCalendarMonth() > 0) {// Calendar Months
												RuleEngineLogger.generateLogs(clazz,"Calendar Months:"+FDTO.getCalendarMonth(), Constants.rule_log_debug,bw);
                                                 ///Complete ME
												Calendar calendar = new GregorianCalendar();
												calendar.setTime(planDate);
												calendar.set(calendar.get(Calendar.YEAR),
														calendar.get(Calendar.MONTH) + FDTO.getCalendarMonth(),
														calendar.get(Calendar.DATE));	
												Date nextDate = calendar.getTime();
												boolean fiscal = DateUtils.isDatesBetweenDates(planDate,
														nextDate, dos);
												if (fiscal) {
													scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);
												}
												
											} else if (FDTO.getDays() > 0) {// Months & Days
												RuleEngineLogger.generateLogs(clazz," Days:"+FDTO.getDays(), Constants.rule_log_debug,bw);
												RuleEngineLogger.generateLogs(clazz,"Months :"+FDTO.getMonths(), Constants.rule_log_debug,bw);
												//
												Calendar calendar = new GregorianCalendar();
												calendar.setTime(TP_Date);
												calendar.set(calendar.get(Calendar.YEAR),
														calendar.get(Calendar.MONTH) - FDTO.getMonths(),
														calendar.get(Calendar.DATE));
												calendar.set(calendar.get(Calendar.YEAR),
														calendar.get(Calendar.MONTH),
														calendar.get(Calendar.DATE) + FDTO.getDays());

												Long dayhis = DateUtils.daysBetweenDates(dos,
														TP_Date);
												Long days = DateUtils.daysBetweenDates(calendar.getTime(),
														TP_Date);
												if ((days - dayhis) > 0) {
													scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);
												}

											} else if (FDTO.getOnlyDays() > 0) {//  Days
												RuleEngineLogger.generateLogs(clazz," Days:"+FDTO.getOnlyDays(), Constants.rule_log_debug,bw);
												//
												Calendar calendar = new GregorianCalendar();
												calendar.setTime(TP_Date);
												calendar.set(calendar.get(Calendar.YEAR),
														calendar.get(Calendar.MONTH),
														calendar.get(Calendar.DATE) -FDTO.getOnlyDays());

												Long dayhis = DateUtils.daysBetweenDates(dos,
														TP_Date);
												Long days = DateUtils.daysBetweenDates(calendar.getTime(),
														TP_Date);
												if ((days - dayhis) > 0) {
													scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);
												}

											} else if (FDTO.getMonths() > 0) {// Months
												RuleEngineLogger.generateLogs(clazz,"Months:"+FDTO.getMonths(), Constants.rule_log_debug,bw);
												Calendar calendar = new GregorianCalendar();
												calendar.setTime(TP_Date);
												calendar.set(calendar.get(Calendar.YEAR),
														calendar.get(Calendar.MONTH) - FDTO.getMonths(),
														calendar.get(Calendar.DATE));
												Long dayhis = DateUtils.daysBetweenDates(dos,
														TP_Date);
												Long days = DateUtils.daysBetweenDates(calendar.getTime(),
														TP_Date);
												if ((days - dayhis) > 0) {
													scivfTFDFinal.setCount(scivfTFDFinal.getCount()+1);
												}
											}

                            			 ////
											List<ServiceCodeIvfTimesFreqFieldDto> ln=mapFlIVFFinal.get(tooth);
											if (ln==null) ln=new ArrayList<>();
											ln.add(scivfTFDFinal);
                            		 }
                            		 
                            	 //}
                             }
				 		}
					}
				}
				}
				
				
				
			}
			//MAIN LOGIC ALIKE CODES
			/*
			    D1206 and D1208
				D4341 and D4342
				D1110 and D1120
				D0272 and D0274
				D0150, D0120, and D0140

			 */
			//    D1206 and D1208
			for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry : mapFlIVFFinal.entrySet()) { 
				
				String tooth=entry.getKey();
				List<ServiceCodeIvfTimesFreqFieldDto> li=entry.getValue();
				List<ServiceCodeIvfTimesFreqFieldDto> D1206=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D1208=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D4341=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D4342=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D1110=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D1120=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D0272=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D0274=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D0150=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D0120=null;
				List<ServiceCodeIvfTimesFreqFieldDto> D0140=null;
				List<ServiceCodeIvfTimesFreqFieldDto> RestC=null;
				
				for(ServiceCodeIvfTimesFreqFieldDto d:li) {
					if (d.getServiceCode().equals("D1206")) {
						if (D1206==null) D1206=new ArrayList<>();
						D1206.add(d);
					}else if (d.getServiceCode().equals("D1208")) {
						if (D1208==null) D1208=new ArrayList<>();
						D1208.add(d);
					}else if (d.getServiceCode().equals("D4341")) {
						if (D4341==null) D4341=new ArrayList<>();
						D4341.add(d);
					}else if (d.getServiceCode().equals("D4342")) {
						if (D4342==null) D4342=new ArrayList<>();
						D4342.add(d);
					}else if (d.getServiceCode().equals("D1110")) {
						if (D1110==null) D1110=new ArrayList<>();
						D1110.add(d);
					}else if (d.getServiceCode().equals("D1120")) {
						if (D1120==null) D1120=new ArrayList<>();
						D1120.add(d);
					}else if (d.getServiceCode().equals("D0272")) {
						if (D0272==null) D0272=new ArrayList<>();
						D0272.add(d);
					}else if (d.getServiceCode().equals("D0274")) {
						if (D0274==null) D0274=new ArrayList<>();
						D1206.add(d);
					}else if (d.getServiceCode().equals("D0150")) {
						if (D0150==null) D0150=new ArrayList<>();
						D0150.add(d);
					}else if (d.getServiceCode().equals("D0120")) {
						if (D0120==null) D0120=new ArrayList<>();
						D0120.add(d);
					}else if (d.getServiceCode().equals("D0140")) {
						if (D0140==null) D0140=new ArrayList<>();
						D0140.add(d);
					}else {
						if(RestC==null) RestC= new ArrayList<>();
						RestC.add(d);
					}
				}//For Loop d:li
				//Alike code
				if (D1206!=null && D1208!=null) {
					Object [] m=FreqencyUtils.getError(D1206, D1208, "D1206", "D1208", tooth);
					if (m!=null) {
						pass=false;
						dList.add(
								new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule21.error.message",
												m, locale),
										Constants.FAIL));
					}
				}
				if (D1206!=null && D1208!=null) {
					Object [] m=FreqencyUtils.getError(D4341,  D4342, "D4341", "D4342", tooth);
					if (m!=null) {
						pass=false;
						dList.add(
								new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule21.error.message",
												m, locale),
										Constants.FAIL));
					}
				}
				if (D1206!=null && D1208!=null) {
					Object [] m=FreqencyUtils.getError(D1110 , D1120, "D1110", "D1120", tooth);
					if (m!=null) {
						pass=false;
						dList.add(
								new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule21.error.message",
												m, locale),
										Constants.FAIL));
					}
				}
				if (D1206!=null && D1208!=null) {
					Object [] m=FreqencyUtils.getError(D0272 , D0274, "D0272", "D0274", tooth);
					if (m!=null) {
						pass=false;
						dList.add(
								new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule21.error.message",
												m, locale),
										Constants.FAIL));
						
					}
				}
				if (D1206!=null && D1208!=null) {
					Object [] m=FreqencyUtils.getError(D0150, D0120,D0140, "D0150", "D0120","D0140", tooth);
					if (m!=null) {
						pass=false;
						dList.add(
								new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule21.error.message",
												m, locale),
										Constants.FAIL));
						
					}
				}
				Map<String,List<ServiceCodeIvfTimesFreqFieldDto>> fm=new HashMap<>();
				for(ServiceCodeIvfTimesFreqFieldDto s:li) {
					List<ServiceCodeIvfTimesFreqFieldDto> l2=fm.get(s.getServiceCode());
					if (l2==null) l2= new ArrayList<>();
					l2.add(s);
					
				}
				for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry2 : fm.entrySet()) { 
					List<ServiceCodeIvfTimesFreqFieldDto> s=   entry2.getValue();
					Object[] m=FreqencyUtils.getCountTimeServiceCode(s,entry2.getKey());
					if (m!=null) {
						pass=false;
						dList.add(
								new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule21.error.message",
												m, locale),
										Constants.FAIL));			
					}
				}
				
			}
			

		}//HISTORY PRESENT
		else {
			RuleEngineLogger.generateLogs(clazz,"History not Present...", Constants.rule_log_debug,bw);

		}
        if (pass) dList.add(
				new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass",
								new Object[] {}, locale),
						Constants.PASS));
		return dList;

	}

	public List<TPValidationResponseDto> Rule21OLD(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings) {

		List<TPValidationResponseDto> dList = new ArrayList<>();
		if (tpList == null) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return dList;
		}
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		boolean pass = true;

		Map<String, String[]> mapFl = new HashMap<>();
		mapFl.put("D1208", new String[] { "FlourideD1208FL", ivf.getFlourideD1208FL() });// 1 Flouride_D1208_FL
		mapFl.put("D1206", new String[] { "Varnish_D1206_FL", ivf.getVarnishD1206FL() });// 2

		mapFl.put("D0270", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 3
		mapFl.put("D0272", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 4
		mapFl.put("D0273", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 5
		mapFl.put("D0274", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });// 6

		mapFl.put("D0220", new String[] { "XRaysPA_D0220_FL", ivf.getxRaysPAD0220FL() });// 7
		mapFl.put("D0230", new String[] { "XRaysPA_D0230_FL", ivf.getxRaysPAD0230FL() });// 8
		mapFl.put("D0210", new String[] { "XRaysFMX_FL", ivf.getxRaysFMXFL() });// 9
		mapFl.put("D0150", new String[] { "ExamsD0150_FL", ivf.getExamsD0150FL() });// 10
		mapFl.put("D0145", new String[] { "ExamsD0145_FL", ivf.geteExamsD0145FL() });// 11
		mapFl.put("D0140", new String[] { "ExamsD0140_FL", ivf.getExamsD0140FL() });// 12
		mapFl.put("D0120", new String[] { "ExamsD0120_FL", ivf.getExamsD0120FL() });// 13
		mapFl.put("D2930", new String[] { "SSC_D2930_FL", ivf.getsSCD2930FL() });// 14
		mapFl.put("D2931", new String[] { "SSC_D2931_FL", ivf.getsSCD2931FL() });// 15
		mapFl.put("D1351", new String[] { "Sealants_D1351_FL", ivf.getSealantsD1351FL() });// 16
		mapFl.put("D4346", new String[] { "Gingivitis_D4346_FL", ivf.getGingivitisD4346FL() });// 17
		mapFl.put("D4341", new String[] { "SRP_D4341_FL", ivf.getsRPD4341FL() });// 18
		mapFl.put("D9940", new String[] { "NightGuards_D9940_FL", ivf.getNightGuardsD9940FL() });// 19
		mapFl.put("D4910", new String[] { "PerioMaintenance_D4910_FL", ivf.getPerioMaintenanceD4910FL() });// 20
		mapFl.put("D4355", new String[] { "FMD_D4355_FL", ivf.getfMDD4355FL() });// 21
		mapFl.put("D4249", new String[] { "CrownLength_D4249_FL", ivf.getCrownLengthD4249FL() });// 22
		mapFl.put("D2950", new String[] { "BuildUps_D2950_FL", ivf.getBuildUpsD2950FL() });// 23
		mapFl.put("D7953", new String[] { "BoneGrafts_D7953_FL", ivf.getBoneGraftsD7953FL() });// 24
		mapFl.put("D7311", new String[] { "Alveo_D7311_FL", ivf.getAlveoD7311FL() });// 25

		mapFl.put("D7310", new String[] { "Alveo_D7310_FL", ivf.getAlveoD7310FL() });// 26

		mapFl.put("D2750", new String[] { "Crowns_D2750_D2740_FL", ivf.getCrownsD2750D2740FL() });// 27
		mapFl.put("D2740", new String[] { "Crowns_D2750_D2740_FL", ivf.getCrownsD2750D2740FL() });// 28

		mapFl.put("D5110", new String[] { "CompleteDentures_D5110_D5120_FL", ivf.getCompleteDenturesD5110D5120FL() });// 29
		mapFl.put("D5120", new String[] { "CompleteDentures_D5110_D5120_FL", ivf.getCompleteDenturesD5110D5120FL() });// 30

		mapFl.put("D5130", new String[] { "ImmediateDentures_D5130_D5140_FL", ivf.getImmediateDenturesD5130D5140FL() });// 31
		mapFl.put("D5140", new String[] { "ImmediateDentures_D5130_D5140_FL", ivf.getImmediateDenturesD5130D5140FL() });// 32

		mapFl.put("D5213", new String[] { "PartialDentures_D5213_D5214_FL", ivf.getPartialDenturesD5213D5214FL() });// 33
		mapFl.put("D5214", new String[] { "PartialDentures_D5213_D5214_FL", ivf.getPartialDenturesD5213D5214FL(),

				"InterimPartialDentures_D5214_FL", ivf.getInterimPartialDenturesD5214FL() });// 34
		mapFl.put("D9310", new String[] { "D9310_FL", ivf.getD9310FL() });// 35

		/*
		 * String history_1_Code=ivf.getHistory1Code(); String
		 * history_1_DOS=ivf.getHistory1DOS(); String
		 * history_1_Tooth=ivf.getHistory1Tooth();
		 * 
		 * String history_2_Code=ivf.getHistory2Code(); String
		 * history_2_DOS=ivf.getHistory2DOS(); String
		 * history_2_Tooth=ivf.getHistory2Tooth();
		 * 
		 * String history_3_Code=ivf.getHistory3Code(); String
		 * history_3_DOS=ivf.getHistory3DOS(); String
		 * history_3_Tooth=ivf.getHistory3Tooth();
		 * 
		 * String history_4_Code=ivf.getHistory4Code(); String
		 * history_4_DOS=ivf.getHistory4DOS(); String
		 * history_4_Tooth=ivf.getHistory4Tooth();
		 * 
		 * String history_5_Code=ivf.getHistory5Code(); String
		 * history_5_DOS=ivf.getHistory5DOS(); String
		 * history_5_Tooth=ivf.getHistory5Tooth();
		 * 
		 * String history_6_Code=ivf.getHistory6Code(); String
		 * history_6_DOS=ivf.getHistory6DOS(); String
		 * history_6_Tooth=ivf.getHistory6Tooth();
		 * 
		 * String history_7_Code=ivf.getHistory7Code(); String
		 * history_7_DOS=ivf.getHistory7DOS(); String
		 * history_7_Tooth=ivf.getHistory7Tooth();
		 * 
		 * String history_8_Code=ivf.getHistory8Code(); String
		 * history_8_DOS=ivf.getHistory8DOS(); String
		 * history_8_Tooth=ivf.getHistory8Tooth();
		 * 
		 * String history_9_Code=ivf.getHistory9Code(); String
		 * history_9_DOS=ivf.getHistory9DOS(); String
		 * history_9_Tooth=ivf.getHistory9Tooth();
		 */
		Map<String, ToothHistoryDto> mapHistory = new HashMap<String, ToothHistoryDto>();

		ToothHistoryDto hdto = null;
		boolean historPresent = false;
		int noOFhistory = 9;
		for (int i = 1; i <= noOFhistory; i++) {
			if (i == 1)
				hdto = new ToothHistoryDto(ivf.getHistory1Code(), ivf.getHistory1DOS(), ivf.getHistory1Tooth());
			else if (i == 2)
				hdto = new ToothHistoryDto(ivf.getHistory2Code(), ivf.getHistory2DOS(), ivf.getHistory2Tooth());
			else if (i == 3)
				hdto = new ToothHistoryDto(ivf.getHistory3Code(), ivf.getHistory3DOS(), ivf.getHistory3Tooth());
			else if (i == 4)
				hdto = new ToothHistoryDto(ivf.getHistory4Code(), ivf.getHistory4DOS(), ivf.getHistory4Tooth());
			else if (i == 5)
				hdto = new ToothHistoryDto(ivf.getHistory5Code(), ivf.getHistory5DOS(), ivf.getHistory5Tooth());
			else if (i == 6)
				hdto = new ToothHistoryDto(ivf.getHistory6Code(), ivf.getHistory6DOS(), ivf.getHistory6Tooth());
			else if (i == 7)
				hdto = new ToothHistoryDto(ivf.getHistory7Code(), ivf.getHistory7DOS(), ivf.getHistory7Tooth());
			else if (i == 8)
				hdto = new ToothHistoryDto(ivf.getHistory8Code(), ivf.getHistory8DOS(), ivf.getHistory8Tooth());
			else if (i == 9)
				hdto = new ToothHistoryDto(ivf.getHistory9Code(), ivf.getHistory9DOS(), ivf.getHistory9Tooth());
			if (hdto.getHistoryCode() != null && !hdto.getHistoryCode().equals("")
					&& !hdto.getHistoryCode().equalsIgnoreCase("blank")) {
				historPresent = true;
				mapHistory.put(i + "", hdto);
			}

		}

		// Date cdate = new Date();
		Date TP_Date = null;
		if (historPresent) {
			// If Yes then pull all the applicable codes and tooth numbers using the
			// Downgrading Mapping tables...
			List<Mappings> mapps = getMappingFrequencyApplicableFomList(mappings);
			HistoryMatcherDto hdm = null;
			ServiceCodeDateDto hdmDate = null;
			Map<String, HistoryMatcherDto> mapHdm = null;// THIS MAP WILL CONTAIN THE COMMMON CODE USED IN TREATEMENT
															// PLAN AND THE HISTORY DATA .Key will be TOOTH NUMBER
			for (Object t : tpList) {
				TreatmentPlan tp = (TreatmentPlan) t;
				Collection<Mappings> mL = Collections2.filter(mapps,
						y -> y.getAdaCodes().getCode().equals((tp).getServiceCode()));

				// Service code found in Mappings then Code is applicable for Check
				if (mL != null && mL.size() > 0) {

					// for (Mappings m : mL) {

					String toothTR[] = ToothUtil.getToothsFromTooth(tp.getTooth());
					for (Map.Entry<String, ToothHistoryDto> entry : mapHistory.entrySet()) {
						ToothHistoryDto tt = entry.getValue();
						if (tt.getHistoryCode() != null && (

						tt.getHistoryCode().trim().equals(tp.getServiceCode()))) {
							// found ...
							try {
								TP_Date = Constants.SIMPLE_DATE_FORMAT
										.parse(tp.getTreatmentPlanDetails().getDateLastUpdated());
							} catch (ParseException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
							String toothHis[] = ToothUtil.getToothsFromTooth(tt.getHistoryTooth());
							List<String> th = ToothUtil.findCommonTooth(toothHis, toothTR);
							if (th != null && th.size() > 0) {

								for (String ttt : th) {

									if (mapHdm == null)
										mapHdm = new HashMap<String, HistoryMatcherDto>();
									if (mapHdm.containsKey(ttt)) {
										// if the key has already been used,
										// we'll just grab the array list and add the value to it
										hdm = mapHdm.get(ttt);
										List<ServiceCodeDateDto> hi = hdm.getServiceCodeDate();
										try {
											hdmDate = new ServiceCodeDateDto(tt.getHistoryCode(),
													Constants.SIMPLE_DATE_FORMAT.parse(tt.getHistoryDos()));
											Date rdate = hdm.getMostRecentDos();
											Date xxp = null;
											xxp = Constants.SIMPLE_DATE_FORMAT.parse(tt.getHistoryDos());

											// Please check
											if (!DateUtils.compareDates(rdate, xxp)) {
												rdate = xxp;
											}
											// Put only Latest Date..
											// String xxCode = tt.getHistoryCode();
											/*
											 * if (xxCode.equals("D1206") || xxCode.equals("D1208")) { xxCode =
											 * "D1206-D1208"; } if (xxCode.equals("D4341") || xxCode.equals("D4342")) {
											 * xxCode = "D4341-D4342"; } if (xxCode.equals("D1110") ||
											 * xxCode.equals("D1120")) { xxCode = "D1110-D1120"; } if
											 * (xxCode.equals("D0150") || xxCode.equals("D0120") ||
											 * xxCode.equals("D0140")) { xxCode = "D0150-D0120-D0140"; }
											 */
											hi.add(hdmDate);
											hdm.setCount(hdm.getCount() + 1);
											hdm.setMostRecentDos(rdate);// @formatter:off
										} catch (Exception es) {

											// @formatter:on

										}
									} else {
										// if the key hasn't been used yet,
										// we'll create a new ArrayList<String> object, add the value
										// and put it in the array list with the new key
										try {
											List<ServiceCodeDateDto> xxx = new ArrayList<>();
											hdmDate = new ServiceCodeDateDto(tt.getHistoryCode(),
													Constants.SIMPLE_DATE_FORMAT.parse(tt.getHistoryDos()));
											// List<Date> xxxD = new ArrayList<>();
											// String ccode = tt.getHistoryCode();

											// String xxCode = tt.getHistoryCode();
											/*
											 * if (xxCode.equals("D1206") || xxCode.equals("D1208")) { ccode =
											 * "D1206-D1208"; } if (xxCode.equals("D4341") || xxCode.equals("D4342")) {
											 * ccode = "D4341-D4342"; } if (xxCode.equals("D1110") ||
											 * xxCode.equals("D1120")) { ccode = "D1110-D1120"; } if
											 * (xxCode.equals("D0150") || xxCode.equals("D0120") ||
											 * xxCode.equals("D0140")) { ccode = "D0150-D0120-D0140"; }
											 */
											xxx.add(hdmDate);

											// List<ServiceCodeDateDto> serviceCodeDate, Date mostRecentDos,
											// String historyTooth, int count
											hdm = new HistoryMatcherDto(xxx,
													Constants.SIMPLE_DATE_FORMAT.parse(tt.getHistoryDos()), ttt, 1);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										mapHdm.put(ttt, hdm);
									}

								}

								if (mapHdm != null) {

									int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
									Date planDate = null;
									try {
										// Calendar calendar = new GregorianCalendar();
										// calendar.setTime(Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate()));
										planDate = Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate());

									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									Map<String, List<String>> finalCheckMap = new HashMap<>(); // here key =(Tooth+
																								// Service code
																								// --Combination) List :
																								// will be 0 =Original
																								// Count,1 Count, 1=Dos,
																								// 2=Field Name 3:
																								// Times: 4 Tooth
									for (Map.Entry<String, HistoryMatcherDto> ex : mapHdm.entrySet()) {
										String tooth = ex.getKey();
										HistoryMatcherDto dx = ex.getValue();

										try {
											boolean expPresent = false;
											int expCount = 0;
											String doss = "";
											String comma = "";
											for (ServiceCodeDateDto sddto : dx.getServiceCodeDate()) {
												doss = doss + sddto.getDos() + comma;
												comma = ",";
												String[] valAndFreq = mapFl.get(sddto.getServiceCode());// From Field
																										// Map...

												for (int p = 0; p < valAndFreq.length; p = p + 1) {
													String fieldVal = valAndFreq[p];
													p = p + 1;
													String freq = valAndFreq[p];

													FreqencyDto FDTO = FreqencyUtils.parseFrequecy(freq);
													Date[] datesFIS = DateUtils.getFiscalYear(FDTO.getFy());
													int ti = FDTO.getTimes();
													if (FDTO.getFy() > 0) {// Fiscal Year
														// isFiscalpresent=true;
														expPresent = true;
														boolean fiscal = DateUtils.isDatesBetweenDates(datesFIS[0],
																datesFIS[1], sddto.getDos());
														if (fiscal) {
															// fiscalCount=fiscalCount+1;
															expCount = expCount + 1;
														}
													} else if (FDTO.getCy() > 0) {// Calendar Year
														// isCalPresent=true;
														expPresent = true;
														// CurrentYear
														Calendar calendar = new GregorianCalendar();
														calendar.setTime(sddto.getDos());
														if (calendar.get(Calendar.YEAR) == CurrentYear) {
															// calCount=calCount+1;
															expCount = expCount + 1;
														}

													} else if (FDTO.getLt() > 0) {// Life Time
														// isLifeTimePresent=true;
														expPresent = true;
														// lifeCount=lifeCount+1;
														expCount = expCount + 1;

													} else if (FDTO.getPy() > 0) {// Plan Year
														// isPlanYearPresent=true;
														expPresent = true;
														Calendar calendar = new GregorianCalendar();
														Date nextDate = DateUtils.getNextYear(planDate);
														// calendar.set(calendar.get(Calendar.YEAR)+1,calendar.get(Calendar.MONTH),
														// calendar.get(Calendar.DATE)+1);
														calendar.setTime(sddto.getDos());
														boolean fiscal = DateUtils.isDatesBetweenDates(planDate,
																nextDate, sddto.getDos());
														if (fiscal) {
															// planCount=planCount+1;
															expCount = expCount + 1;
														}
													} else if (FDTO.getCalendarMonth() > 0) {// Calendar Months

													} else if (FDTO.getDays() > 0) {// Months & Days
														//
														expPresent = true;
														Calendar calendar = new GregorianCalendar();
														calendar.setTime(TP_Date);
														calendar.set(calendar.get(Calendar.YEAR),
																calendar.get(Calendar.MONTH) - FDTO.getMonths(),
																calendar.get(Calendar.DATE));
														calendar.set(calendar.get(Calendar.YEAR),
																calendar.get(Calendar.MONTH),
																calendar.get(Calendar.DATE) + FDTO.getDays());

														Long dayhis = DateUtils.daysBetweenDates(sddto.getDos(),
																TP_Date);
														Long days = DateUtils.daysBetweenDates(calendar.getTime(),
																TP_Date);
														if ((days - dayhis) > 0) {
															// daysMonthCount=daysMonthCount+1;
															expCount = expCount + 1;
														}

													} else if (FDTO.getMonths() > 0) {// Months
														expPresent = true;
														Calendar calendar = new GregorianCalendar();
														calendar.setTime(TP_Date);
														calendar.set(calendar.get(Calendar.YEAR),
																calendar.get(Calendar.MONTH) - FDTO.getMonths(),
																calendar.get(Calendar.DATE));
														Long dayhis = DateUtils.daysBetweenDates(sddto.getDos(),
																TP_Date);
														Long days = DateUtils.daysBetweenDates(calendar.getTime(),
																TP_Date);
														if ((days - dayhis) > 0) {
															// daysMonthCount=daysMonthCount+1;
															expCount = expCount + 1;
														}
													}
													// here String[] 0 = Count, 1=Dos, 2=Field Name, 3 Times, 4 Tooth
													// Already Exists logic
													// finalCheckMap.put(sddto.getServiceCode(),new String[]
													// {expCount+"",doss,fieldVal});
													if (finalCheckMap
															.containsKey(tooth + "----" + sddto.getServiceCode())) {
														List<String> list = finalCheckMap.get(sddto.getServiceCode());
														list.add(0 + "");// Final Count ..Update Latter below
														list.add(expCount + "");
														list.add(doss);
														list.add(fieldVal);
														list.add(ti + "");
														list.add(tooth);

													} else {
														List<String> list = new ArrayList<>();
														list.add(0 + "");// Final Count ..Update Latter below
														list.add(expCount + "");
														list.add(doss);
														list.add(fieldVal);
														list.add(ti + "");
														list.add(tooth);

														finalCheckMap.put(tooth + "----" + sddto.getServiceCode(),
																list);
													}

												} // For

											} // For

											// }//For Map
											/*
											 * if (isFiscalpresent && fiscalCount>ti) { pass=false;//For this key } if
											 * (isCalPresent && calCount>ti) { pass=false;//For this key
											 * 
											 * } if (isLifeTimePresent && lifeCount>ti) { pass=false;//For this key
											 * 
											 * } if (isPlanYearPresent && planCount>ti) { pass=false;//For this key
											 * 
											 * } if (isMonthandDaysPresent && daysMonthCount>ti) { pass=false;//For this
											 * key
											 * 
											 * }
											 */
											/*
											 * if(expPresent && expCount>ti) { pass=false;//For this key dList.add(new
											 * TPValidationResponseDto(-1, "no rule",
											 * messageSource.getMessage("rule21.error.message", new Object[]
											 * {tooth,doss,fkey }, locale), Constants.FAIL));
											 * 
											 * }
											 */
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											pass = false;
										}

									}

									// Total Logic from finalCheckMap;
									for (Map.Entry<String, List<String>> entr : finalCheckMap.entrySet()) {
										List<String> ast = entr.getValue();
										int x = ast.size() / 6;
										int fCount = 0;
										int plus = 0;
										for (int abc = 0; abc < x; abc++) {
											fCount = fCount + Integer.parseInt(ast.get(plus + 1));
											plus = (abc + 1) * 6;
										}
										ast.set(0, fCount + "");
									}
									// End
									List<String> toothDone = new ArrayList<>();
									Map<String, List<String>> finalCheckMap__1 = new HashMap<>();

									for (Map.Entry<String, List<String>> entr : finalCheckMap.entrySet()) {

										String tooth_code = entr.getKey();
										String TC[] = tooth_code.split("----");
										if (toothDone.contains(TC[0]))
											break;
										for (Map.Entry<String, List<String>> entr2 : finalCheckMap.entrySet()) {
											if (entr.getKey().equals(entr2.getKey()))
												break;
											String TC1[] = tooth_code.split("----");
											if (TC[0].equals(TC1[0])) {// Same tooth but different CODES ..Only
																		// Possibility
												List<String> ohh = entr.getValue();
												List<String> ohh1 = entr2.getValue();
												ohh.set(0,
														(Integer.parseInt(ohh1.get(0)) + Integer.parseInt(ohh.get(0)))
																+ "");
												ohh.addAll(ohh1);
												// finalCheckMap__1.put(key, value);
											} else {
												// finalCheckMap__1.put();
											}
										}
										toothDone.add(TC[0]);
									}
									// Alike Code Check

									// here String[] 0 = Count, 1=Dos, 2=Field Name
									// List<String> D1206=finalCheckMap.get("D1206");
									// List<String> D1208=finalCheckMap.get("D1208");
									//
									// for (Map.Entry<String,List<String>> entr : finalCheckMap.entrySet()) {
									// String entr.getValue();
									// }
									//
									// if (D1206!=null && D1208!=null) {
									// List<String> alike1=new ArrayList<>();
									// alike1.add( (Integer.parseInt(D1206.get(0))+ Integer.parseInt(D1208.get(0))
									// )+"" );
									// alike1.add(D1206.get(1)+","+D1208.get(1));
									// alike1.add(D1206.get(2)+","+D1208.get(2));
									// finalCheckMap.put("Alike-D1206-D1208", alike1);
									// finalCheckMap.remove("D1206");
									// finalCheckMap.remove("D1208");
									//
									// }

								}

								/*
								 * try { Long days = DateUtils.daysBetweenDates(
								 * Constants.SIMPLE_DATE_FORMAT.parse(tt.getHistoryDos()), cdate); for
								 * (Map.Entry<String, String> entrys : mapFl.entrySet()) {
								 * 
								 * String fl = entrys.getValue(); try { int flInt = Integer.parseInt(fl); //
								 * Fields Logic if (flInt >= days) { dList.add(new
								 * TPValidationResponseDto(rule.getId(), rule.getName(),
								 * messageSource.getMessage("rule21.error.message", new Object[] {
								 * tt.getHistoryCode(), tp.getTooth(), tt.getHistoryDos(), entrys.getKey() },
								 * locale), Constants.FAIL));
								 * 
								 * pass = false;
								 * 
								 * } } catch (Exception e) { // number format Ex` pass = false; dList.add(new
								 * TPValidationResponseDto(rule.getId(), rule.getName(),
								 * messageSource.getMessage("rule.error.message", new Object[] { entrys.getKey()
								 * + "-" + fl }, locale), Constants.FAIL));
								 * 
								 * } } // For map =mapFl Fields
								 * 
								 * } catch (ParseException e) { // TODO Auto-generated catch block dList.add(
								 * new TPValidationResponseDto(rule.getId(), rule.getName(),
								 * messageSource.getMessage("rule1.error.message.date", new Object[] {
								 * tt.getHistoryDos() }, locale), Constants.FAIL)); pass = false; // Date format
								 * error; }
								 */
								// same tooth Found

							} // ttt for LOOP
						}

					}
					// break;// Because only one row needed to checked
					// }
				}
			}
		}

		if (pass)
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		return dList;

	}

	/*
	 * private Mappings getMappingFromList(List<Mappings> map, String code) {
	 * Mappings r = null; System.out.println("codecode---" + code);
	 * Collection<Mappings> ruleGen = Collections2.filter(map, rule ->
	 * rule.getAdaCodes().getCode().equals(code)); for (Mappings rule : ruleGen) { r
	 * = rule; } // Debug me for nulll return r; }
	 */
	private Mappings getMappingFromListWaiting(List<Mappings> map, String code) {
		Mappings r = null;
		System.out.println("codecode---" + code);
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> (!rule.getServiceCodeCategory().getName().equalsIgnoreCase("No")
						&& !rule.getServiceCodeCategory().getName().equalsIgnoreCase("NA")
						&& rule.getAdaCodes().getCode().equals(code)));
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for nulll
		return r;
	}

	private Mappings getMappingFromListAdditionalInformationNeeded(List<Mappings> map, String code) {
		Mappings r = null;
		System.out.println("codecode---" + code);
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> (!rule.getAdditionalInformationNeeded().equalsIgnoreCase("No")
						&& !rule.getAdditionalInformationNeeded().equalsIgnoreCase("NA")
						&& rule.getAdaCodes().getCode().equals(code)));
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for nulll
		return r;
	}

	private Mappings getMappingFromListPreAuth(List<Mappings> map, String code) {
		Mappings r = null;
		System.out.println("codecode---" + code);
		Collection<Mappings> ruleGen = Collections2.filter(map, rule -> (!rule.getPreAuthNeeded().equalsIgnoreCase("No")
				&& !rule.getPreAuthNeeded().equalsIgnoreCase("NA") && rule.getAdaCodes().getCode().equals(code))

		);
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for nulll
		return r;
	}

	private Mappings getMappingFromListForToothFilling(List<Mappings> map, String code) {
		Mappings r = null;
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> (!rule.getFillingToothNoMapping().equalsIgnoreCase("NA")
						&& !rule.getFillingToothNoMapping().equalsIgnoreCase("No")
						&& rule.getAdaCodes().getCode().equals(code)));
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for nulll
		return r;
	}

	private List<Mappings> getMappingDownGradeFromList(List<Mappings> map) {
		List<Mappings> r = new ArrayList<>();
		Collection<Mappings> ruleGen = Collections2.filter(map, rule -> (!rule.getDowngrading().equalsIgnoreCase("NA")
				&& !rule.getDowngrading().equalsIgnoreCase("No")));
		for (Mappings rule : ruleGen) {
			r.add(rule);
		}
		return r;
	}

	private List<Mappings> getMappingFrequencyApplicableFomList(List<Mappings> map) {
		List<Mappings> r = new ArrayList<>();
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> rule.getFreqencyLimitationApplicable().trim().equalsIgnoreCase("yes"));
		for (Mappings rule : ruleGen) {
			r.add(rule);
		}
		return r;
	}

	public List<TPValidationResponseDto> employerNotFound(IVFTableSheet ivf, MessageSource messageSource) {
		List<TPValidationResponseDto> dList = new ArrayList<>();
		dList.add(new TPValidationResponseDto(-1, "no rule",
				messageSource.getMessage("rule.patient.notfound.espatient",
						new Object[] { "Employer not found Patient (" + Constants.errorMessOPen + ivf.getPatientName()
								+ "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
						locale),
				Constants.FAIL));
		return dList;
	}

	public List<TPValidationResponseDto> patientNotFound(IVFTableSheet ivf, MessageSource messageSource) {
		List<TPValidationResponseDto> dList = new ArrayList<>();
		dList.add(new TPValidationResponseDto(-1, "no rule", messageSource.getMessage("rule.patient.notfound.espatient",
				new Object[] { "Patient Details not found in Patient Sheet(" + Constants.errorMessOPen
						+ ivf.getPatientName() + "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
				locale), Constants.FAIL));
		return dList;
	}
}
