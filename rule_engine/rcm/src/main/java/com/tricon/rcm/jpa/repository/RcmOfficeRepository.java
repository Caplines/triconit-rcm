package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.RcmOfficeDto;

public interface RcmOfficeRepository extends JpaRepository<RcmOffice, String> {

	List<RcmOfficeDto> findByCompany(RcmCompany company);
	List<RcmOffice> getByCompany(RcmCompany company);
	RcmOffice findByUuid(String uuid);
}