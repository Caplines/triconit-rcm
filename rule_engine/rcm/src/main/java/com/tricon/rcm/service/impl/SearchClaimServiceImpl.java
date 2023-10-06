package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmUserClientWithOfficeDto;
import com.tricon.rcm.dto.SearchClaimDto;
import com.tricon.rcm.dto.SearchClaimPaginationDto;
import com.tricon.rcm.dto.SearchClaimResponseDto;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
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
	SearchClaimUtil searchClaimUtil;

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

		StringBuffer searchQuery = new StringBuffer();

		List<SearchClaimPaginationDto> paginationData = null;
		SearchClaimPaginationDto paginationDto = null;
		List<SearchClaimResponseDto> searchClaimData = null;

		// set clients.This is Mandatory to set clients
		searchQuery = searchClaimUtil.setClientUuid(dto.getClientUuid(), searchQuery);

		// set office
		if (!dto.getOfficeUuid().isEmpty()) {
			searchQuery = searchClaimUtil.setOfficeUuid(dto.getOfficeUuid(), searchQuery);
		}

		// set patientId
		if (StringUtils.isNotBlank(dto.getPatientId())) {
			searchQuery = searchClaimUtil.setPatientId(dto.getPatientId(), searchQuery);
		}

		// set claimId
		if (StringUtils.isNotBlank(dto.getClaimId())) {
			searchQuery = searchClaimUtil.setClaimId(dto.getClaimId(), searchQuery);
		}

		// set date range

		if (StringUtils.isNotBlank(dto.getStartDate()) && StringUtils.isNotBlank(dto.getEndDate())) {

			// verify date format-yyyy-MM-dd

			Date checkStartDate = Constants.SDF_SHEET_DATE.parse(dto.getStartDate());
			Date checkEndDate = Constants.SDF_SHEET_DATE.parse(dto.getEndDate());
			if (dto.getStartDate().equals(Constants.SDF_SHEET_DATE.format(checkStartDate))
					&& dto.getEndDate().equals(Constants.SDF_SHEET_DATE.format(checkEndDate))) {
				searchQuery = searchClaimUtil.setDateRange(dto.getStartDate(), dto.getEndDate(), searchQuery);
			}
		}

		// set Archive Status

		searchQuery = searchClaimUtil.setArchiveStatus(dto.getShowArchive(), searchQuery);

		long counts = searchClaimUtil.generateCountQuery(searchQuery);
		if (counts == 0) {
			paginationData = new ArrayList<>();
			paginationDto = new SearchClaimPaginationDto();
			paginationDto.setTotalElements(counts);
			paginationData.add(paginationDto);
			return paginationData;
		}
		searchClaimData = searchClaimUtil.buildSearchQuery(searchQuery, dto.getPageNumber(), totalRecordsperPage);
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
