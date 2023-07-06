package com.tricon.rcm.dto.download;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tricon.rcm.pdfDto.IvfPdfDto;

import lombok.Data;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class IvfDownloadDto {
	private IvfPdfDto data;
	private String fileName;
	private String claimUuid;
}
