package com.tricon.rcm.jpa.repository;

import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmInsuranceType;
import com.tricon.rcm.db.entity.RcmOffice;

public interface RcmInsuranceRepo extends JpaRepository<RcmInsurance, Integer> {

	RcmInsurance findByInsuranceIdAndOffice(String insuranceId,RcmOffice office);
	RcmInsurance findByNameAndOffice(String insurance,RcmOffice office);
	RcmInsurance findByNameAndOfficeAndInsuranceType(String insurance,RcmOffice office,RcmInsuranceType insuranceType);
	
	
	@Modifying
	@Query("update RcmInsurance set active=false where office_id= :officeId")
	void inActiveAllInsuranceByOffice(@Param("officeId") String officeId);
}

