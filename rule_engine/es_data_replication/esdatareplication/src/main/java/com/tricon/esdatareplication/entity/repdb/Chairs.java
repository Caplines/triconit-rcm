package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonChair;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "chairs")
@EqualsAndHashCode(callSuper = true)
public class Chairs extends CommonChair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9108204739361903449L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	public Chairs() {
		super();
	}


}
