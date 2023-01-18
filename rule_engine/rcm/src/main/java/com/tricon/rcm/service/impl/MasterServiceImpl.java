package com.tricon.rcm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
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
	public GenericResponse getTeams(boolean isSmilePoint) {
		RcmTeamEnum[] teams = RcmTeamEnum.values();
		Map<String, Integer> team = new HashMap<>();
		if (isSmilePoint) {
			for (RcmTeamEnum t : teams) {
				if (t.isSmilepoint() && t.isRoleVisible()) {
					team.put(t.getName(), t.getId());
				}
			}
		} else {
			for (RcmTeamEnum t : teams) {
				if (!t.isSmilepoint() && t.isRoleVisible()) {
					team.put(t.getName(), t.getId());
				}
			}
		}
		return new GenericResponse(HttpStatus.OK, "", team.entrySet().stream().collect(Collectors.toList()));
	}

	/**
	 * Get user roles from RcmRoleEnum
	 * 
	 * @return List<Entry<String, String>>
	 */
	public GenericResponse getRoles(boolean isSmilePoint) {
		Map<String, String> data = new HashMap<>();
		RcmRoleEnum[] roles = RcmRoleEnum.values();
		if (isSmilePoint) {
			for (RcmRoleEnum r : roles) {
				if (r.isVisibility()) {
					data.put(r.getFullName(), r.getName());
				}
			}

		} else {
			for (RcmRoleEnum r : roles) {
				if (r.isVisibility()
						&& !(r.getName().equals(Constants.ADMIN) || r.getName().equals(Constants.ASSOCIATE))) {
					data.put(r.getFullName(), r.getName());
				}
			}
		}
		return new GenericResponse(HttpStatus.OK, "",data.entrySet().stream().collect(Collectors.toList()));
	}

}
