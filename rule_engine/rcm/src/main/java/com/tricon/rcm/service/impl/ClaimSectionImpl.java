package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.ClaimUserSectionMapping;
import com.tricon.rcm.db.entity.RcmClaimSection;
import com.tricon.rcm.db.entity.RcmClientSectionMapping;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClientSectionMappingDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto.SectionData;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.enums.RcmRoleEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmClaimDefaultSectionRepo;
import com.tricon.rcm.jpa.repository.RcmClaimSectionRepo;
import com.tricon.rcm.jpa.repository.RcmClaimUserSectionMappingRepo;
import com.tricon.rcm.jpa.repository.RcmClientSectionMappingRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;


@Service
public class ClaimSectionImpl {

	private final Logger logger = LoggerFactory.getLogger(ClaimSectionImpl.class);

	@Autowired
	RcmClientSectionMappingRepo clientSectionMappingRepo;

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;

	@Autowired
	RcmTeamRepo teamRepo;

	@Autowired
	RcmClaimSectionRepo sectionRepo;
	
	
	@Autowired
	MasterServiceImpl masterServiceImpl;
	
	@Autowired
	RcmClaimSectionRepo claimSectionRepo;
	
	@Autowired
	RcmClaimDefaultSectionRepo claimDefaultSectionRepo;
	
	@Autowired
	RCMUserRepository userRepo;
	
	@Autowired
	RcmClaimUserSectionMappingRepo userSectionRepo;

