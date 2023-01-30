package com.tricon.rcm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.service.impl.RuleEngineService;

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
			RcmInsuranceType rcmInsuranceType) {

		claims.setOffice(off);
		claims.setClaimStatusType(cType);//;mStatus("NEED TO RELOOK");// see latter
		
		if (user!=null) claims.setCreatedBy(user);
		claims.setCurrentTeamId(team);
		claims.setPrimInsuranceCompanyId(prim);
		claims.setSecInsuranceCompanyId(sec);
		claims.setPatientId(re.getPatientId());
		claims.setPatientName(re.getPatientName());
		claims.setPrimeSubmittedTotal(re.getPrimSubmittedTotal());
		claims.setPrimStatus(re.getPrimStatus());
		claims.setPrimTotalPaid(re.getPrimTotalPaid());
		claims.setProviderId(re.getProviderId());
		claims.setRcmSource(ClaimSourceEnum.EAGLESOFT.toString());
		//claims.setRcmStatus(Constants.CLAIM_WITH_SYSTEM);
		claims.setSecStatus(re.getSecStatus());
		claims.setSecSubmittedTotal(re.getSecSubmittedTotal());
		claims.setSubmittedTotal(re.getSubmittedTotal());
		claims.setClaimId(re.getClaimId()+claimSuffix);
		claims.setPending(true);
		try {
			claims.setPatientBirthDate(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getBirthDate()).getTime()));
		} catch (Exception dt) {
		}
		try {
			claims.setPrimDateSent(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getPrimDateSent()).getTime()));
		} catch (Exception dt) {
		}

		try {
			claims.setSecDateSent(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getSecDateSent()).getTime()));
		} catch (Exception dt) {
		}
		try {
			claims.setDos(new java.sql.Date(Constants.SDF_ES_DATE.parse(re.getTranDate()).getTime()));
		} catch (Exception dt) {
		}

		return claims;
	}
	
	public static RcmClaims createClaimFromSheetData(RcmClaims claims, RcmOffice off, ClaimFromSheet re,
			RcmTeam team, RcmUser user, RcmInsurance prim, RcmInsurance sec,RcmClaimStatusType cType,
			RcmInsuranceType rcmInsuranceType1) {
        try {
		claims.setOffice(off);
		claims.setClaimStatusType(cType);
		claims.setClaimId(re.getClaimId());
		
		claims.setCreatedBy(user);
		claims.setCurrentTeamId(team);
		claims.setPrimInsuranceCompanyId(prim);
		claims.setSecInsuranceCompanyId(sec);
		claims.setPatientId(re.getPatientId());
		claims.setPatientName(re.getPatientName());
		claims.setPrimeSubmittedTotal(Float.parseFloat(re.getInsuranceEstimatedAmount()));
		claims.setPrimStatus("");
		//re.getInsuranceEstimatedAmount()
		claims.setPrimTotalPaid(Float.parseFloat(re.getInsuranceEstimatedAmount()));
		claims.setProviderId(re.getProvider());
		claims.setRcmSource(ClaimSourceEnum.GOOGLESHEET.toString());
		//claims.setRcmTeamStatus(Constants.CLAIM_WITH_SYSTEM);
		claims.setSecStatus("");
		claims.setSecSubmittedTotal(Float.parseFloat(re.getInsuranceEstimatedAmount()));
		claims.setSubmittedTotal(Float.parseFloat(re.getTotalBilled()));
		claims.setPending(true);
		try {
			claims.setPatientBirthDate(new java.sql.Date(Constants.SDF_SHEET_DATE.parse(re.getSubscriberDob()).getTime()));
		} catch (Exception dt) {
		}
		try {
			//claims.setPrimDateSent(new java.sql.Date(Constants.SDF_SHEET_DATE.parse(re.getPrimDateSent()).getTime()));
		} catch (Exception dt) {
		}

		try {
			//claims.setSecDateSent(new java.sql.Date(Constants.SDF_SHEET_DATE.parse(re.getSecDateSent()).getTime()));
		} catch (Exception dt) {
		}
		try {
			claims.setDos(new java.sql.Date(Constants.SDF_SHEET_DATE.parse(re.getDateOfService()).getTime()));
		} catch (Exception dt) {
		}
		} catch (Exception e) {
			logger.info(e.getMessage());
			logger.info("Error  in  "+re.getClaimId());
			
			claims=null;
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
		return assignment;
	}
}
