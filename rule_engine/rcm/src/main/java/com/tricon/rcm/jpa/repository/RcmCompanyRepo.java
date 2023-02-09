package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.dto.customquery.RcmCompanyWithGsheetDto;

public interface RcmCompanyRepo  extends JpaRepository<RcmCompany, String> {

	RcmCompany findByName(String name); 
	RcmCompany findByUuid(String uuid);
	@Query(value="select distinct(c.uuid) as CompanyUuid,c.name as CompanyName,m.google_sheet_id as GoogleSheetId,m.google_sheet_sub_id as GoogleSheetSubId,m.google_sheet_sub_name as GoogleSheetSubName from company c join rcm_mapping_table m on m.company_id=c.uuid",nativeQuery=true)
	List<RcmCompanyWithGsheetDto> getCompanyWithGsheetData();

}
