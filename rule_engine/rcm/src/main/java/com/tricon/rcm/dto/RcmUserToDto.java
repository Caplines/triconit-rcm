package com.tricon.rcm.dto;

/**
 * convert rcmUser into DTO when fetch data from db for native query
 * @author Admin
 *
 */
public interface RcmUserToDto {
 
	   String getUuid();
	   Integer getActive();
	   String getEmail();
	   String getFullName();
	
}
