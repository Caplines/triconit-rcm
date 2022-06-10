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

	
	@Column(name = "d7210fr", length = 30)
    private String d7210fr;

	@Column(name = "d7220fr", length = 30)
    private String d7220fr;

	@Column(name = "d7230fr", length = 30)
    private String d7230fr;

	@Column(name = "d7240fr", length = 30)
    private String d7240fr;

	@Column(name = "d7250fr", length = 30)
    private String d7250fr;

	@Column(name = "d7310fr", length = 30)
    private String d7310fr;

	@Column(name = "d7311fr", length = 30)
    private String d7311fr;

	@Column(name = "d7320fr", length = 30)
    private String d7320fr;

	@Column(name = "d7321fr", length = 30)
    private String d7321fr;

	@Column(name = "d7473fr", length = 30)
    private String d7473fr;

	@Column(name = "sedations1fr", length = 30)
    private String sedations1fr;

	@Column(name = "sedations3fr", length = 30)
    private String sedations3fr;

	@Column(name = "d9239fr", length = 30)
    private String d9239fr;

	@Column(name = "sedations2fr", length = 30)
    private String sedations2fr;

	@Column(name = "d4263fr", length = 30)
    private String d4263fr;

	@Column(name = "d4264fr", length = 30)
    private String d4264fr;

	@Column(name = "d6104fr", length = 30)
    private String d6104fr;

	@Column(name = "d7953fr", length = 30)
    private String d7953fr;

	@Column(name = "d3310fr", length = 30)
    private String d3310fr;

	@Column(name = "d3320fr", length = 30)
    private String d3320fr;

	@Column(name = "d3346fr", length = 30)
    private String d3346fr;

	@Column(name = "d3347fr", length = 30)
    private String d3347fr;

	@Column(name = "d3348fr", length = 30)
    private String d3348fr;

	@Column(name = "d6058fr", length = 30)
    private String d6058fr;

	@Column(name = "oral1fr", length = 30)
    private String oral1fr;

	@Column(name = "d7951fr", length = 30)
    private String d7951fr;

	@Column(name = "d4266fr", length = 30)
    private String d4266fr;

	@Column(name = "d4267fr", length = 30)
    private String d4267fr;

	@Column(name = "perio1fr", length = 30)
    private String perio1fr;

	@Column(name = "d4273fr", length = 30)
    private String d4273fr;

	@Column(name = "d7251fr", length = 30)
    private String d7251fr;
	
	@Column(name = "d7472", length = 30)
	private String d7472;
	
	@Column(name = "d7472fr", length = 30)
	private String d7472fr;
	
	@Column(name = "d7280", length = 30)
	private String d7280;
	
	@Column(name = "d7280fr", length = 30)
	private String d7280fr;
	
	@Column(name = "d7282", length = 30)
	private String d7282;
	
	@Column(name = "d7282fr", length = 30)
	private String d7282fr;
	
	@Column(name = "d7283", length = 30)
	private String d7283;
	
	@Column(name = "d7283fr", length = 30)
	private String d7283fr;
	
	@Column(name = "d7952", length = 30)
	private String d7952;
	
	@Column(name = "d7952fr", length = 30)
	private String d7952fr;
	
	@Column(name = "d7285", length = 30)
	private String d7285;
	
	@Column(name = "d7285fr", length = 30)
	private String d7285fr;
	
	@Column(name = "d6114", length = 30)
	private String d6114;
	
	@Column(name = "d6114fr", length = 30)
	private String d6114fr;
	
	@Column(name = "d5860", length = 30)
	private String d5860;
	
	@Column(name = "d5860fr", length = 30)
	private String d5860fr;
	
	@Column(name = "d5110", length = 30)
	private String d5110;
	
	@Column(name = "d5110fr", length = 30)
	private String d5110fr;
	
	@Column(name = "d5130", length = 30)
	private String d5130;
	
	@Column(name = "d5130fr", length = 30)
	private String d5130fr;
	
	@Column(name = "d0140", length = 30)
	private String d0140;

	@Column(name = "s_remarks", length = 50)
	private String sRemarks;

	@Column(name = "m_policy", length = 10)
	private String mPolicy;//Not used now

	@Column(name = "m_mip", length = 30)
	private String mMIP;//Not used now


	@Column(name = "es_bcbs", length = 30)
	private String esBcbs;

	@Column(name = "obtain_mpn", length = 30)
	private String obtainMPN;
	
	@Column(name = "waiting_period_duration", length = 30)
	private String waitingPeriodDuration;
	
	@Column(name = "d0350", length = 30)
	private String d0350;

	@Column(name = "d1330", length = 30)
	private String d1330;

	@Column(name = "d2930", length = 30)
	private String d2930;

	@Column(name = "srpd_4341", length = 30)
	private String srpd4341;

	@Column(name = "major_d72101", length = 30)
	private String majord72101;

	@Column(name = "fmx_subject_to_ded", length = 30)
	private String fmxSubjectToDed;

	@Column(name = "d1510", length = 30)
	private String d1510;

	@Column(name = "d1510_freq", length = 30)
	private String d1510Freq;

	@Column(name = "d1516", length = 30)
	private String d1516;

	@Column(name = "d1516_freq", length = 30)
	private String d1516Freq;

	@Column(name = "d1517", length = 30)
	private String d1517;

	@Column(name = "d1517_freq", length = 30)
	private String d1517Freq;

	@Column(name = "d3220", length = 30)
	private String d3220;

	@Column(name = "d3220_freq", length = 30)
	private String d3220Freq;

	@Column(name = "out_network_message", columnDefinition = "text")
	private String outNetworkMessage;
	
	@Column(name = "os_plan_type", length = 70)
	private String osPlanType;

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

	public String getD7210fr() {
		return d7210fr;
	}

	public void setD7210fr(String d7210fr) {
		this.d7210fr = d7210fr;
	}

	public String getD7220fr() {
		return d7220fr;
	}

	public void setD7220fr(String d7220fr) {
		this.d7220fr = d7220fr;
	}

	public String getD7230fr() {
		return d7230fr;
	}

	public void setD7230fr(String d7230fr) {
		this.d7230fr = d7230fr;
	}

	public String getD7240fr() {
		return d7240fr;
	}

	public void setD7240fr(String d7240fr) {
		this.d7240fr = d7240fr;
	}

	public String getD7250fr() {
		return d7250fr;
	}

	public void setD7250fr(String d7250fr) {
		this.d7250fr = d7250fr;
	}

	public String getD7310fr() {
		return d7310fr;
	}

	public void setD7310fr(String d7310fr) {
		this.d7310fr = d7310fr;
	}

	public String getD7311fr() {
		return d7311fr;
	}

	public void setD7311fr(String d7311fr) {
		this.d7311fr = d7311fr;
	}

	public String getD7320fr() {
		return d7320fr;
	}

	public void setD7320fr(String d7320fr) {
		this.d7320fr = d7320fr;
	}

	public String getD7321fr() {
		return d7321fr;
	}

	public void setD7321fr(String d7321fr) {
		this.d7321fr = d7321fr;
	}

	public String getD7473fr() {
		return d7473fr;
	}

	public void setD7473fr(String d7473fr) {
		this.d7473fr = d7473fr;
	}

	public String getSedations1fr() {
		return sedations1fr;
	}

	public void setSedations1fr(String sedations1fr) {
		this.sedations1fr = sedations1fr;
	}

	public String getSedations3fr() {
		return sedations3fr;
	}

	public void setSedations3fr(String sedations3fr) {
		this.sedations3fr = sedations3fr;
	}

	public String getD9239fr() {
		return d9239fr;
	}

	public void setD9239fr(String d9239fr) {
		this.d9239fr = d9239fr;
	}

	public String getSedations2fr() {
		return sedations2fr;
	}

	public void setSedations2fr(String sedations2fr) {
		this.sedations2fr = sedations2fr;
	}

	public String getD4263fr() {
		return d4263fr;
	}

	public void setD4263fr(String d4263fr) {
		this.d4263fr = d4263fr;
	}

	public String getD4264fr() {
		return d4264fr;
	}

	public void setD4264fr(String d4264fr) {
		this.d4264fr = d4264fr;
	}

	public String getD6104fr() {
		return d6104fr;
	}

	public void setD6104fr(String d6104fr) {
		this.d6104fr = d6104fr;
	}

	public String getD7953fr() {
		return d7953fr;
	}

	public void setD7953fr(String d7953fr) {
		this.d7953fr = d7953fr;
	}

	public String getD3310fr() {
		return d3310fr;
	}

	public void setD3310fr(String d3310fr) {
		this.d3310fr = d3310fr;
	}

	public String getD3320fr() {
		return d3320fr;
	}

	public void setD3320fr(String d3320fr) {
		this.d3320fr = d3320fr;
	}

	public String getD3346fr() {
		return d3346fr;
	}

	public void setD3346fr(String d3346fr) {
		this.d3346fr = d3346fr;
	}

	public String getD3347fr() {
		return d3347fr;
	}

	public void setD3347fr(String d3347fr) {
		this.d3347fr = d3347fr;
	}

	public String getD3348fr() {
		return d3348fr;
	}

	public void setD3348fr(String d3348fr) {
		this.d3348fr = d3348fr;
	}

	public String getD6058fr() {
		return d6058fr;
	}

	public void setD6058fr(String d6058fr) {
		this.d6058fr = d6058fr;
	}

	public String getOral1fr() {
		return oral1fr;
	}

	public void setOral1fr(String oral1fr) {
		this.oral1fr = oral1fr;
	}

	public String getD7951fr() {
		return d7951fr;
	}

	public void setD7951fr(String d7951fr) {
		this.d7951fr = d7951fr;
	}

	public String getD4266fr() {
		return d4266fr;
	}

	public void setD4266fr(String d4266fr) {
		this.d4266fr = d4266fr;
	}

	public String getD4267fr() {
		return d4267fr;
	}

	public void setD4267fr(String d4267fr) {
		this.d4267fr = d4267fr;
	}

	public String getPerio1fr() {
		return perio1fr;
	}

	public void setPerio1fr(String perio1fr) {
		this.perio1fr = perio1fr;
	}

	public String getD4273fr() {
		return d4273fr;
	}

	public void setD4273fr(String d4273fr) {
		this.d4273fr = d4273fr;
	}

	public String getD7251fr() {
		return d7251fr;
	}

	public void setD7251fr(String d7251fr) {
		this.d7251fr = d7251fr;
	}

	public String getD7472() {
		return d7472;
	}

	public void setD7472(String d7472) {
		this.d7472 = d7472;
	}

	public String getD7472fr() {
		return d7472fr;
	}

	public void setD7472fr(String d7472fr) {
		this.d7472fr = d7472fr;
	}

	public String getD7280() {
		return d7280;
	}

	public void setD7280(String d7280) {
		this.d7280 = d7280;
	}

	public String getD7280fr() {
		return d7280fr;
	}

	public void setD7280fr(String d7280fr) {
		this.d7280fr = d7280fr;
	}

	public String getD7282() {
		return d7282;
	}

	public void setD7282(String d7282) {
		this.d7282 = d7282;
	}

	public String getD7282fr() {
		return d7282fr;
	}

	public void setD7282fr(String d7282fr) {
		this.d7282fr = d7282fr;
	}

	public String getD7283() {
		return d7283;
	}

	public void setD7283(String d7283) {
		this.d7283 = d7283;
	}

	public String getD7283fr() {
		return d7283fr;
	}

	public void setD7283fr(String d7283fr) {
		this.d7283fr = d7283fr;
	}

	public String getD7952() {
		return d7952;
	}

	public void setD7952(String d7952) {
		this.d7952 = d7952;
	}

	public String getD7952fr() {
		return d7952fr;
	}

	public void setD7952fr(String d7952fr) {
		this.d7952fr = d7952fr;
	}

	public String getD7285() {
		return d7285;
	}

	public void setD7285(String d7285) {
		this.d7285 = d7285;
	}

	public String getD7285fr() {
		return d7285fr;
	}

	public void setD7285fr(String d7285fr) {
		this.d7285fr = d7285fr;
	}

	public String getD6114() {
		return d6114;
	}

	public void setD6114(String d6114) {
		this.d6114 = d6114;
	}

	public String getD6114fr() {
		return d6114fr;
	}

	public void setD6114fr(String d6114fr) {
		this.d6114fr = d6114fr;
	}

	public String getD5860() {
		return d5860;
	}

	public void setD5860(String d5860) {
		this.d5860 = d5860;
	}

	public String getD5860fr() {
		return d5860fr;
	}

	public void setD5860fr(String d5860fr) {
		this.d5860fr = d5860fr;
	}

	public String getD5110() {
		return d5110;
	}

	public void setD5110(String d5110) {
		this.d5110 = d5110;
	}

	public String getD5110fr() {
		return d5110fr;
	}

	public void setD5110fr(String d5110fr) {
		this.d5110fr = d5110fr;
	}

	public String getD5130() {
		return d5130;
	}

	public void setD5130(String d5130) {
		this.d5130 = d5130;
	}

	public String getD5130fr() {
		return d5130fr;
	}

	public void setD5130fr(String d5130fr) {
		this.d5130fr = d5130fr;
	}

	public String getD0140() {
		return d0140;
	}

	public void setD0140(String d0140) {
		this.d0140 = d0140;
	}

	public String getsRemarks() {
		return sRemarks;
	}

	public void setsRemarks(String sRemarks) {
		this.sRemarks = sRemarks;
	}

	public String getmPolicy() {
		return mPolicy;
	}

	public void setmPolicy(String mPolicy) {
		this.mPolicy = mPolicy;
	}

	public String getmMIP() {
		return mMIP;
	}

	public void setmMIP(String mMIP) {
		this.mMIP = mMIP;
	}

	
	public String getEsBcbs() {
		return esBcbs;
	}

	public void setEsBcbs(String esBcbs) {
		this.esBcbs = esBcbs;
	}

	public String getObtainMPN() {
		return obtainMPN;
	}

	public void setObtainMPN(String obtainMPN) {
		this.obtainMPN = obtainMPN;
	}

	public String getWaitingPeriodDuration() {
		return waitingPeriodDuration;
	}

	public void setWaitingPeriodDuration(String waitingPeriodDuration) {
		this.waitingPeriodDuration = waitingPeriodDuration;
	}

	public String getD0350() {
		return d0350;
	}

	public void setD0350(String d0350) {
		this.d0350 = d0350;
	}

	public String getD1330() {
		return d1330;
	}

	public void setD1330(String d1330) {
		this.d1330 = d1330;
	}

	public String getD2930() {
		return d2930;
	}

	public void setD2930(String d2930) {
		this.d2930 = d2930;
	}

	public String getSrpd4341() {
		return srpd4341;
	}

	public void setSrpd4341(String srpd4341) {
		this.srpd4341 = srpd4341;
	}

	public String getMajord72101() {
		return majord72101;
	}

	public void setMajord72101(String majord72101) {
		this.majord72101 = majord72101;
	}

	public String getFmxSubjectToDed() {
		return fmxSubjectToDed;
	}

	public void setFmxSubjectToDed(String fmxSubjectToDed) {
		this.fmxSubjectToDed = fmxSubjectToDed;
	}

	public String getD1510() {
		return d1510;
	}

	public void setD1510(String d1510) {
		this.d1510 = d1510;
	}

	public String getD1510Freq() {
		return d1510Freq;
	}

	public void setD1510Freq(String d1510Freq) {
		this.d1510Freq = d1510Freq;
	}

	public String getD1516() {
		return d1516;
	}

	public void setD1516(String d1516) {
		this.d1516 = d1516;
	}

	public String getD1516Freq() {
		return d1516Freq;
	}

	public void setD1516Freq(String d1516Freq) {
		this.d1516Freq = d1516Freq;
	}

	public String getD1517() {
		return d1517;
	}

	public void setD1517(String d1517) {
		this.d1517 = d1517;
	}

	public String getD1517Freq() {
		return d1517Freq;
	}

	public void setD1517Freq(String d1517Freq) {
		this.d1517Freq = d1517Freq;
	}

	public String getD3220() {
		return d3220;
	}

	public void setD3220(String d3220) {
		this.d3220 = d3220;
	}

	public String getD3220Freq() {
		return d3220Freq;
	}

	public void setD3220Freq(String d3220Freq) {
		this.d3220Freq = d3220Freq;
	}

	public String getOutNetworkMessage() {
		return outNetworkMessage;
	}

	public void setOutNetworkMessage(String outNetworkMessage) {
		this.outNetworkMessage = outNetworkMessage;
	}

	public String getOsPlanType() {
		return osPlanType;
	}

	public void setOsPlanType(String osPlanType) {
		this.osPlanType = osPlanType;
	}

    
	
}

