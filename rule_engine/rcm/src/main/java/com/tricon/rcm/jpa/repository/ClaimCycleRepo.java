package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.ClaimCycle;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.dto.customquery.ClaimSteps;
public interface ClaimCycleRepo extends JpaRepository<ClaimCycle, Integer> {
	
	public ClaimCycle findFirstByClaimOrderByCreatedDateDesc(RcmClaims claim);
	
	
	@Query(nativeQuery = true, value = ""
			+ " select tm.name name,rc.created_date dt,rc.status status from rcm_claim_cycle rc left join rcm_team tm on tm.id=rc.current_team_id "
			+ " where  rc.claim_id=:claimUUid order by rc.created_date asc")
	List<ClaimSteps> getClaimCycle(@Param("claimUUid") String claimUUid);
}