	@Transactional(rollbackOn = Exception.class)
	public String manageClientSectionDetails(List<ClientSectionMappingDto> listOfClaimSections) throws Exception {
		String msg = null;
		RcmClientSectionMapping sectionMapping = null;
		List<RcmCompany> listOfClients = rcmCompanyRepo.findAll();
		List<RcmTeam> listOfTeams = teamRepo.findAll();
		for (ClientSectionMappingDto dto : listOfClaimSections) {
			RcmCompany client = listOfClients.stream().filter(x -> x.getUuid().equals(dto.getClientUuid())).findAny()
					.orElse(null);
			if (client != null) {
				List<RcmClientSectionMapping> existingClientMapping = clientSectionMappingRepo
						.findByCompanyUuid(client.getUuid());
				List<ClaimUserSectionMapping> userSectionMapping = userSectionRepo
						.findByCompanyUuid(client.getUuid());
				for (RcmTeamSectionAccessDto data : dto.getTeamsWithSections()) {
					sectionMapping = new RcmClientSectionMapping();
					RcmTeam team = listOfTeams.stream().filter(x -> x.getId() == data.getTeamId()).findAny()
							.orElse(null);
					if (team != null) {
						// fetch sections data from dto
						for (SectionData sections : data.getSectionData()) {
							sectionMapping = new RcmClientSectionMapping();
							// if existing section with client and team then we can edit accessibility
							// otherwise add new sections with accessibility				
							RcmClientSectionMapping existingTeamWithSection = existingClientMapping.stream()
									.filter(x -> x.getTeamId().getId() == team.getId()
											&& x.getSection().getId() == sections.getSectionId()
											&& x.getCompany().getUuid().equals(client.getUuid()))
									.findAny().orElse(null);
							ClaimUserSectionMapping  existingUserWithSection = userSectionMapping.stream()
									.filter(x -> x.getTeamId().getId() == team.getId()
											&& x.getSection().getId() == sections.getSectionId()
											&& x.getCompany().getUuid().equals(client.getUuid()))
									.findAny().orElse(null);

							if (existingTeamWithSection != null) {
								existingTeamWithSection.setEditAccess(sections.getEditAccess());
								existingTeamWithSection.setViewAccess(
										sections.getEditAccess() == true ? true : sections.getViewAccess());
								existingTeamWithSection = clientSectionMappingRepo.save(existingTeamWithSection);
								//if edit and view is  false then remove data from userSections
								if(existingUserWithSection!=null && (!existingTeamWithSection.isEditAccess()&& !existingTeamWithSection.isViewAccess())) {	
									userSectionRepo.delete(existingUserWithSection);
								}
								
								if (existingTeamWithSection == null) {
									msg = MessageConstants.SOMETHING_WENT_WRONG;
									logger.error(msg);
									break;
								}
							} else {
								sectionMapping.setCompany(client);
								sectionMapping.setTeamId(team);
								sectionMapping.setEditAccess(sections.getEditAccess());
								sectionMapping.setViewAccess(
										sections.getEditAccess() == true ? true : sections.getViewAccess());
								Optional<RcmClaimSection> section = sectionRepo.findById(sections.getSectionId());
								if (section.isPresent()) {
									sectionMapping.setSection(section.get());
								} else {
									msg = "Wrong Section";
									logger.error(msg);
									break;
								}
								sectionMapping = clientSectionMappingRepo.save(sectionMapping);
								if (sectionMapping == null) {
									msg = MessageConstants.SOMETHING_WENT_WRONG;
									logger.error(msg);
									break;
								}
							}
						}
					} else {
						msg = MessageConstants.TEAM_NOT_EXIT;
						logger.error(msg);
						break;
					}
				}
			} else {
				msg = MessageConstants.COMPANY_NOT_EXIST;
				logger.error(msg);
			}
		}
		return msg;
	}

//	public List<ClaimSectionMappingDto> getClientSectionDetails() throws Exception {
//		List<RcmCompany> clients = rcmCompanyRepo.findAll();
//		ClaimSectionMappingDto responseDto = null;
//		List<ClaimSectionMappingDto> response = new ArrayList<>();
//		List<RcmTeamSectionAccessDto> teamsWithSectionsList = null;
//		RcmTeamSectionAccessDto teamsWithSections = null;
//		for (RcmCompany client : clients) {
//			List<RcmClientSectionMapping> clientSectionMapping = clientSectionMappingRepo
//					.findByCompanyUuid(client.getUuid());
//			if (!clientSectionMapping.isEmpty()) {
//				responseDto = new ClaimSectionMappingDto();
//				teamsWithSectionsList = new ArrayList<>();
//				responseDto.setClientName(rcmCompanyRepo.findByUuid(client.getUuid()).getName());
//				responseDto.setClientUuid(client.getUuid());
//				for (RcmClientSectionMapping mapping : clientSectionMapping) {
//					RcmClaimSection claimSection=sectionRepo.findById(mapping.getSection().getId()).get();
//					SectionData sectionData = new SectionData();
//					if (!teamsWithSectionsList.isEmpty() && teamsWithSectionsList.stream()
//							.anyMatch(x -> x.getTeamId() == mapping.getTeamId().getId())) {
//						sectionData.setSectionId(mapping.getSection().getId());
//						sectionData.setSectionName(claimSection.getDisplayName());
//						sectionData.setSectionCategory(claimSection.getSectionCategory());
//						sectionData.setEditAccess(mapping.isEditAccess());
//						sectionData.setViewAccess(mapping.isViewAccess());
//						teamsWithSectionsList.forEach(x -> {
//							if (x.getTeamId() == mapping.getTeamId().getId()) {
//								List<SectionData> existingSectionDataWitjSameTeam = x.getSectionData();
//								existingSectionDataWitjSameTeam.add(sectionData);
//							}
//						});
//					} else {
//						teamsWithSections = new RcmTeamSectionAccessDto();
//						List<SectionData> listOfSections = new ArrayList<>();
//						teamsWithSections.setTeamId(mapping.getTeamId().getId());
//						teamsWithSections.setTeamName(teamRepo.findById(mapping.getTeamId().getId()).getName());
//						sectionData.setSectionId(mapping.getSection().getId());
//						sectionData.setSectionName(claimSection.getDisplayName());
//						sectionData.setSectionCategory(claimSection.getSectionCategory());
//						sectionData.setEditAccess(mapping.isEditAccess());
//						sectionData.setViewAccess(mapping.isViewAccess());
//						listOfSections.add(sectionData);
//						teamsWithSections.setSectionData(listOfSections);
//						teamsWithSectionsList.add(teamsWithSections);
//					}
//				}
//				responseDto.setTeamsWithSections(teamsWithSectionsList);
//				response.add(responseDto);
//			}
//		}
//		return response;
//	}
	
	
	public List<ClientSectionMappingDto> getClientsWithAllSectionsAndTeam() throws Exception {
		List<ClientSectionMappingDto> response = new ArrayList<>();
		List<RcmClaimSection> claimSections = claimSectionRepo.findAllWithSectionCategory().stream()
				.filter(x -> x.isActive() == true).collect(Collectors.toList());
		List<ClientCustomDto> clients = rcmCompanyRepo.findAllClients();
		List<RcmTeamDto> teamData = RcmTeamEnum.getAllTeamsIsRoleVisible();
		clients.forEach(client -> {
			ClientSectionMappingDto responseDto = new ClientSectionMappingDto();
			List<RcmTeamSectionAccessDto> teamsWithSectionsList = new ArrayList<>();
			responseDto.setClientName(client.getClientName());
			responseDto.setClientUuid(client.getUuid());
			List<RcmClientSectionMapping> existingClientMapping = clientSectionMappingRepo
					.findByCompanyUuid(client.getUuid());
			for (RcmTeamDto t : teamData) {
				RcmTeamSectionAccessDto teamsWithSections = new RcmTeamSectionAccessDto();
				teamsWithSections.setTeamId(t.getTeamId());
				teamsWithSections.setTeamName(t.getTeamName());
				List<SectionData> listOfSections = new ArrayList<>();
				for (RcmClaimSection section : claimSections) {
					RcmClientSectionMapping existingSectionMappingWithClient = existingClientMapping.stream()
							.filter(x -> x.getTeamId().getId() == t.getTeamId()
									&& x.getSection().getId() == section.getId()
									&& x.getCompany().getUuid().equals(client.getUuid()))
							.findAny().orElse(null);
					SectionData sectionData = new SectionData();
					sectionData.setSectionId(section.getId());
					sectionData.setSectionName(section.getDisplayName());
					sectionData.setEditAccess(
							existingSectionMappingWithClient != null ? existingSectionMappingWithClient.isEditAccess()
									: false);
					sectionData.setViewAccess(
							existingSectionMappingWithClient != null ? existingSectionMappingWithClient.isViewAccess()
									: false);
					listOfSections.add(sectionData);
				}
				teamsWithSections.setSectionData(listOfSections);
				teamsWithSectionsList.add(teamsWithSections);
			}
			responseDto.setTeamsWithSections(teamsWithSectionsList);
			response.add(responseDto);
		});
		return response;
	}

