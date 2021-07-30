package com.tricon.esdatareplication.entity.ruleenginedb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonTransactionsDetail;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "es_data_replica_transaction_detail")
@EqualsAndHashCode(callSuper = true)
public class TransactionsDetailReplica extends CommonTransactionsDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2999991872363272010L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public TransactionsDetailReplica() {
		super();
	}

}
