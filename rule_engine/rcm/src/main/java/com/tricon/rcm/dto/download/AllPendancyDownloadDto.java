package com.tricon.rcm.dto.download;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.tricon.rcm.dto.PendencyWithOfficeOnlyDto;
import com.tricon.rcm.pdfDto.TotalCount;

import lombok.Data;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class AllPendancyDownloadDto {
	
	@XmlElementWrapper
	@XmlElement(name = "data")
	List<PendencyWithOfficeOnlyDto> data;
	String fileName;
	String clientName;
	String tabSwitch;
	private String currentTeamName;
	List<TotalCount> totalCount;
	private int currentTeamId;
}
