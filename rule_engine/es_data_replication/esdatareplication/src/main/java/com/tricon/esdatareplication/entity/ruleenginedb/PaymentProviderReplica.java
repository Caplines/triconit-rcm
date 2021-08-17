package com.tricon.esdatareplication.entity.ruleenginedb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.esdatareplication.entity.common.CommonPaymentProvider;
import com.tricon.esdatareplication.util.Constants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = Constants.TABLE_REPLICA_IN_CLOUD+Constants.TABLE_PROVIDER)
@EqualsAndHashCode(callSuper = true)
public class PaymentProviderReplica  extends CommonPaymentProvider implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8742078312192125242L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	public PaymentProviderReplica() {
		super();
	}

}
