package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;	
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * This entity class represents mvp and vap data 
 * @author Deepak.Dogra
 *
 */

@Entity
@Table(name = "mvp_and_vap")
public class MVPandVAP  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 726038346161534031L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;


	@Column(name = "base_group")
	private String baseGroup;

	@Column(name = "base")
	private String base;


	@Column(name = "mvp")
	private String mvp;
	
	@Column(name = "vap1")
	private String vap1;

	@Column(name = "vap2")
	private String vap2;

	@Column(name = "vap3")
	private String vap3;

	@Column(name = "vap4")
	private String vap4;

	@Column(name = "vap5")
	private String vap5;

	@Column(name = "vap6")
	private String vap6;

	@Column(name = "vap7")
	private String vap7;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBaseGroup() {
		return baseGroup;
	}

	public void setBaseGroup(String baseGroup) {
		this.baseGroup = baseGroup;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getMvp() {
		return mvp;
	}

	public void setMvp(String mvp) {
		this.mvp = mvp;
	}

	public String getVap1() {
		return vap1;
	}

	public void setVap1(String vap1) {
		this.vap1 = vap1;
	}

	public String getVap2() {
		return vap2;
	}

	public void setVap2(String vap2) {
		this.vap2 = vap2;
	}

	public String getVap3() {
		return vap3;
	}

	public void setVap3(String vap3) {
		this.vap3 = vap3;
	}

	public String getVap4() {
		return vap4;
	}

	public void setVap4(String vap4) {
		this.vap4 = vap4;
	}

	public String getVap5() {
		return vap5;
	}

	public void setVap5(String vap5) {
		this.vap5 = vap5;
	}

	public String getVap6() {
		return vap6;
	}

	public void setVap6(String vap6) {
		this.vap6 = vap6;
	}

	public String getVap7() {
		return vap7;
	}

	public void setVap7(String vap7) {
		this.vap7 = vap7;
	}




@Override
public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((id == null) ? 0 : id.hashCode());
  return result;
}

@Override
public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MVPandVAP other = (MVPandVAP) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!baseGroup.equals(other.baseGroup))
      return false;
    return true;
  }




}
