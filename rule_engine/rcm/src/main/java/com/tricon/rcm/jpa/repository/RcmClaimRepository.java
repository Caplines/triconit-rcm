package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.customquery.FreshClaimLogDto;

import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;

public interface RcmClaimRepository extends JpaRepository<RcmClaims, String> {

	RcmClaims findByClaimIdAndOffice(String claimId,RcmOffice office);
	
	List<RcmClaims> findByClaimIdInAndOffice(List<String> claimIds,RcmOffice office);
	
	/**
	 select off.uuid,off.name,
Case when us.first_name is null then ''  ELSE us.first_name END as userName,
Case when l.created_date is null then null  ELSE l.created_date END as created_date,
Case when new_claims_count is null then 0  ELSE new_claims_count END as new_claims_count,
Case when l.source is null then ''  ELSE l.source END as source,
Case when l.status is null then 0  ELSE l.status END as status
 from office off left join (
select created_date,created_by,new_claims_count,source,office_id,status from rule_engine_2.rcm_claim_log where id in (
select max(id) as id from  rule_engine_2.rcm_claim_log log group by log.office_id))  l

left join rcm_user_assign_office assign on assign.office_id=l.office_id
left join rcm_user us on us.uuid=assign.user_id 
on off.uuid=l.office_id
inner join company cmp on cmp.uuid=off.company_id where cmp.name='Capline';
*/
	
	@Query(nativeQuery=true,value=
			"SELECT off.name as officeName,"+
			"count(Case When claim_status_type_id=1 and pending is true Then 'bill' End) as 'bill' , "+
			"count(Case When claim_status_type_id=2 and pending is true Then 'rebill' End) as 'rebill', "+
			"min(Case When claim_status_type_id=1 Then cl.created_date End)   as 'opdt', "+
			"min(Case When claim_status_type_id=1 Then cl.dos End)   as 'opdos', "+
			"off.uuid as officeUuid,0 as remoteLiteRejections FROM  rule_engine_2.office off left join rule_engine_2.rcm_claims  cl "+
			"on off.uuid=cl.office_id where off.company_id=:companyId "+
			"group by off.uuid ")
	List<FreshClaimDetailsDto> fetchFreshClaimDetails(@Param("companyId") String companyId);
	
	@Query(nativeQuery=true,value=
			"select off.uuid as officeUuid,off.name as officeName, "+
			"Case when l.created_date is null then null  ELSE l.created_date END as cd, "+
			"Case when new_claims_count is null then 0  ELSE new_claims_count END as newClaimsCount, "+
			"Case when l.source is null then ''  ELSE l.source END as source, "+
			"Case when l.status is null then 0  ELSE l.status END as status "+
			" from office off left join (  "+
			" select created_date,created_by,new_claims_count,source,office_id,status from rule_engine_2.rcm_claim_log where id in ( "+
			" select max(id) as id from  rule_engine_2.rcm_claim_log log group by log.office_id))  l "+
			" on off.uuid=l.office_id "+ 
			" inner join company cmp on cmp.uuid=off.company_id where cmp.uuid=:companyId")
	List<FreshClaimLogDto> fetchFreshClaimLogs(@Param("companyId") String companyId);
	

}
