package com.tricon.ruleengine.dto.scrapping;

import java.util.ArrayList;
import java.util.List;

import com.tricon.ruleengine.model.sheet.MCNADentaSheet;

/**
 * 
 * @author Deepak.Dogra
 *
 */
public class EligibilityDto {

	private String name;
	private String eligible;
	private String copay;
	private String benefitRemaining;
	private String employerName;
	private String providerName;
	private String providerSame;// yes //No //Not Sure 3 values
	private List<HistoryDto> historyList = new ArrayList<>();
	private String message;
	private MCNADentaSheet mcnaSheet;
	
	

	/*
	public EligibilityDto(String name, String eligible, String copay, String benefitRemaining, String employerName,
			String providerName, String providerSame, List<HistoryDto> historyList) {
		super();
		this.name = name;
		this.eligible = eligible;
		this.copay = copay;
		this.benefitRemaining = benefitRemaining;
		this.employerName = employerName;
		this.providerName = providerName;
		this.providerSame = providerSame;
		this.historyList = historyList;
	}
    */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEligible() {
		return eligible;
	}

	public void setEligible(String eligible) {
		this.eligible = eligible;
	}

	public String getCopay() {
		return copay;
	}

	public void setCopay(String copay) {
		this.copay = copay;
	}

	public String getBenefitRemaining() {
		return benefitRemaining;
	}

	public void setBenefitRemaining(String benefitRemaining) {
		this.benefitRemaining = benefitRemaining;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderSame() {
		return providerSame;
	}

	public void setProviderSame(String providerSame) {
		this.providerSame = providerSame;
	}

	public List<HistoryDto> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(List<HistoryDto> historyList) {
		this.historyList = historyList;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MCNADentaSheet getMcnaSheet() {
		return mcnaSheet;
	}

	public void setMcnaSheet(MCNADentaSheet mcnaSheet) {
		this.mcnaSheet = mcnaSheet;
	}
	
	

}
