package com.tricon.rcm.service.impl;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.ClaimUserSectionMapping;
import com.tricon.rcm.db.entity.CurrentClaimStatusAndNextAction;
import com.tricon.rcm.db.entity.EOBSectionInformation;
//import com.tricon.rcm.db.entity.NeedToCallInsurance;
import com.tricon.rcm.db.entity.PaymentInformationSection;
import com.tricon.rcm.db.entity.RcmAppealLevelInformation;
import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimDetail;
import com.tricon.rcm.db.entity.RcmClaimLevelSection;
import com.tricon.rcm.db.entity.RcmClaimSection;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmClientSectionMapping;
import com.tricon.rcm.db.entity.RcmCollectionAgency;
import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmInsuranceFollowUpSection;
import com.tricon.rcm.db.entity.RcmInsuranceType;
import com.tricon.rcm.db.entity.RcmInsuranceTypeDateMapping;
import com.tricon.rcm.db.entity.RcmIssueClaims;
import com.tricon.rcm.db.entity.RcmLinkedClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmPatientCommunicationSection;
import com.tricon.rcm.db.entity.RcmPatientPayment;
import com.tricon.rcm.db.entity.RcmPatientStatementSection;
import com.tricon.rcm.db.entity.RcmRebilledClaimValidationRemark;
import com.tricon.rcm.db.entity.RcmRebillingSection;
import com.tricon.rcm.db.entity.RcmRecreateClaim;
import com.tricon.rcm.db.entity.RcmRequestRebiilingSection;
import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.db.entity.RcmServiceLevelInformation;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.AppealInformationDto;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.ClaimLevelInformationDto;
import com.tricon.rcm.dto.ClaimLogDto;
import com.tricon.rcm.dto.ClaimStatusUpdate;
import com.tricon.rcm.dto.ClientSectionMappingDto;
import com.tricon.rcm.dto.CollectionAgencyDto;
import com.tricon.rcm.dto.CurrentStatusAndNextActionDto;
import com.tricon.rcm.dto.EOBDto;
import com.tricon.rcm.dto.EobSectionEditDto;
import com.tricon.rcm.dto.IssueClaimDto;

import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PatientPaymentSectionDto;
import com.tricon.rcm.dto.PaymentInformationSectionDto;
import com.tricon.rcm.dto.RcmFollowUpInsuranceDto;
import com.tricon.rcm.dto.RcmPatientCommunicationDto;
import com.tricon.rcm.dto.RcmPatientStatementDto;
import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto;
import com.tricon.rcm.dto.RcmTeamSectionAccessDto.SectionData;
import com.tricon.rcm.dto.RebillingDto;
import com.tricon.rcm.dto.RebillingDtoSub;
import com.tricon.rcm.dto.RebillingResponseDto;

import com.tricon.rcm.dto.RecreateClaimRequestDto;
import com.tricon.rcm.dto.RecreateResponseDto;
import com.tricon.rcm.dto.RequestRebillingDto;
import com.tricon.rcm.dto.ServiceLevelNotes;
import com.tricon.rcm.dto.ServiceLevelRequestBodyDto;
import com.tricon.rcm.dto.ServiceLevelTotalAmountDto;
import com.tricon.rcm.dto.ValidateCreateClaimInformationDto;
import com.tricon.rcm.dto.ValidateRecreateClaimResponseDto;
import com.tricon.rcm.dto.ValidationRuleRemarksDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;

import com.tricon.rcm.dto.customquery.RcmServiceNotesDto;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.PatientStatementTypeEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.EOBSectionRepo;
import com.tricon.rcm.jpa.repository.FollowUpInsuranceRepo;
//import com.tricon.rcm.jpa.repository.NeedToCallInsuranceRepo;
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
import com.tricon.rcm.jpa.repository.RcmCollectionAgencyRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmCurrentClaimStatusRepo;
import com.tricon.rcm.jpa.repository.RcmInsurancePaymentSectionRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeDateMappingRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceTypeRepo;
import com.tricon.rcm.jpa.repository.RcmIssueClaimsRepo;
import com.tricon.rcm.jpa.repository.RcmLinkedClaimsRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmPatientCommunicationRepo;
import com.tricon.rcm.jpa.repository.RcmPatientPaymentSectionRepo;
import com.tricon.rcm.jpa.repository.RcmPatientStatementRepo;
import com.tricon.rcm.jpa.repository.RcmRebillingSectionRepo;
import com.tricon.rcm.jpa.repository.RcmRecreateClaimRepo;
import com.tricon.rcm.jpa.repository.RcmRecreationValidationRepo;
import com.tricon.rcm.jpa.repository.RcmRequestRebillingSectionRepo;
import com.tricon.rcm.jpa.repository.RcmRuleRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.RcmUserCompanyRepo;
import com.tricon.rcm.jpa.repository.ServiceLevelInformationRepo;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;
import com.tricon.rcm.util.RuleConstants;

@Service
public class ClaimSectionImpl {

	private final Logger logger = LoggerFactory.getLogger(ClaimSectionImpl.class);

	@Autowired
	RcmClientSectionMappingRepo clientSectionMappingRepo;

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;
	
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
	
	@Autowired
	RcmInsuranceTypeRepo rcmInsuranceTypeRepo;
	

	
	@Autowired
	RcmCollectionAgencyRepo collectionAgencyRepo;
	
	@Autowired
	RcmRebillingSectionRepo rebillingSectionRepo;
	
	
	@Autowired
	RcmRequestRebillingSectionRepo  requestRebillingSectionRepo;
	
	@Autowired
	RcmClaimLogServiceImpl rcmClaimLogServiceImpl;
	
	@Autowired
	RcmUtilServiceImpl utilServiceImpl;
	
	
	@Autowired
	RuleBookServiceImpl ruleBookService;
	
	@Autowired
	RcmRuleRepo rcmRuleRepo;
	
	@Autowired
	RcmRecreationValidationRepo rcmRecreationValidationRepo;
	
	@Autowired
	RcmRecreateClaimRepo rcmRecreateClaimRepo;
	
	@Autowired
	RcmLinkedClaimsRepo rcmLinkedClaimsRepo;
	
	@Lazy
	@Autowired
	ClaimServiceImpl claimServiceImpl;
	
	@Autowired
	RcmIssueClaimsRepo rcmIssueClaimsRepo;
	
//	@Autowired
//	NeedToCallInsuranceRepo needToCallRepo;
	
	@Lazy
	@Autowired
	RcmCommonServiceImpl rcmCommonServiceImpl;
	
	@Autowired
	RcmInsuranceTypeDateMappingRepo insuranceTypeDateMappingRepo;
	
	

	@Transactional(rollbackOn = Exception.class)
	public String manageClientSectionDetails(List<ClientSectionMappingDto> listOfClaimSections) throws Exception {
		String msg = null;
		RcmClientSectionMapping sectionMapping = null;
		List<RcmCompany> listOfClients = rcmCompanyRepo.findAll();
		List<RcmTeam> listOfTeams = rcmTeamRepo.findAll();
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
								Optional<RcmClaimSection> section = claimSectionRepo.findById(sections.getSectionId());
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
	
	
	public List<ClientSectionMappingDto> getClientsWithAllSectionsAndTeam(PartialHeader partialHeader) throws Exception {
		List<ClientSectionMappingDto> response = new ArrayList<>();
		List<RcmClaimSection> claimSections = claimSectionRepo.findAllWithSectionCategory().stream()
				.filter(x -> x.isActive() == true).collect(Collectors.toList());
		List<ClientCustomDto> clients = null;
		if (partialHeader.getRole().equals(Constants.ADMIN)) {
			clients = rcmCompanyRepo.findAllClientsOfAssociatedUser(partialHeader.getJwtUser().getUuid());
		} else {
			clients = rcmCompanyRepo.findAllClients();
		}
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
						sectionData.setEditAccessGlobalLevel(existingSectionMappingWithClient != null
								? existingSectionMappingWithClient.isEditAccess()
								: false);
						sectionData.setViewAccessGlobalLevel(existingSectionMappingWithClient != null
								? existingSectionMappingWithClient.isViewAccess()
								: false);
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
		List<RcmTeam> listOfTeams = rcmTeamRepo.findAll();
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
								Optional<RcmClaimSection> section = claimSectionRepo.findById(sections.getSectionId());
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
			claimLevelSection.setDownPaymentAmount(claimLvelInfoDto.getDownPaymentAmount());
			claimLevelSection.setInstallmentAmount(claimLvelInfoDto.getInstallmentAmount());
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
	public AppealInformationDto saveAppealInformation(AppealInformationDto appealInfoDto,RcmClaims claim,RcmUser createdBy,RcmTeam team,boolean isFinalSubmit)
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
			appealInfoDto.setId(appealInformation.getId());
			appealInfoDto.setCreatedDate(appealInformation.getCreatedDate());
			return appealInformation != null ? appealInfoDto : null;
		}
		return null;
	}

