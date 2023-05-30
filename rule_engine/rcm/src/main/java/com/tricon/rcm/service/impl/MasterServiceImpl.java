package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmRoleDto;
import com.tricon.rcm.dto.RcmTeamDto;
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

		return rcmOfficeRepository.findByCompanyAndActiveTrueOrderByNameAsc(company);
	}

	/**
	 * Fetch all RcmTeams data
	 * @return List of RcmTeam
	 */
//	public List<RcmTeamDto> getTeams(String companyName) {
//		List<RcmTeamDto> teams = new ArrayList<>();
//		RcmTeamDto team = null;
//			if (Constants.COMPANY_NAME.equals(companyName)) {
//				for (RcmTeamEnum t : RcmTeamEnum.values()) {
//					team = new RcmTeamDto();
//					if (t.isSmilepoint() && t.isRoleVisible()) {
//						team.setTeamName(t.getDescription());
//						team.setTeamId(t.getId());
//						teams.add(team);
//					}
//				}
//			} else {
//				for (RcmTeamEnum t : RcmTeamEnum.values()) {
//					team = new RcmTeamDto();
//					if (!t.isSmilepoint() && t.isRoleVisible()) {
//						team.setTeamName(t.getDescription());
//						team.setTeamId(t.getId());
//						teams.add(team);
//					}
//				}
//			}
//			return teams;
//		} 

	/**
	 * Get user roles from RcmRoleEnum
	 * @return List of RcmRoles
	 */
//	public List<RcmRoleDto> getRoles(String companyName){
//		List<RcmRoleDto>roles=new ArrayList<>();
//		RcmRoleDto role=null;
//		if (Constants.COMPANY_NAME.equals(companyName)) {
//			for (RcmRoleEnum r : RcmRoleEnum.values()) {
//				role=new RcmRoleDto();
//				if(r.isRoleVisibilityForSmilepoint()) {
//					role.setRoleName(r.getFullName());
//					role.setRoleId(r.getName());
//					role.setTeamMandatory(r.isTeamMandatory());
//					roles.add(role);
//					
//				}
//			}
//		} else {
//			for (RcmRoleEnum r : RcmRoleEnum.values()) {
//				role=new RcmRoleDto();
//				if(r.isRoleVisibilityForOthers()) {
//					role.setRoleName(r.getFullName());
//					role.setRoleId(r.getName());
//					role.setTeamMandatory(r.isTeamMandatory());
//					roles.add(role);
//				}
//			}
//		  }
//		return roles;
//	}

	/**
	 * Get roles by teamId
	 * @param teamId
	 * @return list of roles
	 */
//	public List<RcmRoleDto> getRolesByTeamId(int teamId) {
//		List<RcmRoleDto> roles = new ArrayList<>();
//		RcmRoleDto role = null;
//		for (RcmTeamEnum t : RcmTeamEnum.values()) {
//			if (t.getId() == teamId && t.isRoleVisible()) {
//				for (RcmRoleEnum r : t.getRole()) {
//					role = new RcmRoleDto();
//					role.setRoleName(r.getFullName());
//					role.setRoleId(r.getName());
//					role.setTeamMandatory(t.isTeamMandatory());
//					roles.add(role);
//				}
//			}
//		}
//		return roles;
//	}

	/**
	 * Get defaultRoles in basis of CompanayName
	 * @param companyName
	 * @return
	 */
//	public List<RcmRoleDto> defaultRolesByCompanyName(String companyName) {
//		List<RcmRoleDto> roles = new ArrayList<>();
//		RcmRoleDto role = null;
//		if (Constants.COMPANY_NAME.equals(companyName)) {
//			for(String r:Constants.DEFAULT_ROLE_FOR_SMILEPOINT) {
//			role = new RcmRoleDto();
//			role.setRoleName(RcmRoleEnum.valueOf(r).getFullName());
//			role.setRoleId(RcmRoleEnum.valueOf(r).getName());
//			role.setTeamMandatory(RcmRoleEnum.valueOf(r).isTeamMandatory());
//			roles.add(role);
//			}
//		}else {
//			for(String r:Constants.DEFAULT_ROLE_FOR_OTHERS) {
//				role = new RcmRoleDto();
//				role.setRoleName(RcmRoleEnum.valueOf(r).getFullName());
//				role.setRoleId(RcmRoleEnum.valueOf(r).getName());
//				role.setTeamMandatory(RcmRoleEnum.valueOf(r).isTeamMandatory());
//				roles.add(role);
//				}
//		}
//		return roles;
//	}
	
	
	public List<RcmTeamDto> getTeams() {
		List<RcmTeamDto> teams = new ArrayList<>();
		RcmTeamDto team = null;
		for (RcmTeamEnum t : RcmTeamEnum.values()) {
			team = new RcmTeamDto();
			if (t.isRoleVisible()) {
				team.setTeamName(t.getDescription());
				team.setTeamId(t.getId());
				teams.add(team);
			}
		}
		return teams;
	}
	
	public List<RcmTeamDto> getTeamById(int teamId) {
		List<RcmTeamDto> teams = new ArrayList<>();
		RcmTeamDto team = null;
		for (RcmTeamEnum t : RcmTeamEnum.values()) {
			
			if (t.getId()==teamId) {
				team = new RcmTeamDto();
				team.setTeamName(t.getDescription());
				team.setTeamId(t.getId());
				teams.add(team);
			}
		}
		return teams;
	}

	public List<RcmRoleDto> getRoles() {
		List<RcmRoleDto> roles = new ArrayList<>();
		RcmRoleDto role = null;
		for (RcmRoleEnum r : RcmRoleEnum.values()) {
			if (r.isRoleVisible()) {
				role = new RcmRoleDto();
				role.setRoleName(r.getFullName());
				role.setRoleId(r.getName());
				roles.add(role);
			}

		}
		return roles;
	}

	public List<RcmCompany> getClients() throws Exception {
		return rcmCompanyRepo.findAll();
	}
}
