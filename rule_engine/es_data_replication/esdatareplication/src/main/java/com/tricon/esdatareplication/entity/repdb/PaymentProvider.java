package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.Immutable;

import com.tricon.esdatareplication.entity.common.CommonPaymentProvider;
import com.tricon.esdatareplication.util.Constants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Immutable
@Table(name = Constants.TABLE_PAYMENT_PROVIDER)
/*
 * uniqueConstraints = {
		@UniqueConstraint(columnNames = { "tran_num", "provider_id","prod_provider_id" }) }
 */
@EqualsAndHashCode(callSuper = true)
public class PaymentProvider extends CommonPaymentProvider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7604316076800313420L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	public PaymentProvider() {
		super();
	}

}
