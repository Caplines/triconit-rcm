package com.tricon.rcm.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.ClaimUserSectionMapping;
import com.tricon.rcm.db.entity.RcmClaimDefaultSection;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmClientSectionMapping;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.RcmUserCompany;
import com.tricon.rcm.db.entity.RcmUserRole;
import com.tricon.rcm.db.entity.RcmUserRoleHistory;
import com.tricon.rcm.db.entity.RcmUserRolePk;
import com.tricon.rcm.db.entity.RcmUserTeam;
import com.tricon.rcm.dto.ClientSectionMappingDto;
import com.tricon.rcm.dto.CommonSectionsRequestBodyDto;
import com.tricon.rcm.dto.PartialHeader;
//import com.tricon.rcm.db.entity.RcmUserTemp;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto.SectionData;
import com.tricon.rcm.dto.RcmUserPaginationDto;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.SectionDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimDefaultSectionRepo;
import com.tricon.rcm.jpa.repository.RcmClaimUserSectionMappingRepo;
import com.tricon.rcm.jpa.repository.RcmClientSectionMappingRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmUserRoleHistoryRepo;
import com.tricon.rcm.jpa.repository.RcmUserRoleRepo;
import com.tricon.rcm.jpa.repository.RcmUserTeamRepo;
//import com.tricon.rcm.jpa.repository.RcmUserTempRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.ClaimSectionValidationUtil;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.EncrytedKeyUtil;
import com.tricon.rcm.util.MessageConstants;

@Service
public class RcmCommonServiceImpl {

	private final Logger logger = LoggerFactory.getLogger(RcmCommonServiceImpl.class);

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;

	@Autowired
	RCMUserRepository userRepo;

	@Autowired
	RcmOfficeRepository officeRepo;

	@Autowired
	RcmUserRoleHistoryRepo userTempRepo;

	@Autowired
	RcmUtilServiceImpl utilService;
	
	@Autowired
	RcmUserCompanyRepo userCompanyRepo;
	
	@Autowired
	RcmUserTeamRepo userTeamRepo;
	
	@Autowired
	RcmTeamRepo teamRepo;
	
	@Autowired
	RcmUserRoleRepo userRole;
	
	@Autowired
	ClaimSectionImpl claimSectionimpl;
	
	@Autowired
	RcmClaimDefaultSectionRepo claimDefaultSectionRepo;
	
	@Autowired
	RcmClaimUserSectionMappingRepo userSectionRepo;
	
	@Autowired
	RcmClientSectionMappingRepo clientSectionMappingRepo;
	
	
	@Autowired
	ClaimSectionValidationUtil claimSectionValidationUtil;
	
	@Autowired
	RcmUserCompanyRepo rcmUserCompanyRepo;
	
	public List<RcmOfficeDto> getAllOffices() {

		RcmCompany company = rcmCompanyRepo.findByName(Constants.COMPANY_NAME);

		return officeRepo.findByCompany(company);

	}

	/**
	 * This Method provides common functionality for any role to reset password.
	 * 
	 * @param RecmUser
	 * @param password
	 * @return Generic response
	 */
	public String resetPassword(RcmUser user, RcmUser updatedBy, String password) {
		String msg = "";
		try {
			user.setPassword(EncrytedKeyUtil.encryptKey(password));
			user.setUpdatedBy(updatedBy);
			user.setLastPasswordResetDate(Timestamp.from(Instant.now()));
			user.setTempPassword("null");
			userRepo.save(user);
			msg = MessageConstants.PASSWORD_UPDATE;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			msg = MessageConstants.UPDATION_FAIL;
			return msg;
		}
		return msg;
	}

	public List<ClientCustomDto> findAllClients(PartialHeader partialHeader) {
		if (partialHeader.getRole().equals(Constants.ADMIN)) {
			return rcmCompanyRepo.findAllClientsOfAssociatedUser(partialHeader.getJwtUser().getUuid());
		} else
			return rcmCompanyRepo.findAllClients();
	}

