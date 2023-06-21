package com.tricon.rcm.dto.download;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.tricon.rcm.pdfDto.IssueClaimPdfDto;

import lombok.Data;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class IssueClaimDownloadDto {
	
	@XmlElementWrapper
	@XmlElement(name="data") 
	List<IssueClaimPdfDto> data;
	String fileName;
	String clientName;
	int issueClaimCounts;
}
