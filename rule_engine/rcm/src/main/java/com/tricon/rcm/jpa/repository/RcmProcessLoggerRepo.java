package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tricon.rcm.db.entity.RcmProcessLogger;


public interface RcmProcessLoggerRepo  extends JpaRepository<RcmProcessLogger, Long>{

	@Modifying
	@Query(value = "update rcm_process_logger set status =false where status is true", nativeQuery = true)
	void resetAllRcmProcessLoggerActiveStatus();
}
