package com.tricon.rcm.dto.download;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.tricon.rcm.dto.ProductionDispositionWiseDto;
import com.tricon.rcm.pdfDto.AgingPdfDto;
import com.tricon.rcm.pdfDto.CdpPdfDto;
import com.tricon.rcm.pdfDto.PatientStatementPdfDto;
import com.tricon.rcm.pdfDto.ProductionPdfDto;
import lombok.Data;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class ProductionDownloadDto {
	
	@XmlElementWrapper
	@XmlElement(name = "data")
	private List<ProductionPdfDto> data;
	private List<ProductionDispositionWiseDto>patientCalling;
	private List<PatientStatementPdfDto>patientStatement;
	private List<ProductionPdfDto>paymentPosting;
	private AgingPdfDto agingPdfDto;
	private CdpPdfDto cdpPdfDto;
	private String fileName;
	private String clientName;
	private String currentTeamName;
	private String tabSwitchForAging;
	private String tabSwitchForCDP;
}
