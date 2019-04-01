package com.tricon.ruleengine.utils;

import java.io.BufferedWriter;
import java.lang.reflect.Method;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.MessageSource;

import com.google.api.client.util.Lists;
import com.google.common.collect.Collections2;
import com.tricon.ruleengine.dto.FeeToothDto;
import com.tricon.ruleengine.dto.FreqencyDto;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
//import com.tricon.ruleengine.dto.HistoryMatcherDto;
import com.tricon.ruleengine.dto.Rule6Dto;
//import com.tricon.ruleengine.dto.ServiceCodeDateDto;
import com.tricon.ruleengine.dto.ServiceCodeIvfTimesFreqFieldDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.MVPandVAP;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.EagleSoftEmployerMaster;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.EagleSoftPatientWalkHistory;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;

import edu.umd.cs.findbugs.DiscoverSourceDirectories.Progress;

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
	public List<TPValidationResponseDto> Rule1(Object ivfSheet, MessageSource messageSource, Rules rule, boolean onlyIVF,
			List<Object> tpList, BufferedWriter bw) {
		// Date tpDate = null;
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_1,
				Constants.rule_log_debug, bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		Date currentDate = new Date();
		List<TPValidationResponseDto> dList = new ArrayList<>();
		Date ivfPlanTermDate = null;
		boolean proceed = false;
		boolean pass=true;
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

				if (ivfPlanTermDate != null && ivfPlanTermDate.compareTo(currentDate) > 0) {
					proceed = true;
				} else {

					// Exit Engine
					RuleEngineLogger.generateLogs(clazz, "Exit Engine ", Constants.rule_log_debug, bw);
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule1.error.message.plantermedDate", new Object[] { td }, locale),
							Constants.EXTI_ENGINE));
					pass=false;
					return dList;
				}
			} else {
				proceed = true;
			}
			
			
		//Added Logic
			
			if (proceed && td != null && (!td.trim().equals("") && !td.trim().equalsIgnoreCase("NA"))) {
			    int extraDays=60;
				  Calendar calendar = new GregorianCalendar();

				  calendar.setTime(ivfPlanTermDate);
				   calendar.set(calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)+extraDays);
				   Date dInterval= calendar.getTime();
			      if (dInterval.compareTo(currentDate) >= 0) {
			    	  //Pass with Alert
			    	  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule1.error.message.termPlusExtra",
										new Object[] { td }, locale),
								Constants.ALERT));
			    	  pass=false;
			      }
			      if (dInterval.compareTo(currentDate) < 0) {
			    	  ///nothing just pass
			      }
			      
			}
			
			
			
		} catch (Exception ex) {
			dList.add( new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule1.error.message.date", new Object[] { ivf.getPlanTermedDate() }, locale), Constants.FAIL));
			return dList;

		}
		try {
			if (proceed) {
				if (!onlyIVF) {

					if (!onlyIVF && tpList == null) {
						dList.add( new TPValidationResponseDto(rule.getId(), rule.getName(),
								Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose,
								Constants.FAIL));
						return dList;
					}
					if (tpList != null) {
						for (Object obj : tpList) {
							TreatmentPlan tp = (TreatmentPlan) obj;
							if (!ivf.getPatientId().equals(tp.getPatient().getId())) {
								dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule1.error.message.patientId",
												new Object[] { ivf.getPatientId(), tp.getPatient().getId() }, locale),
										Constants.FAIL));
								return dList;

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

					//if (pass)dList.add( new TPValidationResponseDto(rule.getId(), rule.getName(),
					//		messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS));

				} else {

					dList.add( new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule1.error.message", new Object[] {
									Constants.SIMPLE_DATE_FORMAT_IVF.format(currentDate), ivf.getPlanEffectiveDate() },
									locale),
							Constants.FAIL));
					return dList;
				}
			}
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource.getMessage(
					"rule1.error.message.date", new Object[] { ivf.getPlanEffectiveDate() }, locale), Constants.FAIL));
			return dList;

		}
		if (pass)dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
				messageSource.getMessage("rule.message.pass", null, locale), Constants.PASS));
		return dList;

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
            /*Blocked due Phase 2 change
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
            */
			//Phase 2 change
			List<String> codes=new ArrayList<>();
			codes.add("D0150");
			codes.add("D0140");
			codes.add("D0145");
			codes.add("D0120");
			
			String pcName="";
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				if (codes.contains(tp.getServiceCode())) {
					pcName=tp.getProviderLastName();
					break;
				}
			}
			if (!pcName.equals("")) {
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
						pcName=tp.getProviderLastName();
						break;
					
				}
			}
			if (!pcName.trim().equalsIgnoreCase(ivf.getProviderName())) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee",
						messageSource.getMessage("rule4.error.message_5",
								new Object[] { ivf.getProviderName(), pcName, }, locale),
						Constants.FAIL));

				pass = false;
			}
			
			//Phase 2 change - end
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
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

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
				//permanentMolarTCList.add("1");//Removed a per email :19 Feb, 2019
				permanentMolarTCList.add("2");
				permanentMolarTCList.add("3");
				permanentMolarTCList.add("13");
				permanentMolarTCList.add("14");
				permanentMolarTCList.add("15");
				//permanentMolarTCList.add("16");
				//permanentMolarTCList.add("17");
				permanentMolarTCList.add("18");
				permanentMolarTCList.add("19");
				permanentMolarTCList.add("30");
				permanentMolarTCList.add("31");
				//permanentMolarTCList.add("32");

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
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

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

	// Bundling - X-Rays (updated for Phase 2)
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

				//Phase 2
				List<String> dup=new ArrayList<>();
				dup.add("D0220");
				dup.add("D0230");
				Map<String,List<String>> map= new HashMap<>();

				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;
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
					//Phase 2 Dup
					if (dup.contains(tp.getServiceCode())){
						if (map.containsKey(tp.getServiceCode())){
						List<String> l=	map.get(tp.getServiceCode());
						String [] t=ToothUtil.getToothsFromTooth(tp.getTooth());
						List<String> x= new ArrayList<String>(Arrays.asList(t));
						l.addAll(x);
						}
						else {
							String [] t=ToothUtil.getToothsFromTooth(tp.getTooth());
							List<String> x= new ArrayList<String>(Arrays.asList(t));
						     map.put(tp.getServiceCode(), x);	
						}
					}
					// 2 DUP
					
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
				
				//Phase 2 Dup Check
				List<String> alreadyProcessedKey=new ArrayList<>();
				for (Map.Entry<String, List<String>> entry : map.entrySet()) {
					for (Map.Entry<String, List<String>> entry2 : map.entrySet()) {
					     if (entry.getKey().equals(entry2.getKey())) continue;
					     else {
					    	 if (alreadyProcessedKey.contains(entry.getKey())) continue;
					    	 List<String> list1=entry.getValue();
					    	 List<String> list2=entry2.getValue();
					    	 list1.retainAll(list2);
					    	 if (list1!=null && list1.size()>0) {
					    		 //FAIL Common TEETH is there;
					    		 pass=false;
					    		 d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule16.error.message3", new Object[] {entry.getKey(),entry2.getKey(),String.join(",", list1)}, locale),
											Constants.FAIL));
					    		 alreadyProcessedKey.add(entry.getKey());
					    	 }
					     }
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

	// Bundling - Fillings -
	public List<TPValidationResponseDto> Rule17(List<Object> tpList, Object ivfSheet, List<EagleSoftFeeShedule> esfeess, MessageSource messageSource,
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
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;
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
			//}
				//PHASE -2 
				/**/
				String planType = ivf.getPlanType();
				String cMedicate = Constants.insurance_Medicaid;
				ToothHistoryDto hdto = null;
				Map<String, List<ToothHistoryDto>> mapHistoryD = new HashMap<>();
				Map<String, List<ToothHistoryDto>> mapHistoryP = new HashMap<>();
				Map<String, List<ToothHistoryDto>> mapHistoryM = new HashMap<>();
	            if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {
	       	        List<String> dList=new ArrayList<>();
	       	        dList.add("D2330");
	       	        dList.add("D2331");
			       	dList.add("D2332");
			       	dList.add("D2391");
			       	dList.add("D2392");
			       	dList.add("D2393");
			       	        
	       	       List<String> pList=new ArrayList<>();
	       	        pList.add("P2391");
	       	        pList.add("P2392");
	       	        pList.add("P2393");
	       	       
	       	       List<String> mList=new ArrayList<>();
	       	       mList.add("M2391");
	       	       mList.add("M2392");
	       	       mList.add("M2393");
	       	
       	       
       	        
					int noOFhistory = Constants.history_codes_size;
					Class<?> c2;
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							String hs = "getHistory" + i + "Surface";
							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							Method hdm = c2.getMethod(hd);
							Method hss = c2.getMethod(hs);	
							String code = (String) hcm.invoke(hisSheet);
							if (code.equals("")) continue ;
							Collection<String> pritd = Collections2.filter(dList,
									cd -> cd.substring(0,1).equals(code.toUpperCase().substring(0, 1)) && 
										 cd.substring(1,cd.length()).equals(code.toUpperCase().substring(1,code.length()))
										 );
									  
							Collection<String> pritp = Collections2.filter(pList,
									cd -> cd.substring(0,1).equals(code.toUpperCase().substring(0, 1)) && 
									 cd.substring(1,cd.length()).equals(code.toUpperCase().substring(1,code.length()))
									 );
							Collection<String> pritm = Collections2.filter(mList,
									cd -> cd.substring(0,1).equals(code.toUpperCase().substring(0, 1)) && 
									 cd.substring(1,cd.length()).equals(code.toUpperCase().substring(1,code.length()))
									 );
			         		
							if (pritd!=null && pritd.size()>0) {
								hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistoryD.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistoryD.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistoryD.put(code, l);
								}
							}
							if (pritp!=null && pritp.size()>0) {
								hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistoryP.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistoryP.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistoryP.put(code, l);
								}
							}
							if (pritm!=null && pritm.size()>0) {
								hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistoryM.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistoryM.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistoryM.put(code, l);
								}
							}
						}
						Date TP_Date = new Date();
						for (Object obj1 : tpList) {
						TreatmentPlan tp = (TreatmentPlan) obj1;
						List<String> res= new ArrayList<>();
						//LOW ORDER SAME SURFACE
						List<String> rDLSS= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryD, true, TP_Date, true,1,12, bw);
						if (rDLSS!=null && rDLSS.size()>0) {
							List<String> dx=ToothUtil.generateErrorListForRule171(tp,esfeess,rDLSS,bw);
							for(String p:dx) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule17.error.message1", new Object[] {(p.split("---")[2]+"-"+p.split("---")[3]),p.split("---")[4] }, locale), Constants.FAIL));
							}
							pass= false;
						}
						////LOW ORDER DIFF SURFACE- out of scope
						/*
						List<String> rPLSS= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryP, true, TP_Date, true, bw);
						if (rPLSS!=null && rPLSS.size()>0) {
							List<String> dx=ToothUtil.generateErrorListForRule171(tp,esfeess,rPLSS,bw);
							for(String p:dx) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule17.error.message1", new Object[] {p.split("---")[2]+"-"+p.split("---")[3] }, locale), Constants.FAIL));
							}
							pass= false;
						}
						*/
						//Higher order Filling
						List<String> rMLSS= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryM, true, TP_Date, true,1,12, bw);
						if (rMLSS!=null && rMLSS.size()>0) {
							List<String> dx=ToothUtil.generateErrorListForRule171(tp,esfeess,rMLSS,bw);
							for(String p:dx) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule17.error.message1", new Object[] {p.split("---")[2]+"-"+p.split("---")[3] }, locale), Constants.FAIL));
							}
							pass= false;
						}
						
						
						
						//LOW ORDER DIFF SURFACE
						/* REMOVED FROM SCOPE 14th Feb,2019
						res= new ArrayList<>();
						List<String>   r= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryD, true, TP_Date, false, bw);
						if (r!=null && r.size()>0) {
							res.addAll(new ArrayList<String>(r));
							pass= false;
						}
												
						            r= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryP, true, TP_Date, false, bw);
						if (r!=null && r.size()>0) {
							res.addAll(new ArrayList<String>(r));
							pass= false;
						}
						            r= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryM, true, TP_Date, false, bw);
						if (r!=null && r.size()>0) {
							res.addAll(new ArrayList<String>(r));
							pass= false;
						}
						
						 if (res.size()>0) {
							 for (String p:res) {
								 d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule17.error.message2", new Object[] {p.split("---")[2]+"-"+p.split("---")[3] }, locale), Constants.FAIL));
										 
							 }
					
						 }
						 */
						//HIGH ORDER
						res= new ArrayList<>();
						List<String>   r= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryD, false, TP_Date, true,1,12, bw);
						if (r!=null && r.size()>0) {
							res.addAll(new ArrayList<String>(r));
							pass= false;
						}             
						            r= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryP, false, TP_Date, true,1,12, bw);
						if (r!=null && r.size()>0) {
							res.addAll(new ArrayList<String>(r));
							pass= false;
						}
						            r= ToothUtil.lowerHigherOrderFillingFound(tp, mapHistoryM, false, TP_Date, true,1,12, bw);
						if (r!=null && r.size()>0) {
							res.addAll(new ArrayList<String>(r));
							pass= false;
						}
						
						 for (String p:res) {
							 d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule17.error.message3", new Object[] {p.split("---")[2]+"-"+p.split("---")[3] }, locale), Constants.FAIL));
									 
						 }
					
						
						//RuleEngineLogger.generateLogs(clazz, " History of Sealants found.- " + historyPresent,
					    //			Constants.rule_log_debug, bw);
						}//For loop End
						
	            }
                  /**/
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
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

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

	// Pre-Auth // Attachment Required
	public List<TPValidationResponseDto> Rule10(List<Object> tpList,List<QuestionAnswerDto> ansL, MessageSource messageSource, Rules rule,
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
			List<String> combined3 = new ArrayList<>();
			List<String> combinedY = new ArrayList<>();

			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				Mappings mapA = getMappingFromListAdditionalInformationNeeded(mappings, tp.getServiceCode());
				Mappings mapP = getMappingFromListPreAuth(mappings, tp.getServiceCode());

				if (mapA != null && !tp.getServiceCode().equalsIgnoreCase("D0120")) {//Added on Sahil Suggestion on email/chat 27 Feb
					RuleEngineLogger.generateLogs(clazz,
							"Addtion Info Needed present" + mapA.getAdditionalInformationNeeded(),
							Constants.rule_log_debug, bw);
					pass = false;
					combined1.add(tp.getServiceCode() + " - " + mapA.getAdditionalInformationNeeded() + "<br>");
					
				}
				if (mapP != null && !tp.getServiceCode().equalsIgnoreCase("D7953")) {//Added on Sahil Suggestion on Chat 27 Feb
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
				//Phase 2
				if (ansL!= null) {
					String reqmet="";
					String narrative="";
					String tooth="";
					String notes="";
					String scode="";
					boolean found=false;
					for(QuestionAnswerDto ans:ansL) {
						if (ans.getServiceCode()!=null && ans.getTpId().equalsIgnoreCase(tp.getId()) && ans.getServiceCode().equalsIgnoreCase(tp.getServiceCode())) {
						found =true;
						if (Constants.Attachment_Required_question_header_id_a_all_met==ans.getQuestionId()) {//4
							reqmet= ans.getAnswer();
						}
						if (Constants.Attachment_Required_question_header_id_require==ans.getQuestionId()) {//3
							narrative=ans.getAnswer();
						}
						if (Constants.Attachment_Required_question_header_id_toothno==ans.getQuestionId()) {//2
							tooth=ans.getAnswer();
						}
						if (Constants.Attachment_Required_question_header_id_notes_nar==ans.getQuestionId()) {//5
							notes=ans.getAnswer();
						}
						if (Constants.Attachment_Required_question_header_id_service_code==ans.getQuestionId()) {//1
							scode=ans.getAnswer();
						}
						
					  }//if 
					}//for
					if (found && !scode.equalsIgnoreCase("D0120")) {//Added on Sahil Suggestion on Chat 27 Feb
						RuleEngineLogger.generateLogs(clazz, " narrative-" + narrative+"- reqmet-" + reqmet
								+"- tooth-" + tooth+"- notes-" + notes+"- scode-" + scode,
								Constants.rule_log_debug, bw);
						if (narrative!=null && !narrative.equals("") && !narrative.equalsIgnoreCase("NA") && !narrative.equalsIgnoreCase("None")
								 && notes.equals("")) {
								pass = false;
								combinedY.add(scode);
								
						}
						if (!reqmet.equalsIgnoreCase("yes")) {
								pass = false;
								d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
										.getMessage("rule10.error.message3", new Object[] {narrative,tooth, scode,notes }, locale),
										Constants.FAIL));
							}
					}
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
				if (combinedY.size() > 0)
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
							.getMessage("rule10.error.message4", new Object[] { String.join(", ", combinedY) }, locale),
							Constants.FAIL));

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
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

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
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

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
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

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
                /*For Latter
				boolean cont=false;
				List<String> codestoCheck=  new ArrayList<>( Arrays.asList( new String[]{"D0120", "D0220","D0230"} ) );
				for (Object t : tpList) {
					TreatmentPlan tp = (TreatmentPlan) t;
					if (codestoCheck.contains(tp.getServiceCode())) { 
						cont=true;break;
					}
				}
				
				if (!cont) return new ArrayList<>();
				*/	
					for (Object t : tpList) {
					TreatmentPlan tp = (TreatmentPlan) t;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

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
					0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1208", dL);
			// mapFlIVF.put("D1208", new String[] { "FlourideD1208FL",
			// ivf.getFlourideD1208FL() });// 1 Flouride_D1208_FL

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1206", "Varnish_D1206_FL", ivf.getVarnishD1206FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1206", dL);
			// mapFlIVF.put("D1206", new String[] { "Varnish_D1206_FL",
			// ivf.getVarnishD1206FL() });// 2

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1110", "ProphyD1110_FL", ivf.getProphyD1110FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1110", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1120", "ProphyD1120_FL", ivf.getProphyD1120FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1120", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0270", "XRaysBWS_FL", ivf.getxRaysBWSFL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0270", dL);
			// mapFlIVF.put("D0270", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 3

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0272", "getxRaysBWSFL", ivf.getxRaysBWSFL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0272", dL);
			// mapFlIVF.put("D0272", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 4

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0273", "XRaysBWS_FL", ivf.getxRaysBWSFL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0273", dL);
			// mapFlIVF.put("D0273", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 5

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0274", "XRaysBWS_FL", ivf.getxRaysBWSFL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0274", dL);
			// mapFlIVF.put("D0274", new String[] { "XRaysBWS_FL", ivf.getxRaysBWSFL() });//
			// 6

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0220", "XRaysPA_D0220_FL", ivf.getxRaysPAD0220FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0220", dL);
			// mapFlIVF.put("D0220", new String[] { "XRaysPA_D0220_FL",
			// ivf.getxRaysPAD0220FL() });// 7

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0230", "XRaysPA_D0230_FL", ivf.getxRaysPAD0230FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0230", dL);
			// mapFlIVF.put("D0230", new String[] { "XRaysPA_D0230_FL",
			// ivf.getxRaysPAD0230FL() });// 8

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0210", "XRaysFMX_FL", ivf.getxRaysFMXFL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0210", dL);
			// mapFlIVF.put("D0210", new String[] { "XRaysFMX_FL", ivf.getxRaysFMXFL() });//
			// 9

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0150", "ExamsD0150_FL", ivf.getExamsD0150FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0150", dL);
			// mapFlIVF.put("D0150", new String[] { "ExamsD0150_FL", ivf.getExamsD0150FL()
			// });// 10

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0145", "ExamsD0145_FL", ivf.geteExamsD0145FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0145", dL);
			// mapFlIVF.put("D0145", new String[] { "ExamsD0145_FL", ivf.geteExamsD0145FL()
			// });// 11

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0140", "ExamsD0140_FL", ivf.getExamsD0140FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0140", dL);
			// mapFlIVF.put("D0140", new String[] { "ExamsD0140_FL", ivf.getExamsD0140FL()
			// });// 12

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D0120", "ExamsD0120_FL", ivf.getExamsD0120FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D0120", dL);
			// mapFlIVF.put("D0120", new String[] { "ExamsD0120_FL", ivf.getExamsD0120FL()
			// });// 13

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2930", "SSC_D2930_FL", ivf.getsSCD2930FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2930", dL);
			// mapFlIVF.put("D2930", new String[] { "SSC_D2930_FL", ivf.getsSCD2930FL()
			// });// 14

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2931", "SSC_D2931_FL", ivf.getsSCD2931FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2931", dL);
			// mapFlIVF.put("D2931", new String[] { "SSC_D2931_FL", ivf.getsSCD2931FL()
			// });// 15

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D1351", "Sealants_D1351_FL", ivf.getSealantsD1351FL(), 0,
					0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D1351", dL);
			// mapFlIVF.put("D1351", new String[] { "Sealants_D1351_FL",
			// ivf.getSealantsD1351FL() });// 16

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4346", "Gingivitis_D4346_FL", ivf.getGingivitisD4346FL(),
					0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4346", dL);
			// mapFlIVF.put("D4346", new String[] { "Gingivitis_D4346_FL",
			// ivf.getGingivitisD4346FL() });// 17

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4341", "SRP_D4341_FL", ivf.getsRPD4341FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4341", dL);
			// mapFlIVF.put("D4341", new String[] { "SRP_D4341_FL", ivf.getsRPD4341FL()
			// });// 18

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D9940", "NightGuards_D9940_FL", ivf.getNightGuardsD9940FL(),
					0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D9940", dL);
			// mapFlIVF.put("D9940", new String[] { "NightGuards_D9940_FL",
			// ivf.getNightGuardsD9940FL() });// 19

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4910", "PerioMaintenance_D4910_FL",
					ivf.getPerioMaintenanceD4910FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4910", dL);
			// mapFlIVF.put("D4910", new String[] { "PerioMaintenance_D4910_FL",
			// ivf.getPerioMaintenanceD4910FL() });// 20

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4355", "FMD_D4355_FL", ivf.getfMDD4355FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4355", dL);
			// mapFlIVF.put("D4355", new String[] { "FMD_D4355_FL", ivf.getfMDD4355FL()
			// });// 21

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D4249", "CrownLength_D4249_FL", ivf.getCrownLengthD4249FL(),
					0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D4249", dL);
			// mapFlIVF.put("D4249", new String[] { "CrownLength_D4249_FL",
			// ivf.getCrownLengthD4249FL() });// 22

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2950", "BuildUps_D2950_FL", ivf.getBuildUpsD2950FL(), 0,
					0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2950", dL);
			// mapFlIVF.put("D2950", new String[] { "BuildUps_D2950_FL",
			// ivf.getBuildUpsD2950FL() });// 23

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D7953", "BoneGrafts_D7953_FL", ivf.getBoneGraftsD7953FL(),
					0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D7953", dL);
			// mapFlIVF.put("D7953", new String[] { "BoneGrafts_D7953_FL",
			// ivf.getBoneGraftsD7953FL() });// 24

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D7311", "Alveo_D7311_FL", ivf.getAlveoD7311FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D7311", dL);
			// mapFlIVF.put("D7311", new String[] { "Alveo_D7311_FL", ivf.getAlveoD7311FL()
			// });// 25

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D7310", "Alveo_D7310_FL", ivf.getAlveoD7310FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D7310", dL);
			// mapFlIVF.put("D7310", new String[] { "Alveo_D7310_FL", ivf.getAlveoD7310FL()
			// });// 26

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2750", "Crowns_D2750_D2740_FL",
					ivf.getCrownsD2750D2740FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2750", dL);
			// mapFlIVF.put("D2750", new String[] { "Crowns_D2750_D2740_FL",
			// ivf.getCrownsD2750D2740FL() });// 27

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2740", "Crowns_D2750_D2740_FL",
					ivf.getCrownsD2750D2740FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2740", dL);
			// mapFlIVF.put("D2740", new String[] { "Crowns_D2750_D2740_FL",
			// ivf.getCrownsD2750D2740FL() });// 28

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5110", "CompleteDentures_D5110_D5120_FL",
					ivf.getCompleteDenturesD5110D5120FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5110", dL);
			// mapFlIVF.put("D5110", new String[] { "CompleteDentures_D5110_D5120_FL",
			// ivf.getCompleteDenturesD5110D5120FL() });// 29

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5120", "CompleteDentures_D5110_D5120_FL",
					ivf.getCompleteDenturesD5110D5120FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5120", dL);
			// mapFlIVF.put("D5120", new String[] { "CompleteDentures_D5110_D5120_FL",
			// ivf.getCompleteDenturesD5110D5120FL() });// 30

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5130", "ImmediateDentures_D5130_D5140_FL",
					ivf.getImmediateDenturesD5130D5140FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5130", dL);
			// mapFlIVF.put("D5130", new String[] { "ImmediateDentures_D5130_D5140_FL",
			// ivf.getImmediateDenturesD5130D5140FL() });// 31

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5140", "ImmediateDentures_D5130_D5140_FL",
					ivf.getImmediateDenturesD5130D5140FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5140", dL);
			// mapFlIVF.put("D5140", new String[] { "ImmediateDentures_D5130_D5140_FL",
			// ivf.getImmediateDenturesD5130D5140FL() });// 32

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5213", "PartialDentures_D5213_D5214_FL",
					ivf.getPartialDenturesD5213D5214FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D5213", dL);
			// mapFlIVF.put("D5213", new String[] { "PartialDentures_D5213_D5214_FL",
			// ivf.getPartialDenturesD5213D5214FL() });// 33

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5214", "PartialDentures_D5213_D5214_FL",
					ivf.getPartialDenturesD5213D5214FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D5214", "InterimPartialDentures_D5214_FL",
					ivf.getInterimPartialDenturesD5214FL(), 0, 0,"");
			dL.add(scivftff);
			mapFlIVF.put("D5214", dL);
			// mapFlIVF.put("D5214", new String[] { "PartialDentures_D5213_D5214_FL",
			// ivf.getPartialDenturesD5213D5214FL(),
			//
			// "InterimPartialDentures_D5214_FL", ivf.getInterimPartialDenturesD5214FL()
			// });// 34

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D9310", "D9310_FL", ivf.getD9310FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D9310", dL);
			// mapFlIVF.put("D9310", new String[] { "D9310_FL", ivf.getD9310FL() });// 35

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2391", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2391", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2392", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2392", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2392", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2392", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2392", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2392", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2393", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2393", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2393", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2393", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2393", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2393", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2394", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2394", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("M2394", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("M2394", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("P2394", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("P2394", dL);


			/*
			 * Phase 2 new Codes Added Note Here PostComposites_D2391_FL will be used in new  Codes (Email Text- 25 Feb,2019).
			   Deepak -I need the IVF Columns names from where I can take values for D2330 ,D2331 ,D2332, D2335  and Their corresponding Frequency, I am not able to figure out the corresponding Columns in IVF. - 
			   Sahil - For Frequency please refer column DG -  PostComposites_D2391_FL. 
			 */
			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2330", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2330", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2331", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2331", dL);
			
			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2332", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2332", dL);

			scivftff = new ServiceCodeIvfTimesFreqFieldDto("D2335", "PostComposites_D2391_FL",
					ivf.getPostCompositesD2391FL(), 0, 0,"");
			dL = new ArrayList<>();
			dL.add(scivftff);
			mapFlIVF.put("D2335", dL);
            
			//End Phase 2 new Codes Added 
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
			int noOFhistory = Constants.history_codes_size;
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
				String hs = "getHistory" + i + "Surface";
				
				Method hcm = c2.getMethod(hc);
				Method hdm = c2.getMethod(hd);
				Method htm = c2.getMethod(ht);
				Method hss = c2.getMethod(hs);
				
				// System.out.println((String)hcm.invoke(hisSheet));
				hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
						(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));

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
			//Start- Phase 1 left over Alike Code Case
			/*
			for (Object t : tpList) {
				TreatmentPlan tp = (TreatmentPlan) t;
				if (tp.getServiceCode().equalsIgnoreCase("D1206") || tp.getServiceCode().equalsIgnoreCase("D1208")) {
					List<ServiceCodeIvfTimesFreqFieldDto> dataIVF_1 = mapFlIVF.get(tp.getServiceCode());
					for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF_1) {
						scivfTFD.setTooth(tp.getTooth());
					}
				}
				
				
			}//
				if (mapFlIVF.get("D1206")!=null &&  mapFlIVF.get("D1208")!=null) {
				List<ServiceCodeIvfTimesFreqFieldDto> dataIVF_1 = mapFlIVF.get("D1206");
				List<ServiceCodeIvfTimesFreqFieldDto> dataIVF_2 = mapFlIVF.get("D1208");
				String[] t1=null;
				String[] t2=null;
				
				for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF_1) {
					t1=ToothUtil.getToothsFromTooth(scivfTFD.getTooth());
				}
				for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF_2) {
					t1=ToothUtil.getToothsFromTooth(scivfTFD.getTooth());
				}
				
				List<String> x=ToothUtil.findCommonTooth(t1,t2);
				if (x!=null && x.size()>0) {
					historPresent=true;
					for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF_1) {
						t1=ToothUtil.getToothsFromTooth(scivfTFD.getTooth());
						scivfTFD.setCount(scivfTFD.getCount()+1);
					}
				}
				
			 }
			
			//End
            */  
			if (historPresent) {
				RuleEngineLogger.generateLogs(clazz, "History is present  now proceed Further..",
						Constants.rule_log_debug, bw);
				TP_Date = new Date();
				Map<String, List<String>> tpToothMap = null;
				List<String> list = null;
				for (Object t : tpList) {
					TreatmentPlan tp = (TreatmentPlan) t;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;
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

				if (mapFlIVF != null && tpToothMap!=null) {

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
										List<TPValidationResponseDto> xL=  FreqencyUtils.ivfFrequencyLogic(dataIVF, tpCode, tooth, historyD, c2,
                                    		   bw, CurrentYear, planDate, messageSource, rule, ivf, TP_Date, locale,
                                                mapFlIVFFinal);
										if (xL.size()>0) return xL;
 
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
					//List<ServiceCodeIvfTimesFreqFieldDto> D0140 = null;--Phase 2 removed from a-like codes

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
					
					List<ServiceCodeIvfTimesFreqFieldDto> D5110 = null;
					List<ServiceCodeIvfTimesFreqFieldDto> D5130 = null;
					
					
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
						}/* else if (d.getServiceCode().equals("D0140")) {//Phase 2 - Removed D0140
							if (D0140 == null)
								D0140 = new ArrayList<>();
							D0140.add(d);
						} *///
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
						} else if (d.getServiceCode().equals("D5110")) {
							if (D5110 == null)
								D5110 = new ArrayList<>();
							D5110.add(d);
						} else if (d.getServiceCode().equals("D5130")) {
							if (D5130 == null)
								D5130 = new ArrayList<>();
							D5130.add(d);
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
					if (D0150 != null || D0120 != null ) {//Phase 2 : removed D0140 From alike codes
						Object[] m = FreqencyUtils.getError(D0150, D0120, "D0150", "D0120", tooth);
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
					if (D5110 != null && D5130 != null) {
						Object[] m = FreqencyUtils.getError(D5110, D5130, "D5110", "D5130", tooth);
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
															new Object[] { m[5], m[1], m[2], m[4],m[6],m[7] }, locale),
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
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

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


	// Xray Bundling
    /**
     * 
     * @param tpList
     * @param ivfSheet
     * @param messageSource
     * @param rule
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule23(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_23,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String planType = ivf.getPlanType();
		String cMedicate = Constants.insurance_Medicaid;
		List<TPValidationResponseDto> dList = new ArrayList<>();

		RuleEngineLogger.generateLogs(clazz, "planType-" + planType, Constants.rule_log_debug, bw);
        try {
		//if (planType != null ) {
		if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {
					double fee = 0;

			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D0220") || tp.getServiceCode().equalsIgnoreCase("D0230")
						|| tp.getServiceCode().equalsIgnoreCase("D0272")
						|| tp.getServiceCode().equalsIgnoreCase("D0274")) {
					RuleEngineLogger.generateLogs(clazz, " Fee for " + tp.getServiceCode() + " = " + tp.getFee(),
							Constants.rule_log_debug, bw);
					double fees=0;
					if (!tp.getFee().trim().equals(""))
					fees= Double.parseDouble(tp.getFee());
					fee = fee +fees;
				}

			}

			if (fee > Constants.insurance_Medicaid_max_fee) {
				RuleEngineLogger.generateLogs(clazz,
						" Rule Fails  (" + Constants.insurance_Medicaid_max_fee + ")<" + fee, Constants.rule_log_debug,
						bw);

				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule23.error.message", new Object[] {
								Constants.insurance_Medicaid_max_fee, Constants.insurance_Medicaid_max_fee }, locale),
						Constants.FAIL));

			} else {
				RuleEngineLogger.generateLogs(clazz, " BY pass the rule ", Constants.rule_log_debug, bw);

				dList.add(
						new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule23.pass.message",
										new Object[] { Constants.insurance_Medicaid_max_fee }, locale),
								Constants.PASS));

			}
		} else {
			RuleEngineLogger.generateLogs(clazz, " BY pass the rule - Medicaid not present ", Constants.rule_log_debug,
					bw);

			dList.add(
					new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule23.pass.message1",
									new Object[] {  planType }, locale),
							Constants.PASS));


		}
        }catch (Exception x) {
        	dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));
   
       }
    	return dList;
	}

	// Filling Bundling
	
    /**
     * 
     * @param tpList
     * @param ivfSheet
     * @param messageSource
     * @param rule
     * @param esfeess
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule24(List<Object> tpList,Object ivfSheet,  MessageSource messageSource, Rules rule,
			List<EagleSoftFeeShedule> esfeess, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_24,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String planType = ivf.getPlanType();
		String cMedicate = Constants.insurance_Medicaid;
		List<TPValidationResponseDto> dList = new ArrayList<>();

		Map<String, FeeToothDto> mapE = new HashMap<>();
		Map<String, List<ToothHistoryDto>> mapHistory = new HashMap<>();
		try {
			boolean checkForHistory = false;
			boolean historyPresent = false;

			if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {
						
				List<String> reqList = new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(",")));
                reqList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(","))));
                reqList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_MM_SC.split(","))));
                
				List<String> historyCheckList = Arrays.asList(Constants.SEALANT_SC.split(","));
				
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

					if (reqList.contains(tp.getServiceCode().toUpperCase())) {
						RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug,
								bw);
						RuleEngineLogger.generateLogs(clazz, "Check for History", Constants.rule_log_debug, bw);
						checkForHistory = true;
						if (mapE.containsKey(tp.getServiceCode())) {
							FeeToothDto ft = mapE.get(tp.getServiceCode());
							List<String> l = ft.getTooth();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
						} else {
							List<String> l = new ArrayList<>();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							FeeToothDto ft = new FeeToothDto();
							ft.setFees(tp.getFee());
							ft.setTooth(l);
							mapE.put(tp.getServiceCode(), ft);
						}
					}

				}
				ToothHistoryDto hdto=null;
				if (checkForHistory) {
					int noOFhistory = Constants.history_codes_size;
					Class<?> c2;
					try {
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							String hs = "getHistory" + i + "Surface";
							
							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							Method hdm = c2.getMethod(hd);
							Method hss = c2.getMethod(hs);
							
							String code = (String) hcm.invoke(hisSheet);
							if (historyCheckList.contains(code.toUpperCase())) {
								historyPresent = true;
								hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistory.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistory.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistory.put(code, l);
								}
							}
						}
						RuleEngineLogger.generateLogs(clazz, " History of Sealants found.- " + historyPresent,
								Constants.rule_log_debug, bw);
					    Date TP_DATE=null;	
						if (historyPresent) {
							TP_DATE= new Date();
							boolean breakAll=false;
							for (Map.Entry<String, List<ToothHistoryDto>> entry : mapHistory.entrySet()) {
								List<ToothHistoryDto> d=entry.getValue();
								
								for (Map.Entry<String, FeeToothDto> entry2 : mapE.entrySet()) {
									FeeToothDto fd=entry2.getValue();
									List<String> t=fd.getTooth();
									for(ToothHistoryDto hisd:d) {
										String [] a= ToothUtil.getToothsFromTooth(hisd.getHistoryTooth());
									    String[] objects =t.toArray(new String[0]);
							     		List<String> cm= ToothUtil.findCommonTooth( objects, a);
							     		if (cm!=null && cm.size()>0) {
							     		//if (t.contains(hisd.getHistoryTooth())) {
											Date dos = null;
											try {
												dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(hisd.getHistoryDos());
												RuleEngineLogger.generateLogs(clazz,
														"History DOS-" + hisd.getHistoryDos(),
														Constants.rule_log_debug, bw);
												
												
											} catch (ParseException e2) {
												// TODO Auto-generated catch block
												e2.printStackTrace();
											}
											RuleEngineLogger.generateLogs(clazz,
													"TP_DATE-" + TP_DATE,
													Constants.rule_log_debug, bw);
										
										if (!DateUtils.checkforXm(TP_DATE, dos,12)) {
											RuleEngineLogger.generateLogs(clazz,
													"RULE FAILS-" + TP_DATE,
													Constants.rule_log_debug, bw);
											Collection<EagleSoftFeeShedule> ruleGen = Collections2.filter(esfeess,
													name -> name.getFeesServiceCode().equals(hisd.getHistoryCode()));
											if (ruleGen != null) {
												for (EagleSoftFeeShedule fs : ruleGen) {
													RuleEngineLogger.generateLogs(clazz, "TP ("+entry2.getKey()+")Fee -" + fd.getFees(),
															Constants.rule_log_debug, bw);
													RuleEngineLogger.generateLogs(clazz, "ES ("+hisd.getHistoryCode()+") Fee -" + fs.getFeesFee(),
															Constants.rule_log_debug, bw);
											double diff= Double.parseDouble(fd.getFees())-Double.parseDouble(fs.getFeesFee());
											dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule24.error.message", new Object[] {hisd.getHistoryTooth(), diff  }, locale),
													Constants.FAIL));
										
												}		
											}else{
												RuleEngineLogger.generateLogs(clazz, "Fee Not found for "+hisd.getHistoryCode()+" in Fee Schedule",
														Constants.rule_log_debug, bw);
												dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
														messageSource.getMessage("rule.fee.notfound", new Object[] {  }, locale),
														Constants.FAIL));
												
											}
											breakAll=true; //need to  break;
											//break;
										}
						
									}
										//if (breakAll) break;
								}//FOR-hisd
								//if (breakAll) break;
							}//FOR -ft
						}//FOR -ft
						if (!breakAll) {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule24.pass1.message", new Object[] {  }, locale),
									Constants.PASS));
			
						}
						
					 }//if history
						else {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule24.pass1.message", new Object[] {  }, locale),
									Constants.PASS));
			
						}
					}catch (Exception e) {
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule.error.exception", new Object[] { e.getMessage() }, locale),
								Constants.FAIL));
								}
				}
			} else {
				RuleEngineLogger.generateLogs(clazz, " Plan type not Medicaid",
						Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule24.pass3.message", new Object[] {  }, locale),
						Constants.PASS));

			}
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;
	}

	
	// Crown Bundling with Fillings
	
   /**
    * 
    * @param tpList
    * @param ivfSheet
    * @param messageSource
    * @param rule
    * @param esfeess
    * @param bw
    * @return
    */
	public List<TPValidationResponseDto> Rule25(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
			List<EagleSoftFeeShedule> esfeess, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_25,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String planType = ivf.getPlanType();
		String cMedicate = Constants.insurance_Medicaid;
		List<TPValidationResponseDto> dList = new ArrayList<>();

		Map<String, FeeToothDto> mapE = new HashMap<>();
		Map<String, List<ToothHistoryDto>> mapHistory = new HashMap<>();
		try {
			boolean checkForHistory = false;
			boolean historyPresent = false;

			if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {
				
				List<String> reqList = new ArrayList<String>(Arrays.asList(Constants.CROWN_SC.split(",")));
				reqList.addAll(Arrays.asList(Constants.STAIN_LESS_STEEL_CROWN_SC.split(",")));//Added As per email : Sahil (23 Feb,2019 and same in Crown Rule)
                List<String> historyCheckList = new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(",")));
                historyCheckList.addAll(Arrays.asList(Constants.FILLING_PT_SC.split(",")));
                historyCheckList.addAll(Arrays.asList(Constants.FILLING_MM_SC.split(",")));
                
				
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

					if (reqList.contains(tp.getServiceCode().toUpperCase())) {
						RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug,
								bw);
						RuleEngineLogger.generateLogs(clazz, "Check for History", Constants.rule_log_debug, bw);
						checkForHistory = true;
						if (mapE.containsKey(tp.getServiceCode())) {
							FeeToothDto ft = mapE.get(tp.getServiceCode());
							List<String> l = ft.getTooth();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
						} else {
							List<String> l = new ArrayList<>();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							FeeToothDto ft = new FeeToothDto();
							ft.setFees(tp.getFee());
							ft.setTooth(l);
							mapE.put(tp.getServiceCode(), ft);
						}
					}

				}
				ToothHistoryDto hdto=null;
				if (checkForHistory) {
					int noOFhistory = Constants.history_codes_size;
					Class<?> c2;
					try {
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							String hs = "getHistory" + i + "Surface";
							
							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							Method hdm = c2.getMethod(hd);
							Method hss = c2.getMethod(hs);

							String code = (String) hcm.invoke(hisSheet);
							if (historyCheckList.contains(code.toUpperCase())) {
								historyPresent = true;
								hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistory.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistory.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistory.put(code, l);
								}
							}
						}
						RuleEngineLogger.generateLogs(clazz, " History of Filling found.- " + historyPresent,
								Constants.rule_log_debug, bw);
					    Date TP_DATE=null;	
						if (historyPresent) {
							TP_DATE= new Date();
							boolean breakAll=false;
							for (Map.Entry<String, List<ToothHistoryDto>> entry : mapHistory.entrySet()) {
								List<ToothHistoryDto> d=entry.getValue();
								
								for (Map.Entry<String, FeeToothDto> entry2 : mapE.entrySet()) {
									FeeToothDto fd=entry2.getValue();
									List<String> t=fd.getTooth();
									for(ToothHistoryDto hisd:d) {
										String [] a= ToothUtil.getToothsFromTooth(hisd.getHistoryTooth());
									    String[] objects =t.toArray(new String[0]);
							     		List<String> cm= ToothUtil.findCommonTooth( objects, a);
							     		if (cm!=null && cm.size()>0) {
							     		//if (t.contains(hisd.getHistoryTooth())) {
											Date dos = null;
											try {
												dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(hisd.getHistoryDos());
												RuleEngineLogger.generateLogs(clazz,
														"History DOS-" + hisd.getHistoryDos(),
														Constants.rule_log_debug, bw);
												
												
											} catch (ParseException e2) {
												// TODO Auto-generated catch block
												e2.printStackTrace();
											}
											RuleEngineLogger.generateLogs(clazz,
													"TP_DATE-" + TP_DATE,
													Constants.rule_log_debug, bw);
										
										if (!DateUtils.checkforXm(TP_DATE, dos,12)) {
											RuleEngineLogger.generateLogs(clazz,
													"RULE FAILS-" + TP_DATE,
													Constants.rule_log_debug, bw);
											Collection<EagleSoftFeeShedule> ruleGen = Collections2.filter(esfeess,
													name -> name.getFeesServiceCode().equals(hisd.getHistoryCode()));
											if (ruleGen != null) {
												for (EagleSoftFeeShedule fs : ruleGen) {
													RuleEngineLogger.generateLogs(clazz, "TP ("+entry2.getKey()+")Fee -" + fd.getFees(),
															Constants.rule_log_debug, bw);
													RuleEngineLogger.generateLogs(clazz, "ES ("+hisd.getHistoryCode()+") Fee -" + fs.getFeesFee(),
															Constants.rule_log_debug, bw);
											double diff= Double.parseDouble(fd.getFees())-Double.parseDouble(fs.getFeesFee());
											dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule25.error.message", new Object[] { fs.getFeesServiceCode(),hisd.getHistoryTooth(), diff  }, locale),
													Constants.FAIL));
										
												}		
											}else{
												RuleEngineLogger.generateLogs(clazz, "Fee Not found for "+hisd.getHistoryCode()+" in Fee Schedule",
														Constants.rule_log_debug, bw);
												dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
														messageSource.getMessage("rule.fee.notfound", new Object[] {  }, locale),
														Constants.FAIL));
												
											}
											breakAll=true;
											//break;
										}
						
									}
										//if (breakAll) break;
								}//FOR-hisd
								//if (breakAll) break;
							}//FOR -ft
						}//FOR -ft
						if (!breakAll) {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule25.pass1.message", new Object[] {  }, locale),
									Constants.PASS));
			
						}
						
					 }//if history
						else {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule25.pass1.message", new Object[] {  }, locale),
									Constants.PASS));
			
						}
					}catch (Exception e) {
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule.error.exception", new Object[] { e.getMessage() }, locale),
								Constants.FAIL));
								}
				}else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule25.pass1.message", new Object[] {  }, locale),
							Constants.PASS));
				}
			} else {
				RuleEngineLogger.generateLogs(clazz, " Plan type not Medicaid",
						Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule25.pass3.message", new Object[] {  }, locale),
						Constants.PASS));

			}
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;
	}

	// Filling and Sealant Not Covered
	
   /**
    * 
    * @param tpList
    * @param ivfSheet
    * @param messageSource
    * @param rule
    * @param bw
    * @return
    */
	public List<TPValidationResponseDto> Rule26(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
			 BufferedWriter bw) {
			RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_27,
					Constants.rule_log_debug, bw);

			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			List<TPValidationResponseDto> dList = new ArrayList<>();

			Map<String, FeeToothDto> mapE = new HashMap<>();
			Map<String, FeeToothDto> mapD = new HashMap<>();
			boolean pass=true;
			//Map<String, List<ToothHistoryDto>> mapHistory = new HashMap<>();
			try {
				boolean checkForMajorLogic = false;
				//boolean historyPresent = false;

					
					List<String> reqList = new ArrayList<String>(Arrays.asList(Constants.CROWN_SC.split(",")));
					reqList.addAll(new ArrayList<String>(Arrays.asList(Constants.STAIN_LESS_STEEL_CROWN_SC.split(","))));
					//List<String> hs_extraList = new ArrayList<String>(Arrays.asList(Constants.STAIN_LESS_STEEL_CROWN_SC.split(",")));
					
					
	                List<String> checkList = new ArrayList<String>(Arrays.asList(Constants.SEALANT_SC.split(",")));
	                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
	                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(","))));
	                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_MM_SC.split(","))));
	                
	                //List<String> c1List = new ArrayList<String>(Arrays.asList(Constants.EXTRACTION_SC.split(",")));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(","))));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.CROWN_SC.split(","))));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.SEALANT_SC.split(","))));
	                
	                
	                
					for (Object obj : tpList) {
						TreatmentPlan tp = (TreatmentPlan) obj;
						RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
								Constants.rule_log_debug, bw);
							
						if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
							||	tp.getEstInsurance().equals("0.0")) continue;

						
						if (reqList.contains(tp.getServiceCode().toUpperCase())) {
							RuleEngineLogger.generateLogs(clazz, "code R=" + tp.getServiceCode(), Constants.rule_log_debug,
									bw);
							RuleEngineLogger.generateLogs(clazz, "Tooth R=" + tp.getTooth(), Constants.rule_log_debug,
									bw);
							checkForMajorLogic = true;
							if (mapE.containsKey(tp.getServiceCode())) {
								FeeToothDto ft = mapE.get(tp.getServiceCode());
								List<String> l = ft.getTooth();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							} else {
								List<String> l = new ArrayList<>();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
								FeeToothDto ft = new FeeToothDto();
								ft.setFees(tp.getFee());
								ft.setTooth(l);
								mapE.put(tp.getServiceCode(), ft);
							}
						}
						
						if (mapE.get(tp.getServiceCode())==null && checkList.contains(tp.getServiceCode().toUpperCase())) {//Duplicate Codes
							//We avoid Same Extraction Code in the List
							RuleEngineLogger.generateLogs(clazz, "code C=" + tp.getServiceCode(), Constants.rule_log_debug,
									bw);
							RuleEngineLogger.generateLogs(clazz, "Tooth C=" + tp.getTooth(), Constants.rule_log_debug,
									bw);
							if (mapD.containsKey(tp.getServiceCode())) {
								FeeToothDto ft = mapD.get(tp.getServiceCode());
								List<String> l = ft.getTooth();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							} else {
								List<String> l = new ArrayList<>();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
								FeeToothDto ft = new FeeToothDto();
								ft.setFees(tp.getFee());
								ft.setTooth(l);
								mapD.put(tp.getServiceCode(), ft);
							}
						}

					}
					//Check for history
					int noOFhistory = Constants.history_codes_size;
					Class<?> c2;
					try {
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							//String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							//String hs = "getHistory" + i + "Surface";

							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							//Method hdm = c2.getMethod(hd);
							//Method hss = c2.getMethod(hs);
							String code = (String) hcm.invoke(hisSheet);
							String tooth = (String) htm.invoke(hisSheet);
							//|| hs_extraList.contains(code.toUpperCase())
							if (reqList.contains(code.toUpperCase()) ) {
								RuleEngineLogger.generateLogs(clazz, "i="+i+" -code H=" + code, Constants.rule_log_debug,
										bw);
								RuleEngineLogger.generateLogs(clazz, "Tooth H=" + tooth, Constants.rule_log_debug,
										bw);
								checkForMajorLogic = true;


								//History Check
								/*
								FeeToothDto ft2 = mapE.get(code);
								
								if (ft2!=null) {
									List<String> l = ft2.getTooth();
									for(String x:l) {
										if(x.equals(tooth)) {
											pass=false;
											dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule26.error.message", new Object[] { tooth }, locale),
													Constants.FAIL));
										}
										
									}
								}
								*/
								//Put in Map
								if (mapE.containsKey(code)) {
									FeeToothDto ft = mapE.get(code);
									List<String> l = ft.getTooth();
									l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
								} else {
									List<String> l = new ArrayList<>();
									l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
									FeeToothDto ft = new FeeToothDto();
									ft.setFees("0");
									ft.setTooth(l);
									mapE.put(code, ft);
								}
							}
						}
					}catch (Exception e) {
						// TODO: handle exception
					}
					//
					
					
					
					
					//ToothHistoryDto hdto=null;
					if (checkForMajorLogic) {
						try {
							//boolean breakAll=false;
							/*
							c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

							IVFHistorySheet hisSheet = ivf.getHs();
							
							for (int i = 1; i <= noOFhistory; i++) {
								String hc = "getHistory" + i + "Code";
								String hd = "getHistory" + i + "DOS";
								String ht = "getHistory" + i + "Tooth";
								String hs = "getHistory" + i + "Surface";

								Method hcm = c2.getMethod(hc);
								Method htm = c2.getMethod(ht);
								Method hdm = c2.getMethod(hd);
								Method hss = c2.getMethod(hs);
								String code = (String) hcm.invoke(hisSheet);
								if (reqList.contains(code.toUpperCase())) {
									historyPresent = true;
									hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
											(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
					              if (mapHistory.containsKey(code)) {
										List<ToothHistoryDto> t = mapHistory.get(code);
										t.add(hdto);
									} else {
										List<ToothHistoryDto> l = new ArrayList<>();
										l.add(hdto);
										mapHistory.put(code, l);
									}
								}
							}
							RuleEngineLogger.generateLogs(clazz, " History of Extraction found.- " + historyPresent,
									Constants.rule_log_debug, bw);
							*/
							/*
							if (historyPresent) {
								for (Map.Entry<String, List<ToothHistoryDto>> entry : mapHistory.entrySet()) {
									List<ToothHistoryDto> d=entry.getValue();
									
									for (Map.Entry<String, FeeToothDto> entry2 : mapD.entrySet()) {
										FeeToothDto fd=entry2.getValue();
										List<String> t=fd.getTooth();
										for(ToothHistoryDto hisd:d) {
											String [] a= ToothUtil.getToothsFromTooth(hisd.getHistoryTooth());
										    String[] objects =t.toArray(new String[0]);
								     		List<String> cm= ToothUtil.findCommonTooth( objects, a);
								     		if (cm!=null && cm.size()>0 ) {
								     		//if (t.contains(hisd.getHistoryTooth())) {
												//breakAll=true;
												dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
														messageSource.getMessage("rule28.error.message", new Object[] {hisd.getHistoryTooth()  }, locale),
														Constants.FAIL));
								
												//break;
										}
											//if (breakAll) break;
									}//FOR-hisd
									//if (breakAll) break;
								}//FOR -ft
							 }//FOR -ft
							}
							*/
								for (Map.Entry<String, FeeToothDto> entry : mapE.entrySet()) {// Extraction TP + History
									FeeToothDto d=entry.getValue();
									
									for (Map.Entry<String, FeeToothDto> entry2 : mapD.entrySet()) {//map D ==Filling/Sealant/Crown/Another Extraction no history
									if (entry.getKey().equals(entry2.getKey())) break;
										FeeToothDto fd=entry2.getValue();
										List<String> t=fd.getTooth();
										
										for(String hisd:d.getTooth()) {
											
											if (t.contains(hisd)) {
												pass=false;
												dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
														messageSource.getMessage("rule26.error.message", new Object[] { hisd }, locale),
														Constants.FAIL));
												//break;
										}
											//if (breakAll) break;
									}//FOR-hisd
									//if (breakAll) break;
								}//FOR -ft
							 }//FOR -ft
							
							if (pass) {
								dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule26.pass1.message", new Object[] {  }, locale),
										Constants.PASS));
				
							}
							
						}catch (Exception e) {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule.error.exception", new Object[] { e.getMessage() }, locale),
									Constants.FAIL));
									}
					}else {
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule27.pass1.message", new Object[] {  }, locale),
								Constants.PASS));
					}
				} catch (Exception ex) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
						Constants.FAIL));
			}
			return dList;
		}

	// Sealant Not Covered
	
	/**
	 * 
	 * @param tpList
	 * @param ivfSheet
	 * @param messageSource
	 * @param rule
	 * @param bw
	 * @return
	 */
	
	public List<TPValidationResponseDto> Rule27(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
			 BufferedWriter bw) {
			RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_27,
					Constants.rule_log_debug, bw);

			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			List<TPValidationResponseDto> dList = new ArrayList<>();

			Map<String, FeeToothDto> mapE = new HashMap<>();
			Map<String, FeeToothDto> mapD = new HashMap<>();
			boolean pass=true;
			//Map<String, List<ToothHistoryDto>> mapHistory = new HashMap<>();
			try {
				boolean checkForMajorLogic = false;
				//boolean historyPresent = false;

					
					List<String> reqList = new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(",")));
					reqList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(","))));
					reqList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_MM_SC.split(","))));
					
	                List<String> checkList = new ArrayList<String>(Arrays.asList(Constants.SEALANT_SC.split(",")));
	                
	                
	                //List<String> c1List = new ArrayList<String>(Arrays.asList(Constants.EXTRACTION_SC.split(",")));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(","))));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.CROWN_SC.split(","))));
	                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.SEALANT_SC.split(","))));
	                
	                
	                
					for (Object obj : tpList) {
						TreatmentPlan tp = (TreatmentPlan) obj;
						RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
								Constants.rule_log_debug, bw);
							
						if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
							||	tp.getEstInsurance().equals("0.0")) continue;

						if (reqList.contains(tp.getServiceCode().toUpperCase())) {
							RuleEngineLogger.generateLogs(clazz, "code R=" + tp.getServiceCode(), Constants.rule_log_debug,
									bw);
							RuleEngineLogger.generateLogs(clazz, "Tooth R=" + tp.getTooth(), Constants.rule_log_debug,
									bw);
							checkForMajorLogic = true;
							if (mapE.containsKey(tp.getServiceCode())) {
								FeeToothDto ft = mapE.get(tp.getServiceCode());
								List<String> l = ft.getTooth();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							} else {
								List<String> l = new ArrayList<>();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
								FeeToothDto ft = new FeeToothDto();
								ft.setFees(tp.getFee());
								ft.setTooth(l);
								mapE.put(tp.getServiceCode(), ft);
							}
						}
						
						if (mapE.get(tp.getServiceCode())==null && checkList.contains(tp.getServiceCode().toUpperCase())) {//Duplicate Codes
							//We avoid Same Extraction Code in the List
							RuleEngineLogger.generateLogs(clazz, "code C=" + tp.getServiceCode(), Constants.rule_log_debug,
									bw);
							RuleEngineLogger.generateLogs(clazz, "Tooth C=" + tp.getTooth(), Constants.rule_log_debug,
									bw);
							if (mapD.containsKey(tp.getServiceCode())) {
								FeeToothDto ft = mapD.get(tp.getServiceCode());
								List<String> l = ft.getTooth();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							} else {
								List<String> l = new ArrayList<>();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
								FeeToothDto ft = new FeeToothDto();
								ft.setFees(tp.getFee());
								ft.setTooth(l);
								mapD.put(tp.getServiceCode(), ft);
							}
						}

					}
					//Check for history
					int noOFhistory = Constants.history_codes_size;
					Class<?> c2;
					try {
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							//String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							//String hs = "getHistory" + i + "Surface";

							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							//Method hdm = c2.getMethod(hd);
							//Method hss = c2.getMethod(hs);
							String code = (String) hcm.invoke(hisSheet);
							String tooth = (String) htm.invoke(hisSheet);
							
							if (reqList.contains(code.toUpperCase())) {
								RuleEngineLogger.generateLogs(clazz, "i="+i+" -code H=" + code, Constants.rule_log_debug,
										bw);
								RuleEngineLogger.generateLogs(clazz, "Tooth H=" + tooth, Constants.rule_log_debug,
										bw);
								checkForMajorLogic = true;


								//History Check
								/*
								FeeToothDto ft2 = mapE.get(code);
								
								if (ft2!=null) {
									List<String> l = ft2.getTooth();
									for(String x:l) {
										if(x.equals(tooth)) {
											pass=false;
											dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule27.error.message", new Object[] { tooth }, locale),
													Constants.FAIL));
										}
										
									}
								}
								*/
								//Put in Map
								if (mapE.containsKey(code)) {
									FeeToothDto ft = mapE.get(code);
									List<String> l = ft.getTooth();
									l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
								} else {
									List<String> l = new ArrayList<>();
									l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
									FeeToothDto ft = new FeeToothDto();
									ft.setFees("0");
									ft.setTooth(l);
									mapE.put(code, ft);
								}
							}
						}
					}catch (Exception e) {
						// TODO: handle exception
					}
					//
					
					
					
					
					//ToothHistoryDto hdto=null;
					if (checkForMajorLogic) {
						try {
							//boolean breakAll=false;
							/*
							c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

							IVFHistorySheet hisSheet = ivf.getHs();
							
							for (int i = 1; i <= noOFhistory; i++) {
								String hc = "getHistory" + i + "Code";
								String hd = "getHistory" + i + "DOS";
								String ht = "getHistory" + i + "Tooth";
								String hs = "getHistory" + i + "Surface";

								Method hcm = c2.getMethod(hc);
								Method htm = c2.getMethod(ht);
								Method hdm = c2.getMethod(hd);
								Method hss = c2.getMethod(hs);
								String code = (String) hcm.invoke(hisSheet);
								if (reqList.contains(code.toUpperCase())) {
									historyPresent = true;
									hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
											(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
					              if (mapHistory.containsKey(code)) {
										List<ToothHistoryDto> t = mapHistory.get(code);
										t.add(hdto);
									} else {
										List<ToothHistoryDto> l = new ArrayList<>();
										l.add(hdto);
										mapHistory.put(code, l);
									}
								}
							}
							RuleEngineLogger.generateLogs(clazz, " History of Extraction found.- " + historyPresent,
									Constants.rule_log_debug, bw);
							*/
							/*
							if (historyPresent) {
								for (Map.Entry<String, List<ToothHistoryDto>> entry : mapHistory.entrySet()) {
									List<ToothHistoryDto> d=entry.getValue();
									
									for (Map.Entry<String, FeeToothDto> entry2 : mapD.entrySet()) {
										FeeToothDto fd=entry2.getValue();
										List<String> t=fd.getTooth();
										for(ToothHistoryDto hisd:d) {
											String [] a= ToothUtil.getToothsFromTooth(hisd.getHistoryTooth());
										    String[] objects =t.toArray(new String[0]);
								     		List<String> cm= ToothUtil.findCommonTooth( objects, a);
								     		if (cm!=null && cm.size()>0 ) {
								     		//if (t.contains(hisd.getHistoryTooth())) {
												//breakAll=true;
												dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
														messageSource.getMessage("rule28.error.message", new Object[] {hisd.getHistoryTooth()  }, locale),
														Constants.FAIL));
								
												//break;
										}
											//if (breakAll) break;
									}//FOR-hisd
									//if (breakAll) break;
								}//FOR -ft
							 }//FOR -ft
							}
							*/
								for (Map.Entry<String, FeeToothDto> entry : mapE.entrySet()) {// Extraction TP + History
									FeeToothDto d=entry.getValue();
									
									for (Map.Entry<String, FeeToothDto> entry2 : mapD.entrySet()) {//map D ==Filling/Sealant/Crown/Another Extraction no history
									if (entry.getKey().equals(entry2.getKey())) break;
										FeeToothDto fd=entry2.getValue();
										List<String> t=fd.getTooth();
										
										for(String hisd:d.getTooth()) {
											
											if (t.contains(hisd)) {
												pass=false;
												dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
														messageSource.getMessage("rule27.error.message", new Object[] { hisd }, locale),
														Constants.FAIL));
												//break;
										}
											//if (breakAll) break;
									}//FOR-hisd
									//if (breakAll) break;
								}//FOR -ft
							 }//FOR -ft
							
							if (pass) {
								dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule27.pass1.message", new Object[] {  }, locale),
										Constants.PASS));
				
							}
							
						}catch (Exception e) {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule.error.exception", new Object[] { e.getMessage() }, locale),
									Constants.FAIL));
									}
					}else {
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule27.pass1.message", new Object[] {  }, locale),
								Constants.PASS));
					}
				} catch (Exception ex) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
						Constants.FAIL));
			}
			return dList;
		}

	
	// Restoration Not Covered
	
    /**
     * 
     * @param tpList
     * @param ivfSheet
     * @param messageSource
     * @param rule
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule28(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
		 BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_28,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> dList = new ArrayList<>();

		Map<String, FeeToothDto> mapE = new HashMap<>();
		Map<String, FeeToothDto> mapD = new HashMap<>();
		boolean pass=true;
		//Map<String, List<ToothHistoryDto>> mapHistory = new HashMap<>();
		try {
			boolean checkForMajorLogic = false;
			//boolean historyPresent = false;

				
				List<String> reqList = new ArrayList<String>(Arrays.asList(Constants.EXTRACTION_SC.split(",")));
				
                List<String> checkList = new ArrayList<String>(Arrays.asList(Constants.EXTRACTION_SC.split(",")));
                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(","))));
                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_MM_SC.split(","))));
                
                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.CROWN_SC.split(","))));
                checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.SEALANT_SC.split(","))));
                
                //List<String> c1List = new ArrayList<String>(Arrays.asList(Constants.EXTRACTION_SC.split(",")));
                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(","))));
                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.CROWN_SC.split(","))));
                //c1List.addAll(new ArrayList<String>(Arrays.asList(Constants.SEALANT_SC.split(","))));
                
                
                
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

					if (reqList.contains(tp.getServiceCode().toUpperCase())) {
						RuleEngineLogger.generateLogs(clazz, "code R=" + tp.getServiceCode(), Constants.rule_log_debug,
								bw);
						RuleEngineLogger.generateLogs(clazz, "Tooth R=" + tp.getTooth(), Constants.rule_log_debug,
								bw);
						checkForMajorLogic = true;
						if (mapE.containsKey(tp.getServiceCode())) {
							FeeToothDto ft = mapE.get(tp.getServiceCode());
							List<String> l = ft.getTooth();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
						} else {
							List<String> l = new ArrayList<>();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							FeeToothDto ft = new FeeToothDto();
							ft.setFees(tp.getFee());
							ft.setTooth(l);
							mapE.put(tp.getServiceCode(), ft);
						}
					}
					
					if (mapE.get(tp.getServiceCode())==null && checkList.contains(tp.getServiceCode().toUpperCase())) {//Another Extraction check 
						//We avoid Same Extraction Code in the List
						RuleEngineLogger.generateLogs(clazz, "code C=" + tp.getServiceCode(), Constants.rule_log_debug,
								bw);
						RuleEngineLogger.generateLogs(clazz, "Tooth C=" + tp.getTooth(), Constants.rule_log_debug,
								bw);
						if (mapD.containsKey(tp.getServiceCode())) {
							FeeToothDto ft = mapD.get(tp.getServiceCode());
							List<String> l = ft.getTooth();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
						} else {
							List<String> l = new ArrayList<>();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
							FeeToothDto ft = new FeeToothDto();
							ft.setFees(tp.getFee());
							ft.setTooth(l);
							mapD.put(tp.getServiceCode(), ft);
						}
					}

				}
				//Check for history
				int noOFhistory = Constants.history_codes_size;
				Class<?> c2;
				try {
					c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

					IVFHistorySheet hisSheet = ivf.getHs();
					
					for (int i = 1; i <= noOFhistory; i++) {
						String hc = "getHistory" + i + "Code";
						//String hd = "getHistory" + i + "DOS";
						String ht = "getHistory" + i + "Tooth";
						//String hs = "getHistory" + i + "Surface";

						Method hcm = c2.getMethod(hc);
						Method htm = c2.getMethod(ht);
						//Method hdm = c2.getMethod(hd);
						//Method hss = c2.getMethod(hs);
						String code = (String) hcm.invoke(hisSheet);
						String tooth = (String) htm.invoke(hisSheet);
						
						if (reqList.contains(code.toUpperCase())) {
							RuleEngineLogger.generateLogs(clazz, "i="+i+" -code H=" + code, Constants.rule_log_debug,
									bw);
							RuleEngineLogger.generateLogs(clazz, "Tooth H=" + tooth, Constants.rule_log_debug,
									bw);
							checkForMajorLogic = true;


							//History Check
							FeeToothDto ft2 = mapE.get(code);
							
							if (ft2!=null) {
								List<String> l = ft2.getTooth();
								for(String x:l) {
									if(x.equals(tooth)) {
										pass=false;
										dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
												messageSource.getMessage("rule28.error.message", new Object[] { tooth }, locale),
												Constants.FAIL));
									}
									
								}
							}
							//Put in Map
							if (mapE.containsKey(code)) {
								FeeToothDto ft = mapE.get(code);
								List<String> l = ft.getTooth();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
							} else {
								List<String> l = new ArrayList<>();
								l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
								FeeToothDto ft = new FeeToothDto();
								ft.setFees("0");
								ft.setTooth(l);
								mapE.put(code, ft);
							}
						}
					}
				}catch (Exception e) {
					// TODO: handle exception
				}
				//
				
				
				
				
				//ToothHistoryDto hdto=null;
				if (checkForMajorLogic) {
					try {
						//boolean breakAll=false;
						/*
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							String hs = "getHistory" + i + "Surface";

							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							Method hdm = c2.getMethod(hd);
							Method hss = c2.getMethod(hs);
							String code = (String) hcm.invoke(hisSheet);
							if (reqList.contains(code.toUpperCase())) {
								historyPresent = true;
								hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistory.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistory.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistory.put(code, l);
								}
							}
						}
						RuleEngineLogger.generateLogs(clazz, " History of Extraction found.- " + historyPresent,
								Constants.rule_log_debug, bw);
						*/
						/*
						if (historyPresent) {
							for (Map.Entry<String, List<ToothHistoryDto>> entry : mapHistory.entrySet()) {
								List<ToothHistoryDto> d=entry.getValue();
								
								for (Map.Entry<String, FeeToothDto> entry2 : mapD.entrySet()) {
									FeeToothDto fd=entry2.getValue();
									List<String> t=fd.getTooth();
									for(ToothHistoryDto hisd:d) {
										String [] a= ToothUtil.getToothsFromTooth(hisd.getHistoryTooth());
									    String[] objects =t.toArray(new String[0]);
							     		List<String> cm= ToothUtil.findCommonTooth( objects, a);
							     		if (cm!=null && cm.size()>0 ) {
							     		//if (t.contains(hisd.getHistoryTooth())) {
											//breakAll=true;
											dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule28.error.message", new Object[] {hisd.getHistoryTooth()  }, locale),
													Constants.FAIL));
							
											//break;
									}
										//if (breakAll) break;
								}//FOR-hisd
								//if (breakAll) break;
							}//FOR -ft
						 }//FOR -ft
						}
						*/
							for (Map.Entry<String, FeeToothDto> entry : mapE.entrySet()) {// Extraction TP + History
								FeeToothDto d=entry.getValue();
								
								for (Map.Entry<String, FeeToothDto> entry2 : mapD.entrySet()) {//map D ==Filling/Sealant/Crown/Another Extraction no history
								if (entry.getKey().equals(entry2.getKey())) break;
									FeeToothDto fd=entry2.getValue();
									List<String> t=fd.getTooth();
									
									for(String hisd:d.getTooth()) {
										
										if (t.contains(hisd)) {
											pass=false;
											dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
													messageSource.getMessage("rule28.error.message", new Object[] { hisd }, locale),
													Constants.FAIL));
											//break;
									}
										//if (breakAll) break;
								}//FOR-hisd
								//if (breakAll) break;
							}//FOR -ft
						 }//FOR -ft
						
						if (pass) {
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule28.pass1.message", new Object[] {  }, locale),
									Constants.PASS));
			
						}
						
					}catch (Exception e) {
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule.error.exception", new Object[] { e.getMessage() }, locale),
								Constants.FAIL));
								}
				}else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule28.pass1.message", new Object[] {  }, locale),
							Constants.PASS));
				}
			} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;
	}
	
	// Exams Limitation
	/**
	 * 
	 * @param tpList
	 * @param ivfSheet
	 * @param messageSource
	 * @param rule
	 * @param bw
	 * @return
	 */
	
	public List<TPValidationResponseDto> Rule29(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
			 BufferedWriter bw) {
		
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_29,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String  planType= ivf.getPlanType();
		List<TPValidationResponseDto> dList = new ArrayList<>();
		List<String> codes= new ArrayList<>();
		RuleEngineLogger.generateLogs(clazz,
				"planType-"+planType,
				Constants.rule_log_debug, bw);
		
			int  counter=0;
		try {	
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D0150") || tp.getServiceCode().equalsIgnoreCase("D0120") || 
				    tp.getServiceCode().equalsIgnoreCase("D0145") || tp.getServiceCode().equalsIgnoreCase("D0140") ) {
					RuleEngineLogger.generateLogs(clazz,"code ="+ tp.getServiceCode(),Constants.rule_log_debug, bw);
					counter =counter+  1;
					codes.add(tp.getServiceCode());
				}
				
			}
			
			if (counter> 1) {
				RuleEngineLogger.generateLogs(clazz,
						" Rule Fails  => No. of Exam present ="+counter ,
						Constants.rule_log_debug, bw);
				
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule29.error.message",
								new Object[] { String.join(",", codes) },
								locale),
						Constants.FAIL));
				
			}else if(counter==1) {
			RuleEngineLogger.generateLogs(clazz,
					" BY pass the rule ",
					Constants.rule_log_debug, bw);
			
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule29.pass.message", new Object[] {String.join(",", codes) }, locale),
					Constants.PASS));

			 }else {
					RuleEngineLogger.generateLogs(clazz,
							" BY pass the rule ",
							Constants.rule_log_debug, bw);
					
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule29.pass2.message", new Object[] { }, locale),
							Constants.PASS));

					 }
			} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
			}	
			return dList;
				
		

	}

	// Cleaning Limitation
	
    /**
     * 
     * @param tpList
     * @param ivfSheet
     * @param messageSource
     * @param rule
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule30(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
			 BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_30,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		String  planType= ivf.getPlanType();
		List<TPValidationResponseDto> dList = new ArrayList<>();
		List<String> codes= new ArrayList<>();
		Map<String,List<String>> codesTeethMap= new HashMap<>();
		boolean pass=true;
		RuleEngineLogger.generateLogs(clazz,
				"planType-"+planType,
				Constants.rule_log_debug, bw);
		
			int  counter=0;
			try {
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				//|| tp.getServiceCode().equalsIgnoreCase("D4341") ||tp.getServiceCode().equalsIgnoreCase("D4342") -->These Two are exceptions
				if (tp.getServiceCode().equalsIgnoreCase("D4346")  || 
				     tp.getServiceCode().equalsIgnoreCase("D1110")  ||
				    tp.getServiceCode().equalsIgnoreCase("D1120") || tp.getServiceCode().equalsIgnoreCase("D4910") || 
					 tp.getServiceCode().equalsIgnoreCase("D4355")) {
						RuleEngineLogger.generateLogs(clazz,"code ="+ tp.getServiceCode(),Constants.rule_log_debug, bw);
					counter =counter+  1;
					codes.add(tp.getServiceCode());
				}
				if (tp.getServiceCode().equalsIgnoreCase("D4341") ||tp.getServiceCode().equalsIgnoreCase("D4342")) {
					if (codesTeethMap.containsKey(tp.getServiceCode())) {
						List<String> x = codesTeethMap.get(tp.getServiceCode());
						x.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
					} else {
						List<String> x = new ArrayList<>();
						x.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
						codesTeethMap.put(tp.getServiceCode(), x);
					}
					
					
				}
				
			}
			
			for (Map.Entry<String,List<String>> entry : codesTeethMap.entrySet())  {
				Set<String> set = new LinkedHashSet<>(); 
				List<String> list= entry.getValue();
				set.addAll(list); 
		        if (set.size()!=list.size()) {
		        	RuleEngineLogger.generateLogs(clazz,
							" For -"+entry.getKey()+" common Quad present" +set,
							Constants.rule_log_debug, bw);
		        	pass=false;
		        	codes.add(entry.getKey());
		        }
		       
		       
			}
			
			if (counter> 1 || pass==false) {
				RuleEngineLogger.generateLogs(clazz,
						" Rule Fails  => No. of Cleaning Codes  present ="+counter ,
						Constants.rule_log_debug, bw);
				
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule30.error.message",
								new Object[] { String.join(",", codes) },
								locale),
						Constants.FAIL));
				
			}else if (counter== 1 && pass) {
			RuleEngineLogger.generateLogs(clazz,
					" Pass the rule ",
					Constants.rule_log_debug, bw);
			
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule30.pass.message", new Object[] {String.join(",", codes) }, locale),
					Constants.PASS));

			 }else {
					RuleEngineLogger.generateLogs(clazz,
							" Pass the rule ",
							Constants.rule_log_debug, bw);
					
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule30.pass2.message", new Object[] {}, locale),
							Constants.PASS));

					 }
			} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
			}
			return dList;
				
		

	}

	// Perio Maintainance Clause

	/**
	 * 
	 * @param tpList
	 * @param ivfSheet
	 * @param messageSource
	 * @param rule
	 * @param bw
	 * @return
	 */
	public List<TPValidationResponseDto> Rule31(List<Object> tpList,Object ivfSheet, MessageSource messageSource, Rules rule,
			 BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_31,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> dList = new ArrayList<>();
			int  counter_1=0;
			int  counter_2=0;
			try {
				Date TP_DATE= new Date();
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				
				if (tp.getServiceCode().equalsIgnoreCase("D4910")) {
						RuleEngineLogger.generateLogs(clazz,"code ="+ tp.getServiceCode(),Constants.rule_log_debug, bw);
					//Check history
						
						
						int noOFhistory = Constants.history_codes_size;
						Class<?> c2;
						try {
							c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");
						
						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							String hd = "getHistory" + i + "DOS";
							Method hdm = c2.getMethod(hd);
							boolean fdate=false;
							String dt=(String) hdm.invoke(hisSheet);
							Date dos = null;
							if (!dt.equals("")) {
							try {
								dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(dt);
								RuleEngineLogger.generateLogs(clazz,
										"History DOS-" + dt,
										Constants.rule_log_debug, bw);
							} catch (ParseException e2) {
								// TODO Auto-generated catch block
								//e2.printStackTrace();
							}
                           //ignore Future dates
							if (dos!=null) {
								fdate=DateUtils.compareDates(TP_DATE,dos );
							}
						    }
							Method hcm = c2.getMethod(hc);
							String code=(String) hcm.invoke(hisSheet);
							
							if (code.equalsIgnoreCase("D4341") && fdate) {
								
								RuleEngineLogger.generateLogs(clazz,"History code ="+ code,Constants.rule_log_debug, bw);
					    	    counter_1++;		
							}else if (code.equalsIgnoreCase("D4342") && fdate) {
						        counter_2++;	
						        RuleEngineLogger.generateLogs(clazz,"History code ="+ code,Constants.rule_log_debug, bw);
								}
							
							//
						}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 // For end
						
				}
				
			}
			
			if (counter_1>= 1 || counter_2>= 1) {
				
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule31.pass.message",
								new Object[] {},
								locale),
						Constants.PASS));
				
			}else {
			RuleEngineLogger.generateLogs(clazz,
					" Fail the Rule ",
					Constants.rule_log_debug, bw);
			
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule31.error.message", new Object[] { }, locale),
					Constants.FAIL));

			}
			 } catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
			}
			return dList;
				
		

	}


	// SRP Limitation
	/**
	 * 
	 * @param ivfSheet
	 * @param messageSource
	 * @param rule
	 * @param tpList
	 * @param bw
	 * @return
	 */
	public List<TPValidationResponseDto> Rule32(List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_32,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> dList = new ArrayList<>();
		String toothTR1[] = null;
		String toothTR2[] = null;
		try {
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D4341")) {
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					toothTR1 = ToothUtil.getQuadToothsFromTooth(tp.getTooth());

				}
				if (tp.getServiceCode().equalsIgnoreCase("D4342")) {
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					toothTR2 = ToothUtil.getQuadToothsFromTooth(tp.getTooth());

				}
			}
			if (toothTR1 == null && toothTR2 == null) {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule32.pass2.message", new Object[] {}, locale), Constants.PASS));

			} else if (toothTR1 == null || toothTR2 == null) {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule32.pass3.message", new Object[] {}, locale), Constants.PASS));

			} else if (toothTR1 != null && toothTR2 != null) {
				List<String> li = new ArrayList<>();

				// Remove Duplicates from Tooth
				Set<String> set1 = new LinkedHashSet<>();
				Set<String> set2 = new LinkedHashSet<>();

				// Add the elements to set
				set1.addAll(Arrays.asList(toothTR1));
				set2.addAll(Arrays.asList(toothTR2));

				for (String toothTR11 : set1) {
					for (String toothTR12 : set2) {
						if (toothTR11.equals(toothTR12))
							li.add(toothTR12);
					}
				}

				if (li.size() == set1.size() && set1.size() == set2.size()) {
					// FAIL
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
							.getMessage("rule32.error.message", new Object[] { StringUtils.join(li, ',') }, locale),
							Constants.FAIL));
				} else {
					// PASS
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule32.pass1.message", new Object[] {}, locale), Constants.PASS));

				}

			}
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}

	// Root Canal Clause
	public List<TPValidationResponseDto> Rule33(List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_33,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> dList = new ArrayList<>();
        List<String> checkList = new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(",")));
        checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
        checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_MM_SC.split(","))));
        
        int [] minMaxEndo= new int[2];
        minMaxEndo[0]=3000;
        minMaxEndo[1]=3999;
        List<String> toothFILL= null;
        List<String> toothENDO= null;
        Map<String,List<String>> toothFILLM= null; 
        Map<String,List<String>> toothENDOM= null; 
        boolean pass=true;
		try {
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (checkList.contains(tp.getServiceCode())) {
					toothFILL= new ArrayList<>();
					toothFILL.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
	                if (toothFILLM==null) toothFILLM= new HashMap<>();
	                if (toothFILLM.containsKey(tp.getServiceCode())) {
	                 List<String>	t =toothFILLM.get(tp.getServiceCode());
	                 t.addAll(toothFILL);
	                }else {
	                	toothFILLM.put(tp.getServiceCode(), toothFILL);
	                }
				}
				
				
				if (tp.getServiceCode().substring(0, 1).equalsIgnoreCase("D") && 
				    (		
				    		minMaxEndo[0]<= Integer.parseInt(tp.getServiceCode().substring(1,  tp.getServiceCode().length())) && 
							minMaxEndo[1]>= Integer.parseInt(tp.getServiceCode().substring(1,  tp.getServiceCode().length()))
							
			        )) {
					toothENDO= new ArrayList<>();
					toothENDO.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
	                if (toothENDOM==null) toothENDOM= new HashMap<>();
	                if (toothENDOM.containsKey(tp.getServiceCode())) {
	                 List<String>	t =toothENDOM.get(tp.getServiceCode());
	                 t.addAll(toothENDO);
	                }else {
	                	toothENDOM.put(tp.getServiceCode(), toothENDO);
	                }
				}
			}
			if (toothFILLM!=null && toothENDOM!=null &&toothFILLM.size()>0 && toothENDOM.size()>0) {
				
				for (Map.Entry<String,List<String>> entryFM : toothFILLM.entrySet()) { 
		            for(String t1:entryFM.getValue()) {
					    for (Map.Entry<String,List<String>> entryEM : toothENDOM.entrySet()) { 
				           for(String t2:entryEM.getValue()) {
							if (t2.equalsIgnoreCase(t1)) {
								//FAIL break;
								pass=false;
								dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule33.error.message", new Object[] {
												entryFM.getKey(),entryEM.getKey(), t2}, locale), Constants.FAIL));
								
							}
							
						}
						
					
					 }
					}//FOR TOOTH inner
				
				}
				if (pass) {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule33.pass2.message", new Object[] {}, locale), Constants.PASS));
				}
			} else{
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule33.pass1.message", new Object[] {}, locale), Constants.PASS));

			}
			
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	
	
	// D2954 Clause
    /**
     * 
     * @param tpList
     * @param messageSource
     * @param rule
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule34(List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_34,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> dList = new ArrayList<>();
		String toothTR1[] = null;
		String toothTR2[] = null;
		try {
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D2954")) {
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					toothTR1 = ToothUtil.getToothsFromTooth(tp.getTooth());
				}
				if (tp.getServiceCode().equalsIgnoreCase("D2950")) {
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					toothTR2 = ToothUtil.getToothsFromTooth(tp.getTooth());
				}
			}
			if (toothTR1 == null && toothTR2 == null) {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule34.pass2.message", new Object[] {}, locale), Constants.PASS));

			} else if (toothTR1 == null || toothTR2 == null) {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule34.pass3.message", new Object[] {}, locale), Constants.PASS));

			} else if (toothTR1 != null && toothTR2 != null) {
				List<String> li = new ArrayList<>();

				// Remove Duplicates from Tooth
				Set<String> set1 = new LinkedHashSet<>();
				Set<String> set2 = new LinkedHashSet<>();

				// Add the elements to set
				set1.addAll(Arrays.asList(toothTR1));
				set2.addAll(Arrays.asList(toothTR2));

				for (String toothTR11 : set1) {
					for (String toothTR12 : set2) {
						if (toothTR11.equals(toothTR12))
							li.add(toothTR12);
					}
				}

				if (li.size() == set1.size() && set1.size() == set2.size()) {
					// FAIL
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
							.getMessage("rule34.error.message", new Object[] { StringUtils.join(li, ',') }, locale),
							Constants.FAIL));
				} else {
					// PASS
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule34.pass1.message", new Object[] {}, locale), Constants.PASS));

				}

			}
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	

	// Bone Graft Rule
    /**
     * 
     * @param tpList
     * @param messageSource
     * @param rule
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule35(List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_35,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> dList = new ArrayList<>();
		List<String> teethList = new ArrayList<>();
		try {
			int counter=0;
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D7953") || tp.getServiceCode().equalsIgnoreCase("D7310") ||
						tp.getServiceCode().equalsIgnoreCase("D7311")||tp.getServiceCode().equalsIgnoreCase("D7320") ||	
						tp.getServiceCode().equalsIgnoreCase("D7321")) {
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					teethList.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
					counter++;
				}
			}
			if (counter>1) {
				RuleEngineLogger.generateLogs(clazz, " Fail the rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule35.error.message", new Object[] {String.join(",", teethList)}, locale), Constants.FAIL));

			} else if (counter==1) {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule35.pass1.message", new Object[] {}, locale), Constants.PASS));

			} else  {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule35.pass2.message", new Object[] {}, locale), Constants.PASS));
				
			}

		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	
	// Immediate Denture
    /**
     * 
     * @param tpList
     * @param ivfSheet
     * @param messageSource
     * @param rule
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule36(List<Object> tpList,Object ivfSheet,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_36,
				Constants.rule_log_debug, bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> dList = new ArrayList<>();
		List<String> dcodes = new ArrayList<>();
	    boolean pass=true;
		try {
			boolean checkForHistory=false;
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D5130") || tp.getServiceCode().equalsIgnoreCase("D5140")) {
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "Check for History", Constants.rule_log_debug, bw);
					checkForHistory=true;
					dcodes.add(tp.getServiceCode());
				}
			}
			if (checkForHistory) {
				int noOFhistory = Constants.history_codes_size;
				Class<?> c2;
				try {
					c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");
				
				IVFHistorySheet hisSheet = ivf.getHs();
				
				for (int i = 1; i <= noOFhistory; i++) {
					String hc = "getHistory" + i + "Code";
					String ht = "getHistory" + i + "Tooth";
					//String hs = "getHistory" + i + "Surface";
					
					Method hcm = c2.getMethod(hc);
					Method htm = c2.getMethod(ht);
					//Method hss = c2.getMethod(hs);
					String hcode=(String) hcm.invoke(hisSheet);
					String tooth=(String) htm.invoke(hisSheet);
					//D5110,D5120, D5130, D5140
					if ((hcode.equalsIgnoreCase("D5110") && dcodes.contains("D5130")) 
						                                  ||
						(hcode.equalsIgnoreCase("D5120") && dcodes.contains("D5140"))) {
						RuleEngineLogger.generateLogs(clazz, "code HISTORY=" + hcode +"("+tooth+")", Constants.rule_log_debug, bw);
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule36.error.message", new Object[] {hcode}, locale), Constants.FAIL));
						pass=false;
				     }
				 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(pass) {
					RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule36.pass2.message", new Object[] {}, locale), Constants.PASS));
				}


			} else  {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule36.pass2.message", new Object[] {}, locale), Constants.PASS));
				
			}

		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	
	
	// Extraction Limitation
    /**
     * 
     * @param tpList
     * @param ivfSheet
     * @param messageSource
     * @param rule
     * @param bw
     * @return
     */
	public List<TPValidationResponseDto> Rule37(List<Object> tpList,Object ivfSheet,MessageSource messageSource, Rules rule, 
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_37,
				Constants.rule_log_debug, bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> dList = new ArrayList<>();
		//List<String> dcodes = new ArrayList<>();
	    Map<String,List<String>> mapE = new HashMap<>();
	    //Map<String,List<String>> mapD = new HashMap<>();
	    Map<String,List<String>> mapHistory = new HashMap<>();
		try {
			boolean checkForHistory=false;
			boolean denturePresent=false;
			boolean historyPresent=false;
			
			
			List<String> extList= Arrays.asList( Constants.EXTRACTION_SC.split(","));
			List<String> dentureList= Arrays.asList( Constants.DENTURE_SC.split(","));
			
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (extList.contains(tp.getServiceCode().toUpperCase())) {
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "Check for History", Constants.rule_log_debug, bw);
					checkForHistory=true;
					if (mapE.containsKey(tp.getServiceCode())) {
						List<String> t=mapE.get(tp.getServiceCode());
						t.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
					}else {
						List<String> l= new ArrayList<>();
						l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
						mapE.put(tp.getServiceCode(), l);
					}
					//dcodes.add(tp.getServiceCode());
				}
				/*
				if (dentureList.contains(tp.getServiceCode().toUpperCase())) {
					denturePresent=true;
					RuleEngineLogger.generateLogs(clazz, "code =" + tp.getServiceCode(), Constants.rule_log_debug, bw);
					if (mapD.containsKey(tp.getServiceCode())) {
						List<String> t=mapD.get(tp.getServiceCode());
						t.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
					}else {
						List<String> l= new ArrayList<>();
						l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
						mapD.put(tp.getServiceCode(), l);
					}
					//dcodes.add(tp.getServiceCode());
				}
				*/
				
			}
			if (checkForHistory) {// && denturePresent
				int noOFhistory = Constants.history_codes_size;
				Class<?> c2;
				try {
					c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");
				
				IVFHistorySheet hisSheet = ivf.getHs();
				List<String> allExtractionTooth=new ArrayList<>();
				//List<String> allDentureTooth=new ArrayList<>();
				List<String> allDentureToothHis=new ArrayList<>();
				
				for (int i = 1; i <= noOFhistory; i++) {
					String hc = "getHistory" + i + "Code";
					String ht = "getHistory" + i + "Tooth";
					Method hcm = c2.getMethod(hc);
					Method htm = c2.getMethod(ht);
					String code=(String) hcm.invoke(hisSheet);
					String tooth=(String) htm.invoke(hisSheet);
					tooth=tooth.split("-")[0];
					
					if (dentureList.contains(code.toUpperCase())) {
						historyPresent=true;
						if (mapHistory.containsKey(code)) {
							List<String> t=mapHistory.get(code);
							t.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
						}else {
							List<String> l= new ArrayList<>();
							l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tooth)));
							mapHistory.put(code, l);
						/*	
						*/
				      }
					}
				}
				RuleEngineLogger.generateLogs(clazz, " History of Denture found.- "+historyPresent, Constants.rule_log_debug, bw);
				
				if (historyPresent) {
					//Get TOOTH from map Extraction
					for (Map.Entry<String, List<String>> entry : mapE.entrySet()) {
						allExtractionTooth.addAll(entry.getValue());
					}
					
					//Get TOOTH from map Denture
					/*
					for (Map.Entry<String, List<String>> entry : mapD.entrySet()) {
						allDentureTooth.addAll(entry.getValue());
					}
					*/
					//Get TOOTH from map Denture history
					String UA="1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16";
					String LA="17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37";
					for (Map.Entry<String, List<String>> entry : mapHistory.entrySet()) {
						//allDentureToothHis.addAll(entry.getValue());//As per Sahil => we need to match tooth number, for UA - 1-16, for LA - 17-32
						List<String> th=entry.getValue();
						if (th!=null) {
							for(String x:th) {
								if (x.equalsIgnoreCase("UA")) allDentureToothHis.addAll(
										new ArrayList<String>(Arrays.asList(UA.split(","))));
								else if (x.equalsIgnoreCase("LA")) allDentureToothHis.addAll(
										new ArrayList<String>(Arrays.asList(LA.split(",")))); 
							}
						}
					}
					
					//Collection<String> allDentureToothC = new ArrayList<>(allExtractionTooth);
					//Collection<String> allDentureToothHisC = new ArrayList<>(allDentureToothHis);
					//List<String> allDentureToothCopy = new ArrayList<String>(allDentureTooth);
					//List<String> allDentureToothHisCopy = new ArrayList<String>(allDentureToothHis);
					List<String> common = allExtractionTooth.stream().filter(allDentureToothHis::contains).collect(Collectors.toList());
					//allDentureToothHisCopy.removeAll(allDentureToothC);
					//means Already history for same teeth
					List<String> docodesforDisplay=new ArrayList<>();
					RuleEngineLogger.generateLogs(clazz, " extraction  tooth for current TP- "+String.join(",",allExtractionTooth), Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, " Denture  tooth for HISTORY -"+String.join(",",allDentureToothHis), Constants.rule_log_debug, bw);
					
					if (common!=null && common.size()>0) {
						//rule will fail
						for(String t:common) {
							for (Map.Entry<String, List<String>> entry : mapHistory.entrySet()) {
								List<String> tList=entry.getValue();
								List<String> tListnew=new ArrayList<>();
								
								if (tList!=null) {
									for(String x:tList) {
										if (x.equalsIgnoreCase("UA")) tListnew.addAll(
												new ArrayList<String>(Arrays.asList(UA.split(","))));
										else if (x.equalsIgnoreCase("LA")) tListnew.addAll(
												new ArrayList<String>(Arrays.asList(LA.split(",")))); 
									}
								}
								
								
								for(String t2:tListnew) {
									if (t2.equals(t)) {
										docodesforDisplay.add(entry.getKey());
									}
								}
							}
									
						}
						RuleEngineLogger.generateLogs(clazz, " Fail the rule ", Constants.rule_log_debug, bw);
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule37.error.message", new Object[] {String.join(",", docodesforDisplay)}, locale), Constants.FAIL));

					}else {
						RuleEngineLogger.generateLogs(clazz, " No history for denture for Same tooth.", Constants.rule_log_debug, bw);
						dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule37.pass2.message", new Object[] {}, locale), Constants.PASS));
							
					}
					
				}else {
					//No HISTORY for denture found
					RuleEngineLogger.generateLogs(clazz, " No history for denture.", Constants.rule_log_debug, bw);
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule37.pass2.message", new Object[] {}, locale), Constants.PASS));
						
				}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			} else  {
				if (checkForHistory && !denturePresent) {
				RuleEngineLogger.generateLogs(clazz, " Pass the Rule ", Constants.rule_log_debug, bw);
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule37.pass1.message", new Object[] {}, locale), Constants.PASS));
				}else if((!checkForHistory && denturePresent) && (!checkForHistory && !denturePresent)) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule37.pass3.message", new Object[] {}, locale), Constants.PASS));
						
				}
			}

		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}

    // Medicaid Provider Limitation for D0150,D0210,D0330
	/**
	 * 
	 * @param ivfSheet
	 * @param messageSource
	 * @param rule
	 * @param tpList
	 * @param pHistories
	 * @param bw
	 * @return
	 */
	public List<TPValidationResponseDto> Rule38(Object ivfSheet,List<Object> tpList,List<EagleSoftPatientWalkHistory> pHistories,
			MessageSource messageSource, Rules rule,
			 BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_38,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> dList = new ArrayList<>();

		String planType = ivf.getPlanType();
		String cMedicate = Constants.insurance_Medicaid;
		String chip = Constants.insurance_Chip;
		
		boolean pass = true;
		Map<String, List<ToothHistoryDto>> mapHistory = new HashMap<>();
		Date TP_DATE = new Date();
		String codes="D0150,D0210,D0330";
		try {
			boolean present = false;
			if (planType != null && 
					(planType.trim().toLowerCase().contains(cMedicate) || planType.trim().toLowerCase().contains(chip)) 
				) {
				
				List<String> reqList = new ArrayList<String>(Arrays.asList(codes.split(",")));
				List<String> reqListNew = new ArrayList<>();
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

					//present=false;

					if (reqList.contains(tp.getServiceCode().toUpperCase())) {
						RuleEngineLogger.generateLogs(clazz, " Service Code -"+tp.getServiceCode(), Constants.rule_log_debug, bw);
						present=true;
						//break;
						reqListNew.add(tp.getServiceCode());
				 }
				}
				if (present) {
					//check in ES history
					 if (pHistories!=null && pHistories.size()>0) {
						 for(EagleSoftPatientWalkHistory phis :pHistories) {
							 RuleEngineLogger.generateLogs(clazz, " History Service Code -"+phis.getServiceCode()+" Provider -"+ivf.getProviderName()+
							  " Provider ES -"+phis.getLastNameP(), Constants.rule_log_debug, bw);
							 if (reqListNew.contains(phis.getServiceCode().toUpperCase()) && phis.getLastNameP().trim().equalsIgnoreCase(ivf.getProviderName().trim())) { 
								 //fail as Provider is same
								 pass=false;
								 RuleEngineLogger.generateLogs(clazz, " Fail ", Constants.rule_log_debug, bw);
								 dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule38.error.message", new Object[] { codes,phis.getServiceCode()}, locale),
											Constants.FAIL));
							 }
						 }
					 }
					 
					 if (pass) {
						 ToothHistoryDto hdto=null;
						 int noOFhistory = Constants.history_codes_size;
							Class<?> c2;
							try {
								c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

								IVFHistorySheet hisSheet = ivf.getHs();
								
								for (int i = 1; i <= noOFhistory; i++) {
									String hc = "getHistory" + i + "Code";
									String hd = "getHistory" + i + "DOS";
									String ht = "getHistory" + i + "Tooth";
									String hs = "getHistory" + i + "Surface";
									
									Method hcm = c2.getMethod(hc);
									Method htm = c2.getMethod(ht);
									Method hdm = c2.getMethod(hd);
									Method hss = c2.getMethod(hs);

									String code = (String) hcm.invoke(hisSheet);
									
									if (reqList.contains(code.toUpperCase())) {
										RuleEngineLogger.generateLogs(clazz, " History Service Code -"+code, Constants.rule_log_debug, bw);
										hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
												(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
						              if (mapHistory.containsKey(code)) {
											List<ToothHistoryDto> t = mapHistory.get(code);
											t.add(hdto);
										} else {
											List<ToothHistoryDto> l = new ArrayList<>();
											l.add(hdto);
											mapHistory.put(code, l);
										}
									}
									
									
							 }
								if (mapHistory!=null && mapHistory.size()>0) {
									//Check for Date in 36 Months
									for (Map.Entry<String, List<ToothHistoryDto>> entry : mapHistory.entrySet()) {
										List<ToothHistoryDto> d=entry.getValue();
										for(ToothHistoryDto dto:d) {
											if (dto.getHistoryDos()!="") {
												Date dos = null;
												try {
													dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(dto.getHistoryDos());
													RuleEngineLogger.generateLogs(clazz,
															"History DOS-" + dto.getHistoryDos(),
															Constants.rule_log_debug, bw);
										           if ( !DateUtils.checkforXm(TP_DATE, dos, 36)) {		
										        	pass=false;
										            RuleEngineLogger.generateLogs(clazz,
															"RULE FAILS-" + TP_DATE+" DOS history- "+dto.getHistoryDos(),
															Constants.rule_log_debug, bw);
													 dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
																messageSource.getMessage("rule38.error.message", new Object[] { codes,dto.getHistoryCode()}, locale),
																Constants.FAIL));
										           }
												} catch (ParseException e2) {
													// TODO Auto-generated catch block
													e2.printStackTrace();
												}
											}
										}
									}	
								}
										
								
							}catch (Exception e) {
								dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule.error.exception", new Object[] { e.getMessage() }, locale),
										Constants.FAIL));
										}
						 
					 }
					 if(pass) {
						 dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule38.pass2.message", new Object[] { codes }, locale),
									Constants.PASS));
					 }
					
				}else {
					//pass
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule38.pass3.message", new Object[] { codes }, locale),
							Constants.PASS));
				}
			}else {
				//not medicaid
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule38.pass4.message", new Object[] { planType }, locale),
						Constants.PASS));
			}
					///
			} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;
	}
	
	// Age Limitation Prophylaxis_D1110/D1120

	public List<TPValidationResponseDto> Rule39(Object ivfSheet, List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_39,
				Constants.rule_log_debug, bw);
		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {
			
			
            boolean needCheck=false;
            String tpCode="";
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D1110") || tp.getServiceCode().equalsIgnoreCase("D1120")) {
					needCheck=true;
					tpCode=tp.getServiceCode();
					RuleEngineLogger.generateLogs(clazz, "TP Code- "+tpCode,
							Constants.rule_log_debug, bw);
								
				}
				
			}
			if (needCheck) {
				int rollAge =0;
				RuleEngineLogger.generateLogs(clazz,"Roll Age IVF -"+ivf.getName1201110RollOverAgYe(),
						Constants.rule_log_debug, bw);
						try {
					rollAge= Integer.parseInt(ivf.getName1201110RollOverAgYe().trim());
				}catch (Exception e) {
					RuleEngineLogger.generateLogs(clazz,"Roll Age IVF set as 99",
							Constants.rule_log_debug, bw);
					rollAge=99;	
					// TODO: handle exception
				}
						int[] age = null;
						String dob = ivf.getPatientDOB();
						try {
							age = DateUtils.calculateAgeYMD(dob, true);
							RuleEngineLogger.generateLogs(clazz, "Date of Birth-" + dob, Constants.rule_log_debug, bw);
							RuleEngineLogger.generateLogs(clazz,
									"Age- " + age[0] + " Years, " + age[1] + " Months & " + age[2] + " Days",
									Constants.rule_log_debug, bw);
							if (age[0]<rollAge) {
							if (tpCode.equalsIgnoreCase("D1120")) {	
								//Correct Code
								dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule39.pass2.message", new Object[] {  }, locale),
										Constants.PASS));
							}else {
							    //in-correct code	
								dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
										messageSource.getMessage("rule39.error.message", new Object[] { tpCode,rollAge }, locale),
										Constants.FAIL));
							 }
							}else {
								if (tpCode.equalsIgnoreCase("D1110")) {	
								//correct code
									dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule39.pass2.message", new Object[] {  }, locale),
											Constants.PASS));
								}else {
									//incorrect code
									dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
											messageSource.getMessage("rule39.error.message", new Object[] { tpCode,rollAge }, locale),
											Constants.FAIL));
								}
								
							}
						} catch (ParseException e) {
							RuleEngineLogger.generateLogs(clazz, "Date of Birth-" + dob, Constants.rule_log_debug, bw);
							dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule1.error.message.date", new Object[] { dob }, locale),
									Constants.FAIL));
							return dList;
						}
				

			}else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule39.pass1.message", new Object[] {  }, locale),
						Constants.PASS));
			}

		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}

	// Space Maintainer-Billateral_D1515 Need to bill on Arch
	//40
	public List<TPValidationResponseDto> Rule40(List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_40,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		try {
			
			String[] codes=new String[2];
			String[] tooths=new String[2];
			codes[0]=codes[1]="";
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D1515")) {
					codes[0]="D1515";
					tooths[0]=tp.getTooth();
								
				}else if (tp.getServiceCode().equalsIgnoreCase("D1510")) {
					codes[1]="D1510";
					tooths[1]=tp.getTooth();
				}
				
			}
			List<String> issueTeeth= null;
			List<String> normalteeth= null;
			Set<String> correctBiller=null;
			
			RuleEngineLogger.generateLogs(clazz, "TP Code- "+codes[0]+"-"+codes[1],
					Constants.rule_log_debug, bw);

			if (codes[0]!=null && codes[0].equalsIgnoreCase("D1515")) {
				   String[] tooth= ToothUtil.getToothsFromTooth(tooths[0]);
				   for(String t: tooth) {
					   if (t.equalsIgnoreCase("LA") || t.equalsIgnoreCase("UA")) {
						   //pass
						   if (normalteeth==null) normalteeth=new ArrayList<>();
						   normalteeth.add(t);
					   }else {
						   pass=false;		
						   if (issueTeeth==null) issueTeeth=new ArrayList<>();
						   if (correctBiller==null) correctBiller=new HashSet<>();
						   issueTeeth.add(t);
						   correctBiller.add("(LA");
						   correctBiller.add("UA)");
						   
					   }
				   }
			} 
			if (codes[1]!=null &&  codes[1].equalsIgnoreCase("D1510")) {
				   String[] tooth= ToothUtil.getToothsFromTooth(tooths[1]);
				   for(String t: tooth) {
					   if (t.equalsIgnoreCase("LL") || t.equalsIgnoreCase("LR") ||
						   t.equalsIgnoreCase("UL")  || t.equalsIgnoreCase("UR") ) {
						   //pass
						   if (normalteeth==null) normalteeth=new ArrayList<>();
						   normalteeth.add(t);
					   }else {
						   pass=false;
						   if (issueTeeth==null) issueTeeth=new ArrayList<>();
						   if (correctBiller==null) correctBiller=new HashSet<>();
						   issueTeeth.add(t);
						   correctBiller.add("(LL");
						   correctBiller.add("LR");
						   correctBiller.add("UL");
						   correctBiller.add("UR)");
						   
					   }
				   }
			} 

			String x="";
			if (pass) {
				if (codes[0].equals("") && codes[1].equals("")) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule40.message1.pass", new Object[] {}, locale),
						Constants.PASS));
				}else {
					if (!codes[0].equals("")) x= x+ codes[0];
					if (!codes[1].equals("")) {
						if (!x.equals("")) x=x+",";
						x= x+ codes[1];
					}
						
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule40.message2.pass", new Object[] { x, String.join(",", normalteeth) }, locale),
							Constants.PASS));
						
				}
			}else {
				
				if (!codes[0].equals("")) x= x+ codes[0];
				if (!codes[1].equals("")) {
					if (!x.equals("")) x=x+",";
					x= x+ codes[1];
				}
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule40.error.message", new Object[] { x,String.join(",", issueTeeth),String.join(",",correctBiller) }, locale),
						Constants.FAIL));
				}
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	
	
	// MVP VAP work in Progress
	//41
	public List<TPValidationResponseDto> Rule41(List<Object> tpList,List<MVPandVAP> mvpvapList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_41,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		try {
			
			Map<String,Set<MVPandVAP>> mvpvapMap=null;
			
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if(mvpvapList != null) {	
				for(MVPandVAP mvpvap:mvpvapList) {
					//String base = mvpvap.getBase();
					String baseG = mvpvap.getBaseGroup();
					if ( baseG!=null && baseG.equalsIgnoreCase(tp.getServiceCode())) {
						if (mvpvapMap==null) mvpvapMap=new HashMap<>();
						if (mvpvapMap.containsKey(mvpvap.getBase())) {
							Set<MVPandVAP>	l= mvpvapMap.get(mvpvap.getBase());
						  l.add(mvpvap);
						}else {
							Set<MVPandVAP>	l= new HashSet<>();
                            l.add(mvpvap);
							mvpvapMap.put(mvpvap.getBase(), l);
						}
					}
				}
			 }
			}
			if (mvpvapMap!=null) {
				Set<String> sf= new HashSet<>();
				for (Map.Entry<String, Set<MVPandVAP>> entry : mvpvapMap.entrySet()) {
					Set<MVPandVAP> d=entry.getValue();
					//List<String> dupList= new ArrayList<>();
					
					for (MVPandVAP m:d) {
						
						double bgfees=0;
						double mvpfees=0;
						MVPandVAP calc=null;
						Set<String> s= new HashSet<>();
						String baseG="";
						
						for(Object obj : tpList) {
							TreatmentPlan tp = (TreatmentPlan) obj;
							if(m.getBase()!=null && m.getBaseGroup().equalsIgnoreCase(tp.getServiceCode())) {
								baseG=m.getBaseGroup();
								s.add(m.getBaseGroup());
								bgfees=bgfees+Double.parseDouble(tp.getFee());
								RuleEngineLogger.generateLogs(clazz, "TP Code- "+tp.getServiceCode()
								                              +"- Base- "+m.getBaseGroup()+" TP Fees BASE- "+tp.getFee()+" -MVP Fee-"+m.getMvp(),
										Constants.rule_log_debug, bw);

								calc=m;
								if (m.getMvp()==null ) {
									m.setMvp("0");
								}
								RuleEngineLogger.generateLogs(clazz, "THIS IS MVP FEES- "+m.getMvp(),
			                        Constants.rule_log_debug, bw);
								//Add Only once MVP codes
								///if (!dupList.contains(tp.getServiceCode())) {
								mvpfees=mvpfees+Double.parseDouble(m.getMvp());
								//}
								//dupList.add(tp.getServiceCode());
								
								RuleEngineLogger.generateLogs(clazz, "mvpfees (Commulative) - "+mvpfees,
				                        Constants.rule_log_debug, bw);
								
								RuleEngineLogger.generateLogs(clazz, "THIS IS TP FEE "+tp.getFee()+" - Commulative Fees-"+bgfees,
				                        Constants.rule_log_debug, bw);
									
							}else if(m.getVap1()!=null && m.getVap1().equalsIgnoreCase(tp.getServiceCode())) {
								s.add(m.getVap1());
								RuleEngineLogger.generateLogs(clazz, "TP Code- "+tp.getServiceCode()
	                              +"- Base- "+m.getBaseGroup()+" TP Fees VAP 1- "+tp.getFee()+" -MVP Fee-"+m.getMvp(),
			                     Constants.rule_log_debug, bw);
								bgfees=bgfees+Double.parseDouble(tp.getFee());
								calc=m;
								RuleEngineLogger.generateLogs(clazz, "THIS IS TP FEE "+tp.getFee()+" - Commulative Fees-"+bgfees,
				                        Constants.rule_log_debug, bw);
							}else if(m.getVap2()!=null && m.getVap2().equalsIgnoreCase(tp.getServiceCode())) {
								RuleEngineLogger.generateLogs(clazz, "TP Code- "+tp.getServiceCode()
	                              +"- Base- "+m.getBaseGroup()+" TP Fees VAP 2- "+tp.getFee()+" -MVP Fee-"+m.getMvp(),
			                     Constants.rule_log_debug, bw);
								bgfees=bgfees+Double.parseDouble(tp.getFee());
								s.add(m.getVap2());
								calc=m;
								RuleEngineLogger.generateLogs(clazz, "THIS IS TP FEE "+tp.getFee()+" - Commulative Fees-"+bgfees,
				                        Constants.rule_log_debug, bw);
							}else if(m.getVap3()!=null && m.getVap3().equalsIgnoreCase(tp.getServiceCode())) {
								RuleEngineLogger.generateLogs(clazz, "TP Code- "+tp.getServiceCode()
	                              +"- Base- "+m.getBaseGroup()+" TP Fees VAP 3- "+tp.getFee()+" -MVP Fee-"+m.getMvp(),
			                     Constants.rule_log_debug, bw);
								bgfees=bgfees+Double.parseDouble(tp.getFee());
								s.add(m.getVap3());
								calc=m;
								RuleEngineLogger.generateLogs(clazz, "THIS IS TP FEE "+tp.getFee()+" - Commulative Fees-"+bgfees,
				                        Constants.rule_log_debug, bw);
							}else if(m.getVap4()!=null && m.getVap4().equalsIgnoreCase(tp.getServiceCode())) {
								RuleEngineLogger.generateLogs(clazz, "TP Code- "+tp.getServiceCode()
	                              +"- Base- "+m.getBaseGroup()+" TP Fees VAP 4- "+tp.getFee()+" -MVP Fee-"+m.getMvp(),
			                     Constants.rule_log_debug, bw);
								bgfees=bgfees+Double.parseDouble(tp.getFee());
								s.add(m.getVap4());
								calc=m;
								RuleEngineLogger.generateLogs(clazz, "THIS IS TP FEE "+tp.getFee()+" - Commulative Fees-"+bgfees,
				                        Constants.rule_log_debug, bw);
							}else if(m.getVap5()!=null && m.getVap5().equalsIgnoreCase(tp.getServiceCode())) {
								RuleEngineLogger.generateLogs(clazz, "TP Code- "+tp.getServiceCode()
	                              +"- Base- "+m.getBaseGroup()+" TP Fees VAP 5- "+tp.getFee()+" -MVP Fee-"+m.getMvp(),
			                     Constants.rule_log_debug, bw);
								bgfees=bgfees+Double.parseDouble(tp.getFee());
								s.add(m.getVap5());
								calc=m;
								RuleEngineLogger.generateLogs(clazz, "THIS IS TP FEE "+tp.getFee()+" - Commulative Fees-"+bgfees,
				                        Constants.rule_log_debug, bw);
							}else if(m.getVap6()!=null && m.getVap6().equalsIgnoreCase(tp.getServiceCode())) {
								RuleEngineLogger.generateLogs(clazz, "TP Code- "+tp.getServiceCode()
	                              +"- Base- "+m.getBaseGroup()+" TP Fees VAP 6- "+tp.getFee()+" -MVP Fee-"+m.getMvp(),
			                     Constants.rule_log_debug, bw);
								bgfees=bgfees+Double.parseDouble(tp.getFee());
								s.add(m.getVap6());
								calc=m;
								RuleEngineLogger.generateLogs(clazz, "THIS IS TP FEE "+tp.getFee()+" - Commulative Fees-"+bgfees,
				                        Constants.rule_log_debug, bw);
							}
						}//For TP 
						RuleEngineLogger.generateLogs(clazz, "MVP Fees - "+mvpfees
                        +"- Base Group- "+baseG, Constants.rule_log_debug, bw);
						RuleEngineLogger.generateLogs(clazz, "Base Group Found - "+!baseG.equals(""), Constants.rule_log_debug, bw);
								
						if (calc!=null && !baseG.equals("")) {
							if (bgfees<mvpfees) {
						      //fail
								pass=false;
								sf.addAll(s);
								//dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								//		messageSource.getMessage("rule41.error.message", new Object[] {String.join(",",s)}, locale),
								//		Constants.FAIL));
								
							}
						}
					}//For TP
				}//FOR MAP
				if (!pass)
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule41.error.message", new Object[] {String.join(",",sf)}, locale),
								Constants.FAIL));
			}

          if (pass) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule41.message.pass", new Object[] {  }, locale),
					Constants.PASS));

          }
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}

	// Duplicate TP Codes
	//42
	public List<TPValidationResponseDto> Rule42(List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_42,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		try {
			
			//List<String> codes= new ArrayList<>();
			List<String> dupCodes= new ArrayList<>();
		 	Map<String,List<String>> mapE= new HashMap<>();
			
			
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				/*
				if (codes.contains(tp.getServiceCode())) {
					pass =false;
					dupCodes.add(tp.getServiceCode());
				}else {
					codes.add(tp.getServiceCode());
				}
				*/
				RuleEngineLogger.generateLogs(clazz, "Code FF- "+tp.getServiceCode(),
						Constants.rule_log_debug, bw);
			
				if (mapE.containsKey(tp.getServiceCode())) {
					RuleEngineLogger.generateLogs(clazz, "Code PR- "+tp.getServiceCode(),
							Constants.rule_log_debug, bw);
					List<String>  st = mapE.get(tp.getServiceCode());
					List<String> n=Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth()));
					//Before Adding look for common teeth;
					List<String> l3 = new ArrayList<String>(st);
					l3.retainAll(n);
					if (l3!=null && l3.size()>0) {
						RuleEngineLogger.generateLogs(clazz, "DUP Teeth - "+l3,
								Constants.rule_log_debug, bw);
						pass =false;
						dupCodes.add(tp.getServiceCode()+"(#"+l3+")");
					}
					st.addAll(n);
				} else {
					List<String> l = new ArrayList<>();
					l.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
					mapE.put(tp.getServiceCode(), l);
				}
				
				
			}

          if (pass) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule42.pass.message", new Object[] {  }, locale),
					Constants.PASS));

          }else {
        	  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
  					messageSource.getMessage("rule42.error.message", new Object[] {String.join(",", dupCodes)  }, locale),
  					Constants.FAIL));
          }
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}

	// Bone Graft (User Input) (Not Used)
	public List<TPValidationResponseDto> Rule43(List<Object> tpList,List<QuestionAnswerDto> ansL,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_43,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		try {
			
			boolean bone=false;
			boolean boneQuestionPresent=false;
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;//D4910  //D7953
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

				if (tp.getServiceCode().equalsIgnoreCase("D4910")) {
					bone=true;
					break;
				}
			}
	      if (bone) {
	    	  if (ansL!=null) {
	    		  for(QuestionAnswerDto qDto: ansL) {
	    			 if ( qDto.getQuestionId()==Constants.RULE_BONE_GRAFT_IMPLANT) {
	    			   boneQuestionPresent=true;
	    			   if (	qDto.getAnswer()!=null && qDto.getAnswer().equalsIgnoreCase("yes")) {
	    				   //pass
	    			   }
	    			   else {
	    				   //fail
	    				   pass=false;
	    			   }
	    			   break;
	    			 }
	    		  }
	    	  }
	      }
          if (boneQuestionPresent==false) {
        	  pass=false;
           	  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
    					messageSource.getMessage("rule43.error.message2", new Object[] {  }, locale),
    					Constants.FAIL));
          }else {
        	  if (pass==false) {
        		  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
      					messageSource.getMessage("rule43.error.message1", new Object[] { }, locale),
      					Constants.FAIL));
        	  }
          }
          if (pass) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule43.pass.message", new Object[] {  }, locale),
					Constants.PASS));

          }else {
        	 
          }
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}

	// Signed Consent Requirements (User Input) //Forms Required // Consent Form Requirements
	public List<TPValidationResponseDto> Rule44(List<QuestionAnswerDto> ansL,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_44,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		try {
			
			boolean questionPresent=false;
			
	      
	    	  if (ansL!=null) {
	    		  for(QuestionAnswerDto qDto: ansL) {
	    			 if ( qDto.getQuestionId()==Constants.Consent_Form_Requirements_header_id_Is_Signed_Consent_Form_Available) {
	    				 questionPresent=true;
	    			   if (	qDto.getAnswer()!=null && qDto.getAnswer().equalsIgnoreCase("yes")) {
	    				   //pass
	    			   }
	    			   else {
	    				   //fail
	    				   pass=false;
	    			   }
	    			   //break;
	    			 }
	    		  }
	    	  }
	      
          if (questionPresent==false) {
        	  pass=false;
           	  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
    					messageSource.getMessage("rule44.error.message2", new Object[] {  }, locale),
    					Constants.FAIL));
          }else {
        	  if (pass==false) {
        		  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
      					messageSource.getMessage("rule44.error.message1", new Object[] { }, locale),
      					Constants.FAIL));
        	  }
          }
          if (pass) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule44.pass.message", new Object[] {  }, locale),
					Constants.PASS));

          }else {
        	 
          }
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	
	// Major Service Form Requirements (User Input) //
	public List<TPValidationResponseDto> Rule50(List<QuestionAnswerDto> ansL,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_50,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		try {
			
			boolean questionPresent=false;
			
	      
	    	  if (ansL!=null) {
	    		  for(QuestionAnswerDto qDto: ansL) {
	    			 if ( qDto.getQuestionId()==Constants.Major_Service_Form_header_id_Is_major_Available) {
	    				 questionPresent=true;
	    			   if (	qDto.getAnswer()!=null && qDto.getAnswer().equalsIgnoreCase("yes")) {
	    				   //pass
	    			   }
	    			   else {
	    				   //fail
	    				   pass=false;
	      			   }
	    			 }
	    		  }
	    	  }
	      
           if (pass==false) {
        		  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
      					messageSource.getMessage("rule50.error.message1", new Object[] { }, locale),
      					Constants.FAIL));
        	  }
          
          else  {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule50.pass.message", new Object[] {  }, locale),
					Constants.PASS));

          }
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	
	// Ortho (User Input) (Not Used)
	public List<TPValidationResponseDto> Rule45(List<Object> tpList,List<QuestionAnswerDto> ansL, MessageSource messageSource, Rules rule,
			 BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_45,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			boolean pass = true;
			List<String> combinedY = new ArrayList<>();
			List<String> orthoList= Arrays.asList( Constants.ORTHO_CODE_UI.split(","));
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;

                if (orthoList.contains(tp.getServiceCode())) {
 				if (ansL!= null) {

					for(QuestionAnswerDto ans:ansL) {
						if (ans.getTpId().equalsIgnoreCase(tp.getId())) {
						if (Constants.RULE_ORTHO_question_header_id_narrtive==ans.getQuestionId()) {//4
							if (ans.getAnswer().trim().equals("")){
								pass=false;
								combinedY.add("Narrative");
							}
						}
						if (Constants.RULE_ORTHO_question_header_id_duration==ans.getQuestionId()) {//3
							if (ans.getAnswer().trim().equals("")){
								pass=false;
								combinedY.add("Treatment Duration");
							}
						}
						if (Constants.RULE_ORTHO_question_header_id_month_r==ans.getQuestionId()) {//2
							if (ans.getAnswer().trim().equals("")){
								pass=false;
								combinedY.add("Treatment Months");
							}
						}
						if (Constants.RULE_ORTHO_question_header_id_downpayment==ans.getQuestionId()) {//5
							if (ans.getAnswer().trim().equals("")){
								pass=false;
								combinedY.add("Downpayment");
							}
						}
						if (Constants.RULE_ORTHO_question_header_id_banding_date==ans.getQuestionId()) {//1
							if (ans.getAnswer().trim().equals("")){
								pass=false;
								combinedY.add("Banding Date");
							}
						}
						
					  }//if 
					}//for

				}
 				break;
			}
		  } 
			
			if (!pass) {
				if (combinedY.size() > 0)
					d.add(new TPValidationResponseDto(rule.getId(), rule.getName(), messageSource
							.getMessage("rule45.error.message", new Object[] { String.join(" ", combinedY) }, locale),
							Constants.FAIL));
				

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

	// Pre-Authorization Requirements (User Input)
	public List<TPValidationResponseDto> Rule46(Object ivfSheet,List<Object> tpList,List<QuestionAnswerDto> ansL, MessageSource messageSource, Rules rule,
			List<Mappings> mappings,BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_46,
				Constants.rule_log_debug, bw);

		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}
			
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			String dob = ivf.getPatientDOB();

			int[] age=null;
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
			boolean pass = true;
			boolean checkForAns=false;
			String cMedicate = Constants.insurance_Medicaid;
			String planType=ivf.getPlanType();
			//if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {//not needed for now
			if (true) {
				//if (age[0]>=21){//not needed for now..
				if (true) {
					for (Object obj : tpList) {
						TreatmentPlan tp = (TreatmentPlan) obj;
						Mappings mapP = getMappingFromListPreAuth(mappings, tp.getServiceCode());
						if (mapP != null) {
							RuleEngineLogger.generateLogs(clazz, "PreAuth  present" + mapP.getPreAuthNeeded(),
									Constants.rule_log_debug, bw);
							for(QuestionAnswerDto ans:ansL) {
								if (ans.getTpId().equalsIgnoreCase(tp.getId())) {
								if (Constants.Pre_Authorization_question_header_id_preauth_avail==ans.getQuestionId()) {//21
									if (!ans.getAnswer().equalsIgnoreCase("yes")){
										pass=false;
										/*
										d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
												messageSource.getMessage("rule46.error.message1", new Object[] {  }, locale),
												Constants.FAIL));
										*/		
									}else {
										checkForAns=true;
									}
								 }
								}						
							}
							if (checkForAns) {
								
							for(QuestionAnswerDto ans:ansL) {
								if (ans.getTpId().equalsIgnoreCase(tp.getId())) {
								if (Constants.Pre_Authorization_question_header_id_preauth_no==ans.getQuestionId()) {//22
									if (ans.getAnswer().trim().equals("")){
										pass=false;
										d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
												messageSource.getMessage("rule46.error.message2", new Object[] {  }, locale),
												Constants.FAIL));
									}
								 }
								}						
							}
							}
							break;
						}
					}//FOR
				}
			}

			
			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
			else if (d.size()==0){
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule46.error.message1", new Object[] {  }, locale),
						Constants.FAIL));
			}
		} catch (Exception x) {

			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}

	// Provider Change (User Input)
	public List<TPValidationResponseDto> Rule47(Object ivfSheet,List<Object> tpList,List<QuestionAnswerDto> ansL, MessageSource messageSource, Rules rule,
			List<Mappings> mappings,BufferedWriter bw) {

		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_47,
				Constants.rule_log_debug, bw);

	   boolean pass=true;	
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			String cMedicate=Constants.insurance_Medicaid;
			String planType=ivf.getPlanType();
			RuleEngineLogger.generateLogs(clazz,
					"planType-"+planType,
					Constants.rule_log_debug, bw);
			
			if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {
			
			String pname= ivf.getProviderName();
					for (Object obj : tpList) {
						TreatmentPlan tp = (TreatmentPlan) obj;
						RuleEngineLogger.generateLogs(clazz, " IVF provider -"+pname+" - Provider IN TP-"+tp.getProviderLastName(),
								Constants.rule_log_debug, bw);
						if (!pname.equalsIgnoreCase(tp.getProviderLastName())){
							RuleEngineLogger.generateLogs(clazz, " IVF provider -"+pname+" - Does not match Provider IN TP-"+tp.getProviderLastName(),
									Constants.rule_log_debug, bw);
							
							for(QuestionAnswerDto ans:ansL) {
								if (ans.getTpId().equalsIgnoreCase(tp.getId())) {
								if (Constants.Provider_Change_question_header_id_provider_change==ans.getQuestionId()) {//15
									if (!ans.getAnswer().equals("true")){
										pass=false;
										d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
												messageSource.getMessage("rule47.error.message1", new Object[] {  }, locale),
												Constants.FAIL));
									}
								 }
								}						
							}
							if (pass) {
							for(QuestionAnswerDto ans:ansL) {
								if (ans.getTpId().equalsIgnoreCase(tp.getId())) {
								if (Constants.Provider_Change_question_header_id_patient_change_provider==ans.getQuestionId()) {//16
									String sp=ans.getAnswer().trim();
									if (!sp.startsWith("true ") && sp.startsWith("true") && pass) {
										//This means answer is present but reference no missing
										pass=false;
										d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
												messageSource.getMessage("rule47.error.message1", new Object[] {  }, locale),
												Constants.FAIL));
									}
                                    if (sp.startsWith("false") && pass) {
                                    	//This means answer is not present we can say reference no missing
                                    	pass=false;
                                    	d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
												messageSource.getMessage("rule47.error.message1", new Object[] {  }, locale),
												Constants.FAIL));
									}
									
								  }
								}						
							  }
							}
							
							
							break;
					}						
					}//FOR
			}//If
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

	// Exam limitation for CHIP
	public List<TPValidationResponseDto> Rule48(Object ivfSheet,List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_48,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		try {
			
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			String chip= ivf.getPlanType();
			
			if(chip.toLowerCase().contains(Constants.insurance_Chip)) {
			
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;
	
				if (tp.getServiceCode().equalsIgnoreCase("D0145")){
					RuleEngineLogger.generateLogs(clazz, "Code Exam- "+tp.getServiceCode(),
							Constants.rule_log_debug, bw);
					pass=false;
					break;
				}
			}
		}
          if (pass) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule48.pass.message", new Object[] {}, locale),
					Constants.PASS));

          }else {
        	  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
  					messageSource.getMessage("rule48.error.message", new Object[] {chip}, locale),
  					Constants.FAIL));
          }
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}

	// Sealant limitation in CHIP
	public List<TPValidationResponseDto> Rule49(Object ivfSheet,List<Object> tpList,MessageSource messageSource, Rules rule,
			BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_49,
				Constants.rule_log_debug, bw);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=true;
		List<String> tooth= new ArrayList<>();
		try {
			
			IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
			String chip= ivf.getPlanType();
			Set<String> issueSet=new HashSet<>();
			if(chip.toLowerCase().contains(Constants.insurance_Chip)) {
			for (Object obj : tpList) {
				TreatmentPlan tp = (TreatmentPlan) obj;
				RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
						Constants.rule_log_debug, bw);
					
				if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
					||	tp.getEstInsurance().equals("0.0")) continue;
	
				if (tp.getServiceCode().equalsIgnoreCase("D1351")){
					RuleEngineLogger.generateLogs(clazz, "Code Sealant- "+tp.getServiceCode(),
							Constants.rule_log_debug, bw);
				tooth.addAll(Arrays.asList(ToothUtil.getToothsFromTooth(tp.getTooth())));
				RuleEngineLogger.generateLogs(clazz, "Sealant Tooth- "+tp.getTooth(),
						Constants.rule_log_debug, bw);
				
				}
			}
			if (tooth.contains("20") || tooth.contains("21") || tooth.contains("28")
				|| tooth.contains("29")	) {
				if (tooth.contains("20")) issueSet.add("20");
				if (tooth.contains("21")) issueSet.add("21");
				if (tooth.contains("28")) issueSet.add("28");
				if (tooth.contains("29")) issueSet.add("29");
				
				pass=false;
			}
		}
          if (pass) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule49.pass.message", new Object[] {}, locale),
					Constants.PASS));

          }else {
        	  dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
  					messageSource.getMessage("rule49.error.message", new Object[] {chip,issueSet}, locale),
  					Constants.FAIL));
          }
		} catch (Exception ex) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { ex.getMessage() }, locale),
					Constants.FAIL));
		}
		return dList;

	}
	
	
	// DQ Fillings (Provider Same)
	public List<TPValidationResponseDto> Rule51(List<Object> tpList, Object ivfSheet, List<EagleSoftFeeShedule> esfeess, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_51,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			boolean pass = true;
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}

				//String planType = ivf.getPlanType();
				//String cMedicate = Constants.insurance_Medicaid;
				ToothHistoryDto hdto = null;
				Map<String, List<ToothHistoryDto>> mapHistoryM = new HashMap<>();
				String pname= ivf.getProviderName();
				boolean providerSame=true;
		        List<String> checkList = new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(",")));
		        checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
		        checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_MM_SC.split(","))));

				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					if (!checkList.contains(tp.getServiceCode())) continue;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

					RuleEngineLogger.generateLogs(clazz, " IVF provider -"+pname+" - Provider IN TP-"+tp.getProviderLastName(),
							Constants.rule_log_debug, bw);
					if (!pname.equalsIgnoreCase(tp.getProviderLastName())){
						providerSame=false;
					}
				}
	            //if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {
				RuleEngineLogger.generateLogs(clazz, " Provider is Same :"+providerSame,
						Constants.rule_log_debug, bw);
				Date TP_Date = new Date();
				
				if (providerSame) {
	       	
       	        
					int noOFhistory = Constants.history_codes_size;
					Class<?> c2;
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							String hs = "getHistory" + i + "Surface";
							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							Method hdm = c2.getMethod(hd);
							Method hss = c2.getMethod(hs);	
							String code = (String) hcm.invoke(hisSheet);
							String dt = (String) hdm.invoke(hisSheet);
							
							if (code.equals("")) continue ;
							if (dt.equals("")) continue ;
							if (!checkList.contains(code)) continue;
							//check for future date 
							try {
							if ( Constants.SIMPLE_DATE_FORMAT_IVF.parse(dt).after(TP_Date)) continue;
							}catch (Exception e) {
								continue;
							}
							
								hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistoryM.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistoryM.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistoryM.put(code, l);
								}
						}
						for (Object obj1 : tpList) {
						TreatmentPlan tp = (TreatmentPlan) obj1;
						if (!checkList.contains(tp.getServiceCode())) continue;
						//List<String> res= new ArrayList<>();
						//LOW ORDER
						List<String> rDLSS= ToothUtil.lowerHigherOrderFillingFound51_52(tp, mapHistoryM, true, TP_Date, true,1,36, bw);
						if (rDLSS!=null && rDLSS.size()>0) {
							List<String> dx=ToothUtil.generateErrorListForRule51_52(tp,esfeess,rDLSS,bw);
							for(String p:dx) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule51.alert.message1", new Object[] {p.split("---")[0],p.split("---")[1],p.split("---")[2] }, locale), Constants.FAIL));
							}
							pass= false;
						}
						//LOW ORDER
						rDLSS= ToothUtil.lowerHigherOrderFillingFound51_52(tp, mapHistoryM, true, TP_Date, true,2,36, bw);
						if (rDLSS!=null && rDLSS.size()>0) {
							List<String> dx=ToothUtil.generateErrorListForRule51_52(tp,esfeess,rDLSS,bw);
							for(String p:dx) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule51.alert.message2", new Object[] {p.split("---")[0],p.split("---")[1],p.split("---")[2] }, locale), Constants.FAIL));
							}
							pass= false;
						}
						}//For loop End
						
	            }
                  /**/
			
			if (pass)
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule.message.pass", new Object[] {}, locale), Constants.PASS));
		} catch (Exception x) {
            x.printStackTrace();
			d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { x.getMessage() }, locale),
					Constants.FAIL));

		}
		return d;

	}
	
	// DQ Fillings (Provider Different)
	public List<TPValidationResponseDto> Rule52(List<Object> tpList, Object ivfSheet, List<EagleSoftFeeShedule> esfeess, MessageSource messageSource,
			Rules rule, BufferedWriter bw) {
		RuleEngineLogger.generateLogs(clazz, Constants.rule_log_enter + "-" + Constants.RULE_ID_52,
				Constants.rule_log_debug, bw);

		IVFTableSheet ivf = (IVFTableSheet) ivfSheet;
		List<TPValidationResponseDto> d = new ArrayList<>();
		try {
			boolean pass = true;
			if (tpList == null) {
				d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						Constants.errorMessOPen + "Invalid Treatment Plan" + Constants.errorMessClose, Constants.FAIL));
				return d;
			}

				//String planType = ivf.getPlanType();
				//String cMedicate = Constants.insurance_Medicaid;
				ToothHistoryDto hdto = null;
				Map<String, List<ToothHistoryDto>> mapHistoryM = new HashMap<>();
				String pname= ivf.getProviderName();
				boolean providerSame=true;
		        List<String> checkList = new ArrayList<String>(Arrays.asList(Constants.FILLING_PT_SC.split(",")));
		        checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_AT_SC.split(","))));
		        checkList.addAll(new ArrayList<String>(Arrays.asList(Constants.FILLING_MM_SC.split(","))));
				for (Object obj : tpList) {
					TreatmentPlan tp = (TreatmentPlan) obj;
					RuleEngineLogger.generateLogs(clazz, "EST INS.-"+tp.getEstInsurance(),
							Constants.rule_log_debug, bw);
					if (!checkList.contains(tp.getServiceCode())) continue;
						
					if (tp.getEstInsurance().equals("") || tp.getEstInsurance().equals("0") || tp.getEstInsurance().equals("0.00")
						||	tp.getEstInsurance().equals("0.0")) continue;

					RuleEngineLogger.generateLogs(clazz, " IVF provider -"+pname+" - Provider IN TP-"+tp.getProviderLastName(),
							Constants.rule_log_debug, bw);
					if (!pname.equalsIgnoreCase(tp.getProviderLastName())){
						providerSame=false;
					}
				}
	            //if (planType != null && planType.trim().toLowerCase().contains(cMedicate)) {
				RuleEngineLogger.generateLogs(clazz, " Provider is Same :"+providerSame,
						Constants.rule_log_debug, bw);
				Date TP_Date = new Date();
				
				if (!providerSame) {
	       	
       	       
       	        
					int noOFhistory = Constants.history_codes_size;
					Class<?> c2;
						c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");

						IVFHistorySheet hisSheet = ivf.getHs();
						
						for (int i = 1; i <= noOFhistory; i++) {
							String hc = "getHistory" + i + "Code";
							String hd = "getHistory" + i + "DOS";
							String ht = "getHistory" + i + "Tooth";
							String hs = "getHistory" + i + "Surface";
							Method hcm = c2.getMethod(hc);
							Method htm = c2.getMethod(ht);
							Method hdm = c2.getMethod(hd);
							Method hss = c2.getMethod(hs);	
							String code = (String) hcm.invoke(hisSheet);
							String dt = (String) hdm.invoke(hisSheet);
							
							if (code.equals("")) continue ;
							if (dt.equals("")) continue ;
							if (!checkList.contains(code)) continue;
							//check for future date 
							try {
							if ( Constants.SIMPLE_DATE_FORMAT_IVF.parse(dt).after(TP_Date)) continue;
							}catch (Exception e) {
								continue;
							}

							hdto = new ToothHistoryDto((String) hcm.invoke(hisSheet), (String) hdm.invoke(hisSheet),
										(String) htm.invoke(hisSheet),(String) hss.invoke(hisSheet));
				              if (mapHistoryM.containsKey(code)) {
									List<ToothHistoryDto> t = mapHistoryM.get(code);
									t.add(hdto);
								} else {
									List<ToothHistoryDto> l = new ArrayList<>();
									l.add(hdto);
									mapHistoryM.put(code, l);
								}
						}
						for (Object obj1 : tpList) {
						TreatmentPlan tp = (TreatmentPlan) obj1;
						if (!checkList.contains(tp.getServiceCode())) continue;
						//List<String> res= new ArrayList<>();
						//LOW ORDER
						List<String> rDLSS= ToothUtil.lowerHigherOrderFillingFound51_52(tp, mapHistoryM, true, TP_Date, true,1,12, bw);
						if (rDLSS!=null && rDLSS.size()>0) {
							List<String> dx=ToothUtil.generateErrorListForRule51_52(tp,esfeess,rDLSS,bw);
							for(String p:dx) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule52.alert.message1", new Object[] {p.split("---")[0],p.split("---")[1],p.split("---")[2]}, locale), Constants.FAIL));
							}
							pass= false;
						}
						//LOW ORDER
						rDLSS= ToothUtil.lowerHigherOrderFillingFound51_52(tp, mapHistoryM, true, TP_Date, true,2,12, bw);
						if (rDLSS!=null && rDLSS.size()>0) {
							List<String> dx=ToothUtil.generateErrorListForRule51_52(tp,esfeess,rDLSS,bw);
							for(String p:dx) {
							d.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
									messageSource.getMessage("rule52.alert.message2", new Object[] {p.split("---")[0],p.split("---")[1],p.split("---")[2] }, locale), Constants.FAIL));
							}
							pass= false;
						}

						}//For loop End
						
	            }
                  /**/
			
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

	public Mappings getMappingFromListAdditionalInformationNeeded(List<Mappings> map, String code) {
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

	public Mappings getMappingFromListConsentNeeded(List<Mappings> map, String code) {
		Mappings r = null;
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> (!rule.getConsentNeeded().equalsIgnoreCase("No")
						&& !rule.getConsentNeeded().equalsIgnoreCase("NA")
						&& !rule.getConsentNeeded().equalsIgnoreCase("")
						&& !rule.getConsentNeeded().equalsIgnoreCase("None")
						&& rule.getAdaCodes().getCode().equals(code)));
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for nulll
		return r;
	}

	public Mappings getMappingFromListMajorServiceForm(List<Mappings> map, String code) {
		Mappings r = null;
		Collection<Mappings> ruleGen = Collections2.filter(map,
				rule -> (!rule.getMajorserviceForm().equalsIgnoreCase("No")
						&& !rule.getMajorserviceForm().equalsIgnoreCase("NA")
						&& !rule.getMajorserviceForm().equalsIgnoreCase("")
						&& !rule.getMajorserviceForm().equalsIgnoreCase("None")
						&& rule.getAdaCodes().getCode().equals(code)));
		for (Mappings rule : ruleGen) {
			r = rule;
		}
		// Debug me for nulll
		return r;
	}

	public Mappings getMappingFromListPreAuth(List<Mappings> map, String code) {
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
