package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.RcmOfficeDto;

public interface RcmOfficeRepository extends JpaRepository<RcmOffice, String> {

	List<RcmOfficeDto> findByCompany(RcmCompany company);
	List<RcmOffice> getByCompany(RcmCompany company);
	RcmOffice findByUuid(String uuid);
	RcmOffice findByCompanyAndName(RcmCompany company,String officeName);
	RcmOffice findByNameAndCompanyUuid(String uuid,String companyUuid);
	List<RcmOffice> findByUuidInAndCompanyUuid(List<String> uuids,String companyUuid);
	@Query(value="select max(id) from office",nativeQuery=true)
	int getMaxKeyFromOffice();
}