	public List<AppealInformationDto> fetchAppealLevelInfo(PartialHeader partialHeader, String claimUuid, boolean showWithTeam)
			throws Exception {
		List<AppealInformationDto> responseDto = new ArrayList<>();
		List<RcmAppealLevelInformation> appealLevelInformations = null;
		if (showWithTeam) {
			appealLevelInformations = appealInfoRepo.findByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid,partialHeader.getTeamId());
		} else {
			appealLevelInformations = appealInfoRepo.findByClaimClaimUuidOrderByCreatedDateDesc(
					claimUuid);
		}
		if (appealLevelInformations != null) {
			AppealInformationDto dto=null;
			for(RcmAppealLevelInformation appealLevelInformation:appealLevelInformations) {
				dto = new AppealInformationDto();
			BeanUtils.copyProperties(appealLevelInformation, dto);
			responseDto.add(dto);
			
			}
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Object savePatientPaymentSection(PatientPaymentSectionDto patientPaymentInfoModel,
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
			patientPaymentInformation.setCheckNumber(patientPaymentInfoModel.getCheckNumber());
			patientPaymentInformation.setCardNumber(patientPaymentInfoModel.getCardNumber());
			patientPaymentInformation = patientPaymentRepo.save(patientPaymentInformation);
			patientPaymentInfoModel.setCreatedDate(patientPaymentInformation.getCreatedDate());
			patientPaymentInfoModel.setId(patientPaymentInformation.getId());
			//update AmountCollectedClaims in rcm_claim tables
			if(finalSubmit) {
				claim.setAmountCollectedClaims((float)patientPaymentInfoModel.getAmountCollectedClaims());
				rcmClaimRepository.save(claim);		
			}
			return patientPaymentInformation != null ? patientPaymentInfoModel : null;
		}
		return null;
	}
	
	
	public List<PatientPaymentSectionDto> fetchPatientPaymentInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		List<PatientPaymentSectionDto> responseDto = new ArrayList<>();
		List<RcmPatientPayment> patientPaymentlSections = null;
		if (showWithTeam) {
			patientPaymentlSections = patientPaymentRepo
					.findByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid,partialHeader.getTeamId());
		} else {
			patientPaymentlSections = patientPaymentRepo
					.findByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		}
		if (patientPaymentlSections != null) {
			PatientPaymentSectionDto patientPaymentSectionDto = null;
			for(RcmPatientPayment patientPaymentlSection:patientPaymentlSections) {
				patientPaymentSectionDto = new PatientPaymentSectionDto();
				patientPaymentSectionDto.setDateOfPayment(patientPaymentlSection.getDateOfPayment()==null?"":Constants.SDF_MYSL_DATE.format((patientPaymentlSection.getDateOfPayment())));
			   BeanUtils.copyProperties(patientPaymentlSection, patientPaymentSectionDto);
			   //patientPaymentSectionDto.setCreatedDate(patientPaymentlSection.getCreatedDate());
			   responseDto.add(patientPaymentSectionDto);
			}
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
			eobInformation.setDocumentType(eobInfoModel.getDocumentType());
			eobInformation.setClaim(claim);
			eobInformation.setCreatedBy(createdBy);
			eobInformation.setFinalSubmit(finalSubmit);
			eobInformation = eobRepo.save(eobInformation);
			eobInfoModel.setAttachByTeam(team.getName());
			eobInfoModel.setAttachBy(createdBy.getFirstName());
			eobInfoModel.setDate(Constants.SDF_MYSL_DATE.format((eobInformation.getCreatedDate())));
			eobInfoModel.setId(eobInformation.getId());		
//			try {
//				// set eob file path
//				String fileName = claim.getClaimUuid() + new Date().getTime() + "." + eobInfoModel.getExtension();
//				    URL url = new URL(eobInfoModel.getEobLink());
//					FileUtils.copyURLToFile(url, new File(eobLinkFolder + File.separator + fileName), 60000, 60000);
//					eobInformation.setEobFilePath(fileName);
//					eobInformation = eobRepo.save(eobInformation);
//					eobInfoModel
//							.setEobPathLink(serverDomainLink + "/api/vieweoblink/" + eobInformation.getEobFilePath());
//					eobInfoModel.setAttachByTeam(team.getName());
//					eobInfoModel.setAttachBy(createdBy.getFirstName());
//					eobInfoModel.setDate(Constants.SDF_MYSL_DATE.format((eobInformation.getCreatedDate())));
//				
//					eobInfoModel.setId(eobInformation.getId());		
//			} catch (Exception e) {
//				logger.error("Invalid File Format");
//				eobInfoModel.setEobPathLink("Invalid Format");
//				return null;
//			}
			logger.info("response->" + (eobInformation!=null?true:false));
			return eobInfoModel;
		}
		return null;
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
				responseDto.setEobPathLink(data.getEobFilePath());
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
	public PaymentInformationSectionDto saveInsurancePaymentInformationSection(PaymentInformationSectionDto paymentInformationInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit) throws Exception {

		// if paid amount is 0 then no need to save data in db
		if (paymentInformationInfoModel.getPaidAmount() == 0.0) {
			logger.error("paid amount is 0.so data will not save");
			return null;
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
				paymentInformationInfoModel.setId(paymentInsuranceInformation.getId());
				paymentInformationInfoModel.setCreatedDate(paymentInsuranceInformation.getCreatedDate());
				if(finalSubmit) {
					claim.setAmountReceivedInBank((float)paymentInformationInfoModel.getAmountReceivedInBank());
					rcmClaimRepository.save(claim);		
				}
				
				return paymentInsuranceInformation != null ? paymentInformationInfoModel : null;
			}
		}
		return null;
	}

	public List<PaymentInformationSectionDto> fetchInsurancePaymentInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		List<PaymentInformationSectionDto> responseDto = new ArrayList<>();
		List<PaymentInformationSection> paymentInsuranceInformations = null;
		if (showWithTeam) {
			paymentInsuranceInformations = paymentSectionRepo
					.findByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(claimUuid, partialHeader.getTeamId());
		} else {
			paymentInsuranceInformations = paymentSectionRepo
					.findByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		}
		if (paymentInsuranceInformations != null) {
			PaymentInformationSectionDto dto =null;
			for(PaymentInformationSection paymentInsuranceInformation:paymentInsuranceInformations ) {
				dto = new PaymentInformationSectionDto();
				dto
					.setCheckCashDate(paymentInsuranceInformation.getCheckCashDate()==null?"":Constants.SDF_MYSL_DATE.format((paymentInsuranceInformation.getCheckCashDate())));
				dto.setAmountDateReceivedInBank(
					paymentInsuranceInformation.getAmountDateReceivedInBank()==null?"":
					Constants.SDF_MYSL_DATE.format((paymentInsuranceInformation.getAmountDateReceivedInBank())));
			BeanUtils.copyProperties(paymentInsuranceInformation, dto);
			responseDto.add(dto);
			}
			return responseDto;
			
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Boolean saveServiceLevelInformationSection(ServiceLevelTotalAmountDto serviceLevelInformationInfoModel,
			RcmClaims claim, RcmUser createdBy, RcmTeam team, boolean finalSubmit, String clientName) {
		RcmServiceLevelInformation serviceLevelData = null;
		if (serviceLevelInformationInfoModel == null) {
			logger.error("Empty model");
			return null;
		}
		
		//first we check reconcilation logic
		//TotalBtpAmount+BalanceFromEsBeforePosting=BalanceFromEsAfterPosting
		double totalBtpAmount=serviceLevelInformationInfoModel.getTotalBtpAmount();
		double balanceFromEsBeforePosting= serviceLevelInformationInfoModel
			.getBalanceFromEsBeforePosting();
		double balanceFromEsAfterPosting=serviceLevelInformationInfoModel.getBalanceFromEsAfterPosting();
		double totalValue=totalBtpAmount+balanceFromEsBeforePosting;
		
		if (totalValue != balanceFromEsAfterPosting) {
			logger.error(
					"(TotalBtpAmount+BalanceFromEsBeforePosting=BalanceFromEsAfterPosting),Reconcilation logic failed!");
			// update rcm_claim table for reconciliation_pass value false
			//claim.setReconciliationPass(false);
			//rcmClaimRepository.save(claim);
			return null;
		}

		else {
			logger.info("Total value:"+totalValue+"="+balanceFromEsAfterPosting);	
			logger.info(
					"(TotalBtpAmount+BalanceFromEsBeforePosting=BalanceFromEsAfterPosting),Reconcilation logic passed!");
		}
		
		int maxRun = serviceLevelRepo.getMaxRunFromServiceLevel(claim.getClaimUuid());
		List<RcmClaimDetail> claimDetailData =null;
		List<String> oldServiceCodes =null;
		if (finalSubmit) {
			claimDetailData =claimDetailRepo.findByClaimClaimUuid(claim.getClaimUuid());
			if (claimDetailData!=null) {
			oldServiceCodes = claimDetailData.stream().map(RcmClaimDetail::getServiceCode)
		              .collect(Collectors.toList());
			}			
		}
		for (ServiceLevelRequestBodyDto data : serviceLevelInformationInfoModel.getServiceLevelBody()) {
			serviceLevelData = new RcmServiceLevelInformation();
			serviceLevelData.setClaim(claim);
			serviceLevelData.setCreatedBy(createdBy);
			serviceLevelData.setFinalSubmit(finalSubmit);
			serviceLevelData.setTeam(team);
			serviceLevelData.setActive(true);
			serviceLevelData.setGroupRun(maxRun + 1);
			BeanUtils.copyProperties(data, serviceLevelData);
			serviceLevelData.setTooth((data.getTooth()==null || data.getTooth().isEmpty())?"N/A":data.getTooth());
			serviceLevelData.setSurface((data.getSurface()==null || data.getSurface().isEmpty())?"N/A":data.getSurface());
			serviceLevelData.setCreditAdjustmentAmount(data.getCreditAdjustmentAmount());
			serviceLevelData.setDebitAdjustmentAmount(data.getDebitAdjustmentAmount());
			serviceLevelRepo.save(serviceLevelData); 
			//update Original rcm_claim_detail in
		    if (finalSubmit && claimDetailData!=null) {
		      Optional<RcmClaimDetail> 	 oldRecord= claimDetailData.stream().filter( dt ->dt.getServiceCode().equalsIgnoreCase(data.getServiceCode())).findFirst();
		      if (oldRecord.isPresent()) {
		    	  //Do nothing and  add rest data latter if needed
		    	  if (oldServiceCodes!=null) {
		    		  oldServiceCodes.remove(data.getServiceCode());
		    		  if (!oldRecord.get().isActive()) {
		    			  RcmClaimDetail old =  oldRecord.get();
		    			  old.setActive(true);
		    			  //mark this as  active.
		    			  claimDetailRepo.save(old); 
		    		  }
		    	  }
		    	 
		      }else if (!data.getServiceCode().equalsIgnoreCase("Undistributed")){
		    	  //This is new Data we need to add
		    	  RcmClaimDetail newData = new RcmClaimDetail();
		    	  BeanUtils.copyProperties(data, newData);
		    	  newData.setActive(true);
		    	  newData.setClaim(claim);
		    	  claimDetailRepo.save(newData);
		    	 
		      }
		     
		   }
		}
		// now we update totalPaid amount,adjustment amount and btp amount,CreditAdjustmentAmount,DebitAdjustmentAmount in rcm claim
		// table
		if (finalSubmit) {
			//May be error if Same Service Code exits it will deactivate all the service codes
		 if (oldServiceCodes!=null && oldServiceCodes.size()>0) {
			 //mark old record as deleted.
			 claimDetailRepo.deActivatedRcmDetailWithClaimUUidAndCode(claim.getClaimUuid(),oldServiceCodes);
		 }
		    claim.setBalanceFromEsBeforePosting((float) serviceLevelInformationInfoModel.getBalanceFromEsBeforePosting());
			claim.setBalanceFromEsAfterPosting((float) serviceLevelInformationInfoModel.getBalanceFromEsAfterPosting());
			
			claim.setAdjustment((float) serviceLevelInformationInfoModel.getTotalAdjustmentAmount());
			claim.setBtp((float) serviceLevelInformationInfoModel.getTotalBtpAmount());
			claim.setPaidAmount((float) serviceLevelInformationInfoModel.getTotalPaidAmount());
			claim.setReconciliationPass(true);
			claim.setCreditAdjustmentAmount((float) serviceLevelInformationInfoModel.getTotalCreditAdjustmentAmount());	
			claim.setDebitAdjustmentAmount((float) serviceLevelInformationInfoModel.getTotalDebitAdjustmentAmount());
			rcmClaimRepository.save(claim);
			
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
				.findServiceLevelCodesByClaimUuid(claimUuid,maxRun);// all data max data 
		if (serviceLevelData.isEmpty()) {
			// fetch data from rcm claim_detail table and insert into
			// rcm_service_level_information table
			List<RcmClaimDetail> claimDetailData = claimDetailRepo.findByClaimClaimUuidAndActiveTrue(claimUuid);
			if(claimDetailData.isEmpty()) {
				logger.error("No claim found in claim detail table");
			}
			RcmTeam team=rcmTeamRepo.findById(partialHeader.getTeamId());
			RcmUser createdBy = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
			for (RcmClaimDetail rcmClaimDetail : claimDetailData) {
				serviceLevelDto = new RcmServiceLevelInformation();
				BeanUtils.copyProperties(rcmClaimDetail, serviceLevelDto);
				serviceLevelDto.setGroupRun(1);
				serviceLevelDto.setTooth((rcmClaimDetail.getTooth()==null || rcmClaimDetail.getTooth().isEmpty())?"N/A":rcmClaimDetail.getTooth());
				serviceLevelDto.setSurface((rcmClaimDetail.getSurface()==null || rcmClaimDetail.getSurface().isEmpty())?"N/A":rcmClaimDetail.getSurface());
				serviceLevelDto.setFlag(false);		
				serviceLevelDto.setTeam(team);
				serviceLevelDto.setCreatedBy(createdBy);
				listOfServiceLevelDto.add(serviceLevelDto);
			}
			listOfServiceLevelDto = serviceLevelRepo.saveAll(listOfServiceLevelDto);
			for (RcmServiceLevelInformation list : listOfServiceLevelDto) {
				responseData = new ServiceLevelRequestBodyDto();
				BeanUtils.copyProperties(list, responseData);
				responseData.setAdjustmentReason(list.getAdjustmentReason()==null?"":list.getAdjustmentReason());
				responseData.setBtpReason(list.getBtpReason()==null?"":list.getBtpReason());
				data.add(responseData);
			}

		} else {
			String oldCreatedBy = serviceLevelData.get(0).getCreatedBy().getUuid();
			List<RcmServiceNotesDto> oldServiceNotes = null;
			if (partialHeader.getJwtUser().getUuid().equals(oldCreatedBy)) {
				oldServiceNotes = serviceLevelRepo.findOldServiceLevelCodesByClaimUuid(claimUuid, maxRun);
			} else {
				oldServiceNotes = serviceLevelRepo.findAllServiceLevelCodesByClaimUuid(claimUuid);
			}
			ServiceLevelNotes notes = null;
			Set<ServiceLevelNotes> oldNotesList = new HashSet<ServiceLevelNotes>();
			Set<ServiceLevelNotes> newNotesList = new HashSet<ServiceLevelNotes>();
			for (RcmServiceLevelInformation serviceCodes : serviceLevelData) {
				for (RcmServiceNotesDto serviceNotes : oldServiceNotes) {
					if (serviceCodes.getSurface() == null) {
						serviceCodes.setSurface("N/A");
					}
					if (serviceCodes.getTooth() == null) {
						serviceCodes.setTooth("N/A");
					}
					
					if (serviceCodes.getServiceCode().equals(serviceNotes.getServiceCode())
							&& serviceCodes.getTooth().equals(serviceNotes.getTooth())
							&& serviceCodes.getSurface().equals(serviceNotes.getSurface())){
						if (serviceNotes.getNotes()!=null && !serviceNotes.getNotes().isEmpty()) {
							notes = new ServiceLevelNotes();
							notes.setNotes(serviceNotes.getNotes());
							notes.setSurface(serviceNotes.getSurface());
							notes.setTooth(serviceNotes.getTooth());		
							notes.setServiceCode(serviceNotes.getServiceCode());
							notes.setCreatedBy(serviceNotes.getCreatedBy());
							notes.setCreatedDate(serviceNotes.getDate() == null ? ""
									: Constants.SDF_MYSL_DATE.format(serviceNotes.getDate()));
							notes.setTeamName(serviceNotes.getTeamName());
							oldNotesList.add(notes);
						}
					}
				}
			}
			
			for (RcmServiceLevelInformation serviceData : serviceLevelData) {
				
				if (serviceData.getSurface() == null) {
					serviceData.setSurface("N/A");
				}
				if (serviceData.getTooth() == null) {
					serviceData.setTooth("N/A");
				}
				responseData = new ServiceLevelRequestBodyDto();
				newNotesList = new HashSet<ServiceLevelNotes>();
				for (ServiceLevelNotes notesData : oldNotesList) {
					if (notesData.getServiceCode().equals(serviceData.getServiceCode())
							&& notesData.getTooth().equals(serviceData.getTooth())
							&& notesData.getSurface().equals(serviceData.getSurface())) {
						newNotesList.add(notesData);
						responseData.setServiceCodeNotes(newNotesList);
					}		
				}			
				if (partialHeader.getJwtUser().getUuid().equals(serviceData.getCreatedBy().getUuid())) {
				} else {
					serviceData.setNotes("");
				}
				oldCreatedBy=serviceData.getCreatedBy().getUuid();
				BeanUtils.copyProperties(serviceData, responseData);
				responseData.setRebilledCodeStatus(serviceData.isRebilledStatus());
				responseData.setAdjustmentReason(serviceData.getAdjustmentReason()==null?"":serviceData.getAdjustmentReason());
				responseData.setBtpReason(serviceData.getBtpReason()==null?"":serviceData.getBtpReason());
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
			followUpInformation.setTypeOfFollowUp(rcmFollowUpInsuranceInfoModel.getTypeOfFollowUp());
			followUpInformation.setFollowUpRemarks(rcmFollowUpInsuranceInfoModel.getFollowUpRemarks());
			followUpInformation.setClaim(claim);
			followUpInformation.setInsuranceRepName(rcmFollowUpInsuranceInfoModel.getInsuranceRepName());
			//followUpInformation.setNextFollowUpDate(new Date());
			followUpInformation.setModeOfFollowUp(rcmFollowUpInsuranceInfoModel.getModeOfFollowUp());
			followUpInformation.setRefNumber(rcmFollowUpInsuranceInfoModel.getRefNumber());
			followUpInformation.setCreatedBy(createdBy);
			followUpInformation.setFinalSubmit(finalSubmit);
			followUpInformation.setTeam(team);
			followUpInformation = followUpRepo.save(followUpInformation);
			rcmFollowUpInsuranceInfoModel.setFollowByTeam(followUpInformation.getTeam().getDescription());	
			rcmFollowUpInsuranceInfoModel.setFollowByUser(followUpInformation.getCreatedBy().getFirstName());	
			rcmFollowUpInsuranceInfoModel.setFollowByUserLastName(followUpInformation.getCreatedBy().getLastName());
			rcmFollowUpInsuranceInfoModel.setNextFollowUpDate(followUpInformation.getCreatedDate()==null?"":Constants.SDF_MYSL_DATE_TIME.format(followUpInformation.getCreatedDate()));
			BeanUtils.copyProperties(followUpInformation, rcmFollowUpInsuranceInfoModel);	
		}
		logger.info("response->" + (followUpInformation!=null?true:false));
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
				responseDto.setNextFollowUpDate(data.getCreatedDate()==null?"":Constants.SDF_MYSL_DATE_TIME.format(data.getCreatedDate()));
				BeanUtils.copyProperties(data, responseDto);
				responseData.add(responseDto);
			}
		}
		return responseData;
	}
	
	@Transactional(rollbackOn = Exception.class) 
	public Object savePatientStatementSection(RcmPatientStatementDto rcmPatientStatementInfoModel, RcmClaims claim,
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
			patientStatement.setButtonType(rcmPatientStatementInfoModel.getButtonType());
			patientStatement.setCreatedBy(createdBy);
			patientStatement.setFinalSubmit(finalSubmit);
			patientStatement.setTeam(team);
			patientStatement.setStatus(rcmPatientStatementInfoModel.getStatus());
			patientStatement.setAttachStatement(rcmPatientStatementInfoModel.getAttachStatement());
			if (rcmPatientStatementInfoModel
					.getButtonType() == Constants.NEED_TO_HOLD_BUTTON_TYPE_FOR_PATIENT_STATEMENT_SECTION) {
				patientStatement.setBalanceSheetLink(rcmPatientStatementInfoModel.getBalanceSheetLink());
				patientStatement.setReason(rcmPatientStatementInfoModel.getReason()==null?"":rcmPatientStatementInfoModel.getReason());
				patientStatement.setRemarks(rcmPatientStatementInfoModel.getRemarks());
//				patientStatement.setNextReviewDate(
//						!StringUtils.isNoneBlank(rcmPatientStatementInfoModel.getNextReviewDate()) ? null
//								: Constants.SDF_MYSL_DATE.parse(rcmPatientStatementInfoModel.getNextReviewDate()));
			} else {
				patientStatement.setAmountStatement(rcmPatientStatementInfoModel.getAmountStatement());
				patientStatement.setModeOfStatement(rcmPatientStatementInfoModel.getModeOfStatement()==null?"":rcmPatientStatementInfoModel.getModeOfStatement());
				patientStatement.setStatementType(rcmPatientStatementInfoModel.getStatementType()==null?"":rcmPatientStatementInfoModel.getStatementType());
				patientStatement.setStatementNotes(rcmPatientStatementInfoModel.getStatementNotes());
				
				//next statement date is already present in list of claim page so no need to showing here
//				patientStatement.setNextStatementDate(
//						!StringUtils.isNoneBlank(rcmPatientStatementInfoModel.getNextStatementDate()) ? null
//								: Constants.SDF_MYSL_DATE.parse(rcmPatientStatementInfoModel.getNextStatementDate()));
				patientStatement.setStatementSendingDate(
						!StringUtils.isNoneBlank(rcmPatientStatementInfoModel.getStatementSendingDate()) ? null
								: Constants.SDF_MYSL_DATE
										.parse(rcmPatientStatementInfoModel.getStatementSendingDate()));
			}
			patientStatement = patientStatementRepo.save(patientStatement);
			rcmPatientStatementInfoModel.setId(patientStatement.getId());
			rcmPatientStatementInfoModel.setDt(patientStatement.getCreatedDate());
			return patientStatement != null ? rcmPatientStatementInfoModel: null;
		}
		return null;

	}

	public List<RcmPatientStatementDto> fetchPatientStatementInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		List<RcmPatientStatementSection> patientStatements = null;
		List<RcmPatientStatementDto> responseDto = new ArrayList<>();
		if (showWithTeam) {
			patientStatements = patientStatementRepo.findByClaimClaimUuidAndTeamIdAndMarkAsDeletedFalseOrderByCreatedDateDesc(claimUuid,
					partialHeader.getTeamId());
		} else { 
			patientStatements = patientStatementRepo.findByClaimClaimUuidAndMarkAsDeletedFalseOrderByCreatedDateDesc(claimUuid);
		}
        //next review date and next statement date is a automated fields early but now, not required for this
		RcmUser createdBy = userRepo.findByUuid(partialHeader.getJwtUser().getUuid());
		boolean sectionAccess = rcmCommonServiceImpl.validateUserSectionAccess(partialHeader,
				15,createdBy);
		if (sectionAccess) {
			RcmPatientStatementDto psDto = new RcmPatientStatementDto();
			// set automated fields value StatementType default
			// 1,NextReviewDate,NextStatementDate
			psDto.setStatementType(String.valueOf(1));
			psDto.setButtonType(1);
			responseDto.add(psDto);
			for (RcmPatientStatementSection patientStatement : patientStatements) {
				
				 RcmPatientStatementDto psDto1 = new RcmPatientStatementDto();
					// set automated fields value StatementType,NextReviewDate,NextStatementDate
					if (StringUtils.isNoneBlank(patientStatement.getStatementType())) {
						if (patientStatement.getStatementType()
								.equals("" + PatientStatementTypeEnum.STATEMENT_1.getType())) {
							int type = Integer.valueOf(patientStatement.getStatementType());
							patientStatement.setStatementType(String.valueOf(type + 1));
						} else if (patientStatement.getStatementType()
								.equals("" + PatientStatementTypeEnum.STATEMENT_2.getType())) {
							int type = Integer.valueOf(patientStatement.getStatementType());
							patientStatement.setStatementType(String.valueOf(type + 1));
						}

					} else {
						logger.error("Statement Type is missing,so automation failed");
					}
					psDto1.setStatementSendingDate(patientStatement.getStatementSendingDate() == null ? ""
							: Constants.SDF_MYSL_DATE.format(patientStatement.getStatementSendingDate()));
					BeanUtils.copyProperties(patientStatement, psDto1);
					psDto1.setDt(patientStatement.getCreatedDate());
					
				
				responseDto.add(psDto1);
			}
		} else {
			/*if (patientStatement != null) {
				responseDto = new RcmPatientStatementDto();
				responseDto.setStatementSendingDate(patientStatement.getStatementSendingDate() == null ? ""
						: Constants.SDF_MYSL_DATE.format(patientStatement.getStatementSendingDate()));
				BeanUtils.copyProperties(patientStatement, responseDto);
			}*/
		}
		return responseDto;
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Boolean saveNextActionRequiredAndCurrentClaimStatusSection(
			CurrentStatusAndNextActionDto nextActionRequiredInfoModel, RcmClaims claim, RcmUser createdBy,
			RcmTeam team, boolean finalSubmit, String clientName) {
		CurrentClaimStatusAndNextAction currentClaimStatusAndNextActionData = null;
		RcmTeam assignToTeam=rcmTeamRepo.findById(nextActionRequiredInfoModel.getAssignToTeamId());
		
		//assign to team is null when claim is closed
		if(assignToTeam==null) {
			if(!nextActionRequiredInfoModel.getCurrentClaimStatusRcm().equals(ClaimStatusEnum.Case_Closed.getType())){
				logger.error("Invalid team");
				return null;
			}
		}
		
		if (assignToTeam != null && assignToTeam.getId() == team.getId()) {
			logger.error("Team is assign to logged user team");
		} 

		if (claim != null && finalSubmit) {
			currentClaimStatusAndNextActionData = new CurrentClaimStatusAndNextAction();
			currentClaimStatusAndNextActionData
					.setCurrentClaimStatusEs(nextActionRequiredInfoModel.getCurrentClaimStatusEs());
			currentClaimStatusAndNextActionData
					.setCurrentClaimStatusRcm(nextActionRequiredInfoModel.getCurrentClaimStatusRcm());
			currentClaimStatusAndNextActionData.setNextAction(nextActionRequiredInfoModel.getNextAction());
			currentClaimStatusAndNextActionData.setRemarks(nextActionRequiredInfoModel.getRemarks());
			currentClaimStatusAndNextActionData.setClaim(claim);
			currentClaimStatusAndNextActionData.setCreatedBy(createdBy);
			currentClaimStatusAndNextActionData.setFinalSubmit(finalSubmit);
			currentClaimStatusAndNextActionData.setTeam(team);
			currentClaimStatusAndNextActionData.setAssignToTeam(assignToTeam);
			currentClaimStatusAndNextActionData = currentStatusAndNextActionRepo
					.save(currentClaimStatusAndNextActionData);
			
			// set automated field nextFollowUpDate inside follow_up_section
			if (nextActionRequiredInfoModel.getButtonType() != null
					&& nextActionRequiredInfoModel.getButtonType().equals(Constants.BUTTON_TYPE_ASSIGN_TO_SAME_TEAM)) {
				RcmInsuranceType ins = claim.getRcmInsuranceType();
				RcmInsuranceType inst = rcmInsuranceTypeRepo.findById(ins.getId());
				if (inst != null) {
					RcmInsuranceTypeDateMapping data = insuranceTypeDateMappingRepo
							.findByTeamIdAndName(assignToTeam.getId(), inst.getCode());
					if (data != null) {
						Calendar calendarForNextFollowUpDate = Calendar.getInstance();
						Date date = new Date();//always current Date claim.getNextFollowUpDate() != null ? claim.getNextFollowUpDate() : new Date();
						calendarForNextFollowUpDate.setTime(date);
						calendarForNextFollowUpDate.add(Calendar.DAY_OF_YEAR, data.getNextFollowUpGap());
						claim.setNextFollowUpDate(calendarForNextFollowUpDate.getTime());
						claim.setUpdatedDate(date);
						logger.info("Next Follow Up Data in RcmTable:" + claim.getNextFollowUpDate());
					}else {
						Date date = new Date();
						claim.setNextFollowUpDate(date);
						claim.setUpdatedDate(date);
					}
				}
			}else {
				/*Date date = new Date();
				claim.setNextFollowUpDate(date);
				claim.setUpdatedDate(date);
			*/	
			}
			//update es_satus in claim table
			claim.setStatusESUpdated(nextActionRequiredInfoModel.getCurrentClaimStatusEs());
			rcmClaimRepository.save(claim);
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
	
	@Transactional(rollbackOn = Exception.class)
	public Object saveCollectionAgencySection(CollectionAgencyDto collectionAgencyInfoModel, RcmClaims claim,
			RcmUser createdBy, RcmTeam team, boolean finalSubmit) throws Exception {
		RcmCollectionAgency collectionAgency = null;
		if (!(collectionAgencyInfoModel.getButtonType() == Constants.BUTTON_TYPE_ONE_FOR_COLLECTION_SECTION
				|| collectionAgencyInfoModel.getButtonType() == Constants.BUTTON_TYPE_TWO_FOR_COLLECTION_SECTION
				|| collectionAgencyInfoModel.getButtonType() == Constants.BUTTON_TYPE_THREE_FOR_COLLECTION_SECTION)) {
			logger.error("Wrong button type");
			return null;
		}
		if (claim != null) {
			collectionAgency = new RcmCollectionAgency();
			collectionAgency.setClaim(claim);
			collectionAgency.setCreatedBy(createdBy);
			collectionAgency.setFinalSubmit(finalSubmit);
			collectionAgency.setTeam(team);
			collectionAgency.setButtonType(collectionAgencyInfoModel.getButtonType());
			if (collectionAgencyInfoModel.getButtonType() == Constants.BUTTON_TYPE_ONE_FOR_COLLECTION_SECTION) {
				collectionAgency.setCollectionType(collectionAgencyInfoModel.getCollectionType()==null?"":collectionAgencyInfoModel.getCollectionType());
				collectionAgency.setDebtNumber(collectionAgencyInfoModel.getDebtNumber());
			} else if (collectionAgencyInfoModel.getButtonType() == Constants.BUTTON_TYPE_TWO_FOR_COLLECTION_SECTION) {
				collectionAgency.setAmountReceived(collectionAgencyInfoModel.getAmountReceived());
				collectionAgency.setModeOfPayment(collectionAgencyInfoModel.getModeOfPayment()==null?"":collectionAgencyInfoModel.getModeOfPayment());
				collectionAgency.setCommisionCharged(collectionAgencyInfoModel.getCommisionCharged());
				collectionAgency.setNetAmountReceived(collectionAgencyInfoModel.getNetAmountReceived());
			} else {
				collectionAgency.setReason(collectionAgencyInfoModel.getReason()==null?"":collectionAgencyInfoModel.getReason());
				collectionAgency.setRemarks(collectionAgencyInfoModel.getRemarks());
			}
			collectionAgency = collectionAgencyRepo.save(collectionAgency);
			collectionAgencyInfoModel.setId(collectionAgency.getId());
			collectionAgencyInfoModel.setCreatedDate(collectionAgency.getCreatedDate());
			
			return collectionAgency != null ? collectionAgencyInfoModel : null;
		}
		return null;
	}
	
	public List<CollectionAgencyDto> fetchCollectionAgencyInformation(PartialHeader partialHeader, String claimUuid,
			boolean showWithTeam) throws Exception {
		List<CollectionAgencyDto> responseDto = new ArrayList<>();
		List<RcmCollectionAgency> collectionAgencies = null;
		CollectionAgencyDto collectionAgencyDto=null;
		if (showWithTeam) {
			collectionAgencies = collectionAgencyRepo
					.findByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(claimUuid, partialHeader.getTeamId());
		} else {
			collectionAgencies = collectionAgencyRepo.findByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		}
		if (collectionAgencies != null) {
			for(RcmCollectionAgency collectionAgency:collectionAgencies) {
				collectionAgencyDto = new CollectionAgencyDto();
				BeanUtils.copyProperties(collectionAgency, collectionAgencyDto);
				responseDto.add(collectionAgencyDto);
			}
			return responseDto;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Boolean saveRequestRebillingSection(PartialHeader partialHeader,
			RequestRebillingDto requestRebillingInfoModel, RcmClaims claim, RcmUser createdBy, RcmTeam team,
			boolean finalSubmit) throws Exception {
		RcmRequestRebiilingSection requestRebillingSection = null;
		RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
				partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		if (assign == null) {
			logger.error("claim not assigned to this user");
			// Not assigned to user
			return false;
		}
		
		if (claim != null && requestRebillingInfoModel != null && !finalSubmit) {
			String selectedCodes=null;
			if (!requestRebillingInfoModel.getRebillingServiceCodes().isEmpty()) {
				String serviceCodes[] = Arrays
						.stream(requestRebillingInfoModel.getRebillingServiceCodes().split(","))
						.filter(x -> !x.equalsIgnoreCase("Undistributed")).distinct().toArray(String[]::new);
				selectedCodes = String.join(",", serviceCodes);
				logger.info("After removing duplicate codes from RebillingServiceCodes:"+selectedCodes);				
			}
			requestRebillingSection = new RcmRequestRebiilingSection();
			requestRebillingSection.setClaim(claim);
			requestRebillingSection.setCreatedBy(createdBy);
			requestRebillingSection.setFinalSubmit(finalSubmit);
			requestRebillingSection.setTeam(team);
			requestRebillingSection.setRebillingServiceCodes(selectedCodes);//not we have both Code + Tooth
			requestRebillingSection.setReasonForRebilling(requestRebillingInfoModel.getReasonForRebilling());
			requestRebillingSection.setRebillingRequirements(requestRebillingInfoModel.getRebillingRequirements());
			requestRebillingSection.setRebillingType(requestRebillingInfoModel.getRebillingType());	
			requestRebillingSection.setRemarks(requestRebillingInfoModel.getRemarks());	
			requestRebillingSection.setUsedAI(requestRebillingSection.getUsedAI());	
			requestRebillingSection = requestRebillingSectionRepo.save(requestRebillingSection);

			// update rebilled status true in rcm_claims table

			claim.setRebilledStatus(true);
			rcmClaimRepository.save(claim);

			//String newCycleStatus = requestRebillingInfoModel.getCurrentAction();
			//Assign  Claim to BILLING  in ReBilling
			int nextTeam = RcmTeamEnum.BILLING.getId();//requestRebillingInfoModel.getTeamId();
			ClaimStatusEnum nextAction = ClaimStatusEnum.getByType(requestRebillingInfoModel.getNextAction());

			if (nextAction != null) {
				RcmOffice office = officeRepo.findByUuid(claim.getOffice().getUuid());
				String attach=null;
				if (requestRebillingInfoModel.getRebillingRequirements()!=null && requestRebillingInfoModel.getRebillingRequirements().trim().length()>0) {
					attach = Constants.ATTACH_WITH_REMARKS_REBILL;
				}
				String assignActionName = "Assign To Team";
				String claimTransfer = rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader,
						claim.getClaimUuid(), nextTeam, requestRebillingInfoModel.getRemarks(), claim, assign,
						createdBy, office,attach, ClaimStatusEnum.NEED_TO_REBILL.getType(),ClaimStatusEnum.NEED_TO_REBILL.getType(), assignActionName,
						rcmTeamRepo.findById(partialHeader.getTeamId()));
				
				rcmClaimAssignmentRepo.save(assign);
				logger.info("claim transfer response->" + claimTransfer);
			} else {
				logger.info("claim transfer response-> Wrong claim Status:" + requestRebillingInfoModel.getNextAction()
						+ " send for claim :" + claim.getClaimUuid());
			}
			return requestRebillingSection != null ? true : null;

		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public Object saveRebillingSection(PartialHeader partialHeader, RebillingDto rebillingInfoModel, RcmClaims claim,
			RcmUser createdBy, RcmTeam team, boolean finalSubmit) throws Exception {
		RcmRebillingSection rebillingSection = null;
		if (rebillingInfoModel.isRebillingStatus() && rebillingInfoModel.getSelectedRebillingServiceCodes().isEmpty()) {
			logger.error("service code or requirements are empty!");
			return null;

		}
		RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
				partialHeader.getJwtUser().getUuid(), claim.getClaimUuid(), true);
		if (assign == null) {
			logger.error("claim not assigned to this user");
			// Not assigned to user
			return false;
		}

		if (claim != null && !rebillingInfoModel.isRebillingStatus()) {
			// update rebilled status false in rcm_claims table
			claim.setRebilledStatus(false);
			rcmClaimRepository.save(claim);

		} else if (claim != null && !rebillingInfoModel.isReCeationOptionChoosen()) {
			RcmUser requestedBy = userRepo.findByUuid(rebillingInfoModel.getRequestedByUuid());
			
			String selectedServiceCodes = null;
			String selectedRequirements = null;
			if (!rebillingInfoModel.getSelectedRebillingServiceCodes().isEmpty()) {
				selectedServiceCodes = rebillingInfoModel.getSelectedRebillingServiceCodes().stream().distinct()
						.filter(str -> !str.equalsIgnoreCase("Undistributed")).collect(Collectors.joining(","));
				logger.info("after removing duplicates service code from selectedServiceCodesData->"+selectedServiceCodes);
			}
			if (rebillingInfoModel.getSelectedRebillingRequirements()!=null && !rebillingInfoModel.getSelectedRebillingRequirements().isEmpty()) {
				selectedRequirements = rebillingInfoModel.getSelectedRebillingRequirements().stream()
						.collect(Collectors.joining(","));
				logger.info("selectedrequirementsData->"+selectedRequirements);
			}

			rebillingSection = new RcmRebillingSection();
			rebillingSection.setClaim(claim);
			rebillingSection.setRemarks(rebillingInfoModel.getRebillingRemarks());
			rebillingSection.setRequestedRemarks(rebillingInfoModel.getRequestedRemarks());
			if (rebillingInfoModel.isRebillingStatus()) {
				rebillingSection.setReasonForRebilling(rebillingInfoModel.getReasonForRebilling());
				rebillingSection.setRebillingServiceCodes(selectedServiceCodes);
				rebillingSection.setRebillingRequirements(selectedRequirements);
			}
			rebillingSection.setRebilling(rebillingInfoModel.isRebillingStatus());
			rebillingSection.setCreatedBy(createdBy);
			rebillingSection.setRequestedBy(requestedBy);
			rebillingSection.setFinalSubmit(finalSubmit);
			rebillingSection.setTeam(team);
			rebillingSection.setUsedAI(rebillingInfoModel.getUsedAI());	
			rebillingSection.setReCeationOptionChoosen(rebillingInfoModel.isReCeationOptionChoosen());
			rebillingSection = rebillingSectionRepo.save(rebillingSection);

			rebillingInfoModel.setDateOfRebiiling(Constants.SDF_MYSL_DATE.format(rebillingSection.getCreatedDate()));
			rebillingInfoModel.setRequestedBy(rebillingSection.getRequestedBy().getFirstName());

			rebillingInfoModel.setUsedAI(rebillingSection.getUsedAI());	
			
			// update rebilled status false in rcm_claims table
			claim.setRebilledStatus(false);
			if (claim.getFirstRebilledDate() == null) {
				claim.setFirstRebilledDate(Timestamp.from(Instant.now()));
			}
			rcmClaimRepository.save(claim);

			// First we update rebilled status false in service_level_section as well as
			// rcm
			// claim_detail tables
			if (rebillingInfoModel.isRebillingStatus()) {
				if (!rebillingInfoModel.getOriginalServiceCodes().isEmpty()) {
					List<String> serviceCodesForStatusFalse = rebillingInfoModel.getOriginalServiceCodes().stream()
							.distinct().filter(str -> !str.equalsIgnoreCase("Undistributed"))
							.collect(Collectors.toList());
					logger.info(
							"After removing duplicate codes from originalServiceCodes:" + serviceCodesForStatusFalse);
					// remove duplicate codes and undistributed code if present
					//  now tooths are also present in The originalServiceCodes we will extract Code from this now
					//EG D1022-2,D1034-A earlier we had D1022,D1034 only
					if (serviceCodesForStatusFalse.size() > 0) {
						List<String> serviceCodesForStatusFalseOnlyCode= new ArrayList<>();
						serviceCodesForStatusFalse.stream().map(x->x.split("-")[0]).forEach(serviceCodesForStatusFalseOnlyCode::add);
						boolean isToothPresent = serviceCodesForStatusFalse.get(0).contains("-");
						List<RcmServiceLevelInformation> serviceCodesDataForServiceLevel = serviceLevelRepo
								.findServiceCodesByClaimUuidAndCodes(claim.getClaimUuid(), serviceCodesForStatusFalseOnlyCode);
						if (!serviceCodesDataForServiceLevel.isEmpty()) {
							serviceCodesDataForServiceLevel.forEach(data -> {
							if(isToothPresent) {
								if (serviceCodesForStatusFalse.contains(data.getServiceCode()+"-"+data.getTooth())) {
								data.setRebilledStatus(false);
								serviceLevelRepo.save(data);
								}
							}else {
								//For old Cases when no tooth was there.
								data.setRebilledStatus(false);
								serviceLevelRepo.save(data);
								}
							});
						}

						List<RcmClaimDetail> serviceCodesDataForClaimDetail = claimDetailRepo
								.findServiceCodesByClaimUuidAndCodes(claim.getClaimUuid(), serviceCodesForStatusFalseOnlyCode);
						if (!serviceCodesDataForClaimDetail.isEmpty()) {
							serviceCodesDataForClaimDetail.forEach(data -> {
								if(isToothPresent ) {
								  if (serviceCodesForStatusFalse.contains(data.getServiceCode()+"-"+data.getTooth())) {
									  data.setRebilledStatus(false);
									  claimDetailRepo.save(data);
									}
								}else {
									//For old Cases when no tooth was there.
									data.setRebilledStatus(false);
									claimDetailRepo.save(data);
									}
							  });
						 }
					}
				}

				// Now we update rebilled status true in service_level_section as well as rcm
				// claim_detail tables

				// remove duplicate codes and undistributed code if present
				List<String> serviceCodesForStatusTrue = rebillingInfoModel.getSelectedRebillingServiceCodes().stream().distinct()
						.filter(str -> !str.equalsIgnoreCase("Undistributed")).collect(Collectors.toList());
				logger.info("After removing duplicate codes from SelectedRebillingServiceCodes:"
						+ serviceCodesForStatusTrue);
				if (serviceCodesForStatusTrue.size() > 0) {
					List<String> serviceCodesForStatusTrueOnlyCode= new ArrayList<>();
					serviceCodesForStatusTrue.stream().map(x->x.split("-")[0]).forEach(serviceCodesForStatusTrueOnlyCode::add);
					boolean isToothPresent = serviceCodesForStatusTrue.get(0).contains("-");
				
					List<RcmServiceLevelInformation> serviceCodesData = serviceLevelRepo
							.findServiceCodesByClaimUuidAndCodes(claim.getClaimUuid(), serviceCodesForStatusTrueOnlyCode);
					if (!serviceCodesData.isEmpty()) {
						serviceCodesData.forEach(data -> {
						
						if(isToothPresent) {
							if (serviceCodesForStatusTrue.contains(data.getServiceCode()+"-"+data.getTooth())) {
								data.setRebilledStatus(true);
								serviceLevelRepo.save(data);
							}
						}else {
							//For old Cases when no tooth was there.
							data.setRebilledStatus(true);
							serviceLevelRepo.save(data);
							}
						});
					}

					List<RcmClaimDetail> serviceCodesForClaimDetail = claimDetailRepo
							.findServiceCodesByClaimUuidAndCodes(claim.getClaimUuid(), serviceCodesForStatusTrue);
					if (!serviceCodesForClaimDetail.isEmpty()) {
						serviceCodesForClaimDetail.forEach(data -> {
							
							if(isToothPresent) {
								  if (serviceCodesForStatusTrue.contains(data.getServiceCode()+"-"+data.getTooth())) {
									  data.setRebilledStatus(true);
									  claimDetailRepo.save(data);
									}
							}else {
									//For old Cases when no tooth was there.
									data.setRebilledStatus(true);
									claimDetailRepo.save(data);
						 }
						});
						
					}
				}

				// claim transfer
				String newCycleStatus = ClaimStatusEnum.Need_to_Post.getType();
				try {
					int newCycleStatusId =claim.getClaimStatusType().getId();
					newCycleStatus = ClaimStatusEnum.getById(newCycleStatusId).getType();
				}catch(Exception x){
					
					x.printStackTrace();
					
				}
				int nextTeam = RcmTeamEnum.PAYMENT_POSTING.getId();//rebillingInfoModel.getClaimTransferNextTeamId();
				ClaimStatusEnum nextAction = ClaimStatusEnum.Need_to_Post;

				if (nextAction != null) {
					RcmOffice office = officeRepo.findByUuid(claim.getOffice().getUuid());
					String assignActionName = "Assign To Team";
					String claimTransfer = rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(partialHeader,
							claim.getClaimUuid(), nextTeam, rebillingInfoModel.getRebillingRemarks(), claim, assign,
							createdBy, office, null, newCycleStatus, nextAction.getType(), assignActionName,
							rcmTeamRepo.findById(partialHeader.getTeamId()));
					rcmClaimAssignmentRepo.save(assign);
					logger.info("claim transfer response->" + claimTransfer);
					
				} else {
					logger.info("claim transfer response-> Wrong claim Status:"
							+ ClaimStatusEnum.Billed + " send for claim :" + claim.getClaimUuid());
				}

			}
		}
		return rebillingInfoModel;
	}

	public RebillingDto fetchRequestRebillingServiceCodesInformation(PartialHeader partialHeader, String claimUuid)
			throws Exception {
		//List<RcmRequestRebiilingSection> responseDtos = new ArrayList<>();
		RebillingDto responseDto = new RebillingDto();
		RcmRequestRebiilingSection requestRebillingData = null;
		requestRebillingData = requestRebillingSectionRepo.findFirstByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		if (requestRebillingData != null) {
			responseDto = new RebillingDto();
			if (StringUtils.isNoneBlank(requestRebillingData.getRebillingServiceCodes())) {

				String serviceCodes[] = requestRebillingData.getRebillingServiceCodes().split(",");
				responseDto.setOriginalServiceCodes(Arrays.asList(serviceCodes));
			}

			if (StringUtils.isNoneBlank(requestRebillingData.getRebillingRequirements())) {
				String requirements[] = requestRebillingData.getRebillingRequirements().split(",");
				responseDto.setOriginalRequirements(Arrays.asList(requirements));		
			}

			RcmUser requestedBy = userRepo.findByUuid(requestRebillingData.getCreatedBy().getUuid());
			responseDto.setRequestedBy(requestedBy.getFirstName());
			responseDto.setRequestedByUuid(requestedBy.getUuid());
			responseDto.setRequestedRemarks(requestRebillingData.getRemarks());
			responseDto.setReasonForRebilling(requestRebillingData.getReasonForRebilling());
			responseDto.setClaimTransferNextTeamId(requestRebillingData.getTeam().getId());
			responseDto.setUsedAI(requestRebillingData.getUsedAI());
			responseDto.setDateOfRebiiling(Constants.SDF_MYSL_DATE.format(requestRebillingData.getCreatedDate()));
			//return responseDto;
			
		List<RcmRequestRebiilingSection> listData = requestRebillingSectionRepo.findByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		List<RebillingDtoSub> dl= new ArrayList<>();
		RebillingDtoSub responseDtoSub=null;
		if (listData!=null) {
		for(RcmRequestRebiilingSection d:listData) {
			responseDtoSub = new RebillingDtoSub();
			if (StringUtils.isNoneBlank(d.getRebillingServiceCodes())) {

				String serviceCodes[] = d.getRebillingServiceCodes().split(",");
				responseDtoSub.setOriginalServiceCodes(Arrays.asList(serviceCodes));
			}

			if (StringUtils.isNoneBlank(d.getRebillingRequirements())) {
				String requirements[] = d.getRebillingRequirements().split(",");
				responseDtoSub.setOriginalRequirements(Arrays.asList(requirements));		
			}

			RcmUser requestedBy1 = userRepo.findByUuid(d.getCreatedBy().getUuid());
			responseDtoSub.setRequestedBy(requestedBy1.getFirstName());
			responseDtoSub.setRequestedByUuid(requestedBy1.getUuid());
			responseDtoSub.setRequestedRemarks(d.getRemarks());
			responseDtoSub.setReasonForRebilling(d.getReasonForRebilling());
			responseDtoSub.setClaimTransferNextTeamId(d.getTeam().getId());
			responseDtoSub.setDateOfRebiiling(Constants.SDF_MYSL_DATE.format(d.getCreatedDate()));
			responseDtoSub.setUsedAI(d.getUsedAI());
			dl.add(responseDtoSub);
		}
		 }
		responseDto.setAllRebillingDto(dl);
		}
		
		return responseDto;
	}
	
	public List<RebillingResponseDto> fetchRebillingInformation(PartialHeader partialHeader, String claimUuid)
			throws Exception {
		RebillingResponseDto responseDto = null;
		List<RebillingResponseDto> responseData = new ArrayList<>();
		List<RcmRebillingSection> rebillingInformation = rebillingSectionRepo
				.findByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
		if (!rebillingInformation.isEmpty()) {
			for (RcmRebillingSection data : rebillingInformation) {
				RcmUser requestedBy = userRepo.findByUuid(data.getRequestedBy().getUuid());
				responseDto = new RebillingResponseDto();
				responseDto.setRequestedBy(requestedBy.getFirstName());
				responseDto.setRequestedByUuid(requestedBy.getUuid());
				responseDto.setDateOfRebiiling(Constants.SDF_MYSL_DATE.format(data.getCreatedDate()));
				responseDto.setRequestedRemarks(data.getRequestedRemarks());		
				responseDto.setRebillingRemarks(data.getRemarks());
				responseDto.setRebillingStatus(data.isRebilling());	
				responseDto.setUsedAI(data.getUsedAI());
				responseDto.setReCeationOptionChoosen(data.isReCeationOptionChoosen());	
				responseDto.setRebillingServiceCodes(data.getRebillingServiceCodes());		
				responseDto.setRebillingRequirements(data.getRebillingRequirements());
				responseData.add(responseDto);
			}
		}
		return responseData;
	}

	public RecreateResponseDto validateRecreateClaim(PartialHeader partialHeader, ValidateCreateClaimInformationDto dto)
			throws Exception {

		List<RcmRules> rules = rcmRuleRepo
				.findByRuleTypeInAndActive(Arrays.asList(new String[] { Constants.RULE_TYPE_RCM }), 1);
		RcmRules rule324 = utilServiceImpl.getRulesFromList(rules, RuleConstants.RULE_ID_324);
		RcmRules rule325 = utilServiceImpl.getRulesFromList(rules, RuleConstants.RULE_ID_325);
		RcmRules rule326 = utilServiceImpl.getRulesFromList(rules, RuleConstants.RULE_ID_326);
		RcmRules rule327 = utilServiceImpl.getRulesFromList(rules, RuleConstants.RULE_ID_327);
		RcmRules rule328 = utilServiceImpl.getRulesFromList(rules, RuleConstants.RULE_ID_328);
		RcmRules rule329 = utilServiceImpl.getRulesFromList(rules, RuleConstants.RULE_ID_329);
		RcmRules rule330 = utilServiceImpl.getRulesFromList(rules, RuleConstants.RULE_ID_330);

		List<ValidateRecreateClaimResponseDto> data = new ArrayList<>();
		RecreateResponseDto response = new RecreateResponseDto();

		RcmClaims currentClaim = rcmClaimRepository.findByClaimUuid(dto.getCurrentClaimUuid());

		if (currentClaim == null) {
			logger.error("Invalid current claim");
			data.add(new ValidateRecreateClaimResponseDto(0, "", "Invalid current claim", Constants.FAIL));
			response.setValidationResponse(data);
			return response;
		}

		RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByAssignedToUuidAndClaimsClaimUuidAndActive(
				partialHeader.getJwtUser().getUuid(), currentClaim.getClaimUuid(), true);
		if (assign == null) {
			logger.error("claim not assigned to this user");
			//data.add(new ValidateRecreateClaimResponseDto(0, "", "<b style=\"color:red\" class=\"error-message-api\">claim is not assigned to this user</b> ", Constants.FAIL));
			//response.setValidationResponse(data);
			return null;
		}

		String currentClaimId[] = currentClaim.getClaimId().split("_");
		RcmClaims newPrimaryClaim =null;
		RcmClaims secondaryClaim =null;
		RcmOffice  office=officeRepo.findById(currentClaim.getOffice().getUuid()).get();
		if (dto.getNewClaimId()!=null && dto.getNewClaimId().trim().equals("")) {
			dto.setNewClaimId(null);
		}
		
		
		if (dto.getNewClaimId()!=null) {
			if(currentClaimId[0].equals(dto.getNewClaimId())){
				logger.error("current claim match with new claim");
				//data.add(new ValidateRecreateClaimResponseDto(0, "", "<b style=\"color:red\" class=\"error-message-api\">Current claim match with new claim</b>", Constants.FAIL));
				//response.setValidationResponse(data);
				return null;	
			}			
		newPrimaryClaim = rcmClaimRepository.findByClaimIdAndOffice(dto.getNewClaimId()+ClaimTypeEnum.P.getSuffix(),office);
        secondaryClaim = rcmClaimRepository.findByClaimIdAndOffice(dto.getNewClaimId()+ClaimTypeEnum.S.getSuffix(),office);
		}
		
		boolean isPrimary =true;
        if (("_" + currentClaimId[1]).equals(ClaimTypeEnum.S.getSuffix())) {
        	isPrimary =false;
        }
        
		//check secondary for current claim if button is secondary
		if (dto.getButtonType()!=null) {
			secondaryClaim = rcmClaimRepository.findByClaimIdAndOffice(currentClaimId[0]+ClaimTypeEnum.S.getSuffix(),office);
			data.addAll(ruleBookService.rule324(rule324, secondaryClaim));
			response.setSecondaryValid(secondaryClaim==null ? true : false);
			response.setValidationResponse(data);
			return response;
		}
		
		/*String[] clT = implDto.getClaimId().split("_");
		String claimSubTy = Constants.insuranceTypeSecondary;// May be needed latter
         */
		
		if (dto.getNewClaimId()!=null ) {
		     RcmClaims checkClaim= null;
	        List<String> serviceCodesDataForNewClaim =null;
	        if (!isPrimary) {
	        	if(secondaryClaim==null) {
	        		checkClaim = newPrimaryClaim;
		        	serviceCodesDataForNewClaim = claimDetailRepo
							.findServiceCodesWithToothByClaimUuid(newPrimaryClaim!=null?newPrimaryClaim.getClaimUuid():"");
	        	}else {
	        		checkClaim = secondaryClaim;
		        	serviceCodesDataForNewClaim = claimDetailRepo
							.findServiceCodesWithToothByClaimUuid(secondaryClaim!=null?secondaryClaim.getClaimUuid():"");
	        		
	        	}
	        }
	        else {
	        	checkClaim= newPrimaryClaim;
	        	serviceCodesDataForNewClaim = claimDetailRepo
						.findServiceCodesWithToothByClaimUuid(newPrimaryClaim!=null?newPrimaryClaim.getClaimUuid():"");
	        }
	        
	        //Primary for new Claim present or not
	        data.addAll(ruleBookService.rule329(rule329, newPrimaryClaim, dto.getNewClaimId()));
	        
			//DOS
			data.addAll(ruleBookService.rule325(rule325, checkClaim, currentClaim));
            //patientId from current claim
			data.addAll(ruleBookService.rule326(rule326, checkClaim, currentClaim));
			//match treating provider from current claim
			data.addAll(ruleBookService.rule327(rule327, checkClaim, currentClaim));
            //match provider on claim from current claim
			data.addAll(ruleBookService.rule328(rule328, checkClaim, currentClaim));
			//new claim archive status
			data.addAll(ruleBookService.rule330(rule330, checkClaim));
			
			
			response.setServiceCodesNewClaim(serviceCodesDataForNewClaim);
		}

        response.setValidationResponse(data);
		return response;
	}

	@Transactional(rollbackOn = Exception.class)
	public Object saveRecreateClaimSection(PartialHeader partialHeader,
			RecreateClaimRequestDto recreateClaimRequestInfoModel, RcmClaims currentClaim, RcmUser createdBy, RcmTeam team,
			boolean finalSubmit) {
		Boolean response = null;
		if (!(recreateClaimRequestInfoModel.getActionButtonType() == Constants.BUTTON_TYPE_RECREATE_FULL_CLAIM
				|| recreateClaimRequestInfoModel.getActionButtonType() == Constants.BUTTON_TYPE_RECREATE_PARTIAL_CLAIM
				|| recreateClaimRequestInfoModel.getActionButtonType() == Constants.BUTTON_TYPE_ATTACH_SECONDARY)) {
			logger.error("Wrong button type");
			return null;
		}

		if (currentClaim != null) {
			RcmClaims primaryClaim =null;
			RcmClaims secondaryClaim =null;
			RcmOffice  office=officeRepo.findById(currentClaim.getOffice().getUuid()).get();
			
	        String currentClaimId[] = currentClaim.getClaimId().split("_");
	        String associatedClaimId= recreateClaimRequestInfoModel.getNewClaimId();	
			boolean isPrimary =true;
	        if (("_" + currentClaimId[1]).equals(ClaimTypeEnum.S.getSuffix())) {
	        	isPrimary =false;
	        	primaryClaim = rcmClaimRepository.findByClaimIdAndOffice(currentClaimId[0]+ClaimTypeEnum.P.getSuffix(),office);
		        secondaryClaim = currentClaim;
		        associatedClaimId = recreateClaimRequestInfoModel.getNewClaimId()+ClaimTypeEnum.S.getSuffix();
	        }else {
	        	primaryClaim = currentClaim;
		        secondaryClaim = rcmClaimRepository.findByClaimIdAndOffice(currentClaimId[0]+ClaimTypeEnum.S.getSuffix(),office);
		        associatedClaimId = recreateClaimRequestInfoModel.getNewClaimId()+ClaimTypeEnum.P.getSuffix();
	        }
	        
	        //check for secondary

			if (recreateClaimRequestInfoModel.getNewClaimId() != null && isPrimary
					&& recreateClaimRequestInfoModel.getActionButtonType() == Constants.BUTTON_TYPE_ATTACH_SECONDARY) {
				if (recreateClaimRequestInfoModel.getClaimFromSheet() != null) {
					logger.info("Inside secondary claim->>>>>>>>");
					//find current claim service codes,surface and tooth details
					
					List<RcmClaimDetail>claimDetails= claimDetailRepo.findByClaimClaimUuidAndActiveTrue(primaryClaim.getClaimUuid());		
					List<String>serviceCodes=claimDetails.stream().map(x->x.getServiceCode()).collect(Collectors.toList());
					List<String>toothAndSurface=new ArrayList<>();
					
					//if service code is empty then no need to create secondary claim
					if(serviceCodes.isEmpty()) {
						logger.info("Service code is not present in current claim->>" + serviceCodes);
						return null;
					}
					
					for (RcmClaimDetail data : claimDetails) {
						if (data.getTooth() != null && !data.getTooth().trim().equals("")) {
							if (data.getSurface() != null && !data.getSurface().trim().equals("")) {
								toothAndSurface.add(data.getTooth() + "#" + data.getSurface());
							} else {
								toothAndSurface.add(data.getTooth());
							}
						}
					}
					
					for (ClaimFromSheet dtoSheet : recreateClaimRequestInfoModel.getClaimFromSheet()) {
						dtoSheet.setClientName(office.getCompany().getName());
						dtoSheet.setOfficeName(office.getName());
						dtoSheet.setSecondaryClaimSubmissionDate(Constants.SDF_MYSL_DATE.format(new Date())); //now we are inserting default date .ie.today date when we are executing recreate
						dtoSheet.setOfficeKey(String.valueOf(office.getKey()));
						dtoSheet.setServiceCodes(serviceCodes);		
						dtoSheet.setToothAndSurfaces(toothAndSurface);
						dtoSheet.setClaimId(primaryClaim.getClaimId().split(ClaimTypeEnum.P.getSuffix())[0]);
						dtoSheet.setDos(Constants.SDF_MYSL_DATE.format(primaryClaim.getDos()!=null?primaryClaim.getDos():""));
						dtoSheet.setPaitentDob(Constants.SDF_MYSL_DATE.format(primaryClaim.getPatientBirthDate()!=null?primaryClaim.getPatientBirthDate():""));
						dtoSheet.setPatientName(primaryClaim.getPatientName());	
						dtoSheet.setPatientContactNo(primaryClaim.getPatientContactNo());
						dtoSheet.setTreatingProviderName(primaryClaim.getTreatingProvider());
						dtoSheet.setAccountId(primaryClaim.getPatientId());
						List<ClaimLogDto> responseForSecondaryClaim = claimServiceImpl
								.createSecondaryClaimDataFromRecreateSection(dtoSheet,
										partialHeader.getCompany().getUuid(), primaryClaim.getOffice().getUuid(),
										createdBy, team,primaryClaim.getStatusES(),recreateClaimRequestInfoModel.getRecreateTeam());
						logger.info("ResponseForSecondaryClaim->>" + responseForSecondaryClaim);
						break;
					}

					// if claim is Present in issue_claim table then get data from this table

					List<RcmIssueClaims> isIssueClaim = rcmIssueClaimsRepo
							.findByClaimIdAndOfficeAndIsArchiveFalseAndResolvedFalse(
									recreateClaimRequestInfoModel.getNewClaimId() + ClaimTypeEnum.S.getSuffix(),
									primaryClaim.getOffice());

					IssueClaimDto dto = null;
					for (RcmIssueClaims data : isIssueClaim) {
						dto = new IssueClaimDto();
						dto.setOfficeName(data.getOffice().getName());
						BeanUtils.copyProperties(data, dto);

					}

					if (!isIssueClaim.isEmpty()) {
						logger.error("Issue Claim Status->>" + isIssueClaim);
						return dto;
					} else {
						response = this.saveRecreateClaimData(recreateClaimRequestInfoModel, primaryClaim, createdBy,
								team, finalSubmit);
						
						
						return response != null ? true : null;
					}
				}
			}
			
			// check for recreate partial claim
			if (recreateClaimRequestInfoModel.getActionButtonType() == Constants.BUTTON_TYPE_RECREATE_PARTIAL_CLAIM) {
				logger.info("Inside partial claim->>>>>>>>");
				
				if(recreateClaimRequestInfoModel.getNewClaimId().equals(currentClaimId[0])) {
					logger.error("New claim id match with current claim id");
					return "New claim id match with current claim id";
				}
				
				List<String> existingServiceCodesForNewClaim = recreateClaimRequestInfoModel
						.getExistingNewClaimServiceCodes().stream().distinct()
						.filter(str ->str!=null && !str.equalsIgnoreCase("Undistributed")).collect(Collectors.toList());
				List<String> selectedServiceCodesForExistingClaim = recreateClaimRequestInfoModel
						.getSelectedServiceCodes().stream().distinct()
						.filter(str -> str!=null && !str.equalsIgnoreCase("Undistributed")).collect(Collectors.toList());

				if (existingServiceCodesForNewClaim.isEmpty() || selectedServiceCodesForExistingClaim.isEmpty()) {
					logger.error("service codes are empty");
					return null;
				}


				if (existingServiceCodesForNewClaim.containsAll(selectedServiceCodesForExistingClaim)) {

					if (isPrimary) {
						logger.info("Inside current claim primary->>>>>>>>>>>>>>>");
						
						
						
                        //REDO
						// active false for service level table for selected service codes for current
						// claim
					    //  now tooths are also present in The originalServiceCodes we will extract Code from this now
						//EG D1022-2,D1034-A earlier we had D1022,D1034 only
						List<String> serviceCodesOnlyCode= new ArrayList<>();
						selectedServiceCodesForExistingClaim.stream().map(x->x.split("-")[0]).forEach(serviceCodesOnlyCode::add);
						boolean isToothPresent = selectedServiceCodesForExistingClaim.get(0).contains("-");
						
						List<RcmServiceLevelInformation> serviceCodesData = serviceLevelRepo
								.findServiceCodesByClaimUuidAndCodes(primaryClaim.getClaimUuid(),
										serviceCodesOnlyCode);
						if (!serviceCodesData.isEmpty()) {
							serviceCodesData.forEach(data -> {
								//data.setActive(false);
								//serviceLevelRepo.save(data);
								if(isToothPresent) {
									if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
										data.setActive(false);
										serviceLevelRepo.save(data);
									}
								}else {
									//For old Cases when no tooth was there.
									data.setActive(false);
									serviceLevelRepo.save(data);
								}
								
								
							});
						}

						// active false in claim_detail table for selected service codes for current
						// claim
						List<RcmClaimDetail> serviceCodesForClaimDetail = claimDetailRepo
								.findServiceCodesByClaimUuidAndCodes(primaryClaim.getClaimUuid(),
										serviceCodesOnlyCode);
						if (!serviceCodesForClaimDetail.isEmpty()) {
							serviceCodesForClaimDetail.forEach(data -> {
								//data.setActive(false);
								//claimDetailRepo.save(data);
								if(isToothPresent) {
									if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
										data.setActive(false);
										claimDetailRepo.save(data);
									}
								}else {
									//For old Cases when no tooth was there.
									data.setActive(false);
									claimDetailRepo.save(data);
								}
							});
						}

						logger.info("service code status change 1 to 0 success for current claim primary->>true");

						// DO SAME FOR OTHER SECONDARY
						if (secondaryClaim != null) {
							logger.info("Inside current claim primary if secondary exist->>>>>>>>>>>>>>>");
							
							List<RcmServiceLevelInformation> serviceCodesData1 = serviceLevelRepo
									.findServiceCodesByClaimUuidAndCodes(secondaryClaim.getClaimUuid(),
											serviceCodesOnlyCode);
							if (!serviceCodesData1.isEmpty()) {
								serviceCodesData1.forEach(data -> {
									
									if(isToothPresent) {
										if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
											data.setActive(false);
											serviceLevelRepo.save(data);
										}
									}else {
										//For old Cases when no tooth was there.
										data.setActive(false);
										serviceLevelRepo.save(data);
									}
								});
							}

							// active false in claim_detail table for selected service codes for current
							// claim
							List<RcmClaimDetail> serviceCodesForClaimDetail1 = claimDetailRepo
									.findServiceCodesByClaimUuidAndCodes(secondaryClaim.getClaimUuid(),
											serviceCodesOnlyCode);
							if (!serviceCodesForClaimDetail1.isEmpty()) {
								serviceCodesForClaimDetail1.forEach(data -> {
									
									if(isToothPresent) {
										if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
											data.setActive(false);
											claimDetailRepo.save(data);
										}
									}else {
										//For old Cases when no tooth was there.
										data.setActive(false);
										claimDetailRepo.save(data);
									}
								});
							}
							logger.info(
									"service code status change 1 to 0 success for current claim primary if secondary exist->>true");
						}
					} else {
						logger.info("If current claim is secondary->>>>>>>>>>>>>");
						// DO SAME FOR OTHER SECONDARY
						List<String> serviceCodesOnlyCode= new ArrayList<>();
						selectedServiceCodesForExistingClaim.stream().map(x->x.split("-")[0]).forEach(serviceCodesOnlyCode::add);
						boolean isToothPresent = selectedServiceCodesForExistingClaim.get(0).contains("-");
						
						if (secondaryClaim != null) {
							logger.info("Inside current claim secondary->>>>>>>>>>>>>>>");
							
							List<RcmServiceLevelInformation> serviceCodesData1 = serviceLevelRepo
									.findServiceCodesByClaimUuidAndCodes(secondaryClaim.getClaimUuid(),
											serviceCodesOnlyCode);
							if (!serviceCodesData1.isEmpty()) {
								serviceCodesData1.forEach(data -> {
									//data.setActive(false);
									//serviceLevelRepo.save(data);
									if(isToothPresent) {
										if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
											data.setActive(false);
											serviceLevelRepo.save(data);
										}
									}else {
										//For old Cases when no tooth was there.
										data.setActive(false);
										serviceLevelRepo.save(data);
									}
								});
							}

							// active false in claim_detail table for selected service codes for current
							// claim
							List<RcmClaimDetail> serviceCodesForClaimDetail1 = claimDetailRepo
									.findServiceCodesByClaimUuidAndCodes(secondaryClaim.getClaimUuid(),
											serviceCodesOnlyCode);
							if (!serviceCodesForClaimDetail1.isEmpty()) {
								serviceCodesForClaimDetail1.forEach(data -> {
									//data.setActive(false);
									//claimDetailRepo.save(data);
									if(isToothPresent) {
										if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
											data.setActive(false);
											claimDetailRepo.save(data);
										}
									}else {
										//For old Cases when no tooth was there.
										data.setActive(false);
										claimDetailRepo.save(data);
									}
								});
							}
							logger.info("service code status change 1 to 0 success for current claim secondary->>true");
						}

						if (primaryClaim != null) {
							logger.info("Inside current claim secondary if primary exist->>>>>>>>>>>>>>>");
							List<RcmServiceLevelInformation> serviceCodesData1 = serviceLevelRepo
									.findServiceCodesByClaimUuidAndCodes(primaryClaim.getClaimUuid(),
											serviceCodesOnlyCode);
							if (!serviceCodesData1.isEmpty()) {
								serviceCodesData1.forEach(data -> {
									//data.setActive(false);
									//serviceLevelRepo.save(data);
									if(isToothPresent) {
										if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
											data.setActive(false);
											serviceLevelRepo.save(data);
										}
									}else {
										//For old Cases when no tooth was there.
										data.setActive(false);
										serviceLevelRepo.save(data);
									}
								});
							}

							// active false in claim_detail table for selected service codes for current
							// claim
							List<RcmClaimDetail> serviceCodesForClaimDetail1 = claimDetailRepo
									.findServiceCodesByClaimUuidAndCodes(primaryClaim.getClaimUuid(),
											serviceCodesOnlyCode);
							if (!serviceCodesForClaimDetail1.isEmpty()) {
								serviceCodesForClaimDetail1.forEach(data -> {
									//data.setActive(false);
									//claimDetailRepo.save(data);
									if(isToothPresent) {
										if (selectedServiceCodesForExistingClaim.contains(data.getServiceCode()+"-"+data.getTooth())) {
											data.setActive(false);
											claimDetailRepo.save(data);
										}
									}else {
										//For old Cases when no tooth was there.
										data.setActive(false);
										claimDetailRepo.save(data);
									}
								});
							}

							logger.info(
									"service code status change 1 to 0 success for current claim secondary if primary exist->>true");
						}

					}
					//
					
				} else {
					logger.error("service code not match with cureent claim service codes");
					return null;
				}

				response = this.saveRecreateClaimData(recreateClaimRequestInfoModel, primaryClaim, createdBy, team,
						finalSubmit);
				if (secondaryClaim != null) {
					this.saveRecreateClaimData(recreateClaimRequestInfoModel, secondaryClaim, createdBy, team,
							finalSubmit);
				}
				
				//Move New Claim to New team
				
				RcmClaims associatedClaim =rcmClaimRepository.findByClaimIdAndOffice(associatedClaimId,office);
				
				
				// first we insert current claim linked_data from newClaim data
				RcmLinkedClaims linkedClaimsForCurrent = new RcmLinkedClaims();
				linkedClaimsForCurrent.setRcmClaims(currentClaim);
				linkedClaimsForCurrent.setCreatedBy(createdBy);
				linkedClaimsForCurrent.setLinkedClaims(associatedClaim);
				rcmLinkedClaimsRepo.save(linkedClaimsForCurrent);

				// second we insert new claim linked_data from current claim data
				RcmLinkedClaims linkedClaimsForNew = new RcmLinkedClaims();
				linkedClaimsForNew.setRcmClaims(associatedClaim);
				linkedClaimsForNew.setCreatedBy(createdBy);
				linkedClaimsForNew.setLinkedClaims(currentClaim);
				rcmLinkedClaimsRepo.save(linkedClaimsForNew);
				
				
				RcmTeam newTeam = rcmTeamRepo.findById(recreateClaimRequestInfoModel.getRecreateTeam());
				RcmTeam oldTeam = rcmTeamRepo.findById(associatedClaim.getCurrentTeamId().getId());
				RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByCurrentTeamIdIdAndClaimsClaimUuidAndActive(
						associatedClaim.getCurrentTeamId().getId(),associatedClaim.getClaimUuid(), true);
				associatedClaim.setCurrentTeamId(newTeam);
				if (newTeam.getId() != oldTeam.getId() && assign!=null) {
					
				//ClaimStatusEnum nextAction =ClaimStatusEnum.getByType("Need to Review");
				//associatedClaim.setCurrentStatus(nextAction.getId());
				associatedClaim.setRecreatedSection(Constants.RecreatedSection_ONE);
				//if Moving to other team Apart from  Billing then marked as billed
				if (newTeam.getId()!=RcmTeamEnum.BILLING.getId()) associatedClaim.setPending(false);
				
				//String newCycleStatus="Unbilled";
				//What about pending
				String createStatus = ClaimStatusEnum.Need_Additional_Information_For_Claim.getType();  
				String  nextAction= ClaimStatusEnum.Need_Additional_Information_For_Claim.getType(); 
				String assignActionName="Assign To Team";////Same we have in ui if change needed update that also (billing-claims.component.ts)
				  PartialHeader newPH1=new PartialHeader();
				  newPH1.setTeamId(recreateClaimRequestInfoModel.getRecreateTeam());
				  //partialHeader.setTeamId(recreateClaimRequestInfoModel.getRecreateTeam());
				   String claimTransfer=rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(newPH1, associatedClaim.getClaimUuid(),
						   newTeam.getId(), recreateClaimRequestInfoModel.getRecreationRemarks(), associatedClaim, assign, createdBy, office, null,createStatus,nextAction,assignActionName,
						   newTeam);
				   rcmClaimAssignmentRepo.save(assign);
				}
				logger.info("saveRecreateClaimData" + response);
				return response != null ? true : null;

			}

			// check for full claim

			if (recreateClaimRequestInfoModel.getActionButtonType() == Constants.BUTTON_TYPE_RECREATE_FULL_CLAIM) {
				logger.info("Inside full claim->>>>>>>>");
				RcmClaims newPrimaryClaim = null;
				RcmClaims newSecondaryClaim = null;
				RcmClaims newLinkedClaim = null;
				RcmClaims oldPrimary = null;
				if(recreateClaimRequestInfoModel.getNewClaimId().equals(currentClaimId[0])) {
					logger.error("New claim id match with current claim id");
					return "New claim id match with current claim id";
				}
				if (("_" + currentClaimId[1]).equals(ClaimTypeEnum.S.getSuffix())) {
					isPrimary = false;
					newPrimaryClaim = rcmClaimRepository.findByClaimIdAndOffice(
							recreateClaimRequestInfoModel.getNewClaimId() + ClaimTypeEnum.P.getSuffix(), office);
					newLinkedClaim = rcmClaimRepository.findByClaimIdAndOffice(
							recreateClaimRequestInfoModel.getNewClaimId() + ClaimTypeEnum.S.getSuffix(), office);
					secondaryClaim = currentClaim;
					oldPrimary = rcmClaimRepository
							.findByClaimIdAndOffice(currentClaimId[0] + ClaimTypeEnum.P.getSuffix(), office);

					// first we insert current claim linked_data from newClaim data
					RcmLinkedClaims linkedClaimsForCurrent = new RcmLinkedClaims();
					linkedClaimsForCurrent.setRcmClaims(oldPrimary);
					linkedClaimsForCurrent.setCreatedBy(createdBy);
					linkedClaimsForCurrent.setLinkedClaims(newPrimaryClaim);
					rcmLinkedClaimsRepo.save(linkedClaimsForCurrent);

					// second we insert new claim linked_data from current claim data
					RcmLinkedClaims linkedClaimsForNew = new RcmLinkedClaims();
					linkedClaimsForNew.setRcmClaims(newPrimaryClaim);
					linkedClaimsForNew.setCreatedBy(createdBy);
					linkedClaimsForNew.setLinkedClaims(oldPrimary);
					rcmLinkedClaimsRepo.save(linkedClaimsForNew);

					// now for Secondary
					if (secondaryClaim != null && newLinkedClaim != null) {
						RcmLinkedClaims linkedClaimsForCurrentS = new RcmLinkedClaims();
						linkedClaimsForCurrentS.setRcmClaims(secondaryClaim);
						linkedClaimsForCurrentS.setCreatedBy(createdBy);
						linkedClaimsForCurrentS.setLinkedClaims(newLinkedClaim);
						rcmLinkedClaimsRepo.save(linkedClaimsForCurrentS);

						// second we insert new claim linked_data from current claim data
						RcmLinkedClaims linkedClaimsForNewS = new RcmLinkedClaims();
						linkedClaimsForNewS.setRcmClaims(newLinkedClaim);
						linkedClaimsForNewS.setCreatedBy(createdBy);
						linkedClaimsForNewS.setLinkedClaims(secondaryClaim);
						rcmLinkedClaimsRepo.save(linkedClaimsForNewS);
					}
					logger.info("claim linked data status->> success");

					// now current claim get to archived

					ClaimStatusUpdate dto = new ClaimStatusUpdate();
					dto.setClaimUuid(oldPrimary.getClaimUuid());
					dto.setReason(recreateClaimRequestInfoModel.getReasonForRecreation());
					//String archiveId = new Date().getTime() + Constants.HYPHEN + Constants.ARCHIVE_PREFIX
					//		+ oldPrimary.getClaimId();
					//Archive both primary and secondary
					String archiveId = claimServiceImpl.archiveActiveClaim(dto, partialHeader);//, archiveId);
					logger.info("Archive response:" + archiveId);
					// reset rebilled_satatus false
					oldPrimary.setRebilledStatus(false);
					oldPrimary.setClaimId(archiveId);
					oldPrimary.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED);
					rcmClaimRepository.save(oldPrimary);
					response = this.saveRecreateClaimData(recreateClaimRequestInfoModel, oldPrimary, createdBy, team,
							finalSubmit);
					if (secondaryClaim != null) {
						ClaimStatusUpdate dto2 = new ClaimStatusUpdate();
						dto2.setClaimUuid(secondaryClaim.getClaimUuid());
						dto2.setReason(recreateClaimRequestInfoModel.getReasonForRecreation());
						/*String archiveIdForSecondary = new Date().getTime() + Constants.HYPHEN
								+ Constants.ARCHIVE_PREFIX + secondaryClaim.getClaimId();
						String archiveResponse2 = claimServiceImpl.archiveActiveClaim(dto, partialHeader,
								archiveIdForSecondary);
						logger.info("Archive response:" + archiveResponse2);
						*/
						// reset rebilled_satatus false
						secondaryClaim.setRebilledStatus(false);
						//secondaryClaim.setClaimId(archiveIdForSecondary);
						secondaryClaim.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED);
						rcmClaimRepository.save(secondaryClaim);
						response = this.saveRecreateClaimData(recreateClaimRequestInfoModel, secondaryClaim, createdBy,
								team, finalSubmit);
					}

					
					
					RcmTeam newTeam = rcmTeamRepo.findById(recreateClaimRequestInfoModel.getRecreateTeam());
					RcmTeam oldTeam = rcmTeamRepo.findById(newLinkedClaim.getCurrentTeamId().getId());
					RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByCurrentTeamIdIdAndClaimsClaimUuidAndActive(
							newLinkedClaim.getCurrentTeamId().getId(),newLinkedClaim.getClaimUuid(), true);
					newLinkedClaim.setCurrentTeamId(newTeam);
					if (newTeam.getId() != oldTeam.getId() && assign!=null) {
						
					//ClaimStatusEnum nextAction =ClaimStatusEnum.getByType("Need to Review");
					//associatedClaim.setCurrentStatus(nextAction.getId());
						newLinkedClaim.setRecreatedSection(Constants.RecreatedSection_ONE);
					//if Moving to other team Apart from  Billing then marked as billed
					if (newTeam.getId()!=RcmTeamEnum.BILLING.getId()) newLinkedClaim.setPending(false);
					
					//String newCycleStatus="Unbilled";
					//What about pending
					String createStatus = ClaimStatusEnum.Need_Additional_Information_For_Claim.getType();  
					String  nextAction= ClaimStatusEnum.Need_Additional_Information_For_Claim.getType(); 
					String assignActionName="Assign To Team";////Same we have in ui if change needed update that also (billing-claims.component.ts)
					  PartialHeader newPH1=new PartialHeader();
					  newPH1.setTeamId(recreateClaimRequestInfoModel.getRecreateTeam());
					  //partialHeader.setTeamId(recreateClaimRequestInfoModel.getRecreateTeam());
					   String claimTransfer=rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(newPH1, newLinkedClaim.getClaimUuid(),
							   newTeam.getId(), recreateClaimRequestInfoModel.getRecreationRemarks(), newLinkedClaim, assign, createdBy, office, null,createStatus,nextAction,assignActionName,
							   newTeam);
					   rcmClaimAssignmentRepo.save(assign);
					}
					
					logger.info("saveRecreateClaimData:" + response);

				} else {
					newPrimaryClaim = currentClaim;
					newSecondaryClaim = rcmClaimRepository.findByClaimIdAndOffice(
							recreateClaimRequestInfoModel.getNewClaimId() + ClaimTypeEnum.S.getSuffix(), office);
					newLinkedClaim = rcmClaimRepository.findByClaimIdAndOffice(
							recreateClaimRequestInfoModel.getNewClaimId() + ClaimTypeEnum.P.getSuffix(), office);
					// for current claim is primary

					// first we insert current claim linked_data from newClaim data
					RcmLinkedClaims linkedClaimsForCurrent = new RcmLinkedClaims();
					linkedClaimsForCurrent.setRcmClaims(newPrimaryClaim);
					linkedClaimsForCurrent.setCreatedBy(createdBy);
					linkedClaimsForCurrent.setLinkedClaims(newLinkedClaim);
					rcmLinkedClaimsRepo.save(linkedClaimsForCurrent);

					// second we insert new claim linked_data from current claim data
					RcmLinkedClaims linkedClaimsForNew = new RcmLinkedClaims();
					linkedClaimsForNew.setRcmClaims(newLinkedClaim);
					linkedClaimsForNew.setCreatedBy(createdBy);
					linkedClaimsForNew.setLinkedClaims(newPrimaryClaim);
					rcmLinkedClaimsRepo.save(linkedClaimsForNew);

					// now for Secondary
					if (secondaryClaim != null && newSecondaryClaim != null) {
						RcmLinkedClaims linkedClaimsForCurrentS = new RcmLinkedClaims();
						linkedClaimsForCurrentS.setRcmClaims(secondaryClaim);
						linkedClaimsForCurrentS.setCreatedBy(createdBy);
						linkedClaimsForCurrentS.setLinkedClaims(newSecondaryClaim);
						rcmLinkedClaimsRepo.save(linkedClaimsForCurrentS);

						// second we insert new claim linked_data from current claim data
						RcmLinkedClaims linkedClaimsForNewS = new RcmLinkedClaims();
						linkedClaimsForNewS.setRcmClaims(newSecondaryClaim);
						linkedClaimsForNewS.setCreatedBy(createdBy);
						linkedClaimsForNewS.setLinkedClaims(secondaryClaim);
						rcmLinkedClaimsRepo.save(linkedClaimsForNewS);
					}

					logger.info("claim linked data status->> success");

					// now current claim get to archived

					ClaimStatusUpdate dto = new ClaimStatusUpdate();
					dto.setClaimUuid(primaryClaim.getClaimUuid());
					dto.setReason(recreateClaimRequestInfoModel.getReasonForRecreation());
					//String archiveId = new Date().getTime() + Constants.HYPHEN + Constants.ARCHIVE_PREFIX
					//		+ primaryClaim.getClaimId();
					//Archive both primary and secondary
					String archiveId = claimServiceImpl.archiveActiveClaim(dto, partialHeader);
					logger.info("Archive response:" + archiveId);
					// reset rebilled_satatus false
					primaryClaim.setRebilledStatus(false);
					primaryClaim.setClaimId(archiveId);
					primaryClaim.setCurrentState(Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED);
					rcmClaimRepository.save(primaryClaim);
					response = this.saveRecreateClaimData(recreateClaimRequestInfoModel, primaryClaim, createdBy, team,
							finalSubmit);
					if (secondaryClaim != null) {
						ClaimStatusUpdate dto2 = new ClaimStatusUpdate();
						dto2.setClaimUuid(secondaryClaim.getClaimUuid());
						dto2.setReason(recreateClaimRequestInfoModel.getReasonForRecreation());
						//Already done using above 
						//String archiveIdForSecondary = new Date().getTime() + Constants.HYPHEN
						//		+ Constants.ARCHIVE_PREFIX + secondaryClaim.getClaimId();
						//String archiveId2 = claimServiceImpl.archiveActiveClaim(dto, partialHeader);//,archiveIdForSecondary);
						//logger.info("Archive response:" + archiveId2);
						// reset rebilled_satatus false
						secondaryClaim.setRebilledStatus(false);
						rcmClaimRepository.save(secondaryClaim);
						response = this.saveRecreateClaimData(recreateClaimRequestInfoModel, secondaryClaim, createdBy,
								team, finalSubmit);
					}

					RcmTeam newTeam = rcmTeamRepo.findById(recreateClaimRequestInfoModel.getRecreateTeam());
					RcmTeam oldTeam = rcmTeamRepo.findById(newLinkedClaim.getCurrentTeamId().getId());
					RcmClaimAssignment assign = rcmClaimAssignmentRepo.findByCurrentTeamIdIdAndClaimsClaimUuidAndActive(
							newLinkedClaim.getCurrentTeamId().getId(),newLinkedClaim.getClaimUuid(), true);
					newLinkedClaim.setCurrentTeamId(newTeam);
					if (newTeam.getId() != oldTeam.getId() && assign!=null) {
						
					//ClaimStatusEnum nextAction =ClaimStatusEnum.getByType("Need to Review");
					//associatedClaim.setCurrentStatus(nextAction.getId());
						newLinkedClaim.setRecreatedSection(Constants.RecreatedSection_ONE);
					//if Moving to other team Apart from  Billing then marked as billed
					if (newTeam.getId()!=RcmTeamEnum.BILLING.getId()) newLinkedClaim.setPending(false);
					
					//String newCycleStatus="Unbilled";
					//What about pending
					String createStatus = ClaimStatusEnum.Need_Additional_Information_For_Claim.getType();  
					String  nextAction= ClaimStatusEnum.Need_Additional_Information_For_Claim.getType(); 
					String assignActionName="Assign To Team";////Same we have in ui if change needed update that also (billing-claims.component.ts)
					  PartialHeader newPH1=new PartialHeader();
					  newPH1.setTeamId(recreateClaimRequestInfoModel.getRecreateTeam());
					  //partialHeader.setTeamId(recreateClaimRequestInfoModel.getRecreateTeam());
					   String claimTransfer=rcmClaimLogServiceImpl.assignClaimToOtherTeamWithRemarkCommon(newPH1, newLinkedClaim.getClaimUuid(),
							   newTeam.getId(), recreateClaimRequestInfoModel.getRecreationRemarks(), newLinkedClaim, assign, createdBy, office, null,createStatus,nextAction,assignActionName,
							   newTeam);
					   rcmClaimAssignmentRepo.save(assign);
					}
					logger.info("saveRecreateClaimData:" + response);

				}
				return response != null ? true : null;
			}
		}

		return null;
	}

	private Boolean saveRecreateClaimData(RecreateClaimRequestDto recreateClaimRequestInfoModel, RcmClaims claim,
			RcmUser createdBy, RcmTeam team, boolean finalSubmit) {
		RcmRecreateClaim recreateClaim = null;
		RcmRebilledClaimValidationRemark validationRemarks = null;
		Boolean response = null;
		if (claim!=null) {
			recreateClaim = new RcmRecreateClaim();
			recreateClaim.setClaim(claim);
			recreateClaim.setReasonForRecreation(recreateClaimRequestInfoModel.getReasonForRecreation());
			recreateClaim.setRemarksRecreation(recreateClaimRequestInfoModel.getRecreationRemarks());
			recreateClaim.setNewClaimId(recreateClaimRequestInfoModel.getNewClaimId());
			recreateClaim.setCreatedBy(createdBy);
			recreateClaim.setFinalSubmit(finalSubmit);
			recreateClaim.setTeam(team);
			recreateClaim.setButtonType(recreateClaimRequestInfoModel.getActionButtonType());
			recreateClaim = rcmRecreateClaimRepo.save(recreateClaim);
			response = recreateClaim != null ? true : null;
			logger.info("Data insert into recreate claim table:" + response);
			// if recreationOption chosen is true then we save rebilling remarks and reason

			if (recreateClaim!=null && recreateClaimRequestInfoModel.isReCeationOptionChoosen()) {
				RebillingResponseDto rebillingResponseDto = recreateClaimRequestInfoModel.getRebillingResponseDto();
				if (rebillingResponseDto!=null) {
					RcmUser requestedBy = userRepo.findByUuid(rebillingResponseDto.getRequestedByUuid());
					RcmRebillingSection rebillingSection = new RcmRebillingSection();
					rebillingSection.setClaim(claim);
					rebillingSection.setRemarks(rebillingResponseDto.getRebillingRemarks());
					rebillingSection.setReasonForRebilling(rebillingResponseDto.getReasonForRebilling());
					rebillingSection.setCreatedBy(createdBy);
					rebillingSection.setRequestedBy(requestedBy);
					rebillingSection.setFinalSubmit(finalSubmit);
					rebillingSection.setTeam(team);
					rebillingSection.setReCeationOptionChoosen(recreateClaimRequestInfoModel.isReCeationOptionChoosen());
					rebillingSection = rebillingSectionRepo.save(rebillingSection);
					response = rebillingSection != null ? true : null;
					logger.info("Data insert into rebillingSection table:" + response);
				}
			}
			if (recreateClaim!=null && recreateClaimRequestInfoModel
					.getActionButtonType() == Constants.BUTTON_TYPE_RECREATE_FULL_CLAIM) {
				List<ValidationRuleRemarksDto> validationDto = recreateClaimRequestInfoModel.getValidationRuleRemarks();
				List<RcmRebilledClaimValidationRemark> remarksData = new ArrayList<>();
				if (!validationDto.isEmpty()) {
					for (ValidationRuleRemarksDto dto : validationDto) {
						validationRemarks = new RcmRebilledClaimValidationRemark();
						validationRemarks.setClaim(claim);
						validationRemarks.setCreatedBy(createdBy);
						validationRemarks.setTeam(team);
						validationRemarks.setRuleId(dto.getRuleId());
						validationRemarks.setRemark(dto.getRemarks());
						validationRemarks.setMessage(dto.getMessage());	
						validationRemarks.setMessageType(dto.getMessageType());
						remarksData.add(validationRemarks);
					}
					List<RcmRebilledClaimValidationRemark> data = rcmRecreationValidationRepo.saveAll(remarksData);
					response = !data.isEmpty() ? true : null;
					logger.info("Data insert into validationRemarks table:" + response);
				}
			}
			return response != null ? true : null;
		}

		return null;
	}

