package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.dto.customquery.RcmCompanyWithGsheetDto;

public interface RcmMappingTableRepo extends JpaRepository<RcmMappingTable, Integer>{
	
	RcmMappingTable findByNameAndCompany(String name,RcmCompany comp);
	
//	@Query(value="select m.name as Name,m.google_sheet_id as GoogleSheetId,m.google_sheet_sub_id as GoogleSheetSubId,m.google_sheet_sub_name as GoogleSheetSubName from rcm_mapping_table m where m.company_id=:companyUuid And name<>:ignoreName",nativeQuery=true)
//	List<RcmCompanyWithGsheetDto> getCompanyWithGsheetData(@Param("companyUuid")String companyUuid,@Param("ignoreName")String ignoreName);
	
	@Query(value="select m.name as Name,m.google_sheet_id as Google_sheet_id,m.google_sheet_sub_id as Google_sheet_sub_id,m.google_sheet_sub_name as Google_sheet_sub_name from rcm_mapping_table m where m.company_id=:companyUuid And name=:Name",nativeQuery=true)
	List<RcmCompanyWithGsheetDto> getDataFromRcmMapping(@Param("companyUuid")String companyUuid,@Param("Name")String Name);
	
	@Query(value="select m.name as Name,m.google_sheet_id as Google_sheet_id from rcm_mapping_table m where m.company_id=:clientUuid",nativeQuery=true)
	List<RcmCompanyWithGsheetDto> findGsheetLinkByClientuuid(@Param("clientUuid")String clientUuid);
}
