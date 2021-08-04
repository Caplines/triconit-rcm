package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonTransactionsDetail;
import com.tricon.esdatareplication.entity.common.CommonsTreatmentPlanItems;
import com.tricon.esdatareplication.util.Constants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = Constants.TABLE_TREATMENT_PLAN_ITEMS)
@EqualsAndHashCode(callSuper = true)
public class TreatmentPlanItems extends CommonsTreatmentPlanItems implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1297131196713289656L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public TreatmentPlanItems() {
		super();
	}

}
