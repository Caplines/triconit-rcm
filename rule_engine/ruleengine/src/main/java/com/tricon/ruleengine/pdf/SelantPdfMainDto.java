package com.tricon.ruleengine.pdf;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class SelantPdfMainDto {
	
	
    @XmlElementWrapper
    @XmlElement(name="dto2") 
	List<SelantPdfPatDto> dto;

    @XmlElementWrapper
    @XmlElement(name="dto68") 
	List<SelantPdfPatDto> dto68;

    @XmlElementWrapper
    @XmlElement(name="dto73") 
	List<SelantPdfPatDto> dto73;

	public List<SelantPdfPatDto> getDto() {
		return dto;
	}

	public void setDto(List<SelantPdfPatDto> dto) {
		this.dto = dto;
	}

	public List<SelantPdfPatDto> getDto68() {
		return dto68;
	}

	public void setDto68(List<SelantPdfPatDto> dto68) {
		this.dto68 = dto68;
	}

	public List<SelantPdfPatDto> getDto73() {
		return dto73;
	}

	public void setDto73(List<SelantPdfPatDto> dto73) {
		this.dto73 = dto73;
	}
    
    
    

}
