package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmUserClientWithOfficeDto;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.security.JwtUser;

@Service
public class SearchClaimServiceImpl {

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
	@Autowired
	RcmCommonServiceImpl commonService;
	
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
}
