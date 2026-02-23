package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "patient_detail")
public class PatientDetail extends BaseAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6920430864299568436L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private Office office;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "iv_form_type_id",nullable=true)
	private IVFormType iVFormType;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "patientDetail")
	PatientDetail2 patientDetails2;


	@Column(name = "ins_name", length = 100)
	private String insName;// C

	@Column(name = "tax_id", length = 100)
	private String taxId;// D

	@Column(name = "policy_holder", length = 255)
	private String policyHolder;// E

	@Column(name = "ins_contact", length = 255)
	private String InsContact;// G

	@Column(name = "cs_sr_name", length = 255)
	private String cSRName;// H

	@Column(name = "policy_holder_dob", length = 15)
	private String policyHolderDOB;// I

	@Column(name = "employer_name", length = 255)
	private String employerName;// J

	@Column(name = "const_tx_recall_np", length = 50)
	private String contTxRecallNP;// L

	@Column(name = "ref", length = 70)
	private String ref;// L

	@Column(name = "member_ssn", length = 50)
	private String memberSSN;// M

	@Column(name = "group_p", length = 50)
	private String group;// N

	@Column(name = "cob_status", length = 50)
	private String cOBStatus;// O

	@Column(name = "memberId", length = 50)
	private String memberId;// P

	@Column(name = "apt_date", length = 50)
	private String aptDate;// Q

	@Column(name = "payer_id", length = 50)
	private String payerId;// R

	@Column(name = "provider_name", length = 255)
	private String providerName;// S

	@Column(name = "ins_address", columnDefinition = "text")
	private String insAddress;// T

	@Column(name = "plan_type", length = 50)
	private String planType;// U

	@Column(name = "plan_termed_date", length = 15)
	private String planTermedDate;// V

	@Column(name = "plan_network", length = 50)
	private String planNetwork;// W

	@Column(name = "plan_fee_schedule_name", length = 255)
	private String planFeeScheduleName;// X Fee Schedule Name

	@Column(name = "plan_effective_date", length = 15)
	private String planEffectiveDate;// Y 26-1 Effective Date RULE ONE (Plan_EffectiveDate)

	@Column(name = "plan_calendar_fiscal_year", length = 5)
	private String planCalendarFiscalYear;// Z

	@Column(name = "plan_annual_max", length = 10)
	private String planAnnualMax;// AA

	@Column(name = "plan_annual_max_remaining", length = 10)
	private String planAnnualMaxRemaining;// AB

	@Column(name = "plan_individual_dedudtible", length = 10)
	private String planIndividualDeductible;// AC

	@Column(name = "plan_individual_deductible_remaining", length = 10)
	private String planIndividualDeductibleRemaining;// AD

	@Column(name = "plan_dependents_covered_to_age", length = 50)
	private String planDependentsCoveredtoAge;// AE

	@Column(name = "plan_pre_d_mandatory", length = 255)
	private String planPreDMandatory;// AF

	@Column(name = "plan_non_duplicate_clause", length = 50)
	private String planNonDuplicateClause;// AG

	@Column(name = "plan_full_time_student_status", length = 50)
	private String planFullTimeStudentStatus;// AH

	@Column(name = "plan_assignment_of_benefits", length = 50)
	private String planAssignmentofBenefits;// AI

	@Column(name = "plan_coverage_book", length = 255)
	private String planCoverageBook;// AJ

	@Column(name = "basic_percentage", length = 50)
	private String basicPercentage;// AK

	@Column(name = "basic_subject_deductible", length = 50)
	private String basicSubjectDeductible;// AL

	@Column(name = "major_percentage", length = 50)
	private String majorPercentage;// AM

	@Column(name = "major_subject_deductible", length = 50)
	private String majorSubjectDeductible;// AN

	@Column(name = "endo_dontics_percentage", length = 50)
	private String endodonticsPercentage;// AO

	@Column(name = "endo_subjectdeductible", length = 50)
	private String endoSubjectDeductible;// AP

	@Column(name = "perio_surgrey_percentage", length = 50)
	private String perioSurgeryPercentage;// AQ

	@Column(name = "perio_surgery_subject_deductible", length = 50)
	private String perioSurgerySubjectDeductible;// AR

	@Column(name = "preventive_percentage", length = 50)
	private String preventivePercentage;// AS

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "preventive_sub_ded", length = 50)
	private String preventiveSubDed;// percentages13

	// END

	@Column(name = "diagnostic_percentage", length = 50)
	private String diagnosticPercentage;// AT

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "diagnostic_sub_ded", length = 50)
	private String diagnosticSubDed;// percentages14
	// END

	@Column(name = "pa_xrays_percentage", length = 50)
	private String pAXRaysPercentage;// AU

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "pa_xrays_sub_ded", length = 50)
	private String pAXRaysSubDed;// percentages15

	@Column(name = "fmx_per", length = 15)
	private String fmxPer;// percentages16

	// END

	@Column(name = "missingtooth_clause", length = 50)
	private String missingToothClause;// AV

	@Column(name = "replacementclause", length = 100)
	private String replacementClause;// AW

	@Column(name = "crowns_d2750D2740_pays_prep_seat_date", length = 50)
	private String crownsD2750D2740PaysPrepSeatDate;// AX

	@Column(name = "night_guards_d9940fl", length = 50)
	private String nightGuardsD9940FL;// AY

	@Column(name = "basic_waiting_period", length = 50)
	private String basicWaitingPeriod;// AZ

	@Column(name = "major_waiting_period", length = 50)
	private String majorWaitingPeriod;// BA

	@Column(name = "sscd2930fl", length = 50)
	private String sSCD2930FL;// BB

	@Column(name = "sscd2931fl", length = 50)
	private String sSCD2931FL;// BC

	@Column(name = "exam_d0120_fl", length = 50)
	private String examsD0120FL;// BD

	@Column(name = "exams_d0140_fl", length = 50)
	private String examsD0140FL;// BE

	@Column(name = "eexams_d0145_fl", length = 50)
	private String eExamsD0145FL;// BF

	@Column(name = "exams_d0150_fl", length = 50)
	private String examsD0150FL;// BG

	@Column(name = "x_rays_bw_sfl", length = 50)
	private String xRaysBWSFL;// BH

	@Column(name = "x_rays_pad0220_fl", length = 50)
	private String xRaysPAD0220FL;// BI

	@Column(name = "x_rays_pad0230_fl", length = 50)
	private String xRaysPAD0230FL;// BJ

	@Column(name = "x_rays_fm_xfl", length = 50)
	private String xRaysFMXFL;// BK

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "den_5225_per", length = 20)
	private String den5225Per;// den5225

	@Column(name = "den_f_5225_fr", length = 50)
	private String denf5225FR;// denf5225

	@Column(name = "den_5226_per", length = 20)
	private String den5226Per;// den5226

	@Column(name = "den_f_5226_fr", length = 50)
	private String denf5226Fr;// denf5226

	// END
	@Column(name = "x_rays_bundling", length = 50)
	private String xRaysBundling;// BL

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "bridges1", length = 20)
	private String bridges1;// bridges1

	@Column(name = "bridges2", length = 50)
	private String bridges2;// bridges2

	@Column(name = "will_downgrade_applicable", length = 20)
	private String willDowngradeApplicable;// cdowngrade

	// END
	@Column(name = "flouride_d1208_fl", length = 50)
	private String flourideD1208FL;// BM

	@Column(name = "flouride_age_limit", length = 50)
	private String flourideAgeLimit;// BN

	@Column(name = "varnish_d1206_fl", length = 50)
	private String varnishD1206FL;// BO

	@Column(name = "varnish_d1206age_limit", length = 50)
	private String varnishD1206AgeLimit;// BP

	@Column(name = "sealants_d1351_percentage", length = 50)
	private String sealantsD1351Percentage;// BQ

	@Column(name = "sealants_d1351_fl", length = 50)
	private String sealantsD1351FL;// BR

	@Column(name = "sealants_d1351_age_limit", length = 50)
	private String sealantsD1351AgeLimit;// BS

	@Column(name = "sealants_d1351_primary_molars_covered", length = 50)
	private String sealantsD1351PrimaryMolarsCovered;// BT

	@Column(name = "sealants_d1351_pre_molars_covered", length = 50)
	private String sealantsD1351PreMolarsCovered;// BU

	@Column(name = "sealants_d1351_permanent_molars_covered", length = 50)
	private String sealantsD1351PermanentMolarsCovered;// BV

	@Column(name = "prophy_d1110_fl", length = 50)
	private String prophyD1110FL;// BW

	@Column(name = "prophy_d1120_fl", length = 50)
	private String prophyD1120FL;// BX

	@Column(name = "name1201110_roll_over_age", length = 50)
	private String name1201110RollOverAgYe;// BY

	@Column(name = "s_rpd4341_percentage", length = 50)
	private String sRPD4341Percentage;// BZ

	@Column(name = "s_rpd4341_fl", length = 50)
	private String sRPD4341FL;// CA

	@Column(name = "s_rpd4341_quads_per_day", length = 50)
	private String sRPD4341QuadsPerDay;// CB

	@Column(name = "s_rpd4341_days_bw_treatment", length = 50)
	private String sRPD4341DaysBwTreatment;// CC

	@Column(name = "perio_maintenance_d4910_percentage", length = 50)
	private String perioMaintenanceD4910Percentage;// CD

	@Column(name = "perio_maintenance_d4910_fl", length = 50)
	private String perioMaintenanceD4910FL;// CE

	@Column(name = "perio_maintenance_d4910_altw_prophy_d0110", length = 50)
	private String perioMaintenanceD4910AltWProphyD0110;// CF

	@Column(name = "fmdd4355_percentage", length = 50)
	private String FMDD4355Percentage;// CG

	@Column(name = "fmdd4355_fl", length = 50)
	private String fMDD4355FL;// CH

	@Column(name = "gingivitis_d4346_percentage", length = 50)
	private String gingivitisD4346Percentage;// CI

	@Column(name = "gingivitis_d4346_fl", length = 50)
	private String gingivitisD4346FL;// CJ

	@Column(name = "nitrous_d9230_percentage", length = 50)
	private String NitrousD9230Percentage;// CK

	@Column(name = "iv_sedation_d9243_percentage", length = 50)
	private String iVSedationD9243Percentage;// CL

	 @Column(name = "iv_sededation_d9248_percentage", length = 50)
	 private String iVSedationD9248Percentage;// CM

	@Column(name = "iv_sededation_d9245_percentage", length = 50)
	private String iVSedationD9245Percentage;// CM

	@Column(name = "extractions_minor_percentage", length = 50)
	private String extractionsMinorPercentage;// CN

	@Column(name = "extractions_major_percentage", length = 50)
	private String extractionsMajorPercentage;// CO

	@Column(name = "crown_length_d4249_percentage", length = 50)
	private String crownLengthD4249Percentage;// CP

	@Column(name = "crown_length_d4249_fl", length = 50)
	private String crownLengthD4249FL;// CQ

	@Column(name = "alveo_d7311_covered_with_ext", length = 50)
	private String alveoD7311CoveredWithEXT;// CR

	@Column(name = "alveo_d7311_fl", length = 50)
	private String alveoD7311FL;// CS

	@Column(name = "alveoD7310Covered_with_ext", length = 50)
	private String alveoD7310CoveredWithEXT;// CT

	@Column(name = "alveo_d7310fl", length = 50)
	private String alveoD7310FL;// CU

	@Column(name = "complete_dentures_d5110_d5120_fl", length = 50)
	private String completeDenturesD5110D5120FL;// CV

	@Column(name = "immediate_dentures_d5130_d5140_fl", length = 50)
	private String immediateDenturesD5130D5140FL;// CW

	@Column(name = "partial_dentures_d5213_d5214_fl", length = 50)
	private String partialDenturesD5213D5214FL;// CX

	@Column(name = "interim_partial_dentures_d5214_fl", length = 50)
	private String interimPartialDenturesD5214FL;// CY

	@Column(name = "bone_grafts_d7953_covered_with_ext", length = 50)
	private String boneGraftsD7953CoveredWithEXT;// CZ

	@Column(name = "bone_grafts_d7953_fl", length = 50)
	private String boneGraftsD7953FL;// DA

	@Column(name = "implant_coverage_d6010_percentage", length = 50)
	private String implantCoverageD6010Percentage;// DB

	@Column(name = "implant_coverage_d6057_percentage", length = 50)
	private String implantCoverageD6057Percentage;// DC

	@Column(name = "implant_coverage_d6190_percentage", length = 50)
	private String implantCoverageD6190Percentage;// DD

	@Column(name = "implant_supported_porc_ceramic_d606_percentage", length = 50)
	private String implantSupportedPorcCeramicD6065Percentage;// DE

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "implants_fr_d6010", length = 50)
	private String implantsFrD6010;// implants5

	@Column(name = "implants_fr_d6057", length = 50)
	private String implantsFrD6057;// implants6

	@Column(name = "implants_fr_d6065", length = 50)
	private String implantsFrD6065;// implants7

	@Column(name = "implants_fr_d6190", length = 50)
	private String implantsFrD6190;// implants8

	// END

	@Column(name = "post_composites_d2391_percentage", length = 50)
	private String postCompositesD2391Percentage;// DF

	@Column(name = "post_composites_d2391_fl", length = 50)
	private String postCompositesD2391FL;// DG

	@Column(name = "posterior_composites_d2391_downgrade", length = 50)
	private String posteriorCompositesD2391Downgrade;// DH

	@Column(name = "crowns_d2750_d2740_percentage", length = 50)
	private String crownsD2750D2740Percentage;// DI

	@Column(name = "crowns_d2750_d2740_fl", length = 50)
	private String crownsD2750D2740FL;// DJ

	// NEW NOT IN GOOGLE SHEET

	@Column(name = "crown_grade_code", length = 50)
	private String crowngradeCode;// posterior17
	// END

	@Column(name = "crowns_d2750_d2740_downgrade", length = 50)
	private String crownsD2750D2740Downgrade;// DK

	@Column(name = "night_guards_d9940_percentage", length = 50)
	private String nightGuardsD9940Percentage;// DL THIS IS NOW D9944

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "night_guards_d9944_fr", length = 50)
	private String nightGuardsD9944Fr;// posterior19

	@Column(name = "night_guards_d9945_fr", length = 50)
	private String nightGuardsD9945Fr;// posterior20

	@Column(name = "night_guards_d9945_percentage", length = 50)
	private String nightGuardsD9945Percentage;// posterior18

	// END
	@Column(name = "d9310_percentage", length = 50)
	private String d9310Percentage;// DM

	@Column(name = "d9310_fl", length = 50)
	private String d9310FL;// DN

	@Column(name = "buildups_d2950_covered", length = 50)
	private String buildUpsD2950Covered;// DO

	@Column(name = "buildups_d2950_fl", length = 50)
	private String buildUpsD2950FL;// DP

	@Column(name = "buildups_d2950_same_day_crown", length = 50)
	private String buildUpsD2950SameDayCrown;// DQ

	@Column(name = "orthoPercentage", length = 50)
	private String orthoPercentage;// DR

	@Column(name = "ortho_max", length = 50)
	private String orthoMax;// DS

	@Column(name = "ortho_age_limit", length = 50)
	private String orthoAgeLimit;// DT

	@Column(name = "ortho_subject_deductible", length = 50)
	private String orthoSubjectDeductible;// DU

	// NEW NOT IN GOOGLE SHEET

	@Column(name = "ortho_remaining", length = 50)
	private String orthoRemaining;// ortho5

	@Column(name = "ortho_waiting_period", length = 50)
	private String orthoWaitingPeriod;// waitingPeriod3

	// END

	@Column(name = "fillings_bundling", length = 50)
	private String FillingsBundling;// DV

	@Column(name = "comments", columnDefinition = "text")
	private String comments;// DW

	@Column(name = "general_benefits_verified_by", length = 255)
	private String generalBenefitsVerifiedBy;// DW

	@Column(name = "general_date_iv_wasdone", length = 15)
	private String generalDateIVwasDone;// DY
	// private String patientId;//DZ

	@Column(name = "cra_required", length = 50)
	private String craRequired;// KY

	@Column(name = "claim_filling_limit", length = 50)
	private String claimFillingLimit;// KZ

	@Column(name = "unique_id", length = 80)
	private String uniqueID;// LA

	@Column(name = "d0120", length = 50)
	private String d0120;// LB

	@Column(name = "d2391", length = 50)
	private String d2391;// LC

	// NEW NOT IN GOOGLE SHEET
	@Column(name = "fillings_in_year", length = 20)
	private String fillingsInYear;// fill1

	@Column(name = "extractions_in_year", length = 20)
	private String extractionsInYear;// extr1

	@Column(name = "crowns_in_year", length = 20)
	private String crownsInYear;// crn1
	
	@Column(name = "waiting_period4", length = 20)
	private String waitingPeriod4;// 
	
	@Column(name = "share_fr", length = 20)
	private String shareFr;// 

	@Column(name = "pedo1", length = 20)
	private String pedo1;// pedo1

	@Column(name = "pedo2", length = 20)
	private String pedo2;// pedo2

	@Column(name = "pano1", length = 20)
	private String pano1;// pano1

	@Column(name = "pano2", length = 20)
	private String pano2;// pano2

	@Column(name = "d4381", length = 20)
	private String d4381;// d4381 

	//END
	
	@Column(name = "ckd0120", length = 3)
	private String ckD0120;//ckD0120

	@Column(name = "ckd0140", length = 3)
	private String ckD0140;//ckD0140
	
	@Column(name = "ckd0145", length = 3)
	private String ckD0145;//ckD0145
	
	@Column(name = "ckd0150", length = 3)
	private String ckD0150;//ckD0150
	
	@Column(name = "ckd0160", length = 3)
	private String ckD0160;//ckD0160
	
	@Column(name = "ckd210", length = 3)
	private String ckD210;//ckD210
	
	@Column(name = "ckd220", length = 3)
	private String ckD220;//ckD220
	
	@Column(name = "ckd230", length = 3)
	private String ckD230;//ckD230
	
	@Column(name = "ckd330", length = 3)
	private String ckD330;//ckD330
	
	@Column(name = "ckd274", length = 3)
	private String ckD274;//ckD274

	@Column(name = "d0160_freq", length = 50)
	private String d0160Freq;//d0160Freq

	@Column(name = "d2391_freq", length = 50)
	private String d2391Freq;//d2391Freq
	
	@Column(name = "d0330_freq", length = 50)
	private String d0330Freq;//d0330Freq

	@Column(name = "d4381_freq", length = 50)
	private String d4381Freq;//d4381Freq

	
	@Column(name = "d3330", length = 20)
	private String d3330;//d3330
	
	@Column(name = "d3330_freq", length = 50)
	private String d3330Freq;// d3330Freq 

	@Column(name = "freq_d2934", length = 50)
	private String freqD2934;//freq_d2934
	
	@Column(name = "history_count", length = 5)
	private String historyCount;//history Count

	//OS FORM add in Patient Detail 2 table..
	//
	
	public String getD0120() {
		return d0120;
	}

	public void setD0120(String d0120) {
		this.d0120 = d0120;
	}

	public String getD2391() {
		return d2391;
	}

	public void setD2391(String d2391) {
		this.d2391 = d2391;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getPolicyHolder() {
		return policyHolder;
	}

	public void setPolicyHolder(String policyHolder) {
		this.policyHolder = policyHolder;
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
		this.contTxRecallNP = contTxRecallNP == null ? "" : contTxRecallNP;
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
		this.planPreDMandatory = planPreDMandatory == null ? "" : planPreDMandatory;
	}

	public String getPlanNonDuplicateClause() {
		return planNonDuplicateClause;
	}

	public void setPlanNonDuplicateClause(String planNonDuplicateClause) {
		this.planNonDuplicateClause = planNonDuplicateClause == null ? "" : planNonDuplicateClause;
	}

	public String getPlanFullTimeStudentStatus() {
		return planFullTimeStudentStatus;
	}

	public void setPlanFullTimeStudentStatus(String planFullTimeStudentStatus) {
		this.planFullTimeStudentStatus = planFullTimeStudentStatus == null ? "" : planFullTimeStudentStatus;
	}

	public String getPlanAssignmentofBenefits() {
		return planAssignmentofBenefits;
	}

	public void setPlanAssignmentofBenefits(String planAssignmentofBenefits) {
		this.planAssignmentofBenefits = planAssignmentofBenefits == null ? "" : planAssignmentofBenefits;
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
		this.basicSubjectDeductible = basicSubjectDeductible == null ? "" : basicSubjectDeductible;
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
		this.majorSubjectDeductible = majorSubjectDeductible == null ? "" : majorSubjectDeductible;
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
		this.endoSubjectDeductible = endoSubjectDeductible == null ? "" : endoSubjectDeductible;
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
		this.perioSurgerySubjectDeductible = perioSurgerySubjectDeductible == null ? "" : perioSurgerySubjectDeductible;
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
		this.missingToothClause = missingToothClause == null ? "" : missingToothClause;
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
		this.crownsD2750D2740PaysPrepSeatDate = crownsD2750D2740PaysPrepSeatDate == null ? ""
				: crownsD2750D2740PaysPrepSeatDate;
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

		this.basicWaitingPeriod = basicWaitingPeriod == null ? "" : basicWaitingPeriod;
	}

	public String getMajorWaitingPeriod() {
		return majorWaitingPeriod;
	}

	public void setMajorWaitingPeriod(String majorWaitingPeriod) {
		this.majorWaitingPeriod = majorWaitingPeriod == null ? "" : majorWaitingPeriod;
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
		this.sRPD4341QuadsPerDay = sRPD4341QuadsPerDay == null ? "" : sRPD4341QuadsPerDay;
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

	public void setFMDD4355Percentage(String FMDD4355Percentage) {
		this.FMDD4355Percentage = FMDD4355Percentage;
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
	
	public void setiVSedationD9245Percentage(String iVSedationD9245Percentage) {
		this.iVSedationD9245Percentage = iVSedationD9245Percentage;
	}

	public String getiVSedationD9245Percentage() {
		return iVSedationD9245Percentage;
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
		this.orthoSubjectDeductible = orthoSubjectDeductible == null ? "" : orthoSubjectDeductible;
	}

	public String getFillingsBundling() {
		return FillingsBundling;
	}

	public void setFillingsBundling(String fillingsBundling) {
		this.FillingsBundling = (fillingsBundling == null) ? "" : fillingsBundling;
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

	public String getCraRequired() {
		return craRequired;
	}

	public void setCraRequired(String craRequired) {
		this.craRequired = craRequired;
	}

	public String getClaimFillingLimit() {
		return claimFillingLimit;
	}

	public void setClaimFillingLimit(String claimFillingLimit) {
		this.claimFillingLimit = claimFillingLimit;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
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

	public String getDen5225Per() {
		return den5225Per;
	}

	public void setDen5225Per(String den5225Per) {
		this.den5225Per = den5225Per;
	}

	public String getDenf5225FR() {
		return denf5225FR;
	}

	public void setDenf5225FR(String denf5225FR) {
		this.denf5225FR = denf5225FR;
	}

	public String getDen5226Per() {
		return den5226Per;
	}

	public void setDen5226Per(String den5226Per) {
		this.den5226Per = den5226Per;
	}

	public String getDenf5226Fr() {
		return denf5226Fr;
	}

	public void setDenf5226Fr(String denf5226Fr) {
		this.denf5226Fr = denf5226Fr;
	}

	public String getBridges1() {
		return bridges1;
	}

	public void setBridges1(String bridges1) {
		this.bridges1 = bridges1;
	}

	public String getBridges2() {
		return bridges2;
	}

	public void setBridges2(String bridges2) {
		this.bridges2 = bridges2;
	}

	public String getWillDowngradeApplicable() {
		return willDowngradeApplicable;
	}

	public void setWillDowngradeApplicable(String willDowngradeApplicable) {
		this.willDowngradeApplicable = willDowngradeApplicable;
	}

	public String getImplantsFrD6010() {
		return implantsFrD6010;
	}

	public void setImplantsFrD6010(String implantsFrD6010) {
		this.implantsFrD6010 = implantsFrD6010;
	}

	public String getImplantsFrD6057() {
		return implantsFrD6057;
	}

	public void setImplantsFrD6057(String implantsFrD6057) {
		this.implantsFrD6057 = implantsFrD6057;
	}

	public String getImplantsFrD6065() {
		return implantsFrD6065;
	}

	public void setImplantsFrD6065(String implantsFrD6065) {
		this.implantsFrD6065 = implantsFrD6065;
	}

	public String getImplantsFrD6190() {
		return implantsFrD6190;
	}

	public void setImplantsFrD6190(String implantsFrD6190) {
		this.implantsFrD6190 = implantsFrD6190;
	}

	public String getOrthoRemaining() {
		return orthoRemaining;
	}

	public void setOrthoRemaining(String orthoRemaining) {
		this.orthoRemaining = orthoRemaining;
	}

	public String getOrthoWaitingPeriod() {
		return orthoWaitingPeriod;
	}

	public void setOrthoWaitingPeriod(String orthoWaitingPeriod) {
		this.orthoWaitingPeriod = orthoWaitingPeriod;
	}

	public String getPreventiveSubDed() {
		return preventiveSubDed;
	}

	public void setPreventiveSubDed(String preventiveSubDed) {
		this.preventiveSubDed = preventiveSubDed;
	}

	public String getDiagnosticSubDed() {
		return diagnosticSubDed;
	}

	public void setDiagnosticSubDed(String diagnosticSubDed) {
		this.diagnosticSubDed = diagnosticSubDed;
	}

	public String getpAXRaysSubDed() {
		return pAXRaysSubDed;
	}

	public void setpAXRaysSubDed(String pAXRaysSubDed) {
		this.pAXRaysSubDed = pAXRaysSubDed;
	}

	public String getCrowngradeCode() {
		return crowngradeCode;
	}

	public void setCrowngradeCode(String crowngradeCode) {
		this.crowngradeCode = crowngradeCode;
	}

	public String getNightGuardsD9945Percentage() {
		return nightGuardsD9945Percentage;
	}

	public void setNightGuardsD9945Percentage(String nightGuardsD9945Percentage) {
		this.nightGuardsD9945Percentage = nightGuardsD9945Percentage;
	}

	public String getFmxPer() {
		return fmxPer;
	}

	public void setFmxPer(String fmxPer) {
		this.fmxPer = fmxPer;
	}

	public String getNightGuardsD9944Fr() {
		return nightGuardsD9944Fr;
	}

	public void setNightGuardsD9944Fr(String nightGuardsD9944Fr) {
		this.nightGuardsD9944Fr = nightGuardsD9944Fr;
	}

	public String getNightGuardsD9945Fr() {
		return nightGuardsD9945Fr;
	}

	public void setNightGuardsD9945Fr(String nightGuardsD9945Fr) {
		this.nightGuardsD9945Fr = nightGuardsD9945Fr;
	}

	public String getFillingsInYear() {
		return fillingsInYear;
	}

	public void setFillingsInYear(String fillingsInYear) {
		this.fillingsInYear = fillingsInYear;
	}

	public String getExtractionsInYear() {
		return extractionsInYear;
	}

	public void setExtractionsInYear(String extractionsInYear) {
		this.extractionsInYear = extractionsInYear;
	}

	public String getCrownsInYear() {
		return crownsInYear;
	}

	public void setCrownsInYear(String crownsInYear) {
		this.crownsInYear = crownsInYear;
	}

	public String getWaitingPeriod4() {
		return waitingPeriod4;
	}

	public void setWaitingPeriod4(String waitingPeriod4) {
		this.waitingPeriod4 = waitingPeriod4;
	}

	public String getShareFr() {
		if (shareFr==null) shareFr="";
		return shareFr;
	}

	public void setShareFr(String shareFr) {
		if (shareFr==null) shareFr="";
		this.shareFr = shareFr;
	}

	public String getPedo1() {
		if (pedo1==null) pedo1="";
		return pedo1;
	}

	public void setPedo1(String pedo1) {
		if (pedo1==null) pedo1="";
		this.pedo1 = pedo1;
	}

	public String getPedo2() {
		if (pedo2==null) pedo2="";
		return pedo2;
	}

	public void setPedo2(String pedo2) {
		if (pedo2==null) pedo2="";
		this.pedo2 = pedo2;
	}

	public String getPano1() {
		return pano1;
	}

	public void setPano1(String pano1) {
		this.pano1 = pano1;
	}

	public String getPano2() {
		return pano2;
	}

	public void setPano2(String pano2) {
		this.pano2 = pano2;
	}

	public String getD4381() {
		return d4381;
	}

	public void setD4381(String d4381) {
		this.d4381 = d4381;
	}

	public String getCkD0120() {
		return ckD0120;
	}

	public void setCkD0120(String ckD0120) {
		this.ckD0120 = ckD0120;
	}

	public String getCkD0140() {
		return ckD0140;
	}

	public void setCkD0140(String ckD0140) {
		this.ckD0140 = ckD0140;
	}

	public String getCkD0145() {
		return ckD0145;
	}

	public void setCkD0145(String ckD0145) {
		this.ckD0145 = ckD0145;
	}

	public String getCkD0150() {
		return ckD0150;
	}

	public void setCkD0150(String ckD0150) {
		this.ckD0150 = ckD0150;
	}

	public String getCkD0160() {
		return ckD0160;
	}

	public void setCkD0160(String ckD0160) {
		this.ckD0160 = ckD0160;
	}

	public String getCkD210() {
		return ckD210;
	}

	public void setCkD210(String ckD210) {
		this.ckD210 = ckD210;
	}

	public String getCkD220() {
		return ckD220;
	}

	public void setCkD220(String ckD220) {
		this.ckD220 = ckD220;
	}

	public String getCkD230() {
		return ckD230;
	}

	public void setCkD230(String ckD230) {
		this.ckD230 = ckD230;
	}

	public String getCkD330() {
		return ckD330;
	}

	public void setCkD330(String ckD330) {
		this.ckD330 = ckD330;
	}

	public String getCkD274() {
		return ckD274;
	}

	public void setCkD274(String ckD274) {
		this.ckD274 = ckD274;
	}

	public IVFormType getiVFormType() {
		return iVFormType;
	}

	public void setiVFormType(IVFormType iVFormType) {
		this.iVFormType = iVFormType;
	}

	public String getD0160Freq() {
		return d0160Freq;
	}

	public void setD0160Freq(String d0160Freq) {
		this.d0160Freq = d0160Freq;
	}

	public String getD2391Freq() {
		return d2391Freq;
	}

	public void setD2391Freq(String d2391Freq) {
		this.d2391Freq = d2391Freq;
	}

	public String getD0330Freq() {
		return d0330Freq;
	}

	public void setD0330Freq(String d0330Freq) {
		this.d0330Freq = d0330Freq;
	}

	public String getD4381Freq() {
		return d4381Freq;
	}

	public void setD4381Freq(String d4381Freq) {
		this.d4381Freq = d4381Freq;
	}

	public String getD3330() {
		return d3330;
	}

	public void setD3330(String d3330) {
		this.d3330 = d3330;
	}

	public String getD3330Freq() {
		return d3330Freq;
	}

	public void setD3330Freq(String d3330Freq) {
		this.d3330Freq = d3330Freq;
	}

	public PatientDetail2 getPatientDetails2() {
		return patientDetails2;
	}

	public void setPatientDetails2(PatientDetail2 patientDetails2) {
		this.patientDetails2 = patientDetails2;
	}

	public String getFreqD2934() {
		return freqD2934;
	}

	public void setFreqD2934(String freqD2934) {
		this.freqD2934 = freqD2934;
	}

	public String getHistoryCount() {
		return historyCount;
	}

	public void setHistoryCount(String historyCount) {
		this.historyCount = historyCount;
	}

	

	
	
}
