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
	private List<FreshClaimDataImplDto> data;
	private List<OtherTeamsRemarks>otherTeamsRemark;
	private List<ClaimRules>claimRules;
	private ServiceLevelCodeDataModel serviceLevelCodeManual;
	private List<RuleEngineReport>ruleEngineReport;
	private SubmissionDto claimSubmissionDto;
	private CountA countA;
	private CountAS countAS;
	private Count count;
	private String fileName;
	private String clientName;
	private int teamId;
	private boolean relatedTo_300;
}
