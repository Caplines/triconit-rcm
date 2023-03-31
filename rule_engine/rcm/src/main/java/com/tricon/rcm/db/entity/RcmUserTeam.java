package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_user_team")
public class RcmUserTeam implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7161291370845181901L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rcm_user_id",referencedColumnName="uuid")
	private RcmUser user;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "team_id",referencedColumnName="id")
	private RcmTeam team;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RcmUser getUser() {
		return user;
	}

	public void setUser(RcmUser user) {
		this.user = user;
	}

	public RcmTeam getTeam() {
		return team;
	}

	public void setTeam(RcmTeam team) {
		this.team = team;
	}
	
	
	
}
