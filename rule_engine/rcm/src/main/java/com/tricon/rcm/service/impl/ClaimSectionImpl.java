package com.tricon.rcm.service.impl;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
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
import com.tricon.rcm.db.entity.CurrentClaimStatusAndNextAction;
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
import com.tricon.rcm.db.entity.RcmInsuranceFollowUpSection;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmPatientCommunicationSection;
import com.tricon.rcm.db.entity.RcmPatientPayment;
import com.tricon.rcm.db.entity.RcmPatientStatementSection;
import com.tricon.rcm.db.entity.RcmServiceLevelInformation;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.AppealInformationDto;
import com.tricon.rcm.dto.ClaimLevelInformationDto;
import com.tricon.rcm.dto.ClientSectionMappingDto;
import com.tricon.rcm.dto.CurrentStatusAndNextActionDto;
import com.tricon.rcm.dto.EOBDto;
import com.tricon.rcm.dto.EobSectionEditDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PatientPaymentSectionDto;
import com.tricon.rcm.dto.PaymentInformationSectionDto;
import com.tricon.rcm.dto.RcmFollowUpInsuranceDto;
import com.tricon.rcm.dto.RcmPatientCommunicationDto;
import com.tricon.rcm.dto.RcmPatientStatementDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto.SectionData;
import com.tricon.rcm.dto.ServiceLevelInformationDto;
import com.tricon.rcm.dto.ServiceLevelNotes;
import com.tricon.rcm.dto.ServiceLevelRequestBodyDto;
import com.tricon.rcm.dto.ServiceLevelTotalAmountDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.dto.customquery.RcmServiceNotesDto;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.EOBSectionRepo;
import com.tricon.rcm.jpa.repository.FollowUpInsuranceRepo;
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
import com.tricon.rcm.jpa.repository.RcmCurrentClaimStatusRepo;
import com.tricon.rcm.jpa.repository.RcmInsurancePaymentSectionRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmPatientCommunicationRepo;
import com.tricon.rcm.jpa.repository.RcmPatientPaymentSectionRepo;
import com.tricon.rcm.jpa.repository.RcmPatientStatementRepo;
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
	RcmPatientPaymentSectionRepo patientPaymentRepo;
	
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
	
	@Autowired
	FollowUpInsuranceRepo followUpRepo;
	
	@Value("${rcm.serverdomain}")
	private String serverDomainLink;
	
	@Autowired
	RcmPatientStatementRepo patientStatementRepo;
	
	@Autowired
	RcmCurrentClaimStatusRepo currentStatusAndNextActionRepo;
	
	@Autowired
	RcmPatientCommunicationRepo patientCommunicationRepo;
	

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
	public Boolean saveClaimLevelInformation(ClaimLevelInformationDto claimLvelInfoDto, RcmClaims claim,
			RcmUser createdBy, RcmTeam team, boolean isFinalSubmit) throws Exception {
		RcmClaimLevelSection claimLevelSection = null;
		if (claim != null) {
			claimLevelSection = new RcmClaimLevelSection();
			claimLevelSection.setClaim(claim);
			claimLevelSection.setClaimId(claimLvelInfoDto.getClaimId());
			claimLevelSection.setNetwork(claimLvelInfoDto.getNetwork());
			claimLevelSection.setClaimPassFirstGo(claimLvelInfoDto.getClaimPassFirstGo());
			claimLevelSection.setNoOfEstPayment(claimLvelInfoDto.getNoOfEstPayment());
			claimLevelSection.setNoOfPaymentReceived(claimLvelInfoDto.getNoOfPaymentReceived());
			claimLevelSection.setPaymentFrequency(claimLvelInfoDto.getPaymentFrequency());
			claimLevelSection.setInitialDenial(claimLvelInfoDto.getInitialDenial());
			claimLevelSection.setCreatedBy(createdBy);
			claimLevelSection.setFinalSubmit(isFinalSubmit);
			claimLevelSection.setTeamId(team);
			claimLevelSection
					.setClaimProcessingDate(
							!StringUtils.isNoneBlank(claimLvelInfoDto.getClaimProcessingDate())?null:
							Constants.SDF_MYSL_DATE.parse(claimLvelInfoDto.getClaimProcessingDate()));
			claimLevelSection = claimLevelInfoRepo.save(claimLevelSection);
			return claimLevelSection != null ? true : null;
		}
		return null;
	}

	public ClaimLevelInformationDto fetchClaimLevelInfo(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		ClaimLevelInformationDto responseDto = null;
		RcmClaimLevelSection claimLevelSections = null;
		if (showWithTeam) {
			claimLevelSections = claimLevelInfoRepo
					.findFirstByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid, partialHeader.getTeamId());
		} else {
			claimLevelSections = claimLevelInfoRepo.findFirstByClaimClaimUuidOrderByCreatedDateDesc(
					claimUuid);
		}
		if (claimLevelSections != null) {
			responseDto = new ClaimLevelInformationDto();
			responseDto.setClaimProcessingDate(claimLevelSections.getClaimProcessingDate()==null?"":
					Constants.SDF_MYSL_DATE.format(claimLevelSections.getClaimProcessingDate()));
			BeanUtils.copyProperties(claimLevelSections, responseDto);
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Boolean saveAppealInformation(AppealInformationDto appealInfoDto,RcmClaims claim,RcmUser createdBy,RcmTeam team,boolean isFinalSubmit)
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
			return appealInformation != null ? true : null;
		}
		return false;
	}

	public AppealInformationDto fetchAppealLevelInfo(PartialHeader partialHeader, String claimUuid, boolean showWithTeam)
			throws Exception {
		AppealInformationDto responseDto = null;
		RcmAppealLevelInformation appealLevelInformation = null;
		if (showWithTeam) {
			appealLevelInformation = appealInfoRepo.findFirstByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid,partialHeader.getTeamId());
		} else {
			appealLevelInformation = appealInfoRepo.findFirstByClaimClaimUuidOrderByCreatedDateDesc(
					claimUuid);
		}
		if (appealLevelInformation != null) {
			responseDto = new AppealInformationDto();
			BeanUtils.copyProperties(appealLevelInformation, responseDto);
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Boolean savePatientPaymentSection(PatientPaymentSectionDto patientPaymentInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit) throws Exception {
		RcmPatientPayment patientPaymentInformation = null;
		if (claim != null) {
			patientPaymentInformation = new RcmPatientPayment();
			patientPaymentInformation.setAmountCollectedClaims(patientPaymentInfoModel.getAmountCollectedClaims());
			patientPaymentInformation.setClaim(claim);
			patientPaymentInformation
					.setDateOfPayment(
							!StringUtils.isNoneBlank(patientPaymentInfoModel.getDateOfPayment())?null:
							Constants.SDF_MYSL_DATE.parse(patientPaymentInfoModel.getDateOfPayment()));
			patientPaymentInformation.setDueBalanceInPMS(patientPaymentInfoModel.getDueBalanceInPMS());
			patientPaymentInformation.setModeOfPayment(patientPaymentInfoModel.getModeOfPayment());
			patientPaymentInformation.setPostedInPMS(patientPaymentInfoModel.getPostedInPMS());
			patientPaymentInformation.setCreatedBy(createdBy);
			patientPaymentInformation.setFinalSubmit(finalSubmit);
			patientPaymentInformation.setTeamId(team);
			patientPaymentInformation = patientPaymentRepo.save(patientPaymentInformation);
			return patientPaymentInformation != null ? true : null;
		}
		return null;
	}
	
	
	public PatientPaymentSectionDto fetchPatientPaymentInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		PatientPaymentSectionDto responseDto = null;
		RcmPatientPayment patientPaymentlSections = null;
		if (showWithTeam) {
			patientPaymentlSections = patientPaymentRepo
					.findFirstByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid,partialHeader.getTeamId());
		} else {
			patientPaymentlSections = patientPaymentRepo
					.findFirstByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		}
		if (patientPaymentlSections != null) {
			responseDto = new PatientPaymentSectionDto();
			responseDto.setDateOfPayment(patientPaymentlSections.getDateOfPayment()==null?"":Constants.SDF_MYSL_DATE.format((patientPaymentlSections.getDateOfPayment())));
			BeanUtils.copyProperties(patientPaymentlSections, responseDto);
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Object saveEOBSection(EOBDto eobInfoModel, RcmClaims claim, RcmUser createdBy, RcmTeam team,
			boolean finalSubmit) throws Exception {
		EOBSectionInformation eobInformation = null;
		if (!StringUtils.isNoneBlank(eobInfoModel.getEobLink()))
			return null;
		if (claim != null) {
			eobInformation = new EOBSectionInformation();
			eobInformation.setAttachByTeam(team);
			eobInformation.setEobLink(eobInfoModel.getEobLink());
			eobInformation.setClaim(claim);
			eobInformation.setCreatedBy(createdBy);
			eobInformation.setFinalSubmit(finalSubmit);
			try {
				// set eob file path
				String fileName = claim.getClaimUuid() + new Date().getTime() + "." + eobInfoModel.getExtension();
				    URL url = new URL(eobInfoModel.getEobLink());
					FileUtils.copyURLToFile(url, new File(eobLinkFolder + File.separator + fileName), 60000, 60000);
					eobInformation.setEobFilePath(fileName);
					eobInformation = eobRepo.save(eobInformation);
					eobInfoModel
							.setEobPathLink(serverDomainLink + "/api/vieweoblink/" + eobInformation.getEobFilePath());
					eobInfoModel.setAttachByTeam(team.getName());
					eobInfoModel.setAttachBy(createdBy.getFirstName());
					eobInfoModel.setDate(Constants.SDF_MYSL_DATE.format((eobInformation.getCreatedDate())));
				
			} catch (Exception e) {
				logger.error("Invalid File Format");
				eobInfoModel.setEobPathLink("Invalid Format");
				return null;
			}

		}
		return eobInfoModel;
	}

	public List<EOBDto> fetchEOBInformation(PartialHeader partialHeader, String claimUuid)
			throws Exception {
		EOBDto responseDto = null;
		List<EOBSectionInformation> eobSections = new ArrayList<>();
		List<EOBDto> responseData = new ArrayList<>();
		eobSections = eobRepo.findByClaimClaimUuidAndMarkAsDeletedFalseOrderByCreatedDateDesc(claimUuid);
		if (!eobSections.isEmpty()) {
			for (EOBSectionInformation data : eobSections) {
				RcmUser attachBy = userRepo.findByUuid(data.getCreatedBy().getUuid());
				RcmTeam team = rcmTeamRepo.findById(data.getAttachByTeam().getId());
				responseDto = new EOBDto();
				responseDto.setEobPathLink(serverDomainLink + "/api/vieweoblink/" + data.getEobFilePath());
				responseDto.setAttachBy(attachBy.getFirstName());
				responseDto.setAttachByLastName(attachBy.getFirstName());
				responseDto.setAttachByTeam(team.getDescription());
				responseDto.setDate(Constants.SDF_MYSL_DATE.format((data.getCreatedDate())));
				BeanUtils.copyProperties(data, responseDto);
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
	public Boolean saveInsurancePaymentInformationSection(PaymentInformationSectionDto paymentInformationInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit) throws Exception {

		// if paid amount is 0 then no need to save data in db
		if (paymentInformationInfoModel.getPaidAmount() == 0.0) {
			logger.error("paid amount is 0.so data will not save");
			return false;
		} else {
			PaymentInformationSection paymentInsuranceInformation = null;
			if (claim != null) {
				paymentInsuranceInformation = new PaymentInformationSection();
				paymentInsuranceInformation
						.setAmountPostedInEs(paymentInformationInfoModel.getAmountPostedInEs());
				paymentInsuranceInformation.setAmountReceivedInBank(
						paymentInformationInfoModel.getAmountReceivedInBank());
				paymentInsuranceInformation.setCheckCashDate(
						!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckCashDate())?null:
						Constants.SDF_MYSL_DATE.parse(paymentInformationInfoModel.getCheckCashDate()));
				if (paymentInformationInfoModel.getPaymentMode().equals(Constants.PAYMENT_MODE_CHECK)) {
					paymentInsuranceInformation.setCheckDeliverTo(paymentInformationInfoModel.getCheckDeliverTo());
				}
				paymentInsuranceInformation.setCheckNumber(paymentInformationInfoModel.getCheckNumber());
				paymentInsuranceInformation.setPaymentIssueTo(paymentInformationInfoModel.getPaymentIssueTo());
				paymentInsuranceInformation.setPaymentMode(paymentInformationInfoModel.getPaymentMode());
				paymentInsuranceInformation.setClaim(claim);
				paymentInsuranceInformation.setAmountDateReceivedInBank(
						!StringUtils.isNoneBlank(paymentInformationInfoModel.getAmountDateReceivedInBank())?null:
						Constants.SDF_MYSL_DATE.parse(paymentInformationInfoModel.getAmountDateReceivedInBank()));
				paymentInsuranceInformation.setCreatedBy(createdBy);
				paymentInsuranceInformation.setFinalSubmit(finalSubmit);
				paymentInsuranceInformation.setTeam(team);
				paymentInsuranceInformation = paymentSectionRepo.save(paymentInsuranceInformation);
				return paymentInsuranceInformation != null ? true : null;
			}
		}
		return null;
	}

	public PaymentInformationSectionDto fetchInsurancePaymentInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		PaymentInformationSectionDto responseDto = null;
		PaymentInformationSection paymentInsuranceInformation = null;
		if (showWithTeam) {
			paymentInsuranceInformation = paymentSectionRepo
					.findFirstByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(claimUuid, partialHeader.getTeamId());
		} else {
			paymentInsuranceInformation = paymentSectionRepo
					.findFirstByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		}
		if (paymentInsuranceInformation != null) {
			responseDto = new PaymentInformationSectionDto();
			responseDto
					.setCheckCashDate(paymentInsuranceInformation.getCheckCashDate()==null?"":Constants.SDF_MYSL_DATE.format((paymentInsuranceInformation.getCheckCashDate())));
			responseDto.setAmountDateReceivedInBank(
					paymentInsuranceInformation.getAmountDateReceivedInBank()==null?"":
					Constants.SDF_MYSL_DATE.format((paymentInsuranceInformation.getAmountDateReceivedInBank())));
			BeanUtils.copyProperties(paymentInsuranceInformation, responseDto);
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Boolean saveServiceLevelInformationSection(ServiceLevelTotalAmountDto serviceLevelInformationInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit, String clientName) {
		RcmServiceLevelInformation serviceLevelData = null;
		int maxRun = serviceLevelRepo.getMaxRunFromServiceLevel(claim.getClaimUuid());
		for (ServiceLevelRequestBodyDto data : serviceLevelInformationInfoModel.getServiceLevelBody()) {
			serviceLevelData = new RcmServiceLevelInformation();
			serviceLevelData.setClaim(claim);
			serviceLevelData.setCreatedBy(createdBy);
			serviceLevelData.setFinalSubmit(finalSubmit);
			serviceLevelData.setTeam(team);
			serviceLevelData.setActive(true);
			serviceLevelData.setGroupRun(maxRun + 1);
			BeanUtils.copyProperties(data, serviceLevelData);
			serviceLevelRepo.save(serviceLevelData);
		}
		// now we update totalPaid amount,adjustment amount and btp amount in rcm claim
		// table
		if (finalSubmit) {
			RcmClaims claims = claimRepo.findByClaimUuid(claim.getClaimUuid());
			claims.setAdjustment((float) serviceLevelInformationInfoModel.getTotalAdjustmentAmount());
			claims.setBtp((float) serviceLevelInformationInfoModel.getTotalBtpAmount());
			claims.setPaidAmount((float) serviceLevelInformationInfoModel.getTotalPaidAmount());
			claimRepo.save(claims);
       }
		return serviceLevelData != null ? true : null;
	}

	public List<ServiceLevelRequestBodyDto> fetchServiceLevelInformation(PartialHeader partialHeader, String claimUuid) throws Exception {
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

		int maxRun = serviceLevelRepo.getMaxRunFromServiceLevel(claimUuid);
		List<RcmServiceLevelInformation> serviceLevelData = serviceLevelRepo
				.findServiceLevelCodesByClaimUuid(claimUuid,maxRun);
		if (serviceLevelData.isEmpty()) {
			// fetch data from rcm claim_detail table and insert into
			// rcm_service_level_information table
			List<RcmClaimDetail> claimDetailData = claimDetailRepo.findByClaimClaimUuidAndActiveTrue(claimUuid);
			if(claimDetailData.isEmpty()) {
				logger.error("No claim found in claim detail table");
			}
			RcmTeam team=rcmTeamRepo.findById(partialHeader.getTeamId());
			for (RcmClaimDetail list : claimDetailData) {
				serviceLevelDto = new RcmServiceLevelInformation();
				BeanUtils.copyProperties(list, serviceLevelDto);
				serviceLevelDto.setGroupRun(1);
				serviceLevelDto.setFlag(true);		
				serviceLevelDto.setTeam(team);
				listOfServiceLevelDto.add(serviceLevelDto);
			}
			listOfServiceLevelDto = serviceLevelRepo.saveAll(listOfServiceLevelDto);
			for (RcmServiceLevelInformation list : listOfServiceLevelDto) {
				responseData = new ServiceLevelRequestBodyDto();
				BeanUtils.copyProperties(list, responseData);
				data.add(responseData);
			}

		} else {
			List<RcmServiceNotesDto> oldServiceNotes = serviceLevelRepo.findOldServiceLevelCodesByClaimUuid(claimUuid,
					maxRun);
			ServiceLevelNotes notes = null;
			List<ServiceLevelNotes> oldNotesList = new ArrayList<>();
			List<ServiceLevelNotes> newNotesList = new ArrayList<>();
			for (RcmServiceLevelInformation serviceCodes : serviceLevelData) {
				notes = new ServiceLevelNotes();
				for (RcmServiceNotesDto serviceNotes : oldServiceNotes) {
					notes = new ServiceLevelNotes();
					if (serviceCodes.getServiceCode().equals(serviceNotes.getServiceCode())) {
						notes.setNotes(serviceNotes.getNotes());
						notes.setServiceCode(serviceNotes.getServiceCode());
						notes.setCreatedBy(serviceNotes.getCreatedBy());
						notes.setCreatedDate(serviceNotes.getDate()==null?"":Constants.SDF_MYSL_DATE.format(serviceNotes.getDate()));
						notes.setTeamName(serviceNotes.getTeamName());
						oldNotesList.add(notes);
					}
				}
			}
			for (RcmServiceLevelInformation list : serviceLevelData) {
				responseData = new ServiceLevelRequestBodyDto();
				newNotesList = new ArrayList<>();
				for (ServiceLevelNotes notesData : oldNotesList) {
					if (notesData.getServiceCode().equals(list.getServiceCode())) {
						newNotesList.add(notesData);
						responseData.setServiceCodeNotes(newNotesList);
					}		
				}
				BeanUtils.copyProperties(list, responseData);
				data.add(responseData);
			}
		}
		return data;
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Object saveFollowUpInsuranceSection(RcmFollowUpInsuranceDto rcmFollowUpInsuranceInfoModel, RcmClaims claim,
			RcmUser createdBy, RcmTeam team, boolean finalSubmit, String clientName) throws Exception {
		RcmInsuranceFollowUpSection followUpInformation = null;
		if (claim != null) {
			followUpInformation = new RcmInsuranceFollowUpSection();
			followUpInformation.setCurrentClaimStatus(rcmFollowUpInsuranceInfoModel.getCurrentClaimStatus());
			followUpInformation.setFollowUpRemarks(rcmFollowUpInsuranceInfoModel.getFollowUpRemarks());
			followUpInformation.setClaim(claim);
			followUpInformation.setInsuranceRepName(rcmFollowUpInsuranceInfoModel.getInsuranceRepName());
			followUpInformation.setModeOfFollowUp(rcmFollowUpInsuranceInfoModel.getModeOfFollowUp());
			followUpInformation.setNextFollowUpRequired(rcmFollowUpInsuranceInfoModel.getNextFollowUpRequired());
			followUpInformation.setNextFollowUpDate(!StringUtils.isNoneBlank(rcmFollowUpInsuranceInfoModel.getNextFollowUpDate())?null:
					Constants.SDF_MYSL_DATE.parse(rcmFollowUpInsuranceInfoModel.getNextFollowUpDate()));
			followUpInformation.setRefNumber(rcmFollowUpInsuranceInfoModel.getRefNumber());
			followUpInformation.setCreatedBy(createdBy);
			followUpInformation.setFinalSubmit(finalSubmit);
			followUpInformation.setTeam(team);
			followUpInformation = followUpRepo.save(followUpInformation);
			rcmFollowUpInsuranceInfoModel.setFollowByTeam(followUpInformation.getTeam().getDescription());	
			rcmFollowUpInsuranceInfoModel.setFollowByUser(followUpInformation.getCreatedBy().getFirstName());	
			rcmFollowUpInsuranceInfoModel.setFollowByUserLastName(followUpInformation.getCreatedBy().getLastName());
			rcmFollowUpInsuranceInfoModel.setNextFollowUpDate(followUpInformation.getNextFollowUpDate()==null?"":Constants.SDF_MYSL_DATE_TIME.format(followUpInformation.getNextFollowUpDate()));
			BeanUtils.copyProperties(followUpInformation, rcmFollowUpInsuranceInfoModel);
		}
		return rcmFollowUpInsuranceInfoModel;
	}
	
	public List<RcmFollowUpInsuranceDto> fetchFollowUpInsuranceInformation(PartialHeader partialHeader,
			String claimUuid) throws Exception {
		RcmFollowUpInsuranceDto responseDto = null;
		List<RcmFollowUpInsuranceDto> responseData = new ArrayList<>();
		List<RcmInsuranceFollowUpSection> followUpInsuranceInformation = new ArrayList<>();
			followUpInsuranceInformation = followUpRepo.findByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		if (!followUpInsuranceInformation.isEmpty()) {
			for (RcmInsuranceFollowUpSection data : followUpInsuranceInformation) {
				RcmUser attachBy=userRepo.findByUuid(data.getCreatedBy().getUuid());
				RcmTeam team=rcmTeamRepo.findById(data.getTeam().getId());
				responseDto = new RcmFollowUpInsuranceDto();	
				responseDto.setFollowByUser(attachBy.getFirstName());
				responseDto.setFollowByUserLastName(attachBy.getLastName());
				responseDto.setFollowByTeam(team.getDescription());
				responseDto.setNextFollowUpDate(data.getNextFollowUpDate()==null?"":Constants.SDF_MYSL_DATE_TIME.format(data.getNextFollowUpDate()));
				BeanUtils.copyProperties(data, responseDto);
				responseData.add(responseDto);
			}
		}
		return responseData;
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Boolean savePatientStatementSection(RcmPatientStatementDto rcmPatientStatementInfoModel, RcmClaims claim,
			RcmUser createdBy, RcmTeam team, boolean finalSubmit, String clientName) throws Exception {
		RcmPatientStatementSection patientStatement = null;
		if (!(rcmPatientStatementInfoModel
				.getButtonType() == Constants.NEED_TO_HOLD_BUTTON_TYPE_FOR_PATIENT_STATEMENT_SECTION
				|| rcmPatientStatementInfoModel
						.getButtonType() == Constants.SEND_STATEMENT_BUTTON_TYPE_FOR_PATIENT_STATEMENT_SECTION)) {
			logger.error("Wrong button type");
			return null;
		}
		if (claim != null) {
			patientStatement = new RcmPatientStatementSection();
			patientStatement.setClaim(claim);
			patientStatement.setStatus(rcmPatientStatementInfoModel.getStatus());	
			patientStatement.setAmountStatement(rcmPatientStatementInfoModel.getAmountStatement());
			patientStatement.setBalanceSheetLink(rcmPatientStatementInfoModel.getBalanceSheetLink());
			patientStatement.setModeOfStatement(rcmPatientStatementInfoModel.getModeOfStatement());
			patientStatement.setReason(rcmPatientStatementInfoModel.getReason());
			patientStatement.setButtonType(rcmPatientStatementInfoModel.getButtonType());
			patientStatement.setRemarks(rcmPatientStatementInfoModel.getRemarks());
			patientStatement.setStatementType(rcmPatientStatementInfoModel.getStatementType());
			patientStatement.setStatementNotes(rcmPatientStatementInfoModel.getStatementNotes());
			patientStatement.setStatementSendingDate(!StringUtils.isNoneBlank(rcmPatientStatementInfoModel.getStatementSendingDate())?null:
					Constants.SDF_MYSL_DATE.parse(rcmPatientStatementInfoModel.getStatementSendingDate()));
			patientStatement
					.setNextReviewDate(
							!StringUtils.isNoneBlank(rcmPatientStatementInfoModel.getNextReviewDate())?null:
							Constants.SDF_MYSL_DATE.parse(rcmPatientStatementInfoModel.getNextReviewDate()));
			patientStatement.setStatementSendingDate(!StringUtils.isNoneBlank(rcmPatientStatementInfoModel.getStatementSendingDate())?null:
					Constants.SDF_MYSL_DATE.parse(rcmPatientStatementInfoModel.getStatementSendingDate()));
			patientStatement.setNextStatementDate(!StringUtils.isNoneBlank(rcmPatientStatementInfoModel.getNextStatementDate())?null:
					Constants.SDF_MYSL_DATE.parse(rcmPatientStatementInfoModel.getNextStatementDate()));
			patientStatement.setCreatedBy(createdBy);
			patientStatement.setFinalSubmit(finalSubmit);
			patientStatement.setTeam(team);
			patientStatement = patientStatementRepo.save(patientStatement);
			return patientStatement != null ? true : null;
		}
		return null;
	}
	
	public RcmPatientStatementDto fetchPatientStatementInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		RcmPatientStatementSection patientStatement = null;
		RcmPatientStatementDto responseDto = null;
		if (showWithTeam) {
			patientStatement = patientStatementRepo
					.findFirstByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(claimUuid,partialHeader.getTeamId());
		} else {
			patientStatement = patientStatementRepo.findFirstByClaimClaimUuidOrderByCreatedDateDesc(
					claimUuid);
		}
		if (patientStatement != null) {
			responseDto = new RcmPatientStatementDto();
			responseDto.setNextReviewDate(patientStatement.getNextReviewDate()==null?"":
					Constants.SDF_MYSL_DATE.format(patientStatement.getNextReviewDate()));
			responseDto.setNextStatementDate(
					patientStatement.getNextStatementDate()==null?"":
					Constants.SDF_MYSL_DATE.format(patientStatement.getNextStatementDate()));
			responseDto.setStatementSendingDate(
					patientStatement.getStatementSendingDate()==null?"":
					Constants.SDF_MYSL_DATE.format(patientStatement.getStatementSendingDate()));
			BeanUtils.copyProperties(patientStatement, responseDto);
			return responseDto;
		}
		return null;
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Boolean saveNextActionRequiredAndCurrentClaimStatusSection(
			CurrentStatusAndNextActionDto nextActionReequiredInfoModel, RcmClaims claim, RcmUser createdBy,
			RcmTeam team, boolean finalSubmit, String clientName) {
		CurrentClaimStatusAndNextAction currentClaimStatusAndNextActionData = null;
		RcmTeam assignToTeam=rcmTeamRepo.findById(nextActionReequiredInfoModel.getAssignToTeamId());
		if(assignToTeam==null) {
			logger.error("Invalid team");
			return null;
		}
		if(assignToTeam.getId()==team.getId()) {
			logger.error("Team not assign to logged user team");
			return null;		
		}
		if (claim != null) {
			currentClaimStatusAndNextActionData = new CurrentClaimStatusAndNextAction();
			currentClaimStatusAndNextActionData
					.setCurrentClaimStatusEs(nextActionReequiredInfoModel.getCurrentClaimStatusEs());
			currentClaimStatusAndNextActionData
					.setCurrentClaimStatusRcm(nextActionReequiredInfoModel.getCurrentClaimStatusEs());
			currentClaimStatusAndNextActionData.setNextAction(nextActionReequiredInfoModel.getNextAction());
			currentClaimStatusAndNextActionData.setRemarks(nextActionReequiredInfoModel.getRemarks());
			currentClaimStatusAndNextActionData.setClaim(claim);
			currentClaimStatusAndNextActionData.setCreatedBy(createdBy);
			currentClaimStatusAndNextActionData.setFinalSubmit(finalSubmit);
			currentClaimStatusAndNextActionData.setTeam(team);
			currentClaimStatusAndNextActionData.setAssignToTeam(assignToTeam);
			currentClaimStatusAndNextActionData = currentStatusAndNextActionRepo
					.save(currentClaimStatusAndNextActionData);
			return currentClaimStatusAndNextActionData != null ? true : null;
		}
		return null;
	}

	public CurrentStatusAndNextActionDto fetchCurrentStatusAndNextActionInformation(PartialHeader partialHeader,
			String claimUuid, boolean showWithTeam) throws Exception {
		CurrentClaimStatusAndNextAction currentStatusData = null;
		CurrentStatusAndNextActionDto responseDto = null;
		if (showWithTeam) {
			currentStatusData = currentStatusAndNextActionRepo
					.findFirstByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(claimUuid, partialHeader.getTeamId());
		} else {
			currentStatusData = currentStatusAndNextActionRepo
					.findFirstByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		}
		if (currentStatusData != null) {
			responseDto = new CurrentStatusAndNextActionDto();
			responseDto.setAssignToTeamId(currentStatusData.getAssignToTeam().getId());
			BeanUtils.copyProperties(currentStatusData, responseDto);
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Object savePatientCommunicationSection(RcmPatientCommunicationDto patientCommunicationInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit, String clientName) {
		RcmPatientCommunicationSection patientCommunicationData = null;
		if (claim != null) {
			patientCommunicationData = new RcmPatientCommunicationSection();
			patientCommunicationData.setRemarks(patientCommunicationInfoModel.getRemarks());
			patientCommunicationData.setContact(patientCommunicationInfoModel.getContact());
			patientCommunicationData.setDesposition(patientCommunicationInfoModel.getDesposition());
			patientCommunicationData.setModeOfFollowUp(patientCommunicationInfoModel.getModeOfFollowUp());
			patientCommunicationData.setClaim(claim);
			patientCommunicationData.setCreatedBy(createdBy);
			patientCommunicationData.setFinalSubmit(finalSubmit);
			patientCommunicationData.setTeam(team);
			patientCommunicationData = patientCommunicationRepo.save(patientCommunicationData);
			patientCommunicationInfoModel.setCreatedTeam(patientCommunicationData.getTeam().getDescription());
			patientCommunicationInfoModel.setCreatedBy(patientCommunicationData.getCreatedBy().getFirstName());
			patientCommunicationInfoModel.setDate(Constants.SDF_MYSL_DATE_TIME.format(patientCommunicationData.getCreatedDate()));
			BeanUtils.copyProperties(patientCommunicationData, patientCommunicationInfoModel);
		}
		return patientCommunicationInfoModel;
	}
	
	public List<RcmPatientCommunicationDto> fetchPatientCommunicationInformation(PartialHeader partialHeader,
			String claimUuid) throws Exception {
		RcmPatientCommunicationDto responseDto = null;
		List<RcmPatientCommunicationDto> responseData = new ArrayList<>();
		List<RcmPatientCommunicationSection> patientCommunicationInformation = patientCommunicationRepo
				.findByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		if (!patientCommunicationInformation.isEmpty()) {
			for (RcmPatientCommunicationSection data : patientCommunicationInformation) {
				RcmUser attachBy = userRepo.findByUuid(data.getCreatedBy().getUuid());
				RcmTeam team = rcmTeamRepo.findById(data.getTeam().getId());
				responseDto = new RcmPatientCommunicationDto();
				responseDto.setCreatedBy(attachBy.getFirstName());
				responseDto.setCreatedTeam(team.getDescription());
				responseDto.setDate(data.getCreatedDate()==null?"":Constants.SDF_MYSL_DATE_TIME.format(data.getCreatedDate()));
				BeanUtils.copyProperties(data, responseDto);
				responseData.add(responseDto);
			}
		}
		return responseData;
	}
	
}
