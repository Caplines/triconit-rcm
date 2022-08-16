package com.tricon.esdatareplication.entity.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonsTreatmentPlans extends CommonCloudColumn {
	/**
	 * Checked
	 */
	private static final long serialVersionUID = -5366225049780096365L;

	@Column(name = "treatment_plan_id", length = 50, nullable = false)
	Integer treatmentPlanId;

	@Column(name = "patient_id", length = 50, nullable = false)
	String patientId;

	@Column(name = "description", length = 50, nullable = true)
	String description;

	@Column(name = "status", length = 50, nullable = true)
	String status;

	@Column(name = "date_entered", nullable = true)
	@Temporal(TemporalType.DATE)
	Date dateEntered;
	
	@Column(name = "user_id", length = 50, nullable = true)
	String userId;

	@Column(name = "date_last_updated", nullable = true)
	@Temporal(TemporalType.DATE)
	Date dateLastUpdated;
	
	@Column(name = "last_updated_by", nullable = true)
	String  lastUpdatedBy;
	
	@Column(name = "notes", nullable = true,columnDefinition="text")
	String  notes;
	
	
	
	
	
}
