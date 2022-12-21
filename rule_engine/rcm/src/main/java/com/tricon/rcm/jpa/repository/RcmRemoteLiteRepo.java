package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tricon.rcm.db.entity.RcmRemoteStatusCount;

public interface RcmRemoteLiteRepo extends JpaRepository<RcmRemoteStatusCount, Integer> {

	
	
	
}
