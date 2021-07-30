package com.tricon.esdatareplication.entity.common;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonPlannedServices extends CommonCloudColumn {
	
	/**
	 * Checked
	 */
	private static final long serialVersionUID = 7988370539302522059L;
	
	@Column(name = "appt_group", length = 50, nullable = true)
	Integer apptGroup;
		
	@Column(name = "appt_id", length = 50, nullable = true)
	Integer apptId;
		
	@Column(name = "completion_date", length = 50, nullable = true)
	Date completionDate;
		
	@Column(name = "created_from_upgrade", length = 50, nullable = true)
	String createdFromUpgrade;

	@Column(name = "date_planned", length = 50, nullable = true)
	Date datePlanned;

	@Column(name = "description", length = 50, nullable = true)
	String description;
	 
	@Column(name = "fee", length = 50, nullable = true)
	Double fee;
		
	@Column(name = "lab_code", length = 50, nullable = true)
	String labCode;

	@Column(name = "lab_code2", length = 50, nullable = true)		
	String labCode2;

	@Column(name = "lab_fee", length = 50, nullable = true)		
	Double labFee;

	@Column(name = "lab_fee2", length = 50, nullable = true)
	Double  labFee2;

	@Column(name = "line_number", length = 50, nullable = false)
	Integer lineNumber;
		
	@Column(name = "old_tooth", length = 50, nullable = true)
	String oldTooth;

	@Column(name = "patient_id", length = 50, nullable = false)
	String patientId;

	@Column(name = "pre_fee", length = 50, nullable = true)	
	Double preFee;	

	@Column(name = "procedure_type_codes", length = 50, nullable = true)	
	String procedureTypeCodes;

	@Column(name = "provider_id", length = 50, nullable = true)
	String providerId;

	@Column(name = "sequence", length = 50, nullable = true)
	Integer	sequence;

	@Column(name = "service_code", length = 50, nullable = true)
	String serviceCode;

	@Column(name = "sort_order", length = 50, nullable = true)
	Integer sortOrder;
		
	@Column(name = "standard_fee_id", length = 50, nullable = true)	
	Integer standardFeeId;
		
	@Column(name = "status", length = 50, nullable = false)	
	String status;

	@Column(name = "status_date", nullable = false)	
	Date statusDate;

	@Column(name = "surface", length = 50, nullable = false)
	String surface;

	@Column(name = "tooth", length = 50, nullable = false)
	String tooth;

	@Column(name = "unusual_remarks", length = 150, nullable = false)	
	String unusualRemarks;



}
