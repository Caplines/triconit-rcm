package com.tricon.ruleengine.model.sheet;

public class IVFTableSheet {
	
	private String officeName;
	private String patientName ;
	private String insName ; 
	private String taxId ;
	private String policyHolder ; 
	private String patientDOB;
	private String InsContact;
	private String cSRName;
	private String policyHolderDOB;
	private String employerName;
	private String contTxRecallNP ;
	private String ref;
	private String memberSSN ;
	private String group ;
	private String cOBStatus ;
	private String memberId ;
	private String aptDate ;
	private String payerId ;
	private String providerName ;
	private String insAddress ;
	private String planType ;
	private String planTermedDate ;
	private String planNetwork ;
	private String planFeeScheduleName ;// Fee Schedule Name
	private String planIndFamilyCoverage ;
	private String planEffectiveDate ;//26-1 Effective Date RULE ONE (Plan_EffectiveDate)
	private String planAnnualMax ;
	private String planAnnualMaxUsed ;
	private String planIndividualDeductible ;
	private String planIndividualDeductibleMet ;
	private String planFamilyDeductible ; 
	private String planFamilyDeductibleMet ;
	private String planCalendarFiscalYear ;
	private String planDependentsCoveredtoAge ;
	private String planCoverageBook ;// Coverage Book
	private String planPreDMandatory ;
	private String planNonDuplicateClause;
	private String planFullTimeStudentStatus;
	private String planAssignmentofBenefits  ;
	private String xRaysBundling ;
	private String xRaysBWSperCYFL ;
	private String xRaysFMXperCYFL ;
	private String selants ;
	private String selantsD1351PrimaryMolarsCovered ;
	private String selantsD1351PreMolarCovered ;
	private String selantsD1351PermanentMolarsCovered ;
	private String selantsD135AgeLimit ;
	private String sRPD4341 ;//%
	private String sRPD4341FL ;
	private String sRPD4341QuadsPerDay  ;
	private String sRPD4341DaysBwTreatment ;
	private String perioMaintenanceD4910 ;//%
	private String perioMaintenanceD4910FL ;
	private String perioMaintenanceD4910AltwProphyD0110 ;
	private String fMDD4355 ;//%
	private String fMDD4355FL ;
	private String gingivitisD4346 ;// %
	private String gingivitisD4346FL ;
	private String denturesFL ;
	private String completeDenturesD5110D5120FL ;
	private String immediateDenturesD5130D5140FL ;
	private String PartialDenturesD5213D5214FL ;
	private String interimPartialDenturesD5214FL ;
	private String flourideD1208FL ;
	private String flourideAgeLimit ;
	private String varnishD1206FL ;
	private String varnishD1206AgeLimit ;
	private String rollOverAge11201110 ;
	private String fillingsBundling ; 
	private String postCompFL ; 
	private String orthoAgeLimit ;
	private String sscD2930FL ; 
	private String sscC2931FL ;
	private String oralSurgeryD72107241CoveredMedical ;
	private String crownLengthD4249Per; //%
	private String crownLengthD4249FL ;
	private String boneGraftsD7953CoveredWithEXT ; 
	private String boneGraftsD7953FL ;
	private String alveoD7311CoveredWithEXT ; 
	private String alveoD7311FL ; 
	private String alveoD7310CoveredWithEXT ; 
	private String alveoD7310FL ;
	private String missingToothClause ; 
	private String replacementClause ; 
	private String crownsD2750D2740FL ;
	private String crownsD2750D2740PaysPrepSeatDate ; 
	private String nightGuardsD9940FL ; 
	private String buildUpsD2950Covered ; 
	private String buildUpsD2950FL ;
	private String buildUpsD2950SameDayCrown ;
	private String basicWaitingPeriod ;
	private String majorWaitingPeriod ;
	
	
	private String preventive ;//% 
	private String diagnostic ;//%
	private String paXRays ;//% 
	private String basic;//%
	private String basicSubjectDeductible ;
	private String major ;//% 
	private String majorSubjectDeductible ; 
	private String endodontics ; //%
	private String endoSubjectDeductible ; 
	private String perioSurgery ;//% 
	private String perioSurgerySubjectDeductible ; 
	private String extractionsMinor ;//%  
	private String extraction_Major;//%  
	private String ortho ;//% 
	private String orthoMax ;
	private String orthoSubjectDeductible ; 
	private String nitrousD9230 ;//%  
	private String iVSedationD9243 ;//%  
	private String iVSedationD9248 ; //% 
	private String medicalInsCoverage ; 
	private String implantCoverage ; //% 
	private String implantSupportedPorcCeramicD606;//%  
	private String postCompositesD2391 ; //% 
	private String posteriorCompositesD2391Downgrade ; 
	private String crownsD2750D2740 ; //% 
	private String crownsD2750D2740Downgrade ; 
	private String nightGuardsD9940FL_1_REP ;//REPEAT 
	
	
	private String d9310 ;//% 
	private String d9310FL ; 
	
	private String adaCode ;
	private String toothNo ; 
	private String dos ; 
	
	private String adaCode_1_REP ;//REP
	private String toothNo_1_REP ;//REP 
	private String dos_1_REP ;//REP 
	
	private String adaCode_2_REP ;//REP
	private String toothNo_2_REP ;//REP 
	private String dos_2_REP ;//REP 
	
	private String adaCode_3_REP ;//REP
	private String toothNo_3_REP ;//REP
	private String dos_3_REP ;//REP ; 
	
	private String adaCode_4_REP ;//REP ; 
	private String toothNo_4_REP ;//REP ;
	private String dos_4_REP ;//REP ; 
	
	private String adaCode_5_REP ;//REP ; 
	private String toothNo_5_REP ;//REP ; 
	private String dos_5_REP ;//REP ;
	
	private String adaCode_6_REP ;//REP ;
	private String toothNo_6_REP ;//REP ;
	private String dos_6_REP ;//REP ;
	
	private String adaCode_7_REP ;//REP ;
	private String toothNo_7_REP ;//REP; 
	private String dos_7_REP ;//REP ; 
	
	private String adaCode_8_REP ;//REP;
	private String toothNo_8_REP ;//REP;
	private String dos_8_REP ;//REP ; 
	
