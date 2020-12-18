package com.tricon.ruleengine.model.db;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class CommonPatientDetailLeft2 extends BaseAudit {

	
	private static final long serialVersionUID = -1327704663073670130L;


	
	@Column(name = "npi", length = 30)
    private String npi;
	
	@Column(name = "licence", length = 30)
    private String licence;
	
	//MTC
	@Column(name = "radio3", length = 4)
    private String radio3;
	
	//Eligible for D3330?
	@Column(name = "radio4", length = 4)
    private String radio4;

	//Is there any waiting period?
	@Column(name = "radio5", length = 4)
    private String radio5;

	//Out of Network Benefits:
	@Column(name = "radio1", length = 4)
    private String radio1;

	//Do you file OS under medical first?
	@Column(name = "radio2", length = 4)
    private String radio2;

	@Column(name = "corrd_Of_benefits", length = 50)
    private String corrdOfBenefits;
	
	@Column(name = "what_amount_d7210", length = 50)
    private String whatAmountD7210;
	
	@Column(name = "allow_amount_d7240", length = 50)
    private String allowAmountD7240;
	
	@Column(name = "d7210", length = 50)
    private String d7210;
	
	@Column(name = "d7220", length = 50)
	private String d7220;
	
	@Column(name = "d7230", length = 50)
	private String d7230;
	
	@Column(name = "d7240", length = 50)
	private String d7240;
	
	@Column(name = "d7250", length = 50)
	private String d7250;

	@Column(name = "d7310", length = 50)
	private String d7310;
	
	@Column(name = "d7311", length = 50)
	private String d7311;
	
	@Column(name = "d7320", length = 50)
	private String d7320;
	
	@Column(name = "d7321", length = 50)
	private String d7321;
	
	@Column(name = "d7473", length = 50)
	private String d7473;

	@Column(name = "d9239", length = 50)
	private String d9239;
	
	@Column(name = "d4263", length = 50)
	private String d4263;
	
	@Column(name = "d4264", length = 50)
	private String d4264;
	
	@Column(name = "d6104", length = 50)
	private String d6104;
	
	@Column(name = "d7953", length = 50)
	private String d7953;
	
	@Column(name = "d3310", length = 50)
	private String d3310;
	
	@Column(name = "d3320", length = 50)
	private String d3320;
	
	@Column(name = "d3330", length = 50)
	private String d3330;
	
	@Column(name = "d3346", length = 50)
	private String d3346;
	
	@Column(name = "d3347", length = 50)
	private String d3347;
	
	@Column(name = "d3348", length = 50)
	private String d3348;

	@Column(name = "d6058", length = 50)
	private String d6058;
	
	@Column(name = "d7951", length = 50)
	private String d7951;

	@Column(name = "d4266", length = 50)
	private String d4266;
	
	@Column(name = "d4267", length = 50)
	private String d4267;

	@Column(name = "d4273", length = 50)
	private String d4273;
	
	@Column(name = "d7251", length = 50)
	private String d7251;
	
	@Column(name = "ivSedation", length = 50)
	private String ivSedation;

	
	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getLicence() {
		return licence;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	public String getRadio3() {
		return radio3;
	}

	public void setRadio3(String radio3) {
		this.radio3 = radio3;
	}

	public String getRadio4() {
		return radio4;
	}

	public void setRadio4(String radio4) {
		this.radio4 = radio4;
	}

	public String getRadio5() {
		return radio5;
	}

	public void setRadio5(String radio5) {
		this.radio5 = radio5;
	}

	public String getRadio1() {
		return radio1;
	}

	public void setRadio1(String radio1) {
		this.radio1 = radio1;
	}

	public String getRadio2() {
		return radio2;
	}

	public void setRadio2(String radio2) {
		this.radio2 = radio2;
	}

	public String getCorrdOfBenefits() {
		return corrdOfBenefits;
	}

	public void setCorrdOfBenefits(String corrdOfBenefits) {
		this.corrdOfBenefits = corrdOfBenefits;
	}

	public String getWhatAmountD7210() {
		return whatAmountD7210;
	}

	public void setWhatAmountD7210(String whatAmountD7210) {
		this.whatAmountD7210 = whatAmountD7210;
	}

	public String getAllowAmountD7240() {
		return allowAmountD7240;
	}

	public void setAllowAmountD7240(String allowAmountD7240) {
		this.allowAmountD7240 = allowAmountD7240;
	}

	public String getD7210() {
		return d7210;
	}

	public void setD7210(String d7210) {
		this.d7210 = d7210;
	}

	public String getD7220() {
		return d7220;
	}

	public void setD7220(String d7220) {
		this.d7220 = d7220;
	}

	public String getD7230() {
		return d7230;
	}

	public void setD7230(String d7230) {
		this.d7230 = d7230;
	}

	public String getD7240() {
		return d7240;
	}

	public void setD7240(String d7240) {
		this.d7240 = d7240;
	}

	public String getD7250() {
		return d7250;
	}

	public void setD7250(String d7250) {
		this.d7250 = d7250;
	}

	public String getD7310() {
		return d7310;
	}

	public void setD7310(String d7310) {
		this.d7310 = d7310;
	}

	public String getD7311() {
		return d7311;
	}

	public void setD7311(String d7311) {
		this.d7311 = d7311;
	}

	public String getD7320() {
		return d7320;
	}

	public void setD7320(String d7320) {
		this.d7320 = d7320;
	}

	public String getD7321() {
		return d7321;
	}

	public void setD7321(String d7321) {
		this.d7321 = d7321;
	}

	public String getD7473() {
		return d7473;
	}

	public void setD7473(String d7473) {
		this.d7473 = d7473;
	}

	public String getD9239() {
		return d9239;
	}

	public void setD9239(String d9239) {
		this.d9239 = d9239;
	}

	public String getD4263() {
		return d4263;
	}

	public void setD4263(String d4263) {
		this.d4263 = d4263;
	}

	public String getD4264() {
		return d4264;
	}

	public void setD4264(String d4264) {
		this.d4264 = d4264;
	}

	public String getD6104() {
		return d6104;
	}

	public void setD6104(String d6104) {
		this.d6104 = d6104;
	}

	public String getD7953() {
		return d7953;
	}

	public void setD7953(String d7953) {
		this.d7953 = d7953;
	}

	public String getD3310() {
		return d3310;
	}

	public void setD3310(String d3310) {
		this.d3310 = d3310;
	}

	public String getD3320() {
		return d3320;
	}

	public void setD3320(String d3320) {
		this.d3320 = d3320;
	}

	public String getD3330() {
		return d3330;
	}

	public void setD3330(String d3330) {
		this.d3330 = d3330;
	}

	public String getD3346() {
		return d3346;
	}

	public void setD3346(String d3346) {
		this.d3346 = d3346;
	}

	public String getD3347() {
		return d3347;
	}

	public void setD3347(String d3347) {
		this.d3347 = d3347;
	}

	public String getD3348() {
		return d3348;
	}

	public void setD3348(String d3348) {
		this.d3348 = d3348;
	}

	public String getD6058() {
		return d6058;
	}

	public void setD6058(String d6058) {
		this.d6058 = d6058;
	}

	public String getD7951() {
		return d7951;
	}

	public void setD7951(String d7951) {
		this.d7951 = d7951;
	}

	public String getD4266() {
		return d4266;
	}

	public void setD4266(String d4266) {
		this.d4266 = d4266;
	}

	public String getD4267() {
		return d4267;
	}

	public void setD4267(String d4267) {
		this.d4267 = d4267;
	}

	public String getD4273() {
		return d4273;
	}

	public void setD4273(String d4273) {
		this.d4273 = d4273;
	}

	public String getD7251() {
		return d7251;
	}

	public void setD7251(String d7251) {
		this.d7251 = d7251;
	}

	public String getIvSedation() {
		return ivSedation;
	}

	public void setIvSedation(String ivSedation) {
		this.ivSedation = ivSedation;
	}

	
	

}

