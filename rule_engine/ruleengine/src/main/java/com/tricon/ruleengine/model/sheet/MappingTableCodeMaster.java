package com.tricon.ruleengine.model.sheet;

/**
 * 
 * @author Deepak.Dogra
 *
 */
public class MappingTableCodeMaster {

	//Note Start from Zero as Sheet first Column is Empty
	private String serviceCodes;
	private String serviceCodeCategories;// for Waiting Period (Preventative/Diagnostic/Basic/Major/Adjunctive/Ortho)";
	private String coveredUnderMedical;
	private String downgrading;
	private String toothNo;// for Downgrading;
	private String fillingToothNo;//. Mapping;
	private String isFreqencyLimitationApplicable;//Yes/No)";
	private String isMissingToothClauseApplicable;//(Yes/No)"; 
	private String isBundlingApplicable;//?	(Yes/No)";
	private String whatAdditionalInformation;//n is needed?; 
	private String isPreAuthNeeded;//0?
	private String isAgeLimitAapplicable;//? (Yes/No)
	
	
	public MappingTableCodeMaster(String serviceCodes, String serviceCodeCategories, String coveredUnderMedical,
			String downgrading, String toothNo, String fillingToothNo, String isFreqencyLimitationApplicable,
			String isMissingToothClauseApplicable, String isBundlingApplicable, String whatAdditionalInformation,
			String isPreAuthNeeded, String isAgeLimitAapplicable) {
		super();
		this.serviceCodes = serviceCodes;
		this.serviceCodeCategories = serviceCodeCategories;
		this.coveredUnderMedical = coveredUnderMedical;
		this.downgrading = downgrading;
		this.toothNo = toothNo;
		this.fillingToothNo = fillingToothNo;
		this.isFreqencyLimitationApplicable = isFreqencyLimitationApplicable;
		this.isMissingToothClauseApplicable = isMissingToothClauseApplicable;
		this.isBundlingApplicable = isBundlingApplicable;
		this.whatAdditionalInformation = whatAdditionalInformation;
		this.isPreAuthNeeded = isPreAuthNeeded;
		this.isAgeLimitAapplicable = isAgeLimitAapplicable;
	}
	public String getServiceCodes() {
		return serviceCodes;
	}
	public void setServiceCodes(String serviceCodes) {
		this.serviceCodes = serviceCodes;
	}
	public String getServiceCodeCategories() {
		return serviceCodeCategories;
	}
	public void setServiceCodeCategories(String serviceCodeCategories) {
		this.serviceCodeCategories = serviceCodeCategories;
	}
	public String getCoveredUnderMedical() {
		return coveredUnderMedical;
	}
	public void setCoveredUnderMedical(String coveredUnderMedical) {
		this.coveredUnderMedical = coveredUnderMedical;
	}
	public String getDowngrading() {
		return downgrading;
	}
	public void setDowngrading(String downgrading) {
		this.downgrading = downgrading;
	}
	public String getToothNo() {
		return toothNo;
	}
	public void setToothNo(String toothNo) {
		this.toothNo = toothNo;
	}
	public String getFillingToothNo() {
		return fillingToothNo;
	}
	public void setFillingToothNo(String fillingToothNo) {
		this.fillingToothNo = fillingToothNo;
	}
	public String getIsFreqencyLimitationApplicable() {
		return isFreqencyLimitationApplicable;
	}
	public void setIsFreqencyLimitationApplicable(String isFreqencyLimitationApplicable) {
		this.isFreqencyLimitationApplicable = isFreqencyLimitationApplicable;
	}
	public String getIsMissingToothClauseApplicable() {
		return isMissingToothClauseApplicable;
	}
	public void setIsMissingToothClauseApplicable(String isMissingToothClauseApplicable) {
		this.isMissingToothClauseApplicable = isMissingToothClauseApplicable;
	}
	public String getIsBundlingApplicable() {
		return isBundlingApplicable;
	}
	public void setIsBundlingApplicable(String isBundlingApplicable) {
		this.isBundlingApplicable = isBundlingApplicable;
	}
	public String getWhatAdditionalInformation() {
		return whatAdditionalInformation;
	}
	public void setWhatAdditionalInformation(String whatAdditionalInformation) {
		this.whatAdditionalInformation = whatAdditionalInformation;
	}
	public String getIsPreAuthNeeded() {
		return isPreAuthNeeded;
	}
	public void setIsPreAuthNeeded(String isPreAuthNeeded) {
		this.isPreAuthNeeded = isPreAuthNeeded;
	}
	public String getIsAgeLimitAapplicable() {
		return isAgeLimitAapplicable;
	}
	public void setIsAgeLimitAapplicable(String isAgeLimitAapplicable) {
		this.isAgeLimitAapplicable = isAgeLimitAapplicable;
	}

	
}
