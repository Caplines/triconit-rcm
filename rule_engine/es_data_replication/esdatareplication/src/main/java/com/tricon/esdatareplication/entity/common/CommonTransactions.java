package com.tricon.esdatareplication.entity.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonTransactions extends CommonCloudColumn {


	/**
	 * Checked
	 */
	private static final long serialVersionUID = -7483499943910321403L;

	@Column(name = "tran_num",length=50,nullable=false, unique= true)//	1	integer	No
	Integer tranNum;

	@Column	(name="user_id",length=50,nullable=false)//	No
	String userId;

	@Column	(name="type",length=50,nullable=false)//	No
	String type;

	@Column	(name="tran_date",length=50,nullable=false)//	4	date	No
	@Temporal(TemporalType.DATE)
	Date tranDate;

	@Column	(name="patient_id",length=50)//	5	char(5)	Yes
	String patientId;

	@Column(name = "resp_party_id", length = 50, nullable = false) // 6 char(5) No
	String respPartyId;

	@Column	(name="amount",length=50)//	7	numeric(15,2)	Yes
	Double amount;

	@Column	(name="service_code",length=50)//	8	char(5)	Yes
	String serviceCode;

	@Column	(name="paytype_id",length=50)//	9	smallint	Yes
	Integer paytypeId;

	@Column	(name="sequence",length=50)//	10	smallint	Yes
	Integer sequence;

	@Column	(name="provider_id",length=50)//	11	char(3)	Yes
	String providerId;

	@Column	(name="collections_go_to",length=50)//	12	char(3)	Yes
	String collectionsGoTo;

	@Column	(name="statement_num",length=50)//	13	integer	Yes
	Integer statementNum;

	@Column	(name="old_tooth",length=50)//	14	smallint	Yes
	Integer oldTooth;

	@Column	(name="surface",length=50)//	15	char(8)	Yes
	String surface;

	@Column	(name="fee",length=50)//	16	numeric(15,2)	Yes
	Double fee;

	@Column	(name="discount_surcharge",length=50)//	17	numeric(15,2)	Yes
	Double discountSurcharge;

	@Column	(name="tax",length=50)//	18	numeric(15,2)	Yes
	Double tax;

	@Column	(name="description",length=150)//	19	varchar(50)	Yes
	String description;

	@Column	(name="defective",length=50)//	20	char(1)	Yes
	String defective;

	@Column	(name="impacts",length=50)//	21	char(1)	Yes
	String impacts;

	@Column	(name="status",length=50)//	22	char(1)	Yes
	String status;

	@Column	(name="adjustment_type",length=50)//	23	smallint	Yes
	Integer adjustmentType;

	@Column	(name="claim_id",length=50)//	24	integer	Yes
	Integer claimId;

	@Column	(name="est_primary",length=50)//	25	numeric(15,2)	Yes
	Double estPrimary;

	@Column	(name="est_secondary",length=50)//	26	numeric(15,2)	Yes
	Double estSecondary;

	@Column	(name="paid_primary",length=50)//	27	numeric(15,2)	Yes
	Double paidPrimary;

	@Column	(name="paid_secondary",length=50)//	28	numeric(15,2)	Yes
	Double paidSecondary;

	@Column	(name="provider_practice_id",length=50)//	29	smallint	Yes
	Integer providerPracticeId;

	@Column	(name="patient_practice_id",length=50)//	30	smallint	Yes
	Integer patientPracticeId;

	@Column	(name="bulk_payment_num",length=50)//	31	integer	Yes
	Integer bulkPaymentNum;

	@Column	(name="aging_date",length=50)//	32	date	Yes
	@Temporal(TemporalType.DATE)
	Date agingDate;

	@Column	(name="tooth",length=50)//	33	char(10)	Yes
	String tooth;

	@Column	(name="lab_fee",length=50)//	34	numeric(15,2)	Yes
	Double labFee;

	@Column	(name="lab_fee2",length=50)//	35	numeric(15,2)	Yes
	Double labFee2;

	@Column	(name="lab_code",length=50)//	36	char(5)	Yes
	String labCode;

	@Column	(name="lab_code2",length=50)//	37	char(5)	Yes
	String labCode2;

	@Column	(name="pre_fee",length=50)//	38	numeric(15,2)	Yes
	Double preFee;

	@Column	(name="standard_fee_id",length=50)//	39	integer	Yes
	Integer standardFeeId;

	@Column	(name="practice_id",length=50)//	40	smallint	Yes
	Integer practiceId;

	@Column	(name="procedure_type_codes",length=50)//	41	char(6)	Yes
	String procedureTypeCodes;

	@Column(name="balance",length=50)//	42	numeric(15,2)	Yes
	Double balance;

}