	/**
	 * Fetchs all offices of Login user's company
	 * 
	 * @param companyUuid
	 * @return
	 */
	public List<RcmOfficeDto> getOfficesByUuid(String companyUuid) {
		RcmCompany company = rcmCompanyRepo.findByUuid(companyUuid);
		if (company != null) {
			List<RcmOfficeDto> office = officeRepo.findByCompanyAndActiveTrueOrderByNameAsc(company);
			return office;
		}
		return null;
	}

	public void dumpDataToRcmUserTemp(RcmUser user, String roles, RcmUserCompany userCompany,
			RcmUserTeam userTeam) {
		RcmUserRoleHistory tempUser = new RcmUserRoleHistory();
		if (userCompany!=null && userCompany.getUser().getUuid().equals(user.getUuid())) {
			List<RcmUserCompany> clientName = userCompanyRepo.findByUserUuid(userCompany.getUser().getUuid());
			if (clientName != null && !clientName.isEmpty()) {
				tempUser.setClientName(clientName.stream().map(x -> x.getCompany().getName())
						.collect(Collectors.joining(",", "[", "]")));
			}
		}
		if (userTeam!=null && userTeam.getUser().getUuid().equals(user.getUuid())) {
			List<RcmUserTeam> teamName = userTeamRepo.findByUserUuid(userTeam.getUser().getUuid());
			if (teamName != null && !teamName.isEmpty()) {
				tempUser.setTeamName(
						teamName.stream().map(x -> x.getTeam().getName()).collect(Collectors.joining(",", "[", "]")));
			}
		}
		tempUser.setUser(user);
		tempUser.setEmail(user.getEmail());
		tempUser.setFirstName(user.getFirstName());
		tempUser.setLastName(user.getLastName());
		tempUser.setCreatedDate(Timestamp.from(Instant.now()));
		tempUser.setRolesDetails(roles);
		userTempRepo.save(tempUser);
	}

	/**
	 * From List of RcmCompany check if companyuuid is present
	 * @param companyuuid
	 * @param companies
	 * @return
	 */
	public boolean checkIfSessionClientValid(String companyuuid, List<RcmCompany> companies) {

		if (companies == null)
			return false;
		List<RcmCompany> filter = companies.stream().filter(e -> e.getUuid().equals(companyuuid))
				.collect(Collectors.toList());
		if (filter.size() == 1)
			return true;
		else
			return false;
	}

	/**
	 * From List of RcmTeam check if Team Id is present
	 * @param teamId
	 * @param teams
	 * @return boolean
	 */
	public boolean checkIfSessionTeamValid(int teamId, List<RcmTeam> teams) {

		if (teams == null)
			return false;
		List<RcmTeam> filter = teams.stream().filter(e -> e.getId() == teamId).collect(Collectors.toList());
		if (filter.size() == 1)
			return true;
		else
			return false;

	}

	/**
	 * Get SmilePoint Company from JwtToken
	 * @param jwtUser JwtUser
	 * @return RcmCompany
	 */
	public RcmCompany getSmilePointCompanyfromJWT(JwtUser jwtUser) {

		List<RcmCompany> companies = jwtUser.getCompanies();
		List<RcmCompany> filter = companies.stream().filter(e -> e.getName().equals(Constants.COMPANY_NAME))
				.collect(Collectors.toList());
		if (filter.size() == 1)
			return filter.get(0);
		else
			return null;
	}
	
	
	public RcmCompany getCompanyFormParitalHeaderCompanyId(String companyuuid, RcmCompany company) {

		if (company.getUuid().equals(companyuuid))
			return company;
		else
			return null;
	}
	

	/**
	 * Get SmilePoint Company from RcmUser
	 * @param user RcmUser
	 * @return RcmCompany
	 */
	public RcmCompany getSmilePointCompanyfromJWT(RcmUser user) {

		Set<RcmUserCompany> companies = user.getRcmCompanies();
		RcmCompany com = null;
		for (RcmUserCompany company : companies) {
			if (company.getCompany().getName().equals(Constants.COMPANY_NAME)) {
				com = company.getCompany();
				break;
			}
		}

		return com;
	}
	
