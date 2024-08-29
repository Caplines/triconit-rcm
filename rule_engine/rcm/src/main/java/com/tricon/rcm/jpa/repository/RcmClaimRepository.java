package com.tricon.rcm.jpa.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.IVFDto;
import com.tricon.rcm.dto.customquery.IssueClaimDto;
import com.tricon.rcm.dto.customquery.PendingClaimToReAssignDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.ProductionForAging;
import com.tricon.rcm.dto.customquery.ProductionForPatientCalling;
import com.tricon.rcm.dto.customquery.ProductionForPatientStatement;
import com.tricon.rcm.dto.customquery.ProductionForPaymentPosting;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
import com.tricon.rcm.dto.customquery.ReconcillationClaimDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.dto.customquery.RcmClaimDataDto;
import com.tricon.rcm.dto.customquery.AllPendencyDateDto;
import com.tricon.rcm.dto.customquery.AllPendencyDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.ClaimXDaysDto;
import com.tricon.rcm.dto.customquery.DataPatientRuleDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;

public interface RcmClaimRepository extends JpaRepository<RcmClaims, String> {

	RcmClaims findByClaimIdAndOffice(String claimId, RcmOffice office);

	//RcmClaims findByClaimId(String claimId);

	RcmClaims findByClaimUuid(String claimId);

	List<RcmClaims> findByClaimIdInAndOffice(List<String> claimIds, RcmOffice office);