	public List<ClientSectionMappingDto> sectionsPermissionOfUser(String userUuid, String clientUuid,int selectedTeamId) throws Exception {
		RcmUser user = userRepo.findByUuid(userUuid);
		List<ClientSectionMappingDto> response = new ArrayList<>();
		List<RcmClaimSection> claimSections = claimSectionRepo.findAllWithSectionCategory().stream()
				.filter(x -> x.isActive() == true).collect(Collectors.toList());
		if (user != null) {
			List<RcmCompany> userClients = clientUuid == null
					? user.getRcmCompanies().stream().map(x -> x.getCompany()).sorted(Comparator.comparing(x->x.getName())).collect(Collectors.toList())
					: user.getRcmCompanies().stream().map(x -> x.getCompany()).sorted(Comparator.comparing(x->x.getName()))
							.filter(x -> x.getUuid().equals(clientUuid)).collect(Collectors.toList());

			List<RcmTeamDto> teamData = RcmTeamEnum.validateTeamId(selectedTeamId) != 0
					? RcmTeamEnum.getAllTeamsIsRoleVisible().stream().filter(x -> x.getTeamId() == selectedTeamId)
							.collect(Collectors.toList())
					: user.getRcmTeams().stream().map(x -> x.getTeam()).sorted(Comparator.comparing(x -> x.getName()))
							.map(team -> {
								RcmTeamDto rcmTeamData = new RcmTeamDto();
								rcmTeamData.setTeamId(team.getId());
								rcmTeamData.setTeamName(team.getName());
								return rcmTeamData;
							}).collect(Collectors.toList());

			userClients.forEach(client -> {
				ClientSectionMappingDto responseDto = new ClientSectionMappingDto();
				List<RcmTeamSectionAccessDto> teamsWithSectionsList = new ArrayList<>();
				responseDto.setClientUuid(client.getUuid());
				responseDto.setClientName(client.getName());
				responseDto.setUserUuid(userUuid);
				List<RcmClientSectionMapping> existingClientMapping = clientSectionMappingRepo
						.findByCompanyUuid(client.getUuid());
				List<ClaimUserSectionMapping> userSectionMapping = userSectionRepo
						.findByCompanyUuidAndUserUuid(client.getUuid(), user.getUuid());
				for (RcmTeamDto t : teamData) {
					RcmTeamSectionAccessDto teamsWithSections = new RcmTeamSectionAccessDto();
					teamsWithSections.setTeamId(t.getTeamId());
					teamsWithSections.setTeamName(t.getTeamName());
					List<SectionData> listOfSections = new ArrayList<>();
					for (RcmClaimSection section : claimSections) {
						RcmClientSectionMapping existingSectionMappingWithClient = existingClientMapping.stream()
								.filter(x -> x.getTeamId().getId() == t.getTeamId()
										&& x.getSection().getId() == section.getId()
										&& x.getCompany().getUuid().equals(client.getUuid()))
								.findAny().orElse(null);
						ClaimUserSectionMapping existingUserMappingWithClient = userSectionMapping.stream()
								.filter(x -> x.getTeamId().getId() == t.getTeamId()
										&& x.getSection().getId() == section.getId()
										&& x.getCompany().getUuid().equals(client.getUuid()))
								.findAny().orElse(null);

						// set the edit and view access with the combination of
						// existingSectionMappingWithClient & existingUserMappingWithClient
						SectionData sectionData = new SectionData();
						sectionData.setSectionId(section.getId());
						sectionData.setSectionName(section.getDisplayName());
						sectionData.setEditAccess(existingUserMappingWithClient == null
								? (existingSectionMappingWithClient != null
										? existingSectionMappingWithClient.isEditAccess()
										: false)
								: (existingSectionMappingWithClient != null
										? existingSectionMappingWithClient.isEditAccess()
												&& existingUserMappingWithClient.isEditAccess()
										: false));
						sectionData.setViewAccess(existingUserMappingWithClient == null
								? (existingSectionMappingWithClient != null
										? existingSectionMappingWithClient.isViewAccess()
										: false)
								: (existingSectionMappingWithClient != null
										? existingSectionMappingWithClient.isViewAccess()
												&& existingUserMappingWithClient.isViewAccess()
										: false));
						listOfSections.add(sectionData);
					}
					teamsWithSections.setSectionData(listOfSections);
					teamsWithSectionsList.add(teamsWithSections);
				}
				responseDto.setTeamsWithSections(teamsWithSectionsList);
				response.add(responseDto);
			});

		}
		return response;
	}