	public boolean checkIfRolesValidFromJWT(String role, JwtUser jwtUser) {
		if (role == null || role.trim().equals(""))
			return false;
		return jwtUser.getAuthorities().stream()
				.anyMatch(x -> x.getAuthority().equals(Constants.ROLE_PREFIX.concat(role)));
	}
	
	public boolean isTeamLead(Set<RcmUserRole> roles) {
		return roles.stream()
				.anyMatch(x -> x.getRole().equals(Constants.ROLE_PREFIX.concat(Constants.TEAMLEAD)));
	}
	
	public boolean isSuperAdmin(JwtUser jwtUser) {
		return jwtUser.getAuthorities().stream()
				.anyMatch(x -> x.getAuthority().equals(Constants.ROLE_PREFIX.concat(Constants.SUPER_ADMIN)));
	}
	
	public boolean isAdmin(JwtUser jwtUser) {
		return jwtUser.getAuthorities().stream()
				.anyMatch(x -> x.getAuthority().equals(Constants.ROLE_PREFIX.concat(Constants.ADMIN)));
	}
	
	public boolean validateUserClients(JwtUser jwtUser, List<String> uuids) throws Exception {
		List<RcmUserCompany> clientsData = userCompanyRepo.findByUserUuid(jwtUser.getUuid());
		List<String> clientUuid = null;
		boolean isFound = false;
		if (clientsData != null && !clientsData.isEmpty()) {
			clientUuid = clientsData.stream().map(x -> x.getCompany().getUuid()).collect(Collectors.toList());
			for (String id : uuids) {
				if (clientUuid.contains(id)) {
					isFound = true;
					continue;
				} else
					isFound = false;
			}
			if (isFound)
				return true;
		}
		return false;
	}
	
	public String saveOrEditUser(RcmUser user, String role,
			List<String> companyUuid, List<Integer> teamIds,String isAdminRole) throws Exception {
		RcmUserRole roles = null;
		RcmUserRolePk pk = null;
		RcmCompany company = null;
		RcmTeam team = null;
		RcmUserCompany userCompany = null;
		RcmUserTeam userTeam = null;
		
		// save clients details
		for (String clientUuid : companyUuid) {
			company = rcmCompanyRepo.findByUuid(clientUuid);
			if (company != null) {
				userCompany = new RcmUserCompany();
				userCompany.setCompany(company);
				userCompany.setUser(user);
				userCompanyRepo.save(userCompany);
			} else {
				return MessageConstants.COMPANY_NOT_EXIST;
			}
		}
		
		//if role is SUPER_ADMIN then all clients assign to super user
		
		if (isAdminRole.equals(Constants.SUPER_ADMIN) && role.equals(Constants.SUPER_ADMIN)) {
			List<RcmCompany> clients = rcmCompanyRepo.findAll();
			for (RcmCompany c : clients) {
				userCompany = new RcmUserCompany();
				userCompany.setUser(user);
				userCompany.setCompany(c);
				userCompanyRepo.save(userCompany);
			}
		}	

		// save Teams details
		for (int teamId : teamIds) {
			team = teamRepo.findById(teamId);
			if (team != null) {
				userTeam = new RcmUserTeam();
				userTeam.setTeam(team);
				userTeam.setUser(user);
				userTeamRepo.save(userTeam);
			} else {
				return MessageConstants.TEAM_NOT_EXIT;
			}
		}

         //save role
		if (role != null && !role.trim().equals("")) {
				roles = new RcmUserRole();
				pk = new RcmUserRolePk();
				pk.setUuid(user.getUuid());
				roles.setId(pk);
				roles.setRole(RcmTeamEnum.generateRoleByRoleType(role));
				userRole.save(roles);
			
		}
		dumpDataToRcmUserTemp(user,role,userCompany,userTeam);
		
		return null;
	}
	
