package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmClaimDetail;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.dto.CredentialData;
import com.tricon.rcm.dto.CredentialDataAnesthesia;
import com.tricon.rcm.dto.TPValidationResponseDto;
import com.tricon.rcm.dto.ValidateCreateClaimInformationDto;
import com.tricon.rcm.dto.ValidateRecreateClaimResponseDto;
import com.tricon.rcm.dto.customquery.DataPatientRuleDto;
import com.tricon.rcm.dto.customquery.RcmClaimDataDto;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.RuleConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

@Service
public class RuleBookServiceImpl {

	private final Logger logger = LoggerFactory.getLogger(RuleBookServiceImpl.class);
	private static Locale locale = new Locale("en");

	@Autowired
	MessageSource messageSource;

	/**
	 * Rule to Comparing IV Member Id and Claim Group Member Id
	 * 
	 * @param rule
	 *            Rule name
	 * @param ivf
	 *            Patient IV Data
	 * @param claimData
	 *            ClaimData For a Claim
	 */
	public List<TPValidationResponseDto> rule301(RcmRules rule, DataPatientRuleDto ivf, String memberId) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {
			// System.out.println(ivf.getBasicInfo16());
			if (ivf == null) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule301.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			} else if (ivf.getBasicInfo16().trim().equals(memberId == null ? "" : memberId)) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule301.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));
			} else {
				dList.add(
						new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule301.error.message",
										new Object[] { memberId, ivf.getBasicInfo16() }, locale),
								Constants.FAIL, "", "", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;
	}

	/**
	 * Rule to Comparing IV Group and Claim Group number
	 * 
	 * @param rule
	 *            Rule name
	 * @param ivf
	 *            Patient IV Data
	 * @param claimData
	 *            ClaimData For a Claim
	 */
	public List<TPValidationResponseDto> rule302(RcmRules rule, DataPatientRuleDto ivf, String groupNo) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			if (ivf == null) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule302.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			} else if (ivf.getBasicInfo14().trim().equalsIgnoreCase(groupNo == null ? "" : groupNo.trim())) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule302.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));
			} else {
				dList.add(
						new TPValidationResponseDto(rule.getId(), rule.getName(),
								messageSource.getMessage("rule302.error.message",
										new Object[] { groupNo, ivf.getBasicInfo14() }, locale),
								Constants.FAIL, "", "", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	/**
	 * Rule For Verifying Patient Details.
	 * 
	 * @param rule
	 * @param ivf
	 * @param rcmClaim
	 * @return
	 */
	public List<TPValidationResponseDto> rule303(RcmRules rule, DataPatientRuleDto ivf, RcmClaims rcmClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass = true;
		String[] clT = rcmClaim.getClaimId().split("_");
		// String claimSubTy = Constants.insuranceTypeSecondary;// May be needed latter
		boolean primary = true;
		if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
			primary = true;

		} else {
			primary = false;

		}
		List<String> errorMessage = null;
		try {

			if (ivf == null) {

				if (errorMessage == null)
					errorMessage = new ArrayList<>();
				errorMessage.add("IV Not Found");
				pass = false;
			} else {
				if (!rcmClaim.getPatientName().trim().equalsIgnoreCase(ivf.getBasicInfo2().trim())) {
					if (errorMessage == null)
						errorMessage = new ArrayList<>();
					errorMessage.add("Claim Patient name: " + rcmClaim.getPatientName() + "; IV Patient name: "
							+ ivf.getBasicInfo2());
					pass = false;
					// Fail
				}
				if (!Constants.SDF_SHEET_DATE.format(rcmClaim.getPatientBirthDate()).equals(ivf.getBasicInfo6())) {
					if (errorMessage == null)
						errorMessage = new ArrayList<>();
					errorMessage
							.add("Claim Patient DOB: " + Constants.SDF_SHEET_DATE.format(rcmClaim.getPatientBirthDate())
									+ "; Claim Patient DOB: " + ivf.getBasicInfo6());
					pass = false;
					// Fail
				}
				if (primary) {
					if (!rcmClaim.getPrimePolicyHolder().trim().equalsIgnoreCase(ivf.getBasicInfo5().trim())) {
						if (errorMessage == null)
							errorMessage = new ArrayList<>();
						errorMessage.add("Claim PolicyHolder: " + rcmClaim.getPrimePolicyHolder()
								+ "; IV PolicyHolder: " + ivf.getBasicInfo5());
						pass = false;
						// Fail
					}
				} else {
					if (!rcmClaim.getSecPolicyHolder().trim().equalsIgnoreCase(ivf.getBasicInfo5().trim())) {
						if (errorMessage == null)
							errorMessage = new ArrayList<>();
						errorMessage.add("Claim PolicyHolder: " + rcmClaim.getSecPolicyHolder() + "; IV PolicyHolder: "
								+ ivf.getBasicInfo5());
						pass = false;
						// Fail
					}
				}

				if (pass) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule303.pass.message", new Object[] {}, locale), Constants.PASS,
							"", "", ""));
				} else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule303.error.message",
									new Object[] { String.join(",", errorMessage) }, locale),
							Constants.FAIL, "", "", ""));
				}

			}

			if (!pass && dList.size() == 0) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule303.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	/**
	 * Rule For :Treating Provider and Provider on Claim Same in ES
	 * 
	 * @param rule
	 * @param ivf
	 * @param rcmClaim
	 * @return
	 */
	public List<TPValidationResponseDto> rule304(RcmRules rule, RcmClaims rcmClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			String providerOnClaim = rcmClaim.getProviderOnClaim();
			String providersOnClaimFromSheet = rcmClaim.getProviderOnClaimFromSheet();// sheet
			String treatingProvider = rcmClaim.getTreatingProvider();// sheet
			String treatingProviderFromClaim = rcmClaim.getTreatingProviderFromClaim();

			if (providerOnClaim == null)
				providerOnClaim = "";
			if (providersOnClaimFromSheet == null)
				providersOnClaimFromSheet = "";
			if (treatingProvider == null)
				treatingProvider = "";
			if (treatingProviderFromClaim == null)
				treatingProviderFromClaim = "";

			if (!providerOnClaim.equals("") && !providersOnClaimFromSheet.equals("")) {

				String[] providerOnClaimFromSheet = providersOnClaimFromSheet.split(Constants.ProviderJoinCons);
				boolean match = false;
				for (String pr : providerOnClaimFromSheet) {

					if (providerOnClaim.equalsIgnoreCase(pr)) {
						match = true;
					}
				}
				if (match) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule304.pass.message", new Object[] {}, locale), Constants.PASS,
							"", "", ""));
				} else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule304.error.message",
									new Object[] { treatingProvider.replace(Constants.ProviderJoinCons, ","),
											treatingProviderFromClaim },
									locale),
							Constants.FAIL, "", "", ""));
				}
				/*
				 * if (!providerOnClaim.equalsIgnoreCase(providerOnClaimFromSheet)) {
				 * 
				 * dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
				 * messageSource.getMessage("rule304.error.message", new Object[] {
				 * treatingProvider.replace(Constants.ProviderJoinCons,","),
				 * treatingProviderFromClaim }, locale), Constants.FAIL, "", "", "")); }else {
				 * dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
				 * messageSource.getMessage("rule304.pass.message", new Object[] { }, locale),
				 * Constants.PASS, "", "", "")); }
				 */

			} else if (!treatingProviderFromClaim.equals("")) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule304.error.message1",
								new Object[] { " Missing Data for Treating Proivder in sheet For provider Id -"
										+ rcmClaim.getProviderId() },
								locale),
						Constants.FAIL, "", "", ""));
			} else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule304.error.message1",
								new Object[] { " Missing Data for Treating Proivder in sheet For DOS -("
										+ Constants.SDF_SHEET_PROVIDER_DATE_HELPING.format(rcmClaim.getDos()) + ") " },
								locale),
						Constants.FAIL, "", "", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	public static void main(String a[]) throws Exception {

		String ddd = "asddasdaDeepak" + Constants.ProviderJoinCons;
		System.out.println(ddd.replace(Constants.ProviderJoinCons, ""));
		System.out.println(ddd.split(Constants.ProviderJoinCons)[0]);
		System.out.println("qeqeq2e".split(Constants.ProviderJoinCons)[0]);
	}

	/**
	 * Rule For :Provider on Claim Credentialed with the insurance Credentialing
	 * Status
	 * 
	 * @param rule
	 * @param creList
	 * @param rcmClaim
	 * @param claimofficeName
	 * @return
	 */
	public List<TPValidationResponseDto> rule305(RcmRules rule, List<CredentialData> creList, RcmClaims rcmClaim,
			String claimofficeName, String insuranceCodeFromSheet, String insName) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
		logger.info("-->" + creList.size());
		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			// List<ProviderCodeWithOffice> pro = (List<ProviderCodeWithOffice>)
			// providerSheetData[1];

			String claimProvider = rcmClaim.getProviderId();

			if (claimProvider == null) {

				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule305.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
				return dList;
			}
			// String sheetProviderCode = "";
			String applicationStatus = "";
			String effectiveDate = "";
			/*
			 * List<ProviderCodeWithOffice> pCodeList = pro.stream() .filter(e ->
			 * e.getOffice().trim().equalsIgnoreCase(claimofficeName) &&
			 * e.getEsCode().trim().equalsIgnoreCase(claimProvider))
			 * .collect(Collectors.toList()); if (pCodeList != null && pCodeList.size() > 0)
			 * { sheetProviderCode = pCodeList.get(0).getProviderCode(); }
			 */

			final String testVal = insuranceCodeFromSheet;
			// final String insNameFinal = insName;
			// CredentialData ddd =creList.get(953);
			// CredentialData ddd1 =creList.get(3032);
			// CredentialData dd22=creList.get(3031);
			// CredentialData dd221=creList.get(3030);

			List<CredentialData> filterCodeList = creList.stream()
					.filter(e -> e.getLocation().trim().equalsIgnoreCase(claimofficeName)
							&& e.getPlanType().trim().equalsIgnoreCase(rcmClaim.getRcmInsuranceType().getName())
							&& e.getInsuranceCode().trim().equalsIgnoreCase(testVal)
							&& e.getProviderCode().equalsIgnoreCase(rcmClaim.getTreatingProviderFromClaim()))
					.collect(Collectors.toList());

			if (filterCodeList != null && filterCodeList.size() > 0) {
				applicationStatus = filterCodeList.get(0).getApplicationStatus().toLowerCase();
				effectiveDate = filterCodeList.get(0).getEffectiveDate().trim();
				logger.info("applicationStatus --" + applicationStatus);
				logger.info("Effective Date --" + filterCodeList.get(0).getEffectiveDate());
			}
			// Effective Date Check
			boolean dtCheck = false;
			try {
				Date effectDate = Constants.SDF_CredentialSheetAnes.parse(effectiveDate);
				int comp = effectDate.compareTo(rcmClaim.getDos());
				if (comp <= 0)
					dtCheck = true;

			} catch (Exception dIssue) {

			}
			// Point 28
			if ((applicationStatus.contains("completed") || applicationStatus.contains("termination in-process")
					|| applicationStatus.contains("termination in process")
					|| applicationStatus.contains("enrolled as non par")
					|| applicationStatus.contains("enrolled as non-par")) && dtCheck) {

				// pass
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule305.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));

			} else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule305.error.message", new Object[] { claimProvider }, locale),
						Constants.FAIL, "", "", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	/*
	 * public List<TPValidationResponseDto> rule306(RcmRules rule,
	 * DataPatientRuleDto dto, RcmClaims rcmClaim) {
	 * 
	 * logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
	 * 
	 * List<TPValidationResponseDto> dList = new ArrayList<>();
	 * 
	 * try {
	 * 
	 * 
	 * // pass if (dto!=null && dto.getPlanAssignmentofBenefits()!=null &&
	 * dto.getPlanAssignmentofBenefits().equalsIgnoreCase("yes")) { dList.add(new
	 * TPValidationResponseDto(rule.getId(), rule.getName(),
	 * messageSource.getMessage("rule306.pass.message", new Object[] {}, locale),
	 * Constants.PASS, "", "", ""));
	 * 
	 * } else if (dto!=null) { dList.add(new TPValidationResponseDto(rule.getId(),
	 * rule.getName(), messageSource.getMessage("rule306.error.message", new
	 * Object[] { dto.getPlanAssignmentofBenefits() }, locale), Constants.FAIL, "",
	 * "", "")); }else { dList.add(new TPValidationResponseDto(rule.getId(),
	 * rule.getName(), messageSource.getMessage("rule306.error.message1", new
	 * Object[] { "IV not found." }, locale), Constants.FAIL, "", "", "")); }
	 * 
	 * } catch (Exception n) { dList.add(new TPValidationResponseDto(rule.getId(),
	 * rule.getName(), messageSource.getMessage("rule.error.exception", new Object[]
	 * { n.getMessage() }, locale), Constants.FAIL, "", "", "")); }
	 * 
	 * logger.info(RuleConstants.rule_log_exit + rule.getName()); return dList;
	 * 
	 * }
	 */

	// DOS vs Appointment Date
	public List<TPValidationResponseDto> rule323(RcmRules rule, String appointmentDate, RcmClaims rcmClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
       //Date format when not blank --2023-08-31 09:00:00.000000
		List<TPValidationResponseDto> dList = new ArrayList<>();
        if (appointmentDate==null) appointmentDate="";
		try {

			// pass
			if (!appointmentDate.equals("")) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule323.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));

			} else  {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule323.error.message",
								new Object[] { rcmClaim.getDos() }, locale),
						Constants.FAIL, "", "", ""));
			} 

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	// CRA Code
	public List<TPValidationResponseDto> rule307(RcmRules rule, List<RcmClaimDetail> cdList) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
		List<String> list = new ArrayList<>();
		String array[] = { "D0601", "D0602", "D0603" };
		Collections.addAll(list, array);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass = false;
		try {

			for (RcmClaimDetail cd : cdList) {
				if (list.contains(cd.getServiceCode())) {
					pass = true;
					break;
				}
			}

			if (pass) {
				// pass
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule307.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));

			} else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule307.error.message", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	// FDH Certification
	public List<TPValidationResponseDto> rule308(RcmRules rule, List<RcmClaimDetail> cdList, String providerCode,
			List<CredentialDataAnesthesia> sheetData, Date claimDos, String insCode) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		String cd = "D0145";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean found = false;
		boolean pass = false;
		try {

			for (RcmClaimDetail code : cdList) {
				if (code.getServiceCode().equalsIgnoreCase(cd)) {
					found = true;
					break;
				}
			}

			if (found) {
				List<CredentialDataAnesthesia> filterList = sheetData.stream()
						.filter(c -> c.getProviderCodes().equals(providerCode)).collect(Collectors.toList());
				if (filterList.size() > 0) {
					String effDate = filterList.get(0).getFDHEffectiveDate();
					String firstHome = filterList.get(0).getFirstHomeD0145();
					if (firstHome.trim().equalsIgnoreCase("yes")) {
						if (!effDate.equals("")) {
							Date eff = Constants.SDF_CredentialSheetAnes.parse(effDate);
							if (eff.compareTo(claimDos) < 0) {
								pass = true;
							}
						}
					} else {
						pass = false;// no is fail
					}
					// pass = false;//not listed then Fail.
				}
				// pass

			} else {
				// N/A
				pass = true;

			}

			if (pass) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule308.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));
			} else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule308.error.message", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	// Nitrous Certification (D9230)
	public List<TPValidationResponseDto> rule309(RcmRules rule, List<RcmClaimDetail> cdList, String providerCode,
			List<CredentialDataAnesthesia> sheetData) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		String cd = "D9230";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean found = false;
		boolean pass = false;
		try {

			for (RcmClaimDetail cdL : cdList) {
				if (cdL.getServiceCode().equalsIgnoreCase(cd)) {
					found = true;
					break;
				}
			}

			if (found) {
				List<CredentialDataAnesthesia> filterList = sheetData.stream()
						.filter(c -> c.getProviderCodes().equals(providerCode)).collect(Collectors.toList());
				if (filterList.size() > 0) {

					String firstHome = filterList.get(0).getD9230Nirtrous();
					if (firstHome.trim().equalsIgnoreCase("yes")) {
						pass = true;
					} else {
						pass = false;// no is fail
					}
					// pass = false;//not listed then Fail.
				}
				// pass

			} else {
				// N/A
				pass = true;

			}

			if (pass) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule309.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));
			} else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule309.error.message", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	// Sedation Certification
	public List<TPValidationResponseDto> rule310(RcmRules rule, Set<String> claimCodes, String providerCode,
			List<CredentialDataAnesthesia> sheetData) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		String cd = "D9248";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean found = false;
		boolean pass = false;
		try {

			for (String code : claimCodes) {
				if (code.equalsIgnoreCase(cd)) {
					found = true;
					break;
				}
			}

			if (found) {
				List<CredentialDataAnesthesia> filterList = sheetData.stream()
						.filter(c -> c.getProviderCodes().equals(providerCode)).collect(Collectors.toList());
				if (filterList.size() > 0) {

					String firstHome = filterList.get(0).getD9248Anesthesia();
					if (firstHome.trim().equalsIgnoreCase("yes")) {
						pass = true;
					} else {
						pass = false;// no is fail
					}

				} else {
					pass = false;// not listed then Fail.
				}
				// pass

			} else {
				// N/A
				pass = true;

			}

			if (pass) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule310.pass.message", new Object[] {}, locale), Constants.PASS, "",
						"", ""));
			} else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule310.error.message", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	// current claim is secondary or not
	public List<ValidateRecreateClaimResponseDto> rule324(RcmRules rule, RcmClaims currentClaimSecondary) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		boolean pass = true;

		List<ValidateRecreateClaimResponseDto> dList = new ArrayList<>();
		try {
			if (currentClaimSecondary != null) {
				pass = false;
			}
			if (pass) {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule324.pass.message", new Object[] {}, locale), Constants.PASS));
			} else {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule324.error.message",
								new Object[] { currentClaimSecondary.getClaimId() }, locale),
						Constants.FAIL));
			}

		} catch (Exception n) {
			dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}

	// match dos from current claim
	public List<ValidateRecreateClaimResponseDto> rule325(RcmRules rule, RcmClaims primaryClaimForNew,
			RcmClaims currentClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		boolean pass = true;
		List<ValidateRecreateClaimResponseDto> dList = new ArrayList<>();
		try {
			if (!Constants.SDF_MYSL_DATE
					.format(currentClaim.getDos() == null ? "" : currentClaim.getDos())
					.equals(Constants.SDF_MYSL_DATE.format(
							primaryClaimForNew.getDos() == null ? "" : primaryClaimForNew.getDos()))) {

				pass = false;
			}

			if (pass) {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule325.pass.message", new Object[] {}, locale), Constants.PASS));
			} else {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule325.error.message",
								new Object[] {Constants.SDF_MYSL_DATE.format(primaryClaimForNew.getDos()== null ? "" :primaryClaimForNew.getDos()), Constants.SDF_MYSL_DATE.format(currentClaim.getDos()==null?"":currentClaim.getDos()) }, locale),
						Constants.FAIL));
			}

		} catch (Exception n) {
			dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;
	}

	// match patientId from current claim
	public List<ValidateRecreateClaimResponseDto> rule326(RcmRules rule, RcmClaims primaryClaimForNew,
			RcmClaims currentClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		boolean pass = true;
		List<ValidateRecreateClaimResponseDto> dList = new ArrayList<>();
		try {
			if (!currentClaim.getPatientId().equals(primaryClaimForNew.getPatientId())) {
				pass = false;
			}

			if (pass) {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule326.pass.message", new Object[] {}, locale), Constants.PASS));
			} else {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule326.error.message",
								new Object[] {primaryClaimForNew.getPatientId(), currentClaim.getPatientId() }, locale),
						Constants.FAIL));
			}

		} catch (Exception n) {
			dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;
	}
	
	
	// match treating provider from current claim
	public List<ValidateRecreateClaimResponseDto> rule327(RcmRules rule, RcmClaims primaryClaimForNew,
			RcmClaims currentClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		boolean pass = true;
		List<ValidateRecreateClaimResponseDto> dList = new ArrayList<>();
		try {
			if (!currentClaim.getTreatingProvider().equals(primaryClaimForNew.getTreatingProvider())) {
				pass = false;
			}

			if (pass) {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule327.pass.message", new Object[] {}, locale), Constants.PASS));
			} else {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule327.error.message",
								new Object[] {primaryClaimForNew.getTreatingProvider(), currentClaim.getTreatingProvider() }, locale),
						Constants.FAIL));
			}

		} catch (Exception n) {
			dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;
	}
	
	// match provider on claim from current claim
	public List<ValidateRecreateClaimResponseDto> rule328(RcmRules rule, RcmClaims primaryClaimForNew,
			RcmClaims currentClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		boolean pass = true;
		List<ValidateRecreateClaimResponseDto> dList = new ArrayList<>();
		try {
			if (!currentClaim.getProviderOnClaim().equals(primaryClaimForNew.getProviderOnClaim())) {
				pass = false;
			}

			if (pass) {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule328.pass.message", new Object[] {}, locale), Constants.PASS));
			} else {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule328.error.message",
								new Object[] {primaryClaimForNew.getProviderOnClaim(), currentClaim.getProviderOnClaim() }, locale),
						Constants.FAIL));
			}

		} catch (Exception n) {
			dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;
	}
	
	// check primary status for new claim
	public List<ValidateRecreateClaimResponseDto> rule329(RcmRules rule, RcmClaims primaryClaimForNew,
			String newClaimId) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		boolean pass = true;
		List<ValidateRecreateClaimResponseDto> dList = new ArrayList<>();
		try {
			if (primaryClaimForNew == null) {
				pass = false;
			}
			if (pass) {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule329.pass.message", new Object[] {}, locale), Constants.PASS));
			} else {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule329.error.message", new Object[] {newClaimId}, locale),
						Constants.FAIL));
			}

		} catch (Exception n) {
			dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;
	}

	// new claim archive status
	public List<ValidateRecreateClaimResponseDto> rule330(RcmRules rule, RcmClaims primaryClaimForNew) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		boolean pass = true;
		List<ValidateRecreateClaimResponseDto> dList = new ArrayList<>();
		try {
			if (primaryClaimForNew.getCurrentState() == Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED) {
				pass = false;
			}

			if (pass) {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule330.pass.message", new Object[] {}, locale), Constants.PASS));
			} else {
				dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(), messageSource
						.getMessage("rule330.error.message", new Object[] { primaryClaimForNew.getClaimId() }, locale),
						Constants.FAIL));
			}

		} catch (Exception n) {
			dList.add(new ValidateRecreateClaimResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;
	}

}
