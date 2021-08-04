package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonAppointment;
import com.tricon.esdatareplication.entity.common.CommonPlannedServices;
import com.tricon.esdatareplication.util.Constants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = Constants.TABLE_PLANNED_SERVICES)
@EqualsAndHashCode(callSuper = true)
public class PlannedServices extends CommonPlannedServices implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6264197288902564036L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public PlannedServices() {
		super();
	}

}
