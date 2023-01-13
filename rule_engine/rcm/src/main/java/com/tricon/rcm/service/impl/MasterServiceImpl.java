package com.tricon.rcm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserRoleRepo;
import com.tricon.rcm.util.Constants;

@Service
public class MasterServiceImpl {

	@Autowired
	RcmTeamRepo rcmTeam;

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;

	@Autowired
	RcmOfficeRepository rcmOfficeRepository;

	@Autowired
	RcmUserRoleRepo roleRepo;

	/**
	 * Fetch office data by given company name
	 * 
	 * @return RcmOffice list
	 */

	public List<RcmOfficeDto> getSmilePointOffice() {
		RcmCompany company = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);

		return rcmOfficeRepository.findByCompany(company);
	}

	/**
	 * Fetch all RcmTeams data
	 * 
	 * @return List of RcmTeam
	 */
	public List<RcmTeam> getAllTeams() {
		List<RcmTeam> team = rcmTeam.findAll();
		team.removeIf(x -> x.getNameId().contentEquals(RcmRoleEnum.valueOf("SYSTEM").getName()));
		team.removeIf(x -> x.getNameId().contentEquals(RcmRoleEnum.valueOf("ADMIN").getName()));
		return team;

	}

	/**
	 * Get user roles from RcmRoleEnum
	 * 
	 * @return List<Entry<String, String>>
	 */
	public List<Entry<String, String>> getRoles() {
		Map<String, String> data = new HashMap<>();
		RcmRoleEnum[] roles = RcmRoleEnum.values();
		for (RcmRoleEnum r : roles) {
			if (r.isVisibility()) {
				data.put(r.getFullName(), r.getName());
			}
		}
		return data.entrySet().stream().collect(Collectors.toList());
	}

}