	private String comments ;
	private String generalBenefitsVerifiedBy ; 
	private String generalDateIVwasDone ;//Format Issue
	private String uniqueId;
	
	
	//Constructor
	
	//
	
	
	public IVFTableSheet(String officeName, String patientName, String insName, String taxId, String policyHolder,
			String patientDOB, String insContact, String cSRName, String policyHolderDOB, String employerName,
			String contTxRecallNP, String ref, String memberSSN, String group, String cOBStatus, String memberId,
			String aptDate, String payerId, String providerName, String insAddress, String planType,
			String planTermedDate, String planNetwork, String planFeeScheduleName, String planIndFamilyCoverage,
			String planEffectiveDate, String planAnnualMax, String planAnnualMaxUsed, String planIndividualDeductible,
			String planIndividualDeductibleMet, String planFamilyDeductible, String planFamilyDeductibleMet,
			String planCalendarFiscalYear, String planDependentsCoveredtoAge, String planCoverageBook,
			String planPreDMandatory, String planNonDuplicateClause, String planFullTimeStudentStatus,
			String planAssignmentofBenefits, String xRaysBundling, String xRaysBWSperCYFL, String xRaysFMXperCYFL,
			String selants, String selantsD1351PrimaryMolarsCovered, String selantsD1351PreMolarCovered,
			String selantsD1351PermanentMolarsCovered, String selantsD135AgeLimit, String sRPD4341, String sRPD4341FL,
			String sRPD4341QuadsPerDay, String sRPD4341DaysBwTreatment, String perioMaintenanceD4910,
			String perioMaintenanceD4910FL, String perioMaintenanceD4910AltwProphyD0110, String fMDD4355,
			String fMDD4355FL, String gingivitisD4346, String gingivitisD4346FL, String denturesFL,
			String completeDenturesD5110D5120FL, String immediateDenturesD5130D5140FL,
			String partialDenturesD5213D5214FL, String interimPartialDenturesD5214FL, String flourideD1208FL,
			String flourideAgeLimit, String varnishD1206FL, String varnishD1206AgeLimit, String rollOverAge11201110,
			String fillingsBundling, String postCompFL, String orthoAgeLimit, String sscD2930FL, String sscC2931FL,
			String oralSurgeryD72107241CoveredMedical, String crownLengthD4249Per, String crownLengthD4249FL,
			String boneGraftsD7953CoveredWithEXT, String boneGraftsD7953FL, String alveoD7311CoveredWithEXT,
			String alveoD7311FL, String alveoD7310CoveredWithEXT, String alveoD7310FL, String missingToothClause,
			String replacementClause, String crownsD2750D2740FL, String crownsD2750D2740PaysPrepSeatDate,
			String nightGuardsD9940FL, String buildUpsD2950Covered, String buildUpsD2950FL,
			String buildUpsD2950SameDayCrown, String basicWaitingPeriod, String majorWaitingPeriod, String preventive,
			String diagnostic, String paXRays, String basic, String basicSubjectDeductible, String major,
			String majorSubjectDeductible, String endodontics, String endoSubjectDeductible, String perioSurgery,
			String perioSurgerySubjectDeductible, String extractionsMinor, String extraction_Major, String ortho,
			String orthoMax, String orthoSubjectDeductible, String nitrousD9230, String iVSedationD9243,
			String iVSedationD9248, String medicalInsCoverage, String implantCoverage,
			String implantSupportedPorcCeramicD606, String postCompositesD2391,
			String posteriorCompositesD2391Downgrade, String crownsD2750D2740, String crownsD2750D2740Downgrade,
			String nightGuardsD9940FL_1_REP, String d9310, String d9310fl, String adaCode, String toothNo, String dos,
			String adaCode_1_REP, String toothNo_1_REP, String dos_1_REP, String adaCode_2_REP, String toothNo_2_REP,
			String dos_2_REP, String adaCode_3_REP, String toothNo_3_REP, String dos_3_REP, String adaCode_4_REP,
			String toothNo_4_REP, String dos_4_REP, String adaCode_5_REP, String toothNo_5_REP, String dos_5_REP,
			String adaCode_6_REP, String toothNo_6_REP, String dos_6_REP, String adaCode_7_REP, String toothNo_7_REP,
			String dos_7_REP, String adaCode_8_REP, String toothNo_8_REP, String dos_8_REP, String comments,
			String generalBenefitsVerifiedBy, String generalDateIVwasDone, String uniqueId) {
		super();
		this.officeName = officeName;
		this.patientName = patientName;
		this.insName = insName;
		this.taxId = taxId;
		this.policyHolder = policyHolder;
		this.patientDOB = patientDOB;
		InsContact = insContact;
		this.cSRName = cSRName;
		this.policyHolderDOB = policyHolderDOB;
		this.employerName = employerName;
		this.contTxRecallNP = contTxRecallNP;
		this.ref = ref;
		this.memberSSN = memberSSN;
		this.group = group;
		this.cOBStatus = cOBStatus;
		this.memberId = memberId;
		this.aptDate = aptDate;
		this.payerId = payerId;
		this.providerName = providerName;
		this.insAddress = insAddress;
		this.planType = planType;
		this.planTermedDate = planTermedDate;
		this.planNetwork = planNetwork;
		this.planFeeScheduleName = planFeeScheduleName;
		this.planIndFamilyCoverage = planIndFamilyCoverage;
		this.planEffectiveDate = planEffectiveDate;
		this.planAnnualMax = planAnnualMax;
		this.planAnnualMaxUsed = planAnnualMaxUsed;
		this.planIndividualDeductible = planIndividualDeductible;
		this.planIndividualDeductibleMet = planIndividualDeductibleMet;
		this.planFamilyDeductible = planFamilyDeductible;
		this.planFamilyDeductibleMet = planFamilyDeductibleMet;
		this.planCalendarFiscalYear = planCalendarFiscalYear;
		this.planDependentsCoveredtoAge = planDependentsCoveredtoAge;
		this.planCoverageBook = planCoverageBook;
		this.planPreDMandatory = planPreDMandatory;
		this.planNonDuplicateClause = planNonDuplicateClause;
		this.planFullTimeStudentStatus = planFullTimeStudentStatus;
		this.planAssignmentofBenefits = planAssignmentofBenefits;
		this.xRaysBundling = xRaysBundling;
		this.xRaysBWSperCYFL = xRaysBWSperCYFL;
		this.xRaysFMXperCYFL = xRaysFMXperCYFL;
		this.selants = selants;
		this.selantsD1351PrimaryMolarsCovered = selantsD1351PrimaryMolarsCovered;
		this.selantsD1351PreMolarCovered = selantsD1351PreMolarCovered;
		this.selantsD1351PermanentMolarsCovered = selantsD1351PermanentMolarsCovered;
		this.selantsD135AgeLimit = selantsD135AgeLimit;
		this.sRPD4341 = sRPD4341;
		this.sRPD4341FL = sRPD4341FL;
		this.sRPD4341QuadsPerDay = sRPD4341QuadsPerDay;
		this.sRPD4341DaysBwTreatment = sRPD4341DaysBwTreatment;
		this.perioMaintenanceD4910 = perioMaintenanceD4910;
		this.perioMaintenanceD4910FL = perioMaintenanceD4910FL;
		this.perioMaintenanceD4910AltwProphyD0110 = perioMaintenanceD4910AltwProphyD0110;
		this.fMDD4355 = fMDD4355;
		this.fMDD4355FL = fMDD4355FL;
		this.gingivitisD4346 = gingivitisD4346;
		this.gingivitisD4346FL = gingivitisD4346FL;
		this.denturesFL = denturesFL;
		this.completeDenturesD5110D5120FL = completeDenturesD5110D5120FL;
		this.immediateDenturesD5130D5140FL = immediateDenturesD5130D5140FL;
		PartialDenturesD5213D5214FL = partialDenturesD5213D5214FL;
		this.interimPartialDenturesD5214FL = interimPartialDenturesD5214FL;
		this.flourideD1208FL = flourideD1208FL;
		this.flourideAgeLimit = flourideAgeLimit;
		this.varnishD1206FL = varnishD1206FL;
		this.varnishD1206AgeLimit = varnishD1206AgeLimit;
		this.rollOverAge11201110 = rollOverAge11201110;
		this.fillingsBundling = fillingsBundling;
		this.postCompFL = postCompFL;
		this.orthoAgeLimit = orthoAgeLimit;
		this.sscD2930FL = sscD2930FL;
		this.sscC2931FL = sscC2931FL;
		this.oralSurgeryD72107241CoveredMedical = oralSurgeryD72107241CoveredMedical;
		this.crownLengthD4249Per = crownLengthD4249Per;
		this.crownLengthD4249FL = crownLengthD4249FL;
		this.boneGraftsD7953CoveredWithEXT = boneGraftsD7953CoveredWithEXT;
		this.boneGraftsD7953FL = boneGraftsD7953FL;
		this.alveoD7311CoveredWithEXT = alveoD7311CoveredWithEXT;
		this.alveoD7311FL = alveoD7311FL;
		this.alveoD7310CoveredWithEXT = alveoD7310CoveredWithEXT;
		this.alveoD7310FL = alveoD7310FL;
		this.missingToothClause = missingToothClause;
		this.replacementClause = replacementClause;
		this.crownsD2750D2740FL = crownsD2750D2740FL;
		this.crownsD2750D2740PaysPrepSeatDate = crownsD2750D2740PaysPrepSeatDate;
		this.nightGuardsD9940FL = nightGuardsD9940FL;
		this.buildUpsD2950Covered = buildUpsD2950Covered;
		this.buildUpsD2950FL = buildUpsD2950FL;
		this.buildUpsD2950SameDayCrown = buildUpsD2950SameDayCrown;
		this.basicWaitingPeriod = basicWaitingPeriod;
		this.majorWaitingPeriod = majorWaitingPeriod;
		this.preventive = preventive;
		this.diagnostic = diagnostic;
		this.paXRays = paXRays;
		this.basic = basic;
		this.basicSubjectDeductible = basicSubjectDeductible;
		this.major = major;
		this.majorSubjectDeductible = majorSubjectDeductible;
		this.endodontics = endodontics;
		this.endoSubjectDeductible = endoSubjectDeductible;
		this.perioSurgery = perioSurgery;
		this.perioSurgerySubjectDeductible = perioSurgerySubjectDeductible;
		this.extractionsMinor = extractionsMinor;
		this.extraction_Major = extraction_Major;
		this.ortho = ortho;
		this.orthoMax = orthoMax;
		this.orthoSubjectDeductible = orthoSubjectDeductible;
		this.nitrousD9230 = nitrousD9230;
		this.iVSedationD9243 = iVSedationD9243;
		this.iVSedationD9248 = iVSedationD9248;
		this.medicalInsCoverage = medicalInsCoverage;
		this.implantCoverage = implantCoverage;
		this.implantSupportedPorcCeramicD606 = implantSupportedPorcCeramicD606;
		this.postCompositesD2391 = postCompositesD2391;
		this.posteriorCompositesD2391Downgrade = posteriorCompositesD2391Downgrade;
		this.crownsD2750D2740 = crownsD2750D2740;
		this.crownsD2750D2740Downgrade = crownsD2750D2740Downgrade;
		this.nightGuardsD9940FL_1_REP = nightGuardsD9940FL_1_REP;
		this.d9310 = d9310;
		d9310FL = d9310fl;
		this.adaCode = adaCode;
		this.toothNo = toothNo;
		this.dos = dos;
		this.adaCode_1_REP = adaCode_1_REP;
		this.toothNo_1_REP = toothNo_1_REP;
		this.dos_1_REP = dos_1_REP;
		this.adaCode_2_REP = adaCode_2_REP;
		this.toothNo_2_REP = toothNo_2_REP;
		this.dos_2_REP = dos_2_REP;
		this.adaCode_3_REP = adaCode_3_REP;
		this.toothNo_3_REP = toothNo_3_REP;
		this.dos_3_REP = dos_3_REP;
		this.adaCode_4_REP = adaCode_4_REP;
		this.toothNo_4_REP = toothNo_4_REP;
		this.dos_4_REP = dos_4_REP;
		this.adaCode_5_REP = adaCode_5_REP;
		this.toothNo_5_REP = toothNo_5_REP;
		this.dos_5_REP = dos_5_REP;
		this.adaCode_6_REP = adaCode_6_REP;
		this.toothNo_6_REP = toothNo_6_REP;
		this.dos_6_REP = dos_6_REP;
		this.adaCode_7_REP = adaCode_7_REP;
		this.toothNo_7_REP = toothNo_7_REP;
		this.dos_7_REP = dos_7_REP;
		this.adaCode_8_REP = adaCode_8_REP;
		this.toothNo_8_REP = toothNo_8_REP;
		this.dos_8_REP = dos_8_REP;
		this.comments = comments;
		this.generalBenefitsVerifiedBy = generalBenefitsVerifiedBy;
		this.generalDateIVwasDone = generalDateIVwasDone;
		this.uniqueId = uniqueId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getInsName() {
		return insName;
	}
	public void setInsName(String insName) {
		this.insName = insName;
	}
	public String getTaxId() {
		return taxId;
	}
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
	public String getPolicyHolder() {
		return policyHolder;
	}
	public void setPolicyHolder(String policyHolder) {
		this.policyHolder = policyHolder;
	}
	public String getPatientDOB() {
		return patientDOB;
	}
	public void setPatientDOB(String patientDOB) {
		this.patientDOB = patientDOB;
	}
	public String getInsContact() {
		return InsContact;
	}
	public void setInsContact(String insContact) {
		InsContact = insContact;
	}
	public String getcSRName() {
		return cSRName;
	}
	public void setcSRName(String cSRName) {
		this.cSRName = cSRName;
	}
	public String getPolicyHolderDOB() {
		return policyHolderDOB;
	}
	public void setPolicyHolderDOB(String policyHolderDOB) {
		this.policyHolderDOB = policyHolderDOB;
	}
	public String getEmployerName() {
		return employerName;
	}
	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}
	public String getContTxRecallNP() {
		return contTxRecallNP;
	}
	public void setContTxRecallNP(String contTxRecallNP) {
		this.contTxRecallNP = contTxRecallNP;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getMemberSSN() {
		return memberSSN;
	}
	public void setMemberSSN(String memberSSN) {
		this.memberSSN = memberSSN;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getcOBStatus() {
		return cOBStatus;
	}
	public void setcOBStatus(String cOBStatus) {
		this.cOBStatus = cOBStatus;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getAptDate() {
		return aptDate;
	}
	public void setAptDate(String aptDate) {
		this.aptDate = aptDate;
	}
	public String getPayerId() {
		return payerId;
	}
	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getInsAddress() {
		return insAddress;
	}
	public void setInsAddress(String insAddress) {
		this.insAddress = insAddress;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getPlanTermedDate() {
		return planTermedDate;
	}
	public void setPlanTermedDate(String planTermedDate) {
		this.planTermedDate = planTermedDate;
	}
	public String getPlanNetwork() {
		return planNetwork;
	}
	public void setPlanNetwork(String planNetwork) {
		this.planNetwork = planNetwork;
	}
	public String getPlanFeeScheduleName() {
		return planFeeScheduleName;
	}
	public void setPlanFeeScheduleName(String planFeeScheduleName) {
		this.planFeeScheduleName = planFeeScheduleName;
	}
	public String getPlanIndFamilyCoverage() {
		return planIndFamilyCoverage;
	}
	public void setPlanIndFamilyCoverage(String planIndFamilyCoverage) {
		this.planIndFamilyCoverage = planIndFamilyCoverage;
	}
	public String getPlanEffectiveDate() {
		return planEffectiveDate;
	}
	public void setPlanEffectiveDate(String planEffectiveDate) {
		this.planEffectiveDate = planEffectiveDate;
	}
	public String getPlanAnnualMax() {
		return planAnnualMax;
	}
	public void setPlanAnnualMax(String planAnnualMax) {
		this.planAnnualMax = planAnnualMax;
	}
	public String getPlanAnnualMaxUsed() {
		return planAnnualMaxUsed;
	}
	public void setPlanAnnualMaxUsed(String planAnnualMaxUsed) {
		this.planAnnualMaxUsed = planAnnualMaxUsed;
	}
	public String getPlanIndividualDeductible() {
		return planIndividualDeductible;
	}
	public void setPlanIndividualDeductible(String planIndividualDeductible) {
		this.planIndividualDeductible = planIndividualDeductible;
	}
	public String getPlanIndividualDeductibleMet() {
		return planIndividualDeductibleMet;
	}
	public void setPlanIndividualDeductibleMet(String planIndividualDeductibleMet) {
		this.planIndividualDeductibleMet = planIndividualDeductibleMet;
	}
	public String getPlanFamilyDeductible() {
		return planFamilyDeductible;
	}
	public void setPlanFamilyDeductible(String planFamilyDeductible) {
		this.planFamilyDeductible = planFamilyDeductible;
	}
	public String getPlanFamilyDeductibleMet() {
		return planFamilyDeductibleMet;
	}
	public void setPlanFamilyDeductibleMet(String planFamilyDeductibleMet) {
		this.planFamilyDeductibleMet = planFamilyDeductibleMet;
	}
	public String getPlanCalendarFiscalYear() {
		return planCalendarFiscalYear;
	}
	public void setPlanCalendarFiscalYear(String planCalendarFiscalYear) {
		this.planCalendarFiscalYear = planCalendarFiscalYear;
	}
	public String getPlanDependentsCoveredtoAge() {
		return planDependentsCoveredtoAge;
	}
	public void setPlanDependentsCoveredtoAge(String planDependentsCoveredtoAge) {
		this.planDependentsCoveredtoAge = planDependentsCoveredtoAge;
	}
	public String getPlanCoverageBook() {
		return planCoverageBook;
	}
	public void setPlanCoverageBook(String planCoverageBook) {
		this.planCoverageBook = planCoverageBook;
	}
	public String getPlanPreDMandatory() {
		return planPreDMandatory;
	}
	public void setPlanPreDMandatory(String planPreDMandatory) {
		this.planPreDMandatory = planPreDMandatory;
	}
	public String getPlanNonDuplicateClause() {
		return planNonDuplicateClause;
	}
	public void setPlanNonDuplicateClause(String planNonDuplicateClause) {
		this.planNonDuplicateClause = planNonDuplicateClause;
	}
	public String getPlanFullTimeStudentStatus() {
		return planFullTimeStudentStatus;
	}
	public void setPlanFullTimeStudentStatus(String planFullTimeStudentStatus) {
		this.planFullTimeStudentStatus = planFullTimeStudentStatus;
	}
	public String getPlanAssignmentofBenefits() {
		return planAssignmentofBenefits;
	}
	public void setPlanAssignmentofBenefits(String planAssignmentofBenefits) {
		this.planAssignmentofBenefits = planAssignmentofBenefits;
	}
	public String getxRaysBundling() {
		return xRaysBundling;
	}
	public void setxRaysBundling(String xRaysBundling) {
		this.xRaysBundling = xRaysBundling;
	}
	public String getxRaysBWSperCYFL() {
		return xRaysBWSperCYFL;
	}
	public void setxRaysBWSperCYFL(String xRaysBWSperCYFL) {
		this.xRaysBWSperCYFL = xRaysBWSperCYFL;
	}
	public String getxRaysFMXperCYFL() {
		return xRaysFMXperCYFL;
	}
	public void setxRaysFMXperCYFL(String xRaysFMXperCYFL) {
		this.xRaysFMXperCYFL = xRaysFMXperCYFL;
	}
	public String getSelants() {
		return selants;
	}
	public void setSelants(String selants) {
		this.selants = selants;
	}
	public String getSelantsD1351PrimaryMolarsCovered() {
		return selantsD1351PrimaryMolarsCovered;
	}
	public void setSelantsD1351PrimaryMolarsCovered(String selantsD1351PrimaryMolarsCovered) {
		this.selantsD1351PrimaryMolarsCovered = selantsD1351PrimaryMolarsCovered;
	}
	public String getSelantsD1351PreMolarCovered() {
		return selantsD1351PreMolarCovered;
	}
	public void setSelantsD1351PreMolarCovered(String selantsD1351PreMolarCovered) {
		this.selantsD1351PreMolarCovered = selantsD1351PreMolarCovered;
	}
	public String getSelantsD1351PermanentMolarsCovered() {
		return selantsD1351PermanentMolarsCovered;
	}
	public void setSelantsD1351PermanentMolarsCovered(String selantsD1351PermanentMolarsCovered) {
		this.selantsD1351PermanentMolarsCovered = selantsD1351PermanentMolarsCovered;
	}
	public String getSelantsD135AgeLimit() {
		return selantsD135AgeLimit;
	}
	public void setSelantsD135AgeLimit(String selantsD135AgeLimit) {
		this.selantsD135AgeLimit = selantsD135AgeLimit;
	}
	public String getsRPD4341() {
		return sRPD4341;
	}
	public void setsRPD4341(String sRPD4341) {
		this.sRPD4341 = sRPD4341;
	}
	public String getsRPD4341FL() {
		return sRPD4341FL;
	}
	public void setsRPD4341FL(String sRPD4341FL) {
		this.sRPD4341FL = sRPD4341FL;
	}
	public String getsRPD4341QuadsPerDay() {
		return sRPD4341QuadsPerDay;
	}
	public void setsRPD4341QuadsPerDay(String sRPD4341QuadsPerDay) {
		this.sRPD4341QuadsPerDay = sRPD4341QuadsPerDay;
	}
	public String getsRPD4341DaysBwTreatment() {
		return sRPD4341DaysBwTreatment;
	}
	public void setsRPD4341DaysBwTreatment(String sRPD4341DaysBwTreatment) {
		this.sRPD4341DaysBwTreatment = sRPD4341DaysBwTreatment;
	}
	public String getPerioMaintenanceD4910() {
		return perioMaintenanceD4910;
	}
	public void setPerioMaintenanceD4910(String perioMaintenanceD4910) {
		this.perioMaintenanceD4910 = perioMaintenanceD4910;
	}
	public String getPerioMaintenanceD4910FL() {
		return perioMaintenanceD4910FL;
	}
	public void setPerioMaintenanceD4910FL(String perioMaintenanceD4910FL) {
		this.perioMaintenanceD4910FL = perioMaintenanceD4910FL;
	}
	public String getPerioMaintenanceD4910AltwProphyD0110() {
		return perioMaintenanceD4910AltwProphyD0110;
	}
	public void setPerioMaintenanceD4910AltwProphyD0110(String perioMaintenanceD4910AltwProphyD0110) {
		this.perioMaintenanceD4910AltwProphyD0110 = perioMaintenanceD4910AltwProphyD0110;
	}
	public String getfMDD4355() {
		return fMDD4355;
	}
	public void setfMDD4355(String fMDD4355) {
		this.fMDD4355 = fMDD4355;
	}
	public String getfMDD4355FL() {
		return fMDD4355FL;
	}
	public void setfMDD4355FL(String fMDD4355FL) {
		this.fMDD4355FL = fMDD4355FL;
	}
	public String getGingivitisD4346() {
		return gingivitisD4346;
	}
	public void setGingivitisD4346(String gingivitisD4346) {
		this.gingivitisD4346 = gingivitisD4346;
	}
	public String getGingivitisD4346FL() {
		return gingivitisD4346FL;
	}
	public void setGingivitisD4346FL(String gingivitisD4346FL) {
		this.gingivitisD4346FL = gingivitisD4346FL;
	}
	public String getDenturesFL() {
		return denturesFL;
	}
	public void setDenturesFL(String denturesFL) {
		this.denturesFL = denturesFL;
	}
	public String getCompleteDenturesD5110D5120FL() {
		return completeDenturesD5110D5120FL;
	}
	public void setCompleteDenturesD5110D5120FL(String completeDenturesD5110D5120FL) {
		this.completeDenturesD5110D5120FL = completeDenturesD5110D5120FL;
	}
	public String getImmediateDenturesD5130D5140FL() {
		return immediateDenturesD5130D5140FL;
	}
	public void setImmediateDenturesD5130D5140FL(String immediateDenturesD5130D5140FL) {
		this.immediateDenturesD5130D5140FL = immediateDenturesD5130D5140FL;
	}
	public String getPartialDenturesD5213D5214FL() {
		return PartialDenturesD5213D5214FL;
	}
	public void setPartialDenturesD5213D5214FL(String partialDenturesD5213D5214FL) {
		PartialDenturesD5213D5214FL = partialDenturesD5213D5214FL;
	}
	public String getInterimPartialDenturesD5214FL() {
		return interimPartialDenturesD5214FL;
	}
	public void setInterimPartialDenturesD5214FL(String interimPartialDenturesD5214FL) {
		this.interimPartialDenturesD5214FL = interimPartialDenturesD5214FL;
	}
	public String getFlourideD1208FL() {
		return flourideD1208FL;
	}
	public void setFlourideD1208FL(String flourideD1208FL) {
		this.flourideD1208FL = flourideD1208FL;
	}
	public String getFlourideAgeLimit() {
		return flourideAgeLimit;
	}
	public void setFlourideAgeLimit(String flourideAgeLimit) {
		this.flourideAgeLimit = flourideAgeLimit;
	}
	public String getVarnishD1206FL() {
		return varnishD1206FL;
	}
	public void setVarnishD1206FL(String varnishD1206FL) {
		this.varnishD1206FL = varnishD1206FL;
	}
	public String getVarnishD1206AgeLimit() {
		return varnishD1206AgeLimit;
	}
	public void setVarnishD1206AgeLimit(String varnishD1206AgeLimit) {
		this.varnishD1206AgeLimit = varnishD1206AgeLimit;
	}
	public String getRollOverAge11201110() {
		return rollOverAge11201110;
	}
	public void setRollOverAge11201110(String rollOverAge11201110) {
		this.rollOverAge11201110 = rollOverAge11201110;
	}
	public String getFillingsBundling() {
		return fillingsBundling;
	}
	public void setFillingsBundling(String fillingsBundling) {
		this.fillingsBundling = fillingsBundling;
	}
	public String getPostCompFL() {
		return postCompFL;
	}
	public void setPostCompFL(String postCompFL) {
		this.postCompFL = postCompFL;
	}
	public String getOrthoAgeLimit() {
		return orthoAgeLimit;
	}
	public void setOrthoAgeLimit(String orthoAgeLimit) {
		this.orthoAgeLimit = orthoAgeLimit;
	}
	public String getSscD2930FL() {
		return sscD2930FL;
	}
	public void setSscD2930FL(String sscD2930FL) {
		this.sscD2930FL = sscD2930FL;
	}
	public String getSscC2931FL() {
		return sscC2931FL;
	}
	public void setSscC2931FL(String sscC2931FL) {
		this.sscC2931FL = sscC2931FL;
	}
	public String getOralSurgeryD72107241CoveredMedical() {
		return oralSurgeryD72107241CoveredMedical;
	}
	public void setOralSurgeryD72107241CoveredMedical(String oralSurgeryD72107241CoveredMedical) {
		this.oralSurgeryD72107241CoveredMedical = oralSurgeryD72107241CoveredMedical;
	}
	public String getCrownLengthD4249Per() {
		return crownLengthD4249Per;
	}
	public void setCrownLengthD4249Per(String crownLengthD4249Per) {
		this.crownLengthD4249Per = crownLengthD4249Per;
	}
	public String getCrownLengthD4249FL() {
		return crownLengthD4249FL;
	}
	public void setCrownLengthD4249FL(String crownLengthD4249FL) {
		this.crownLengthD4249FL = crownLengthD4249FL;
	}
	public String getBoneGraftsD7953CoveredWithEXT() {
		return boneGraftsD7953CoveredWithEXT;
	}
	public void setBoneGraftsD7953CoveredWithEXT(String boneGraftsD7953CoveredWithEXT) {
		this.boneGraftsD7953CoveredWithEXT = boneGraftsD7953CoveredWithEXT;
	}
	public String getBoneGraftsD7953FL() {
		return boneGraftsD7953FL;
	}
	public void setBoneGraftsD7953FL(String boneGraftsD7953FL) {
		this.boneGraftsD7953FL = boneGraftsD7953FL;
	}
	public String getAlveoD7311CoveredWithEXT() {
		return alveoD7311CoveredWithEXT;
	}
	public void setAlveoD7311CoveredWithEXT(String alveoD7311CoveredWithEXT) {
		this.alveoD7311CoveredWithEXT = alveoD7311CoveredWithEXT;
	}
	public String getAlveoD7311FL() {
		return alveoD7311FL;
	}
	public void setAlveoD7311FL(String alveoD7311FL) {
		this.alveoD7311FL = alveoD7311FL;
	}
	public String getAlveoD7310CoveredWithEXT() {
		return alveoD7310CoveredWithEXT;
	}
	public void setAlveoD7310CoveredWithEXT(String alveoD7310CoveredWithEXT) {
		this.alveoD7310CoveredWithEXT = alveoD7310CoveredWithEXT;
	}
	public String getAlveoD7310FL() {
		return alveoD7310FL;
	}
	public void setAlveoD7310FL(String alveoD7310FL) {
		this.alveoD7310FL = alveoD7310FL;
	}
	public String getMissingToothClause() {
		return missingToothClause;
	}
	public void setMissingToothClause(String missingToothClause) {
		this.missingToothClause = missingToothClause;
	}
	public String getReplacementClause() {
		return replacementClause;
	}
	public void setReplacementClause(String replacementClause) {
		this.replacementClause = replacementClause;
	}
	public String getCrownsD2750D2740FL() {
		return crownsD2750D2740FL;
	}
	public void setCrownsD2750D2740FL(String crownsD2750D2740FL) {
		this.crownsD2750D2740FL = crownsD2750D2740FL;
	}
	public String getCrownsD2750D2740PaysPrepSeatDate() {
		return crownsD2750D2740PaysPrepSeatDate;
	}
	public void setCrownsD2750D2740PaysPrepSeatDate(String crownsD2750D2740PaysPrepSeatDate) {
		this.crownsD2750D2740PaysPrepSeatDate = crownsD2750D2740PaysPrepSeatDate;
	}
	public String getNightGuardsD9940FL() {
		return nightGuardsD9940FL;
	}
	public void setNightGuardsD9940FL(String nightGuardsD9940FL) {
		this.nightGuardsD9940FL = nightGuardsD9940FL;
	}
	public String getBuildUpsD2950Covered() {
		return buildUpsD2950Covered;
	}
	public void setBuildUpsD2950Covered(String buildUpsD2950Covered) {
		this.buildUpsD2950Covered = buildUpsD2950Covered;
	}
	public String getBuildUpsD2950FL() {
		return buildUpsD2950FL;
	}
	public void setBuildUpsD2950FL(String buildUpsD2950FL) {
		this.buildUpsD2950FL = buildUpsD2950FL;
	}
	public String getBuildUpsD2950SameDayCrown() {
		return buildUpsD2950SameDayCrown;
	}
	public void setBuildUpsD2950SameDayCrown(String buildUpsD2950SameDayCrown) {
		this.buildUpsD2950SameDayCrown = buildUpsD2950SameDayCrown;
	}
	public String getBasicWaitingPeriod() {
		return basicWaitingPeriod;
	}
	public void setBasicWaitingPeriod(String basicWaitingPeriod) {
		this.basicWaitingPeriod = basicWaitingPeriod;
	}
	public String getMajorWaitingPeriod() {
		return majorWaitingPeriod;
	}
	public void setMajorWaitingPeriod(String majorWaitingPeriod) {
		this.majorWaitingPeriod = majorWaitingPeriod;
	}
	public String getPreventive() {
		return preventive;
	}
	public void setPreventive(String preventive) {
		this.preventive = preventive;
	}
	public String getDiagnostic() {
		return diagnostic;
	}
	public void setDiagnostic(String diagnostic) {
		this.diagnostic = diagnostic;
	}
	public String getPaXRays() {
		return paXRays;
	}
	public void setPaXRays(String paXRays) {
		this.paXRays = paXRays;
	}
	public String getBasic() {
		return basic;
	}
	public void setBasic(String basic) {
		this.basic = basic;
	}
	public String getBasicSubjectDeductible() {
		return basicSubjectDeductible;
	}
	public void setBasicSubjectDeductible(String basicSubjectDeductible) {
		this.basicSubjectDeductible = basicSubjectDeductible;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getMajorSubjectDeductible() {
		return majorSubjectDeductible;
	}
	public void setMajorSubjectDeductible(String majorSubjectDeductible) {
		this.majorSubjectDeductible = majorSubjectDeductible;
	}
	public String getEndodontics() {
		return endodontics;
	}
	public void setEndodontics(String endodontics) {
		this.endodontics = endodontics;
	}
	public String getEndoSubjectDeductible() {
		return endoSubjectDeductible;
	}
	public void setEndoSubjectDeductible(String endoSubjectDeductible) {
		this.endoSubjectDeductible = endoSubjectDeductible;
	}
	public String getPerioSurgery() {
		return perioSurgery;
	}
	public void setPerioSurgery(String perioSurgery) {
		this.perioSurgery = perioSurgery;
	}
	public String getPerioSurgerySubjectDeductible() {
		return perioSurgerySubjectDeductible;
	}
	public void setPerioSurgerySubjectDeductible(String perioSurgerySubjectDeductible) {
		this.perioSurgerySubjectDeductible = perioSurgerySubjectDeductible;
	}
	public String getExtractionsMinor() {
		return extractionsMinor;
	}
	public void setExtractionsMinor(String extractionsMinor) {
		this.extractionsMinor = extractionsMinor;
	}
	public String getExtraction_Major() {
		return extraction_Major;
	}
	public void setExtraction_Major(String extraction_Major) {
		this.extraction_Major = extraction_Major;
	}
	public String getOrtho() {
		return ortho;
	}
	public void setOrtho(String ortho) {
		this.ortho = ortho;
	}
	public String getOrthoMax() {
		return orthoMax;
	}
	public void setOrthoMax(String orthoMax) {
		this.orthoMax = orthoMax;
	}
	public String getOrthoSubjectDeductible() {
		return orthoSubjectDeductible;
	}
	public void setOrthoSubjectDeductible(String orthoSubjectDeductible) {
		this.orthoSubjectDeductible = orthoSubjectDeductible;
	}
	public String getNitrousD9230() {
		return nitrousD9230;
	}
	public void setNitrousD9230(String nitrousD9230) {
		this.nitrousD9230 = nitrousD9230;
	}
	public String getiVSedationD9243() {
		return iVSedationD9243;
	}
	public void setiVSedationD9243(String iVSedationD9243) {
		this.iVSedationD9243 = iVSedationD9243;
	}
	public String getiVSedationD9248() {
		return iVSedationD9248;
	}
	public void setiVSedationD9248(String iVSedationD9248) {
		this.iVSedationD9248 = iVSedationD9248;
	}
	public String getMedicalInsCoverage() {
		return medicalInsCoverage;
	}
	public void setMedicalInsCoverage(String medicalInsCoverage) {
		this.medicalInsCoverage = medicalInsCoverage;
	}
	public String getImplantCoverage() {
		return implantCoverage;
	}
	public void setImplantCoverage(String implantCoverage) {
		this.implantCoverage = implantCoverage;
	}
	public String getImplantSupportedPorcCeramicD606() {
		return implantSupportedPorcCeramicD606;
	}
	public void setImplantSupportedPorcCeramicD606(String implantSupportedPorcCeramicD606) {
		this.implantSupportedPorcCeramicD606 = implantSupportedPorcCeramicD606;
	}
	public String getPostCompositesD2391() {
		return postCompositesD2391;
	}
	public void setPostCompositesD2391(String postCompositesD2391) {
		this.postCompositesD2391 = postCompositesD2391;
	}
	public String getPosteriorCompositesD2391Downgrade() {
		return posteriorCompositesD2391Downgrade;
	}
	public void setPosteriorCompositesD2391Downgrade(String posteriorCompositesD2391Downgrade) {
		this.posteriorCompositesD2391Downgrade = posteriorCompositesD2391Downgrade;
	}
	public String getCrownsD2750D2740() {
		return crownsD2750D2740;
	}
	public void setCrownsD2750D2740(String crownsD2750D2740) {
		this.crownsD2750D2740 = crownsD2750D2740;
	}
	public String getCrownsD2750D2740Downgrade() {
		return crownsD2750D2740Downgrade;
	}
	public void setCrownsD2750D2740Downgrade(String crownsD2750D2740Downgrade) {
		this.crownsD2750D2740Downgrade = crownsD2750D2740Downgrade;
	}
	public String getNightGuardsD9940FL_1_REP() {
		return nightGuardsD9940FL_1_REP;
	}
	public void setNightGuardsD9940FL_1_REP(String nightGuardsD9940FL_1_REP) {
		this.nightGuardsD9940FL_1_REP = nightGuardsD9940FL_1_REP;
	}
	public String getD9310() {
		return d9310;
	}
	public void setD9310(String d9310) {
		this.d9310 = d9310;
	}
	public String getD9310FL() {
		return d9310FL;
	}
	public void setD9310FL(String d9310fl) {
		d9310FL = d9310fl;
	}
	public String getAdaCode() {
		return adaCode;
	}
	public void setAdaCode(String adaCode) {
		this.adaCode = adaCode;
	}
	public String getToothNo() {
		return toothNo;
	}
	public void setToothNo(String toothNo) {
		this.toothNo = toothNo;
	}
	public String getDos() {
		return dos;
	}
	public void setDos(String dos) {
		this.dos = dos;
	}
	public String getAdaCode_1_REP() {
		return adaCode_1_REP;
	}
	public void setAdaCode_1_REP(String adaCode_1_REP) {
		this.adaCode_1_REP = adaCode_1_REP;
	}
	public String getToothNo_1_REP() {
		return toothNo_1_REP;
	}
	public void setToothNo_1_REP(String toothNo_1_REP) {
		this.toothNo_1_REP = toothNo_1_REP;
	}
	public String getDos_1_REP() {
		return dos_1_REP;
	}
	public void setDos_1_REP(String dos_1_REP) {
		this.dos_1_REP = dos_1_REP;
	}
	public String getAdaCode_2_REP() {
		return adaCode_2_REP;
	}
	public void setAdaCode_2_REP(String adaCode_2_REP) {
		this.adaCode_2_REP = adaCode_2_REP;
	}
	public String getToothNo_2_REP() {
		return toothNo_2_REP;
	}
	public void setToothNo_2_REP(String toothNo_2_REP) {
		this.toothNo_2_REP = toothNo_2_REP;
	}
	public String getDos_2_REP() {
		return dos_2_REP;
	}
	public void setDos_2_REP(String dos_2_REP) {
		this.dos_2_REP = dos_2_REP;
	}
	public String getAdaCode_3_REP() {
		return adaCode_3_REP;
	}
	public void setAdaCode_3_REP(String adaCode_3_REP) {
		this.adaCode_3_REP = adaCode_3_REP;
	}
	public String getToothNo_3_REP() {
		return toothNo_3_REP;
	}
	public void setToothNo_3_REP(String toothNo_3_REP) {
		this.toothNo_3_REP = toothNo_3_REP;
	}
	public String getDos_3_REP() {
		return dos_3_REP;
	}
	public void setDos_3_REP(String dos_3_REP) {
		this.dos_3_REP = dos_3_REP;
	}
	public String getAdaCode_4_REP() {
		return adaCode_4_REP;
	}
	public void setAdaCode_4_REP(String adaCode_4_REP) {
		this.adaCode_4_REP = adaCode_4_REP;
	}
	public String getToothNo_4_REP() {
		return toothNo_4_REP;
	}
	public void setToothNo_4_REP(String toothNo_4_REP) {
		this.toothNo_4_REP = toothNo_4_REP;
	}
	public String getDos_4_REP() {
		return dos_4_REP;
	}
	public void setDos_4_REP(String dos_4_REP) {
		this.dos_4_REP = dos_4_REP;
	}
	public String getAdaCode_5_REP() {
		return adaCode_5_REP;
	}
	public void setAdaCode_5_REP(String adaCode_5_REP) {
		this.adaCode_5_REP = adaCode_5_REP;
	}
	public String getToothNo_5_REP() {
		return toothNo_5_REP;
	}
	public void setToothNo_5_REP(String toothNo_5_REP) {
		this.toothNo_5_REP = toothNo_5_REP;
	}
	public String getDos_5_REP() {
		return dos_5_REP;
	}
	public void setDos_5_REP(String dos_5_REP) {
		this.dos_5_REP = dos_5_REP;
	}
	public String getAdaCode_6_REP() {
		return adaCode_6_REP;
	}
	public void setAdaCode_6_REP(String adaCode_6_REP) {
		this.adaCode_6_REP = adaCode_6_REP;
	}
	public String getToothNo_6_REP() {
		return toothNo_6_REP;
	}
	public void setToothNo_6_REP(String toothNo_6_REP) {
		this.toothNo_6_REP = toothNo_6_REP;
	}
	public String getDos_6_REP() {
		return dos_6_REP;
	}
	public void setDos_6_REP(String dos_6_REP) {
		this.dos_6_REP = dos_6_REP;
	}
	public String getAdaCode_7_REP() {
		return adaCode_7_REP;
	}
	public void setAdaCode_7_REP(String adaCode_7_REP) {
		this.adaCode_7_REP = adaCode_7_REP;
	}
	public String getToothNo_7_REP() {
		return toothNo_7_REP;
	}
	public void setToothNo_7_REP(String toothNo_7_REP) {
		this.toothNo_7_REP = toothNo_7_REP;
	}
	public String getDos_7_REP() {
		return dos_7_REP;
	}
	public void setDos_7_REP(String dos_7_REP) {
		this.dos_7_REP = dos_7_REP;
	}
	public String getAdaCode_8_REP() {
		return adaCode_8_REP;
	}
	public void setAdaCode_8_REP(String adaCode_8_REP) {
		this.adaCode_8_REP = adaCode_8_REP;
	}
	public String getToothNo_8_REP() {
		return toothNo_8_REP;
	}
	public void setToothNo_8_REP(String toothNo_8_REP) {
		this.toothNo_8_REP = toothNo_8_REP;
	}
	public String getDos_8_REP() {
		return dos_8_REP;
	}
	public void setDos_8_REP(String dos_8_REP) {
		this.dos_8_REP = dos_8_REP;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getGeneralBenefitsVerifiedBy() {
		return generalBenefitsVerifiedBy;
	}
	public void setGeneralBenefitsVerifiedBy(String generalBenefitsVerifiedBy) {
		this.generalBenefitsVerifiedBy = generalBenefitsVerifiedBy;
	}
	public String getGeneralDateIVwasDone() {
		return generalDateIVwasDone;
	}
	public void setGeneralDateIVwasDone(String generalDateIVwasDone) {
		this.generalDateIVwasDone = generalDateIVwasDone;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	
	

}
