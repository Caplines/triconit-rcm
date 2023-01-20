package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	 * @return RcmOffice list
	 */

	public List<RcmOfficeDto> getSmilePointOffice() {
		RcmCompany company = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);

		return rcmOfficeRepository.findByCompany(company);
	}

	/**
	 * Fetch all RcmTeams data
	 * @return List of RcmTeam
	 */
	public GenericResponse getTeams(boolean isSmilePoint) {
		List<Map<String,Object>>teams=new ArrayList<Map<String,Object>>();
		Map<String,Object>team=null;
		if (isSmilePoint) {
			for (RcmTeamEnum t : RcmTeamEnum.values()) {
				team = new LinkedHashMap<>();
				if (t.isSmilepoint() && t.isRoleVisible()) {
					team.put("teamName", t.getDescription());
					team.put("teamId", t.getId());
					teams.add(team);
				}
			}
		} else {
			for (RcmTeamEnum t : RcmTeamEnum.values()) {
				team = new LinkedHashMap<>();
				if (!t.isSmilepoint() && t.isRoleVisible()) {
					team.put("teamName", t.getDescription());
					team.put("teamId", t.getId());
					teams.add(team);
				}
			}
		}
		return new GenericResponse(HttpStatus.OK, "", teams);
	}

	/**
	 * Get user roles from RcmRoleEnum
	 * @return List of RcmRoles
	 */
	public GenericResponse getRoles(boolean isSmilePoint){
		List<Map<String,Object>>roles=new ArrayList<Map<String,Object>>();
		Map<String,Object>role=null;
		if (isSmilePoint) {
			for (RcmRoleEnum r : RcmRoleEnum.values()) {
				role=new LinkedHashMap<>();
				if (r.isVisibility()) {
					role.put("roleName", r.getFullName());
					role.put("roleId", r.getName());
					roles.add(role);
					
				}
			}
		} else {
			for (RcmRoleEnum r : RcmRoleEnum.values()) {
				role=new LinkedHashMap<>();
				if (r.isVisibility()
						&& !(r.getName().equals(Constants.ADMIN) || r.getName().equals(Constants.ASSOCIATE))) {
					role.put("roleName", r.getFullName());
					role.put("roleId", r.getName());
					roles.add(role);
				}
			}
		}
		return new GenericResponse(HttpStatus.OK, "",roles);
	}
}
