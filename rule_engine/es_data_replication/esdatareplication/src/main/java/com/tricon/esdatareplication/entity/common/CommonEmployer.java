package com.tricon.esdatareplication.entity.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonEmployer extends CommonCloudColumn {

	/**
	 * Checked
	 */
	private static final long serialVersionUID = -5311319078674961794L;

	@Column(name = "employer_id", length = 50, nullable= false)
	Integer employerId;
	
	@Column(name = "name", length = 40)
	String name;
	
	@Column(name = "address_1", length = 50)
	String address1;

	@Column(name = "address_2", length = 40)
	String address2;
	
	@Column(name = "city", length = 50)
	String city;
	
	@Column(name = "state", length = 50)
	String state;

	@Column(name = "zipcode", length = 50)
	String zipCode;
	
	@Column(name = "contact", length = 50)
	String contact;
	
	@Column(name = "phone1", length = 20)
	String phone1;
	
	@Column(name = "phone1ext", length = 50)
	String phone1ext;
	
	@Column(name = "phone2", length = 50)
	String phone2;
	
	@Column(name = "phone2ext", length = 50)
	String phone2ext;
	
	
	@Column(name = "fax", length = 50)
	String fax;
	
	@Column(name = "signature_on_file_yn", length = 50)
	String signatureOnFileYn;
	
	@Column(name = "group_number", length = 50)
	String groupNumber;
	
	@Column(name = "group_name", length = 50)
	String groupName;
	
	@Column(name = "insurance_company_id", length = 50)
	Integer insuranceCompanyId;
	
	@Column(name = "form_id", length = 50)
	Integer formId;
	
	@Column(name = "fee_schedule", length = 50)
	Integer feeSchedule;
	
	@Column(name = "maximum_coverage", length = 50)
	Double maximumCoverage;
	
	@Column(name = "lifetime_maximum_yn", length = 50)
	String lifetimeMaximumYn;
	
	@Column(name = "yearly_deductible", length = 50)
	Double yearlyDeductible;
	
	@Column(name = "lifetime_deductible_yn", length = 50)
	String lifetimeDeductibleYn;
	
	@Column(name = "beginning_month", length = 50)
	Integer beginningMonth;
	
	@Column(name = "submit_electronically", length = 50)
	String submitElectronically;
	
	@Column(name = "m_number_submitted", length = 50)
	Integer mNumberSubmitted;
	
	@Column(name = "y_number_submitted", length = 50)
	Integer yNumberSubmitted;
	
	
	@Column(name = "m_amount_submitted", length = 50)
	Double mAmountSubmitted;
	
	@Column(name = "y_amount_submitted", length = 50)
	Double yAmountSubmitted;
	
	@Column(name = "m_number_received", length = 50)
	Integer mNumberReceived;
	
	@Column(name = "y_number_received", length = 50)
	Integer yNumberReceived;
	
	@Column(name = "m_amount_received", length = 50)
	Double mAmountReceived;
	
	@Column(name = "y_amount_received", length = 50)
	Double yAmountReceived;
	
	@Column(name = "notes", columnDefinition = "text")
	String notes;
	
	@Column(name = "carrier_type", length = 50)
	String carrierType;
	
	@Column(name = "receive_paper_claim", length = 50)
	String receivePaperClaim;
	
	@Column(name = "estimate_insurance", length = 50)
	String estimateInsurance;
	
	@Column(name = "provider_id_flag", length = 50)
	String providerIdFlag;
	
	@Column(name = "patient_id_flag", length = 50)
	String patientIdFlag;
	
	@Column(name = "patient_ssn_flag", length = 50)
	String patientSsnFlag;
	
	@Column(name = "trojan_id", length = 50)
	String trojanId;
	
	@Column(name = "secondary_calculation", length = 50)
	String secondaryCalculation;
	
	@Column(name = "secondary_provider_id", length = 50)
	String secondaryProviderId;
	
	@Column(name = "book_id", length = 50)
	Integer bookId;
	
	@Column(name = "status", length = 50)
	String status;
	
	@Column(name = "central_id", length = 50)
	String centralId;
	
	@Column(name = "daily_number_submitted", length = 50)
	Integer dailyNumberSubmitted;
	
	@Column(name = "daily_amount_submitted", length = 50)
	Double dailyAmountSubmitted;
	
	@Column(name = "daily_number_received", length = 50)
	Integer dailyNumberReceived;
	
	@Column(name = "daily_amount_received", length = 50)
	Double dailyAmountReceived;
	
	@Column(name = "bill_standard_fee", length = 50)
	String billStandardFee;
	
	@Column(name = "do_not_track_yn", length = 50)
	String doNotTrackYn;
	
	@Column(name = "show_tax_on_ins_claim_yn", length = 50)
	String showTaxOnInsClaimYn;
	
	@Column(name = "secondary_form_id", length = 50)
	Integer secondaryFormId;
	
	@Column(name = "id_treating_dentist", length=50)
	String idTreatingDentist;
	
	@Column(name = "trojan_mc", length = 50)
	String trojanMc;
	
	@Column(name = "Managed_care", length = 50)
	String managedCare;
	
	@Column(name = "division_section_no", length = 10)
	String divisionSectionNo;
	
	@Column(name = "id_facility_by", length = 60)
	String idFacilityBy;
	
	@Column(name = "adjustment_type", length = 60)
	Integer adjustmentType;

}