	@Transactional(rollbackOn = Exception.class)
	public String manageSectionsOfUsers(List<ClientSectionMappingDto> listOfClaimSections, String userUuid) {
		String msg = null;
		ClaimUserSectionMapping sectionMapping = null;
		List<RcmCompany> listOfClients = rcmCompanyRepo.findAll();
		List<RcmTeam> listOfTeams = teamRepo.findAll();
		RcmUser user = userRepo.findByUuid(userUuid);
		if (user == null)
			return msg = MessageConstants.USER_NOT_EXIST;
		for (ClientSectionMappingDto dto : listOfClaimSections) {
			RcmCompany client = listOfClients.stream().filter(x -> x.getUuid().equals(dto.getClientUuid())).findAny()
					.orElse(null);
			if (client != null) {
				List<RcmClientSectionMapping> existingClientMapping = clientSectionMappingRepo
						.findByCompanyUuid(client.getUuid());
				for (RcmTeamSectionAccessDto data : dto.getTeamsWithSections()) {
					sectionMapping = new ClaimUserSectionMapping();
					RcmTeam team = listOfTeams.stream().filter(x -> x.getId() == data.getTeamId()).findAny()
							.orElse(null);
					if (team != null) {
						// fetch sections data from dto
						for (SectionData sections : data.getSectionData()) {
							sectionMapping = new ClaimUserSectionMapping();
							// mapping with clientSection for edit and view accces
							// if view and edit false in clientSection then data will not saved in
							// userSectionMapping

							RcmClientSectionMapping existingSectionMappingWithClient = existingClientMapping.stream()
									.filter(x -> x.getTeamId().getId() == team.getId()
											&& x.getSection().getId() == sections.getSectionId()
											&& x.getCompany().getUuid().equals(client.getUuid()))
									.findAny().orElse(null);

							ClaimUserSectionMapping existingTeamWithSection = userSectionRepo
									.findByCompanyUuidAndTeamIdIdAndSectionIdAndUserUuid(client.getUuid(), team.getId(),
											sections.getSectionId(), user.getUuid());
							if (existingTeamWithSection != null) {
								existingTeamWithSection.setEditAccess(existingSectionMappingWithClient != null
										? (!(existingSectionMappingWithClient.isEditAccess()) ? false
												: sections.getEditAccess())
										: false);
								existingTeamWithSection.setViewAccess(existingSectionMappingWithClient != null
										? (!(existingSectionMappingWithClient.isViewAccess()) ? false
												: sections.getViewAccess())
										: false);
								if(!existingTeamWithSection.isEditAccess() && !existingTeamWithSection.isViewAccess()) continue;	
								existingTeamWithSection = userSectionRepo.save(existingTeamWithSection);
								if (existingTeamWithSection == null) {
									msg = MessageConstants.SOMETHING_WENT_WRONG;
									logger.error(msg);
									break;
								}
							} else {
								sectionMapping.setUser(user);
								sectionMapping.setCompany(client);
								sectionMapping.setTeamId(team);
								sectionMapping.setEditAccess(existingSectionMappingWithClient != null
										? (!(existingSectionMappingWithClient.isEditAccess()) ? false
												: sections.getEditAccess())
										: false);
								sectionMapping.setViewAccess(existingSectionMappingWithClient != null
										? (!(existingSectionMappingWithClient.isViewAccess()) ? false
												: sections.getViewAccess())
										: false);
								Optional<RcmClaimSection> section = sectionRepo.findById(sections.getSectionId());
								if (section.isPresent()) {
									sectionMapping.setSection(section.get());
								} else {
									msg = "Wrong Section";
									logger.error(msg);
									break;
								}
								if(!sectionMapping.isEditAccess() && !sectionMapping.isViewAccess()) continue;	
								sectionMapping = userSectionRepo.save(sectionMapping);
								if (sectionMapping == null) {
									msg = MessageConstants.SOMETHING_WENT_WRONG;
									logger.error(msg);
									break;
								}
							}
						}
					} else {
						msg = MessageConstants.TEAM_NOT_EXIT;
						logger.error(msg);
						break;
					}
				}
			} else {
				msg = MessageConstants.COMPANY_NOT_EXIST;
				logger.error(msg);
			}
		}
		return msg;
	}
}
