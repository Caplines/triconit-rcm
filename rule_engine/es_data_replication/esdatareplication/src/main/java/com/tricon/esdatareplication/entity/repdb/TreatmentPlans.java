package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonsTreatmentPlanItems;
import com.tricon.esdatareplication.entity.common.CommonsTreatmentPlans;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "treatment_plans")
@EqualsAndHashCode(callSuper = true)
public class TreatmentPlans extends CommonsTreatmentPlans  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5721980429782682380L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public TreatmentPlans() {
		super();
	}

}
