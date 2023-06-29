package com.tricon.rcm.jpa.repository;

import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.IVFDto;
import com.tricon.rcm.dto.customquery.IssueClaimDto;
import com.tricon.rcm.dto.customquery.PendingClaimToReAssignDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.RcmClaimDetailDto;
import com.tricon.rcm.dto.customquery.RuleEngineClaimDto;
import com.tricon.rcm.util.Constants;
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
			+ " inner join company cmp on cmp.uuid=off.company_id where cmp.uuid=:companyId and off.active is true order by off.name asc ")
	List<FreshClaimLogDto> fetchFreshClaimLogs(@Param("companyId") String companyId);

	@Query(nativeQuery = true, value = ""
	        +" select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+" claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " where claims.first_worked_team_id=:teamid and claims.current_team_id=:teamid  and off.company_id=:companyId " + " and pending=true"
			+ " and (primary_status = "+Constants.Primary_Status_Primary+" or primary_status ="+Constants.Primary_Status_Primary_submit+" )  "
		
			+ " union "
			
			+" select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+" claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ " inner join rcm_claim_assignment assign on claims.claim_uuid=assign.claim_id and assign.current_team_id=:teamid "
			+ " where claims.first_worked_team_id<>:teamid  and off.company_id=:companyId " + " and pending=true"
			+ " and (primary_status = "+Constants.Primary_Status_Primary+" or primary_status = "+Constants.Primary_Status_Primary_submit+" )  "
						
			+ " ")
	List<FreshClaimDataDto> fetchFreshClaimDetails(@Param("companyId") String companyId, @Param("teamid") int teamid);
	
	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal, prime_sec_submitted_total primeSecSubmittedTotal from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " inner join rcm_claim_assignment rca on rca.claim_id=claims.claim_uuid "
			+ " inner join rcm_user ru on ru.uuid=rca.assigned_to "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid  and off.company_id=:companyId and rca.assigned_to=:userid and rca.active=1  and pending=true"
			+ " and (primary_status ="+Constants.Primary_Status_Primary+" or primary_status ="+Constants.Primary_Status_Primary_submit+"  ) ")
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
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and claims.last_work_team_id!=:teamid and claims.last_work_team_id!=3 and off.company_id=:companyId " + " and pending=true ")
	List<FreshClaimDataDto> fetchClaimDetailsWorkedByTeamBilling(@Param("companyId") String companyId, @Param("teamid") int teamid);

	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,"
			+ " claims.claim_status_type_id as statusType,insurance.name as primaryInsurance "
			+ " ,secinsurance.name as secondaryInsurance ,insuranceT.name prName,secinsuranceT.name secName, "
			+ " lastteam.name as lastTeam,case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge, "
			+ " timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount, "
			+ " claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal,prime_sec_submitted_total primeSecSubmittedTotal from rcm_claims claims "
			+ " left join rcm_team team on team.id=claims.current_team_id "
			+ " inner join office off on off.uuid=claims.office_id  "
			+ " left join rcm_team lastteam on lastteam.id=claims.last_work_team_id "
			+ " left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ " left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id"
			+ " left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ " left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "  where claims.current_team_id=:teamid and claims.last_work_team_id!=:teamid and off.company_id=:companyId " + " and pending=true ")
	List<FreshClaimDataDto> fetchClaimDetailsWorkedByTeamInternalAudit(@Param("companyId") String companyId, @Param("teamid") int teamid);


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
			+ " count(Case When claim_status_type_id in :status and pending is true and cl.first_worked_team_id=:teamId and cl.rcm_insurance_type in :inst Then 'bill' End) as 'count', "
			+ " min(Case When claim_status_type_id in :status and pending=true and cl.first_worked_team_id=:teamId  and cl.rcm_insurance_type in :inst Then  DATEDIFF(NOW(), cl.created_date) End)   as 'opdt',"
			+ " min(Case When claim_status_type_id in :status and pending=true and cl.first_worked_team_id=:teamId  and cl.rcm_insurance_type in :inst Then  DATEDIFF(NOW(), cl.dos) End) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName,assig.team_id as assignTeamId " + " FROM "
			+ "  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  left join rcm_user_assign_office assig on assig.office_id=off.uuid  and assig.team_id=:teamId "
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where off.company_id=:companyId and off.active is true  group by off.uuid order by off.name asc ")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignmentsByTeam(@Param("companyId") String companyId,
			@Param("status") List<Integer> status, @Param("inst") Set<Integer> inst,@Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = " SELECT off.name as officeName,off.uuid as  officeUuid,"
			+ " count(Case When claim_status_type_id in :status and pending is true and cl.rcm_insurance_type in :inst Then 'bill' End) as 'count', "
			+ " min(Case When claim_status_type_id in :status and pending=true  and cl.rcm_insurance_type in :inst Then cl.created_date End)   as 'opdt',"
			+ " min(Case When claim_status_type_id in :status and pending=true  and cl.rcm_insurance_type in :inst Then cl.dos End) as 'opdos',0 as remoteLiteRejections, "
			+ " us.uuid as assignedUser,us.first_name as fName,us.last_name  as lName,assig.team_id as assignTeamId " + " FROM "
			+ "  office off left join rcm_claims  " + "  cl on off.uuid=cl.office_id "
			+ "  left join rcm_insurance_type inst on inst.id=cl.rcm_insurance_type  "
			+ "  left join rcm_user_assign_office assig on assig.office_id=off.uuid "
			+ "  left join rcm_user us on us.uuid=assig.user_id "
			+ "  where off.company_id=:companyId and off.active is true  group by off.uuid,assig.team_id")
	List<AssignFreshClaimLogsDto> fetchClaimsForAssignments(@Param("companyId") String companyId,
			@Param("status") List<Integer> status, @Param("inst") Set<Integer> inst);
	
	//count(distinct DATE(assign.created_date)) as days
	//Production means Total Claims Submitted and No days between 2 dates 
	@Query(nativeQuery = true, value = 
		       " select count(distinct cl.claim_id) as total,FLOOR(count(distinct cl.claim_id))/(DATEDIFF(:endDate,:startDate)+1) as days ,"
						+" us.uuid as uuid,us.first_name "
						+" 	 as fName,us.last_name as lName from rcm_user us "
						+"    inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
						+"     inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
						+" 	left join rcm_claim_assignment assign on us.uuid=assign.assigned_to and assign.current_team_id=:teamId "
						+" 	left join rcm_claims cl on cl.claim_uuid=assign.claim_id "
						+"     and rut.team_id=:teamId and taken_back is false and  cl.pending is false and cl.first_worked_team_id=:teamId  "
						+" 	and  CAST(cl.updated_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d')"
						+"     and STR_TO_DATE(:endDate, '%Y-%m-%d') "
						+" 	left join office off on off.uuid=cl.office_id  "
						+" 	where   cmp.company_id=:companyId and rut.team_id=:teamId group by us.uuid")
	List<ProductionDto> claimProductionByForBilling(@Param("companyId") String companyId,
			@Param("teamId") int teamId,@Param("startDate") String stDate,@Param("endDate") String endDate);
	
	//Production means Total Claims Assigned by Internal Audit to other team and No days between 2 dates 
	@Query(nativeQuery = true, value = 
            " select count(distinct cl.claim_id) as total,FLOOR(count(distinct cl.claim_id))/(DATEDIFF(:endDate,:startDate)+1) as days ,"
			+" us.uuid as uuid,us.first_name "
			+" 	 as fName,us.last_name as lName from rcm_user us "
			+"    inner join rcm_user_company cmp on cmp.rcm_user_id=us.uuid "
			+"     inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			+" 	left join rcm_claim_assignment assign on us.uuid=assign.assigned_to and assign.current_team_id=:teamId and rut.team_id=:teamId "
			+" 	and  CAST(assign.updated_date as DATE) between STR_TO_DATE( :startDate, '%Y-%m-%d')"
			+"     and STR_TO_DATE(:endDate, '%Y-%m-%d') "
			+" 	left join rcm_claims cl on cl.claim_uuid=assign.claim_id "
			+"     and   taken_back is false and cl.first_worked_team_id=:teamId and cl.current_team_id<>:teamId "
			+" 	left join office off on off.uuid=cl.office_id  "
			+" 	where   cmp.company_id=:companyId and rut.team_id=:teamId group by us.uuid")
