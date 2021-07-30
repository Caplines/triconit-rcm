package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonAppointment;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "appointment")
@EqualsAndHashCode(callSuper = true)
public class Appointment extends CommonAppointment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2310809895169377302L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public Appointment() {
		super();
	}

}
