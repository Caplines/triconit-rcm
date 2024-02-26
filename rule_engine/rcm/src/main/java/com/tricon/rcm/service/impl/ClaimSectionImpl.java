package com.tricon.rcm.service.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.ClaimUserSectionMapping;
import com.tricon.rcm.db.entity.EOBSectionInformation;
import com.tricon.rcm.db.entity.PaymentInformationSection;
import com.tricon.rcm.db.entity.RcmAppealLevelInformation;
import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimDetail;
import com.tricon.rcm.db.entity.RcmClaimLevelSection;
import com.tricon.rcm.db.entity.RcmClaimSection;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmClientSectionMapping;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmServiceLevelInformation;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.AppealInformationDto;
import com.tricon.rcm.dto.ClaimLevelInformationDto;
import com.tricon.rcm.dto.ClientSectionMappingDto;
import com.tricon.rcm.dto.EOBDto;
import com.tricon.rcm.dto.EobSectionEditDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PaymentInformationSectionDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto.SectionData;
import com.tricon.rcm.dto.ServiceLevelInformationDto;
import com.tricon.rcm.dto.ServiceLevelNotes;
import com.tricon.rcm.dto.ServiceLevelRequestBodyDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.EOBSectionRepo;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmAppealInfoRepo;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimDefaultSectionRepo;
import com.tricon.rcm.jpa.repository.RcmClaimDetailRepo;
import com.tricon.rcm.jpa.repository.RcmClaimLevelInfoRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimSectionRepo;
import com.tricon.rcm.jpa.repository.RcmClaimUserSectionMappingRepo;
import com.tricon.rcm.jpa.repository.RcmClientSectionMappingRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmInsurancePaymentSectionRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserCompanyRepo;
import com.tricon.rcm.jpa.repository.ServiceLevelInformationRepo;
import com.tricon.rcm.util.ClaimUtil;
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
	
	@Autowired
	RcmClaimLevelInfoRepo claimLevelInfoRepo;
	
	@Autowired
	RcmClaimRepository claimRepo;
	
	@Autowired
	RcmAppealInfoRepo appealInfoRepo;
	
	@Autowired
	EOBSectionRepo eobRepo;
	
	@Autowired
	RcmTeamRepo rcmTeamRepo;
	
	@Autowired
	RcmClaimAssignmentRepo rcmClaimAssignmentRepo;
	
	@Autowired
	RcmClaimRepository rcmClaimRepository;
	
	@Autowired
	RcmUserCompanyRepo rcmUserCompanyRepo;
	
	@Autowired
	RcmInsurancePaymentSectionRepo paymentSectionRepo;
	@Autowired
	ServiceLevelInformationRepo serviceLevelRepo;
	
	@Autowired
	RcmClaimDetailRepo claimDetailRepo;
	
	@Autowired
	RcmOfficeRepository officeRepo;
	@Value("${eoblink.folder}")
	private String eobLinkFolder;
	@Value("${rcm.serverdomain}")
	private String serverDomainLink;
	

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
					sectionData.setSectionDisplayName(section.getDisplayName());
					sectionData.setSectionName(section.getSectionName());
					sectionData.setSectionCategory(section.getSectionCategory());
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
						sectionData.setSectionDisplayName(section.getDisplayName());
						sectionData.setSectionName(section.getSectionName());
						sectionData.setSectionCategory(section.getSectionCategory());
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

	
	
	@Transactional(rollbackOn = Exception.class)
	public boolean saveClaimLevelInformation(ClaimLevelInformationDto claimLvelInfoDto, RcmClaims claim,
			RcmUser createdBy, RcmTeam team, boolean isFinalSubmit) throws Exception {
		RcmClaimLevelSection claimLevelSection = null;
		if (claim != null) {
			claimLevelSection = new RcmClaimLevelSection();
			claimLevelSection.setClaim(claim);
			claimLevelSection.setClaimId(claimLvelInfoDto.getClaimId());
			claimLevelSection.setNetwork(claimLvelInfoDto.getNetwork());
			claimLevelSection.setClaimPassFirstGo(claimLvelInfoDto.getClaimPassFirstGo());
			claimLevelSection.setClaimStatusEs(claimLvelInfoDto.getClaimStatusEs());
			claimLevelSection.setClaimStatusRcm(claimLvelInfoDto.getClaimStatusRcm());
			claimLevelSection.setInitialDenial(claimLvelInfoDto.getInitialDenial());
			claimLevelSection.setCreatedBy(createdBy);
			claimLevelSection.setFinalSubmit(isFinalSubmit);
			claimLevelSection.setTeamId(team);
			claimLevelSection
					.setClaimProcessingDate(Constants.SDF_MYSL_DATE.parse(claimLvelInfoDto.getClaimProcessingDate()));
			claimLevelSection = claimLevelInfoRepo.save(claimLevelSection);
			return claimLevelSection != null ? true : false;
		}
		return false;
	}

	public ClaimLevelInformationDto fetchClaimLevelInfo(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		ClaimLevelInformationDto responseDto = null;
		RcmClaimLevelSection claimLevelSections = null;
		if (showWithTeam) {
			claimLevelSections = claimLevelInfoRepo
					.findFirstByClaimClaimUuidAndCreatedByUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid,
							partialHeader.getJwtUser().getUuid(), partialHeader.getTeamId());
		} else {
			claimLevelSections = claimLevelInfoRepo.findFirstByClaimClaimUuidAndCreatedByUuidOrderByCreatedDateDesc(
					claimUuid, partialHeader.getJwtUser().getUuid());
		}
		if (claimLevelSections != null) {
			responseDto = new ClaimLevelInformationDto();
			responseDto.setClaimProcessingDate(
					Constants.SDF_MYSL_DATE.format(claimLevelSections.getClaimProcessingDate()));
			BeanUtils.copyProperties(claimLevelSections, responseDto);
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean saveAppealInformation(AppealInformationDto appealInfoDto,RcmClaims claim,RcmUser createdBy,RcmTeam team,boolean isFinalSubmit)
			throws Exception {
		RcmAppealLevelInformation appealInformation = null;
		if (claim != null) {
			appealInformation = new RcmAppealLevelInformation();
			appealInformation.setAiToolUsed(appealInfoDto.getAiToolUsed());
			appealInformation.setAppealDocument(appealInfoDto.getAppealDocument());
			appealInformation.setClaim(claim);
			appealInformation.setRemarks(appealInfoDto.getRemarks());
			appealInformation.setModeOfAppeal(appealInfoDto.getModeOfAppeal());
			appealInformation.setCreatedBy(createdBy);
			appealInformation.setFinalSubmit(isFinalSubmit);
			appealInformation.setTeamId(team);
			appealInformation = appealInfoRepo.save(appealInformation);
			return appealInformation != null ? true : false;
		}
		return false;
	}

	public AppealInformationDto fetchAppealLevelInfo(PartialHeader partialHeader, String claimUuid, boolean showWithTeam)
			throws Exception {
		AppealInformationDto responseDto = null;
		RcmAppealLevelInformation appealLevelInformation = null;
		if (showWithTeam) {
			appealLevelInformation = appealInfoRepo
					.findFirstByClaimClaimUuidAndCreatedByUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid,
							partialHeader.getJwtUser().getUuid(), partialHeader.getTeamId());
		} else {
			appealLevelInformation = appealInfoRepo.findFirstByClaimClaimUuidAndCreatedByUuidOrderByCreatedDateDesc(
					claimUuid, partialHeader.getJwtUser().getUuid());
		}
		if (appealLevelInformation != null) {
			responseDto = new AppealInformationDto();
			BeanUtils.copyProperties(appealLevelInformation, responseDto);
			return responseDto;
		}
		return null;
	}

	
	@Transactional(rollbackOn = Exception.class)
	public Object saveEOBSection(EOBDto eobInfoModel, RcmClaims claim, RcmUser createdBy, RcmTeam team,
			boolean finalSubmit) throws Exception {
		EOBSectionInformation eobInformation = null;
		if (claim != null) {
			eobInformation = new EOBSectionInformation();
			eobInformation.setAttachByTeam(team);
			eobInformation.setEobLink(eobInfoModel.getEobLink());
			eobInformation.setClaim(claim);
			eobInformation.setCreatedBy(createdBy);
			eobInformation.setFinalSubmit(finalSubmit);
			// set eob file path
			String fileName = claim.getClaimUuid() + new Date().getTime() + "." + eobInfoModel.getExtension();
			FileUtils.copyURLToFile(new URL(eobInfoModel.getEobLink()), new File(eobLinkFolder + File.separator+ fileName), 60000,
					60000);
			eobInformation.setEobFilePath(fileName);
			eobInformation = eobRepo.save(eobInformation);
			eobInfoModel.setEobPathLink(serverDomainLink+"/api/vieweoblink/"+eobInformation.getEobFilePath());
			//return eobInformation != null ? true : false;
		}
		return eobInfoModel;
	}

	public List<EOBDto> fetchEOBInformation(PartialHeader partialHeader, String claimUuid, boolean showWithTeam)
			throws Exception {
		EOBDto responseDto = null;
		List<EOBSectionInformation> eobSections = null;
		List<EOBDto> responseData = new ArrayList<>();
		if (showWithTeam) {
			eobSections = eobRepo.findByClaimClaimUuidAndCreatedByUuidAndAttachByTeamIdAndMarkAsDeletedFalseOrderByCreatedDateDesc(claimUuid,
					partialHeader.getJwtUser().getUuid(), partialHeader.getTeamId());
		} else {
			eobSections = eobRepo.findByClaimClaimUuidAndCreatedByUuidAndMarkAsDeletedFalseOrderByCreatedDateDesc(claimUuid,
					partialHeader.getJwtUser().getUuid());
		}
		if (eobSections != null) {
			for (EOBSectionInformation data : eobSections) {
				responseDto = new EOBDto();
				responseDto.setEobPathLink(data.getEobFilePath());
				responseDto.setAttachBy(userRepo.findByUuid(data.getCreatedBy().getUuid()).getFirstName());
				responseDto.setAttachByTeam(rcmTeamRepo.findById(data.getAttachByTeam().getId()).getDescription());
				responseDto.setDate(Constants.SDF_MYSL_DATE.format((data.getCreatedDate())));
				BeanUtils.copyProperties(eobSections, responseDto);
				responseData.add(responseDto);
			}
		}
		return responseData;
	}

	@Transactional(rollbackOn = Exception.class)
	public String removeEobSectionDetails(EobSectionEditDto ids, PartialHeader partialHeader) throws Exception {
		boolean validateClaimRight = checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),
				partialHeader.getCompany().getUuid());

		if (!validateClaimRight) {
			return "wrong claim";
		}
		RcmClaims claim = rcmClaimRepository.findByClaimUuid(ids.getClaimUuid());

		RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
				partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		if (assign == null) {
			// Not assigned to user
			return "claim not assigned to this user";
		}

		List<EOBSectionInformation> listOfEob = eobRepo
				.findByClaimClaimUuidAndAttachByTeamIdAndMarkAsDeletedFalseAndCreatedByUuidAndIdIn(ids.getClaimUuid(),
						partialHeader.getTeamId(), partialHeader.getJwtUser().getUuid(), ids.getIds());
		if (!listOfEob.isEmpty()) {
			listOfEob.forEach(x -> {
				x.setMarkAsDeleted(true);
				x.setUpdatedBy(userRepo.findByUuid(partialHeader.getJwtUser().getUuid()));
			});
			eobRepo.saveAll(listOfEob);
			return MessageConstants.RECORDS_UPDATE;
		}
		return MessageConstants.RECORD_NOT_EXIST;
	}

	private boolean checkifCompanyIdMatchesList(String userid, String userCompanyId) {
		List<String> companies = rcmUserCompanyRepo.findAssociatedCompanyIdByUserUuid(userid);
		return ClaimUtil.checkifCompanyIdMatchesList(userCompanyId, companies);
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean saveInsurancePaymentInformationSection(PaymentInformationSectionDto paymentInformationInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit) throws Exception {

		// if paid amount is 0 then no need to save data in db
		if (paymentInformationInfoModel.getPaidAmount() == 0.0) {
			return true;
		} else {
			PaymentInformationSection paymentInsuranceInformation = null;
			if (claim != null) {
				paymentInsuranceInformation = new PaymentInformationSection();
				paymentInsuranceInformation
						.setAmountPostedInEs(paymentInformationInfoModel.getAmountPostedInEs());
				paymentInsuranceInformation.setAmountReceivedInBank(
						paymentInformationInfoModel.getAmountReceivedInBank());
				paymentInsuranceInformation.setCheckCashDate(
						Constants.SDF_MYSL_DATE.parse(paymentInformationInfoModel.getCheckCashDate()));
				if (paymentInformationInfoModel.getPaymentMode().equals(Constants.PAYMENT_MODE_CHECK)) {
					paymentInsuranceInformation.setCheckDeliverTo(paymentInformationInfoModel.getCheckDeliverTo());
				}
				paymentInsuranceInformation.setCheckNumber(paymentInformationInfoModel.getCheckNumber());
				paymentInsuranceInformation.setPaymentIssueTo(paymentInformationInfoModel.getPaymentIssueTo());
				paymentInsuranceInformation.setPaymentMode(paymentInformationInfoModel.getPaymentMode());
				paymentInsuranceInformation.setClaim(claim);
				paymentInsuranceInformation.setAmountDateReceivedInBank(
						Constants.SDF_MYSL_DATE.parse(paymentInformationInfoModel.getAmountDateReceivedInBank()));
				paymentInsuranceInformation.setCreatedBy(createdBy);
				paymentInsuranceInformation.setFinalSubmit(finalSubmit);
				paymentInsuranceInformation.setTeam(team);
				paymentInsuranceInformation = paymentSectionRepo.save(paymentInsuranceInformation);
				return paymentInsuranceInformation != null ? true : false;
			}
		}
		return false;
	}

	public PaymentInformationSectionDto fetchInsurancePaymentInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		PaymentInformationSectionDto responseDto = null;
		PaymentInformationSection paymentInsuranceInformation = null;
		if (showWithTeam) {
			paymentInsuranceInformation = paymentSectionRepo
					.findFirstByClaimClaimUuidAndCreatedByUuidAndTeamIdOrderByCreatedDateDesc(claimUuid,
							partialHeader.getJwtUser().getUuid(), partialHeader.getTeamId());
		} else {
			paymentInsuranceInformation = paymentSectionRepo
					.findFirstByClaimClaimUuidAndCreatedByUuidOrderByCreatedDateDesc(claimUuid,
							partialHeader.getJwtUser().getUuid());
		}
		if (paymentInsuranceInformation != null) {
			responseDto = new PaymentInformationSectionDto();
			responseDto.setAmountReceivedInBank(paymentInsuranceInformation.getAmountReceivedInBank());
			responseDto.setAmountPostedInEs(paymentInsuranceInformation.getAmountPostedInEs());
			responseDto
					.setCheckCashDate(Constants.SDF_MYSL_DATE.format((paymentInsuranceInformation.getCheckCashDate())));
			responseDto.setAmountDateReceivedInBank(
					Constants.SDF_MYSL_DATE.format((paymentInsuranceInformation.getAmountDateReceivedInBank())));
			BeanUtils.copyProperties(paymentInsuranceInformation, responseDto);
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean saveServiceLevelInformationSection(ServiceLevelInformationDto serviceLevelInformationInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit, String clientName) {
		RcmServiceLevelInformation serviceLevelData = null;
		for (ServiceLevelRequestBodyDto data : serviceLevelInformationInfoModel.getServiceLevelBody()) {
			serviceLevelData = new RcmServiceLevelInformation();
			serviceLevelData.setClaim(claim);
			serviceLevelData.setCreatedBy(createdBy);
			serviceLevelData.setFinalSubmit(finalSubmit);
			serviceLevelData.setTeam(team);
			BeanUtils.copyProperties(data, serviceLevelData);
			serviceLevelData = serviceLevelRepo.save(serviceLevelData);
		}
		return serviceLevelData != null ? true : false;
	}

	public List<ServiceLevelRequestBodyDto> fetchServiceLevelInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		ServiceLevelRequestBodyDto responseData = null;
		RcmServiceLevelInformation serviceLevelDto = null;
		List<ServiceLevelRequestBodyDto> data = new ArrayList<>();
		List<RcmServiceLevelInformation> listOfServiceLevelDto = new ArrayList<>();
		boolean validateClaimRight = checkifCompanyIdMatchesList(partialHeader.getJwtUser().getUuid(),
				partialHeader.getCompany().getUuid());
		if (!validateClaimRight) {
			logger.error("claim not valid");
			return data;
		}
		List<RcmServiceLevelInformation> serviceLevelData = serviceLevelRepo.findServiceLevelCodesByClaimUuid(claimUuid);
		if (serviceLevelData.isEmpty()) {
			// fetch data from rcm claim_detail table and insert into
			// rcm_service_level_information table
			List<RcmClaimDetail> claimDetailData = claimDetailRepo.findByClaimClaimUuid(claimUuid);
			for (RcmClaimDetail list : claimDetailData) {
				serviceLevelDto = new RcmServiceLevelInformation();
				BeanUtils.copyProperties(list, serviceLevelDto);
				listOfServiceLevelDto.add(serviceLevelDto);
			}
			listOfServiceLevelDto = serviceLevelRepo.saveAll(listOfServiceLevelDto);
			for (RcmServiceLevelInformation list : listOfServiceLevelDto) {
				responseData = new ServiceLevelRequestBodyDto();
				BeanUtils.copyProperties(list, responseData);
				data.add(responseData);
			}

		} else {
			ServiceLevelNotes notes = null;
			List<ServiceLevelNotes> notesList = new ArrayList<>();
			List<RcmServiceLevelInformation> serviceLevelNotes = serviceLevelRepo
					.findServiceLevelNotesByClaimUuid(claimUuid);
			for (RcmServiceLevelInformation serviceCodes : serviceLevelData) {
				notes = new ServiceLevelNotes();
				for (RcmServiceLevelInformation serviceNotes : serviceLevelNotes) {
					if (serviceCodes.getId() == serviceNotes.getId()
							&& serviceCodes.getServiceCode().equals(serviceNotes.getServiceCode())) {
						continue;
					} else if (serviceCodes.getId() != serviceNotes.getId()
							&& serviceCodes.getServiceCode().equals(serviceNotes.getServiceCode())) {
						notes.setNotes(serviceNotes.getNotes());
						notes.setServiceCode(serviceNotes.getServiceCode());
						notesList.add(notes);
					}
				}
			}
			for (RcmServiceLevelInformation list : serviceLevelData) {
				responseData = new ServiceLevelRequestBodyDto();
				if (notesList.stream().anyMatch(x -> x.getServiceCode().equals(list.getServiceCode()))) {
					responseData.setServiceCodeNotes(notesList);
				}
				BeanUtils.copyProperties(list, responseData);
				data.add(responseData);
			}
		}
		return data;
	}
}
