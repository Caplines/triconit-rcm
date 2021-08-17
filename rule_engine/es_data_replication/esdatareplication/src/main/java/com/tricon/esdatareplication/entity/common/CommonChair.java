package com.tricon.esdatareplication.entity.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonChair extends CommonCloudColumn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3702399230848008151L;

	@Column(name = "chair_name", length = 50)
	String chairName;

	@Column(name = "chair_num",unique=true, length = 50)
	Integer chairNum;

	@Column(name = "sun_temp_id", length = 50)
	Integer sunTempId;

	@Column(name = "mon_temp_id", length = 50)
	Integer monTempId;

	@Column(name = "tue_temp_id", length = 50)
	Integer tueTempId;

	@Column(name = "wed_temp_id", length = 50)
	Integer wedTempId;

	@Column(name = "thu_temp_id", length = 50)
	Integer thuTempId;

	@Column(name = "fri_temp_id", length = 50)
	Integer friTempId;

	@Column(name = "sat_temp_id", length = 50)
	Integer satTempId;

	@Column(name = "practice_id", length = 50)
	Integer practiceId;


}
