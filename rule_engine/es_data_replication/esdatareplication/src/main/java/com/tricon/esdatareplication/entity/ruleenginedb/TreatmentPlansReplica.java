package com.tricon.esdatareplication.entity.ruleenginedb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonsTreatmentPlans;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "es_data_replica_treatment_plans")
@EqualsAndHashCode(callSuper = true)
public class TreatmentPlansReplica extends CommonsTreatmentPlans  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9103206016324333447L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public TreatmentPlansReplica() {
		super();
	}

	
}
