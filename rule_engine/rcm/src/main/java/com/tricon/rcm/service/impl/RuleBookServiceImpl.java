package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Strings;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.dto.CaplineIVFFormDto;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.CredentialData;
import com.tricon.rcm.dto.CredentialDataAnesthesia;
import com.tricon.rcm.dto.ProviderCodeWithOffice;
import com.tricon.rcm.dto.TPValidationResponseDto;
import com.tricon.rcm.dto.customquery.DataPatientRuleDto;
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
//System.out.println(ivf.getBasicInfo16());
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
	public List<TPValidationResponseDto> rule303(RcmRules rule, DataPatientRuleDto ivf, RcmClaims rcmClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass= true;
		String[] clT = rcmClaim.getClaimId().split("_");
		//String claimSubTy = Constants.insuranceTypeSecondary;// May be needed latter
		boolean primary=true;
		if (("_" + clT[1]).equals(ClaimTypeEnum.P.getSuffix())) {
			primary=true;
			
		} else {
			 primary=false;
			
		}
		List<String> errorMessage=null;
		try {

			if (ivf == null) {
			
				if (errorMessage==null) errorMessage= new ArrayList<>();
				errorMessage.add("IV Not Found");
				pass= false;
			} else {
				if (!rcmClaim.getPatientName().trim().equalsIgnoreCase(ivf.getBasicInfo2())) {
					if (errorMessage==null) errorMessage= new ArrayList<>();
					errorMessage.add("Claim Patient name: "+rcmClaim.getPatientName() +"; IV Patient name: "+ivf.getBasicInfo2());
					pass= false;
					//Fail
				}
				if (!Constants.SDF_ES_DATE.format(rcmClaim.getPatientBirthDate()).equals(ivf.getBasicInfo6())) {
					if (errorMessage==null) errorMessage= new ArrayList<>();
					errorMessage.add("Claim Patient DOB: "+Constants.SDF_ES_DATE.format(rcmClaim.getPatientBirthDate()) +"; Claim Patient DOB: "+ivf.getBasicInfo6());
					pass= false;
					//Fail
				}
				if (primary) {
					if (!rcmClaim.getPrimePolicyHolder().trim().equalsIgnoreCase(ivf.getBasicInfo5())) {
						if (errorMessage==null) errorMessage= new ArrayList<>();
						errorMessage.add("Claim PolicyHolder: "+rcmClaim.getPrimePolicyHolder() +"; IV PolicyHolder: "+ivf.getBasicInfo5());
						pass= false;
						//Fail
					}
				}else {
					if (!rcmClaim.getSecPolicyHolder().trim().equalsIgnoreCase(ivf.getBasicInfo5())) {
						if (errorMessage==null) errorMessage= new ArrayList<>();
						errorMessage.add("Claim PolicyHolder: "+rcmClaim.getSecPolicyHolder() +"; IV PolicyHolder: "+ivf.getBasicInfo5());
						pass= false;
						//Fail
					}
				}
			
				if (pass) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule303.pass.message", new Object[] {}, locale), Constants.PASS, "",
							"", ""));
				}else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule303.error.message", new Object[] {String.join(",", errorMessage)}, locale), Constants.FAIL, "",
							"", ""));
				}
				
			}
			
			if (!pass) {
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
	public List<TPValidationResponseDto> rule304(RcmRules rule,  RcmClaims rcmClaim	 ) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		try {

			String claimProvider = rcmClaim.getProviderOnClaim();
			String sheetProvider =rcmClaim.getTreatingProvider();
			
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
	
	
	public List<TPValidationResponseDto> rule306(RcmRules rule, DataPatientRuleDto dto, RcmClaims rcmClaim) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());

		List<TPValidationResponseDto> dList = new ArrayList<>();
		
		try {

			
					// pass
			if (dto!=null && dto.getPlanAssignmentofBenefits()!=null && dto.getPlanAssignmentofBenefits().equalsIgnoreCase("yes")) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule306.pass.message", new Object[] {}, locale), Constants.PASS,
							"", "", ""));

			} else if (dto!=null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule306.error.message", new Object[] { dto.getPlanAssignmentofBenefits() }, locale),
							Constants.FAIL, "", "", ""));
			}else {
				dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
						messageSource.getMessage("rule306.error.message1", new Object[] { "IV not found." }, locale),
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
	
	public List<TPValidationResponseDto> rule307(RcmRules rule,Set<String> claimCodes) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
		List<String> list= new ArrayList<>();
		String array[]  = { "D0603", "D0602", "D0603" };
		 Collections.addAll(list, array);
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean pass=false;
		try {

			   for(String code:claimCodes ) {
				   if (list.contains(code)){
					   pass=true;
					   break;
				   }
			   }

				if (pass) {
					// pass
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule307.pass.message", new Object[] {}, locale), Constants.PASS,
							"", "", ""));

				} else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule307.error.message", new Object[] {  }, locale),
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
	
	public List<TPValidationResponseDto> rule308(RcmRules rule,Set<String> claimCodes,
			String providerCode,List<CredentialDataAnesthesia> sheetData,Date claimDos) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
		
		String cd  = "D0145";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean found=false;
		boolean pass=false;
		try {

			   for(String code:claimCodes ) {
				   if (code.equalsIgnoreCase(cd)){
					   found=true;
					   break;
				   }
			   }

				if (found) {
					List<CredentialDataAnesthesia> filterList = sheetData.stream().filter(c -> c.getProviderCodes().equals(providerCode))
					.collect(Collectors.toList());
					if (filterList.size()>0) {
					 String effDate = filterList.get(0).getFDHEffectiveDate();
					 String firstHome = filterList.get(0).getFirstHomeD0145();
					 if (firstHome.equalsIgnoreCase("yes")) {
						 if (!effDate.equals("")) {
							Date eff=  Constants.SDF_CredentialSheetAnes.parse(effDate);
							if (eff.compareTo(claimDos)<0) {
								pass = true;
							}
						 }
					 }else {
						 pass = false;//no is fail
					 }
					 pass = false;//not listed then Fail.
					}
					// pass
					

				} else {
					// N/A
					pass = true;
					
				}
				
				if (pass) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule308.pass.message", new Object[] {  }, locale),
							Constants.PASS, "", "", ""));
				}else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule308.error.message", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
				}
			
		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}
	
	public List<TPValidationResponseDto> rule309(RcmRules rule,Set<String> claimCodes,
			String providerCode,List<CredentialDataAnesthesia> sheetData) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
		
		String cd  = "D9230";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean found=false;
		boolean pass=false;
		try {

			   for(String code:claimCodes ) {
				   if (code.equalsIgnoreCase(cd)){
					   found=true;
					   break;
				   }
			   }

				if (found) {
					List<CredentialDataAnesthesia> filterList = sheetData.stream().filter(c -> c.getProviderCodes().equals(providerCode))
					.collect(Collectors.toList());
					if (filterList.size()>0) {
					
					 String firstHome = filterList.get(0).getD9230Nirtrous();
					 if (firstHome.equalsIgnoreCase("yes")) {
						 pass = true;
					 }else {
						 pass = false;//no is fail
					 }
					 pass = false;//not listed then Fail.
					}
					// pass
					

				} else {
					// N/A
					pass = true;
					
				}
				
				if (pass) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule309.pass.message", new Object[] {  }, locale),
							Constants.PASS, "", "", ""));
				}else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule309.error.message", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
				}
			
		} catch (Exception n) {
			dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
					messageSource.getMessage("rule.error.exception", new Object[] { n.getMessage() }, locale),
					Constants.FAIL, "", "", ""));
		}

		logger.info(RuleConstants.rule_log_exit + rule.getName());
		return dList;

	}
	
	public List<TPValidationResponseDto> rule310(RcmRules rule,Set<String> claimCodes,
			String providerCode,List<CredentialDataAnesthesia> sheetData) {

		logger.info(RuleConstants.rule_log_enter + "-" + rule.getName());
		
		String cd  = "D9248";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		boolean found=false;
		boolean pass=false;
		try {

			   for(String code:claimCodes ) {
				   if (code.equalsIgnoreCase(cd)){
					   found=true;
					   break;
				   }
			   }

				if (found) {
					List<CredentialDataAnesthesia> filterList = sheetData.stream().filter(c -> c.getProviderCodes().equals(providerCode))
					.collect(Collectors.toList());
					if (filterList.size()>0) {
					
					 String firstHome = filterList.get(0).getD9248Anesthesia();
					 if (firstHome.equalsIgnoreCase("yes")) {
						 pass = true;
					 }else {
						 pass = false;//no is fail
					 }
					 pass = false;//not listed then Fail.
					}
					// pass
					

				} else {
					// N/A
					pass = true;
					
				}
				
				if (pass) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule310.pass.message", new Object[] {  }, locale),
							Constants.PASS, "", "", ""));
				}else {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule310.error.message", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
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
