package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tricon.rcm.db.entity.RcmClaimSection;

public interface RcmClaimSectionRepo extends JpaRepository<RcmClaimSection,Integer>{
	
	@Query("SELECT cs FROM RcmClaimSection cs LEFT JOIN FETCH cs.sectionCategory sc order by sc.id ")
	List<RcmClaimSection> findAllWithSectionCategory();

}
