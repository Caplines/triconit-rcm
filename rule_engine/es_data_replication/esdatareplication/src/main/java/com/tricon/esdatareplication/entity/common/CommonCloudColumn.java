package com.tricon.esdatareplication.entity.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public class CommonCloudColumn {

	
	@Column(name = "moved_to_cloud", nullable = false)
	private int movedToCloud;
}
