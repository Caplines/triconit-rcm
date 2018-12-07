package com.tricon.ruleengine.utils;

import java.io.BufferedWriter;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.tricon.ruleengine.dto.FreqencyDto;
//import com.tricon.ruleengine.dto.HistoryMatcherDto;
import com.tricon.ruleengine.dto.Rule6Dto;
//import com.tricon.ruleengine.dto.ServiceCodeDateDto;
import com.tricon.ruleengine.dto.ServiceCodeIvfTimesFreqFieldDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.EagleSoftEmployerMaster;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
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
	public TPValidationResponseDto Rule1(Object ivfSheet, MessageSource messageSource, Rules rule, boolean onlyIVF,
			List<Object> tpList, BufferedWriter bw) {
		// Date tpDate = null;
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_1,
				Constants.rule_log_debug, bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		Date currentDate = new Date();
		Date ivfPlanTermDate = null;
		boolean proceed = false;
		try {
			String td = ivf.getPlanTermedDate();
			RuleEngineLogger.generateLogs(clazz, "PlanTermedDate --" + td, Constants.rule_log_debug, bw);
			if (td != null && (!td.trim().equals("") && !td.trim().equalsIgnoreCase("NA"))) {
				/*
				 * RuleEngineLogger.generateLogs(clazz, "Exit Engine ",
				 * Constants.rule_log_debug, bw); return new
				 * TPValidationResponseDto(rule.getId(), rule.getName(),
				 * messageSource.getMessage("rule1.error.message.exitE", null, locale),
				 * Constants.EXTI_ENGINE);
				 */

				ivfPlanTermDate = Constants.SIMPLE_DATE_FORMAT_IVF.parse(td);

				if (ivfPlanTermDate != null && ivfPlanTermDate.compareTo(currentDate) <= 0) {
					proceed = true;
				} else {

					// Exit Engine
					RuleEngineLogger.generateLogs(clazz, "Exit Engine ", Constants.rule_log_debug, bw);
					return new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule1.error.message.plantermedDate", new Object[] { td }, locale),
							Constants.EXTI_ENGINE);
				}
			} else {
				proceed = true;
			}
		} catch (Exception ex) {
			return new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule1.error.message.date", new Object[] { ivf.getPlanTermedDate() }, locale), Constants.FAIL);

		}
		try {
			if (proceed) {
				if (!onlyIVF) {

					if (!onlyIVF && tpList == null) {
						return new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose,
								Constants.FAIL);
					}
					if (tpList != null) {
						for (Object obj : tpList) {
							TreatmentPlan tp = (TreatmentPlan) obj;
							if (!ivf.getPatientId().equals(tp.getPatient().getId())) {
								return new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule1.error.message.patientId",
												new Object[] { ivf.getPatientId(), tp.getPatient().getId() }, locale),
										Constants.FAIL);

							}
						}
					}
				}
				Date ivfDate = null;

				ivfDate = Constants.SIMPLE_DATE_FORMAT_IVF.parse(ivf.getPlanEffectiveDate());
				RuleEngineLogger.generateLogs(clazz, "Current Date:" + currentDate + "- PlanEffective Date:" + ivfDate,
						Constants.rule_log_debug, bw);
				if (ivfDate != null && DateUtils.compareDates(currentDate, ivfDate)) {
					// pass --RULE_ID_1

					return new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS);

				} else {

					return new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule1.error.message", new Object[] {
									Constants.SIMPLE_DATE_FORMAT_IVF.format(currentDate), ivf.getPlanEffectiveDate() },
									locale),
							Constants.FAIL);
				}
			}
		} catch (Exception ex) {
			return new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule1.error.message.date", new Object[] { ivf.getPlanEffectiveDate() }, locale), Constants.FAIL);

		}
		return new TPValidationResponseDto(rule.getId(), rule.getName(),
				messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS);

	}

	// Compare Coverage Book, Fee Schedule and Fee in IV and Eaglesoft - B -- Normal
	// Mode
	public List<TPValidationResponseDto> Rule4_B(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings, List<EagleSoftFeeShedule> esfeess, List<EagleSoftPatient> espatients,
			BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_4,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {
			if (tpList == null) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return dList;
			}
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			boolean pass = true;

			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				if (!tp.getProviderLastName().trim().equalsIgnoreCase(ivf.getProviderName())) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
							messageSource.getMessage("rule4.error.message_5",
									new Object[] { ivf.getProviderName(), tp.getProviderLastName(), }, locale),
							Constants.FAIL));

					pass = false;
					break;// only one needs to be displayed.
				}
				break;// only Read first record .as per new Req (Enhancement).

			}

			if (espatients != null && espatients.get(0) != null) {
				EagleSoftPatient pat = espatients.get(0);

				if (ivf.getPlanCoverageBook() != null && pat.getCovBookHeaderName() != null
						&& (ivf.getPlanCoverageBook().equals("") || ivf.getPlanCoverageBook().equalsIgnoreCase("none"))
						&& (pat.getCovBookHeaderName().equals("")
								|| pat.getCovBookHeaderName().equalsIgnoreCase("none"))) {

					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
							messageSource.getMessage("rule4.error.message_4", new Object[] {}, locale),
							Constants.ALERT));
					// return dList;
				}
				// String esp_ = ivf.getPlanFeeScheduleName();
				RuleEngineLogger.generateLogs(clazz, "Coverage Book-" + ivf.getPlanCoverageBook()
						+ " :: Coverage Book Header Name-" + pat.getCovBookHeaderName(), Constants.rule_log_debug, bw);

				if (ivf.getPlanCoverageBook() != null && pat.getCovBookHeaderName() != null
						&& !ivf.getPlanCoverageBook().trim().equalsIgnoreCase(pat.getCovBookHeaderName().trim())) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
							messageSource.getMessage("rule4.error.message_1",
									new Object[] { ivf.getPlanCoverageBook(), pat.getCovBookHeaderName() }, locale),
							Constants.FAIL));
					pass = false;
				}
				RuleEngineLogger.generateLogs(
						clazz, "Plan Fee Schedule Name-" + ivf.getPlanFeeScheduleName()
								+ " :: Patient Fee Schedule Name-" + pat.getFeeScheduleName(),
						Constants.rule_log_debug, bw);

				if (ivf.getPlanFeeScheduleName() != null && pat.getFeeScheduleName() != null
						&& !ivf.getPlanFeeScheduleName().trim().equalsIgnoreCase(pat.getFeeScheduleName().trim())) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
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
								RuleEngineLogger.generateLogs(clazz,
										" FS FEE -" + fs.getFeesFee() + " :: Treatment Plan Fee-" + tp.getFee(),
										Constants.rule_log_debug, bw);

								if (Double.parseDouble(fs.getFeesFee())!=Double.parseDouble(tp.getFee())) {
									missing_code.add(tp.getServiceCode());
									missing_name.add(fs.getName());

								}
							}
						} else {
							RuleEngineLogger.generateLogs(clazz,
									" Treatment Plan Service code is mssing in  Fee Schedule-" + tp.getServiceCode(),
									Constants.rule_log_debug, bw);

							// Service Code not found..
							missing_cp_EG.add(tp.getServiceCode());
						}

					}

					if (missing_code.size() > 0 || missing_name.size() > 0 || missing_cp_EG.size() > 0) {
						pass = false;
						if (missing_code.size() > 0 && missing_name.size() > 0) {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
									messageSource.getMessage("rule4.error.message_2", new Object[] {
											String.join(",", missing_code), String.join(",", missing_name) }, locale),
									Constants.FAIL));

						} else {
							dList.add(
									new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule.servicecode.notfound.esFeeSchedule",
													new Object[] { missing_cp_EG.toString() }, locale),
											Constants.FAIL));

						}

					} else {
						// PASS
					}

				}

			} else {
				RuleEngineLogger.generateLogs(clazz, "Patient Details not found in Patient Sheet -"
						+ ivf.getPatientName() + "-" + ivf.getPatientDOB(), Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee", messageSource.getMessage(
						"rule.patient.notfound.espatient",
						new Object[] { "Patient Details not found in Patient Sheet(" + Constants.errorMessOPen
								+ ivf.getPatientName() + "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
						locale), Constants.FAIL));
				pass = false;
			}

			if (pass)
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		} catch (Exception x) {

			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return dList;

	}

	// Compare Coverage Book, Fee Schedule and Fee in IV and Eaglesoft A- BATCH
	public List<TPValidationResponseDto> Rule4_A(IVFTableSheet ivfSheet, MessageSource messageSource, Rules rule,
			List<EagleSoftPatient> espatients, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_4,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			boolean pass = true;
			if (espatients != null && espatients.get(0) != null) {
				EagleSoftPatient pat = espatients.get(0);
				RuleEngineLogger.generateLogs(clazz, "PlanCoverageBook-" + ivf.getPlanCoverageBook(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "CovBookHeaderName-" + pat.getCovBookHeaderName(),
						Constants.rule_log_debug, bw);

				if (ivf.getPlanCoverageBook() != null && pat.getCovBookHeaderName() != null
						&& (ivf.getPlanCoverageBook().equals("") || ivf.getPlanCoverageBook().equalsIgnoreCase("none"))
						&& (pat.getCovBookHeaderName().equals("")
								|| pat.getCovBookHeaderName().equalsIgnoreCase("none"))) {

					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
							messageSource.getMessage("rule4.error.message_4", new Object[] {}, locale),
							Constants.ALERT));
					// return dList;
				}

				// String esp_ = ivf.getPlanFeeScheduleName();
				if (ivf.getPlanCoverageBook() != null && pat.getCovBookHeaderName() != null
						&& !ivf.getPlanCoverageBook().trim().equalsIgnoreCase(pat.getCovBookHeaderName().trim())) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule4.error.message_1",
									new Object[] { ivf.getPlanCoverageBook(), pat.getCovBookHeaderName() }, locale),
							Constants.FAIL));
					pass = false;
				}
				RuleEngineLogger.generateLogs(
						clazz, "Plan Fee Schedule Name-" + ivf.getPlanFeeScheduleName()
								+ " :: Patient Fee Schedule Name-" + pat.getFeeScheduleName(),
						Constants.rule_log_debug, bw);
				if (ivf.getPlanFeeScheduleName() != null && pat.getFeeScheduleName() != null
						&& !ivf.getPlanFeeScheduleName().trim().equalsIgnoreCase((pat.getFeeScheduleName().trim()))) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule4.error.message_3",
									new Object[] { ivf.getPlanFeeScheduleName(), pat.getFeeScheduleName() }, locale),
							Constants.FAIL));
					pass = false;
				} else {
					// same value
				}

			} else {
				RuleEngineLogger.generateLogs(clazz, "Patient Details not found in Patient Sheet -"
						+ ivf.getPatientName() + "-" + ivf.getPatientDOB(), Constants.rule_log_debug, bw);
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
		} catch (Exception x) {

			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return dList;

	}

	// Remaining Deductible, Remaining Balance and Benefit Max as per IV form
	public List<TPValidationResponseDto> Rule5(Object ivfSheet, MessageSource messageSource, Rules rule,
			List<EagleSoftPatient> espatients, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_5,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String  planType= ivf.getPlanType();
		String cMedicate="Children Medicaid";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		
		RuleEngineLogger.generateLogs(clazz,
				"planType-"+planType,
				Constants.rule_log_debug, bw);
		
		if (planType!=null && planType.trim().equalsIgnoreCase(cMedicate)) {
			RuleEngineLogger.generateLogs(clazz,
					" BY pass the rule ",
					Constants.rule_log_debug, bw);
			
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule5.error.message_M", new Object[] {}, locale),
					Constants.NotApplicable));

			return dList;
		}
		try {
			double planAnnualMax = 0;
			double planAnnualMaxRem = 0;
			double planIndDedRem = 0;

			RuleEngineLogger.generateLogs(clazz,
					"Annual Max-" + ivf.getPlanAnnualMax() + "  :: Annual Max Remaining -"
							+ ivf.getPlanAnnualMaxRemaining() + " IndividualDeductible Met-"
							+ ivf.getPlanIndividualDeductibleRemaining(),
					Constants.rule_log_debug, bw);

			try {
				planAnnualMax = Double.parseDouble(ivf.getPlanAnnualMax().replaceAll(",", ""));
				planAnnualMaxRem = Double.parseDouble(ivf.getPlanAnnualMaxRemaining().replaceAll(",", ""));
				planIndDedRem = Double.parseDouble(ivf.getPlanIndividualDeductibleRemaining().replaceAll(",", ""));// Plan_IndividualDeductibleRemaining
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
					primeMaxCov = Double.parseDouble(pat.getMaximumCoverage().replaceAll(",", ""));
					primeBenefitRem = Double.parseDouble(pat.getPrimBenefitsRemaining().replaceAll(",", ""));
					primeRemDed = Double.parseDouble(pat.getPrimRemainingDeductible().replaceAll(",", ""));

					/*
					 * Compare (Plan_AnnualMax with prim_maximum_coverage), (
					 * Plan_AnnualMaxRemaining with prim_benefits_remaining), and
					 * (Plan_IndividualDeductibleRemaining with prim_remaining_deductible)
					 * 
					 */

					RuleEngineLogger.generateLogs(
							clazz, "PrimMaximumCcoverage-" + primeMaxCov + "  :: PrimBenefitsRemaining -"
									+ primeBenefitRem + "  :: PrimRemainingDeductible-" + primeRemDed,
							Constants.rule_log_debug, bw);

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
								messageSource.getMessage("rule5.error.message_R", new Object[] {
										"Plan_IndividualDeductibleRemaining", primeRemDed, planIndDedRem }, locale),
								Constants.FAIL));
						pass = false;

					}

					if (pass)
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule.message.pass", new Object[] {}, locale),
								Constants.PASS));

				} catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz,
							" PrimMaximumCcoverage- " + pat.getMaximumCoverage() + "  :: PrimBenefitsRemaining-"
									+ pat.getPrimBenefitsRemaining() + "  :: PrimRemainingDeductible-"
									+ pat.getPrimRemainingDeductible(),
							Constants.rule_log_debug, bw);
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule.error.message",
									new Object[] { " PrimMaximumCcoverage- " + pat.getMaximumCoverage()
											+ " PrimBenefitsRemaining-" + pat.getPrimBenefitsRemaining()
											+ " PrimRemainingDeductible-" + pat.getPrimRemainingDeductible() },
									locale),
							Constants.FAIL));
					return dList;
				}

			} else {
				RuleEngineLogger.generateLogs(clazz,
						"Patient Details not found in Patient Sheet(" + Constants.errorMessOPen + ivf.getPatientName()
								+ "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")",
						Constants.rule_log_debug, bw);

				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
						"rule5.error.message_no_data",
						new Object[] { "Patient Details not found in Patient Sheet(" + Constants.errorMessOPen
								+ ivf.getPatientName() + "-" + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
						locale), Constants.FAIL));

			}
		} catch (Exception x) {

			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return dList;
	}

	// Percentage Coverage check...
	// -Sub-GingivalIrrigation_D4921_%,
	public List<TPValidationResponseDto> Rule6(Object ivfSheet, MessageSource messageSource, Rules rule,
			List<EagleSoftEmployerMaster> esempmaster, List<EagleSoftPatient> espatients, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_6,
				Constants.rule_log_debug, bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		// String coverageBook = ivf.getPlanCoverageBook();
		// String fs = ivf.getPlanFeeScheduleName();
		boolean pass = true;
		List<TPValidationResponseDto> li = new ArrayList<>();
		try {
			List<Rule6Dto> druleList = new ArrayList<>();
			druleList.add(new Rule6Dto("Preventive_%", ivf.getPreventivePercentage(), "Preventive"));
			druleList.add(new Rule6Dto("Diagnostic_%", ivf.getDiagnosticPercentage(), "Diagnostic"));
			druleList.add(new Rule6Dto("PA_XRays_%", ivf.getpAXRaysPercentage(), "PAs/FMX"));
			druleList.add(new Rule6Dto("Sealants_D1351_%", ivf.getSealantsD1351Percentage(), "Sealants"));
			druleList.add(new Rule6Dto("Basic_%", ivf.getBasicPercentage(), "Basic"));
			druleList.add(new Rule6Dto("Endodontics_%", ivf.getEndodonticsPercentage(), "Endodontics"));
			druleList.add(new Rule6Dto("PerioSurgery_%", ivf.getPerioSurgeryPercentage(), "Periodontal Surgery"));
			druleList.add(new Rule6Dto("Gingivitis_D4346_%", ivf.getGingivitisD4346Percentage(), "Gingivitis TX"));
			// RuleEngineLogger.generateLogs(clazz,"Sub-GingivalIrrigation_D4921_% -- is
			// missing", Constants.rule_log_debug,bw);
			druleList.add(new Rule6Dto("SRP_D4341_%", ivf.getsRPD4341Percentage(), "SRP"));
			// druleList.add(new Rule6Dto("Sub-GingivalIrrigation_D4921_%",
			// "NNNNNNNNNNNNNNNNNNNNNNNNNNNN",
			// "Subgingival Irrigation"));
			druleList.add(new Rule6Dto("PerioMaintenance_D4910_%", ivf.getPerioMaintenanceD4910Percentage(),
					"Perio Maintenance"));
			druleList.add(new Rule6Dto("FMD_D4355_%", ivf.getFMDD4355Percentage(), "Full Mouth Debridement"));
			druleList
					.add(new Rule6Dto("CrownLength_D4249_%", ivf.getCrownLengthD4249Percentage(), "Crown Lengthening"));
			druleList.add(new Rule6Dto("PostComposites_D2391_%", ivf.getPostCompositesD2391Percentage(),
					"Posterior Composites"));
			druleList.add(new Rule6Dto("IVSedation_D9248_%", ivf.getiVSedationD9248Percentage(), "Sedation 9248"));// Sedation
																													// 9248
			druleList.add(new Rule6Dto("IVSedation_D9243_%", ivf.getiVSedationD9243Percentage(), "Sedation 9243"));
			druleList.add(new Rule6Dto("Nitrous_D9230_%", ivf.getNitrousD9230Percentage(), "Nitrous"));
			druleList.add(new Rule6Dto("Ortho_%", ivf.getOrthoPercentage(), "Orthodontics"));
			druleList.add(new Rule6Dto("Major_%", ivf.getMajorPercentage(), "Major"));
			druleList
					.add(new Rule6Dto("Extractions_Minor_%", ivf.getExtractionsMinorPercentage(), "Minor Extractions"));
			druleList
					.add(new Rule6Dto("Extractions_Major_%", ivf.getExtractionsMajorPercentage(), "Major Extractions"));
			druleList.add(new Rule6Dto("ImplantCoverage_D6010_%", ivf.getImplantCoverageD6010Percentage(), "Implants"));
			druleList.add(new Rule6Dto("ImplantCoverage_D6057_%", ivf.getImplantCoverageD6057Percentage(), "Implant Abutment"));
			druleList.add(new Rule6Dto("ImplantCoverage_D6190_%", ivf.getImplantCoverageD6190Percentage(), "Implant Index"));
			druleList.add(new Rule6Dto("ImplantSupportedPorcCeramic_D6065_%",
					ivf.getImplantSupportedPorcCeramicD6065Percentage(), "Implant Supported Prosthetics"));
			druleList.add(new Rule6Dto("Crowns_D2750_D2740_%", ivf.getCrownsD2750D2740Percentage(), "Crowns"));
			druleList.add(new Rule6Dto("D9310_%", ivf.getD9310Percentage(), "D9310"));
			List<String> mess = new ArrayList<>();
			//
			boolean namecheck = true;
			if (espatients != null && esempmaster != null && espatients.get(0) != null && esempmaster.get(0) != null) {

				// Enhancement
				EagleSoftPatient ep = espatients.get(0);
				RuleEngineLogger.generateLogs(clazz, "IVF EMP NAME-" + ivf.getEmployerName(), Constants.rule_log_debug,
						bw);

				RuleEngineLogger.generateLogs(clazz, "ES EMP NAME-" + ep.getEmployerName(), Constants.rule_log_debug,
						bw);
				//NOTE IN REPLACE ALL it's not a space ..never remove it
				if (!(ivf.getEmployerName().trim().replaceAll(" ","").equalsIgnoreCase(ep.getEmployerName().trim().replaceAll(" ","")))) {

					pass = false;
					li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule6.error.message_2",
									new Object[] { ep.getEmployerName(), ivf.getEmployerName() }, locale),
							Constants.FAIL));
					namecheck = false;

				}

				//
				if (namecheck) {
					for (Rule6Dto d6 : druleList) {

						Collection<EagleSoftEmployerMaster> ivfESMap2 = Collections2.filter(esempmaster,
								x -> x.getServiceTypeDescription().trim().equalsIgnoreCase(d6.getFsName().trim()));
						if (ivfESMap2 != null && ivfESMap2.size() > 0) {

							for (EagleSoftEmployerMaster y : ivfESMap2) {

								RuleEngineLogger.generateLogs(clazz,
										"Employer -Percentage-" + y.getPercentage() + " ::IVF FROM Name- "
												+ d6.getIvfName() + " ::IVF PERCENTAGE- " + d6.getPercentage().replace("$", ""),
										Constants.rule_log_debug, bw);

								if (y.getPercentage().trim().equalsIgnoreCase(d6.getPercentage().replace("$", "").trim())) {
									// Pass
								} else {
									pass = false;
									mess.add(d6.getFsName()+" in ES = "+Constants.errorMessOPen+y.getPercentage()+Constants.errorMessClose+": "+d6.getIvfName()+" in IV = "+Constants.errorMessOPen + d6.getPercentage().replace("$", "")
									      + Constants.errorMessClose);
									/*
									 * li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									 * messageSource.getMessage("rule6.error.message", new Object[] {
									 * d6.getIvfName() + "(" + d6.getFsName() + ")" }, locale), Constants.FAIL));
									 */
								}
								break;// Only one value will be there
							}

						} else {
							// Name not found in Emp Master
							pass = false;
							RuleEngineLogger.generateLogs(clazz,
									"ES Names in Employer Master not found--" + d6.getFsName() + "-" + d6.getIvfName(),
									Constants.rule_log_debug, bw);

							li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule6.error.message_3",
											new Object[] { d6.getFsName() + "-" + d6.getIvfName() }, locale),
									Constants.FAIL));
						}

					}
				}
			}
			if (!pass && namecheck) {
				li.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
						"rule6.error.message", new Object[] { String.join("<br>", mess) }, locale), Constants.FAIL));
			}
			if (pass) {
				li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS));
			}
		} catch (Exception x) {

			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return li;

	}

	// Alert
	public List<TPValidationResponseDto> Rule7(Object ivfSheet, MessageSource messageSource, Rules rule,
			BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_7,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> li = new ArrayList<>();
		try {
			RuleEngineLogger.generateLogs(clazz, "Non Dup clause- " + ivf.getPlanNonDuplicateClause(),
					Constants.rule_log_debug, bw);

			if (ivf.getPlanNonDuplicateClause().equalsIgnoreCase("yes")) {
				li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule7.error.message_duplicate", null, locale), Constants.ALERT));
			}
			RuleEngineLogger.generateLogs(clazz, "PlanPreDMandatory - " + ivf.getPlanPreDMandatory(),
					Constants.rule_log_debug, bw);
			if (ivf.getPlanPreDMandatory().equalsIgnoreCase("yes")) {
				li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule7.error.message_preman", null, locale), Constants.ALERT));
			}
			RuleEngineLogger.generateLogs(clazz, "getPlanFullTimeStudentStatus -" + ivf.getPlanFullTimeStudentStatus(),
					Constants.rule_log_debug, bw);
			if (ivf.getPlanFullTimeStudentStatus().equalsIgnoreCase("yes")) {
				li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule7.error.message_fulltime", null, locale), Constants.ALERT));
			}
			RuleEngineLogger.generateLogs(clazz, "getPlanAssignmentofBenefits - " + ivf.getPlanAssignmentofBenefits(),
					Constants.rule_log_debug, bw);
			if (ivf.getPlanAssignmentofBenefits().equalsIgnoreCase("yes")) {
				li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule7.error.message_benefit", null, locale), Constants.ALERT));
			}
			if (li.size() == 0)
				li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS));
		} catch (Exception x) {

			li.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return li;

	}

	// Age Limits
	public List<TPValidationResponseDto> Rule8(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_8,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String fl = ivf.getFlourideAgeLimit();

		String var = ivf.getVarnishD1206AgeLimit();
		String sel = ivf.getSealantsD1351AgeLimit();
		String ortho = ivf.getOrthoAgeLimit();
		String dob = ivf.getPatientDOB();
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			int[] age = null;
			boolean pass = true;
			try {
				age = DateUtils.calculateAgeYMD(dob, true);
				RuleEngineLogger.generateLogs(clazz, "Date of Birth-" + dob, Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz,
						"Age- " + age[0] + " Years, " + age[1] + " Months & " + age[2] + " Days",
						Constants.rule_log_debug, bw);
			} catch (ParseException e) {
				RuleEngineLogger.generateLogs(clazz, "Date of Birth-" + dob, Constants.rule_log_debug, bw);
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
			boolean d1208 = false;
			boolean d1206 = false;
			boolean d1351 = false;
			boolean rest = false;

			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				if (tp.getServiceCode().equals("D1208")) {
					if (!d1208) {
						try {
							int f = Integer.parseInt(fl);
							RuleEngineLogger.generateLogs(clazz, "Age -" + dob + " ,FlourideAgeLimit-" + f,
									Constants.rule_log_debug, bw);
							if (!DateUtils.checkForAgeLimit(age, f)) {
								d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource
												.getMessage("rule8.error.message",
														new Object[] { tp.getServiceCode(), age[0] + " Years, " + age[1]
																+ " Months & " + age[2] + " Days", f },
														locale),
										Constants.FAIL));
								pass = false;
							}
						} catch (NumberFormatException e) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									"rule1.error.message.numberformat", new Object[] { fl }, locale), Constants.FAIL));
							pass = false;

						}
					}
					d1208 = true;
				} else if (tp.getServiceCode().equals("D1206")) {
					if (!d1206) {
						try {
							int v = Integer.parseInt(var);
							RuleEngineLogger.generateLogs(clazz, "Age -" + dob + " ,VarnishD1206AgeLimit()-" + v,
									Constants.rule_log_debug, bw);
							if (!DateUtils.checkForAgeLimit(age, v)) {
								d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource
												.getMessage("rule8.error.message",
														new Object[] { tp.getServiceCode(), age[0] + " Years, " + age[1]
																+ " Months & " + age[2] + " Days", v },
														locale),
										Constants.FAIL));
								pass = false;
							}
						} catch (NumberFormatException e) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									"rule1.error.message.numberformat", new Object[] { var }, locale), Constants.FAIL));
							pass = false;

						}
					}
					d1206 = true;
				} else if (tp.getServiceCode().equals("D1351")) {
					if (!d1351) {
						try {
							int s = Integer.parseInt(sel);
							RuleEngineLogger.generateLogs(clazz, "Age -" + dob + " ,SealantsD1351AgeLimit-" + s,
									Constants.rule_log_debug, bw);
							if (!DateUtils.checkForAgeLimit(age, s)) {
								d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource
												.getMessage("rule8.error.message",
														new Object[] { tp.getServiceCode(), age[0] + " Years, " + age[1]
																+ " Months & " + age[2] + " Days", s },
														locale),
										Constants.FAIL));
								pass = false;
							}
						} catch (NumberFormatException e) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
									"rule1.error.message.numberformat", new Object[] { sel }, locale), Constants.FAIL));
							pass = false;

						}
					}
					d1351 = true;
				} else if (tp.getServiceCode().equals("D8010") || tp.getServiceCode().equals("D8020")
						|| tp.getServiceCode().equals("D8030") || tp.getServiceCode().equals("D8040")
						|| tp.getServiceCode().equals("D8050") || tp.getServiceCode().equals("D8060")
						|| tp.getServiceCode().equals("D8070") || tp.getServiceCode().equals("D8080")
						|| tp.getServiceCode().equals("D8090")) {
					if (!rest) {
						try {
							int o = Integer.parseInt(ortho);
							RuleEngineLogger.generateLogs(clazz, "Age -" + dob + " ,OrthoAgeLimit-" + o,
									Constants.rule_log_debug, bw);
							if (!DateUtils.checkForAgeLimit(age, o)) {
								d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource
												.getMessage("rule8.error.message",
														new Object[] { tp.getServiceCode(), age[0] + " Years, " + age[1]
																+ " Months & " + age[2] + " Days", o },
														locale),
										Constants.FAIL));
								pass = false;
							}
						} catch (NumberFormatException e) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
									.getMessage("rule1.error.message.numberformat", new Object[] { ortho }, locale),
									Constants.FAIL));
							pass = false;

						}
					}
					rest = true;
				}
				// }
			} // For LOOP end
			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Sealants
	public List<TPValidationResponseDto> Rule14(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_14,
				Constants.rule_log_debug, bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		//String dob = ivf.getPatientDOB();
		// String sealantPer=ivf.getSealantsD1351Percentage();
		//String sealantAL = ivf.getSealantsD1351AgeLimit();
		String primaryMolar = ivf.getSealantsD1351PrimaryMolarsCovered();
		String preMolar = ivf.getSealantsD1351PreMolarsCovered();
		String premanentMolar = ivf.getSealantsD1351PermanentMolarsCovered();
		// String insName = ivf.getInsName();
		List<TPValidationResponseDto> d = new ArrayList<>();
		boolean sealantPresent = false;
		// First Check For Sealant
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		for (Object obj : tpList) {
			TreatmentPlan tp = (TreatmentPlan) obj;
			// IF Any more Sealants are there plz add in OR Conditions
			if (tp.getServiceCode().equalsIgnoreCase("D1351")) {
				sealantPresent = true;
			}
		}

		RuleEngineLogger.generateLogs(clazz, "sealantPresent-" + sealantPresent, Constants.rule_log_debug, bw);

		if (!sealantPresent) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
			return d;
		}
		try {
			boolean pass = true;
			/*
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			*/
			/*
			 * int age[] = null; try { age = DateUtils.calculateAgeYMD(dob, true); } catch
			 * (ParseException e) { d.add(new TPValidationResponseDto(rule.getId(),
			 * rule.getName(), messageSource.getMessage("rule1.error.message.date", new
			 * Object[] { dob }, locale), Constants.FAIL)); return d; } try {
			 * Integer.parseInt(sealantAL); } catch (NumberFormatException e) { d.add(new
			 * TPValidationResponseDto(rule.getId(), rule.getName(),
			 * messageSource.getMessage("rule.error.message", new Object[] { sealantAL },
			 * locale), Constants.FAIL)); return d; } RuleEngineLogger.generateLogs(clazz,
			 * "getSealantsD1351AgeLimit -" + sealantAL, Constants.rule_log_debug, bw);
			 * RuleEngineLogger.generateLogs(clazz, "Age  -" + age[0] + " Years, "+
			 * age[1]+" Months & " + age[2]+ " Days", Constants.rule_log_debug, bw);
			 * RuleEngineLogger.generateLogs(clazz, " is age <= sealantAL  -" +
			 * DateUtils.checkForAgeLimit(age, Integer.parseInt(sealantAL)),
			 * Constants.rule_log_debug, bw);
			 */

			// As per requirement hard Code these
			// if (DateUtils.checkForAgeLimit(age, Integer.parseInt(sealantAL))) { // { if
			// (age <=
			// Integer.parseInt(sealantAL)) {
			// date logic removed after chat -27 Aug
			if (true) {
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
					// IF Any more Sealants are there plz add in OR Conditions
					if (tp.getServiceCode().equalsIgnoreCase("D1351")) {

						String tooths[] = ToothUtil.getToothsFromTooth(tp.getTooth());
						for (String tooth : tooths) {
							RuleEngineLogger.generateLogs(clazz, "primaryMolar-" + primaryMolar + " -Tooth-" + tooth,
									Constants.rule_log_debug, bw);

							if (!primaryMolar.trim().equalsIgnoreCase("yes")) {
								Collection<String> prit = Collections2.filter(primaryMolarTCList,
										th -> th.equals(tooth));
								for (String x : prit) {
									primaryMolarT.add(x);
								}
							}
							RuleEngineLogger.generateLogs(clazz,
									"premanentMolar-" + premanentMolar + " -Tooth-" + tooth, Constants.rule_log_debug,
									bw);

							if (!premanentMolar.trim().equalsIgnoreCase("yes")) {
								Collection<String> permat = Collections2.filter(permanentMolarTCList,
										th -> th.equals(tooth));
								for (String x : permat) {
									permamentMolarT.add(x);
								}
							}
							RuleEngineLogger.generateLogs(clazz, "preMolar-" + preMolar + " -Tooth-" + tooth,
									Constants.rule_log_debug, bw);
							if (!preMolar.trim().equalsIgnoreCase("yes")) {
								Collection<String> perm = Collections2.filter(preMolarCList, th -> th.equals(tooth));
								for (String x : perm) {
									preMolarT.add(x);
								}
							}

						} // For loop end= Tooth
					}
				} // For LOOP end
				if (primaryMolarT.size() > 0) {
					// String.join(", ", primaryMolarT)
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule14.error.message1",
									new Object[] { " Primary Molar (Tooth # " + String.join(",", primaryMolarT) + ")" },
									locale),
							Constants.FAIL));

					pass = false;
				}
				if (permamentMolarT.size() > 0) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule14.error.message1",
									new Object[] {
											" Permanent Molar (Tooth # " + String.join(",", permamentMolarT) + ")" },
									locale),
							Constants.FAIL));
					pass = false;
				}
				if (preMolarT.size() > 0) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule14.error.message1",
									new Object[] { " Pre-Molar (Tooth  # " + String.join(",", preMolarT) + ")" },
									locale),
							Constants.FAIL));
					pass = false;
				}
			} // if end for age limit
			/*
			 * else {
			 * 
			 * pass = false; d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
			 * messageSource .getMessage("rule14.error.message", new Object[] { "Age(" +
			 * age[0] + " Years, "+ age[1]+" Months & " + age[2]+ " Days " +
			 * "- sealant Age " + sealantAL }, locale), Constants.FAIL));
			 * 
			 * }
			 */
			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// SRP Quads Per Day
	public List<TPValidationResponseDto> Rule15(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_15,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String srpperday = ivf.getsRPD4341QuadsPerDay();
		String datybetweenTr = ivf.getsRPD4341DaysBwTreatment();
		if (srpperday != null && (srpperday.trim().equalsIgnoreCase("no") || srpperday.trim().equalsIgnoreCase("")))
			srpperday = "0";
		if (datybetweenTr != null
				&& (datybetweenTr.trim().equalsIgnoreCase("no") || datybetweenTr.trim().equalsIgnoreCase("")))
			datybetweenTr = "0";
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
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
						messageSource.getMessage("rule.error.message", new Object[] { datybetweenTr }, locale),
						Constants.FAIL));
				return d;
			}

			RuleEngineLogger.generateLogs(clazz, "getsRPD4341QuadsPerDay--" + ivf.getsRPD4341QuadsPerDay(),
					Constants.rule_log_debug, bw);
			RuleEngineLogger.generateLogs(clazz, "getsRPD4341DaysBwTreatment--" + ivf.getsRPD4341DaysBwTreatment(),
					Constants.rule_log_debug, bw);

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
			RuleEngineLogger.generateLogs(clazz, "D4341-D4342 Total combined size-" + size, Constants.rule_log_debug,
					bw);

			if (size > Integer.parseInt(srpperday)) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
						.getMessage("rule15.error.message3", new Object[] { srpperday, size, datybetweenTr }, locale),
						Constants.ALERT));
				pass = false;
			}

			// No. of Days Check
			/*
			 * Not neede Now. RuleEngineLogger.generateLogs(clazz, "datybetweenTr - " +
			 * datybetweenTr, Constants.rule_log_debug, bw); if
			 * (Integer.parseInt(datybetweenTr) > 0 && size > 0) { pass = false; d.add(new
			 * TPValidationResponseDto(rule.getId(), rule.getName(),
			 * messageSource.getMessage("rule15.error.message2", new Object[] {
			 * datybetweenTr }, locale), Constants.FAIL)); }
			 */
			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Bundling - X-Rays
	public List<TPValidationResponseDto> Rule16(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_16,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String xray = ivf.getxRaysBundling();
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			boolean pass = true;
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			RuleEngineLogger.generateLogs(clazz, "getxRaysBundling--" + xray, Constants.rule_log_debug, bw);
			if (xray != null && xray.trim().equalsIgnoreCase("yes")) {
				List<String> xrayCodeList = new ArrayList<>();
				xrayCodeList.add("D0220");
				xrayCodeList.add("D0230");
				xrayCodeList.add("D0272");
				xrayCodeList.add("D0274");
				xrayCodeList.add("D0330");
				String mandatoryXrayCode = "D0330";

				List<String> paxrayCodeList = new ArrayList<>();
				paxrayCodeList.add("D0220");
				paxrayCodeList.add("D0230");

				List<String> finalXrayCodeList = new ArrayList<>();
				List<String> finalpaXrayCodeList = new ArrayList<>();

				int sizeXray = 0;
				int sizePAXray = 0;

				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					// X-Rays Code
					Collection<String> prit = Collections2.filter(xrayCodeList, cd -> cd.equals(tp.getServiceCode()));
					if (prit != null && prit.size() > 0) {
						sizeXray = sizeXray + prit.size();
						if (!finalXrayCodeList.contains(tp.getServiceCode()))
							finalXrayCodeList.add(tp.getServiceCode());
					}
					// 9+ PA into FMX
					Collection<String> prita = Collections2.filter(paxrayCodeList,
							cd -> cd.equals(tp.getServiceCode()));
					if (prita != null && prita.size() > 0) {
						sizePAXray = sizePAXray + prita.size();
						finalpaXrayCodeList.add(tp.getServiceCode());

					}
				} // For LOOP end

				RuleEngineLogger.generateLogs(clazz, "finalXrayCodeList-" + finalXrayCodeList.size(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "finalXrayCodeList-" + String.join(",", finalXrayCodeList),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "finalXrayCodeList Mandatory Code -" + mandatoryXrayCode + " is "
						+ finalXrayCodeList.contains(mandatoryXrayCode), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "sizePAXray size-" + sizePAXray, Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "finalpaXrayCodeList-" + String.join(",", finalpaXrayCodeList),
						Constants.rule_log_debug, bw);

				if (finalXrayCodeList.size() > 1 && finalXrayCodeList.contains(mandatoryXrayCode)) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule16.error.message1", new Object[] {}, locale),
							Constants.FAIL));
					pass = false;
				}
				RuleEngineLogger.generateLogs(clazz, "sizePAXray-" + sizePAXray, Constants.rule_log_debug, bw);
				if (sizePAXray > 9) {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule16.error.message2", new Object[] {}, locale),
							Constants.FAIL));
					pass = false;
				}
			}

			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Bundling - Fillings -
	public List<TPValidationResponseDto> Rule17(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_17,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String fillings = ivf.getFillingsBundling();
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			boolean pass = true;
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			RuleEngineLogger.generateLogs(clazz, "fillings-" + fillings, Constants.rule_log_debug, bw);

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

				List<String> fillingCodeList_15 = new ArrayList<>();
				fillingCodeList_15.add("M2391");
				fillingCodeList_15.add("M2391");
				fillingCodeList_15.add("M2391");
				fillingCodeList_15.add("M2391");

				List<String> fillingCodeList_16 = new ArrayList<>();
				fillingCodeList_16.add("M2391");
				fillingCodeList_16.add("M2391");
				fillingCodeList_16.add("M2391");

				List<String> fillingCodeList_17 = new ArrayList<>();
				fillingCodeList_17.add("M2391");
				fillingCodeList_17.add("M2391");

				List<String> fillingCodeList_18 = new ArrayList<>();
				fillingCodeList_18.add("M2391");
				fillingCodeList_18.add("M2392");

				List<String> fillingCodeList_19 = new ArrayList<>();
				fillingCodeList_19.add("M2391");
				fillingCodeList_19.add("M2393");

				List<String> fillingCodeList_20 = new ArrayList<>();
				fillingCodeList_20.add("M2391");
				fillingCodeList_20.add("M2391");
				fillingCodeList_20.add("M2392");

				List<String> fillingCodeList_21 = new ArrayList<>();
				fillingCodeList_21.add("M2392");
				fillingCodeList_21.add("M2392");

				List<String> fillingCodeList_22 = new ArrayList<>();
				fillingCodeList_22.add("P2391");
				fillingCodeList_22.add("P2391");
				fillingCodeList_22.add("P2391");
				fillingCodeList_22.add("P2391");

				List<String> fillingCodeList_23 = new ArrayList<>();
				fillingCodeList_23.add("P2391");
				fillingCodeList_23.add("P2391");

				List<String> fillingCodeList_24 = new ArrayList<>();
				fillingCodeList_24.add("P2391");
				fillingCodeList_24.add("P2392");

				List<String> fillingCodeList_25 = new ArrayList<>();
				fillingCodeList_25.add("P2391");
				fillingCodeList_25.add("P2393");

				List<String> fillingCodeList_26 = new ArrayList<>();
				fillingCodeList_26.add("P2391");
				fillingCodeList_26.add("P2391");
				fillingCodeList_26.add("P2392");

				List<String> fillingCodeList_27 = new ArrayList<>();
				fillingCodeList_27.add("P2392");
				fillingCodeList_27.add("P2392");
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

				fillingCodeList.addAll(fillingCodeList_15);
				fillingCodeList.addAll(fillingCodeList_16);
				fillingCodeList.addAll(fillingCodeList_17);
				fillingCodeList.addAll(fillingCodeList_18);
				fillingCodeList.addAll(fillingCodeList_19);
				fillingCodeList.addAll(fillingCodeList_20);
				fillingCodeList.addAll(fillingCodeList_21);
				fillingCodeList.addAll(fillingCodeList_22);
				fillingCodeList.addAll(fillingCodeList_23);
				fillingCodeList.addAll(fillingCodeList_24);
				fillingCodeList.addAll(fillingCodeList_25);
				fillingCodeList.addAll(fillingCodeList_26);
				fillingCodeList.addAll(fillingCodeList_27);

				// List<String> scCode=new ArrayList<>();
				// Map<String,List<String>> toothMap=new HashMap<>();

				Map<String, List<String>> filligToothMap = null;
				List<String> list = null;
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					// fillingCodes
					Collection<String> prit = Collections2.filter(fillingCodeList,
							cd -> cd.equals(tp.getServiceCode()));

					if (prit != null) {
						RuleEngineLogger.generateLogs(clazz, "ServiceCode Found --" + tp.getServiceCode(),
								Constants.rule_log_debug, bw);

						String[] tooths = ToothUtil.getToothsFromTooth(tp.getTooth());
						for (String tooth : tooths) {
							RuleEngineLogger.generateLogs(clazz, "tooth --" + tooth, Constants.rule_log_debug, bw);
							if (filligToothMap == null)
								filligToothMap = new HashMap<>();
							if (filligToothMap.get(tooth) != null) {
								list = (List<String>) filligToothMap.get(tooth);
								list.add(tp.getServiceCode());
							} else {
								list = new ArrayList<>();
								list.add(tp.getServiceCode());
								filligToothMap.put(tooth, list);

							}

						}
					} else {
						RuleEngineLogger.generateLogs(clazz, "ServiceCode NOT Found --" + tp.getServiceCode(),
								Constants.rule_log_debug, bw);

					}

				} // For LOOP end

				if (filligToothMap != null) {
					List<String> btooths = null;
					/// can be optimized more
					boolean found = false;
					for (Map.Entry<String, List<String>> entry : filligToothMap.entrySet()) {
						found = false;

						List<String> serviceCodes = entry.getValue();
						int countD2330 = Collections.frequency(serviceCodes, "D2330");
						int countD2331 = Collections.frequency(serviceCodes, "D2331");
						int countD2332 = Collections.frequency(serviceCodes, "D2332");
						int countD2391 = Collections.frequency(serviceCodes, "D2391");
						int countD2392 = Collections.frequency(serviceCodes, "D2392");
						int countD2393 = Collections.frequency(serviceCodes, "D2393");

						int countM2391 = Collections.frequency(serviceCodes, "M2391");
						int countM2392 = Collections.frequency(serviceCodes, "M2392");
						int countM2393 = Collections.frequency(serviceCodes, "M2393");
						int countP2391 = Collections.frequency(serviceCodes, "P2391");
						int countP2392 = Collections.frequency(serviceCodes, "P2392");
						int countP2393 = Collections.frequency(serviceCodes, "P2393");

						RuleEngineLogger.generateLogs(clazz, "countD2330--" + countD2330, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countD2331--" + countD2331, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countD2332--" + countD2332, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countD2392--" + countD2392, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countD2391--" + countD2391, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countD2393--" + countD2393, Constants.rule_log_debug, bw);

						RuleEngineLogger.generateLogs(clazz, "countM2391--" + countM2391, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countM2392--" + countM2392, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countM2393--" + countM2393, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countP2391--" + countP2391, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countP2392--" + countP2392, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "countP2391--" + countP2393, Constants.rule_log_debug, bw);
						if (countD2330 >= 4) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2335", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2330 >= 3) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2332", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2330 >= 2) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2331", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2330 >= 1 && countD2331 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2332", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2330 >= 1 && countD2332 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2335", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2330 >= 2 && countD2331 >= 1) {
							found = true;
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2335", Constants.rule_log_debug, bw);

						} else if (countD2331 >= 2) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2335", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2391 >= 4) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2391 >= 3) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2393", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2391 >= 2) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2392", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2391 >= 1 && countD2392 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2393", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2391 >= 1 && countD2393 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2391 >= 2 && countD2392 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countD2392 >= 2) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--D2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countM2391 >= 4) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--M2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countM2391 >= 3) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--M2393", Constants.rule_log_debug, bw);
							found = true;

						} else if (countM2391 >= 2) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--M2392", Constants.rule_log_debug, bw);
							found = true;

						} else if (countM2391 >= 1 && countM2392 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--M2393", Constants.rule_log_debug, bw);
							found = true;

						} else if (countM2391 >= 1 && countM2393 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--M2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countM2391 >= 2 && countM2392 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--M2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countM2392 >= 2) {
							found = true;
							RuleEngineLogger.generateLogs(clazz, "countD2330--M2394", Constants.rule_log_debug, bw);

						} else if (countP2391 >= 4) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--P2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countP2391 >= 3) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--P2393", Constants.rule_log_debug, bw);
							found = true;

						} else if (countP2391 >= 2) {
							found = true;
							RuleEngineLogger.generateLogs(clazz, "countD2330--P2392", Constants.rule_log_debug, bw);

						} else if (countP2391 >= 1 && countP2392 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--P2393", Constants.rule_log_debug, bw);

							found = true;

						} else if (countP2391 >= 1 && countP2393 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--P2394", Constants.rule_log_debug, bw);

							found = true;

						} else if (countP2391 >= 2 && countP2392 >= 1) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--P2394", Constants.rule_log_debug, bw);
							found = true;

						} else if (countP2392 >= 2) {
							RuleEngineLogger.generateLogs(clazz, "countD2330--P2394", Constants.rule_log_debug, bw);
							found = true;
						}

						if (found) {
							if (btooths == null)
								btooths = new ArrayList<>();
							btooths.add(entry.getKey());
						}

					} // For loop outer

					if (btooths != null && btooths.size() > 0) {
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
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Filling Codes based on Tooth No
	public List<TPValidationResponseDto> Rule9(List<Object> tpList, MessageSource messageSource, Rules rule,
			List<Mappings> mappings, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_18,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
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
						RuleEngineLogger.generateLogs(clazz, "Filling Codes Found-" + scode, Constants.rule_log_debug,
								bw);
						for (String tooth : tooths) {
							RuleEngineLogger.generateLogs(clazz, "Tooth-" + tooth, Constants.rule_log_debug, bw);
							boolean toothFound = false;
							String toothsm[] = map.getFillingToothNoMapping().split(",");
							RuleEngineLogger.generateLogs(clazz, "Filling Tooth-" + map.getFillingToothNoMapping(),
									Constants.rule_log_debug, bw);

							List<String> ts = ToothUtil.findCommonTooth(tooth.trim().split(","), toothsm);
							if (ts != null && ts.size() > 0) {
								RuleEngineLogger.generateLogs(clazz, "CommonTooth-" + String.join(",", ts),
										Constants.rule_log_debug, bw);
								toothFound = true;
							} else {
								RuleEngineLogger.generateLogs(clazz, "CommonTooth not found", Constants.rule_log_debug,
										bw);

							}
							/*
							 * for (String toothm : toothsm) { if (toothm.equals(tooth)) { toothFound =
							 * true; } }
							 */
							if (!toothFound) {
								pass = false;
								d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
										.getMessage("rule9.error.message", new Object[] { scode, tooth }, locale),
										Constants.FAIL));
							}
						} // End - for loop from tooth of Treatment Plan
					} else {
						RuleEngineLogger.generateLogs(clazz, "Filling Codes Not Found-" + scode,
								Constants.rule_log_debug, bw);

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
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Pre-Auth
	public List<TPValidationResponseDto> Rule10(List<Object> tpList, MessageSource messageSource, Rules rule,
			List<Mappings> mappings, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_10,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			// int age = 0;
			boolean pass = true;
			RuleEngineLogger.generateLogs(clazz, "Age Logic removed as per Updated Doc", Constants.rule_log_debug, bw);

			/*
			 * Age Logic removed in updated Document try { age =
			 * DateUtils.calculateAge(dob); } catch (ParseException e) { d.add(new
			 * TPValidationResponseDto(rule.getId(), rule.getName(),
			 * messageSource.getMessage("rule1.error.message.date", new Object[] { dob },
			 * locale), Constants.FAIL)); return d; }
			 */
			List<String> combined1 = new ArrayList<>();
			// List<String> combined2 = new ArrayList<>();
			List<String> combined3 = new ArrayList<>();

			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				Mappings mapA = getMappingFromListAdditionalInformationNeeded(mappings, tp.getServiceCode());
				Mappings mapP = getMappingFromListPreAuth(mappings, tp.getServiceCode());

				// 100
				if (mapA != null) {
					RuleEngineLogger.generateLogs(clazz,
							"Addtion Info Needed present" + mapA.getAdditionalInformationNeeded(),
							Constants.rule_log_debug, bw);
					pass = false;
					combined1.add(tp.getServiceCode() + " - " + mapA.getAdditionalInformationNeeded() + "<br>");
					// combined2.add(mapA.getAdditionalInformationNeeded());
					/*
					 * d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					 * messageSource.getMessage("rule10.error.message1", new Object[] {
					 * tp.getServiceCode(), mapA.getAdditionalInformationNeeded() }, locale),
					 * Constants.FAIL));
					 */
				}
				if (mapP != null) {
					RuleEngineLogger.generateLogs(clazz, "PreAuth  present" + mapP.getPreAuthNeeded(),
							Constants.rule_log_debug, bw);
					pass = false;
					combined3.add(tp.getServiceCode());
					/*
					 * d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					 * messageSource.getMessage("rule10.error.message2", new Object[] {
					 * tp.getServiceCode() }, locale), Constants.FAIL));
					 */
				}
			}
			if (!pass) {
				if (combined1.size() > 0)
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
							.getMessage("rule10.error.message1", new Object[] { String.join(" ", combined1) }, locale),
							Constants.ALERT));
				if (combined3.size() > 0)
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
							.getMessage("rule10.error.message2", new Object[] { String.join(" ", combined3) }, locale),
							Constants.ALERT));

			}
			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.NotNeeded));
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Waiting Period Checks
	public List<TPValidationResponseDto> Rule11(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_11,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String eff = ivf.getPlanEffectiveDate();
		String waitb = ivf.getBasicWaitingPeriod();
		String waitm = ivf.getMajorWaitingPeriod();
		RuleEngineLogger.generateLogs(clazz, "getPlanEffectiveDate-" + eff, Constants.rule_log_debug, bw);
		RuleEngineLogger.generateLogs(clazz, "getBasicWaitingPeriod-" + waitb, Constants.rule_log_debug, bw);
		RuleEngineLogger.generateLogs(clazz, "getMajorWaitingPeriod-" + waitm, Constants.rule_log_debug, bw);
		waitb = waitb.toLowerCase().replace("mo", "");
		waitm = waitm.toLowerCase().replace("mo", "");

		Date effD = null;
		Date dos = null;
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			boolean pass = true;
			try {
				DateUtils.CheckForStringInDate(eff);
				effD = Constants.SIMPLE_DATE_FORMAT_IVF.parse(eff);
				if (!waitb.trim().equalsIgnoreCase("no"))
					Integer.parseInt(waitb);
				if (!waitm.trim().equalsIgnoreCase("no"))
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
				RuleEngineLogger.generateLogs(clazz, "Service Code check in Mapping -" + tp.getServiceCode(),
						Constants.rule_log_debug, bw);

				if (map != null && map.getServiceCodeCategory() != null
						&& map.getServiceCodeCategory().getName() != null
						&& (map.getServiceCodeCategory().getName().equalsIgnoreCase("Major")
								|| map.getServiceCodeCategory().getName().equalsIgnoreCase("Basic"))) {
					RuleEngineLogger.generateLogs(clazz, "Category Name:" + map.getServiceCodeCategory().getName(),
							Constants.rule_log_debug, bw);
					int wt = 0;
					if (map.getServiceCodeCategory().getName().equalsIgnoreCase("Major")
							&& waitm.equalsIgnoreCase("no")) {
						continue;
					}
					if (map.getServiceCodeCategory().getName().equalsIgnoreCase("Basic")
							&& waitb.equalsIgnoreCase("no")) {
						continue;
					}
					if (map.getServiceCodeCategory().getName().equalsIgnoreCase("Major")) {
						wt = Integer.parseInt(waitm);
					}
					if (map.getServiceCodeCategory().getName().equalsIgnoreCase("Basic")) {
						wt = Integer.parseInt(waitb);
					}
					RuleEngineLogger.generateLogs(clazz, "DOS " + dos, Constants.rule_log_debug, bw);

					Calendar nextAvailbleDate = new GregorianCalendar();
					nextAvailbleDate.setTime(effD);
					nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR), nextAvailbleDate.get(Calendar.MONTH) + wt,
							nextAvailbleDate.get(Calendar.DATE));
					RuleEngineLogger.generateLogs(clazz, "Next Date Available-" + nextAvailbleDate.getTime(),
							Constants.rule_log_debug, bw);

					RuleEngineLogger.generateLogs(clazz, "WAIT -" + wt, Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "LAST UPDATED DATE-- " + dos, Constants.rule_log_debug, bw);
					Date x = nextAvailbleDate.getTime();
					if (x.compareTo(dos) >= 0) {
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
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Missing Tooth Clause
	public List<TPValidationResponseDto> Rule18(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_18,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			boolean pass = true;
			RuleEngineLogger.generateLogs(clazz, "getMissingToothClause=" + ivf.getMissingToothClause(),
					Constants.rule_log_debug, bw);
			if (ivf.getMissingToothClause() != null && ivf.getMissingToothClause().trim().equalsIgnoreCase("yes")) {
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					Collection<Mappings> mL = Collections2.filter(mappings,
							y -> y.getAdaCodes().getCode().equals((tp).getServiceCode()));
					if (mL == null) {
						RuleEngineLogger.generateLogs(clazz, "Service code not found -" + tp.getServiceCode(),
								Constants.rule_log_debug, bw);
						d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
								.getMessage("rule.servicecode.notfound", new Object[] { tp.getServiceCode() }, locale),
								Constants.FAIL));
						pass = false;
						return d;
					} else {
						for (Mappings m : mL) {
							RuleEngineLogger.generateLogs(
									clazz, "Service code  founnd -" + tp.getServiceCode()
											+ "  MissingToothClauseApplicable-" + m.getMissingToothClauseApplicable(),
									Constants.rule_log_debug, bw);
							if (m.getMissingToothClauseApplicable() != null
									&& m.getMissingToothClauseApplicable().trim().equalsIgnoreCase("yes")) {
								d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule18.error.message",
												new Object[] { tp.getServiceCode() }, locale),
										Constants.ALERT));
								pass = false;
							}
						}

					}

				}
			}
			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale),
						Constants.NotApplicable));
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Build-Ups & Crown Same Day
	public List<TPValidationResponseDto> Rule13(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_13,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String samedayCr = ivf.getBuildUpsD2950SameDayCrown();
		List<TPValidationResponseDto> d = new ArrayList<>();
		if (tpList == null) {
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
			return d;
		}
		boolean pass = true;

		RuleEngineLogger.generateLogs(clazz, "BuildUpsD2950SameDayCrown- " + samedayCr, Constants.rule_log_debug, bw);
		if (samedayCr != null && samedayCr.trim().equalsIgnoreCase("yes")) {
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
					RuleEngineLogger.generateLogs(clazz, "D2950 code found", Constants.rule_log_debug, bw);
					tooth = tp.getTooth();
					RuleEngineLogger.generateLogs(clazz, "Tooth -" + tooth, Constants.rule_log_debug, bw);
					found = true;
				}
				if (tp.getServiceCode().equals("D2740")) {
					RuleEngineLogger.generateLogs(clazz, "D2740 code found", Constants.rule_log_debug, bw);
					toothD2740 = tp.getTooth();
					RuleEngineLogger.generateLogs(clazz, "Tooth -" + toothD2740, Constants.rule_log_debug, bw);
				}
				if (tp.getServiceCode().equals("D2750")) {
					RuleEngineLogger.generateLogs(clazz, "D2750 code found", Constants.rule_log_debug, bw);
					toothD2750 = tp.getTooth();
					RuleEngineLogger.generateLogs(clazz, "Tooth -" + toothD2750, Constants.rule_log_debug, bw);
				}

			} // For end
			if (found && tooth != "" && (toothD2740 != "" || toothD2750 != "")) {
				// Logic here
				if (tooth.trim().equals(toothD2740.trim()) || tooth.trim().equals(toothD2750.trim())) {
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
			Rules rule, List<Mappings> mappings, List<EagleSoftFeeShedule> esfeess, List<EagleSoftPatient> espatients,
			BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_19,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {
			if (tpList == null) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return dList;
			}
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			boolean pass = true;
			RuleEngineLogger.generateLogs(clazz,
					"PosteriorCompositesD2391Downgrade-" + ivf.getPosteriorCompositesD2391Downgrade()
							+ " CrownsD2750D2740Downgrade-" + ivf.getCrownsD2750D2740Downgrade(),
					Constants.rule_log_debug, bw);

			if (ivf.getPosteriorCompositesD2391Downgrade() != null && ivf.getCrownsD2750D2740Downgrade() != null
					&& (ivf.getPosteriorCompositesD2391Downgrade().trim().equalsIgnoreCase("yes")
							|| ivf.getCrownsD2750D2740Downgrade().trim().equalsIgnoreCase("yes"))) {
				// If Yes then pull all the applicable codes and tooth numbers using the
				// Downgrading Mapping tables...
				List<Mappings> mappsP = null;
				List<Mappings> mappsC = null;
				if (ivf.getPosteriorCompositesD2391Downgrade().trim().equalsIgnoreCase("yes"))
					mappsP = getMappingDownGradeFromListByType(mappings, "posterior composites");
				if (ivf.getCrownsD2750D2740Downgrade().trim().equalsIgnoreCase("yes"))
					mappsC = getMappingDownGradeFromListByType(mappings, "crowns");
				List<Mappings> mapps = new ArrayList<>();
				if (mappsP != null)
					mapps.addAll(mappsP);
				if (mappsC != null)
					mapps.addAll(mappsC);

				for (Object t : tpList) {
					TreatmentPlan tp = (TreatmentPlan) t;
					Collection<Mappings> mL = Collections2.filter(mapps,
							y -> y.getAdaCodes().getCode().equals((tp.getServiceCode())));
					/*
					 * if (ivf.getPosteriorCompositesD2391Downgrade().trim().equalsIgnoreCase("yes")
					 * && !( tp.getServiceCode().equals("D2391") ||
					 * tp.getServiceCode().equals("D2392") || tp.getServiceCode().equals("D2393") ||
					 * tp.getServiceCode().equals("D2394") ) ) { continue; } if
					 * (ivf.getCrownsD2750D2740Downgrade().trim().equalsIgnoreCase("yes") && (
					 * tp.getServiceCode().equals("D2740") || tp.getServiceCode().equals("D2750") ||
					 * tp.getServiceCode().equals("D2751") || tp.getServiceCode().equals("D2751") )
					 * ) { continue; }
					 */
					// Now Downgrading is applicable
					if (mL != null && mL.size() > 0) {

						for (Mappings m : mL) {

							String toothMa[] = ToothUtil.getToothsFromTooth(m.getToothNoForDowngrading());
							String toothTR[] = ToothUtil.getToothsFromTooth(tp.getTooth());
							List<String> th = ToothUtil.findCommonTooth(toothMa, toothTR);
							if (th != null && th.size() > 0) {
								// Tooth Matched
								RuleEngineLogger.generateLogs(clazz, "SERVICE CODE " + tp.getServiceCode(),
										Constants.rule_log_debug, bw);
								RuleEngineLogger.generateLogs(clazz, "CommonTooth-" + String.join(",", th),
										Constants.rule_log_debug, bw);
								RuleEngineLogger.generateLogs(clazz, "DownGrading -" + m.getDowngrading(),
										Constants.rule_log_debug, bw);
								if (m.getDowngrading() != null && !m.getDowngrading().trim().equals("")) {
									// Go Fee Schedule goto Check Fee
									if (esfeess != null && esfeess.size() > 0) {
										Collection<EagleSoftFeeShedule> ruleGen = Collections2.filter(esfeess,
												name -> name.getFeesServiceCode()
														.indexOf(m.getDowngrading().trim()) >= 0);
										if (ruleGen != null) {
											for (EagleSoftFeeShedule fs : ruleGen) {
												RuleEngineLogger.generateLogs(clazz, "TP Fee -" + tp.getFee(),
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "ES  Fee -" + fs.getFeesFee(),
														Constants.rule_log_debug, bw);

												RuleEngineLogger.generateLogs(clazz,
														"EstInsurance -" + tp.getEstInsurance(),
														Constants.rule_log_debug, bw);
												Double estins = Double.parseDouble(tp.getEstInsurance());
												Double fcal = DowngradingPercentUtil.getIVFColumnforServiceCode(ivf,
														tp.getServiceCode(), fs.getFeesFee(), bw);
												RuleEngineLogger.generateLogs(clazz, "Down Graded Ins -" + fcal,
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "estins -" + estins,
														Constants.rule_log_debug, bw);
												if (fcal.doubleValue() != estins.doubleValue()) {
													pass = false;
													dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
															messageSource.getMessage("rule19.error.message2",
																	new Object[] { tp.getServiceCode(), fcal,
																			m.getDowngrading() },
																	locale),
															Constants.FAIL));

												} else {
													// Unique case
													/*
													 * dList.add(new TPValidationResponseDto(rule.getId(),
													 * rule.getName(), messageSource.getMessage("rule19.pass.message1",
													 * new Object[] {}, locale), Constants.PASS));
													 */
												}
											}
										}
									} // if end
									else {
										pass = false;
										RuleEngineLogger.generateLogs(clazz,
												"Downgraded Fee Not found in Fee Master for Service code-"
														+ m.getDowngrading(),
												Constants.rule_log_debug, bw);
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
		} catch (Exception x) {

			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return dList;

	}

	// Frequency Limitations
	/**
	 * @param tpList
	 * @param ivfSheet
	 * @param messageSource
	 * @param rule
	 * @param mappings
	 * @param bw
	 * @return
	 */
	public List<TPValidationResponseDto> Rule21(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, List<Mappings> mappings, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_21,
				Constants.rule_log_debug, bw);
		Set<String> failedCodeSet = new HashSet<>();
		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {
			if (tpList == null) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return dList;
			}
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> mapFlIVF = new HashMap<>();
			ServiceCodeIvfTimesFreqFieldDto scivftff = null;
			List<ServiceCodeIvfTimesFreqFieldDto> dL = null;
			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1208", "Flouride_D1208_FL", ivf.getFlourideD1208FL(), 0,
					0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1208", dL);
			// mapFlIVF.put("D1208", new String[] { "FlourideD1208FL",
			// ivf.getFlourideD1208FL() });// 1 Flouride_D1208_FL

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1206", "Varnish_D1206_FL", ivf.getVarnishD1206FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1206", dL);
			// mapFlIVF.put("D1206", new String[] { "Varnish_D1206_FL",
			// ivf.getVarnishD1206FL() });// 2

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1110", "ProphyD1110_FL", ivf.getProphyD1110FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1110", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1120", "ProphyD1120_FL", ivf.getProphyD1120FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1120", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0270", "XRaysBWS_FL", ivf.getxRaysBWSFL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0270", dL);
			// mapFlIVF.put("D0270", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 3

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0272", "getxRaysBWSFL", ivf.getxRaysBWSFL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0272", dL);
			// mapFlIVF.put("D0272", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 4

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0273", "XRaysBWS_FL", ivf.getxRaysBWSFL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0273", dL);
			// mapFlIVF.put("D0273", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 5

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0274", "XRaysBWS_FL", ivf.getxRaysBWSFL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0274", dL);
			// mapFlIVF.put("D0274", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 6

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0220", "XRaysPA_D0220_FL", ivf.getxRaysPAD0220FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0220", dL);
			// mapFlIVF.put("D0220", new String[] { "XRaysPA_D0220_FL",
			// ivf.getxRaysPAD0220FL() });// 7

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0230", "XRaysPA_D0230_FL", ivf.getxRaysPAD0230FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0230", dL);
			// mapFlIVF.put("D0230", new String[] { "XRaysPA_D0230_FL",
			// ivf.getxRaysPAD0230FL() });// 8

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0210", "XRaysFMX_FL", ivf.getxRaysFMXFL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0210", dL);
			// mapFlIVF.put("D0210", new String[] { "XRaysFMX_FL", ivf.getxRaysFMXFL() });//
			// 9

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0150", "ExamsD0150_FL", ivf.getExamsD0150FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0150", dL);
			// mapFlIVF.put("D0150", new String[] { "ExamsD0150_FL", ivf.getExamsD0150FL()
			// });// 10

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0145", "ExamsD0145_FL", ivf.geteExamsD0145FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0145", dL);
			// mapFlIVF.put("D0145", new String[] { "ExamsD0145_FL", ivf.geteExamsD0145FL()
			// });// 11

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0140", "ExamsD0140_FL", ivf.getExamsD0140FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0140", dL);
			// mapFlIVF.put("D0140", new String[] { "ExamsD0140_FL", ivf.getExamsD0140FL()
			// });// 12

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0120", "ExamsD0120_FL", ivf.getExamsD0120FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0120", dL);
			// mapFlIVF.put("D0120", new String[] { "ExamsD0120_FL", ivf.getExamsD0120FL()
			// });// 13

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2930", "SSC_D2930_FL", ivf.getsSCD2930FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2930", dL);
			// mapFlIVF.put("D2930", new String[] { "SSC_D2930_FL", ivf.getsSCD2930FL()
			// });// 14

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2931", "SSC_D2931_FL", ivf.getsSCD2931FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2931", dL);
			// mapFlIVF.put("D2931", new String[] { "SSC_D2931_FL", ivf.getsSCD2931FL()
			// });// 15

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1351", "Sealants_D1351_FL", ivf.getSealantsD1351FL(), 0,
					0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1351", dL);
			// mapFlIVF.put("D1351", new String[] { "Sealants_D1351_FL",
			// ivf.getSealantsD1351FL() });// 16

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4346", "Gingivitis_D4346_FL", ivf.getGingivitisD4346FL(),
					0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4346", dL);
			// mapFlIVF.put("D4346", new String[] { "Gingivitis_D4346_FL",
			// ivf.getGingivitisD4346FL() });// 17

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4341", "SRP_D4341_FL", ivf.getsRPD4341FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4341", dL);
			// mapFlIVF.put("D4341", new String[] { "SRP_D4341_FL", ivf.getsRPD4341FL()
			// });// 18

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D9940", "NightGuards_D9940_FL", ivf.getNightGuardsD9940FL(),
					0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D9940", dL);
			// mapFlIVF.put("D9940", new String[] { "NightGuards_D9940_FL",
			// ivf.getNightGuardsD9940FL() });// 19

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4910", "PerioMaintenance_D4910_FL",
					ivf.getPerioMaintenanceD4910FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4910", dL);
			// mapFlIVF.put("D4910", new String[] { "PerioMaintenance_D4910_FL",
			// ivf.getPerioMaintenanceD4910FL() });// 20

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4355", "FMD_D4355_FL", ivf.getfMDD4355FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4355", dL);
			// mapFlIVF.put("D4355", new String[] { "FMD_D4355_FL", ivf.getfMDD4355FL()
			// });// 21

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4249", "CrownLength_D4249_FL", ivf.getCrownLengthD4249FL(),
					0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4249", dL);
			// mapFlIVF.put("D4249", new String[] { "CrownLength_D4249_FL",
			// ivf.getCrownLengthD4249FL() });// 22

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2950", "BuildUps_D2950_FL", ivf.getBuildUpsD2950FL(), 0,
					0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2950", dL);
			// mapFlIVF.put("D2950", new String[] { "BuildUps_D2950_FL",
			// ivf.getBuildUpsD2950FL() });// 23

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D7953", "BoneGrafts_D7953_FL", ivf.getBoneGraftsD7953FL(),
					0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D7953", dL);
			// mapFlIVF.put("D7953", new String[] { "BoneGrafts_D7953_FL",
			// ivf.getBoneGraftsD7953FL() });// 24

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D7311", "Alveo_D7311_FL", ivf.getAlveoD7311FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D7311", dL);
			// mapFlIVF.put("D7311", new String[] { "Alveo_D7311_FL", ivf.getAlveoD7311FL()
			// });// 25

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D7310", "Alveo_D7310_FL", ivf.getAlveoD7310FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D7310", dL);
			// mapFlIVF.put("D7310", new String[] { "Alveo_D7310_FL", ivf.getAlveoD7310FL()
			// });// 26

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2750", "Crowns_D2750_D2740_FL",
					ivf.getCrownsD2750D2740FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2750", dL);
			// mapFlIVF.put("D2750", new String[] { "Crowns_D2750_D2740_FL",
			// ivf.getCrownsD2750D2740FL() });// 27

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2740", "Crowns_D2750_D2740_FL",
					ivf.getCrownsD2750D2740FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2740", dL);
			// mapFlIVF.put("D2740", new String[] { "Crowns_D2750_D2740_FL",
			// ivf.getCrownsD2750D2740FL() });// 28

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5110", "CompleteDentures_D5110_D5120_FL",
					ivf.getCompleteDenturesD5110D5120FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5110", dL);
			// mapFlIVF.put("D5110", new String[] { "CompleteDentures_D5110_D5120_FL",
			// ivf.getCompleteDenturesD5110D5120FL() });// 29

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5120", "CompleteDentures_D5110_D5120_FL",
					ivf.getCompleteDenturesD5110D5120FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5120", dL);
			// mapFlIVF.put("D5120", new String[] { "CompleteDentures_D5110_D5120_FL",
			// ivf.getCompleteDenturesD5110D5120FL() });// 30

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5130", "ImmediateDentures_D5130_D5140_FL",
					ivf.getImmediateDenturesD5130D5140FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5130", dL);
			// mapFlIVF.put("D5130", new String[] { "ImmediateDentures_D5130_D5140_FL",
			// ivf.getImmediateDenturesD5130D5140FL() });// 31

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5140", "ImmediateDentures_D5130_D5140_FL",
					ivf.getImmediateDenturesD5130D5140FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5140", dL);
			// mapFlIVF.put("D5140", new String[] { "ImmediateDentures_D5130_D5140_FL",
			// ivf.getImmediateDenturesD5130D5140FL() });// 32

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5213", "PartialDentures_D5213_D5214_FL",
					ivf.getPartialDenturesD5213D5214FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5213", dL);
			// mapFlIVF.put("D5213", new String[] { "PartialDentures_D5213_D5214_FL",
			// ivf.getPartialDenturesD5213D5214FL() });// 33

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5214", "PartialDentures_D5213_D5214_FL",
					ivf.getPartialDenturesD5213D5214FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5214", "InterimPartialDentures_D5214_FL",
					ivf.getInterimPartialDenturesD5214FL(), 0, 0);
			dL.add(scivftff);
			mapFlIVF.put("D5214", dL);
			// mapFlIVF.put("D5214", new String[] { "PartialDentures_D5213_D5214_FL",
			// ivf.getPartialDenturesD5213D5214FL(),
			//
			// "InterimPartialDentures_D5214_FL", ivf.getInterimPartialDenturesD5214FL()
			// });// 34

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D9310", "D9310_FL", ivf.getD9310FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D9310", dL);
			// mapFlIVF.put("D9310", new String[] { "D9310_FL", ivf.getD9310FL() });// 35

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2392", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2392", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2392", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2392", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2392", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2392", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2393", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2393", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2393", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2393", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2393", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2393", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2394", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2394", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2394", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2394", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2394", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0);
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2394", dL);

			RuleEngineLogger.generateLogs(clazz,
					"Create Map with Key as Service Code..and values as Given in the Document ..-mapFlIVF",
					Constants.rule_log_debug, bw);

			boolean pass = true;
			int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
			Date planDate = null;
			try {
				// Calendar calendar = new GregorianCalendar();
				// calendar.setTime(Constants.SIMPLE_DATE_FORMAT.parse(ivf.getPlanEffectiveDate()));
				planDate = Constants.SIMPLE_DATE_FORMAT_IVF.parse(ivf.getPlanEffectiveDate());
				RuleEngineLogger.generateLogs(clazz, "planDate-" + planDate.toString(), Constants.rule_log_debug, bw);

			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				RuleEngineLogger.generateLogs(clazz, "PlanEffectiveDate issue-" + ivf.getPlanEffectiveDate(), Constants.rule_log_debug, bw);

			}
			Map<String, List<ToothHistoryDto>> mapHistoryTooth = new HashMap<>();
			Date TP_Date = null;
			ToothHistoryDto hdto = null;
			boolean historPresent = false;
			int noOFhistory = 60;
			List<ToothHistoryDto> list1 = null;
			// Class<?> c1 =
			// Class.forName("com.tricon.ruleengine.model.sheet.IVFTableSheet");
			Class<?> c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");
			// Object ot = vif;
			IVFHistorySheet hisSheet = ivf.getHs();
			
			RuleEngineLogger.generateLogs(clazz,
					"HISTORTY CODES-- Start",
					Constants.rule_log_debug, bw);
			
			for (int i = 1; i <= noOFhistory; i++) {
				// String hs="getHs";
				// Method hsm = c1.getMethod(hs);
				// hisSheet = (IVFHistorySheet)hsm.invoke(ivf);
				String hc = "getHistory" + i + "Code";
				String hd = "getHistory" + i + "DOS";
				String ht = "getHistory" + i + "Tooth";
				Method hcm = c2.getMethod(hc);
				Method hdm = c2.getMethod(hd);
				Method htm = c2.getMethod(ht);
				// System.out.println((String)hcm.invoke(hisSheet));
				hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
						(String) htm.invoke(hisSheet));

				/*
				 * if (i == 1) hdto = new ToothHistoryDto(ivf.getHistory1Code(),
				 * ivf.getHistory1DOS(), ivf.getHistory1Tooth()); else if (i == 2) hdto = new
				 * ToothHistoryDto(ivf.getHistory2Code(), ivf.getHistory2DOS(),
				 * ivf.getHistory2Tooth()); else if (i == 3) hdto = new
				 * ToothHistoryDto(ivf.getHistory3Code(), ivf.getHistory3DOS(),
				 * ivf.getHistory3Tooth()); else if (i == 4) hdto = new
				 * ToothHistoryDto(ivf.getHistory4Code(), ivf.getHistory4DOS(),
				 * ivf.getHistory4Tooth()); else if (i == 5) hdto = new
				 * ToothHistoryDto(ivf.getHistory5Code(), ivf.getHistory5DOS(),
				 * ivf.getHistory5Tooth()); else if (i == 6) hdto = new
				 * ToothHistoryDto(ivf.getHistory6Code(), ivf.getHistory6DOS(),
				 * ivf.getHistory6Tooth()); else if (i == 7) hdto = new
				 * ToothHistoryDto(ivf.getHistory7Code(), ivf.getHistory7DOS(),
				 * ivf.getHistory7Tooth()); else if (i == 8) hdto = new
				 * ToothHistoryDto(ivf.getHistory8Code(), ivf.getHistory8DOS(),
				 * ivf.getHistory8Tooth()); else if (i == 9) hdto = new
				 * ToothHistoryDto(ivf.getHistory9Code(), ivf.getHistory9DOS(),
				 * ivf.getHistory9Tooth());
				 */
				if (hdto.getHistoryCode() != null && !hdto.getHistoryCode().equalsIgnoreCase("blank")) {
					if (hdto.getHistoryTooth().trim().equals(""))
						hdto.setHistoryTooth("NA");
					for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry : mapFlIVF.entrySet()) {
						if (entry.getKey().equals(hdto.getHistoryCode())) {
							historPresent = true;
							String toothTR[] = ToothUtil.getToothsFromTooth(hdto.getHistoryTooth());
						
							for (String tooth : toothTR) {
								RuleEngineLogger.generateLogs(clazz,
										"HISTORTY CODE -at "+i +" is -"+hdto.getHistoryCode()+" TOOTH - "+tooth,
										Constants.rule_log_debug, bw);		if (mapHistoryTooth.containsKey(tooth)) {
									list1 = mapHistoryTooth.get(tooth);
									list1.add(hdto);
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
			RuleEngineLogger.generateLogs(clazz,
					"HISTORTY CODES-- END",
					Constants.rule_log_debug, bw);
			
			RuleEngineLogger.generateLogs(clazz,
					"Create Map with History Data From IVF Data--- with Key as Totoh Number and Values as History,Serice Code ..",
					Constants.rule_log_debug, bw);

			Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> mapFlIVFFinal = new HashMap<>();// Now key will be tooth

			if (historPresent) {
				RuleEngineLogger.generateLogs(clazz, "History is present  now proceed Further..",
						Constants.rule_log_debug, bw);
				TP_Date = new Date();
				Map<String, List<String>> tpToothMap = null;
				List<String> list = null;
				for (Object t : tpList) {
					TreatmentPlan tp = (TreatmentPlan) t;
					/*
					 * NEED FOR SECOND PHASE.. try { //TP_Date = Constants.SIMPLE_DATE_FORMAT //
					 * .parse(tp.getTreatmentPlanDetails().getDateLastUpdated()); } catch
					 * (ParseException e2) { // TODO Auto-generated catch block
					 * e2.printStackTrace(); }
					 */
					for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry : mapFlIVF.entrySet()) {
						if (entry.getKey().equals(tp.getServiceCode())) {
							RuleEngineLogger
							.generateLogs(clazz,
									"Treatment Plan TOOTH- " + tp.getTooth() +" CODE ="+tp.getServiceCode(),
									Constants.rule_log_debug, bw);
							
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

					} // FoR

				} // for end
				RuleEngineLogger.generateLogs(clazz, "Create a Map with Keys as Tooth Number -tpToothMap - "
						+ "This is created by Using Tooth Number from Treatment Plan, if that tooth No's service code"
						+ "is found in the Fields where we have to Check for Frequncy Limits ..Rest codes are ignored.-tpToothMap "
						+ "", Constants.rule_log_debug, bw);

				if (mapFlIVF != null) {

					for (Map.Entry<String, List<String>> entry : tpToothMap.entrySet()) {
						String tooth = entry.getKey();
						RuleEngineLogger
						.generateLogs(clazz,
								"Tooth From TP- " + tooth ,
								Constants.rule_log_debug, bw);
						List<ToothHistoryDto> hd = mapHistoryTooth.get(tooth);
						if (hd != null && hd.size() > 0) {
							List<String> fromTreatmentPlanCode = entry.getValue();
							for (String tpCode : fromTreatmentPlanCode) {
								for (ToothHistoryDto historyD : hd) {
									RuleEngineLogger
											.generateLogs(clazz,
													"Treatment Plan code- " + tpCode + " History Code-"
															+ historyD.getHistoryCode() + "",
													Constants.rule_log_debug, bw);
									// Check For alike codes
									if (FreqencyUtils.checkforAlikeCodes(tpCode.trim(),
											historyD.getHistoryCode().trim())
											|| tpCode.equalsIgnoreCase(historyD.getHistoryCode().trim())) {
										// if (tpCode.equalsIgnoreCase(historyD.getHistoryCode().trim())) {

										List<ServiceCodeIvfTimesFreqFieldDto> dataIVF = mapFlIVF.get(tpCode);
										boolean present = false;
										for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF) {
											String freq = scivfTFD.getFreqency();

											ServiceCodeIvfTimesFreqFieldDto scivfTFDFinal = new ServiceCodeIvfTimesFreqFieldDto(
													tpCode, scivfTFD.getFieldName(), scivfTFD.getFreqency(), 0, 0);
											scivfTFDFinal.setTooth(tooth);
											scivfTFDFinal.setDos(historyD.getHistoryDos());
											RuleEngineLogger.generateLogs(clazz,
													"HISTORY CODE- " + historyD.getHistoryCode(),
													Constants.rule_log_debug, bw);
											RuleEngineLogger.generateLogs(clazz,
													"HISTORY DOS- " + historyD.getHistoryDos(),
													Constants.rule_log_debug, bw);

											RuleEngineLogger.generateLogs(clazz, "Frequency- " + freq,
													Constants.rule_log_debug, bw);
											if (freq.equalsIgnoreCase("NF") || freq.equalsIgnoreCase("no frequency"))
												continue;
											FreqencyDto FDTO = FreqencyUtils.parseFrequecy(freq);
											Date[] datesFIS = DateUtils.getFiscalYear(FDTO.getFy());
											int ti = FDTO.getTimes();
											scivfTFDFinal.setTimes(ti);
											RuleEngineLogger.generateLogs(clazz, "Tooth Number-" + tooth,
													Constants.rule_log_debug, bw);

											Date dos = null;
											try {
												dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(historyD.getHistoryDos());
												RuleEngineLogger.generateLogs(clazz,
														"History DOS-" + historyD.getHistoryDos(),
														Constants.rule_log_debug, bw);
											} catch (ParseException e2) {
												// TODO Auto-generated catch block
												e2.printStackTrace();
											}
											RuleEngineLogger.generateLogs(clazz, "TIMES:" + ti,
													Constants.rule_log_debug, bw);
											if (FDTO.getFy() > 0) {// Fiscal Year
												// isFiscalpresent=true;
												RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + FDTO.getFy(),
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz,
														"Fiscal Year:" + datesFIS[0] + "-" + datesFIS[1],
														Constants.rule_log_debug, bw);
												boolean fiscal = DateUtils.isDatesBetweenDates(datesFIS[0], datesFIS[1],
														dos);
												RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + fiscal,
														Constants.rule_log_debug, bw);
												if (fiscal) {
													present = true;
													scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
													// expCount = expCount + 1;
												}
											} else if (FDTO.getCy() > 0) {// Calendar Year
												RuleEngineLogger.generateLogs(clazz, "Calendar Year:" + FDTO.getCy(),
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "CurrentYear:" + CurrentYear,
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "CurrentYear:" + CurrentYear,
														Constants.rule_log_debug, bw);
												Date[] calcy = DateUtils.getCalendarYear(FDTO.getCy());
												RuleEngineLogger.generateLogs(clazz, "DATE RANGE: FROM -" + calcy[0],
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "DATE RANGE: TO -" + calcy[1],
														Constants.rule_log_debug, bw);

												// isCalPresent=true;
												// CurrentYear
												if (DateUtils.isDatesBetweenDates(calcy[0], calcy[1], dos)) {
													present = true;
													scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
												}

											} else if (FDTO.getLt() > 0) {// Life Time
												RuleEngineLogger.generateLogs(clazz, "Life Time:" + FDTO.getLt(),
														Constants.rule_log_debug, bw);
												// isLifeTimePresent=true;
												present = true;
												scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);

											} else if (FDTO.getPy() > 0) {// Plan Year
												RuleEngineLogger.generateLogs(clazz, "Plan Year:" + FDTO.getPy(),
														Constants.rule_log_debug, bw);
												// isPlanYearPresent=true;
												Calendar calendar = new GregorianCalendar();
												if (planDate==null) {
													dList.add(
															new TPValidationResponseDto(rule.getId(), rule.getName(),
																	messageSource.getMessage("rule21.error.messagepl",
																			new Object[] { ivf.getPlanEffectiveDate()}, locale),
																	Constants.FAIL));	
													return dList;
												}
												Date nextDate = DateUtils.getNextYear(planDate);
												// calendar.set(calendar.get(Calendar.YEAR)+1,calendar.get(Calendar.MONTH),
												// calendar.get(Calendar.DATE)+1);
												calendar.setTime(dos);
												boolean fiscal = DateUtils.isDatesBetweenDates(planDate, nextDate, dos);
												if (fiscal) {
													present = true;
													scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
												}
											} else if (FDTO.getCalendarMonth() > 0) {// Calendar Months Done (cal.mo)
												RuleEngineLogger.generateLogs(clazz,
														"Calendar Months:" + FDTO.getCalendarMonth(),
														Constants.rule_log_debug, bw);
												// (1X6cal.mo) Plan Date 1 Jan - 31 JAN --> jan-june and july-Dec. two
												// phase..
												if (planDate==null) {
													dList.add(
															new TPValidationResponseDto(rule.getId(), rule.getName(),
																	messageSource.getMessage("rule21.error.messagepl",
																			new Object[] { ivf.getPlanEffectiveDate()}, locale),
																	Constants.FAIL));	
													return dList;
												}
												for (int x = 0; x <= 11;) {

													x = x + FDTO.getCalendarMonth();
													int initMonth = x - FDTO.getCalendarMonth();
													int endMonth = (x - 1);

													Calendar calendar = new GregorianCalendar();
													calendar.setTime(planDate);
													calendar.set(calendar.get(Calendar.YEAR),
															calendar.get(Calendar.MONTH) + initMonth,
															calendar.get(Calendar.DATE));
													initMonth = calendar.get(Calendar.MONTH);

													calendar = new GregorianCalendar();
													calendar.setTime(planDate);
													calendar.set(calendar.get(Calendar.YEAR),
															calendar.get(Calendar.MONTH) + endMonth,
															calendar.get(Calendar.DATE));
													endMonth = calendar.get(Calendar.MONTH);

													RuleEngineLogger
															.generateLogs(clazz,
																	"Initial Calendar Month for Plan Date is:"
																			+ (initMonth + 1),
																	Constants.rule_log_debug, bw);
													RuleEngineLogger.generateLogs(clazz,
															"End Month for Plan Date is:" + (endMonth + 1),
															Constants.rule_log_debug, bw);

													calendar = new GregorianCalendar();
													calendar.setTime(dos);
													int dosmonth = calendar.get(Calendar.MONTH);

													calendar = new GregorianCalendar();
													calendar.setTime(TP_Date);
													int tpmonth = calendar.get(Calendar.MONTH);

													RuleEngineLogger.generateLogs(clazz,
															"Month for DOS is:" + (dosmonth + 1),
															Constants.rule_log_debug, bw);
													RuleEngineLogger.generateLogs(clazz,
															"Month for TP is:" + (tpmonth + 1),
															Constants.rule_log_debug, bw);
													RuleEngineLogger.generateLogs(clazz,
															" IS Month for DOS-(" + (dosmonth + 1)
																	+ ") is between Initial Calendar Month:("
																	+ (initMonth + 1) + ") and End Month for Plan("
																	+ (endMonth + 1) + ")==>"
																	+ (initMonth <= dosmonth && dosmonth >= endMonth),
															Constants.rule_log_debug, bw);

													RuleEngineLogger.generateLogs(clazz,
															" IS Month for TP-(" + (tpmonth + 1)
																	+ ") is between Initial Calendar Month:("
																	+ (initMonth + 1) + ") and End Month for Plan("
																	+ (endMonth + 1) + ")==>"
																	+ (initMonth <= tpmonth && tpmonth >= endMonth),
															Constants.rule_log_debug, bw);

													if ((initMonth <= dosmonth && dosmonth >= endMonth)
															&& (initMonth <= tpmonth && tpmonth >= endMonth)) {
														present = true;
														scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
													}
												}

											} else if (FDTO.getDays() > 0) {// Months & Days (1x6Mo_1D)

												RuleEngineLogger.generateLogs(clazz, " Days:" + FDTO.getDays(),
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "Months :" + FDTO.getMonths(),
														Constants.rule_log_debug, bw);
												//
												if (planDate==null) {
													dList.add(
															new TPValidationResponseDto(rule.getId(), rule.getName(),
																	messageSource.getMessage("rule21.error.messagepl",
																			new Object[] { ivf.getPlanEffectiveDate()}, locale),
																	Constants.FAIL));	
													return dList;
												}
												if (dos.compareTo(planDate) < 0) {
													RuleEngineLogger.generateLogs(clazz,
															" HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
															Constants.rule_log_debug, bw);

													// continue;
												}
												Calendar nextAvailbleDate = new GregorianCalendar();
												nextAvailbleDate.setTime(dos);
												nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
														nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(),
														nextAvailbleDate.get(Calendar.DATE));

												nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
														nextAvailbleDate.get(Calendar.MONTH),
														nextAvailbleDate.get(Calendar.DATE) + FDTO.getDays());
												//
												RuleEngineLogger.generateLogs(clazz,
														"NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date,
														Constants.rule_log_debug, bw);

												// 1 June Dec 12 -->6 Months
												// 1 Dec Dec 12
												if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
													RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-",
															Constants.rule_log_debug, bw);
													present = true;
													scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
												}

											} else if (FDTO.getOnlyDays() > 0) {// Days
												RuleEngineLogger.generateLogs(clazz, "ONLY DAYS :" + FDTO.getOnlyDays(),
														Constants.rule_log_debug, bw);
												if (planDate==null) {
													dList.add(
															new TPValidationResponseDto(rule.getId(), rule.getName(),
																	messageSource.getMessage("rule21.error.messagepl",
																			new Object[] { ivf.getPlanEffectiveDate()}, locale),
																	Constants.FAIL));	
													return dList;
												}
												if (dos.compareTo(planDate) < 0) {
													RuleEngineLogger.generateLogs(clazz,
															" HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
															Constants.rule_log_debug, bw);

													// continue;
												}
												Calendar nextAvailbleDate = new GregorianCalendar();
												nextAvailbleDate.setTime(dos);
												nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
														nextAvailbleDate.get(Calendar.MONTH),
														nextAvailbleDate.get(Calendar.DATE) + FDTO.getOnlyDays());
												//
												RuleEngineLogger.generateLogs(clazz,
														"NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date,
														Constants.rule_log_debug, bw);

												// 1 June Dec 12 -->6 Months
												// 1 Dec Dec 12
												if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
													RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-",
															Constants.rule_log_debug, bw);
													present = true;
													scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
												}

											} else if (FDTO.getMonths() > 0) {// Months
												RuleEngineLogger.generateLogs(clazz, "MONTHS :" + FDTO.getMonths(),
														Constants.rule_log_debug, bw);
												if (planDate==null) {
													dList.add(
															new TPValidationResponseDto(rule.getId(), rule.getName(),
																	messageSource.getMessage("rule21.error.messagepl",
																			new Object[] { ivf.getPlanEffectiveDate()}, locale),
																	Constants.FAIL));	
													return dList;
												}
												if (dos.compareTo(planDate) < 0) {
													RuleEngineLogger.generateLogs(clazz,
															" HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
															Constants.rule_log_debug, bw);

													// continue;
												}
												Calendar nextAvailbleDate = new GregorianCalendar();
												nextAvailbleDate.setTime(dos);
												nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
														nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(),
														nextAvailbleDate.get(Calendar.DATE));
												RuleEngineLogger.generateLogs(clazz,
														"NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
														Constants.rule_log_debug, bw);
												RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date,
														Constants.rule_log_debug, bw);

												// 1 JUne Dec 12 -->6 Months
												// 1 Dec Dec 12
												if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
													RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-",
															Constants.rule_log_debug, bw);
													present = true;
													scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
												}
											}

											////
											if (present) {
												List<ServiceCodeIvfTimesFreqFieldDto> ln = mapFlIVFFinal.get(tooth);
												if (ln == null) {
													ln = new ArrayList<>();
													ln.add(scivfTFDFinal);
													mapFlIVFFinal.put(tooth, ln);
												} else {
													ln.add(scivfTFDFinal);
												}
											}
										}

										// }
									}
								}
							}
						}
					}

				}
				// MAIN LOGIC ALIKE CODES
				/*
				 * D1206 and D1208 D4341 and D4342 D1110 and D1120 D0272 and D0274 D0150, D0120,
				 * and D0140
				 * 
				 */
				// D1206 and D1208
				for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry : mapFlIVFFinal.entrySet()) {

					String tooth = entry.getKey();
					List<ServiceCodeIvfTimesFreqFieldDto> li = entry.getValue();
					List<ServiceCodeIvfTimesFreqFieldDto> D1206 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D1208 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D4341 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D4342 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D1110 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D1120 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D0270 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D0272 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D0273 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D0274 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D0150 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D0120 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D0140 = null;

					// New Codes
					List<ServiceCodeIvfTimesFreqFieldDto> D2391 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> M2391 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> P2391 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D2392 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> M2392 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> P2392 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D2393 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> M2393 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> P2393 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D2394 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> M2394 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> P2394 = null;

					List<ServiceCodeIvfTimesFreqFieldDto> RestC1 = null;

					for (ServiceCodeIvfTimesFreqFieldDto d : li) {
						if (RestC1 == null)
							RestC1 = new ArrayList<>();
						RestC1.add(d);
						if (d.getServiceCode().equals("D1206")) {
							if (D1206 == null)
								D1206 = new ArrayList<>();
							D1206.add(d);
						} else if (d.getServiceCode().equals("D1208")) {
							if (D1208 == null)
								D1208 = new ArrayList<>();
							D1208.add(d);
						} else if (d.getServiceCode().equals("D4341")) {
							if (D4341 == null)
								D4341 = new ArrayList<>();
							D4341.add(d);
						} else if (d.getServiceCode().equals("D4342")) {
							if (D4342 == null)
								D4342 = new ArrayList<>();
							D4342.add(d);
						} else if (d.getServiceCode().equals("D1110")) {
							if (D1110 == null)
								D1110 = new ArrayList<>();
							D1110.add(d);
						} else if (d.getServiceCode().equals("D1120")) {
							if (D1120 == null)
								D1120 = new ArrayList<>();
							D1120.add(d);
						} else if (d.getServiceCode().equals("D0270")) {
							if (D0270 == null)
								D0270 = new ArrayList<>();
							D0270.add(d);
						} else if (d.getServiceCode().equals("D0272")) {
							if (D0272 == null)
								D0272 = new ArrayList<>();
							D0272.add(d);
						} else if (d.getServiceCode().equals("D0273")) {
							if (D0273 == null)
								D0273 = new ArrayList<>();
							D0273.add(d);
						} else if (d.getServiceCode().equals("D0274")) {
							if (D0274 == null)
								D0274 = new ArrayList<>();
							D0274.add(d);
						} else if (d.getServiceCode().equals("D0150")) {
							if (D0150 == null)
								D0150 = new ArrayList<>();
							D0150.add(d);
						} else if (d.getServiceCode().equals("D0120")) {
							if (D0120 == null)
								D0120 = new ArrayList<>();
							D0120.add(d);
						} else if (d.getServiceCode().equals("D0140")) {
							if (D0140 == null)
								D0140 = new ArrayList<>();
							D0140.add(d);
						} ///
						else if (d.getServiceCode().equals("D2391")) {
							if (D2391 == null)
								D2391 = new ArrayList<>();
							D2391.add(d);
						} else if (d.getServiceCode().equals("M2391")) {
							if (M2391 == null)
								M2391 = new ArrayList<>();
							M2391.add(d);
						} else if (d.getServiceCode().equals("P2391")) {
							if (P2391 == null)
								P2391 = new ArrayList<>();
							P2391.add(d);
						} else if (d.getServiceCode().equals("D2392")) {
							if (D2392 == null)
								D2392 = new ArrayList<>();
							D2392.add(d);
						} else if (d.getServiceCode().equals("M2392")) {
							if (M2392 == null)
								M2392 = new ArrayList<>();
							M2392.add(d);
						} else if (d.getServiceCode().equals("P2392")) {
							if (P2392 == null)
								P2392 = new ArrayList<>();
							P2392.add(d);
						} else if (d.getServiceCode().equals("D2393")) {
							if (D2393 == null)
								D2393 = new ArrayList<>();
							D2393.add(d);
						} else if (d.getServiceCode().equals("M2393")) {
							if (M2393 == null)
								M2393 = new ArrayList<>();
							M2393.add(d);
						} else if (d.getServiceCode().equals("P2393")) {
							if (P2393 == null)
								P2393 = new ArrayList<>();
							P2393.add(d);
						} else if (d.getServiceCode().equals("D2394")) {
							if (D2394 == null)
								D2394 = new ArrayList<>();
							D2394.add(d);
						} else if (d.getServiceCode().equals("M2394")) {
							if (M2394 == null)
								M2394 = new ArrayList<>();
							M2394.add(d);
						} else if (d.getServiceCode().equals("P2394")) {
							if (P2394 == null)
								P2394 = new ArrayList<>();
							P2394.add(d);
						}
					} // For Loop d:li
						// Alike code
					if (D1206 != null && D1208 != null) {
						Object[] m = FreqencyUtils.getError(D1206, D1208, "D1206", "D1208", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));
						}
					}
					if (D4341 != null && D4342 != null) {
						Object[] m = FreqencyUtils.getError(D4341, D4342, "D4341", "D4342", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));
						}
					}
					if (D1110 != null && D1120 != null) {
						Object[] m = FreqencyUtils.getError(D1110, D1120, "D1110", "D1120", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));
						}
					}
					if (D0270 != null || D0272 != null || D0274 != null || D0273 != null) {
						Object[] m = FreqencyUtils.getError(D0270, D0272, D0274, D0273, "D0270", "D0272", "D0274",
								"D0273", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));

						}
					}
					if (D0150 != null || D0120 != null || D0140 != null) {
						Object[] m = FreqencyUtils.getError(D0150, D0120, D0140, "D0150", "D0120", "D0140", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));

						}
					}

					//
					if (D2391 != null || M2391 != null || P2391 != null) {
						Object[] m = FreqencyUtils.getError(D2391, M2391, P2391, "D2391", "M2391", "P2391", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));

						}
					}

					if (D2392 != null || M2392 != null || P2392 != null) {
						Object[] m = FreqencyUtils.getError(D2392, M2392, P2392, "D2392", "M2392", "P2392", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));

						}
					}
					if (D2393 != null || M2393 != null || P2393 != null) {
						Object[] m = FreqencyUtils.getError(D2393, M2393, P2393, "D2393", "M2393", "P2393", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));

						}
					}
					if (D2394 != null || M2394 != null || P2394 != null) {
						Object[] m = FreqencyUtils.getError(D2394, M2394, P2394, "D2394", "M2394", "P2394", tooth);
						if (m != null) {
							pass = false;
							FreqencyUtils.addToFailedSet(failedCodeSet, m);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule21.error.message", m, locale), Constants.FAIL));

						}
					}
					// if (pass) {
					Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> fm = new HashMap<>();
					for (ServiceCodeIvfTimesFreqFieldDto s : li) {
						List<ServiceCodeIvfTimesFreqFieldDto> l2 = fm.get(s.getServiceCode());
						if (l2 == null) {
							l2 = new ArrayList<>();
							l2.add(s);
							fm.put(s.getServiceCode(), l2);
						} else {
							l2.add(s);
						}

					}
					for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry2 : fm.entrySet()) {
						List<ServiceCodeIvfTimesFreqFieldDto> s = entry2.getValue();
						Object[] m = FreqencyUtils.getCountTimeServiceCode(s, entry2.getKey());
						if (m != null) {

							if (!failedCodeSet.contains(m[5])) {
								RuleEngineLogger.generateLogs(clazz, "ACTUAL COUNT for " + m[5] + " -->" + m[0],
										Constants.rule_log_debug, bw);
								RuleEngineLogger.generateLogs(clazz, " FREQUENCY   for " + m[5] + " -->" + m[4],
										Constants.rule_log_debug, bw);
								if ((int) m[0] >= (int) m[4]) {
									pass = false;
									dList.add(
											new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule21.error.message",
															new Object[] { m[5], m[1], m[2], m[4],m[6] }, locale),
													Constants.FAIL));
								}
							}
						}
					}
					// }

				}

			} // HISTORY PRESENT
			else {
				RuleEngineLogger.generateLogs(clazz, "History not Present...", Constants.rule_log_debug, bw);

			}
			if (pass)
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		} catch (Exception x) {

			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return dList;

	}

	// CRA
	public List<TPValidationResponseDto> Rule22(List<Object> tpList, Object ivfSheet, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_22,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String cra = ivf.getCraRequired();
		RuleEngineLogger.generateLogs(clazz, "CRA-"+cra, Constants.rule_log_debug, bw);
		boolean pass = true;
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			String codeFound1 = "";
			String codeFound2 = "";

			if (cra != null && cra.trim().equalsIgnoreCase("yes")) {
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;

					if (tp.getServiceCode().equalsIgnoreCase("D0120") || tp.getServiceCode().equalsIgnoreCase("D0150")
							|| tp.getServiceCode().equalsIgnoreCase("D0145")) {
						codeFound1 = tp.getServiceCode();
						RuleEngineLogger.generateLogs(clazz, "Service Code IN TP-"+tp.getServiceCode(), Constants.rule_log_debug, bw);

					}
					if (tp.getServiceCode().equalsIgnoreCase("D0601") || tp.getServiceCode().equalsIgnoreCase("D0602")
							|| tp.getServiceCode().equalsIgnoreCase("D0603")) {
						codeFound2 = tp.getServiceCode();
						RuleEngineLogger.generateLogs(clazz, "Service Code IN TP-"+tp.getServiceCode(), Constants.rule_log_debug, bw);

					}
				} // For LOOP end
				if (!codeFound1.equals("") && codeFound2.equals("")) {
					pass = false;
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule22.error.message", new Object[] {ivf.getInsName()}, locale), Constants.FAIL));
				}else {
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
					
				}
			}else if (cra != null && cra.trim().equalsIgnoreCase("no")) {
				pass = true;
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule22.error.messageno", new Object[] {}, locale), Constants.NotNeeded));
				
			}else  {
				pass = false;
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule22.error.messagere", new Object[] { cra }, locale), Constants.FAIL));
				
			}
			/*
			if (pass) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
			}
			*/
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

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
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> (!rule.getAdditionalInformationNeeded().equalsIgnoreCase("No")
						&& !rule.getAdditionalInformationNeeded().equalsIgnoreCase("NA")
						&& !rule.getAdditionalInformationNeeded().equalsIgnoreCase("None")
						&& rule.getAdaCodes().getCode().equals(code)));
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for nulll
		return r;
	}

	private Mappings getMappingFromListPreAuth(List<Mappings> map, String code) {
		Mappings r = null;
		Collection<Mappings> ruleGen = Collections2.filter(map, rule -> (!rule.getPreAuthNeeded().equalsIgnoreCase("No")
				&& !rule.getPreAuthNeeded().equalsIgnoreCase("NA") && !rule.getPreAuthNeeded().equalsIgnoreCase("None")
				&& rule.getAdaCodes().getCode().equals(code))

		);
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for null
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
		// Debug me for null
		return r;
	}

	private List<Mappings> getMappingDownGradeFromListByType(List<Mappings> map, String downgradingType) {
		List<Mappings> r = new ArrayList<>();
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> (!rule.getDowngrading().equalsIgnoreCase("NA") && !rule.getDowngrading().equalsIgnoreCase("No"))
						&& rule.getDowngradingCaCrown().trim().equalsIgnoreCase(downgradingType));
		for (Mappings rule : ruleGen) {
			r.add(rule);
		}
		return r;
	}

	/*
	 * private List<Mappings> getMappingDownGradeFromListCR(List<Mappings> map) {
	 * List<Mappings> r = new ArrayList<>(); Collection<Mappings> ruleGen =
	 * Collections2.filter(map, rule ->
	 * (!rule.getDowngrading().equalsIgnoreCase("NA") &&
	 * !rule.getDowngrading().equalsIgnoreCase("No")) &&
	 * rule.getDowngradingCaCrown().equalsIgnoreCase("crowns")); for (Mappings rule
	 * : ruleGen) { r.add(rule); } return r; }
	 */
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
