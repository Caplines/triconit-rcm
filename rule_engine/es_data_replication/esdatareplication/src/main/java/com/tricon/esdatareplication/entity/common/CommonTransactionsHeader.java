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
public class CommonTransactionsHeader extends CommonCloudColumn {


	/**
	 * Checked
	 */
	private static final long serialVersionUID = -7483499943910321403L;

	@Column(name = "tran_num",length=50,nullable=false, unique= true)//	1	integer	No
	Integer tranNum;

	@Column	(name="user_id",length=50,nullable=false)//	2	char(3)
	String userId;

	@Column	(name="type",length=50)//	3	char(1)
	String type;

	@Column	(name="tran_date",length=50,nullable=false)//	4	date	No
	@Temporal(TemporalType.DATE)
	Date tranDate;

	@Column	(name="resp_party_id",length=50)//	5	char(5)	
	String respPartyId;

	@Column	(name="amount",length=50)//	6	numeric(15,2)
	Double amount;

	@Column	(name="service_code",length=50)//	7	char(5)	
	String serviceCode;

	@Column	(name="paytype_id",length=50,nullable=false)// 8	smallint
	Integer paytypeId;

	@Column	(name="sequence",length=50)//	9	smallint
	Integer sequence;

	@Column	(name="statement_num",length=50)//	10	integer	
	Integer statementNum;

	@Column	(name="surface",length=50)//	11	char(8)	
	String surface;

	@Column	(name="fee",length=50)//	12	numeric(15,2)
	Double fee;

	@Column	(name="discount_surcharge",length=50)//	13	numeric(15,2)
	Double discountSurcharge;

	@Column	(name="tax",length=50)//	14	numeric(15,2)
	Double tax;

	@Column	(name="description",length=50)// 15	varchar(50)
	String description;

	@Column	(name="defective",length=50)//	16	char(1)
	String defective;

	@Column	(name="impacts",length=50)//	17	char(1)
	String impacts;

	@Column	(name="status",length=150)//	18	char(1)
	String status;

	@Column	(name="adjustment_type",length=50)//	19	smallint
	Integer adjustmentType;

	@Column	(name="claim_id",length=50)//	20	integer	
	Integer claimId;

	@Column	(name="est_primary",length=50)//	21	numeric(15,2)
	Double estPrimary;

	@Column	(name="est_secondary",length=50)//	22	integer	Yes
	Double estSecondary;

	@Column	(name="paid_primary",length=50)//	23	numeric(15,2)	Yes
	Double paidPrimary;

	@Column	(name="paid_secondary",length=50)//	24	numeric(15,2)	Yes
	Double paidSecondary;

	@Column	(name="bulk_payment_num",length=50)//	25	integer		Yes
	Integer bulkPaymentNum;

	@Column	(name="aging_date",length=50)//	26	numeric(15,2)	Yes
	@Temporal(TemporalType.DATE)
	Date agingDate;

	@Column	(name="tooth",length=50)//	27	char(10)
	String tooth;

	@Column	(name="lab_fee",length=50)//	28	numeric(15,2)
	Double labFee;

	@Column	(name="lab_fee2",length=50)//	29 numeric(15,2)
	Double labFee2;

	@Column	(name="lab_code",length=50)//	30	char(5)
	String labCode;

	@Column	(name="lab_code2",length=50)//	31	char(5)
	String labCode2;

	@Column	(name="pre_fee",length=50)//	32	numeric(15,2)
	Double preFee;

	@Column	(name="standard_fee_id",length=50)//	33	integer
	Integer standardFeeId;

	@Column	(name="practice_id",length=50)//	34	smallint	
	Integer practiceId;


	@Column	(name="procedure_type_codes",length=50)//	35	char(6)	Yes
	String procedureTypeCodes;

	@Column	(name="balance",length=50)//	36	numeric(15,2)	Yes
	Double balance;

}
