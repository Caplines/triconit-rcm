package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonTransactions;
import com.tricon.esdatareplication.entity.common.CommonTransactionsDetail;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "transaction_detail")
@EqualsAndHashCode(callSuper = true)
public class TransactionsDetail extends CommonTransactionsDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2943606730732217141L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	public TransactionsDetail() {
		super();
	}

}
