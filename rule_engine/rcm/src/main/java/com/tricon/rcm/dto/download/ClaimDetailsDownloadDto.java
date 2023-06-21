package com.tricon.rcm.dto.download;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.pdfDto.ClaimRules;
import com.tricon.rcm.pdfDto.Count;
import com.tricon.rcm.pdfDto.CountA;
import com.tricon.rcm.pdfDto.CountAS;
import com.tricon.rcm.pdfDto.OtherTeamsRemarks;
import com.tricon.rcm.pdfDto.RuleEngineReport;
import com.tricon.rcm.pdfDto.ServiceLevelCodeDataModel;
import com.tricon.rcm.pdfDto.SubmissionDto;

import lombok.Data;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class ClaimDetailsDownloadDto {
	
	@XmlElementWrapper
	@XmlElement(name = "data")
	List<FreshClaimDataImplDto> data;
	List<OtherTeamsRemarks>otherTeamsRemark;
	List<ClaimRules>claimRules;
	ServiceLevelCodeDataModel serviceLevelCodeManual;
	List<RuleEngineReport>ruleEngineReport;
	SubmissionDto claimSubmissionDto;
	CountA countA;
	CountAS countAS;
	Count count;
	String fileName;
	String clientName;
	int teamId;
	boolean relatedTo_300;
}
