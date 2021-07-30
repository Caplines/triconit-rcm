package com.tricon.esdatareplication.entity.ruleenginedb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonPlannedServices;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "es_data_replica_planned_services")
@EqualsAndHashCode(callSuper = true)
public class PlannedServicesReplica  extends CommonPlannedServices implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 651146093238561903L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public PlannedServicesReplica() {
		super();
	}

}
