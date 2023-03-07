package com.tricon.rcm.util;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmInsuranceType;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.ClaimsFromRuleEngine;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;


public class ClaimUtil {

	private static final Logger logger = LoggerFactory.getLogger(ClaimUtil.class);
	
   /**
    * 
    * @param claims
    * @param off
    * @param re
    * @param team
    * @param user
    * @param prim
    * @param sec
    * @param cType
    * @param claimSuffix
    * @param rcmInsuranceType
    * @return
    */
	public static RcmClaims createClaimFromESData(RcmClaims claims, RcmOffice off, ClaimsFromRuleEngine re,
			RcmTeam team, RcmUser user, RcmInsurance prim, RcmInsurance sec,RcmClaimStatusType cType,String claimSuffix,
			RcmInsuranceType rcmInsuranceType,String timelyLmt,ClaimTypeEnum claimTypeEnum) {

		claims.setOffice(off);
		claims.setClaimStatusType(cType);//;mStatus("NEED TO RELOOK");// see latter
		
		if (user!=null) claims.setCreatedBy(user);
		claims.setCurrentTeamId(team);
		claims.setFirstWorkedTeamId(team);
		
		
		claims.setPatientId(re.getPatientId());
		claims.setPatientName(re.getPatientName());
		if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary)) {
			claims.setPrimInsuranceCompanyId(prim);
			claims.setPrimeSecSubmittedTotal(re.getPrimSecSubmittedTotal());
			claims.setPrimStatus(re.getPrimSecStatus());
			claims.setPrimePolicyHolder(re.getPrimeSecPolicyHolder());
			try {
				claims.setPrimePolicyHolderDob(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getBirthDate()).getTime()));
			} catch (Exception dt) {
			}
		}
         if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary)) {
        	 claims.setSecInsuranceCompanyId(sec);
        	 claims.setSecSubmittedTotal(re.getPrimSecSubmittedTotal());
     		 claims.setSecStatus(re.getPrimSecStatus());
     		 claims.setSecPolicyHolder(re.getPrimeSecPolicyHolder());
     		 
     		 claims.setPrimTotalPaid(re.getPrimTotalPaid());//extra
    		 try {
    			claims.setPrimDateSent(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getPrimDateSent()).getTime()));////extra
    		} catch (Exception dt) {
    		}
		}
		
		
		claims.setProviderId(re.getProviderId());
		claims.setRcmSource(ClaimSourceEnum.EAGLESOFT.toString());
		//claims.setRcmStatus(Constants.CLAIM_WITH_SYSTEM);
		
		claims.setSubmittedTotal(re.getSubmittedTotal());
		claims.setClaimId(re.getClaimId()+claimSuffix);
		claims.setTimelyFilingLimitData(timelyLmt);
		claims.setRcmInsuranceType(rcmInsuranceType);
		claims.setPending(true);
		claims.setPulledClaimsServiceDataFromEs(false);
		
		if (!re.getSecMemberId().equals(Constants.NO_DATA))claims.setSecMemberId(re.getSecMemberId());
		if (!re.getGroupNumber().equals(Constants.NO_DATA))claims.setGroupNumber(re.getGroupNumber());
		try {
			claims.setPatientBirthDate(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getBirthDate()).getTime()));
		} catch (Exception dt) {
		}
		