	public void syncClientsWithSuperAdminAndAddDefaultSections(RcmCompany client, String isSuperAdmin) throws Exception {
		RcmUserCompany userCompany = null;
		if (client != null && isSuperAdmin.equals(Constants.SUPER_ADMIN)) {
			List<RcmUserToDto> data = userRepo.findSuperAdminUser(Constants.ROLE_PREFIX + Constants.SUPER_ADMIN);
			List<String> uuid = data.stream().map(x -> x.getUuid()).collect(Collectors.toList());
			List<RcmUser> user = userRepo.findByUuidIn(uuid);
			if (user != null && !user.isEmpty()) {
				for (RcmUser u : user) {
					userCompany = new RcmUserCompany();
					userCompany.setUser(u);
					userCompany.setCompany(client);
					userCompanyRepo.save(userCompany);
				}
			}
			
			// sync client with default section and dump data into
			// rcm_claim_client_section_mapping table when added new client from manage
			// client section

			List<RcmClaimDefaultSection> defaultSections = claimDefaultSectionRepo.findAll();
			if (!defaultSections.isEmpty()) {
				ClientSectionMappingDto sectionMappingDto = new ClientSectionMappingDto();
				List<RcmTeamSectionAccessDto> teamsWithSectionsList = new ArrayList<>();
				RcmTeamSectionAccessDto teamsWithSections = null;
				sectionMappingDto.setClientUuid(client.getUuid());
				for (RcmClaimDefaultSection defaultSection : defaultSections) {
					SectionData sectionData = new SectionData();
					if (!teamsWithSectionsList.isEmpty() && teamsWithSectionsList.stream()
							.anyMatch(x -> x.getTeamId() == defaultSection.getTeamId().getId())) {
						sectionData.setSectionId(defaultSection.getSection().getId());
						sectionData.setEditAccess(defaultSection.isEditAccess());
						sectionData.setViewAccess(defaultSection.isViewAccess());
						teamsWithSectionsList.forEach(x -> {
							if (x.getTeamId() == defaultSection.getTeamId().getId()) {
								List<SectionData> existingSectionDataWitjSameTeam = x.getSectionData();
								existingSectionDataWitjSameTeam.add(sectionData);
							}
						});
					} else {
						teamsWithSections = new RcmTeamSectionAccessDto();
						List<SectionData> listOfSections = new ArrayList<>();
						teamsWithSections.setTeamId(defaultSection.getTeamId().getId());
						sectionData.setSectionId(defaultSection.getSection().getId());
						sectionData.setEditAccess(defaultSection.isEditAccess());
						sectionData.setViewAccess(defaultSection.isViewAccess());
						listOfSections.add(sectionData);
						teamsWithSections.setSectionData(listOfSections);
						teamsWithSectionsList.add(teamsWithSections);
					}
				}
				sectionMappingDto.setTeamsWithSections(teamsWithSectionsList);
				String response=claimSectionimpl.manageClientSectionDetails(Arrays.asList(sectionMappingDto));
				logger.info(response);
			}

		}
	}
	
	public List<RcmUserPaginationDto> setUsersInPaginationDto(Page<RcmUserToDto> pageableList){		
		List<RcmUserPaginationDto> listOfUsers = new ArrayList<>();
		RcmUserPaginationDto paginationDto = new RcmUserPaginationDto();
		if (pageableList != null && !pageableList.isEmpty()) {
			paginationDto.setData(pageableList.getContent());
			paginationDto.setPageNumber(pageableList.getNumber());
			paginationDto.setTotalElements(pageableList.getTotalElements());
			paginationDto.setPageSize(pageableList.getSize());
			paginationDto.setHasNextElement(pageableList.hasNext());
			listOfUsers.add(paginationDto);
			return listOfUsers;
		}
		return null;
	}
	
