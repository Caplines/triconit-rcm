package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.CredentialData;
import com.tricon.rcm.dto.ProviderCodeWithOffice;
import com.tricon.rcm.dto.TPValidationResponseDto;
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
	public List<TPValidationResponseDto> rule301(RcmRules rule, CaplineIVFFormDto ivf, String memberId) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

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
	public List<TPValidationResponseDto> rule302(RcmRules rule, CaplineIVFFormDto ivf, String groupNo) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			if (ivf == null) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule302.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			} else if (ivf.getBasicInfo14().trim().equals(groupNo == null ? "" : groupNo)) {
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
	public List<TPValidationResponseDto> rule303(RcmRules rule, CaplineIVFFormDto ivf, RcmClaims rcmClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			if (ivf == null) {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule303.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			} else {

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
	public List<TPValidationResponseDto> rule304(RcmRules rule, HashMap<String, String> doc1NameMap, RcmClaims rcmClaim,
			String claimofficeName, Object[] providerSheetData) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			String claimProvider = rcmClaim.getProviderId();
			Date claimDate = rcmClaim.getDos();
			String sheetProvider = "";
			String sheetDate = Constants.SDF_SHEET_PROVIDER_DATE.format(claimDate);
			String doc1FromProvider = doc1NameMap.get(claimofficeName + "->" + sheetDate);
			List<ProviderCodeWithOffice> pro = (List<ProviderCodeWithOffice>) providerSheetData[1];

			List<ProviderCodeWithOffice> pCodeList = pro.stream()
					.filter(e -> e.getOffice().trim().equalsIgnoreCase(claimofficeName)
							&& e.getProviderCode().trim().equalsIgnoreCase(doc1FromProvider))
					.collect(Collectors.toList());
			if (pCodeList != null && pCodeList.size() > 0) {
				sheetProvider = pCodeList.get(0).getEsCode();
			}

			if (claimProvider == null) {

				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule304.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
			} else {
				if (claimProvider.equalsIgnoreCase(sheetProvider)) {
					// pass
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule304.pass.message", new Object[] {}, locale), Constants.PASS,
							"", "", ""));
				} else {
					// Fail..
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule304.error.message",
									new Object[] { claimProvider, sheetProvider }, locale),
							Constants.FAIL, "", "", ""));
				}
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
	 * Rule For :Provider on Claim Credentialed with the insurance
	 * 
	 * @param rule
	 * @param creList
	 * @param rcmClaim
	 * @param claimofficeName
	 * @return
	 */
	public List<TPValidationResponseDto> rule305(RcmRules rule, List<CredentialData> creList, RcmClaims rcmClaim,
			String claimofficeName, Object[] providerSheetData) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			List<ProviderCodeWithOffice> pro = (List<ProviderCodeWithOffice>) providerSheetData[1];

			String claimProvider = rcmClaim.getProviderId();
			
			if (claimProvider == null) {

				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule305.error.message1", new Object[] {}, locale), Constants.FAIL, "",
						"", ""));
				return dList;
			}
			String sheetProviderCode = "";
			String applicationStatus = "";
			List<ProviderCodeWithOffice> pCodeList = pro.stream()
					.filter(e -> e.getOffice().trim().equalsIgnoreCase(claimofficeName)
							&& e.getEsCode().trim().equalsIgnoreCase(claimProvider))
					.collect(Collectors.toList());
			if (pCodeList != null && pCodeList.size() > 0) {
				sheetProviderCode = pCodeList.get(0).getProviderCode();
			}

			final String testVal = sheetProviderCode;
			List<CredentialData> filterCodeList = creList.stream()
					.filter(e -> e.getLocation().trim().equalsIgnoreCase(claimofficeName)
							&& e.getPlanType().trim().equalsIgnoreCase(rcmClaim.getRcmInsuranceType().getName())
							&& e.getProviderCode().trim().equalsIgnoreCase(testVal))
					.collect(Collectors.toList());

			
				if (filterCodeList != null && filterCodeList.size() > 0) {
					applicationStatus = filterCodeList.get(0).getApplicationStatus().toLowerCase();
				}

				if (applicationStatus.contains("completed")) {
					// pass
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule305.pass.message", new Object[] {}, locale), Constants.PASS,
							"", "", ""));

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
}
