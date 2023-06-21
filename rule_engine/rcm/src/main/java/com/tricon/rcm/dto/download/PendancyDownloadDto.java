package com.tricon.rcm.dto.download;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.tricon.rcm.pdfDto.PendancyPdfDto;

import lombok.Data;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class PendancyDownloadDto {
	
		@XmlElementWrapper
		@XmlElement(name="data") 
		List<PendancyPdfDto> data;
		String fileName;
		String clientName;
		int totalCount;
		int totalRemLiteReject;
		int totalcountAndRemLiteReject;
	
}
