package com.tricon.rcm.db.entity;

import java.io.Serializable;
//import java.sql.Date;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_claims", uniqueConstraints = { @UniqueConstraint(columnNames = { "claim_id", "office_id" }) })
public class RcmClaims extends BaseAuditEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5125694073291000159L;

	@GeneratedValue(generator = "uuid2") // only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "claim_uuid", nullable = false, length = 45)
	private String claimUuid;

	@Column(name = "claim_id", nullable = false)
	private String claimId;// c1

	//@Column(name = "claim_status")
	//private String claimStatus;// For Rule Engine..

	@Column(name = "patient_id", length = 255)
	private String patientId;// c2

	@Column(name = "patient_name", length = 255)
	private String patientName;

	@Column(name = "dos", length = 8)
	private Date dos;// c4

	// @Column(name = "claim_age_days", length = 6)
	// private int claimAgeDays;

	@Column(name = "submitted_total", precision = 5, scale = 2)
	private float submittedTotal;// C5

	@Column(name = "prime_sec_submitted_total", precision = 5, scale = 2)
	private float primeSecSubmittedTotal;// C6

	@Column(name = "prim_total_paid", precision = 5, scale = 2)
	private float primTotalPaid;// C7

	@Column(name = "prim_date_sent")
	private Date primDateSent;// C8

	@Column(name = "sec_submitted_total", precision = 5, scale = 2)
	private float secSubmittedTotal;// C9

	@Column(name = "sec_date_sent")
	private Date secDateSent;// C10

	@Column(name = "provider_id")
	private String providerId;// C11 //ES CODE

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prim_insurance_company_id", referencedColumnName = "id")
	private RcmInsurance primInsuranceCompanyId;// C12

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sec_insurance_company_id", referencedColumnName = "id")
	private RcmInsurance secInsuranceCompanyId;// C13

	@Column(name = "prim_status")
	private String primStatus;// C14

	@Column(name = "sec_status")
	private String secStatus;// C15

	//@Column(name = "claim_type", length = 255)
	//private String claimType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_team_id", referencedColumnName = "id")
	private RcmTeam currentTeamId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "first_worked_team_id", referencedColumnName = "id")
	private RcmTeam firstWorkedTeamId;

	@Column(name = "patient_birth_date")
	private Date patientBirthDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id", referencedColumnName = "uuid")
	private RcmOffice office;

	///@Column(name = "rcm_team_status")
	//private int rcmTeamStatus;// 0 Mean with SYSTEM 1 =TL 2= Means with Associate//Rethink logic//
	// Look in status Journey table

	@Column(name = "rcm_source")
	private String rcmSource;// 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_status_type_id", referencedColumnName = "id")
	private RcmClaimStatusType claimStatusType;
	
	
	@Column(name = "regenerated",columnDefinition = "BIT default 0")
	private boolean regenerated;//

	
	@Column(name = "pending",columnDefinition = "BIT default 0")
	private boolean pending;//
	
	@Column(name = "review",columnDefinition = "BIT default 0")
	private boolean review;//
	
	@Column(name = "recreated_section",columnDefinition = "integer default 0")
	private int recreatedSection;//
	
	@Column(name = "auto_rule_run",columnDefinition = "BIT default 0")
	private boolean autoRuleRun;//
	
	@Column(name = "pulled_claims_service_data_from_es",columnDefinition = "BIT default 0")
	private boolean pulledClaimsServiceDataFromEs;//,mean we have fetched data from and Service Code sheet. 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rcm_insurance_type", referencedColumnName = "id")
	private RcmInsuranceType rcmInsuranceType;
	
	@Column(name = "timely_fil_lmt_dt")
	private String  timelyFilingLimitData;
	
	//Same for Primary and secondary 
	@Column(name = "sec_member_id")
	private String  secMemberId;
	
	@Column(name = "group_number")
	private String  groupNumber;
	
	@Column(name = "prime_policy_holder")
	private String  primePolicyHolder;
	
	@Column(name = "prime_policy_holder_dob")
	private Date  primePolicyHolderDob;
	
	@Column(name = "sec_policy_holder_dob")
	private Date  secPolicyHolderDob;
	
	@Column(name = "sec_policy_holder")
	private String  secPolicyHolder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_work_team_id", referencedColumnName = "id",nullable=true)
	private RcmTeam lastWorkTeamId;
	
	
	//From ES- Start
	@Column(name = "date_last_updated_es")
	private String dateLastUpdatedES;// This is DOS but not considered-- we have use Current Date as DOS in TP but in Claim we will consider this .
	
	@Column(name = "status_es")
	private String statusES;
	
	@Column(name = "est_secondary_es")
	private String estSecondaryES;
	
	@Column(name = "description_es")
	private String descriptionES;
	
	@Column(name = "treating_provider")
	private String treatingProvider;//From GSheet
	//From ES - END
	
	@Column(name = "provider_on_claim")
	private String providerOnClaim;//Provider Name from ES using ES code
	
	@Column(name = "provider_on_claim_from_sheet")
	private String providerOnClaimFromSheet;//Provider Name from ES using TreatingProvider
	
	@Column(name = "treating_provider_from_claim")
	private String treatingProviderFromClaim;//using ProviderId(ES Code) and Sheet
	
	@Column(name = "treating_provider_from_claim_on_sheet")
	private String treatingProviderFromClaimOnSheet;//using ProviderId(ES Code) and Sheet
	
	@Column(name = "ivf_id")
	private String ivfId;
	
	@Column(name = "ivf_id_system")
	private String ivfIdSystem;
	
	@Column(name = "iv_dos")
	private String ivDos;
	
	@Column(name = "tp_id")
	private String tpId;
	
	@Column(name = "tp_dos")
	private String tpDos;
	
	@Column(name = "claim_type")//Ortho/General/etc from provider Sheet.
	private String claimType;
	
	@Column(name = "provider_code")
	private String providerCode;
	
	@Column(name = "primary_eob")
	private String primaryEob;
	
	@Column(name = "ssn")
	private String ssn;
	
	@Column(name = "assignment_of_benefits")
	private String assignmentOfBenefits;
	
	
	//0 --> it self primary 
	//1--> primary not submitted for secondary 
	//2-> primary submitted for secondary 
	@Column(name = "primary_status",columnDefinition = "integer default 0")
	private int primaryStaus;//
	
	@Column(name = "attachment_count",columnDefinition = "integer default 0")
	private int attachmentCount;
	
	//0- Valid 1 = Archive
	@Column(name = "current_state",columnDefinition = "integer default 0")
	private int currentState;
	
	@Column(name = "preferred_mode_of_submission")
	private String preferredModeOfSubmission;
	
	@Column(name = "rule_engine_run_remark")
	private String ruleEngineRunRemark;
	
	@Column(name = "patient_contact_no")
	private String patientContactNo;
	
	@Column(name = "insurance_contact_no")
	private String insuranceContactNo;
	//@Column(name = "insurance_code")
	//private String insuranceCode;
	
	@Column(name = "current_status",columnDefinition = "integer default 0")
	private int currentStatus;//
	
	@Column(name = "next_action",columnDefinition = "integer default 0")
	private int nextAction;//
	
	@Column(name = "btp")
	private Float btp;//
	
	@Column(name = "adjustment")
	private Float adjustment;//
	
	@Column(name = "payment_received")
	private Float paymentReceived ;//
	
	@Column(name = "first_posting_date", nullable = true)
	private Date firstPostingDate;
	
	@Column(name = "paid_amount")
	private Float paidAmount;//
	
	@Column(name = "rebilled_status",columnDefinition = "BIT default 0")
	private boolean rebilledStatus;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "first_rebilled_date")
	private Date firstRebilledDate;
	
	@Column(name = "balance_from_es_before_posting")
	private Float balanceFromEsBeforePosting;

	@Column(name = "balance_from_es_after_posting")
	private Float balanceFromEsAfterPosting;
	
	//for PatientPaymentSection
	@Column(name = "amount_collected_claims")
	private Float amountCollectedClaims;
	
	@Column(name = "reconciliation_pass")
	private Boolean reconciliationPass;
	
	@Column(name = "status_es_updated")
	private String statusESUpdated;
	
	@Column(name = "next_follow_up_date")
	private Date nextFollowUpDate;
	
	@Column(name = "credit_adjustment_amount")
	private Float creditAdjustmentAmount;

	@Column(name = "debit_adjustment_amount")
	private Float debitAdjustmentAmount;
	
	@Column(name = "amount_received_in_bank")
	private Float amountReceivedInBank;
	
	@Column(name = "is_primary",columnDefinition = "BIT default 0")
	private boolean primary;
	
	//Same in rcm claim Assignment
	@Column(name = "is_force_unassigned",columnDefinition = "BIT default 0")
	private boolean forceUnassigned;
	
	//This claim will not be assigned to any one from Automated process/ claim Assignment screen
	@Column(name = "force_unassigned_comment")
	private String forceUnassignedComment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unassigned_team_id", referencedColumnName = "id")
	private RcmTeam unassignedTeamId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unassigned_by",referencedColumnName="uuid")
	private RcmUser unAssignedBy;
	
	@Column(name = "due_balance_res_party")
	private String dueBalanceResponsibleParty;
	
	@Column(name = "res_party")
	private String responsibleParty;
}
