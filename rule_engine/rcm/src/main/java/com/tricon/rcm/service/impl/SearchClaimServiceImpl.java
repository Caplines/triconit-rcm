package com.tricon.rcm.service.impl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmUserClientWithOfficeDto;
import com.tricon.rcm.dto.SearchClaimDto;
import com.tricon.rcm.dto.SearchClaimPaginationDto;
import com.tricon.rcm.dto.SearchClaimResponseDto;
import com.tricon.rcm.dto.download.SearchClaimDownloadDto;
import com.tricon.rcm.enums.AgeBracketEnum;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.SearchClaimRepo;
import com.tricon.rcm.pdfDto.SearchClaimPdfDto;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.SearchClaimUtil;

@Service
public class SearchClaimServiceImpl {

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	RcmCommonServiceImpl commonService;
	

	@Autowired
	SearchClaimRepo searchClaimRepo;
	
	@Autowired
	DownLoadService downloadService;

	@Value("${data.searchClaims.totalRecordperPage}")
	private int totalRecordsperPage;

	public List<RcmUserClientWithOfficeDto> getUserClientsWithOffices(JwtUser jwtUser) throws Exception {
		List<RcmUserClientWithOfficeDto> listOfOffices = new ArrayList<>();
		jwtUser.getCompanies().stream().forEach(data -> {
			if (data != null && !data.getUuid().isEmpty()) {
				RcmUserClientWithOfficeDto officesdata = new RcmUserClientWithOfficeDto();
				List<RcmOfficeDto> offices = commonService.getOfficesByUuid(data.getUuid());
				officesdata.setClientUuid(data.getUuid());
				officesdata.setClientName(data.getName());
				officesdata.setOffices(offices);
				listOfOffices.add(officesdata);
			}
		});
		return listOfOffices;
	}

