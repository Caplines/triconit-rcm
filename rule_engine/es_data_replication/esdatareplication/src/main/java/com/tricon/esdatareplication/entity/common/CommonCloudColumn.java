package com.tricon.esdatareplication.entity.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.tricon.esdatareplication.entity.BaseAudit;
import com.tricon.esdatareplication.entity.repdb.Patient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonCloudColumn extends BaseAudit{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1183613386475408146L;
	
	@Column(name = "moved_to_cloud", nullable = false)
	private int movedToCloud;
	
	@Column(name = "office_id", length = 150)
	String officeId;

}
