package com.tricon.ruleengine.model.sheet;

public class IVFTableSheet {

	private String officeName;// A
	private String patientName;// B
	private String insName; // C
	private String taxId;// D
	private String policyHolder;// E
	private String patientDOB;// F
	private String InsContact;// G
	private String cSRName;// H
	private String policyHolderDOB;// I
	private String employerName;// J
	private String contTxRecallNP;// L
	private String ref;// L
	private String memberSSN;// M
	private String group;// N
	private String cOBStatus;// O
	private String memberId;// P
	private String aptDate;// Q
	private String payerId;// R
	private String providerName;// S
	private String insAddress;// T
	private String planType;// U
	private String planTermedDate;// V
	private String planNetwork;// W
	private String planFeeScheduleName;// X Fee Schedule Name
	// private String planIndFamilyCoverage ;//Y
	private String planEffectiveDate;// Y 26-1 Effective Date RULE ONE (Plan_EffectiveDate)
	private String planCalendarFiscalYear;// Z
	private String planAnnualMax;// AA
	private String planAnnualMaxRemaining;// AB
	private String planIndividualDeductible;// AC
	private String planIndividualDeductibleRemaining;// AD
	private String planDependentsCoveredtoAge;// AE
	private String planPreDMandatory;// AF
	private String planNonDuplicateClause;// AG
	private String planFullTimeStudentStatus;// AH
	private String planAssignmentofBenefits;// AI
	private String planCoverageBook;// AJ
	private String basicPercentage;// AK
	private String basicSubjectDeductible;// AL
	private String majorPercentage;// AM
	private String majorSubjectDeductible;// AN
	private String endodonticsPercentage;// AO
	private String endoSubjectDeductible;// AP
	private String perioSurgeryPercentage;// AQ
	private String perioSurgerySubjectDeductible;// AR
	private String preventivePercentage;// AS
	private String diagnosticPercentage;// AT
	private String pAXRaysPercentage;// AU
	private String missingToothClause;// AV
	private String replacementClause;// AW
	private String crownsD2750D2740PaysPrepSeatDate;// AX
	private String nightGuardsD9940FL;// AY
	private String basicWaitingPeriod;// AZ
	private String majorWaitingPeriod;// BA
	private String sSCD2930FL;// BB
	private String sSCD2931FL;// BC
	private String examsD0120FL;// BD
	private String examsD0140FL;// BE
	private String eExamsD0145FL;// BF
	private String examsD0150FL;// BG
	private String xRaysBWSFL;// BH
	private String xRaysPAD0220FL;// BI
	private String xRaysPAD0230FL;// BJ
	private String xRaysFMXFL;// BK
	private String xRaysBundling;// BL
	private String flourideD1208FL;// BM
	private String flourideAgeLimit;// BN
	private String varnishD1206FL;// BO
	private String varnishD1206AgeLimit;// BP
	private String sealantsD1351Percentage;// BQ
	private String sealantsD1351FL;// BR
	private String sealantsD1351AgeLimit;// BS
	private String sealantsD1351PrimaryMolarsCovered;// BT
	private String sealantsD1351PreMolarsCovered;// BU
	private String sealantsD1351PermanentMolarsCovered;// BV
	private String prophyD1110FL;// BW
	private String prophyD1120FL;// BX
	private String name1201110RollOverAgYe;// BY
	private String sRPD4341Percentage;// BZ
	private String sRPD4341FL;// CA
	private String sRPD4341QuadsPerDay;// CB
	private String sRPD4341DaysBwTreatment;// CC
	private String perioMaintenanceD4910Percentage;// CD
	private String perioMaintenanceD4910FL;// CE
	private String perioMaintenanceD4910AltWProphyD0110;// CF
	
	 
	private String FMDD4355Percentage;// CG
	private String fMDD4355FL;// CH
	private String gingivitisD4346Percentage;// CI
	private String gingivitisD4346FL;// CJ
	private String NitrousD9230Percentage;// CK
	private String iVSedationD9243Percentage;// CL
	private String iVSedationD9248Percentage;// CM
	private String extractionsMinorPercentage;// CN
	private String extractionsMajorPercentage;// CO
	private String crownLengthD4249Percentage;// CP
	private String crownLengthD4249FL;// CQ
	private String alveoD7311CoveredWithEXT;// CR
	private String alveoD7311FL;// CS
	private String alveoD7310CoveredWithEXT;// CT
	private String alveoD7310FL;// CU
	private String completeDenturesD5110D5120FL;// CV
	private String immediateDenturesD5130D5140FL;// CW
	private String partialDenturesD5213D5214FL;// CX
	private String interimPartialDenturesD5214FL;// CY
	private String boneGraftsD7953CoveredWithEXT;//CZ
	private String boneGraftsD7953FL;//DA
	private String implantCoverageD6010Percentage;//DB
	private String implantCoverageD6057Percentage;//DC
	private String implantCoverageD6190Percentage;//DD
	private String implantSupportedPorcCeramicD6065Percentage;//DE
	private String postCompositesD2391Percentage;//DF
	private String postCompositesD2391FL;//DG
	private String posteriorCompositesD2391Downgrade;//DH
	private String crownsD2750D2740Percentage;//DI
	private String crownsD2750D2740FL;//DJ
	private String crownsD2750D2740Downgrade;//DK
	private String nightGuardsD9940Percentage;//DL
	private String d9310Percentage;//DM
	private String d9310FL;//DN
	private String buildUpsD2950Covered;//DO
	private String buildUpsD2950FL;//DP
	private String buildUpsD2950SameDayCrown;//DQ
	private String orthoPercentage;//DR
	private String orthoMax;//DS
	private String orthoAgeLimit;//DT
	private String orthoSubjectDeductible;//DU
	private String FillingsBundling;//DV
	private String comments;//DW
	private String generalBenefitsVerifiedBy;//DW
	private String generalDateIVwasDone;//DY
	private String patientId;//DZ
    //history
	private IVFHistorySheet hs;
	private String craRequired;//KY
	private String claimFillingLimit;//KZ
	private String uniqueID;//LA
	
	
	
