package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.tricon.esdatareplication.entity.common.CommonPlannedServices;
import com.tricon.esdatareplication.entity.common.CommonProviders;
import com.tricon.esdatareplication.util.Constants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = Constants.TABLE_PROVIDER, uniqueConstraints = {
		@UniqueConstraint(columnNames = {"provider_id", "office_id" }) })
@EqualsAndHashCode(callSuper = true)
public class Provider  extends CommonProviders implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4839975981522375579L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	public Provider() {
		super();
	}

	

}
