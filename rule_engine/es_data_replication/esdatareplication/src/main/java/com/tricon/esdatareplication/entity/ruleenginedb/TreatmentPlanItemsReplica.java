package com.tricon.esdatareplication.entity.ruleenginedb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonsTreatmentPlanItems;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "es_data_replica_treatment_plan_items")
@EqualsAndHashCode(callSuper = true)
public class TreatmentPlanItemsReplica extends CommonsTreatmentPlanItems implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1530748814132806406L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public TreatmentPlanItemsReplica() {
		super();
	}

	
}
