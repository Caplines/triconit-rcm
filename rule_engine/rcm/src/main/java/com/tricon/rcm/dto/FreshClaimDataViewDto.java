package com.tricon.rcm.dto;

import java.util.Date;

import com.tricon.rcm.dto.customquery.FreshClaimDataDto;

import lombok.Data;

@Data
public class FreshClaimDataViewDto {
	
	String OfficeName;
	String uuid;
	String claimId;
	String patientId;
	Date dos;
	String patientName;
	String statusType;
	String primaryInsurance;
	String secondaryInsurance;
	String prName;
	String secName;
	String lastTeam;
	int claimAge;
	String timelyFilingLimitData;
	float billedAmount;
	float primTotal;
	float secTotal;
	Float primeSecSubmittedTotal;
	Integer attachmentCount;
	String  lastTeamRemark;
	Date pendingSince;

	String statusESUpdated;
	String statusES;
	String nextAction;
	Date followUpDate;
	Float dueBalance;
	Date updatedDate;
	String providerSpeciality;
	String claimStatus;
	boolean claimTypeStatus;
	String assignedToFName;
	String assignedToLName;
	String assignedTo;
	String assignedToTeam;
	int rebilledStatus;
	
	public String getAssignedTo() {
		String name="";
		if (assignedToFName!=null)name =name + assignedToFName;
		if (assignedToLName!=null)name =name +" "+ assignedToLName;
		return name;
	}
	
	
}
