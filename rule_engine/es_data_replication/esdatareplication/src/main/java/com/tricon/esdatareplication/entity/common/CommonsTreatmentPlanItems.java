package com.tricon.esdatareplication.entity.common;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonsTreatmentPlanItems extends CommonCloudColumn {
	
	/**
	 * Cross checked
	 */
	private static final long serialVersionUID = -3678048924346214004L;
	
	@Column(name = "treatment_plan_id", length = 50, nullable = false)
	Integer treatmentPlanId;

	@Column(name = "patient_id", length = 50, nullable = false)
	String patientId;

	@Column(name = "line_number", length = 50, nullable = false)
	Integer lineNumber;
	
	@Column(name = "est_primary", length = 50, nullable = true)
	Double estPrimary;
	
	@Column(name = "est_secondary", length = 50, nullable = true)
	Double estSecondary;

	@Column(name = "approved_by_insurance", length = 50, nullable = true)
	String approvedByInsurance;

	@Column(name = "submit_yn", length = 50, nullable = true)
	String submitYn;

	@Column(name = "claim_id", length = 50, nullable = true)
	Integer claimId;
	
	@Column(name = "sort_order", length = 50, nullable = true)
	Integer sortOrder;
	
	@Column(name = "discount", length = 50, nullable = true)
	Double discount;
	
	@Column(name = "apply_discount", length = 50, nullable = true)
	String applyDiscount;
	


}
