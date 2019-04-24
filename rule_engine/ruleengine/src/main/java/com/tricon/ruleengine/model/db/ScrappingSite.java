package com.tricon.ruleengine.model.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Entity
@Table(name = "scrapping_site")
public class ScrappingSite extends BaseAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5945209812248233498L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "site_name")
	private String siteName;

	@Column(name = "site_url")
	private String siteUrl;

	@Column(name = "description")
	private String description;

    @OneToMany(mappedBy = "scrappingSite", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<ScrappingSiteDetails> siteSiteDetails = new HashSet<ScrappingSiteDetails>();
	 
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<ScrappingSiteDetails> getSiteSiteDetails() {
		return siteSiteDetails;
	}

	public void setSiteSiteDetails(Set<ScrappingSiteDetails> siteSiteDetails) {
		this.siteSiteDetails = siteSiteDetails;
	}

		

}