	public List<SearchClaimPaginationDto> searchClaims(SearchClaimDto dto) throws Exception {

		StringBuilder searchQuery = new StringBuilder();

		List<SearchClaimPaginationDto> paginationData = null;
		SearchClaimPaginationDto paginationDto = null;
		List<Object[]> searchClaimData = null;
		SearchClaimResponseDto searchResponseDto=null;
		List<SearchClaimResponseDto> searchResponseList=null;
		
		boolean isOverdueDefaultButtonActive=dto.getDefaultButtonType()==Constants.OVERDUE_UNBILLED_MEDICAID || dto.getDefaultButtonType()==Constants.OVERDUE_UNBILLED_NON_MEDICAID;

		// set clients.This is Mandatory to set clients
		searchQuery = SearchClaimUtil.setClientUuid(dto.getClientUuid(), searchQuery);

		// set office
		if (!dto.getOfficeUuid().isEmpty()) {
			searchQuery = SearchClaimUtil.setOfficeUuid(dto.getOfficeUuid(), searchQuery);
		}
		
		// set Claim Status
		if (!dto.getClaimStatus().isEmpty()) {
			searchQuery = SearchClaimUtil.setClaimStatus(dto.getClaimStatus(), searchQuery);
		}

		// set patientId
		if (StringUtils.isNotBlank(dto.getPatientId())) {
			searchQuery = SearchClaimUtil.setPatientId(dto.getPatientId(), searchQuery);
		}

		// set claimId
		if (StringUtils.isNotBlank(dto.getClaimId())) {
			searchQuery = SearchClaimUtil.setClaimId(dto.getClaimId(), searchQuery);
		}

		// set date range

		if (StringUtils.isNotBlank(dto.getStartDate()) && StringUtils.isNotBlank(dto.getEndDate())) {

			// verify date format-yyyy-MM-dd

			Date checkStartDate = Constants.SDF_SHEET_DATE.parse(dto.getStartDate());
			Date checkEndDate = Constants.SDF_SHEET_DATE.parse(dto.getEndDate());
			if (dto.getStartDate().equals(Constants.SDF_SHEET_DATE.format(checkStartDate))
					&& dto.getEndDate().equals(Constants.SDF_SHEET_DATE.format(checkEndDate))) {
				searchQuery = SearchClaimUtil.setDateRange(dto.getStartDate(), dto.getEndDate(), searchQuery);
			}
		}

		// set Archive Status

		searchQuery = SearchClaimUtil.setArchiveStatus(dto.getShowArchive(), searchQuery);

		// set Insurance Name
		if (!dto.getInsuranceName().isEmpty()) {
			searchQuery = SearchClaimUtil.setInsuranceName(dto.getInsuranceName(), searchQuery);
		}

		// set Insurance type
		if (!dto.getInsuranceType().isEmpty()) {
			searchQuery = SearchClaimUtil.setInsuranceType(dto.getInsuranceType(), searchQuery);
		}

		// set Provider Name
		if (!dto.getProviderName().isEmpty()) {
			searchQuery = SearchClaimUtil.setProviderName(dto.getProviderName(), searchQuery);
		}

		// set Provider type
		if (!dto.getProviderType().isEmpty()) {
			searchQuery = SearchClaimUtil.setProviderType(dto.getProviderType(), searchQuery);
		}

		// set Currently Responsible Team
		if (!dto.getResponsibleTeam().isEmpty()) {
			searchQuery = SearchClaimUtil.setResponsibleTeam(dto.getResponsibleTeam(), searchQuery);
		}

		// set age bracket

		if (!dto.getAgeCategory().isEmpty()) {
			if (dto.getAgeCategory().size() < 5
					&& !dto.getAgeCategory().stream().anyMatch(x -> AgeBracketEnum.getAgeByValue(x) == 0))
				searchQuery = SearchClaimUtil.setAgeCategory(
						dto.getAgeCategory().stream().distinct().collect(Collectors.toList()), searchQuery);
		}
		
		// set pending since for default button-type

		if (dto.getDefaultButtonType() == Constants.OVERDUE_UNBILLED_MEDICAID
				|| dto.getDefaultButtonType() == Constants.OVERDUE_UNBILLED_NON_MEDICAID) {
			searchQuery = SearchClaimUtil.setPendingSinceForOverdueClaims(dto.getResponsibleTeam(), searchQuery);
		}
		
		long counts = searchClaimRepo.generateCountQuery(searchQuery,isOverdueDefaultButtonActive);
		if (counts == 0) {
			paginationData = new ArrayList<>();
			paginationDto = new SearchClaimPaginationDto();
			paginationDto.setTotalElements(counts);
			paginationData.add(paginationDto);
			return paginationData;
		}
		searchClaimData = searchClaimRepo.buildSearchQuery(searchQuery, dto.getPageNumber(), totalRecordsperPage,isOverdueDefaultButtonActive);
		if (searchClaimData != null && !searchClaimData.isEmpty()) {
			paginationData = new ArrayList<>();
			paginationDto = new SearchClaimPaginationDto();
			searchResponseList=new ArrayList<>();
			// set data beacuse data is List<Object> form by default
			for (Object[] data : searchClaimData) {
				searchResponseDto = new SearchClaimResponseDto();
				//BeanUtils.copyProperties(data, searchResponseDto); not working 
				searchResponseDto.setOfficeName((String) data[0]);
				searchResponseDto.setUuid((String) data[1]);
				searchResponseDto.setClaimId((String) data[2]);
				searchResponseDto.setPatientId((String) data[3]);
				searchResponseDto.setDos((Date) data[4]);
				searchResponseDto.setPatientName((String) data[5]);
				searchResponseDto.setStatusType(String.valueOf(data[6]));
				searchResponseDto.setPrimaryInsurance((String) data[7]);
				searchResponseDto.setSecondaryInsurance((String) data[8]);
				searchResponseDto.setPrName((String) data[9]);
				searchResponseDto.setSecName((String) data[10]);
				//System.out.println( data[11].getClass().getName());
				if (data[11].getClass().getName().equals("java.lang.Integer")) {
					searchResponseDto.setClaimAge(((Integer) data[11]).intValue());
				}else {
					searchResponseDto.setClaimAge(((BigInteger) data[11]).intValue());
				}
				
				searchResponseDto.setTimelyFilingLimitData((String) data[12]);
				searchResponseDto.setBilledAmount((float) data[13]);
				searchResponseDto.setPrimTotal((float) data[14]);
				searchResponseDto.setSecTotal((float) data[15]);
				searchResponseDto.setPrimeSecSubmittedTotal((Float) data[16]);
				searchResponseDto.setClientName((String) data[17]);
				searchResponseList.add(searchResponseDto);
			}
			paginationDto.setData(searchResponseList);
			paginationDto.setPageNumber(dto.getPageNumber());
			paginationDto.setTotalElements(counts);
			paginationDto.setPageSize(totalRecordsperPage);
			paginationDto.setTotalPages((int) Math.ceil((double) counts / totalRecordsperPage));
			paginationData.add(paginationDto);
		}
		return paginationData;
	}