	String nextActionAndRebillProductionjoin="left join rcm_next_action_required_section nextAction on nextAction.claim_uuid=claims.claim_uuid "
			+ "left join rcm_request_rebilling_section rebillingSection on rebillingSection.claim_uuid=claims.claim_uuid ";
	String nextActionAndRebillProductionwhere=" ( ( nextAction.team_id=:teamId and nextAction.final_submit=true  and "//nextAction.current_claim_status_rcm=:claimStaus
			+ "CAST(nextAction.created_date as DATE) between STR_TO_DATE(:startDate, '%Y-%m-%d') "
			+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and us.uuid=nextAction.created_by)"
			+ " or "
			+ " ( rebillingSection.team_id=:teamId and "
			+ " CAST(rebillingSection.created_date as DATE) between STR_TO_DATE(:startDate, '%Y-%m-%d') "
			+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and us.uuid=rebillingSection.created_by ) )";
	
	String nextActionAndRebillProductionwherewithuser=" ( ( nextAction.team_id=:teamId and nextAction.final_submit=true  and "//nextAction.current_claim_status_rcm=:claimStaus
			+ "CAST(nextAction.created_date as DATE) between STR_TO_DATE(:startDate, '%Y-%m-%d') "
			+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and nextAction.created_by=:userId)"
			+ " or "
			+ " ( rebillingSection.team_id=:teamId and "
			+ " CAST(rebillingSection.created_date as DATE) between STR_TO_DATE(:startDate, '%Y-%m-%d') "
			+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and rebillingSection.created_by=:userId) )";
	String s=" SELECT off.name as officeName,"
			+ "sum(Case When claim_status_type_id=:status and pending is true Then 1 ELSE 0 End) as 'count' , "
			+ "min(Case When claim_status_type_id=:status Then date (cl.created_date) End)   as 'opdt', "
			+ "min(Case When claim_status_type_id=:status Then cl.dos End)   as 'opdos', "
			+ "off.uuid as officeUuid,0 as remoteLiteRejections FROM  office off left join rcm_claims  cl "
			+ "on off.uuid=cl.office_id where cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and off.company_id=:companyId and ( cl.current_team_id=:teamId  or  current_team_id is null )" + "group by off.uuid";
	
	@Query(nativeQuery = true, value = s)
	List<FreshClaimDetailsDto> fetchBillingOrReBillingClaimDetails(@Param("companyId") String companyId,
			@Param("status") int status,@Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = "SELECT off.name as officeName, "
			+ "sum (Case When assign.status_id=:status and pending is true Then 1 ELSE 0 End) as 'count' , "
			+ "min(Case When  claim_status_type_id=:status Then date(cl.created_date) End)   as 'opdt', "
			+ "min(Case When claim_status_type_id=:status Then cl.dos End)   as 'opdos', "
			+ "off.uuid as officeUuid,0 as remoteLiteRejections FROM  office off left join rcm_claims  cl "
			+ " on off.uuid=cl.office_id "
			+ " left join rcm_claim_assignment  assign on  cl.claim_uuid=assign.claim_id "
			+ " and assign.assigned_to=:assignedTo "
			+ " where off.company_id=:companyId "
			+ " and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
	        + " and (cl.current_team_id=:teamId or  cl.current_team_id is null) "
			+ " and (assign.active is true or assign.active is null ) "
			+ " group by off.uuid" )
	List<FreshClaimDetailsDto> fetchBillingOrReBillingClaimDetails(@Param("companyId") String companyId,
			@Param("status") int status,@Param("teamId") int teamId,@Param("assignedTo") String assignedTo);

	@Query(nativeQuery = true, value = "select off.uuid as officeUuid,off.name as officeName, "
			+ "Case when l.created_date is null then null  ELSE l.created_date END as cd, "
			+ "Case when new_claims_count is null then 0  ELSE new_claims_count END as newClaimsCount, "
			+ "Case when l.source is null then ''  ELSE l.source END as source, "
			+ "Case when l.status is null then 0  ELSE l.status END as status " + " from office off left join (  "
			+ " select created_date,created_by,new_claims_count,source,office_id,status from rcm_claim_log where id in ( "
			+ " select max(id) as id from  rcm_claim_log log group by log.office_id))  l "
			+ " on off.uuid=l.office_id "
			+ " inner join company cmp on cmp.uuid=off.company_id where cmp.uuid=:companyId and off.active is true order by off.name asc ")
	List<FreshClaimLogDto> fetchFreshClaimLogs(@Param("companyId") String companyId);

	@Query(nativeQuery = true, value = ""
			+ "select * from ("
	        +" select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+" claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+"  CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,"
			+ " timely_fil_lmt_dt as timelyFilingLimitData, "
			+ " claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal, "
			+ " case when assign.pending_since is not null then assign.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,claims.next_action as nextAction,claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " left join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id  and last_work_team_id!=:teamid "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " left join rcm_claim_assignment assign on claims.claim_uuid=assign.claim_id and assign.current_team_id=:teamid and assign.active=1 "
			+ " where claims.first_worked_team_id=:teamid and off.active is true and claims.last_work_team_id is null and claims.current_team_id=:teamid  and off.company_id=:companyId " + " and pending=true"
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+ " and  claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ " and (primary_status = "+Constants.Primary_Status_Primary+" or primary_status ="+Constants.Primary_Status_Primary_submit+" )  "
		
			+ " union "
			
			+" select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+" claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+"  CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,"
			+ " timely_fil_lmt_dt as timelyFilingLimitData, "
			+ " claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal, "
			+ " case when assign.pending_since is not null then assign.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,claims.next_action as nextAction,claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " left join rcm_claim_assignment assign on claims.claim_uuid=assign.claim_id and assign.current_team_id=:teamid and assign.active=true  "
			+ " where claims.first_worked_team_id<>:teamid and off.active is true and claims.last_work_team_id!=:teamid and claims.current_team_id=:teamid  and off.company_id=:companyId " + " and pending=true"
			+ " and  claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+ " and (primary_status = "+Constants.Primary_Status_Primary+" or primary_status = "+Constants.Primary_Status_Primary_submit+" ))a order by ust-claimAge,primeSecSubmittedTotal asc  "
						
			+ " ")
	List<FreshClaimDataDto> fetchFreshClaimDetails(@Param("companyId") String companyId, @Param("teamid") int teamid);
	
	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal, prime_sec_submitted_total primeSecSubmittedTotal, "
			+ " case when rca.pending_since is not null then rca.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,claims.next_action as nextAction,"
			+ " claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid "
			+ " inner join rcm_user ru on ru.uuid=rca.assigned_to "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid  and off.company_id=:companyId and off.active is true and rca.assigned_to=:userid and rca.active=1  and pending=true"
			+ " and claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+"  and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" "
			+ " and (primary_status ="+Constants.Primary_Status_Primary+" or primary_status ="+Constants.Primary_Status_Primary_submit+"  ) order by ust-claimAge,primeSecSubmittedTotal asc ")
	List<FreshClaimDataDto> fetchFreshClaimDetailsInd(@Param("companyId") String companyId, @Param("teamid") int teamid,@Param("userid") String userId);
	
	
	/**
	 * The claims on which first Internal Audit Team has worked and are now in bucket of Billing Team, they 
	 * should  be shown under "Fresh Claims" and not "Claims Sent back by Other Teams" because it is fresh for
	 *  the Billing Team. This section "Claims Sent back by Other Teams" is applicable when the claim is sent
	 *   to Billing Team by other teams apart from Internal Audit Team
	 * @param companyId
	 * @param teamid
	 * @return
	 */
	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,claims.claim_type providerSpeciality,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal,case when rca.pending_since is not null then rca.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,"
			+ " claims.next_action as nextAction,claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and off.active is true and claims.last_work_team_id!=:teamid and off.company_id=:companyId " + "  and rca.active=true "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+ " and claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ " order by ust-claimAge,primeSecSubmittedTotal asc  ")
	List<FreshClaimDataDto> fetchClaimDetailsWorkedByTeamBilling(@Param("companyId") String companyId, @Param("teamid") int teamid);

	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,claims.claim_type providerSpeciality,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal,case when rca.pending_since is not null then rca.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,claims.next_action as nextAction,"
			+ " claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and claims.last_work_team_id!=:teamid and off.active is true and off.company_id=:companyId " + " and  rca.active=1"
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+ " and claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ " order by ust-claimAge,primeSecSubmittedTotal asc ")
	List<FreshClaimDataDto> fetchClaimDetailsWorkedByTeamInternalAudit(@Param("companyId") String companyId, @Param("teamid") int teamid);


	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,ct.status as claimStatus,claims.claim_type providerSpeciality,insurance.name as primaryInsurance,prime_sec_submitted_total primeSecSubmittedTotal "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,DATEDIFF(sysdate(),claims.dos) as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,case when rca.pending_since is not null then rca.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,"
			+ " claims.next_action as nextAction,claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_claim_status_type ct on ct.id=claims.current_status "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid and rca.active=1 "
			+ "  where claims.current_team_id=:teamid and off.active is true and off.company_id=:companyId " + " and pending=true "
			+"  and  claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
					+ " order by ust-claimAge,primeSecSubmittedTotal asc  ")
	List<FreshClaimDataDto> fetchFreshClaimDetailsOtherTeam(@Param("companyId") String companyId,
			@Param("teamid") int teamid);
	
	@Query(nativeQuery = true, value = " select off.name as officeName,rca.updated_date as updatedDate,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,ct.status as claimStatus,insurance.name as primaryInsurance,prime_sec_submitted_total primeSecSubmittedTotal "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,claims.claim_type providerSpeciality,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,DATEDIFF(sysdate(),claims.dos) as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,case when rca.pending_since is not null then rca.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,claims.next_action as nextAction,claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance ,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_claim_status_type ct on ct.id=claims.current_status"
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid and rca.active=1 "
			+ "  where claims.current_team_id=:teamid and off.active is true and off.company_id=:companyId " + " and pending=false "
			+ " and claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
					+ " order by ust-claimAge,primeSecSubmittedTotal asc  ")
	List<FreshClaimDataDto> fetchSubmittedClaimDetailsOtherTeam(@Param("companyId") String companyId,
			@Param("teamid") int teamid);
	
	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,claims.claim_type providerSpeciality,insurance.name as primaryInsurance "
			+ " ,prime_sec_submitted_total primeSecSubmittedTotal ,ct.status as claimStatus,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,DATEDIFF(sysdate(),claims.dos) as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,case when rca.pending_since is not null then rca.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,"
			+ " claims.next_action as nextAction,claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance ,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_claim_status_type ct on ct.id=claims.current_status"
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid and rca.active=1 "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and off.active is true and claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ "  and off.company_id=:companyId and rca.assigned_to=:userId and pending=true "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED +" order by ust-claimAge asc "
			+ "")
	List<FreshClaimDataDto> fetchFreshClaimDetailsOtherTeamInd(@Param("companyId") String companyId,
			@Param("teamid") int teamid,@Param("userId") String userId);
	
	@Query(nativeQuery = true, value = " select off.name as officeName,rca.updated_date as updatedDate,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,ct.status as claimStatus,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName,claims.claim_type providerSpeciality,"
			+ " lastteam.name as lastTeam,DATEDIFF(sysdate(),claims.dos) as claimAge,prime_sec_submitted_total primeSecSubmittedTotal, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,case when rca.pending_since is not null then rca.pending_since else claims.created_date end as pendingSince,claims.status_es as statusES,claims.status_es_updated as statusESUpdated,claims.next_action as nextAction,claims.next_follow_up_date as followUpDate,claims.balance_from_es_after_posting as dueBalance ,claims.is_primary as claimTypeStatus from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id"
			+ " left join rcm_claim_status_type ct on ct.id=claims.current_status "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid and rca.active=1 "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and off.active is true and off.company_id=:companyId and rca.assigned_to=:userId and pending=false "
			+ " and claims.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED +" order by ust-claimAge asc "
			+ "")
	List<FreshClaimDataDto> fetchSubmittedClaimDetailsOtherTeamInd(@Param("companyId") String companyId,
			@Param("teamid") int teamid,@Param("userId") String userId);

	@Deprecated
	@Query(nativeQuery = true, value = " SELECT cmp.name as companyName,off.name as officeName,off.uuid as  officeUuid,"
			+ " count(Case When claim_status_type_id in :status  and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.current_team_id=:teamId and cl.rcm_insurance_type in :inst Then 'bill' End) as 'count', "
			+ " min(Case When claim_status_type_id in :status  and  cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.current_team_id=:teamId  and cl.rcm_insurance_type in :inst Then  DATEDIFF(NOW(), cl.created_date) End)   as 'opdt',"
			+ " min(Case When claim_status_type_id in :status  and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.current_team_id=:teamId  and cl.rcm_insurance_type in :inst Then  DATEDIFF(NOW(), cl.dos) End) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName,assig.team_id as assignTeamId " + " FROM "
			+ "  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  inner join company cmp on cmp.uuid=off.company_id  "
			+ "  inner join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId and assig.user_id=:userId "
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where  off.company_id in (:companyIds) and off.active is true "
			+ "  and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" "
			+ "  and cl.current_status not in (:currentStatusClosed,:currentStatusVoided)"
			+ "  group by off.uuid order by companyName asc,opdos desc ")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignmentsByTeamAndUser(
			@Param("companyIds") List<String> companyIds,@Param("status") List<Integer> status, @Param("inst") Set<Integer> inst,@Param("teamId") int teamId,@Param("userId") String userId,@Param("currentStatusClosed")int currentStatusClosed,@Param("currentStatusVoided")int currentStatusVoided);
	
	@Deprecated
	@Query(nativeQuery = true, value = " SELECT  cmp.name as companyName,off.name as officeName,off.uuid as  officeUuid,"
			+ " count(Case When claim_status_type_id in :status and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.current_team_id=:teamId and cl.rcm_insurance_type in :inst Then 'bill' End) as 'count', "
			+ " min(Case When claim_status_type_id in :status and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.current_team_id=:teamId  and cl.rcm_insurance_type in :inst Then  DATEDIFF(NOW(), cl.created_date) End)   as 'opdt',"
			+ " min(Case When claim_status_type_id in :status and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.current_team_id=:teamId  and cl.rcm_insurance_type in :inst Then  DATEDIFF(NOW(), cl.dos) End) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName,assig.team_id as assignTeamId " + " FROM "
			+ "  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			+ "  inner join company cmp on cmp.uuid=off.company_id  "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  left join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId "
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where off.company_id in (:companyIds) and off.active is true and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" "
			+ "  and cl.current_status not in (:currentStatusClosed,:currentStatusVoided) group by off.uuid order by companyName asc,opdos desc ")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignmentsByTeam(@Param("companyIds") List<String> companyIds,@Param("status") List<Integer> status,
			@Param("inst") Set<Integer> inst,@Param("teamId") int teamId,@Param("currentStatusClosed")int currentStatusClosed,@Param("currentStatusVoided")int currentStatusVoided);
	
	@Query(nativeQuery = true, value = " SELECT  cmp.name as companyName,off.name as officeName,off.uuid as  officeUuid,"
			+ " cl.claim_id as claimId, Case When cl.is_primary Then 1 ELSE 0 End as primaryC,Case When cl.pending Then 1 ELSE 0 End as pending, "
			+ " DATEDIFF(NOW(), cl.created_date)  as 'opdt',"
			+ " DATEDIFF(NOW(), cl.dos) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName,assig.team_id as assignTeamId "
			+ " from  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			+"  and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+"" 
		    + " and cl.rcm_insurance_type in :inst   "
		    + " and cl.claim_status_type_id in :status and cl.current_team_id=:teamId and " 
		    + "  cl.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ "  inner join company cmp on cmp.uuid=off.company_id  "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  left join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId "
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where off.company_id in (:companyIds) and off.active is true  order by companyName ")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignmentsByTeamAndUserType(@Param("companyIds") List<String> companyIds,@Param("status") List<Integer> status,
			@Param("inst") Set<Integer> inst,@Param("teamId") int teamId);

	
	@Query(nativeQuery = true, value = " SELECT  cmp.name as companyName,off.name as officeName,off.uuid as  officeUuid,"
			+ " cl.claim_id as claimId, Case When cl.is_primary Then 1 ELSE 0 End as primaryC,Case When cl.pending Then 1 ELSE 0 End as pending, "
			+ " DATEDIFF(NOW(), cl.created_date)  as 'opdt',"
			+ " DATEDIFF(NOW(), cl.dos) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName,assig.team_id as assignTeamId "
			+ " from  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			
            + " and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" "
			+ " and cl.rcm_insurance_type in :inst   "
			+ "  and cl.claim_status_type_id in :status and cl.current_team_id=:teamId  and "
			+ "  cl.current_status<>"+Constants.CLAIM_CLOSED+" "
			+ "  inner join company cmp on cmp.uuid=off.company_id  "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  left join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId"
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where off.company_id in (:companyIds) and off.active is true order by companyName")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignmentsByTeamType(@Param("companyIds") List<String> companyIds,@Param("status") List<Integer> status,
			@Param("inst") Set<Integer> inst,@Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = " SELECT off.name as officeName,off.uuid as  officeUuid,"
			+ " count(Case When claim_status_type_id in :status and pending is true and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.rcm_insurance_type in :inst Then 'bill' End) as 'count', "
			+ " min(Case When claim_status_type_id in :status and pending=true  and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.rcm_insurance_type in :inst Then cl.created_date End)   as 'opdt',"
			+ " min(Case When claim_status_type_id in :status and pending=true  and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.rcm_insurance_type in :inst Then cl.dos End) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName,assig.team_id as assignTeamId " + " FROM "
			+ "  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  left join rcm_user_assign_office assig on assig.office_id=off.uuid "
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where off.company_id=:companyId and off.active is true  group by off.uuid,assig.team_id")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignments(@Param("companyId") String companyId,
			@Param("status") List<Integer> status, @Param("inst") Set<Integer> inst);
	
	// count(distinct DATE(assign.created_date)) as days
	// Production means Total Claims Submitted and No days between 2 dates
	// use refrence from Ruleengine RcmClaimDaoImpl->>
	// Constants.QUERY_FOR_RCMCALIM_1
	@Query(nativeQuery = true, value = "select count(distinct claim_uuid) as total,FLOOR(count(distinct claim_uuid))/count(distinct cast(dats as date)) as days,"
			+ "count(distinct dats) as disDate,w.uuid as uuid,w.fName as fName,w.lName as lName,w.companyName as companyName from ( "
			+ "select  cl.claim_uuid as claim_uuid,rcsd.es_date as dats ,"
			+ "us.uuid as uuid,us.first_name "
			+ "as fName,us.last_name as lName,comp.name as companyName  "
			+" from rcm_claims_submission_details rcsd inner join "
			+ " rcm_user us on  rcsd.submitted_by=us.uuid  "
			+ "left join rcm_claims cl on rcsd.claim_id=cl.claim_uuid  " + "inner join office off on off.uuid=cl.office_id "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
			+ "inner join company comp on comp.uuid=cmp.company_id "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ "where cl.pending =false and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
			+ "and cast(rcsd.es_date as date) "//IF(rcsd.updated_date is null, rcsd.created_date, rcsd.updated_date)
			+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid,cl.claim_uuid "
			+" union all "
			+ "select cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
			+ "us.uuid as uuid,us.first_name "
			+ "as fName,us.last_name as lName,comp.name as companyName  "
			+" from rcm_rebilling_section rcsd inner join "
			+ " rcm_user us on  rcsd.created_by=us.uuid and rcsd.is_rebilling is true  "
			+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid inner join office off on off.uuid=cl.office_id "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
			+ "inner join company comp on comp.uuid=cmp.company_id "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ "where rcsd.team_id=:teamId and  cl.pending =false and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
			+ " and cast(rcsd. created_date as date) "//IF(rcsd.updated_date is null, rcsd.created_date, rcsd.updated_date)
			+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid,cl.claim_uuid "
		    +" union all "
				+ "select  cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
				+ "us.uuid as uuid,us.first_name "
				+ "as fName,us.last_name as lName,comp.name as companyName "+
				" from rcm_next_action_required_section rcsd  inner join "+
				" rcm_user us on  rcsd.created_by=us.uuid  and rcsd.final_submit is true "
				+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
				+ "inner join company comp on comp.uuid=cmp.company_id "
				+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
				+ "where rcsd.team_id=:teamId and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "//cl.pending =false and
				+ " and  cmp.company_id in (:companyIds) and rut.team_id=:teamId "
				+ " and cast(rcsd.created_date as date) "
				+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid,cl.claim_uuid "

			+" ) as w  group by companyName,uuid "
			
			+ "")
	List<ProductionDto> claimProductionByForBilling(@Param("companyIds") List<String> companyIds,
			@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate);
	
	// Production can be considered on the basis of processing of claim in ES
	// instead of when
	// Production can be considered on the basis of processing of claim in ES
	// instead of when we are marking that as submitted in the RCM Tool
	// we are marking that as submitted in the RCM Tool
	// use refrence from Ruleengine RcmClaimDaoImpl->>
	// Constants.QUERY_FOR_RCMCALIM_1
	@Query(nativeQuery = true, value = "select count(distinct claim_uuid) as total,FLOOR(count(distinct claim_uuid))/count(distinct cast(dats as date)) as days,"
			+ "count(distinct dats) as disDate,w.uuid as uuid,w.fName as fName,w.lName as lName,w.companyName as companyName from ( "
			+ "select  cl.claim_uuid as claim_uuid,rcsd.es_date as dats ,"
			+ "us.uuid as uuid,us.first_name "
			+ "as fName,us.last_name as lName,comp.name as companyName "+
			" from rcm_claims_submission_details rcsd inner join "+
			" rcm_user us on  rcsd.submitted_by=us.uuid "
			+ "left join rcm_claims cl on rcsd.claim_id=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
			+ "inner join company comp on comp.uuid=cmp.company_id "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ " left join rcm_user_assign_office assig on assig.user_id=us.uuid  and assig.team_id=:teamId and assig.user_id=:userId "
			+ "where cl.pending =false and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "
			+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
			+ "and cast(rcsd.es_date as date) "
			+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid "
			+" union all "
			+ "select  cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
			+ "us.uuid as uuid,us.first_name "
			+ "as fName,us.last_name as lName,comp.name as companyName "+
			" from rcm_rebilling_section rcsd inner join "+
			" rcm_user us on  rcsd.created_by=us.uuid  and rcsd.is_rebilling is true "
			+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
			+ "inner join company comp on comp.uuid=cmp.company_id "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ " left join rcm_user_assign_office assig on assig.user_id=us.uuid  and assig.team_id=:teamId and assig.user_id=:userId "
			+ "where  rcsd.team_id=:teamId and  cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "//cl.pending =false and
			+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
			+ " and cast(rcsd.created_date as date) "
			+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid "
			+" union all "
			+ "select  cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
			+ "us.uuid as uuid,us.first_name "
			+ "as fName,us.last_name as lName,comp.name as companyName "+
			" from rcm_next_action_required_section rcsd  inner join "+
			" rcm_user us on  rcsd.created_by=us.uuid  and rcsd.final_submit is true "
			+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
			+ "inner join company comp on comp.uuid=cmp.company_id "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ "where rcsd.team_id=:teamId and rcsd.created_by=:userId and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "//cl.pending =false and
			+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
			+ " and cast(rcsd.created_date as date) "
			+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid "

			+" ) as w  group by companyName,uuid "
			)
	List<ProductionDto> claimProductionByForBillingAssoicate(@Param("companyIds") List<String> companyIds,
			@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate,
			@Param("userId") String userId);
	
	
	// Production means Total Claims Assigned by Internal Audit to other team and No
	// days between 2 dates
	// use refrence from Ruleengine RcmClaimDaoImpl->>
	// Constants.QUERY_FOR_RCMCALIM_AUDITED:
	@Query(nativeQuery = true, value = "select count(distinct claim_uuid) as total,FLOOR(count(distinct claim_uuid))/count(distinct cast(dats as date)) as days,"
			+ "count(distinct dats) as disDate,w.uuid as uuid,w.fName as fName,w.lName as lName,w.companyName as companyName from ( "
			+ "select cl.claim_uuid as claim_uuid,assign.created_date as dats,"
			+ "us.uuid as uuid,us.first_name  as fName,us.last_name as lName,comp.name as companyName "
			+ "from rcm_user us " + "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ "left join rcm_claim_assignment assign on us.uuid=assign.created_by "
			+ "left join rcm_claims cl on cl.claim_uuid=assign.claim_id "
			+ "inner join office off on off.uuid=cl.office_id "
			+ "inner join company comp on comp.uuid=off.company_id  "
			+ "where cmp.company_id in (:companyIds) and assign.created_by=us.uuid and cl.current_state="
			+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "
			+ "and assign.action_name = 'Reviewed' " //and  assign.active=false 
			+ "and assign.System_comment='Claim Transfered To Team( From 3 to 7)' and assign.current_team_id=7 and rut.team_id=:teamId "
			+ "and  CAST(assign.created_date as DATE) between STR_TO_DATE(:startDate, '%Y-%m-%d') "
			+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') " + "group by us.uuid,comp.name,cl.claim_uuid"
			+" union all "
			+ "select  cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
			+ "us.uuid as uuid,us.first_name "
			+ "as fName,us.last_name as lName,comp.name as companyName "+
			" from rcm_rebilling_section rcsd inner join "+
			" rcm_user us on  rcsd.created_by=us.uuid  and rcsd.is_rebilling is true "
			+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
			+ "inner join company comp on comp.uuid=cmp.company_id "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ "where  rcsd.team_id=:teamId and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "//cl.pending =false and
			+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
			+ " and cast(rcsd.created_date as date) "
			+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid "
			+" union all "
			+ "select  cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
			+ "us.uuid as uuid,us.first_name "
			+ "as fName,us.last_name as lName,comp.name as companyName "+
			" from rcm_next_action_required_section rcsd  inner join "+
			" rcm_user us on  rcsd.created_by=us.uuid  and rcsd.final_submit is true "
			+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
			+ "inner join company comp on comp.uuid=cmp.company_id "
			+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+ "where rcsd.team_id=:teamId and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "//cl.pending =false and
			+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
			+ " and cast(rcsd.created_date as date) "
			+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid "
			+" ) as w  group by companyName,uuid "
			
			)
	List<ProductionDto> claimProductionForInternalAudit(@Param("companyIds") List<String> companyIds,
			@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate);
	
	
	@Query(nativeQuery = true, value = 
            " select count(distinct cl.claim_uuid) as total,FLOOR(count(distinct cl.claim_uuid))/count(distinct cast(assign.updated_date as date)) as days ,"
			+" us.uuid as uuid,us.first_name "
			+" 	 as fName,us.last_name as lName,comp.name as companyName from rcm_user us "
			+"    inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+"     inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+" 	left join rcm_claim_assignment assign on us.uuid=assign.assigned_to and assign.current_team_id=:teamId and rut.team_id=:teamId "
			+" 	and  CAST(assign.updated_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d')"
			+"     and STR_TO_DATE(:endDate, '%Y-%m-%d') "
			+" 	left join rcm_claims cl on cl.claim_uuid=assign.claim_id and assign.created_by=us.uuid and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+"     and   taken_back is false  and cl.current_team_id<>:teamId "
			//+ " and cl.first_worked_team_id=:teamId "
			+" 	left join office off on off.uuid=cl.office_id  "
			+ " inner join company comp on comp.uuid=off.company_id  "
			+" 	where   cmp.company_id in (:companyIds) and rut.team_id=:teamId group by us.uuid,comp.name")
     List<ProductionDto> claimProductionForOtherTeam(@Param("companyIds") List<String> companyIds,
		@Param("teamId") int teamId,@Param("startDate") String stDate,@Param("endDate") String endDate);
	
		// use refrence from Ruleengine RcmClaimDaoImpl->>
		// Constants.QUERY_FOR_RCMCALIM_AUDITED:
	@Query(nativeQuery = true, value = "select count(distinct claim_uuid) as total,FLOOR(count(distinct claim_uuid))/count(distinct cast(dats as date)) as days,"
			   + "count(distinct dats) as disDate,w.uuid as uuid,w.fName as fName,w.lName as lName,w.companyName as companyName from ( "
			   + "select cl.claim_uuid as claim_uuid,assign.created_date as dats,"
			   + "us.uuid as uuid,us.first_name  as fName,us.last_name as lName,comp.name as companyName "
			   + "from rcm_user us " + "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
				+ "left join rcm_claim_assignment assign on us.uuid=assign.created_by "
				+ "left join rcm_claims cl on cl.claim_uuid=assign.claim_id "
				+ "inner join office off on off.uuid=cl.office_id "
				+ "inner join company comp on comp.uuid=off.company_id  "
				+ "left join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId and assig.user_id=:userId "
				+ "where cmp.company_id in (:companyIds) and assign.created_by=us.uuid and cl.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "
				+ " and assign.action_name = 'Reviewed' "
				+ "and assign.System_comment='Claim Transfered To Team( From 3 to 7)' and assign.current_team_id=7 and rut.team_id=:teamId "
				+ "and  CAST(assign.created_date as DATE) between STR_TO_DATE(:startDate, '%Y-%m-%d') "
				+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') " + "group by us.uuid,comp.name,cl.claim_uuid"
				+" union all "
				+ "select  cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
				+ "us.uuid as uuid,us.first_name "
				+ "as fName,us.last_name as lName,comp.name as companyName "+
				" from rcm_rebilling_section rcsd inner join "+
				" rcm_user us on  rcsd.created_by=us.uuid  and rcsd.is_rebilling is true "
				+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
				+ "inner join company comp on comp.uuid=cmp.company_id "
				+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
				+ "where rcsd.team_id=:teamId and  rcsd.created_by=:userId and  cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "//cl.pending =false and
				+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
				+ " and cast(rcsd.created_date as date) "
				+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid "
				+" union all "
				+ "select  cl.claim_uuid as claim_uuid,rcsd.created_date as dats ,"
				+ "us.uuid as uuid,us.first_name "
				+ "as fName,us.last_name as lName,comp.name as companyName "+
				" from rcm_next_action_required_section rcsd  inner join "+
				" rcm_user us on  rcsd.created_by=us.uuid  and rcsd.final_submit is true "
				+ "left join rcm_claims cl on rcsd.claim_uuid=cl.claim_uuid " + "inner join office off on off.uuid=cl.office_id "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  and cmp.company_id=off.company_id "
				+ "inner join company comp on comp.uuid=cmp.company_id "
				+ "inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
				+ "where rcsd.team_id=:teamId and rcsd.created_by=:userId and cl.current_state=" + Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " "//cl.pending =false and
				+ " and cmp.company_id in (:companyIds) and rut.team_id=:teamId "
				+ " and cast(rcsd.created_date as date) "
				+ "between STR_TO_DATE(:startDate, '%Y-%m-%d') and STR_TO_DATE(:endDate, '%Y-%m-%d') group by comp.name,us.uuid,cl.claim_uuid "
				+" ) as w  group by companyName,uuid "
				)
		List<ProductionDto> claimProductionForInternalAuditAssoicate(@Param("companyIds") List<String> companyIds,
				@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate,
				@Param("userId") String userId);

	@Query(nativeQuery = true, value = 
            " select count(distinct cl.claim_uuid) as total,FLOOR(count(distinct cl.claim_uuid))/count(distinct cast(assign.updated_date as date)) as days,"
			+" us.uuid as uuid,us.first_name "
			+" 	 as fName,us.last_name as lName,comp.name as companyName from rcm_user us "
			+"    inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+"     inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+" 	left join rcm_claim_assignment assign on us.uuid=assign.assigned_to and assign.current_team_id=:teamId and rut.team_id=:teamId "
			+" 	and  CAST(assign.updated_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d')"
			+"     and STR_TO_DATE(:endDate, '%Y-%m-%d') "
			+" 	left join rcm_claims cl on cl.claim_uuid=assign.claim_id and assign.created_by=us.uuid and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
			+"     and   taken_back is false  and cl.current_team_id<>:teamId "
			//+" and cl.first_worked_team_id=:teamId"
			+" 	left join office off on off.uuid=cl.office_id  "
			+ " inner join company comp on comp.uuid=off.company_id  "
			+ " left join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId and assig.user_id=:userId "
			+" 	where   cmp.company_id in (:companyIds) and rut.team_id=:teamId group by us.uuid,comp.name")
     List<ProductionDto> claimProductionForOtherTeamAssoicate(@Param("companyIds") List<String> companyIds,
		@Param("teamId") int teamId,@Param("startDate") String stDate,@Param("endDate") String endDate,@Param("userId") String userId);

	
	@Query(nativeQuery = true, value = " select claim_uuid uuid,cmp.uuid companyId,cl.claim_id claimId,dos,patient_birth_date  patientDob,"+
			" patient_id patientId,patient_name patientName,pending ,cl.current_state currentState, auto_rule_run autoRuleRun,"+
			" date_last_updated_es dateLastUpdatedES, status_es statusES,est_secondary_es estSecondaryES,description_es descriptionES, "+
			" prim_date_sent primDateSent, prim_status primeStatus,prim_total_paid primeTotalPaid,"+
			" rcm_source source, sec_date_sent secDateSend,sec_status secStatus,"+
			" sec_submitted_total secSubmittedTotal,submitted_total submittedTotal, timely_fil_lmt_dt timeFilLimitDay,"+
			" off.name officeName,off.uuid officeUuid,off.active officeActive,ct.id claimStatus,lTeam.name lastTeam,Cteam.name currentTeam,Cteam.id currentTeamId,"+
			" pins.name primInsurance,sins.name secInsurance, cl.group_number groupNumber,cl.prime_policy_holder primePolicyHolder,"+
			" prime_sec_submitted_total primeSecSubmittedTotal,sec_policy_holder_dob secPolicyHolderDob,"+
			" cl.created_date createdDate,assign.assigned_to assignedTo,us.email,us.first_name firstName,us.last_name lastName,"+
			" pinst.name primaryInsType,sinst.name secondaryInsType,pinst.code primaryInsCode,sinst.code secondaryInsCode," +
			"cmp.name clientName,cl.regenerated regenerated, " +
			" cl.sec_member_id secMemberId,cl.sec_policy_holder secPolicyHolder, "+ 
			" cl.provider_id providerId,cl.created_date pulledDate, "+
			" cl.treating_provider treatingProvider , provider_on_claim providerOnClaim," +
			" cl.provider_on_claim_from_sheet providerOnClaimFromSheet,cl.treating_provider_from_claim treatingProviderFromClaim, " +
			" cl.prime_policy_holder_dob primePolicyHolderDob, cl.ivf_id ivId,iv_dos ivDos,tp_id tpId,tp_dos tpDos,primary_eob primaryEob, "+
			" cl.claim_type claimType,Fteam.name firstTeam,Fteam.id firstTeamId, "+
			" pins.insurance_code primaryInsCodeSheet,sins.insurance_code secondaryInsCodeSheet,cl.ssn ssn,cl.assignment_of_benefits assignmentOfBenefits, "+
			" cl.treating_provider_from_claim_on_sheet treatingProviderFromSheet,preferred_mode_of_submission preferredModeOfSubmission,rule_engine_run_remark ruleEngineRunRemark, " +
			" cl.insurance_contact_no insuranceContactNo, cl.patient_contact_no patientContactNo, cl.current_status currentStatus,cl.next_action nextAction,cl.rebilled_status rebilledStatus, "
			+ "COALESCE(cl.btp, 0) as btp,COALESCE(cl.adjustment, 0) as adjustment,COALESCE(cl.payment_received, 0) as paymentReceived,COALESCE(cl.paid_amount, 0) as paidAmount, "
			+ "COALESCE(cl.balance_from_es_after_posting, 0) as balanceFromEsAfterPosting,COALESCE(cl.balance_from_es_before_posting, 0) as balanceFromEsBeforePosting ,cl.first_posting_date as firstPostingDate, "
			+ "cl.first_rebilled_date as firstRebilledDate, cl.reconciliation_pass as reconciliationPass, COALESCE(cl.amount_collected_claims,0) as amountCollectedClaims,cl.status_es_updated as statusESUpdated,cl.next_follow_up_date as nextFollowUpDate,"
			+ "COALESCE(cl.last_work_team_id,-1) as lastTeamWorkId from  rcm_claims cl inner join office off on  off.uuid=cl.office_id "+
			"  inner join company cmp on cmp.uuid=off.company_id"+
			"  inner join rcm_claim_status_type ct on ct.id=cl.claim_status_type_id"+
			"  left join rcm_team Cteam  on Cteam.id=cl.current_team_id"+
			"  left join rcm_team lTeam  on lTeam.id=cl.last_work_team_id"+
			"  left join rcm_team Fteam  on Fteam.id=cl.first_worked_team_id"+
			"  left join rcm_insurance pins on pins.id = cl.prim_insurance_company_id"+
			"  left join rcm_insurance sins on sins.id = cl.sec_insurance_company_id"+
			"  left join rcm_insurance_type pinst on pins.insurance_type_id = pinst.id"+
			"  left join rcm_insurance_type sinst on sins.insurance_type_id = sinst.id"+
			"  left join rcm_claim_assignment assign on  assign.claim_id=cl.claim_uuid and assign.active=1 and assign.assigned_to is not null "+
			"  left join rcm_user us on us.uuid=assign.assigned_to"+
			"  where claim_uuid=:claimUuid ")//and cmp.uuid=:companyId
	RcmClaimDetailDto fetchIndividualClaim(@Param("claimUuid")  String claimUuid) ;
	
	@Query(nativeQuery = true, value = " select pinst.code primaryInsCode,sinst.code secondaryInsCode " +
			"  from  rcm_claims cl "+
			"  left join rcm_insurance pins on pins.id = cl.prim_insurance_company_id"+
			"  left join rcm_insurance sins on sins.id = cl.sec_insurance_company_id"+
			"  left join rcm_insurance_type pinst on pins.insurance_type_id = pinst.id"+
			"  left join rcm_insurance_type sinst on sins.insurance_type_id = sinst.id"+
			"  where claim_uuid=:claimUuid ")
	Object fetchInsuranceCodeOfClaim(@Param("claimUuid")  String claimUuid) ;

	//9 May 2023 and IV Date is 5th May 2023 -
	@Query(nativeQuery = true, value = "select pd.id ivId,p.office_id officeId,general_date_iv_wasdone dos,policy_holder_dob pdob,policy_holder pdName, "
			+ " pd.member_ssn ssn,pd.ins_contact as insuranceContact " + 
			" from  patient p , patient_detail pd where pd.patient_id=p.id and p.patient_id=:patientId " + 
			" and p.office_id=:officeId and pd.cob_status in (:insTypes) " + 
			" and STR_TO_DATE(:dos,'%Y-%m-%d')>=STR_TO_DATE(general_date_iv_wasdone,'%Y-%m-%d') order by " + 
			" STR_TO_DATE(general_date_iv_wasdone,'%Y-%m-%d') desc limit 1 ")
	IVFDto getIVIdOfClaimByDos(@Param("dos") String dos,@Param("officeId") String officeId,
			@Param("patientId") String patientId,@Param("insTypes") Set<String> insTypes);
	
	/*@Query(nativeQuery = true, value = "select ivf_form_id,date_of_service from reports_claim r inner join " + 
			"report_claim_detail rd on rd.report_id=r.id where "
			+ " office_id=:officeId and claim_id=:claimid and  patient_id=:patientId and insurance_type in (:insTypes) limit 1 ")
	Object getIVIdOfClaim(@Param("claimid") String claimid,@Param("officeId") String officeId,
			@Param("patientId") String patientId,@Param("insTypes") Set<String> insTypes);*/
	
	/*
	@Query(nativeQuery = true, value = "select treatement_plan_id from reports where "
			+ " office_id=:officeId and ivf_form_id=:ivId and  patient_id=:patientId")
    String getTreatmentPlanIdIV(@Param("ivId") String ivId,@Param("officeId") String officeId,
			@Param("patientId") String patientId);
	*/		
	@Query(nativeQuery = true, value = ""
			+" SELECT treatement_plan_id,tx_plan_date FROM reports r inner join report_detail rd on rd.report_id=r.id "
			+" where office_id=:officeId and patient_id=:patientId "
			+" and treatement_plan_id not in ('PREBATCHMODE','SEALANTMODE') and iv_date is not null and ivf_form_id=:ivid "
			//+" and STR_TO_DATE(iv_date, '%m/%d/%Y')<=STR_TO_DATE(:dos,'%m/%d/%Y') "
			+" order by STR_TO_DATE(iv_date, '%m/%d/%Y') desc limit 1"
			)
	Object getLatestTPIdForPatientByIVId(@Param("officeId") String officeId,
			@Param("patientId") String patientId,@Param("ivid") String ivid);
	
	@Query(nativeQuery = true, value = ""
			+" SELECT treatement_plan_id,tx_plan_date FROM reports r inner join report_detail rd on rd.report_id=r.id "
			+" where office_id=:officeId and patient_id=:patientId "
			+" and treatement_plan_id =:tpId "
			//+" and STR_TO_DATE(iv_date, '%m/%d/%Y')<=STR_TO_DATE(:dos,'%m/%d/%Y') "
			+" order by STR_TO_DATE(iv_date, '%m/%d/%Y') desc limit 1"
			)
	Object getTPIdByTpid(@Param("officeId") String officeId,
			@Param("patientId") String patientId,@Param("tpId") String ivid);
	
	@Query(nativeQuery = true, value ="select cl.id as Id,claim_id claimId,issue,source,off.name officeName,cl.created_date createdDate,cl.is_archive as IsArchive from rcm_issue_claims cl "+
			" left join office off on off.uuid=cl.office_id "+
			" where off.company_id=:cmpid and cl.resolved is false "
			+ "and cl.is_archive is false order by cl.created_date desc")
	List<IssueClaimDto> getIssueClaims(@Param("cmpid") String ivId);
	
	
	String IVF_DATA_QUERY =""+
			" select r.claim_id claimId,rd.rule_id ruleId,rs.name ruleName,r.patient_name patientName,patient_id patientId,r.ivf_form_id ivId,rd.iv_date ivDate,rd.date_of_service"+
			" dos,rd.error_message message,rd.message_type mType,"+
			" rd.surface surface,rd.tooth tooth,rd.codes codes,off.name officeName,rd.insurance_type insuranceType,rd.created_date runDate from reports_claim r "+
			" inner join ("+
			" SELECT rd.group_run,rd.report_id,iv_date FROM reports_claim r inner join report_claim_detail rd"+
			" on rd.report_id=r.id "+
			" where r.claim_id=:claim_id and r.patient_id=:patientid and "+
			"  r.office_id=:office_id order by STR_TO_DATE( iv_date, '%m/%d/%Y'),rd.group_run desc limit 1) r1 "+
			"  on r.id=r1.report_id and r.group_run=r1.group_run "+
			" inner join report_claim_detail rd "+
			" on r.id=rd.report_id and rd.group_run=r1.group_run inner join office off on off.uuid=r.office_id "
			+ " inner join rules rs on rs.id=rd.rule_id "
			+ ""+
			"  where r.claim_id=:claim_id and r.office_id=:office_id and r.patient_id=:patientid and off.company_id=:cmp_id ";
	@Query(nativeQuery = true, value = IVF_DATA_QUERY)
	List<RuleEngineClaimDto> getRuleEngineClaimReport(@Param("office_id") String officeId,@Param("cmp_id") String companyId,
			@Param("patientid") String patientId,@Param("claim_id") String claimId);
	
	@Query(nativeQuery = true, value = IVF_DATA_QUERY +" limit 1 ")
	RuleEngineClaimDto getRuleEngineClaimReportOnlyIV(@Param("office_id") String officeId,@Param("cmp_id") String companyId,
			@Param("patientid") String patientId,@Param("claim_id") String claimId);
	
	
	
	/*@Query(nativeQuery = true, value = ""+
			" SELECT ivf_form_id ivId,r.office_id officeId FROM reports_claim r inner join report_claim_detail rd "+
			" on rd.report_id=r.id inner join patient_detail pd on pd.id=r.ivf_form_id  and  pd.office_id=r.office_id "+
			" where r.claim_id=:claim_id and r.patient_id=:patientid and pd.cob_status in (:primarysecnnoifo) and "+
			"  r.office_id=:office_id order by STR_TO_DATE( iv_date, '%m/%d/%Y'),rd.group_run desc limit 1"	)
	IVFDto getLatestIvfNumberForClaim(@Param("office_id") String officeId,
			@Param("patientid") String patientId,@Param("claim_id") String claimId,
			@Param("primarysecnnoifo") List<String> primarysecnnoifo);//Primary Secondary no information*/
	
	
	@Query(nativeQuery = true, value = "select cl.claim_uuid,cl.next_action,sins.name secInsurance,cl.current_status currentStatus from rcm_claims cl "
			+ " left join rcm_insurance sins on sins.id = cl.sec_insurance_company_id "
			+ " where "
			+ " cl.office_id=:officeId and cl.claim_id=:claimid ")
	Object getClaimsUuidClaimIdSec(@Param("claimid") String claimid,@Param("officeId") String officeId);
	
	@Query(nativeQuery = true, value = "select cl.claim_uuid,cl.next_action,sins.name primInsurance,cl.current_status currentStatus from rcm_claims cl "
			+ " left join rcm_insurance sins on sins.id = cl.prim_insurance_company_id "
			+ " where "
			+ " cl.office_id=:officeId and cl.claim_id=:claimid ")
	Object getClaimsUuidClaimIdPrim(@Param("claimid") String claimid,@Param("officeId") String officeId);
	
	
	@Query(nativeQuery = true, value = ""
			+ " select cl.claim_uuid from rcm_claims cl inner join office off on off.uuid=cl.office_id and "
			+ " off.company_id=:companyId  where cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_uuid "
			+ " not in (select cl.claim_uuid ascl from rcm_claims cl inner join rcm_claim_assignment ass on "
			+ " ass.claim_id=cl.claim_uuid inner join office off on off.uuid=cl.office_id  where  "
			+ " ass.active=1 and ass.assigned_to is not null and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and off.company_id=:companyId )")
	List<String> getUnAsignedClaims(@Param("companyId") String companyId);
	
	@Query(nativeQuery = true, value = ""
			+" select cl.claim_uuid,cl.office_id from rcm_claims cl inner join office off on off.uuid=cl.office_id "
			+"  where cl.current_state=0 and cl.current_team_id=:teamId and off.company_id=:companyId "
			+"  and cl.current_status<>:currentStatusClosed")
			List<Object> getValidClaimWithCompanyTeams(@Param("companyId") String companyId,@Param("teamId") int teamId ,@Param("currentStatusClosed")int currentStatusClosed);
	
	/*@Query(nativeQuery = true, value = ""
			+ " select cl.claim_uuid from rcm_claims cl inner join office off on off.uuid=cl.office_id and "
			+ " off.company_id=:companyId and  off.office_id=:officeId where pending is true and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_uuid "
			+ " not in (select cl.claim_uuid ascl from rcm_claims cl inner join rcm_claim_assignment ass on "
			+ " ass.claim_id=cl.claim_uuid inner join office off on off.uuid=cl.office_id  where  "
			+ " cl.pending is true  and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and off.company_id=:companyId and  off.office_id=:officeId )")
	List<String> getUnAsignedClaimByOffice(@Param("companyId") String companyId,@Param("officeId") String officeId);*/

	
	@Query(nativeQuery = true, value = ""
			+ "select count(concat(rt.name,off.name)) as count,rt.name as teamName,off.name as officeName,rt.id as teamId from rcm_claims rc " + 
			" inner join office off on off.uuid=rc.office_id inner join rcm_team rt on rt.id=rc.current_team_id " + 
			" inner join company cmp on cmp.uuid=off.company_id" + 
			" where rc.current_status<>:currentStatusClosed  and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cmp.uuid=:companyId " + 
			" group by rc.office_id,rt.name ")
	List<AllPendencyDto> allPendencyCount(@Param("companyId") String companyId, @Param("currentStatusClosed")int currentStatusClosed);
	
	@Query(nativeQuery = true, value = ""
			+ "select count(concat(rt.name,off.name)) as count,rt.name as teamName,off.name as officeName,rt.id as teamId from rcm_claims rc " + 
			" inner join office off on off.uuid=rc.office_id inner join rcm_team rt on rt.id=rc.current_team_id " + 
			" inner join company cmp on cmp.uuid=off.company_id" + 
			" inner join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId and assig.user_id=:userId "+
			" where rc.current_status<>:currentStatusClosed and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cmp.uuid=:companyId " + 
			" group by rc.office_id,rt.name ")
	List<AllPendencyDto> allPendencyCountForUser(@Param("companyId") String companyId,@Param("teamId") int teamId,@Param("userId") String userId, @Param("currentStatusClosed")int currentStatusClosed);
	
	@Query(nativeQuery = true, value = ""
			+ "select min(rc.dos) minDate,min(cast(rc.created_date as Date)) dt,rt.name as teamName,off.name as officeName,rt.id as teamId from rcm_claims rc " + 
			" inner join office off on off.uuid=rc.office_id inner join rcm_team rt on rt.id=rc.current_team_id " + 
			" inner join company cmp on cmp.uuid=off.company_id" + 
			" where rc.current_status<>:currentStatusClosed  and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cmp.uuid=:companyId " + 
			" group by rc.office_id,rt.name ")
	List<AllPendencyDateDto> allPendencyDateCount(@Param("companyId") String companyId, @Param("currentStatusClosed")int currentStatusClosed);
	
	@Query(nativeQuery = true, value = ""
			+ "select min(rc.dos) minDate,min(cast(rc.created_date as Date)) dt,rt.name as teamName,off.name as officeName,rt.id as teamId from rcm_claims rc " + 
			" inner join office off on off.uuid=rc.office_id inner join rcm_team rt on rt.id=rc.current_team_id " + 
			" inner join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId and assig.user_id=:userId "+
			" inner join company cmp on cmp.uuid=off.company_id" + 
			" where rc.current_status<>:currentStatusClosed and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cmp.uuid=:companyId " + 
			" group by rc.office_id,rt.name ")
	List<AllPendencyDateDto> allPendencyDateCountForUser(@Param("companyId") String companyId,@Param("teamId") int teamId, @Param("userId") String userId ,@Param("currentStatusClosed")int currentStatusClosed);
	
	@Query(nativeQuery = true, value = ""
			+ " select distinct rc.claim_id as claimId,rc.office_id as officeId,rc.claim_uuid as claimUUid  from rcm_claims rc inner join office off on off.uuid=rc.office_id "
			+ " inner join company cmp on cmp.uuid=off.company_id left join rcm_claim_detail rcd on rcd.claim_id=rc.claim_uuid "
			+ " where  cmp.name=:companyName and date(rc.created_date)  >= (CURDATE() - interval :days day) and rc.pending is true "
			+ " and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and rcd.id is null "
			+ "")
	List<ClaimXDaysDto> getClaimIdsdWithNoDetailForGivenLastDays(@Param("companyName") String companyName,
			@Param("days") int days);
	
	@Query(nativeQuery = true, value = ""
			+ " select distinct rc.claim_id as claimId,rc.office_id as officeId,rc.claim_uuid as claimUUid  from rcm_claims rc inner join office off on off.uuid=rc.office_id "
			+ " inner join company cmp on cmp.uuid=off.company_id left join rcm_claim_detail rcd on rcd.claim_id=rc.claim_uuid "
			+ " where  cmp.name=:companyName and off.uuid=:offuuid and date(rc.created_date)  >= (CURDATE() - interval :days day) and rc.pending is true "
			+ " and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and rcd.id is null "
			+ "")
	List<ClaimXDaysDto> getClaimIdsdWithNoDetailForGivenLastDayForOffice(@Param("companyName") String companyName,
			@Param("days") int days,@Param("offuuid") String offuuid);
	
	@Query(nativeQuery = true, value = ""
			+" select apt_date appointmentDate, plan_assignment_of_benefits  as planAssignmentofBenefits ,memberId as basicInfo16,group_p as basicInfo14," 
			+" concat(coalesce(p.first_name,''),' ',coalesce(p.last_name,'')) as basicInfo2, p.dob as basicInfo6," 
		    +" policy_holder as basicInfo5 from patient p inner join patient_detail pd  on p.id=pd.patient_id "
			+ " where pd.id=:ivId ")
	DataPatientRuleDto getDataForRuleCheckFromIV(@Param("ivId") String ivId);


	@Query(value = "select cl.claim_id claimId,cl.issue,cl.source,off.name officeName,cl.created_date createdDate from rcm_issue_claims cl "
			+ "left join office off on off.uuid=cl.office_id "
			+ "where off.company_id=:companyId and cl.resolved is false order by cl.id limit :offset, :limit", nativeQuery = true)
	List<IssueClaimDto> getIssueClaimsByPagination(@Param("companyId") String companyId,@Param("offset")int offSet,@Param("limit")int limit); //and off.activeis true
	
	@Query(nativeQuery = true, value = "select pd.id ivId,p.office_id officeId,general_date_iv_wasdone dos,member_ssn ssn,pd.ins_contact as insuranceContact " + 
			" from  patient p , patient_detail pd where pd.patient_id=p.id and p.patient_id=:patientId " + 
			" and p.office_id=:officeId and pd.id=:ivid " + 
			" order by " + 
			" STR_TO_DATE(general_date_iv_wasdone,'%Y-%m-%d') desc limit 1 ")
	IVFDto getIVDosByIvId(@Param("ivid") String ivid,@Param("officeId") String officeId,
			@Param("patientId") String patientId);
	
	
	@Query(nativeQuery = true, value = " select  sins.name secInsurance "+
			"  from  rcm_claims cl inner join office off on  off.uuid=cl.office_id "+
			"  inner join company cmp on cmp.uuid=off.company_id"+
			"  left join rcm_insurance sins on sins.id = cl.sec_insurance_company_id"+
			"  where claim_uuid=:claimUuid and cmp.uuid=:companyId")
	String fetchSecInsuranceOfClaim(@Param("companyId")  String companyId,@Param("claimUuid")  String claimUuid) ;
	
	@Query(nativeQuery = true, value = ""+
		"	select cl.claim_uuid claimUuid,rca.assigned_to claimAssignedTo,rca.id  claimAssignmentId,cl.office_id officeId from rcm_claims cl inner join office off on off.uuid=cl.office_id "+
		"	inner join company com on com.uuid =off.company_id "+
		"	inner join rcm_claim_assignment rca on rca.claim_id=cl.claim_uuid and rca.active is true  and rca.current_team_id =:teamId "+
		"	where  com.uuid=:companyId and cl.current_status<>:currentStatusClosed and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+"")
	List<PendingClaimToReAssignDto> fetchAllPendingClaimsAssignedToSomeOneByCompanyIdAndTeamId(@Param("companyId") String companyId,@Param("teamId")  int teamId, @Param("currentStatusClosed")int currentStatusClosed) ;

	@Query(value = "select cl.id as Id,cl.is_archive as IsArchive,cl.claim_id claimId,cl.issue,cl.source,off.name officeName,cl.created_date createdDate from rcm_issue_claims cl "
			+ "left join office off on off.uuid=cl.office_id "
			+ "where off.company_id=:companyId and cl.resolved is false and cl.is_archive is true order by cl.id desc limit :offset, :limit", nativeQuery = true)
	List<IssueClaimDto> archiveClaimsByPagination(@Param("companyId") String companyId,@Param("offset")int offSet,@Param("limit")int limit); //and off.active is true
  
	@Modifying
	@Query(nativeQuery = true, value = "update rcm_issue_claims set is_archive =:archiveStatus,updated_by=:updatedBy,updated_date=CURRENT_TIMESTAMP where id in (:id) AND resolved is false")
	int updateIssueClaimsArchiveStatus(@Param("id")List<Integer>id,@Param("archiveStatus")boolean archiveStatus,@Param("updatedBy")RcmUser updatedBy);

	@Query(nativeQuery = true, value = " select  claim_id  "+
			"  from  rcm_claims cl inner join office off on  off.uuid=cl.office_id "+
			"  inner join company cmp on cmp.uuid=off.company_id"+
			"  where cmp.uuid=:companyId and claim_id=:claimId and off.uuid=:officeId and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+"")
	String fetchClaimIdByClaimIdAnCompany(@Param("claimId")  String claimId,@Param("companyId")  String companyId,@Param("officeId") String officeId) ;
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "update rcm_issue_claims set is_archive=false,claim_id=:claimId,updated_by=:updatedBy,updated_date=CURRENT_TIMESTAMP where id =:id AND resolved is false")
	int updateIssueClaimsUnArchiveStatus(@Param("id")int id,@Param("updatedBy")RcmUser updatedBy,@Param("claimId")String claimId);
	
	@Modifying
	@Transactional
	@Query(value = "update rcm_claims set current_status=:status,next_action=:nextaction where claim_uuid=:claimUuid ", nativeQuery = true)
	void updateClaimCurrentStatusWithAction(@Param("status")int status,@Param("nextaction")int nextaction,@Param("claimUuid")String claimUuid);


    /*@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance,prime_sec_submitted_total primeSecSubmittedTotal "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,DATEDIFF(sysdate(),claims.dos) as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,rca.created_date pendingSince " + " from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid and rca.active=1 "
			+ "  where claims.current_team_id=:teamid and off.company_id=:companyId " + " and pending=false "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
					+ " order by ust-claimAge,primeSecSubmittedTotal asc  ")
	List<FreshClaimDataDto> fetchSubmittedClaimForOtherTeam(@Param("companyId") String companyId,
			@Param("teamid") int teamid);*/

	/*@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,claims.attachment_count as attachmentCount, "
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance,prime_sec_submitted_total primeSecSubmittedTotal "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,DATEDIFF(sysdate(),claims.dos) as claimAge, "
			+ " CAST(COALESCE(timely_fil_lmt_dt,0) as signed) as ust,timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,rca.created_date pendingSince " + " from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid and rca.active=1 "
			+ "  where claims.current_team_id=:teamid and off.company_id=:companyId " + " and pending=true "
			+ " and claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
					+ " order by ust-claimAge,primeSecSubmittedTotal asc  ")
	List<FreshClaimDataDto> fetchUnSubmittedClaimForOtherTeam(@Param("companyId") String companyId,
			@Param("teamid") int teamid);*/
	
	
	/*@Query(nativeQuery = true, value = ""
			+ " select cl.claim_uuid as claimUuid,cl.claim_id as claimId,cl.dos as dos,cl.patient_id as patientId, "
			+ "provider_on_claim as providerOnClaim,treating_provider as treatingProvider,cl.current_state as currentState "
			+ "from rcm_claims cl  where cl.claim_uuid=:claimUuid")
	RcmClaimDataDto getClaimsDataByClaimUuid(@Param("claimUuid") String claimUuid);
	/
	@Query(nativeQuery = true, value = ""
			+ " select cl.claim_uuid as claimUuid,cl.claim_id as claimId,cl.dos as dos,cl.patient_id as patientId, "
			+ "provider_on_claim as providerOnClaim,treating_provider as treatingProvider,cl.current_state as currentState "
			+ "from rcm_claims cl  WHERE cl.claim_id LIKE %:claimId% ")
	List<RcmClaimDataDto> getClaimsDataByClaimId(@Param("claimId") String claimId);
	*/
	
	//For Aging
	@Query(nativeQuery = true, value = " SELECT claims.current_status "
			+ "as currentClaimStatus,claims.claim_uuid as claimid,off.uuid as officeUuid,off.name as officeName, "
			+ "us.first_name as fName,us.last_name as lName,"
			+ "case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge FROM rcm_user us "
			//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+ "inner join  office off on off.company_id=cmp.company_id "
			+ "left join rcm_claims claims on claims.office_id=off.uuid "
			+ nextActionAndRebillProductionjoin
			//+ "left join rcm_insurance_follow_up_section ifd on ifd.claim_uuid=claims.claim_uuid "
			+ "where cmp.company_id in (:companyIds) and "// uoff.team_id =:teamId and "
			//+ "ifd.team_id=:teamId and ifd.final_submit=true and "
			//+ "CAST(ifd.created_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d') "
			//+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and
			+  nextActionAndRebillProductionwhere +" and "
			+"  claims.current_state="
			+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED 
			+ " group by off.uuid,claims.claim_uuid")
	List<ProductionForAging> claimProductionForAging(@Param("companyIds") List<String> companyIds,@Param("teamId") int teamId, @Param("startDate") String stDate,
			@Param("endDate") String endDate);

	@Query(nativeQuery = true, value = " SELECT claims.current_status "
			+ "as currentClaimStatus,claims.claim_uuid as claimid,off.uuid as officeUuid,off.name as officeName, "
			+ "us.first_name as fName,us.last_name as lName,"
			+ "case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge FROM rcm_user us "
			//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+ "inner join  office off on off.company_id=cmp.company_id "
			+ "left join rcm_claims claims on claims.office_id=off.uuid "
			+ nextActionAndRebillProductionjoin
			//+ "left join rcm_insurance_follow_up_section ifd on ifd.claim_uuid=claims.claim_uuid "
			+ "where cmp.company_id in (:companyIds) and "// uoff.team_id =:teamId and "
			//+ "ifd.team_id=:teamId and ifd.final_submit=true and "
			//+ "CAST(ifd.created_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d') "
			//+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and
			+  nextActionAndRebillProductionwherewithuser +" and "
			+"  claims.current_state="
			+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED +" and us.uuid=:userId "//+ " and uoff.user_id=:userId "
			+ " group by off.uuid,claims.claim_uuid")
	List<ProductionForAging> claimProductionForAgingAssoicate(@Param("companyIds") List<String> companyIds,@Param("teamId") int teamId, @Param("startDate") String stDate,
			@Param("endDate") String endDate, @Param("userId") String userId);
	
	@Query(nativeQuery = true, value = "SELECT count(distinct claims.claim_uuid) as total,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days,count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date) ) as disDate,"
			+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName "
			+ "FROM rcm_user us "
			//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+ "inner join  office off on off.company_id=cmp.company_id "
			+ "inner join company comp on comp.uuid=off.company_id  "
			+ "left join rcm_claims claims on claims.office_id=off.uuid "
			+ nextActionAndRebillProductionjoin
			+ "where cmp.company_id in (:companyIds)  and  "// uoff.team_id = :teamId  "
			//+ "and claims.current_team_id<>:teamId and "
			//+ "CAST(assign.updated_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d') "
			//+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and "
			+  nextActionAndRebillProductionwhere +" and "
			+ "claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED +"  group by us.uuid,comp.name")
	 List<ProductionDto> claimProductionForAssignToOtherTeams(@Param("companyIds") List<String> companyIds,@Param("teamId") int teamId, @Param("startDate") String stDate,
			@Param("endDate") String endDate);

	@Query(nativeQuery = true, value = "SELECT count(distinct claims.claim_uuid) as total,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days,count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date) ) as disDate,"
			+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName "
			+ "FROM rcm_user us "
			+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+ "inner join  office off on off.company_id=cmp.company_id "
			+ "inner join company comp on comp.uuid=off.company_id  "
			+ "left join rcm_claims claims on claims.office_id=off.uuid "
			+ nextActionAndRebillProductionjoin
			+ "where cmp.company_id in (:companyIds)  and us.uuid=:userId and "// uoff.team_id = :teamId  "
			//+ "and claims.current_team_id<>:teamId and "
			//+ "CAST(assign.updated_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d') "
			//+ "and STR_TO_DATE(:endDate, '%Y-%m-%d') and "
			+  nextActionAndRebillProductionwherewithuser +" and "
			+ "claims.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED +"  group by us.uuid,comp.name")
	 List<ProductionDto> claimProductionForAssignToOtherTeamsAssoicate(@Param("companyIds") List<String> companyIds,@Param("teamId") int teamId, @Param("startDate") String stDate,
			@Param("endDate") String endDate, @Param("userId") String userId);
	
	
	//Patient Calling
		@Query(nativeQuery = true, value = "select a.id,b.desposition,a.claimid,a. officeUuid,a.officeName from ("
				+ " SELECT   max(pc.id) as id,pc.desposition "
				+ "as desposition,claims.claim_uuid as claimid,off.uuid as officeUuid,off.name as officeName FROM rcm_user us "
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join  office off on off.company_id=cmp.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_patient_communication_section pc on pc.claim_uuid=claims.claim_uuid and pc.team_id=:teamId and pc.final_submit=true  "// and pc.mode_of_follow ='Call' "
				+ nextActionAndRebillProductionjoin
				+ " where cmp.company_id in (:companyIds) and "// uoff.team_id =:teamId  and "
				+  nextActionAndRebillProductionwhere +" and "
				+ "claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by off.uuid,claims.claim_uuid"
				+ " )a inner join rcm_patient_communication_section b where a.id=b.id  ")
		List<ProductionForPatientCalling> claimProductionForPatientCalling(@Param("companyIds") List<String> companyIds,@Param("teamId") int teamId,
				@Param("startDate") String stDate, @Param("endDate") String endDate);

		@Query(nativeQuery = true, value = "select a.id,b.desposition,a.claimid,a. officeUuid,a.officeName from ("
				+ " SELECT  max(pc.id) as id,pc.desposition as desposition "
				+ ",claims.claim_uuid as claimid,off.uuid as officeUuid,off.name as officeName " + "FROM rcm_user us "
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join  office off on off.company_id=cmp.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_patient_communication_section pc on pc.claim_uuid=claims.claim_uuid and pc.team_id=:teamId and pc.created_by=:userId and pc.final_submit=true "//and pc.mode_of_follow ='Call'"
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds) and "//uoff.team_id =:teamId  and "
				+  nextActionAndRebillProductionwherewithuser +" and "
				+ " claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " and us.uuid=:userId "//" and uoff.user_id=:userId "
				+ "group by off.uuid,claims.claim_uuid "
				+ " )a inner join rcm_patient_communication_section b where a.id=b.id ")
		List<ProductionForPatientCalling> claimProductionForPatientCallingAssoicate(@Param("companyIds") List<String> companyIds,@Param("teamId") int teamId,
				@Param("startDate") String stDate, @Param("endDate") String endDate, @Param("userId") String userId);
		
		//Patient Statement
		@Query(nativeQuery = true, value = "select a.id,COALESCE(b.statement_type,0) as statementType,a.total, "
		        +" a.uuid as uuid,a.fName as fName,a.lName as lName,a.companyName as companyName,a.days"
				+" from ("
		        +" SELECT  max(ps.id) as id,count(distinct claims.claim_uuid) as total,ps.statement_type as statementType,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days "
				+ "FROM rcm_user us " 
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join office off on  off.company_id=cmp.company_id "
				+ "inner join company comp on comp.uuid=off.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_patient_statement_section ps on ps.claim_uuid=claims.claim_uuid and ps.team_id=:teamId and ps.final_submit=true "
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds) and "// uoff.team_id =:teamId   and "
				+  nextActionAndRebillProductionwhere +" and "
				+ "claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by us.uuid,comp.name"
				+ "  )a inner join rcm_patient_statement_section b where a.id=b.id")
		List<ProductionForPatientStatement> claimProductionForPatientStatement(@Param("companyIds") List<String> companyIds,@Param("teamId") int teamId,
				@Param("startDate") String stDate, @Param("endDate") String endDate);

		@Query(nativeQuery = true, value = "select a.id,COALESCE(b.statement_type,0) as statementType,a.total, "
		        +" a.uuid as uuid,a.fName as fName,a.lName as lName,a.companyName as companyName,a.days"
				+" from ("
		        +" SELECT  max(ps.id) as id,count(distinct claims.claim_uuid) as total,ps.statement_type as statementType,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days "
				+ "FROM rcm_user us " 
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join office off on  off.company_id=cmp.company_id "
				+ "inner join company comp on comp.uuid=off.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_patient_statement_section ps on ps.claim_uuid=claims.claim_uuid and ps.created_by=:userId and  ps.team_id=:teamId and ps.final_submit=true "
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds)  and us.uuid=:userId and "// uoff.team_id =:teamId   and "
				+  nextActionAndRebillProductionwherewithuser +" and "
				+ "claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by us.uuid,comp.name"
				+ "  )a inner join rcm_patient_statement_section b where a.id=b.id")
		List<ProductionForPatientStatement> claimProductionForPatientStatementAssoicate(
				@Param("companyIds") List<String> companyIds, @Param("teamId") int teamId,
				@Param("startDate") String stDate, @Param("endDate") String endDate, @Param("userId") String userId);

		//CDP BY InsFollow Up
		@Query(nativeQuery = true, value = "select a.total, "
		        +" a.uuid as uuid,a.fName as fName,a.lName as lName,a.companyName as companyName,a.days,a.dats as disDate"
				+" from ("
		        +" SELECT  max(insFollow.id) as id,count(distinct claims.claim_uuid) as total,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days,count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date) ) as dats "
				+ "FROM rcm_user us " 
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join office off on  off.company_id=cmp.company_id "
				+ "inner join company comp on comp.uuid=off.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_insurance_follow_up_section insFollow on insFollow.claim_uuid=claims.claim_uuid and insFollow.team_id=:teamId and insFollow.final_submit=true "
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds) and "// uoff.team_id =:teamId   and "
				+  nextActionAndRebillProductionwhere +" and "
				+ " claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by us.uuid,comp.name"
				+"  )a inner join rcm_insurance_follow_up_section b where a.id=b.id ")
		List<ProductionDto> claimProductionForCDPByInsuranceFollowUp(@Param("companyIds") List<String> companyIds,
				@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate);

		@Query(nativeQuery = true, value = "select a.total, "
		        +" a.uuid as uuid,a.fName as fName,a.lName as lName,a.companyName as companyName,a.days,a.dats as disDate"
				+" from ("
		        +" SELECT  max(insFollow.id) as id,count(distinct claims.claim_uuid) as total,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days,count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date) ) as dats "
				+ "FROM rcm_user us " 
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join office off on  off.company_id=cmp.company_id "
				+ "inner join company comp on comp.uuid=off.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_insurance_follow_up_section insFollow on insFollow.claim_uuid=claims.claim_uuid and insFollow.created_by=:userId and insFollow.team_id=:teamId and insFollow.final_submit=true "
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds) and   us.uuid=:userId and "// uoff.team_id =:teamId   and "
				+  nextActionAndRebillProductionwherewithuser +" and "
				+ " claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by us.uuid,comp.name"
				+"  )a inner join rcm_insurance_follow_up_section b where a.id=b.id ")
			List<ProductionDto> claimProductionForCDPByInsuranceFollowUpAssoicate(
				@Param("companyIds") List<String> companyIds, @Param("teamId") int teamId,
				@Param("startDate") String stDate, @Param("endDate") String endDate, @Param("userId") String userId);
		
		//CDP BY APPEAL
		@Query(nativeQuery = true, value = "select a.total, "
		        +" a.uuid as uuid,a.fName as fName,a.lName as lName,a.companyName as companyName,a.days,a.dats as disDate"
				+" from ("
		        +" SELECT  max(appeal.id) as id,count(distinct claims.claim_uuid) as total,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days,count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date) ) as dats "
				+ "FROM rcm_user us " 
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join office off on  off.company_id=cmp.company_id "
				+ "inner join company comp on comp.uuid=off.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_appeal_level_info_section appeal on appeal.claim_uuid=claims.claim_uuid and appeal.team_id=:teamId and appeal.final_submit=true  "
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds) and "//uoff.team_id =:teamId  "
				+  nextActionAndRebillProductionwhere +" and "
				+ "claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by us.uuid,comp.name"
				+"  )a inner join rcm_insurance_follow_up_section b where a.id=b.id ")
		List<ProductionDto> claimProductionForCDPByAppeal(@Param("companyIds") List<String> companyIds,
				@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate);

		@Query(nativeQuery = true, value = "select a.total, "
		        +" a.uuid as uuid,a.fName as fName,a.lName as lName,a.companyName as companyName,a.days,a.dats as disDate"
				+" from ("
		        +" SELECT  max(appeal.id) as id,count(distinct claims.claim_uuid) as total,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days,count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date) ) as dats "
				+ "FROM rcm_user us " 
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ "inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
				+ "inner join office off on  off.company_id=cmp.company_id "
				+ "inner join company comp on comp.uuid=off.company_id "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ "left join rcm_appeal_level_info_section appeal on appeal.claim_uuid=claims.claim_uuid and appeal.created_by=:userId and appeal.team_id=:teamId and appeal.final_submit=true  "
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds) and  us.uuid=:userId and "//uoff.team_id =:teamId  "
				+  nextActionAndRebillProductionwherewithuser +" and "
				+ "claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by us.uuid,comp.name"
				+"  )a inner join rcm_insurance_follow_up_section b where a.id=b.id ")
		List<ProductionDto> claimProductionForCDPByAppealAssoicate(@Param("companyIds") List<String> companyIds,
				@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate,
				@Param("userId") String userId);
		
		// payment posting
		@Query(nativeQuery = true, value = "SELECT count(distinct claims.claim_uuid) as total,"
				+ "sum(claims.amount_received_in_bank) as totalAmountReceivedInBank,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days "
				+ "FROM rcm_user us "
				//+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
				+ " inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  "
				+ "inner join  office off on off.company_id=cmp.company_id "
				+ "inner join  company comp on comp.uuid=off.company_id   "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ nextActionAndRebillProductionjoin
				+ " where cmp.company_id in (:companyIds) and "// uoff.team_id =:teamId and "
				+ nextActionAndRebillProductionwhere
				+ " and " + "claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED + " group by us.uuid")
		List<ProductionForPaymentPosting> claimProductionForPaymentPosting(@Param("companyIds") List<String> companyIds,
				@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate);

		@Query(nativeQuery = true, value = "SELECT count(distinct claims.claim_uuid) as total,"
				+ "sum(claims.amount_received_in_bank) as totalAmountReceivedInBank,"
				+ "us.uuid as uuid,us.first_name as fName,us.last_name as lName,comp.name as companyName,FLOOR(count(distinct claims.claim_uuid))/count(distinct cast(COALESCE(nextAction.created_date,rebillingSection.created_date) as date)) as days "
				+ "FROM rcm_user us "
			    //+ "inner join rcm_user_assign_office uoff on uoff.user_id=us.uuid "
			    + " inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid  "
				+ "inner join  office off on off.company_id=cmp.company_id "
				+ "inner join  company comp on comp.uuid=off.company_id   "
				+ "left join rcm_claims claims on claims.office_id=off.uuid "
				+ nextActionAndRebillProductionjoin
				+ "where cmp.company_id in (:companyIds) and "// uoff.team_id =:teamId and "
				+ nextActionAndRebillProductionwherewithuser
			
				+ " and " + "claims.current_state="
				+ Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED
				+ " and us.uuid=:userId group by us.uuid")
		List<ProductionForPaymentPosting> claimProductionForPaymentPostingAssoicate(@Param("companyIds") List<String> companyIds,
				@Param("teamId") int teamId, @Param("startDate") String stDate, @Param("endDate") String endDate,
				@Param("userId") String userId);
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId, "
				+ "cl.claim_uuid as claimUuid ,cl.current_state as currentState,"
				+ "cl.current_status as currentStatus,status_es_updated as statusEsUpdated,cl.patient_id as patientId,cl.patient_name as patientName "
				+ " from  rcm_claims  cl where "
				+ " cl.office_id=:officeId and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED+" and cl.claim_id  REGEXP :claimsIds "
				+ " " )//select * from rcm_claims where claim_id  REGEXP '_13767_P|P';
		List<ReconcillationClaimDto> getClaimbyOfficeAndClaimIdsArchived(@Param("officeId") String officeId,
				@Param("claimsIds") String claimsIds);
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId, "
				+ "cl.claim_uuid as claimUuid ,cl.current_state as currentState,"
				+ "cl.current_status as currentStatus,status_es_updated as statusEsUpdated,cl.patient_id as patientId,cl.patient_name as patientName from "
				+ " rcm_claims  cl where "
				+ " cl.office_id=:officeId and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_id in :claimsIds "
				+ " " )
		List<ReconcillationClaimDto> getClaimbyOfficeAndClaimIdsUnarchived(@Param("officeId") String officeId,
				@Param("claimsIds") List<String> claimsIds);
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId, "
				+ "cl.claim_uuid as claimUuid ,cl.current_state as currentState,"
				+ "cl.current_status as currentStatus,status_es_updated as statusEsUpdated,cl.patient_id as patientId,cl.patient_name as patientName from "
				+ " rcm_claims  cl where "
				+ " cl.office_id=:officeId and status_es_updated=:statusEsUpdated and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_id in :claimsIds "
				+ " " )
		List<ReconcillationClaimDto> getClaimbyOfficeAndClaimIdsUnarchivedAndWithStatusEsUpdated(@Param("officeId") String officeId,
				@Param("claimsIds") List<String> claimsIds,@Param("statusEsUpdated") String statusEsUpdated);
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId, "
				+ "cl.claim_uuid as claimUuid ,cl.current_state as currentState,"
				+ "cl.current_status as currentStatus,status_es_updated as statusEsUpdated,cl.patient_id as patientId,cl.patient_name as patientName from "
				+ " rcm_claims  cl where "
				+ " cl.office_id=:officeId and status_es_updated in ('Unbilled','Open') and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_id in :claimsIds "
				+ " " )
		List<ReconcillationClaimDto> getClaimbyOfficeAndClaimIdsUnarchivedAndWithStatusEsUpdatedOPenUnbilled(@Param("officeId") String officeId,
				@Param("claimsIds") List<String> claimsIds);
		
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId, "
				+ "cl.claim_id as claimUuid ,cl.resolved as currentState,"
				+ "cl.is_archive as currentStatus,cl.issue as patientId,cl.issue as patientName "
				+ " from  rcm_issue_claims  cl where "
				+ " cl.office_id=:officeId and cl.resolved=false and is_archive=false and cl.claim_id in :claimsIds "
				+ " " )
		List<ReconcillationClaimDto> getClaimInIssueClaimByClaimIdAndOfficeUnarchived(@Param("officeId") String officeId,
				@Param("claimsIds") List<String> claimsIds);
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId, "
				+ "cl.claim_id as claimUuid ,cl.resolved as currentState,"
				+ "cl.is_archive as currentStatus,cl.issue as patientId,cl.issue as patientName "
				+ " from  rcm_issue_claims  cl where "
				+ " cl.office_id=:officeId and cl.resolved=false and is_archive=true and cl.claim_id REGEXP :claimsIds "
				+ " " )//select * from rcm_claims where claim_id  REGEXP '_13767_P|P';
		List<ReconcillationClaimDto> getClaimInIssueClaimByClaimIdAndOfficeArchived(@Param("officeId") String officeId,
				@Param("claimsIds") String claimsIds);
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId from  rcm_claims  cl where "
				+ " cl.office_id=:officeId and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED+" and cl.claim_id  REGEXP :claimsId "
				+ " " )//select * from rcm_claims where claim_id  REGEXP '_arc_13767_P';
		List<String> getClaimbyOfficeAndClaimIdsArchivedForBatch(@Param("officeId") String officeId,
				@Param("claimsId") String claimsId);
		
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_uuid as claimUuid,"
				+ "cl.claim_id as claimId ,cl.current_status as currentStatus,"
				+ "cl.current_state as currentState,cl.patient_id as patientId,cl.patient_name as patientName "
				+"  from  rcm_claims  cl where "
				+ " cl.office_id=:officeId and cl.pending = :pend and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_id  like :type "
				+ " " )
		List<ReconcillationClaimDto> getClaimbyOfficeAndNotArchivedPrimaryorSecondarySubmitedorNot(@Param("officeId") String officeId,
				@Param("type") String type,@Param("pend") boolean pend);
		
		@Query(nativeQuery = true, value = "SELECT cl.claim_uuid as claimUuid,"
				+ "cl.claim_id as claimId ,cl.current_status as currentStatus,"
				+ "cl.current_state as currentState,cl.patient_id as patientId,cl.patient_name as patientName "
				+"  from  rcm_claims  cl where "
				+ " cl.office_id=:officeId and status_es_updated=:esUpdatedStatus and cl.pending = :pend and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_id  like :type "
				+ " " )
		List<ReconcillationClaimDto> getClaimbyOfficeAndNotArchivedPrimaryorSecondarySubmitedorNotEsUpdatedStatus(@Param("officeId") String officeId,
				@Param("type") String type,@Param("pend") boolean pend,@Param("esUpdatedStatus") String esUpdatedStatus);
	
		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId from  rcm_claims  cl where "
				+ " cl.office_id=:officeId and (pending is true or status_es_updated=:esUpdatedStatus) and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_id  REGEXP :claimsId "
				+ " " )//select * from rcm_claims where claim_id  REGEXP '_arc_13767_P';
		List<ReconcillationClaimDto> getClaimbyOfficeAndClaimIdsEsUpdatedStatusOrNotSubmittedByBilling(@Param("officeId") String officeId,
				@Param("claimsId") String claimsId,@Param("esUpdatedStatus") String esUpdatedStatus);

		@Query(nativeQuery = true, value = "SELECT cl.claim_id as claimId from  rcm_claims  cl where "
				+ " cl.office_id=:officeId and status_es_updated=:esUpdatedStatus and cl.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED+" and cl.claim_id  REGEXP :claimsId "
				+ " " )//select * from rcm_claims where claim_id  REGEXP '_arc_13767_P';
		List<ReconcillationClaimDto> getClaimbyOfficeAndClaimIdsEsUpdatedStatus(@Param("officeId") String officeId,
				@Param("claimsId") String claimsId,@Param("esUpdatedStatus") String esUpdatedStatus);


}