List<ProductionDto> claimProductionForInternalAudit(@Param("companyId") String companyId,
		@Param("teamId") int teamId,@Param("startDate") String stDate,@Param("endDate") String endDate);


	@Query(nativeQuery = true, value = " select claim_uuid uuid,cl.claim_id claimId,dos,patient_birth_date  patientDob,"+
			" patient_id patientId,patient_name patientName,pending , auto_rule_run autoRuleRun,"+
			" date_last_updated_es dateLastUpdatedES, status_es statusES,est_secondary_es estSecondaryES,description_es descriptionES, "+
			" prim_date_sent primDateSent, prim_status primeStatus,prim_total_paid primeTotalPaid,"+
			" rcm_source source, sec_date_sent secDateSend,sec_status secStatus,"+
			" sec_submitted_total secSubmittedTotal,submitted_total submittedTotal, timely_fil_lmt_dt timeFilLimitDay,"+
			" off.name officeName,off.uuid officeUuid,ct.id claimStatus,lTeam.name lastTeam,Cteam.name currentTeam,Cteam.id currentTeamId,"+
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
			" pins.insurance_code primaryInsCodeSheet,sins.insurance_code secondaryInsCodeSheet"+
			"  from  rcm_claims cl inner join office off on  off.uuid=cl.office_id "+
			"  inner join company cmp on cmp.uuid=off.company_id"+
			"  inner join rcm_claim_status_type ct on ct.id=cl.claim_status_type_id"+
			"  left join rcm_team Cteam  on Cteam.id=cl.current_team_id"+
			"  left join rcm_team lTeam  on lTeam.id=cl.last_work_team_id"+
			"  left join rcm_team Fteam  on Fteam.id=cl.first_worked_team_id"+
			"  left join rcm_insurance pins on pins.id = cl.prim_insurance_company_id"+
			"  left join rcm_insurance sins on sins.id = cl.sec_insurance_company_id"+
			"  left join rcm_insurance_type pinst on pins.insurance_type_id = pinst.id"+
			"  left join rcm_insurance_type sinst on sins.insurance_type_id = sinst.id"+
			"  left join rcm_claim_assignment assign on  assign.claim_id=cl.claim_uuid and assign.active=1"+
			"  left join rcm_user us on us.uuid=assign.assigned_to"+
			"  where claim_uuid=:claimUuid and cmp.uuid=:companyId")
	RcmClaimDetailDto fetchIndividualClaim(@Param("companyId")  String companyId,@Param("claimUuid")  String claimUuid) ;
	
	//9 May 2023 and IV Date is 5th May 2023 -
	@Query(nativeQuery = true, value = "select pd.id ivId,p.office_id officeId,general_date_iv_wasdone dos,policy_holder_dob pdob,policy_holder pdName " + 
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
	
	@Query(nativeQuery = true, value = ""+
			" select claim_id claimId,issue,source,off.name officeName,cl.created_date createdDate from rcm_issue_claims cl "+
			" left join office off on  off.uuid=cl.office_id "+
			" where off.company_id=:cmpid and cl.resolved is false order by cl.created_date desc")
	List<IssueClaimDto> getIssueClaims(@Param("cmpid") String ivId);
	
	
	String IVF_DATA_QUERY =""+
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
	
	
	@Query(nativeQuery = true, value = "select cl.claim_uuid,cl.pending,sins.name secInsurance from rcm_claims cl "
			+ " left join rcm_insurance sins on sins.id = cl.sec_insurance_company_id "
			+ " where "
			+ " cl.office_id=:officeId and cl.claim_id=:claimid ")
	Object getClaimsUuidClaimIdSec(@Param("claimid") String claimid,@Param("officeId") String officeId);
	
	@Query(nativeQuery = true, value = "select cl.claim_uuid,cl.pending,sins.name primInsurance from rcm_claims cl "
			+ " left join rcm_insurance sins on sins.id = cl.prim_insurance_company_id "
			+ " where "
			+ " cl.office_id=:officeId and cl.claim_id=:claimid ")
	Object getClaimsUuidClaimIdPrim(@Param("claimid") String claimid,@Param("officeId") String officeId);
	
	
	@Query(nativeQuery = true, value = ""
			+ " select cl.claim_uuid from rcm_claims cl inner join office off on off.uuid=cl.office_id and "
			+ " off.company_id=:companyId  where pending is true and cl.claim_uuid "
			+ " not in (select cl.claim_uuid ascl from rcm_claims cl inner join rcm_claim_assignment ass on "
			+ " ass.claim_id=cl.claim_uuid inner join office off on off.uuid=cl.office_id  where  "
			+ " cl.pending is true and off.company_id=:companyId )")
	List<String> getUnAsignedClaims(@Param("companyId") String companyId);
	
	
	@Query(nativeQuery = true, value = ""
			+ " select cl.claim_uuid from rcm_claims cl inner join office off on off.uuid=cl.office_id and "
			+ " off.company_id=:companyId and  off.office_id=:officeId where pending is true and cl.claim_uuid "
			+ " not in (select cl.claim_uuid ascl from rcm_claims cl inner join rcm_claim_assignment ass on "
			+ " ass.claim_id=cl.claim_uuid inner join office off on off.uuid=cl.office_id  where  "
			+ " cl.pending is true and off.company_id=:companyId and  off.office_id=:officeId )")
	List<String> getUnAsignedClaimByOffice(@Param("companyId") String companyId,@Param("officeId") String officeId);

	
	@Query(nativeQuery = true, value = ""
			+ "select count(concat(rt.name,off.name)) as count,rt.name as teamName,off.name as officeName,rt.id as teamId from rcm_claims rc " + 
			" inner join office off on off.uuid=rc.office_id inner join rcm_team rt on rt.id=rc.current_team_id " + 
			" inner join company cmp on cmp.uuid=off.company_id" + 
			" where rc.pending is true  and cmp.uuid=:companyId " + 
			" group by rc.office_id,rt.name ")
	List<AllPendencyDto> allPendencyCount(@Param("companyId") String companyId);
	
	@Query(nativeQuery = true, value = ""
			+ "select min(rc.dos) minDate,min(cast(rc.created_date as Date)) dt,rt.name as teamName,off.name as officeName,rt.id as teamId from rcm_claims rc " + 
			" inner join office off on off.uuid=rc.office_id inner join rcm_team rt on rt.id=rc.current_team_id " + 
			" inner join company cmp on cmp.uuid=off.company_id" + 
			" where rc.pending is true  and cmp.uuid=:companyId " + 
			" group by rc.office_id,rt.name ")
	List<AllPendencyDateDto> allPendencyDateCount(@Param("companyId") String companyId);
	
	@Query(nativeQuery = true, value = ""
			+ " select distinct rc.claim_id as claimId,rc.office_id as officeId,rc.claim_uuid as claimUUid  from rcm_claims rc inner join office off on off.uuid=rc.office_id "
			+ " inner join company cmp on cmp.uuid=off.company_id left join rcm_claim_detail rcd on rcd.claim_id=rc.claim_uuid "
			+ " where  cmp.name=:companyName and date(rc.created_date)  >= (CURDATE() - interval :days day) and rc.pending is true and rcd.id is null "
			+ "")
	List<ClaimXDaysDto> getClaimIdsdWithNoDetailForGivenLastDays(@Param("companyName") String companyName,
			@Param("days") int days);
	
	@Query(nativeQuery = true, value = ""
			+ " select distinct rc.claim_id as claimId,rc.office_id as officeId,rc.claim_uuid as claimUUid  from rcm_claims rc inner join office off on off.uuid=rc.office_id "
			+ " inner join company cmp on cmp.uuid=off.company_id left join rcm_claim_detail rcd on rcd.claim_id=rc.claim_uuid "
			+ " where  cmp.name=:companyName and off.uuid=:offuuid and date(rc.created_date)  >= (CURDATE() - interval :days day) and rc.pending is true and rcd.id is null "
			+ "")
	List<ClaimXDaysDto> getClaimIdsdWithNoDetailForGivenLastDayForOffice(@Param("companyName") String companyName,
			@Param("days") int days,@Param("offuuid") String offuuid);
	
	@Query(nativeQuery = true, value = ""
			+" select plan_assignment_of_benefits  as planAssignmentofBenefits ,memberId as basicInfo16,group_p as basicInfo14," 
			+" concat(coalesce(p.first_name,''),' ',coalesce(p.last_name,'')) as basicInfo2, p.dob as basicInfo6," 
		    +" policy_holder as basicInfo5 from patient p inner join patient_detail pd  on p.id=pd.patient_id "
			+ " where pd.id=:ivId ")
	DataPatientRuleDto getDataForRuleCheckFromIV(@Param("ivId") String ivId);


	@Query(value = "select cl.claim_id claimId,cl.issue,cl.source,off.name officeName,cl.created_date createdDate from rcm_issue_claims cl "
			+ "left join office off on off.uuid=cl.office_id "
			+ "where off.company_id=:companyId and cl.resolved is false order by cl.id limit :offset, :limit", nativeQuery = true)
	List<IssueClaimDto> getIssueClaimsByPagination(@Param("companyId") String companyId,@Param("offset")int offSet,@Param("limit")int limit); //and off.activeis true
	
	@Query(nativeQuery = true, value = "select pd.id ivId,p.office_id officeId,general_date_iv_wasdone dos " + 
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
		"	where  com.uuid=:companyId and pending is true")
	List<PendingClaimToReAssignDto> fetchAllPendingClaimsAssignedToSomeOneByCompanyIdAndTeamId(@Param("companyId") String companyId,@Param("teamId")  int teamId) ;

}