	public Object[] searchClaimsWithoutPagination(SearchClaimDto dto,String clientName) throws Exception {

		StringBuilder searchQuery = new StringBuilder();

		List<SearchClaimPdfDto> listOfData = null;
		SearchClaimPdfDto searchClaimPdfDto = null;
		List<Object[]> searchClaimData = null;
		
		boolean isOverdueDefaultButtonActive=dto.getDefaultButtonType()==Constants.OVERDUE_UNBILLED_MEDICAID || dto.getDefaultButtonType()==Constants.OVERDUE_UNBILLED_NON_MEDICAID;

		// set clients.This is Mandatory to set clients
		searchQuery = SearchClaimUtil.setClientUuid(dto.getClientUuid(), searchQuery);

		// set office
		if (!dto.getOfficeUuid().isEmpty()) {
			searchQuery = SearchClaimUtil.setOfficeUuid(dto.getOfficeUuid(), searchQuery);
		}

		// set Claim Status
		if (!dto.getClaimStatus().isEmpty()) {
			searchQuery = SearchClaimUtil.setClaimStatus(dto.getClaimStatus(), searchQuery);
		}

		// set patientId
		if (StringUtils.isNotBlank(dto.getPatientId())) {
			searchQuery = SearchClaimUtil.setPatientId(dto.getPatientId(), searchQuery);
		}

		// set claimId
		if (StringUtils.isNotBlank(dto.getClaimId())) {
			searchQuery = SearchClaimUtil.setClaimId(dto.getClaimId(), searchQuery);
		}

		// set date range

		if (StringUtils.isNotBlank(dto.getStartDate()) && StringUtils.isNotBlank(dto.getEndDate())) {

			// verify date format-yyyy-MM-dd

			Date checkStartDate = Constants.SDF_SHEET_DATE.parse(dto.getStartDate());
			Date checkEndDate = Constants.SDF_SHEET_DATE.parse(dto.getEndDate());
			if (dto.getStartDate().equals(Constants.SDF_SHEET_DATE.format(checkStartDate))
					&& dto.getEndDate().equals(Constants.SDF_SHEET_DATE.format(checkEndDate))) {
				searchQuery = SearchClaimUtil.setDateRange(dto.getStartDate(), dto.getEndDate(), searchQuery);
			}
		}

		// set Archive Status

		searchQuery = SearchClaimUtil.setArchiveStatus(dto.getShowArchive(), searchQuery);

		// set Insurance Name
		if (!dto.getInsuranceName().isEmpty()) {
			searchQuery = SearchClaimUtil.setInsuranceName(dto.getInsuranceName(), searchQuery);
		}

		// set Insurance type
		if (!dto.getInsuranceType().isEmpty()) {
			searchQuery = SearchClaimUtil.setInsuranceType(dto.getInsuranceType(), searchQuery);
		}

		// set Provider Name
		if (!dto.getProviderName().isEmpty()) {
			searchQuery = SearchClaimUtil.setProviderName(dto.getProviderName(), searchQuery);
		}

		// set Provider type
		if (!dto.getProviderType().isEmpty()) {
			searchQuery = SearchClaimUtil.setProviderType(dto.getProviderType(), searchQuery);
		}

		// set Currently Responsible Team
		if (!dto.getResponsibleTeam().isEmpty()) {
			searchQuery = SearchClaimUtil.setResponsibleTeam(dto.getResponsibleTeam(), searchQuery);
		}

		// set age bracket

		if (!dto.getAgeCategory().isEmpty()) {
			if (dto.getAgeCategory().size() < 5
					&& !dto.getAgeCategory().stream().anyMatch(x -> AgeBracketEnum.getAgeByValue(x) == 0))
				searchQuery = SearchClaimUtil.setAgeCategory(
						dto.getAgeCategory().stream().distinct().collect(Collectors.toList()), searchQuery);
		}
		
		// set pending since for default button-type

		if (dto.getDefaultButtonType() == Constants.OVERDUE_UNBILLED_MEDICAID
				|| dto.getDefaultButtonType() == Constants.OVERDUE_UNBILLED_NON_MEDICAID) {
			searchQuery = SearchClaimUtil.setPendingSinceForOverdueClaims(dto.getResponsibleTeam(), searchQuery);
		}

		searchClaimData = searchClaimRepo.buildSearchQueryWithoutPagination(searchQuery,isOverdueDefaultButtonActive);
		if (searchClaimData != null && !searchClaimData.isEmpty()) {
			listOfData = new ArrayList<>();
			for (Object[] data : searchClaimData) {
				searchClaimPdfDto = new SearchClaimPdfDto();
				searchClaimPdfDto.setOfficeName((String) data[0]);
				searchClaimPdfDto.setUuid((String) data[1]);
				searchClaimPdfDto.setClaimId((String) data[2]);
				if(data[2]!=null && data[2].toString().contains(Constants.HYPHEN+Constants.ARCHIVE_PREFIX)) {
					searchClaimPdfDto.setNewClaimId(((String) data[2]).split(Constants.HYPHEN+Constants.ARCHIVE_PREFIX)[1].split(Constants.HYPHEN)[0]);
				}else {
					searchClaimPdfDto.setNewClaimId(((String) data[2]).split(Constants.HYPHEN)[0]);
				}
				searchClaimPdfDto.setPatientId((String) data[3]);
				searchClaimPdfDto.setDos(data[4]!=null?new SimpleDateFormat("yyyy-MM-dd").format((Date)data[4]):"");
				searchClaimPdfDto.setPatientName((String) data[5]);
				searchClaimPdfDto.setStatusType(String.valueOf(data[6]));
				searchClaimPdfDto.setPrimaryInsurance((String) data[7]);
				searchClaimPdfDto.setSecondaryInsurance((String) data[8]);
				searchClaimPdfDto.setPrName((String) data[9]);
				searchClaimPdfDto.setSecName((String) data[10]);
				searchClaimPdfDto.setClaimAge(String.valueOf(data[11]));
				searchClaimPdfDto.setTimelyFilingLimitData((String) data[12]);
				searchClaimPdfDto.setPrimeSecSubmittedTotal(Float.toString((Float) data[16]));
				searchClaimPdfDto.setSecTotal(Float.toString((Float)data[15]));
				searchClaimPdfDto.setClientName((String) data[17]);
				listOfData.add(searchClaimPdfDto);
			}

			// generate pdf internally

			SearchClaimDownloadDto pdfdDto = new SearchClaimDownloadDto();
			pdfdDto.setData(listOfData);
			pdfdDto.setFileName("Search-claims");
			pdfdDto.setClientName(clientName);
			return downloadService.generatePdfForSearchClaim(pdfdDto);
		}
		return null;
	}
}
