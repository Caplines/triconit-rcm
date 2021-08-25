package com.tricon.esdatareplication.entity.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonPaymentProvider extends CommonCloudColumn {

	/**
	 * Checked
	 */
	private static final long serialVersionUID = 8314485039583453501L;

	@Column(name = "tran_num", length = 50, nullable = false)
	Integer tranNum;

	@Column(name = "provider_id", length = 50)
	String providerId;

	@Column(name = "amount", length = 50)
	Double amount;

	@Column(name = "practice_id", length = 50)
	Integer practiceId;

	@Column(name = "prod_provider_id", length = 50)
	String prodProviderId;

}
