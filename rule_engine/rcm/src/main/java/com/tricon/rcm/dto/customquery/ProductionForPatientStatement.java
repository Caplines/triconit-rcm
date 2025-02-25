package com.tricon.rcm.dto.customquery;

public interface ProductionForPatientStatement {

	//int getStatementType();

	int getTotal();

	int getDays();

	String getUuid();

	String getFName();

	String getLName();

	String getCompanyName();
	
	int getId();
	
	int getStatementType1();
	int getStatementType2();
	int getStatementType3();
}
