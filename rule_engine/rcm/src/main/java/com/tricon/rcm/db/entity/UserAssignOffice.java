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
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_user_assign_office",uniqueConstraints = { @UniqueConstraint(columnNames = { "team_id", "office_id" }) })
public class UserAssignOffice implements Serializable{
	
	private static final long serialVersionUID = 4089920262978783006L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "uuid",nullable = false)
	private RcmUser user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id",referencedColumnName="id",nullable = false)
	private RcmTeam team;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "office_id", referencedColumnName = "uuid",nullable = false)
	private RcmOffice office;
}
