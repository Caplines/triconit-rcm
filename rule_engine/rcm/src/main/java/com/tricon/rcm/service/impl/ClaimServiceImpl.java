package com.tricon.rcm.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Collections2;
import com.tricon.rcm.db.entity.RcmClaimLog;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmMappingTable;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimFromSheet;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.InsuranceNameTypeDto;
import com.tricon.rcm.dto.RcmClaimMainRootDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.customquery.FreshClaimDetailsDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
import com.tricon.rcm.jpa.repository.RcmClaimLogRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimStatusTypeRepo;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.jpa.repository.RcmInsuranceRepo;
import com.tricon.rcm.jpa.repository.RcmMappingTableRepo;
import com.tricon.rcm.jpa.repository.RcmOfficeRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.ConnectAndReadSheets;
import com.tricon.rcm.util.Constants;

@Service
public class ClaimServiceImpl {

	@Autowired
	RuleEngineService ruleEngineService;

	@Autowired
	RcmMappingTableRepo rcmMappingTableRepo;

	@Autowired
	RcmCompanyRepo rcmCompanyRepo;

	@Autowired
	RcmTeamRepo rcmTeamRepo;

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@Autowired
	RcmOfficeRepository officeRepo;

	@Autowired
	RcmInsuranceRepo insuranceRepo;

	@Autowired
	RcmClaimRepository rcmClaimRepository;

	@Autowired
	RcmClaimStatusTypeRepo rcmClaimStatusTypeRepo;

	@Autowired
	CommonClaimServiceImpl commonClaimServiceImpl;

	private final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public HashMap<String, String> pullClaimFromSource(ClaimSourceDto dto, RcmUser user) {
		// go to Rule Engine.
		HashMap<String, String> messages = new HashMap<>();
		if (dto.getOfficeuuids() == null) {

			ruleEngineService.pullAndSaveRemoteLiteData(dto, user);
			ruleEngineService.pullAndSaveInsuranceFromRE(dto, user);
			try {
				if (dto.getSource().equals(ClaimSourceEnum.EAGLESOFT.toString())) {
					messages.put(dto.getOfficeuuid(), ruleEngineService.pullAndSaveClaimFromRE(dto, user));
				} else {
					messages.put(dto.getOfficeuuid(), pullAndSaveClaimFromSheet(dto, user));
				}
			} catch (Exception error) {

			}
		} else {
			// Loop through
			for (String dtoOff : dto.getOfficeuuids()) {
				ClaimSourceDto d = new ClaimSourceDto();
				BeanUtils.copyProperties(dto, d);
				d.setOfficeuuid(dtoOff);
				ruleEngineService.pullAndSaveRemoteLiteData(d, user);
				ruleEngineService.pullAndSaveInsuranceFromRE(d, user);
				try {
					if (d.getSource().equals(ClaimSourceEnum.EAGLESOFT.toString())) {
						messages.put(d.getOfficeuuid(), ruleEngineService.pullAndSaveClaimFromRE(d, user));
					} else {
						messages.put(dto.getOfficeuuid(), pullAndSaveClaimFromSheet(d, user));
					}
				} catch (Exception error) {

				}
			}

		}
		return messages;
	}
	
	
	/**
	 * Service For Billing Pendency Dashboard (Get Fresh Claims Count Details)
	 * @return
	 */
	public List<FreshClaimDetailsDto> fetchFreshClaimDetails() {
		return rcmClaimRepository.fetchFreshClaimDetails();
	}

	public String pullAndSaveClaimFromSheet(ClaimSourceDto dto, RcmUser user) {

		logger.info(" In pullClaimFromSheet");
		String success = "";
		RcmClaims claims = null, oldClaims = null;
		RcmMappingTable table = rcmMappingTableRepo.findByName(Constants.RCM_MAPPING_RCM_DATABASE);
		List<ClaimFromSheet> li = null;
		try {
			li = ConnectAndReadSheets.readClaimsFromGSheet(table.getGoogleSheetId(), table.getGoogleSheetSubName(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
			RcmOffice office = null;
			if (li != null) {

				List<RcmOffice> offices = officeRepo.getByCompany(rcmCompanyRepo.findByName(Constants.COMPANY_NAME));
				RcmClaimStatusType systemStatus = rcmClaimStatusTypeRepo.findByStatus(Constants.billingClaim);
				for (ClaimFromSheet ins : li) {
					Collection<RcmOffice> ruleGen = Collections2.filter(offices,
							sh -> sh.getName().equals(ins.getOfficeName()));
					office = ruleGen.iterator().next();
					claims = rcmClaimRepository.findByClaimIdAndOffice(Integer.parseInt(ins.getClaimId()), office);
					if (claims == null) {
						claims = new RcmClaims();
						ClaimUtil.createClaimFromESData(claims, office, ins,
								rcmTeamRepo.findByNameId(RcmTeamEnum.SYSYEM.toString()), user,
								insuranceRepo.findByInsuranceIdAndOffice(ins.getInsuranceCompanyName(), office),
								insuranceRepo.findByInsuranceIdAndOffice(ins.getInsuranceCompanyName(), office),
								systemStatus);
						rcmClaimRepository.save(claims);
					}
				}
				RcmClaimLog log = new RcmClaimLog();
				// commonClaimServiceImpl.saveClaimLog(log, user, office, dto.getSource(), 1, newClaimCt);
			}
		} catch (Exception n) {
			logger.error("Error in Fetching Claims From Sheet.. ");
			logger.error(n.getMessage());
			success = n.getMessage();
		}

		return success;
	}

	/*public void saveClaimLog(RcmClaimLog log, RcmUser user, RcmOffice off, String source, int logStatus,
			int newClaimCt) {
		log.setCreatedBy(user);
		log.setOffice(off);
		log.setSource(source);
		log.setStatus(logStatus);
		log.setNewClaimsCount(newClaimCt);
		commonClaimServiceImpl.save(log);
	}*/

}
