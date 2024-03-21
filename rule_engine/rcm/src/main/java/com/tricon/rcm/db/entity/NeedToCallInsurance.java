//package com.tricon.rcm.db.entity;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//
//import com.tricon.rcm.db.BaseAuditEntity;
//
//import lombok.Data;
//
//@Data
//@Entity
//@Table(name = "rcm_need_call_insurance_section")
//public class NeedToCallInsurance extends BaseAuditEntity implements java.io.Serializable{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 4697275405520482432L;
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id", nullable = false)
//	private int id;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "claim_uuid", referencedColumnName = "claim_uuid")
//	private RcmClaims claim;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "team_id", referencedColumnName = "id")
//	private RcmTeam teamId;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "team_to_call", referencedColumnName = "id")
//	private RcmTeam teamToCall;
//
//	@Column(name = "reason_of_calling")
//	private String reasonOfCalling;
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "date_of_Calling")
//	private Date dateOfCalling;
//
//	@Column(name = "remarks", columnDefinition = "text")
//	private String remarks;
//
//	@Column(name = "final_submit")
//	private boolean finalSubmit;
//}