	public IVFTableSheet() {
		
	}
	public IVFTableSheet(String officeName, String patientName, String insName, String taxId, String policyHolder,
			String patientDOB, String insContact, String cSRName, String policyHolderDOB, String employerName,
			String contTxRecallNP, String ref, String memberSSN, String group, String cOBStatus, String memberId,
			String aptDate, String payerId, String providerName, String insAddress, String planType,
			String planTermedDate, String planNetwork, String planFeeScheduleName, String planEffectiveDate,
			String planCalendarFiscalYear, String planAnnualMax, String planAnnualMaxRemaining,
			String planIndividualDeductible, String planIndividualDeductibleRemaining,
			String planDependentsCoveredtoAge, String planPreDMandatory, String planNonDuplicateClause,
			String planFullTimeStudentStatus, String planAssignmentofBenefits, String planCoverageBook,
			String basicPercentage, String basicSubjectDeductible, String majorPercentage,
			String majorSubjectDeductible, String endodonticsPercentage, String endoSubjectDeductible,
			String perioSurgeryPercentage, String perioSurgerySubjectDeductible, String preventivePercentage,
			String diagnosticPercentage, String pAXRaysPercentage, String missingToothClause, String replacementClause,
			String crownsD2750D2740PaysPrepSeatDate, String nightGuardsD9940FL, String basicWaitingPeriod,
			String majorWaitingPeriod, String sSCD2930FL, String sSCD2931FL, String examsD0120FL, String examsD0140FL,
			String eExamsD0145FL, String examsD0150FL, String xRaysBWSFL, String xRaysPAD0220FL, String xRaysPAD0230FL,
			String xRaysFMXFL, String xRaysBundling, String flourideD1208FL, String flourideAgeLimit,
			String varnishD1206FL, String varnishD1206AgeLimit, String sealantsD1351Percentage, String sealantsD1351FL,
			String sealantsD1351AgeLimit, String sealantsD1351PrimaryMolarsCovered,
			String sealantsD1351PreMolarsCovered, String sealantsD1351PermanentMolarsCovered, String prophyD1110FL,
			String prophyD1120FL, String name1201110RollOverAgYe, String sRPD4341Percentage, String sRPD4341FL,
			String sRPD4341QuadsPerDay, String sRPD4341DaysBwTreatment, String perioMaintenanceD4910Percentage,
			String perioMaintenanceD4910FL, String perioMaintenanceD4910AltWProphyD0110, String fMDD4355Percentage,
			String fMDD4355FL, String gingivitisD4346Percentage, String gingivitisD4346FL,
			String nitrousD9230Percentage, String iVSedationD9243Percentage, String iVSedationD9248Percentage,
			String extractionsMinorPercentage, String extractionsMajorPercentage, String crownLengthD4249Percentage,
			String crownLengthD4249FL, String alveoD7311CoveredWithEXT, String alveoD7311FL,
			String alveoD7310CoveredWithEXT, String alveoD7310FL, String completeDenturesD5110D5120FL,
			String immediateDenturesD5130D5140FL, String partialDenturesD5213D5214FL,
			String interimPartialDenturesD5214FL, String boneGraftsD7953CoveredWithEXT, String boneGraftsD7953FL,
			String implantCoverageD6010Percentage, String implantCoverageD6057Percentage,
			String implantCoverageD6190Percentage, String implantSupportedPorcCeramicD6065Percentage,
			String postCompositesD2391Percentage, String postCompositesD2391FL,
			String posteriorCompositesD2391Downgrade, String crownsD2750D2740Percentage, String crownsD2750D2740FL,
			String crownsD2750D2740Downgrade, String nightGuardsD9940Percentage, String d9310Percentage, String d9310fl,
			String buildUpsD2950Covered, String buildUpsD2950FL, String buildUpsD2950SameDayCrown,
			String orthoPercentage, String orthoMax, String orthoAgeLimit, String orthoSubjectDeductible,
			String fillingsBundling, String comments, String generalBenefitsVerifiedBy, String generalDateIVwasDone,
			String patientId,  IVFHistorySheet hs,String craRequired, String claimFillingLimit, String uniqueID) {
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
		this.planEffectiveDate = planEffectiveDate;
		this.planCalendarFiscalYear = planCalendarFiscalYear;
		this.planAnnualMax = planAnnualMax;
		this.planAnnualMaxRemaining = planAnnualMaxRemaining;
		this.planIndividualDeductible = planIndividualDeductible;
		this.planIndividualDeductibleRemaining = planIndividualDeductibleRemaining;
		this.planDependentsCoveredtoAge = planDependentsCoveredtoAge;
		this.planPreDMandatory = planPreDMandatory;
		this.planNonDuplicateClause = planNonDuplicateClause;
		this.planFullTimeStudentStatus = planFullTimeStudentStatus;
		this.planAssignmentofBenefits = planAssignmentofBenefits;
		this.planCoverageBook = planCoverageBook;
		this.basicPercentage = basicPercentage;
		this.basicSubjectDeductible = basicSubjectDeductible;
		this.majorPercentage = majorPercentage;
		this.majorSubjectDeductible = majorSubjectDeductible;
		this.endodonticsPercentage = endodonticsPercentage;
		this.endoSubjectDeductible = endoSubjectDeductible;
		this.perioSurgeryPercentage = perioSurgeryPercentage;
		this.perioSurgerySubjectDeductible = perioSurgerySubjectDeductible;
		this.preventivePercentage = preventivePercentage;
		this.diagnosticPercentage = diagnosticPercentage;
		this.pAXRaysPercentage = pAXRaysPercentage;
		this.missingToothClause = missingToothClause;
		this.replacementClause = replacementClause;
		this.crownsD2750D2740PaysPrepSeatDate = crownsD2750D2740PaysPrepSeatDate;
		this.nightGuardsD9940FL = nightGuardsD9940FL;
		this.basicWaitingPeriod = basicWaitingPeriod;
		this.majorWaitingPeriod = majorWaitingPeriod;
		this.sSCD2930FL = sSCD2930FL;
		this.sSCD2931FL = sSCD2931FL;
		this.examsD0120FL = examsD0120FL;
		this.examsD0140FL = examsD0140FL;
		this.eExamsD0145FL = eExamsD0145FL;
		this.examsD0150FL = examsD0150FL;
		this.xRaysBWSFL = xRaysBWSFL;
		this.xRaysPAD0220FL = xRaysPAD0220FL;
		this.xRaysPAD0230FL = xRaysPAD0230FL;
		this.xRaysFMXFL = xRaysFMXFL;
		this.xRaysBundling = xRaysBundling;
		this.flourideD1208FL = flourideD1208FL;
		this.flourideAgeLimit = flourideAgeLimit;
		this.varnishD1206FL = varnishD1206FL;
		this.varnishD1206AgeLimit = varnishD1206AgeLimit;
		this.sealantsD1351Percentage = sealantsD1351Percentage;
		this.sealantsD1351FL = sealantsD1351FL;
		this.sealantsD1351AgeLimit = sealantsD1351AgeLimit;
		this.sealantsD1351PrimaryMolarsCovered = sealantsD1351PrimaryMolarsCovered;
		this.sealantsD1351PreMolarsCovered = sealantsD1351PreMolarsCovered;
		this.sealantsD1351PermanentMolarsCovered = sealantsD1351PermanentMolarsCovered;
		this.prophyD1110FL = prophyD1110FL;
		this.prophyD1120FL = prophyD1120FL;
		this.name1201110RollOverAgYe = name1201110RollOverAgYe;
		this.sRPD4341Percentage = sRPD4341Percentage;
		this.sRPD4341FL = sRPD4341FL;
		this.sRPD4341QuadsPerDay = sRPD4341QuadsPerDay;
		this.sRPD4341DaysBwTreatment = sRPD4341DaysBwTreatment;
		this.perioMaintenanceD4910Percentage = perioMaintenanceD4910Percentage;
		this.perioMaintenanceD4910FL = perioMaintenanceD4910FL;
		this.perioMaintenanceD4910AltWProphyD0110 = perioMaintenanceD4910AltWProphyD0110;
		FMDD4355Percentage = fMDD4355Percentage;
		this.fMDD4355FL = fMDD4355FL;
		this.gingivitisD4346Percentage = gingivitisD4346Percentage;
		this.gingivitisD4346FL = gingivitisD4346FL;
		NitrousD9230Percentage = nitrousD9230Percentage;
		this.iVSedationD9243Percentage = iVSedationD9243Percentage;
		this.iVSedationD9248Percentage = iVSedationD9248Percentage;
		this.extractionsMinorPercentage = extractionsMinorPercentage;
		this.extractionsMajorPercentage = extractionsMajorPercentage;
		this.crownLengthD4249Percentage = crownLengthD4249Percentage;
		this.crownLengthD4249FL = crownLengthD4249FL;
		this.alveoD7311CoveredWithEXT = alveoD7311CoveredWithEXT;
		this.alveoD7311FL = alveoD7311FL;
		this.alveoD7310CoveredWithEXT = alveoD7310CoveredWithEXT;
		this.alveoD7310FL = alveoD7310FL;
		this.completeDenturesD5110D5120FL = completeDenturesD5110D5120FL;
		this.immediateDenturesD5130D5140FL = immediateDenturesD5130D5140FL;
		this.partialDenturesD5213D5214FL = partialDenturesD5213D5214FL;
		this.interimPartialDenturesD5214FL = interimPartialDenturesD5214FL;
		this.boneGraftsD7953CoveredWithEXT = boneGraftsD7953CoveredWithEXT;
		this.boneGraftsD7953FL = boneGraftsD7953FL;
		this.implantCoverageD6010Percentage = implantCoverageD6010Percentage;
		this.implantCoverageD6057Percentage = implantCoverageD6057Percentage;
		this.implantCoverageD6190Percentage = implantCoverageD6190Percentage;
		this.implantSupportedPorcCeramicD6065Percentage = implantSupportedPorcCeramicD6065Percentage;
		this.postCompositesD2391Percentage = postCompositesD2391Percentage;
		this.postCompositesD2391FL = postCompositesD2391FL;
		this.posteriorCompositesD2391Downgrade = posteriorCompositesD2391Downgrade;
		this.crownsD2750D2740Percentage = crownsD2750D2740Percentage;
		this.crownsD2750D2740FL = crownsD2750D2740FL;
		this.crownsD2750D2740Downgrade = crownsD2750D2740Downgrade;
		this.nightGuardsD9940Percentage = nightGuardsD9940Percentage;
		this.d9310Percentage = d9310Percentage;
		this.d9310FL = d9310fl;
		this.buildUpsD2950Covered = buildUpsD2950Covered;
		this.buildUpsD2950FL = buildUpsD2950FL;
		this.buildUpsD2950SameDayCrown = buildUpsD2950SameDayCrown;
		this.orthoPercentage = orthoPercentage;
		this.orthoMax = orthoMax;
		this.orthoAgeLimit = orthoAgeLimit;
		this.orthoSubjectDeductible = orthoSubjectDeductible;
		FillingsBundling = fillingsBundling;
		this.comments = comments;
		this.generalBenefitsVerifiedBy = generalBenefitsVerifiedBy;
		this.generalDateIVwasDone = generalDateIVwasDone;
		this.patientId = patientId;
		this.hs = hs;
		this.craRequired=craRequired;
		this.claimFillingLimit=claimFillingLimit;
		this.uniqueID = uniqueID;
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
	public String getPlanEffectiveDate() {
		return planEffectiveDate;
	}
	public void setPlanEffectiveDate(String planEffectiveDate) {
		this.planEffectiveDate = planEffectiveDate;
	}
	public String getPlanCalendarFiscalYear() {
		return planCalendarFiscalYear;
	}
	public void setPlanCalendarFiscalYear(String planCalendarFiscalYear) {
		this.planCalendarFiscalYear = planCalendarFiscalYear;
	}
	public String getPlanAnnualMax() {
		return planAnnualMax;
	}
	public void setPlanAnnualMax(String planAnnualMax) {
		this.planAnnualMax = planAnnualMax;
	}
	public String getPlanAnnualMaxRemaining() {
		return planAnnualMaxRemaining;
	}
	public void setPlanAnnualMaxRemaining(String planAnnualMaxRemaining) {
		this.planAnnualMaxRemaining = planAnnualMaxRemaining;
	}
	public String getPlanIndividualDeductible() {
		return planIndividualDeductible;
	}
	public void setPlanIndividualDeductible(String planIndividualDeductible) {
		this.planIndividualDeductible = planIndividualDeductible;
	}
	public String getPlanIndividualDeductibleRemaining() {
		return planIndividualDeductibleRemaining;
	}
	public void setPlanIndividualDeductibleRemaining(String planIndividualDeductibleRemaining) {
		this.planIndividualDeductibleRemaining = planIndividualDeductibleRemaining;
	}
	public String getPlanDependentsCoveredtoAge() {
		return planDependentsCoveredtoAge;
	}
	public void setPlanDependentsCoveredtoAge(String planDependentsCoveredtoAge) {
		this.planDependentsCoveredtoAge = planDependentsCoveredtoAge;
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
	public String getPlanCoverageBook() {
		return planCoverageBook;
	}
	public void setPlanCoverageBook(String planCoverageBook) {
		this.planCoverageBook = planCoverageBook;
	}
	public String getBasicPercentage() {
		return basicPercentage;
	}
	public void setBasicPercentage(String basicPercentage) {
		this.basicPercentage = basicPercentage;
	}
	public String getBasicSubjectDeductible() {
		return basicSubjectDeductible;
	}
	public void setBasicSubjectDeductible(String basicSubjectDeductible) {
		this.basicSubjectDeductible = basicSubjectDeductible;
	}
	public String getMajorPercentage() {
		return majorPercentage;
	}
	public void setMajorPercentage(String majorPercentage) {
		this.majorPercentage = majorPercentage;
	}
	public String getMajorSubjectDeductible() {
		return majorSubjectDeductible;
	}
	public void setMajorSubjectDeductible(String majorSubjectDeductible) {
		this.majorSubjectDeductible = majorSubjectDeductible;
	}
	public String getEndodonticsPercentage() {
		return endodonticsPercentage;
	}
	public void setEndodonticsPercentage(String endodonticsPercentage) {
		this.endodonticsPercentage = endodonticsPercentage;
	}
	public String getEndoSubjectDeductible() {
		return endoSubjectDeductible;
	}
	public void setEndoSubjectDeductible(String endoSubjectDeductible) {
		this.endoSubjectDeductible = endoSubjectDeductible;
	}
	public String getPerioSurgeryPercentage() {
		return perioSurgeryPercentage;
	}
	public void setPerioSurgeryPercentage(String perioSurgeryPercentage) {
		this.perioSurgeryPercentage = perioSurgeryPercentage;
	}
	public String getPerioSurgerySubjectDeductible() {
		return perioSurgerySubjectDeductible;
	}
	public void setPerioSurgerySubjectDeductible(String perioSurgerySubjectDeductible) {
		this.perioSurgerySubjectDeductible = perioSurgerySubjectDeductible;
	}
	public String getPreventivePercentage() {
		return preventivePercentage;
	}
	public void setPreventivePercentage(String preventivePercentage) {
		this.preventivePercentage = preventivePercentage;
	}
	public String getDiagnosticPercentage() {
		return diagnosticPercentage;
	}
	public void setDiagnosticPercentage(String diagnosticPercentage) {
		this.diagnosticPercentage = diagnosticPercentage;
	}
	public String getpAXRaysPercentage() {
		return pAXRaysPercentage;
	}
	public void setpAXRaysPercentage(String pAXRaysPercentage) {
		this.pAXRaysPercentage = pAXRaysPercentage;
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
	public String getsSCD2930FL() {
		return sSCD2930FL;
	}
	public void setsSCD2930FL(String sSCD2930FL) {
		this.sSCD2930FL = sSCD2930FL;
	}
	public String getsSCD2931FL() {
		return sSCD2931FL;
	}
	public void setsSCD2931FL(String sSCD2931FL) {
		this.sSCD2931FL = sSCD2931FL;
	}
	public String getExamsD0120FL() {
		return examsD0120FL;
	}
	public void setExamsD0120FL(String examsD0120FL) {
		this.examsD0120FL = examsD0120FL;
	}
	public String getExamsD0140FL() {
		return examsD0140FL;
	}
	public void setExamsD0140FL(String examsD0140FL) {
		this.examsD0140FL = examsD0140FL;
	}
	public String geteExamsD0145FL() {
		return eExamsD0145FL;
	}
	public void seteExamsD0145FL(String eExamsD0145FL) {
		this.eExamsD0145FL = eExamsD0145FL;
	}
	public String getExamsD0150FL() {
		return examsD0150FL;
	}
	public void setExamsD0150FL(String examsD0150FL) {
		this.examsD0150FL = examsD0150FL;
	}
	public String getxRaysBWSFL() {
		return xRaysBWSFL;
	}
	public void setxRaysBWSFL(String xRaysBWSFL) {
		this.xRaysBWSFL = xRaysBWSFL;
	}
	public String getxRaysPAD0220FL() {
		return xRaysPAD0220FL;
	}
	public void setxRaysPAD0220FL(String xRaysPAD0220FL) {
		this.xRaysPAD0220FL = xRaysPAD0220FL;
	}
	public String getxRaysPAD0230FL() {
		return xRaysPAD0230FL;
	}
	public void setxRaysPAD0230FL(String xRaysPAD0230FL) {
		this.xRaysPAD0230FL = xRaysPAD0230FL;
	}
	public String getxRaysFMXFL() {
		return xRaysFMXFL;
	}
	public void setxRaysFMXFL(String xRaysFMXFL) {
		this.xRaysFMXFL = xRaysFMXFL;
	}
	public String getxRaysBundling() {
		return xRaysBundling;
	}
	public void setxRaysBundling(String xRaysBundling) {
		this.xRaysBundling = xRaysBundling;
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
	public String getSealantsD1351Percentage() {
		return sealantsD1351Percentage;
	}
	public void setSealantsD1351Percentage(String sealantsD1351Percentage) {
		this.sealantsD1351Percentage = sealantsD1351Percentage;
	}
	public String getSealantsD1351FL() {
		return sealantsD1351FL;
	}
	public void setSealantsD1351FL(String sealantsD1351FL) {
		this.sealantsD1351FL = sealantsD1351FL;
	}
	public String getSealantsD1351AgeLimit() {
		return sealantsD1351AgeLimit;
	}
	public void setSealantsD1351AgeLimit(String sealantsD1351AgeLimit) {
		this.sealantsD1351AgeLimit = sealantsD1351AgeLimit;
	}
	public String getSealantsD1351PrimaryMolarsCovered() {
		return sealantsD1351PrimaryMolarsCovered;
	}
	public void setSealantsD1351PrimaryMolarsCovered(String sealantsD1351PrimaryMolarsCovered) {
		this.sealantsD1351PrimaryMolarsCovered = sealantsD1351PrimaryMolarsCovered;
	}
	public String getSealantsD1351PreMolarsCovered() {
		return sealantsD1351PreMolarsCovered;
	}
	public void setSealantsD1351PreMolarsCovered(String sealantsD1351PreMolarsCovered) {
		this.sealantsD1351PreMolarsCovered = sealantsD1351PreMolarsCovered;
	}
	public String getSealantsD1351PermanentMolarsCovered() {
		return sealantsD1351PermanentMolarsCovered;
	}
	public void setSealantsD1351PermanentMolarsCovered(String sealantsD1351PermanentMolarsCovered) {
		this.sealantsD1351PermanentMolarsCovered = sealantsD1351PermanentMolarsCovered;
	}
	public String getProphyD1110FL() {
		return prophyD1110FL;
	}
	public void setProphyD1110FL(String prophyD1110FL) {
		this.prophyD1110FL = prophyD1110FL;
	}
	public String getProphyD1120FL() {
		return prophyD1120FL;
	}
	public void setProphyD1120FL(String prophyD1120FL) {
		this.prophyD1120FL = prophyD1120FL;
	}
	public String getName1201110RollOverAgYe() {
		return name1201110RollOverAgYe;
	}
	public void setName1201110RollOverAgYe(String name1201110RollOverAgYe) {
		this.name1201110RollOverAgYe = name1201110RollOverAgYe;
	}
	public String getsRPD4341Percentage() {
		return sRPD4341Percentage;
	}
	public void setsRPD4341Percentage(String sRPD4341Percentage) {
		this.sRPD4341Percentage = sRPD4341Percentage;
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
	public String getPerioMaintenanceD4910Percentage() {
		return perioMaintenanceD4910Percentage;
	}
	public void setPerioMaintenanceD4910Percentage(String perioMaintenanceD4910Percentage) {
		this.perioMaintenanceD4910Percentage = perioMaintenanceD4910Percentage;
	}
	public String getPerioMaintenanceD4910FL() {
		return perioMaintenanceD4910FL;
	}
	public void setPerioMaintenanceD4910FL(String perioMaintenanceD4910FL) {
		this.perioMaintenanceD4910FL = perioMaintenanceD4910FL;
	}
	public String getPerioMaintenanceD4910AltWProphyD0110() {
		return perioMaintenanceD4910AltWProphyD0110;
	}
	public void setPerioMaintenanceD4910AltWProphyD0110(String perioMaintenanceD4910AltWProphyD0110) {
		this.perioMaintenanceD4910AltWProphyD0110 = perioMaintenanceD4910AltWProphyD0110;
	}
	public String getFMDD4355Percentage() {
		return FMDD4355Percentage;
	}
	public void setFMDD4355Percentage(String fMDD4355Percentage) {
		FMDD4355Percentage = fMDD4355Percentage;
	}
	public String getfMDD4355FL() {
		return fMDD4355FL;
	}
	public void setfMDD4355FL(String fMDD4355FL) {
		this.fMDD4355FL = fMDD4355FL;
	}
	public String getGingivitisD4346Percentage() {
		return gingivitisD4346Percentage;
	}
	public void setGingivitisD4346Percentage(String gingivitisD4346Percentage) {
		this.gingivitisD4346Percentage = gingivitisD4346Percentage;
	}
	public String getGingivitisD4346FL() {
		return gingivitisD4346FL;
	}
	public void setGingivitisD4346FL(String gingivitisD4346FL) {
		this.gingivitisD4346FL = gingivitisD4346FL;
	}
	public String getNitrousD9230Percentage() {
		return NitrousD9230Percentage;
	}
	public void setNitrousD9230Percentage(String nitrousD9230Percentage) {
		NitrousD9230Percentage = nitrousD9230Percentage;
	}
	public String getiVSedationD9243Percentage() {
		return iVSedationD9243Percentage;
	}
	public void setiVSedationD9243Percentage(String iVSedationD9243Percentage) {
		this.iVSedationD9243Percentage = iVSedationD9243Percentage;
	}
	public String getiVSedationD9248Percentage() {
		return iVSedationD9248Percentage;
	}
	public void setiVSedationD9248Percentage(String iVSedationD9248Percentage) {
		this.iVSedationD9248Percentage = iVSedationD9248Percentage;
	}
	public String getExtractionsMinorPercentage() {
		return extractionsMinorPercentage;
	}
	public void setExtractionsMinorPercentage(String extractionsMinorPercentage) {
		this.extractionsMinorPercentage = extractionsMinorPercentage;
	}
	public String getExtractionsMajorPercentage() {
		return extractionsMajorPercentage;
	}
	public void setExtractionsMajorPercentage(String extractionsMajorPercentage) {
		this.extractionsMajorPercentage = extractionsMajorPercentage;
	}
	public String getCrownLengthD4249Percentage() {
		return crownLengthD4249Percentage;
	}
	public void setCrownLengthD4249Percentage(String crownLengthD4249Percentage) {
		this.crownLengthD4249Percentage = crownLengthD4249Percentage;
	}
	public String getCrownLengthD4249FL() {
		return crownLengthD4249FL;
	}
	public void setCrownLengthD4249FL(String crownLengthD4249FL) {
		this.crownLengthD4249FL = crownLengthD4249FL;
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
		return partialDenturesD5213D5214FL;
	}
	public void setPartialDenturesD5213D5214FL(String partialDenturesD5213D5214FL) {
		this.partialDenturesD5213D5214FL = partialDenturesD5213D5214FL;
	}
	public String getInterimPartialDenturesD5214FL() {
		return interimPartialDenturesD5214FL;
	}
	public void setInterimPartialDenturesD5214FL(String interimPartialDenturesD5214FL) {
		this.interimPartialDenturesD5214FL = interimPartialDenturesD5214FL;
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
	public String getImplantCoverageD6010Percentage() {
		return implantCoverageD6010Percentage;
	}
	public void setImplantCoverageD6010Percentage(String implantCoverageD6010Percentage) {
		this.implantCoverageD6010Percentage = implantCoverageD6010Percentage;
	}
	public String getImplantCoverageD6057Percentage() {
		return implantCoverageD6057Percentage;
	}
	public void setImplantCoverageD6057Percentage(String implantCoverageD6057Percentage) {
		this.implantCoverageD6057Percentage = implantCoverageD6057Percentage;
	}
	public String getImplantCoverageD6190Percentage() {
		return implantCoverageD6190Percentage;
	}
	public void setImplantCoverageD6190Percentage(String implantCoverageD6190Percentage) {
		this.implantCoverageD6190Percentage = implantCoverageD6190Percentage;
	}
	public String getImplantSupportedPorcCeramicD6065Percentage() {
		return implantSupportedPorcCeramicD6065Percentage;
	}
	public void setImplantSupportedPorcCeramicD6065Percentage(String implantSupportedPorcCeramicD6065Percentage) {
		this.implantSupportedPorcCeramicD6065Percentage = implantSupportedPorcCeramicD6065Percentage;
	}
	public String getPostCompositesD2391Percentage() {
		return postCompositesD2391Percentage;
	}
	public void setPostCompositesD2391Percentage(String postCompositesD2391Percentage) {
		this.postCompositesD2391Percentage = postCompositesD2391Percentage;
	}
	public String getPostCompositesD2391FL() {
		return postCompositesD2391FL;
	}
	public void setPostCompositesD2391FL(String postCompositesD2391FL) {
		this.postCompositesD2391FL = postCompositesD2391FL;
	}
	public String getPosteriorCompositesD2391Downgrade() {
		return posteriorCompositesD2391Downgrade;
	}
	public void setPosteriorCompositesD2391Downgrade(String posteriorCompositesD2391Downgrade) {
		this.posteriorCompositesD2391Downgrade = posteriorCompositesD2391Downgrade;
	}
	public String getCrownsD2750D2740Percentage() {
		return crownsD2750D2740Percentage;
	}
	public void setCrownsD2750D2740Percentage(String crownsD2750D2740Percentage) {
		this.crownsD2750D2740Percentage = crownsD2750D2740Percentage;
	}
	public String getCrownsD2750D2740FL() {
		return crownsD2750D2740FL;
	}
	public void setCrownsD2750D2740FL(String crownsD2750D2740FL) {
		this.crownsD2750D2740FL = crownsD2750D2740FL;
	}
	public String getCrownsD2750D2740Downgrade() {
		return crownsD2750D2740Downgrade;
	}
	public void setCrownsD2750D2740Downgrade(String crownsD2750D2740Downgrade) {
		this.crownsD2750D2740Downgrade = crownsD2750D2740Downgrade;
	}
	public String getNightGuardsD9940Percentage() {
		return nightGuardsD9940Percentage;
	}
	public void setNightGuardsD9940Percentage(String nightGuardsD9940Percentage) {
		this.nightGuardsD9940Percentage = nightGuardsD9940Percentage;
	}
	public String getD9310Percentage() {
		return d9310Percentage;
	}
	public void setD9310Percentage(String d9310Percentage) {
		this.d9310Percentage = d9310Percentage;
	}
	public String getD9310FL() {
		return d9310FL;
	}
	public void setD9310FL(String d9310fl) {
		d9310FL = d9310fl;
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
	public String getOrthoPercentage() {
		return orthoPercentage;
	}
	public void setOrthoPercentage(String orthoPercentage) {
		this.orthoPercentage = orthoPercentage;
	}
	public String getOrthoMax() {
		return orthoMax;
	}
	public void setOrthoMax(String orthoMax) {
		this.orthoMax = orthoMax;
	}
	public String getOrthoAgeLimit() {
		return orthoAgeLimit;
	}
	public void setOrthoAgeLimit(String orthoAgeLimit) {
		this.orthoAgeLimit = orthoAgeLimit;
	}
	public String getOrthoSubjectDeductible() {
		return orthoSubjectDeductible;
	}
	public void setOrthoSubjectDeductible(String orthoSubjectDeductible) {
		this.orthoSubjectDeductible = orthoSubjectDeductible;
	}
	public String getFillingsBundling() {
		return FillingsBundling;
	}
	public void setFillingsBundling(String fillingsBundling) {
		FillingsBundling = fillingsBundling;
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
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public IVFHistorySheet getHs() {
		return hs;
	}
	public void setHs(IVFHistorySheet hs) {
		this.hs = hs;
	}
	public String getClaimFillingLimit() {
		return claimFillingLimit;
	}
	public void setClaimFillingLimit(String claimFillingLimit) {
		this.claimFillingLimit = claimFillingLimit;
	}
	public String getCraRequired() {
		return craRequired;
	}
	public void setCraRequired(String craRequired) {
		this.craRequired = craRequired;
	}
	
	



	
}