//		try {
//			claims.setSecDateSent(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getSecDateSent()).getTime()));
//		} catch (Exception dt) {
//		}
		try {
			claims.setDos(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getTranDate()).getTime()));
		} catch (Exception dt) {
		}

		return claims;
	}
	
	public static RcmClaims createClaimFromSheetData(RcmClaims claims, RcmOffice off, ClaimFromSheet re,
			RcmTeam team, RcmUser user, RcmInsurance prim, RcmInsurance sec,RcmClaimStatusType cType,String claimSuffix,
			RcmInsuranceType rcmInsuranceType,String timelyLmt,ClaimTypeEnum claimTypeEnum) {

		claims.setOffice(off);
		claims.setClaimStatusType(cType);//;mStatus("NEED TO RELOOK");// see latter
		
		if (user!=null) claims.setCreatedBy(user);
		claims.setCurrentTeamId(team);
		claims.setFirstWorkedTeamId(team);
		
		
		claims.setPatientId(re.getAccountId());
		claims.setPatientName(re.getPatientName());
		if (claimTypeEnum.getType().equals(Constants.insuranceTypePrimary)) {
			claims.setPrimInsuranceCompanyId(prim);
			 claims.setProviderId(re.getProviderIdProviderName());
			 claims.setSecMemberId(re.getPrimaryMemberId());
			 claims.setGroupNumber(re.getPrimaryGroupNumber());
			 try {
			 claims.setPrimeSecSubmittedTotal(Float.parseFloat(re.getPrimaryBilledAmount().replaceAll("[^0-9]", "")));
			 }catch(Exception q) {
				 claims.setPrimeSecSubmittedTotal(0);
			 }
			 claims.setPrimStatus(re.getPrimaryClaimStatus());
			 claims.setPrimePolicyHolder(re.getPrimaryPolicyHolderName());
			 try {
			 claims.setSubmittedTotal(Float.parseFloat(re.getPrimaryEstAmount().replaceAll("[^0-9]", "")));
			 }catch(Exception q) {
				 claims.setSubmittedTotal(0);
			 }
			 
			 try {
					claims.setPrimePolicyHolderDob(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getPrimaryPolicyHolderDob()).getTime()));
				} catch (Exception dt) {
			}
			 claims.setPrimStatus(re.getPrimaryClaimStatus());
		}
         if (claimTypeEnum.getType().equals(Constants.insuranceTypeSecondary)) {
        	 claims.setSecInsuranceCompanyId(sec);
        	 claims.setProviderId(re.getProviderIdReport());
        	 claims.setSecMemberId(re.getSecondaryMemberId());
        	 claims.setGroupNumber(re.getSecondaryGroupNumber());
        	 try {
        	 claims.setPrimeSecSubmittedTotal(Float.parseFloat(re.getSecondaryBIlledAmount().replaceAll("[^0-9]", "")));
        	 }catch(Exception q) {
				 claims.setPrimeSecSubmittedTotal(0);
			 }
     		 claims.setSecStatus(re.getSecondaryClaimStatus());
     		 claims.setSecPolicyHolder(re.getSecondaryPolicyHolder());
     		 try {
     		 claims.setPrimTotalPaid(Float.parseFloat(re.getSecondaryPaid().replaceAll("[^0-9]", "")));//extra
     		 }catch(Exception q) {
     			claims.setPrimTotalPaid(0);
     		 }
    		 try {
    			claims.setPrimDateSent(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getSecondaryClaimSubmissionDate()).getTime()));////extra
    		} catch (Exception dt) {
    		}
    		 
    		 try {
					claims.setSecPolicyHolderDob(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getSecondaryPolicyHolderDob()).getTime()));
				} catch (Exception dt) {
			}
		}
		
		
		
		claims.setRcmSource(ClaimSourceEnum.GOOGLESHEET.toString());
		//claims.setRcmStatus(Constants.CLAIM_WITH_SYSTEM);
		
		
		claims.setClaimId(re.getClaimId()+claimSuffix);
		claims.setTimelyFilingLimitData(timelyLmt);
		claims.setRcmInsuranceType(rcmInsuranceType);
		claims.setPending(true);
	
		try {
			claims.setPatientBirthDate(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getPaitentDob()).getTime()));
		} catch (Exception dt) {
		}
		

//		try {
//			claims.setSecDateSent(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getSecDateSent()).getTime()));
//		} catch (Exception dt) {
//		}
		try {
			claims.setDos(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getDos()).getTime()));
		} catch (Exception dt) {
		}

		return claims;
	}
	
	public static RcmClaimAssignment createAssginmentData(RcmClaimAssignment assignment,
			RcmUser assigneByUser,RcmUser assigneToUser,String uuid,RcmClaims claims,
			String commentsBy,RcmClaimStatusType rcmClaimStatusType) {
		
		assignment.setAssignedBy(assigneByUser);
		assignment.setAssignedTo(assigneToUser);
		assignment.setClaims(claims);
		assignment.setCommentAssignedBy(commentsBy);
		assignment.setCreatedBy(assigneByUser);
		assignment.setCurrentTeamId(assigneToUser.getTeam());
		assignment.setRcmClaimStatus(rcmClaimStatusType);
		assignment.setActive(true);
		assignment.setTakenBack(false);
		return assignment;
	}
	
	public static String getTimelyLimitFromSheetList(List<TimelyFilingLimitDto> sheetData, String name) {
		String insuranceType = null;
		if (sheetData == null) {
			logger.error("Data From Mapping sheet not found");
			return null;
		}
		Collection<TimelyFilingLimitDto> ruleGen = Collections2.filter(sheetData,
				sh -> sh.getInsuranceName().equalsIgnoreCase(name));
		for (TimelyFilingLimitDto gs : ruleGen) {
			insuranceType = gs.getTimelyFilingLimit();
			break;
		}
		if (insuranceType == null) {
			logger.error(name + " TimelyLimit Not found in  Google sheet");

		}
		return insuranceType;
	}
	
	public static  RcmTeam filterTeamByNameId(List<RcmTeam> rcmTeamList, String teamNameId) {

		Collection<RcmTeam> ruleGen = Collections2.filter(rcmTeamList, sh -> sh.getNameId().equals(teamNameId));
		return ruleGen.iterator().next();

	}
}