	public void dumpUserDataToRcmUserTemp(RcmUser user,String role,RcmUserCompany saveClients,RcmUserTeam userTeam) {
		RcmUserRoleHistory tempUser = new RcmUserRoleHistory();
		String roles=null;
		if (user.getRcmCompanies()!=null) {
			List<RcmUserCompany> clientName = userCompanyRepo.findByUserUuid(user.getUuid());
			if (clientName != null && !clientName.isEmpty()) {
				tempUser.setClientName(clientName.stream().map(x -> x.getCompany().getName())
						.collect(Collectors.joining(",", "[", "]")));
			}
		}
		
		if (saveClients!=null && saveClients.getUser().getUuid().equals(user.getUuid())) {
			List<RcmUserCompany> clientName = userCompanyRepo.findByUserUuid(saveClients.getUser().getUuid());
			if (clientName != null && !clientName.isEmpty()) {
				tempUser.setClientName(clientName.stream().map(x -> x.getCompany().getName())
						.collect(Collectors.joining(",", "[", "]")));
			}
		}
		if (user.getRcmTeams()!=null) {
			List<RcmUserTeam> teamName = userTeamRepo.findByUserUuid(user.getUuid());
			if (teamName != null && !teamName.isEmpty()) {
				tempUser.setTeamName(
						teamName.stream().map(x -> x.getTeam().getName()).collect(Collectors.joining(",", "[", "]")));
			}
		}
		
		if (userTeam!=null && userTeam.getUser().getUuid().equals(user.getUuid())) {
			List<RcmUserTeam> teamName = userTeamRepo.findByUserUuid(userTeam.getUser().getUuid());
			if (teamName != null && !teamName.isEmpty()) {
				tempUser.setTeamName(
						teamName.stream().map(x -> x.getTeam().getName()).collect(Collectors.joining(",", "[", "]")));
			}
		}
		
		if(role==null) {
			roles=user.getRoles().stream().map(x->x.getRole()).findFirst().orElse("null");
		}else {
			roles=role;
		}
		tempUser.setUser(user);
		tempUser.setEmail(user.getEmail());
		tempUser.setFirstName(user.getFirstName());
		tempUser.setLastName(user.getLastName());
		tempUser.setCreatedDate(Timestamp.from(Instant.now()));
		tempUser.setRolesDetails(roles);
		userTempRepo.save(tempUser);
	}
	
	public boolean validateUserSectionAccess(PartialHeader partialHeader, int sectionId, RcmUser user)
			throws Exception {
		if (user != null) {
			if (partialHeader.getRole().equals(Constants.SUPER_ADMIN)) {
				RcmClientSectionMapping clientSecrtionMapping = clientSectionMappingRepo
						.findByCompanyUuidAndTeamIdIdAndSectionId(partialHeader.getCompany().getUuid(),
								partialHeader.getTeamId(), sectionId);
				if (clientSecrtionMapping != null && clientSecrtionMapping.isEditAccess()) {
					return true;
				}
			} else {
//				ClaimUserSectionMapping checkSectionAccessOfLoginUser = userSectionRepo
//						.findByCompanyUuidAndTeamIdIdAndSectionIdAndUserUuid(partialHeader.getCompany().getUuid(),
//								partialHeader.getTeamId(), sectionId, user.getUuid());
//				if (checkSectionAccessOfLoginUser != null && checkSectionAccessOfLoginUser.isEditAccess()) {
//					return true;
//				}

				List<ClientSectionMappingDto> sectionAccessPermission = claimSectionimpl.sectionsPermissionOfUser(
						partialHeader.getJwtUser().getUuid(), partialHeader.getCompany().getUuid(),
						partialHeader.getTeamId());
				if (sectionAccessPermission.isEmpty()) {
					return false;
				} else {
					List<RcmTeamSectionAccessDto> teamWithSection = sectionAccessPermission.get(0)
							.getTeamsWithSections();
					List<SectionData> sections = teamWithSection.get(0).getSectionData();
					SectionData section = sections.stream()
							.filter(x -> x.getSectionId() == sectionId && x.getEditAccess()).findAny().orElse(null);
					return section != null ? true : false;
				}

			}
		}
		return false;
	}

