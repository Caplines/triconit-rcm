package com.tricon.rcm.dto.customquery;

public interface ProductionForPaymentPosting {

	int getTotal();
	int getDays();
	String getUuid();
	String getFName();
	String getLName();
	String getCompanyName();
	Float getTotalAmountReceivedInBank();
}
