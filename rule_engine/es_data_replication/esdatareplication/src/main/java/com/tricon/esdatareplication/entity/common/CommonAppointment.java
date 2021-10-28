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
public class CommonAppointment extends CommonCloudColumn {
	/**
	 * Checked
	 */
	private static final long serialVersionUID = 1111409863044643669L;

	@Column(name = "appointment_id", length = 50, nullable = false, unique= true)
	Integer appointmentId;

	@Column(name = "description", length = 150, nullable = true)
	String description;

	@Column(name = "allday_event", length = 50, nullable = true)
	boolean alldayEvent;

	@Column(name = "start_time", length = 50, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date startTime;

	@Column(name = "end_time", length = 50, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date endTime;

	@Column(name = "patient_id", length = 50, nullable = true)
	String patientId;

	@Column(name = "recall_id", length = 50, nullable = true)
	Integer recallId;

	@Column(name = "location_id", length = 50, nullable = true)	
	Integer locationId;

	@Column(name = "location_free_form", length = 50, nullable = true)	
	String locationFreeForm;
	
	@Column(name = "classification", length = 50, nullable = true)	
	Integer classification;
	
	@Column(name = "appointment_type_id", length = 50, nullable = true)	
	Integer appointmentTypeId;
	
	@Column(name = "prefix", length = 50, nullable = true)	
	String prefix;
	
	@Column(name = "dollars_scheduled", length = 50, nullable = true)	
	Double dollarsScheduled;
	
	@Column(name = "date_appointed", length = 50, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date dateAppointed;
	
	@Column(name = "date_confirmed", length = 50, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date dateConfirmed;
	
	@Column(name = "appointment_notes",columnDefinition="text", nullable = true)
	String appointmentNotes;
	
	@Column(name = "deletion_note", nullable = true,columnDefinition="text")
	String deletionNote;
	
	@Column(name = "sooner_if_possible", length = 50, nullable = true)	
	String soonerIfPossible;
	
	@Column(name = "scheduled_by", length = 50, nullable = true)
	String scheduledBy;
	
	@Column(name = "modified_by", length = 50, nullable = true)	
	String modifiedBy;
	
	@Column(name = "appointment_name", length = 50, nullable = true)	
	String appointmentName;
	
	@Column(name = "arrival_status", length = 50, nullable = true)	
	Integer arrivalStatus;
	
	@Column(name = "arrival_time", length = 50, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date arrivalTime;
	
	@Column(name = "inchair_time", length = 50, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date inchairTime;
	
	@Column(name = "walkout_time", length = 50, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date walkoutTime;
	
	@Column(name = "confirmation_status", length = 50, nullable = true)	
	Integer confirmationStatus;
	
	@Column(name = "confirmation_note", nullable = true,columnDefinition="text")
	String confirmationNote;
	
	@Column(name = "auto_confirm_sent", length = 50, nullable = true)
	Integer autoConfirmSent;
	
	@Column(name = "recurrence_id", length = 50, nullable = true)
	Integer	 recurrenceId;
	
	@Column(name = "private", length = 50, nullable = true)	
	boolean privateR;
	
	@Column(name = "priority", length = 50, nullable = true)	
	Integer priority;
	
	@Column(name = "appointment_data", length = 50, nullable = true)
	String appointmentData;

	

}