	public Object saveCommonSectionInformations(CommonSectionsRequestBodyDto sectionRequestBody,
			PartialHeader partialHeader, int sectionId, List<SectionDto> sectionsData, RcmUser createdBy,
			RcmClaims claim, RcmTeam team) throws Exception {
		Object response = null;
		SectionDto section = sectionsData.stream().filter(x -> x.getSectionId() == sectionId && x.isActive()).findAny()
				.orElse(null);
		if (section != null) {
			switch (section.getSectionId()) {
			// Case 1 to 13 has been covered already in phase 1 so it will start case 13(New
			// section)
			case 13:
				logger.info("Inside section 13-> Claim Level Information");
				response = claimSectionimpl.saveClaimLevelInformation(sectionRequestBody.getClaimInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				logger.info("response->" + response);
				break;
			case 19:
				logger.info("Inside section 19->Appeal");
				response = claimSectionimpl.saveAppealInformation(sectionRequestBody.getAppealInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				logger.info("response->" + response);
				break;
				
			case 20:
				logger.info("Inside section 20->Patient Payment");
				response = claimSectionimpl.savePatientPaymentSection(sectionRequestBody.getPatientPaymentInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				logger.info("response->" + response);
				break;
			case 12:
				logger.info("Inside section 12->EOB");
				response = claimSectionimpl.saveEOBSection(sectionRequestBody.getEobInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				//logger.info("response->" + response);
				break;
				
			case 14:
				logger.info("Inside section 14->Insurance Payment Information");
				response = claimSectionimpl.saveInsurancePaymentInformationSection(sectionRequestBody.getPaymentInformationInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				logger.info("response->" + response);
				break;
			case 11:
				logger.info("Inside section 11->Service Level Information");
				response = claimSectionimpl.saveServiceLevelInformationSection(sectionRequestBody.getServiceLevelInformationInfoModel().getServiceLevelTotalAmount(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit(),partialHeader.getClientName());
				logger.info("response->" + response);
				break;
				
			case 17:
				logger.info("Inside section 17->Follow-up Insurance");
				response = claimSectionimpl.saveFollowUpInsuranceSection(sectionRequestBody.getRcmFollowUpInsuranceInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit(),partialHeader.getClientName());
				//logger.info("response->" + response);
				break;
				
			case 15:
				logger.info("Inside section 15->Patient Statement");
				response = claimSectionimpl.savePatientStatementSection(sectionRequestBody.getRcmPatientStatementInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit(),partialHeader.getClientName());
				logger.info("response->" + response);
				break;
			case 26:
				logger.info("Inside section 26->Next Action Required");
				response = claimSectionimpl.saveNextActionRequiredAndCurrentClaimStatusSection(sectionRequestBody.getNextActionRequiredInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit(),partialHeader.getClientName());
				logger.info("response->" + response);
				break;
			case 21:
				logger.info("Inside section 21->Patient Communication");
				response = claimSectionimpl.savePatientCommunicationSection(sectionRequestBody.getPatientCommunicationInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit(),partialHeader.getClientName());
				break;

			case 25:
//				logger.info("Inside section 25->Need to call insurance");
//				response = claimSectionimpl.saveNeedToCallInsuranceSection(sectionRequestBody.getNeedToCallInfoModel(),
//						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
//				logger.info("response->" + response);
//				break;
			case 22:
				logger.info("Inside section 22->Collection Agency");
				response = claimSectionimpl.saveCollectionAgencySection(sectionRequestBody.getCollectionAgencyInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				logger.info("response->" + response);
				break;
				
			case 23:
				logger.info("Inside section 23->RequestRebilling");
				response = claimSectionimpl.saveRequestRebillingSection(partialHeader,sectionRequestBody.getRequestRebillingInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				logger.info("response->" + response);
				break;
				
			case 24:
				logger.info("Inside section 24->Rebilling");
				response = claimSectionimpl.saveRebillingSection(partialHeader,sectionRequestBody.getRebillingInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				break;
				
			case 18:
				logger.info("Inside section 18->Recreate the claim");
				response = claimSectionimpl.saveRecreateClaimSection(partialHeader,sectionRequestBody.getRecreateClaimRequestInfoModel(),
						 claim, createdBy, team, sectionRequestBody.isFinalSubmit());
				break;

			default:
				logger.error("section not found");
			}
		}
		return response;
	}

	public boolean commonSectionInformationsForAllWithValidation(CommonSectionsRequestBodyDto sectionRequestBody,
			PartialHeader partialHeader, int sectionId, List<SectionDto> sectionsData) {
		boolean response = false;
		SectionDto section = sectionsData.stream().filter(x -> x.getSectionId() == sectionId && x.isActive()).findAny()
				.orElse(null);
		if (section != null) {
			switch (section.getSectionId()) {
			// Case 1 to 13 has been covered already in phase 1 so it will start case 13(New
			// section)
			case 13:
				logger.info("Inside section 13-> Claim Level Information");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForClaimInfoSectionFields(sectionRequestBody.getClaimInfoModel());
				logger.info("validation response->" + response);
				break;
			case 19:
				logger.info("Inside section 19->Appeal");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForAppealInfoSectionFields(sectionRequestBody.getAppealInfoModel());
				logger.info("validation response->" + response);
				break;
			case 20:
				logger.info("Inside section 20->Patient Payment");
				if (sectionRequestBody.isFinalSubmit())
//					response = claimSectionValidationUtil
//							.validationForPatientPaymentSectionFields(sectionRequestBody.getPatientPaymentInfoModel());
				logger.info("validation response->" + response);
				break;
			case 12:
				logger.info("Inside section 12->EOB");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForEOBSectionFields(sectionRequestBody.getEobInfoModel());
				logger.info("validation response->" + response);
				break;
			case 14:
				logger.info("Inside section 14->Insurance Payment Information");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForInsurancePaymentInformationSectionFields(sectionRequestBody.getPaymentInformationInfoModel());
				logger.info("validation response->" + response);
				break;
			case 11:
				logger.info("Inside section 11->Service Level Information");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForServiceLevelInformationSectionFields(sectionRequestBody.getServiceLevelInformationInfoModel());
				logger.info("validation response->" + response);
				break;
			case 17:
				logger.info("Inside section 17->Follow-up Insurance");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForFollowUpInsuranceSectionFields(sectionRequestBody.getRcmFollowUpInsuranceInfoModel());
				logger.info("validation response->" + response);
				break;
			case 15:
				logger.info("Inside section 15->Patient Statement");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForPatientStatementSectionFields(sectionRequestBody.getRcmPatientStatementInfoModel());
				logger.info("validation response->" + response);
				break;
			case 26:
				logger.info("Inside section 26->Next Action Required");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForCurrentClaimStatusAndNextActionSectionFields(sectionRequestBody.getNextActionRequiredInfoModel());
				logger.info("validation response->" + response);
				break;
			case 21:
				logger.info("Inside section 21->Patient Communication");
				if (sectionRequestBody.isFinalSubmit())
					response = claimSectionValidationUtil
							.validationForPatientCommunicationSectionFields(sectionRequestBody.getPatientCommunicationInfoModel());
				logger.info("validation response->" + response);
				break;
			default:
				logger.error("section not found");
			}
		}
		return response;
	}
	
	public List<RcmOfficeDto> getAllOfficesByClientUuid(String companyUuid) {
		RcmCompany company = rcmCompanyRepo.findByUuid(companyUuid);
		if (company != null) {
			List<RcmOfficeDto> office = officeRepo.findByCompanyOrderByNameAsc(company);
			return office;
		}
		return null;
	}
}
