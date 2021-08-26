package com.tricon.esdatareplication.entity.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonTransactionsDetail extends CommonCloudColumn {

	/**
	 * Checked
	 */
	private static final long serialVersionUID = -1324960160058778881L;

	@Column(name = "detail_id", length = 50, nullable = false, unique =true)
	Integer detailId;

	@Column(name = "tran_num", length = 50)
	Integer tranNum;

	@Column(name = "user_id", length = 50)
	String userId;

	@Column(name = "date_entered")
	Date dateEntered;

	@Column(name = "provider_id", length = 50)
	String providerId;

	@Column(name = "collections_go_to", length = 50)
	String collectionsGoTo;

	@Column(name = "patient_id", length = 50)
	String patientId;

	@Column(name = "amount", length = 50)
	Double amount;

	@Column(name = "provider_practice_id", length = 50)
	Integer providerPracticeId;

	@Column(name = "patient_practice_id", length = 50)
	Integer patientPracticeId;

	@Column(name = "applied_to", length = 50)
	Integer appliedTo;

	@Column(name = "status", length = 50)
	Integer status;

	@Column(name = "status_modifier", length = 50)
	Integer statusModifier;

	@Column(name = "posneg", length = 50)
	Integer posneg;

}
