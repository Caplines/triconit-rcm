package com.tricon.rcm.dto.download;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.tricon.rcm.pdfDto.TreatmentPlanPdfDto;

import lombok.Data;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class TreatmentPlanDownloadDto {
	
	@XmlElementWrapper
	@XmlElement(name = "data")
	private List<TreatmentPlanPdfDto> data;
	private double fee=0;
	private double ins=0;
	private double pat=0;
	private String fileName;
	private String claimUuid;

}
