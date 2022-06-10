package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.tricon.esdatareplication.entity.common.CommonsTreatmentPlans;
import com.tricon.esdatareplication.util.Constants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = Constants.TABLE_TREATMENT_PLANS, uniqueConstraints = {
		@UniqueConstraint(columnNames = {"treatment_plan_id","patient_id","office_id"})})
@EqualsAndHashCode(callSuper = true)
public class TreatmentPlans extends CommonsTreatmentPlans  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5721980429782682380L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	public TreatmentPlans() {
		super();
	}

}
