package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "office_colud_name")
public class Office implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7962204595313115965L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "office_name", unique = true, nullable = false)
	String officeName;

	@Column(name = "uuid", unique = true, nullable = false)
    String uuid;
	
}
