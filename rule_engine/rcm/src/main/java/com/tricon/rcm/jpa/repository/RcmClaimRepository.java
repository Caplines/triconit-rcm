package com.tricon.rcm.jpa.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;
import com.tricon.rcm.dto.customquery.ProductionDto;
import com.tricon.rcm.dto.customquery.AssignFreshClaimLogsDto;
import com.tricon.rcm.dto.customquery.FreshClaimDataDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;

public interface RcmClaimRepository extends JpaRepository<RcmClaims, String> {

	RcmClaims findByClaimIdAndOffice(String claimId, RcmOffice office);

	//RcmClaims findByClaimId(String claimId);

	RcmClaims findByClaimUuid(String claimId);

	List<RcmClaims> findByClaimIdInAndOffice(List<String> claimIds, RcmOffice office);

	
	@Query(nativeQuery = true, value = "SELECT off.name as officeName,"
			+ "count(Case When claim_status_type_id=:status and pending is true Then 'billre' End) as 'count' , "
			+ "min(Case When claim_status_type_id=:status Then cl.created_date End)   as 'opdt', "
			+ "min(Case When claim_status_type_id=:status Then cl.dos End)   as 'opdos', "
			+ "off.uuid as officeUuid,0 as remoteLiteRejections FROM  office off left join rcm_claims  cl "
			+ "on off.uuid=cl.office_id where off.company_id=:companyId " + "group by off.uuid ")
	List<FreshClaimDetailsDto> fetchBillingOrReBillingClaimDetails(@Param("companyId") String companyId,
			@Param("status") int status);

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
			+ "  where claims.current_team_id=:teamid and claims.last_work_team_id is null and off.company_id=:companyId " + " and pending=true ")
	List<FreshClaimDataDto> fetchFreshClaimDetails(@Param("companyId") String companyId, @Param("teamid") int teamid);
	
	@Query(nativeQuery = true, value = " select off.name as officeName,claims.claim_uuid as uuid ,claims.claim_id as claimId,claims.patient_id as patientId,"
			+ " claims.dos as dos ,claims.patient_name as patientName,"
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

	
	

	
}
