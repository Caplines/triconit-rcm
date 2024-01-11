package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmClaimSection;
import com.tricon.rcm.db.entity.RcmClientSectionMapping;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.dto.ClaimSectionMappingDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto.SectionData;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RcmClaimDefaultSectionRepo;
import com.tricon.rcm.jpa.repository.RcmClaimSectionRepo;
import com.tricon.rcm.jpa.repository.RcmClientSectionMappingRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
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

	@Transactional(rollbackOn = Exception.class)
	public String manageClientSectionDetails(List<ClaimSectionMappingDto> listOfClaimSections) throws Exception {
		String msg = null;
		RcmClientSectionMapping sectionMapping = null;
		List<RcmCompany> listOfClients = rcmCompanyRepo.findAll();
		List<RcmTeam> listOfTeams = teamRepo.findAll();
		for (ClaimSectionMappingDto dto : listOfClaimSections) {
			RcmCompany client = listOfClients.stream().filter(x -> x.getUuid().equals(dto.getClientUuid())).findAny()
					.orElse(null);
			if (client != null) {
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
							RcmClientSectionMapping existingTeamWithSection = clientSectionMappingRepo
									.findByCompanyUuidAndTeamIdIdAndSectionId(client.getUuid(), team.getId(),
											sections.getSectionId());
							if (existingTeamWithSection != null) {
								existingTeamWithSection.setEditAccess(sections.getEditAccess());
								existingTeamWithSection.setViewAccess(
										sections.getEditAccess() == true ? true : sections.getViewAccess());
								existingTeamWithSection = clientSectionMappingRepo.save(existingTeamWithSection);
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
	
	
	public List<ClaimSectionMappingDto> getClientsWithAllSectionsAndTeam() throws Exception {
		List<ClaimSectionMappingDto> response = new ArrayList<>();
		List<RcmClaimSection> claimSections = claimSectionRepo.findAllWithSectionCategory().stream()
				.filter(x -> x.isActive() == true).collect(Collectors.toList());
		List<ClientCustomDto> clients = rcmCompanyRepo.findAllClients();
		List<RcmTeamDto> teamData = RcmTeamEnum.getAllTeamsIsRoleVisible();
		clients.forEach(client -> {
			ClaimSectionMappingDto responseDto = new ClaimSectionMappingDto();
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
}