//	@Transactional(rollbackOn = Exception.class)
//	public Boolean saveNeedToCallInsuranceSection(NeedToCallInsuranceDto needToCallInfoModel, RcmClaims claim,
//			RcmUser createdBy, RcmTeam team, boolean finalSubmit) throws Exception {
//		NeedToCallInsurance needToCallInsurance = null;
//		if (claim != null) {
//			RcmTeam teamToCall = rcmTeamRepo.findById(needToCallInfoModel.getTeamToCall());
//			if (teamToCall == null)
//				return null;
//			needToCallInsurance = new NeedToCallInsurance();
//			needToCallInsurance.setClaim(claim);
//			needToCallInsurance.setDateOfCalling(Constants.SDF_MYSL_DATE.parse(needToCallInfoModel.getDateOfCalling()));
//			needToCallInsurance.setReasonOfCalling(needToCallInfoModel.getReasonOfCalling());
//			needToCallInsurance.setRemarks(needToCallInfoModel.getRemarks());
//			needToCallInsurance.setTeamToCall(teamToCall);
//			needToCallInsurance.setCreatedBy(createdBy);
//			needToCallInsurance.setFinalSubmit(finalSubmit);
//			needToCallInsurance.setTeamId(team);
//			needToCallInsurance = needToCallRepo.save(needToCallInsurance);
//			return needToCallInsurance != null ? true : null;
//		}
//		return null;
//	}
//
//	public NeedToCallInsuranceDto fetchNeedToCallInformation(PartialHeader partialHeader, String claimUuid,
//			boolean showWithTeam) throws Exception {
//		NeedToCallInsuranceDto responseDto = null;
//		NeedToCallInsurance needToCallInsurance = null;
//		if (showWithTeam) {
//			needToCallInsurance = needToCallRepo.findFirstByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(claimUuid,
//					partialHeader.getTeamId());
//		} else {
//			needToCallInsurance = needToCallRepo.findFirstByClaimClaimUuidOrderByCreatedDateDesc(claimUuid);
//		}
//		if (needToCallInsurance != null) {
//			responseDto = new NeedToCallInsuranceDto();
//			responseDto.setTeamToCall(needToCallInsurance.getTeamToCall().getId());
//			responseDto.setDateOfCalling(Constants.SDF_MYSL_DATE.format((needToCallInsurance.getDateOfCalling())));
//			BeanUtils.copyProperties(needToCallInsurance, responseDto);
//			return responseDto;
//		}
//		return null;
//	}
}
