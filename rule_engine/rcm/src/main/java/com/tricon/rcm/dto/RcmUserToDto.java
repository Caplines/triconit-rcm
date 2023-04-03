package com.tricon.rcm.dto;

import java.util.List;

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
	   String getClientName();
	   String getFirstName();
	   String getLastName();
	   String getRoles();
	
}
