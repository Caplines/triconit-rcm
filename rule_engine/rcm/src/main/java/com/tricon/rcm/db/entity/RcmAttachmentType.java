package com.tricon.rcm.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_attachment_type")
public class RcmAttachmentType implements java.io.Serializable {

	private static final long serialVersionUID = 7492103862018669602L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@Column(name = "attachmentType", nullable = false,length = 50)
	private String attachmentType;
}
