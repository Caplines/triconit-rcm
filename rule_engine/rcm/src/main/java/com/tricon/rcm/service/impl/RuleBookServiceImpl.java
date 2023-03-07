package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.TPValidationResponseDto;
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
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule301.error.message", new Object[] {memberId,ivf.getBasicInfo16()}, locale), Constants.FAIL, "",
						"", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] {n.getMessage()}, locale), Constants.FAIL, "", "",
					""));
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
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule302.error.message", new Object[] {groupNo,ivf.getBasicInfo14()}, locale), Constants.FAIL, "",
						"", ""));
			}

		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] {n.getMessage()}, locale), Constants.FAIL, "", "",
					""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}
}
