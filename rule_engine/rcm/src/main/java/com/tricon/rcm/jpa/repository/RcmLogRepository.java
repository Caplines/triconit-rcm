package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmLogs;

public interface RcmLogRepository extends JpaRepository<RcmLogs, Integer>{
	
	

}
