package com.tricon.esdatareplication.entity.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CommonChair extends CommonCloudColumn {

	@Column(name = "patient_id", length = 50)
	Integer patientId;

	@Column(name = "chair_name", length = 50)
	String chairName;

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
