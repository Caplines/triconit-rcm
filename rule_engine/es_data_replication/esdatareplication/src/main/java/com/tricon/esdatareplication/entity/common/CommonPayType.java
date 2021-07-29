package com.tricon.esdatareplication.entity.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonPayType extends CommonCloudColumn{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8265978139516272306L;

	@Column(name = "paytype_id", unique = true, nullable = false)
	private int payTypId;
	
	@Column(name = "sequence")
	private int sequence;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "prompt")
	private String prompt;
	
	@Column(name = "display_on_payment_screen")
	private String displayOonPaymentScreen;
	
	@Column(name = "currency_type")
	private String currencyType;
	
	@Column(name = "include_on_deposit_yn")
	private String includeOnDepositYn;
	
	@Column(name = "central_id")
	private String centralId;
	
	@Column(name = "system_required")
	private int systemRequired;

}
