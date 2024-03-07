package com.tricon.rcm.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmClaimStatusType;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.db.entity.UserAssignOffice;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.jpa.repository.RcmClaimAssignmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimAttachmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmClaimStatusTypeRepo;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.jpa.repository.UserAssignOfficeRepo;
import com.tricon.rcm.util.ClaimUtil;
import com.tricon.rcm.util.Constants;

@Service
public class RcmClaimLogServiceImpl {

	@Autowired
	RcmTeamRepo rcmTeamRepo;
	
	@Autowired
	RcmClaimAssignmentRepo rcmClaimAssignmentRepo;
	
	@Autowired
	UserAssignOfficeRepo userAssignOfficeRepo;
	
	@Autowired
	RcmClaimStatusTypeRepo rcmClaimStatusTypeRepo;
	
	@Autowired
	RcmClaimAttachmentRepo attachmentRepo;
	
	@Autowired
	ClaimCycleServiceImpl claimCycleService;
	
	@Autowired
	RcmClaimRepository rcmClaimRepository;
	
	/**
	 * Assign Claim To Other Team
	 * @param partialHeader
	 * @param dto
	 * @param claim
	 * @param assign
	 * @param user
	 * @param office
	 * @return
	 */
	public String assignClaimToOtherTeamWithRemarkCommon(PartialHeader partialHeader,String claimUuid,
			int assignToTeam,String assignToComment,RcmClaims claim,
			RcmClaimAssignment assign,RcmUser user,RcmOffice office,String attachmentsWithRemarks,String newCycleStatus,String nextAction) {
          
		if (!claim.isPending() && claim.getCurrentStatus()==0) {
			
			return "Already Submitted";
		}
		
		if (claim.getCurrentState() ==Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED) {
			return "Claim is Archived";
		}
		
		  RcmTeam assignTeam = rcmTeamRepo.findById(assignToTeam);
		  RcmTeam lastTeam = rcmTeamRepo.findById(partialHeader.getTeamId());
		  RcmTeam oldTeam = assign.getCurrentTeamId();
		  RcmUser oldRcmUser = assign.getAssignedTo();
		  
		  claim.setUpdatedBy(user);
		  claim.setCurrentTeamId(assignTeam);
		  claim.setLastWorkTeamId(lastTeam);
		  //Edit Assignment Data
		  assign.setActive(false);
		  //assign.setCommentAssignedBy(Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+ ":"+assignTeam.getName());
		  assign.setUpdatedBy(user);
		  assign.setUpdatedDate(new Date());
		  
		  rcmClaimAssignmentRepo.save(assign);
		  //Assignment Table
		  UserAssignOffice assignedUser = userAssignOfficeRepo
					.findByOfficeUuidAndTeamId(office.getUuid(), assignTeam.getId());
		  if (assignedUser!=null) {
			  RcmClaimAssignment rcmAssigment = new RcmClaimAssignment();
			  //make New Entry
			  RcmTeam assignedTeam  = rcmTeamRepo.findById(assignTeam.getId());
			  RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo
						.findByStatus(ClaimStatusEnum.Billing.getType());
			  
				rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						assignedUser.getUser(), claimUuid, claim,
						assignToComment, systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");
				rcmAssigment.setCurrentTeamId(oldTeam); 
				rcmAssigment.setAssignedTo(oldRcmUser);
				rcmAssigment.setActive(false);
				
				
				// save attachment-with-remarks(yes/no)
				if (attachmentsWithRemarks!=null && attachmentsWithRemarks.equals(Constants.ATTACH_WITH_REMARKS)){
					int existingAttachmentCounts=attachmentRepo.attachmentCountOfUserUuid(claimUuid, user.getUuid());
					if(existingAttachmentCounts>0) {
					rcmAssigment.setAttachmentWithRemarks(Constants.ATTACHMENT_WITH_REMARKS);}
				}
				
				rcmClaimAssignmentRepo.save(rcmAssigment);
				
				rcmAssigment= new RcmClaimAssignment();
				rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						assignedUser.getUser(), claimUuid, claim,
						"", systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");

				rcmClaimAssignmentRepo.save(rcmAssigment);
				claimCycleService.createNewClaimCycleWithOldStatus(claim,assignedTeam,assignedUser.getUser(),newCycleStatus,nextAction);
				
				
		  }else {
			  RcmTeam assignedTeam  = rcmTeamRepo.findById(assignTeam.getId());
			  RcmClaimStatusType systemStatusBilling = rcmClaimStatusTypeRepo
						.findByStatus(ClaimStatusEnum.Billing.getType());
			  RcmClaimAssignment rcmAssigment = new RcmClaimAssignment();
			  //make New Entry
			  rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						null, claimUuid, claim,
						assignToComment, systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");
			  rcmAssigment.setCurrentTeamId(oldTeam);
			  rcmAssigment.setAssignedTo(oldRcmUser);
			  rcmAssigment.setActive(false);
			  rcmClaimAssignmentRepo.save(rcmAssigment);
				
			rcmAssigment= new RcmClaimAssignment();
			  
			  
			  
				rcmAssigment = ClaimUtil.createAssginmentData(rcmAssigment, user,
						null, claimUuid, claim,
						"", systemStatusBilling,assignedTeam,
						Constants.SYSTEM_TRANSFER_TO_TEAM_COMMENT+"( From "+partialHeader.getTeamId()+" to "+assignToTeam +")");
				rcmClaimAssignmentRepo.save(rcmAssigment);
				claimCycleService.createNewClaimCycleWithOldStatus(claim,assignedTeam,user,newCycleStatus,nextAction);
				
		  }

		  //claim.set
			claim.setUpdatedDate(new Date());
		    if (newCycleStatus != null) {
		    	ClaimStatusEnum sta= ClaimStatusEnum.getByType(newCycleStatus);
		    	if (sta!=null) {
		    		claim.setCurrentStatus(sta.getId());
		    	}
		    }
		    if (nextAction != null) {
		    	ClaimStatusEnum act= ClaimStatusEnum.getByType(nextAction);
		    	if (act!=null) {
		    		claim.setNextAction(act.getId());
		    	}
		    }
			rcmClaimRepository.save(claim);
			return "OtherTeam";
		
	}
}
