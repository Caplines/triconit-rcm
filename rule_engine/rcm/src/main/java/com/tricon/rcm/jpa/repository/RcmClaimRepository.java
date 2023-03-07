package com.tricon.rcm.jpa.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.IVFDto;
import com.tricon.rcm.dto.customquery.IssueClaimDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;

public interface RcmClaimRepository extends JpaRepository<RcmClaims, String> {

	RcmClaims findByClaimIdAndOffice(String claimId, RcmOffice office);

	//RcmClaims findByClaimId(String claimId);

	RcmClaims findByClaimUuid(String claimId);

	List<RcmClaims> findByClaimIdInAndOffice(List<String> claimIds, RcmOffice office);

	String s=" SELECT off.name as officeName,"
			+ "sum(Case When claim_status_type_id=:status and pending is true Then 1 ELSE 0 End) as 'count' , "
			+ "min(Case When claim_status_type_id=:status Then date (cl.created_date) End)   as 'opdt', "
			+ "min(Case When claim_status_type_id=:status Then cl.dos End)   as 'opdos', "
			+ "off.uuid as officeUuid,0 as remoteLiteRejections FROM  office off left join rcm_claims  cl "
			+ "on off.uuid=cl.office_id where off.company_id=:companyId and ( cl.current_team_id=:teamId  or  current_team_id is null )" + "group by off.uuid";
	
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
	         + "  and (cl.current_team_id=:teamId or  cl.current_team_id is null) "
			+ "   and (assign.active is true or assign.active is null ) "
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
			+ " inner join company cmp on cmp.uuid=off.company_id where cmp.uuid=:companyId")
	List<FreshClaimLogDto> fetchFreshClaimLogs(@Param("companyId") String companyId);

	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal " + " from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and claims.last_work_team_id is null and off.company_id=:companyId " + " and pending=true ")
	List<FreshClaimDataDto> fetchFreshClaimDetails(@Param("companyId") String companyId, @Param("teamid") int teamid);
	
	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal " + " from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and claims.last_work_team_id is not null and off.company_id=:companyId " + " and pending=true ")
	List<FreshClaimDataDto> fetchClaimDetailsWorkedByTeam(@Param("companyId") String companyId, @Param("teamid") int teamid);

	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName, "
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,DATEDIFF(sysdate(),claims.dos) as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal " + " from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id<>:teamid and off.company_id=:companyId " + " and pending=true ")
	List<FreshClaimDataDto> fetchFreshClaimDetailsOtherTeam(@Param("companyId") String companyId,
			@Param("teamid") int teamid);

	@Query(nativeQuery = true, value = " SELECT off.name as officeName,off.uuid as  officeUuid,"
			+ " count(Case When claim_status_type_id in :status and pending is true and cl.rcm_insurance_type in :inst Then 'bill' End) as 'count', "
			+ " min(Case When claim_status_type_id in :status and pending=true  and cl.rcm_insurance_type in :inst Then cl.created_date End)   as 'opdt',"
			+ " min(Case When claim_status_type_id in :status and pending=true  and cl.rcm_insurance_type in :inst Then cl.dos End) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName " + " FROM "
			+ "  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  left join rcm_user_assign_office assig on assig.office_id=off.uuid "
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where off.company_id=:companyId  group by off.uuid")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignments(@Param("companyId") String companyId,
			@Param("status") List<Integer> status, @Param("inst") Set<Integer> inst);
	
	@Query(nativeQuery = true, value = 
			" select count(distinct assign.claim_id) as total,count(distinct DATE(assign.created_date)) as days ,us.uuid as uuid,us.first_name "+
			" as fName,us.last_name as lName from rcm_user us "+
			" left join rcm_claim_assignment assign on us.uuid=assign.assigned_to "+
			" and  assign.created_date between STR_TO_DATE( :startDate, '%m/%d/%Y %H:%i:%s') and STR_TO_DATE(:endDate, '%m/%d/%Y %H:%i:%s') "+
			" left join rcm_claims cl on cl.claim_uuid=assign.claim_id  and team_id=:teamId and taken_back is false and  cl.pending is true  "+
			" left join office off on off.uuid=cl.office_id  "+
			" where   us.company_id=:companyId  and us.team_id=:teamId group by us.uuid ")
	List<ProductionDto> claimProductionByTeamMember(@Param("companyId") String companyId,
			@Param("teamId") int teamId,@Param("startDate") String stDate,@Param("endDate") String endDate);


	@Query(nativeQuery = true, value = " select claim_uuid uuid,cl.claim_id claimId,dos,patient_birth_date  patientDob,"+
			" patient_id patientId,patient_name patientName,pending ,"+
			" prim_date_sent primDateSent, prim_status primeStatus,prim_total_paid primeTotalPaid,"+
			" rcm_source source, sec_date_sent secDateSend,sec_status secStatus,"+
			" sec_submitted_total secSubmittedTotal,submitted_total submittedTotal, timely_fil_lmt_dt timeFilLimitDay,"+
			" off.name officeName,off.uuid officeUuid,ct.id claimStatus,lTeam.name lastTeam,cTeam.name currentTeam,"+
			" pins.name primInsurance,sins.name secInsurance, cl.group_number groupNumber,cl.prime_policy_holder primePolicyHolder,"+
			" prime_sec_submitted_total primeSecSubmittedTotal,sec_policy_holder_dob secPolicyHolderDob,"+
			" cl.created_date createdDate,assign.assigned_to assignedTo,us.email,us.first_name firstName,us.last_name lastName,"+
			" pinst.name primaryInsType,sinst.name secondaryInsType,cmp.name clientName,cl.regenerated regenerated, " +
			" cl.sec_member_id secMemberId,cl.sec_policy_holder secPolicyHolder, "+ 
			" cl.provider_id providerId,cl.created_date pulledDate "+
			"  from  rcm_claims cl inner join office off on  off.uuid=cl.office_id "+
			"  inner join company cmp on cmp.uuid=off.company_id"+
			"  inner join rcm_claim_status_type ct on ct.id=cl.claim_status_type_id"+
			"  left join rcm_team Cteam  on Cteam.id=cl.current_team_id"+
			"  left join rcm_team lTeam  on lTeam.id=cl.last_work_team_id"+
			"  left join rcm_insurance pins on pins.id = cl.prim_insurance_company_id"+
			"  left join rcm_insurance sins on sins.id = cl.sec_insurance_company_id"+
			"  left join rcm_insurance_type pinst on pins.insurance_type_id = pinst.id"+
			"  left join rcm_insurance_type sinst on sins.insurance_type_id = sinst.id"+
			"  left join rcm_claim_assignment assign on  assign.claim_id=cl.claim_uuid and assign.active=1"+
			"  left join rcm_user us on us.uuid=assign.assigned_to"+
			"  where claim_uuid=:claimUuid and cmp.uuid=:companyId")
	RcmClaimDetailDto fetchIndividualClaim(@Param("companyId")  String companyId,@Param("claimUuid")  String claimUuid) ;
	
	
	
	@Query(nativeQuery = true, value = "select ivf_form_id,date_of_service from reports_claim where "
			+ " office_id=:officeId and claim_id=:claimid and  patient_id=:patientId limit 1 ")
	Object getIVIdOfClaim(@Param("claimid") String claimid,@Param("officeId") String officeId,
			@Param("patientId") String patientId);
	
	/*
	@Query(nativeQuery = true, value = "select treatement_plan_id from reports where "
			+ " office_id=:officeId and ivf_form_id=:ivId and  patient_id=:patientId")
    String getTreatmentPlanIdIV(@Param("ivId") String ivId,@Param("officeId") String officeId,
			@Param("patientId") String patientId);
	*/		
	@Query(nativeQuery = true, value = ""
			+" SELECT treatement_plan_id FROM reports r inner join report_detail rd on rd.report_id=r.id "
			+" where office_id=:officeId and patient_id=:patientId "
			+" and treatement_plan_id not in ('PREBATCHMODE','SEALANTMODE') and iv_date is not null "
			+" and STR_TO_DATE(iv_date, '%m/%d/%Y')<:dos "
			+" order by STR_TO_DATE(iv_date, '%m/%d/%Y') desc limit 1"
			)
    String getLatestTPIdForPatientDosAndIV(@Param("officeId") String officeId,
			@Param("patientId") String patientId,@Param("dos") String dos);
	
	@Query(nativeQuery = true, value = ""+
			" select claim_id claimId,issue,source,off.name officeName,cl.created_date createdDate from rcm_issue_claims cl "+
			" left join office off on  off.uuid=cl.office_id "+
			" where off.company_id=:cmpid and cl.resolved is false")
	List<IssueClaimDto> getIssueClaims(@Param("cmpid") String ivId);
	
	
	@Query(nativeQuery = true, value = ""+
			" select r.claim_id claimId,rd.rule_id ruleId,rs.name ruleName,r.patient_name patientName,patient_id patientId,r.ivf_form_id ivId,rd.iv_date ivDate,rd.date_of_service"+
			" dos,rd.error_message message,rd.message_type mType,"+
			" rd.surface surface,rd.tooth tooth,rd.codes codes,off.name officeName,rd.insurance_type insuranceType from reports_claim r "+
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
			"  where r.claim_id=:claim_id and r.office_id=:office_id and r.patient_id=:patientid and off.company_id=:cmp_id ")
	List<RuleEngineClaimDto> getRuleEngineClaimReport(@Param("office_id") String officeId,@Param("cmp_id") String companyId,
			@Param("patientid") String patientId,@Param("claim_id") String claimId);
	
	
	;
	@Query(nativeQuery = true, value = ""+
			" SELECT ivf_form_id ivId,office_id officeId FROM reports_claim r inner join report_claim_detail rd "+
			" on rd.report_id=r.id "+
			" where r.claim_id=:claim_id and r.patient_id=:patientid and "+
			"  r.office_id=:office_id order by STR_TO_DATE( iv_date, '%m/%d/%Y'),rd.group_run desc limit 1"	)
	IVFDto getLatestIvfNumberForClaim(@Param("office_id") String officeId,
			@Param("patientid") String patientId,@Param("claim_id") String claimId);
	
	
	@Query(nativeQuery = true, value = "select claim_uuid,pending from rcm_claims where "
			+ " office_id=:officeId and claim_id=:claimid ")
	Object getClaimsUuidClaimId(@Param("claimid") String claimid,@Param("officeId") String officeId);

	
}
