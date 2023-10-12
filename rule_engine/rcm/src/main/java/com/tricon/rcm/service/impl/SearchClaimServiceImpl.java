package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmUserClientWithOfficeDto;
import com.tricon.rcm.dto.SearchClaimDto;
import com.tricon.rcm.dto.SearchClaimPaginationDto;
import com.tricon.rcm.dto.SearchClaimResponseDto;
import com.tricon.rcm.enums.AgeBracketEnum;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.SearchClaimRepo;
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
		List<SearchClaimResponseDto> searchClaimData = null;

		// set clients.This is Mandatory to set clients
		searchQuery = SearchClaimUtil.setClientUuid(dto.getClientUuid(), searchQuery);

		// set office
		if (!dto.getOfficeUuid().isEmpty()) {
			searchQuery = SearchClaimUtil.setOfficeUuid(dto.getOfficeUuid(), searchQuery);
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
		long counts = searchClaimRepo.generateCountQuery(searchQuery);
		if (counts == 0) {
			paginationData = new ArrayList<>();
			paginationDto = new SearchClaimPaginationDto();
			paginationDto.setTotalElements(counts);
			paginationData.add(paginationDto);
			return paginationData;
		}
		searchClaimData = searchClaimRepo.buildSearchQuery(searchQuery, dto.getPageNumber(), totalRecordsperPage);
		if (searchClaimData != null && !searchClaimData.isEmpty()) {
			paginationData = new ArrayList<>();
			paginationDto = new SearchClaimPaginationDto();
			paginationDto.setData(searchClaimData);
			paginationDto.setPageNumber(dto.getPageNumber());
			paginationDto.setTotalElements(counts);
			paginationDto.setPageSize(totalRecordsperPage);
			paginationDto.setTotalPages((int) Math.ceil((double) counts / totalRecordsperPage));
			paginationData.add(paginationDto);
		}
		return paginationData;
	}
}
