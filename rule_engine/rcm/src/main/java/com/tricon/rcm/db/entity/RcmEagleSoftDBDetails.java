package com.tricon.rcm.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "eaglesoft_db_details")//Used in Rule Engine As well..
public class RcmEagleSoftDBDetails  implements Serializable{

		
		private static final long serialVersionUID = -5388242519384452950L;

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Column(name = "id", unique = true, nullable = false)
		private int id;

		@Column(name = "ip_address")
		private String ipAddress;

		@Column(name = "es_port")
		private int eSport;

		@Column(name = "password")
		private String password;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "office_id", unique=true)
		private RcmOffice office;

		@Column(name = "is_server")
		private boolean server;
		
	    @Column(name = "is_sheet")
		private int sheet;
	    
}
