package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;

public interface RcmClaimRepository extends JpaRepository<RcmClaims, String> {

	RcmClaims findByClaimIdAndOffice(String claimId,RcmOffice office);
	
	List<RcmClaims> findByClaimIdInAndOffice(List<String> claimIds,RcmOffice office);
	
	@Query(nativeQuery=true,value=
		"	select off.uuid as officeUuid ,off.name as officeName,"+
		"	Case when us.userName is null then ''  ELSE us.userName END as createByName,"+
		"	Case when l.created_date is null then null  ELSE l.created_date END as claimFetchDate,"+
		"	Case when new_claims_count is null then 0  ELSE new_claims_count END as claimCount,"+
		"	Case when l.source is null then ''  ELSE l.source END as source,"+
		"	Case when l.status is null then 0  ELSE l.status END as status"+
		"	 from office off left join ("+
		"	select created_date,created_by,new_claims_count,source,office_id,status"+
		"    from rcm_claim_log where id in ("+
		"	select max(id) as id from  rcm_claim_log log group by log.office_id))  l"+
		"	inner join rcm_user us on us.uuid=l.created_by "+
		"	on off.uuid=l.office_id")
	List<FreshClaimDetailsDto> fetchFreshClaimDetails();

}
