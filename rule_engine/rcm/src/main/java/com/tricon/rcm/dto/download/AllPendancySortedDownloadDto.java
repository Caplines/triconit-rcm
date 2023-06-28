package com.tricon.rcm.dto.download;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.tricon.rcm.pdfDto.AllPendancyPdfDto;
import com.tricon.rcm.pdfDto.TotalCount;
import lombok.Data;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class AllPendancySortedDownloadDto {

	@XmlElementWrapper
	@XmlElement(name = "data")
	private List<AllPendancyPdfDto> data;
	private String fileName;
	private String clientName;
	private String tabSwitch;
	private String currentTeamName;
	private List<TotalCount> sortedTotalCount;
	private int currentTeamId;
	private List<String> teamsData;
}
