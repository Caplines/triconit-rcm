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
	
	@Column(name = "sm_age_limit", length = 5)
	private String smAgeLimit;
	
	@Column(name = "perio_d4921", length = 30)
	private String perioD4921;

	@Column(name = "d4921_frequency", length = 30)
	private String d4921Frequency;

	@Column(name = "perio_d4266", length = 30)
	private String perioD4266;

	@Column(name = "d4266_frequency", length = 30)
	private String d4266Frequency;

	@Column(name = "perio_d9910", length = 30)
	private String perioD9910;
	
	@Column(name = "d9910_frequency", length = 6)
	private String d9910Frequency;
	
	@Column(name = "oonbenfits", length =6)
	private String oonbenfits;
	
	@Column(name = "d9630", length = 4)
	private String d9630;
	
	@Column(name = "d9630fr", length = 30)
	private String d9630fr;
	
	@Column(name = "d0431", length = 4)
	private String d0431;
	
	@Column(name = "d0431fr", length = 30)
	private String d0431fr;
	
	@Column(name = "d4999", length = 4)
	private String d4999;
	
	@Column(name = "d4999fr", length = 30)
	private String d4999fr;
	
	@Column(name = "d2962", length = 4)
	private String d2962;
	
	@Column(name = "d2962_fr", length = 30)
	private String d2962fr;
	
	@Column(name = "mis_toth_clause", length = 30)
	private String mistothclause;

//----
//	@Column(name = "d0140", length = 30)
//	private String d0140;

	@Column(name = "d0145", length = 30)
	private String d0145;

	@Column(name = "d0150", length = 30)
	private String d0150;

	@Column(name = "d2750", length = 30)
	private String d2750;

	@Column(name = "d2750_fr", length = 30)
	private String d2750fr;

	@Column(name = "d0220", length = 30)
	private String d0220;

	@Column(name = "d0220_freq", length = 30)
	private String d0220Freq;

	@Column(name = "d0230", length = 30)
	private String d0230;

	@Column(name = "bwx", length = 30)
	private String bwx;

	@Column(name = "d0210", length = 30)
	private String d0210;

	@Column(name = "d0210_freq", length = 30)
	private String d0210Freq;

	@Column(name = "d0350_freq", length = 30)
	private String d0350Freq;

	@Column(name = "bwx_freq", length = 30)
	private String bwxFreq;

	@Column(name = "d2931", length = 30)
	private String d2931;

	@Column(name = "d1206", length = 30)
	private String d1206;

	@Column(name = "d1208", length = 30)
	private String d1208;

	@Column(name = "b_which_code", length = 30)
	private String bWhichCode;

	@Column(name = "d5110_20", length = 30)
	private String d5110_20;

	@Column(name = "d1330_freq", length = 30)
	private String d1330Freq;

	@Column(name = "d5111_12_13_14", length = 30)
	private String d5111_12_13_14;

	@Column(name = "d5130_40", length = 30)
	private String d5130_40;

	@Column(name = "d5810_c", length = 30)
	private String d5810_c;

	@Column(name = "d5225_26_c", length = 30)
	private String d5225_26_c;

	@Column(name = "extractions1_fr", length = 30)
	private String extractions1fr;

	@Column(name = "extractions2_fr", length = 30)
	private String extractions2fr;

	@Column(name = "implants_c", length = 30)
	private String implantsC;

	@Column(name = "d1520_26_27", length = 30)
	private String d1520_26_27;

	@Column(name = "d1520_26_27_fr", length = 30)
	private String d1520_26_27_fr;

	@Column(name = "waiting_period", length = 30)
	private String waitingPeriod;

	@Column(name = "wip", length = 30)
	private String wip;

	@Column(name = "ins_billing_c", length = 30)
	private String insBillingC;

	@Column(name = "benefit_period", length = 30)
	private String benefitPeriod;

	@Column(name = "waiting_period_drop", length = 30)
	private String waitingPeriodDrop;

	@Column(name = "d8070", length = 30)
	private String d8070;

	@Column(name = "d8080", length = 30)
	private String d8080;

	@Column(name = "d8090", length = 30)
	private String d8090;

	@Column(name = "d8670", length = 30)
	private String d8670;

	@Column(name = "d8680", length = 30)
	private String d8680;

	@Column(name = "d8690", length = 30)
	private String d8690;

	@Column(name = "d8070_fr", length = 30)
	private String d8070fr;

	@Column(name = "d8080_fr", length = 30)
	private String d8080fr;

	@Column(name = "d8090_fr", length = 30)
	private String d8090fr;

	@Column(name = "d8670_fr", length = 30)
	private String d8670fr;

	@Column(name = "d8680_fr", length = 30)
	private String d8680fr;

	@Column(name = "d8690_fr", length = 30)
	private String d8690fr;
	
	@Column(name = "apptype", length = 40)
	private String apptype;
	
	@Column(name = "sec_provider_name", length = 100)
	private String secProviderName;
	
	@Column(name = "sec_prov_network", length = 10)
	private String secProvNetwork;
	
	@Column(name = "yes_no_assign_to_office", length = 5)
	private String yesNoAssignToffice;
	
	
	@Column(name = "d1120", length = 20)
	private String d1120;
	
	@Column(name = "d1110", length = 20)
	private String d1110;
	
	@Column(name = "policy18", length = 20)
	private String policy18;//interchange with D0120 to work on both forms old and new
	
	@Column(name = "d7953_extraction", length = 50)
	private String d7953Extraction;
	
	@Column(name = "d8660", length = 15)
	private String d8660;
	
	@Column(name = "d8660_fr", length = 15)
	private String d8660fr;
	
	@Column(name = "d8210", length = 15)
	private String d8210;
	
	@Column(name = "d8210_fr", length = 15)
	private String d8210fr;
	
	@Column(name = "d8220", length = 15)
	private String d8220;
	
	@Column(name = "d8220_fr", length = 15)
	private String d8220fr;
	
	@Column(name = "d8020", length = 15)
	private String d8020;
	
	@Column(name = "d8020_fr", length = 15)
	private String d8020fr;
	
	@Column(name = "d8692", length = 15)
	private String d8692;
	
	@Column(name = "d8692_fr", length = 15)
	private String d8692fr;
	
	@Column(name = "implantsC_percentage", length = 15)
	private String implantsCPercentage;
	
	@Column(name = "does_exam_share_freq", length = 15)
	private String doesExamShareFreq;
	
	@Column(name = "d5110_20_percentage", length = 15)
	private String d511020Percentage;
	
	@Column(name = "d5130_40_percentage", length = 15)
	private String d513040Percentage;
	
	@Column(name = "d5810_c_percentage", length = 15)
	private String d5810CPercentage;
	
	@Column(name = "d9310", length = 15)
	private String d9310;
	
	@Column(name = "d9310_fr", length = 15)
	private String d9310fr;
	
	@Column(name = "d6011", length = 15)
	private String d6011;
	
	@Column(name = "d6011_fr", length = 15)
	private String d6011fr;
	
	@Column(name = "d5862", length = 15)
	private String d5862;
	
	@Column(name = "d5862_fr", length = 15)
	private String d5862fr;
	
	@Column(name = "d7311_select", length = 15)
	private String d7311Select;
	
	@Column(name = "d5213142625", length = 15)
	private String d5213142625;
	
	@Column(name = "d5213142625_fr", length = 15)
	private String d5213142625fr;
	
	@Column(name = "d2954", length = 15)
	private String d2954;
	
	@Column(name = "d2954_fr", length = 15)
	private String d2954fr;
	
	@Column(name = "policy21")
	private String policy21;
	
	@Column(name = "share_fr2",length = 15)
	private String shareFr2;

	public String getD2954() {
		return d2954;
	}

	public void setD2954(String d2954) {
		this.d2954 = d2954;
	}

	public String getD2954fr() {
		return d2954fr;
	}

	public void setD2954fr(String d2954fr) {
		this.d2954fr = d2954fr;
	}

	public String getD5213142625() {
		return d5213142625;
	}

	public void setD5213142625(String d5213142625) {
		this.d5213142625 = d5213142625;
	}

	public String getD5213142625fr() {
		return d5213142625fr;
	}

	public void setD5213142625fr(String d5213142625fr) {
		this.d5213142625fr = d5213142625fr;
	}

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

	public String getSmAgeLimit() {
		return smAgeLimit;
	}

	public void setSmAgeLimit(String smAgeLimit) {
		this.smAgeLimit = smAgeLimit;
	}

	public String getPerioD4921() {
		return perioD4921;
	}

	public void setPerioD4921(String perioD4921) {
		this.perioD4921 = perioD4921;
	}

	public String getD4921Frequency() {
		return d4921Frequency;
	}

	public void setD4921Frequency(String d4921Frequency) {
		this.d4921Frequency = d4921Frequency;
	}

	public String getPerioD4266() {
		return perioD4266;
	}

	public void setPerioD4266(String perioD4266) {
		this.perioD4266 = perioD4266;
	}

	public String getD4266Frequency() {
		return d4266Frequency;
	}

	public void setD4266Frequency(String d4266Frequency) {
		this.d4266Frequency = d4266Frequency;
	}

	public String getPerioD9910() {
		return perioD9910;
	}

	public void setPerioD9910(String perioD9910) {
		this.perioD9910 = perioD9910;
	}

	public String getD9910Frequency() {
		return d9910Frequency;
	}

	public void setD9910Frequency(String d9910Frequency) {
		this.d9910Frequency = d9910Frequency;
	}

	public String getOonbenfits() {
		return oonbenfits;
	}

	public void setOonbenfits(String oonbenfits) {
		this.oonbenfits = oonbenfits;
	}

	public String getD9630() {
		return d9630;
	}

	public void setD9630(String d9630) {
		this.d9630 = d9630;
	}

	public String getD9630fr() {
		return d9630fr;
	}

	public void setD9630fr(String d9630fr) {
		this.d9630fr = d9630fr;
	}

	public String getD0431() {
		return d0431;
	}

	public void setD0431(String d0431) {
		this.d0431 = d0431;
	}

	public String getD0431fr() {
		return d0431fr;
	}

	public void setD0431fr(String d0431fr) {
		this.d0431fr = d0431fr;
	}

	public String getD4999() {
		return d4999;
	}

	public void setD4999(String d4999) {
		this.d4999 = d4999;
	}

	public String getD4999fr() {
		return d4999fr;
	}

	public void setD4999fr(String d4999fr) {
		this.d4999fr = d4999fr;
	}

	public String getD2962() {
		return d2962;
	}

	public void setD2962(String d2962) {
		this.d2962 = d2962;
	}

	public String getD2962fr() {
		return d2962fr;
	}

	public void setD2962fr(String d2962fr) {
		this.d2962fr = d2962fr;
	}

	public String getMistothclause() {
		return mistothclause;
	}

	public void setMistothclause(String mistothclause) {
		this.mistothclause = mistothclause;
	}

	public String getD0150() {
		return d0150;
	}

	public void setD0150(String d0150) {
		this.d0150 = d0150;
	}

	public String getD2750() {
		return d2750;
	}

	public void setD2750(String d2750) {
		this.d2750 = d2750;
	}

	public String getD2750fr() {
		return d2750fr;
	}

	public void setD2750fr(String d2750fr) {
		this.d2750fr = d2750fr;
	}

	public String getD0220() {
		return d0220;
	}

	public void setD0220(String d0220) {
		this.d0220 = d0220;
	}

	public String getD0220Freq() {
		return d0220Freq;
	}

	public void setD0220Freq(String d0220Freq) {
		this.d0220Freq = d0220Freq;
	}

	public String getD0230() {
		return d0230;
	}

	public void setD0230(String d0230) {
		this.d0230 = d0230;
	}

	public String getBwx() {
		return bwx;
	}

	public void setBwx(String bwx) {
		this.bwx = bwx;
	}

	public String getD0210() {
		return d0210;
	}

	public void setD0210(String d0210) {
		this.d0210 = d0210;
	}

	public String getD0210Freq() {
		return d0210Freq;
	}

	public void setD0210Freq(String d0210Freq) {
		this.d0210Freq = d0210Freq;
	}

	public String getD0350Freq() {
		return d0350Freq;
	}

	public void setD0350Freq(String d0350Freq) {
		this.d0350Freq = d0350Freq;
	}

	public String getBwxFreq() {
		return bwxFreq;
	}

	public void setBwxFreq(String bwxFreq) {
		this.bwxFreq = bwxFreq;
	}

	public String getD2931() {
		return d2931;
	}

	public void setD2931(String d2931) {
		this.d2931 = d2931;
	}

	public String getD1206() {
		return d1206;
	}

	public void setD1206(String d1206) {
		this.d1206 = d1206;
	}

	public String getD1208() {
		return d1208;
	}

	public void setD1208(String d1208) {
		this.d1208 = d1208;
	}

	public String getbWhichCode() {
		return bWhichCode;
	}

	public void setbWhichCode(String bWhichCode) {
		this.bWhichCode = bWhichCode;
	}

	public String getD5110_20() {
		return d5110_20;
	}

	public void setD5110_20(String d5110_20) {
		this.d5110_20 = d5110_20;
	}

	public String getD1330Freq() {
		return d1330Freq;
	}

	public void setD1330Freq(String d1330Freq) {
		this.d1330Freq = d1330Freq;
	}

	public String getD5111_12_13_14() {
		return d5111_12_13_14;
	}

	public void setD5111_12_13_14(String d5111_12_13_14) {
		this.d5111_12_13_14 = d5111_12_13_14;
	}

	public String getD5130_40() {
		return d5130_40;
	}

	public void setD5130_40(String d5130_40) {
		this.d5130_40 = d5130_40;
	}

	public String getD5810_c() {
		return d5810_c;
	}

	public void setD5810_c(String d5810_c) {
		this.d5810_c = d5810_c;
	}

	public String getD5225_26_c() {
		return d5225_26_c;
	}

	public void setD5225_26_c(String d5225_26_c) {
		this.d5225_26_c = d5225_26_c;
	}

	public String getExtractions1fr() {
		return extractions1fr;
	}

	public void setExtractions1fr(String extractions1fr) {
		this.extractions1fr = extractions1fr;
	}

	public String getExtractions2fr() {
		return extractions2fr;
	}

	public void setExtractions2fr(String extractions2fr) {
		this.extractions2fr = extractions2fr;
	}

	public String getImplantsC() {
		return implantsC;
	}

	public void setImplantsC(String implantsC) {
		this.implantsC = implantsC;
	}

	public String getD1520_26_27() {
		return d1520_26_27;
	}

	public void setD1520_26_27(String d1520_26_27) {
		this.d1520_26_27 = d1520_26_27;
	}

	public String getD1520_26_27_fr() {
		return d1520_26_27_fr;
	}

	public void setD1520_26_27_fr(String d1520_26_27_fr) {
		this.d1520_26_27_fr = d1520_26_27_fr;
	}

	public String getWaitingPeriod() {
		return waitingPeriod;
	}

	public void setWaitingPeriod(String waitingPeriod) {
		this.waitingPeriod = waitingPeriod;
	}

	public String getWip() {
		return wip;
	}

	public void setWip(String wip) {
		this.wip = wip;
	}

	public String getInsBillingC() {
		return insBillingC;
	}

	public void setInsBillingC(String insBillingC) {
		this.insBillingC = insBillingC;
	}

	public String getBenefitPeriod() {
		return benefitPeriod;
	}

	public void setBenefitPeriod(String benefitPeriod) {
		this.benefitPeriod = benefitPeriod;
	}

	public String getWaitingPeriodDrop() {
		return waitingPeriodDrop;
	}

	public void setWaitingPeriodDrop(String waitingPeriodDrop) {
		this.waitingPeriodDrop = waitingPeriodDrop;
	}

	public String getD8070() {
		return d8070;
	}

	public void setD8070(String d8070) {
		this.d8070 = d8070;
	}

	public String getD8080() {
		return d8080;
	}

	public void setD8080(String d8080) {
		this.d8080 = d8080;
	}

	public String getD8090() {
		return d8090;
	}

	public void setD8090(String d8090) {
		this.d8090 = d8090;
	}

	public String getD8670() {
		return d8670;
	}

	public void setD8670(String d8670) {
		this.d8670 = d8670;
	}

	public String getD8680() {
		return d8680;
	}

	public void setD8680(String d8680) {
		this.d8680 = d8680;
	}

	public String getD8690() {
		return d8690;
	}

	public void setD8690(String d8690) {
		this.d8690 = d8690;
	}

	public String getD8070fr() {
		return d8070fr;
	}

	public void setD8070fr(String d8070fr) {
		this.d8070fr = d8070fr;
	}

	public String getD8080fr() {
		return d8080fr;
	}

	public void setD8080fr(String d8080fr) {
		this.d8080fr = d8080fr;
	}

	public String getD8090fr() {
		return d8090fr;
	}

	public void setD8090fr(String d8090fr) {
		this.d8090fr = d8090fr;
	}

	public String getD8670fr() {
		return d8670fr;
	}

	public void setD8670fr(String d8670fr) {
		this.d8670fr = d8670fr;
	}

	public String getD8680fr() {
		return d8680fr;
	}

	public void setD8680fr(String d8680fr) {
		this.d8680fr = d8680fr;
	}

	public String getD8690fr() {
		return d8690fr;
	}

	public void setD8690fr(String d8690fr) {
		this.d8690fr = d8690fr;
	}


	public String getD0145() {
		return d0145;
	}

	public void setD0145(String d0145) {
		this.d0145 = d0145;
	}

	public String getApptype() {
		return apptype;
	}

	public void setApptype(String apptype) {
		this.apptype = apptype;
	}

	public String getSecProviderName() {
		return secProviderName;
	}

	public void setSecProviderName(String secProviderName) {
		this.secProviderName = secProviderName;
	}

	public String getSecProvNetwork() {
		return secProvNetwork;
	}

	public void setSecProvNetwork(String secProvNetwork) {
		this.secProvNetwork = secProvNetwork;
	}

	public String getYesNoAssignToffice() {
		return yesNoAssignToffice;
	}

	public void setYesNoAssignToffice(String yesNoAssignToffice) {
		this.yesNoAssignToffice = yesNoAssignToffice;
	}

	public String getD1120() {
		return d1120;
	}

	public void setD1120(String d1120) {
		this.d1120 = d1120;
	}

	public String getD1110() {
		return d1110;
	}

	public void setD1110(String d1110) {
		this.d1110 = d1110;
	}

	public String getPolicy18() {
		return policy18;
	}

	public void setPolicy18(String policy18) {
		this.policy18 = policy18;
	}

	public String getD7953Extraction() {
		return d7953Extraction;
	}

	public void setD7953Extraction(String d7953Extraction) {
		this.d7953Extraction = d7953Extraction;
	}

	public String getD8660() {
		return d8660;
	}

	public void setD8660(String d8660) {
		this.d8660 = d8660;
	}

	public String getD8660fr() {
		return d8660fr;
	}

	public void setD8660fr(String d8660fr) {
		this.d8660fr = d8660fr;
	}

	public String getD8210() {
		return d8210;
	}

	public void setD8210(String d8210) {
		this.d8210 = d8210;
	}

	public String getD8210fr() {
		return d8210fr;
	}

	public void setD8210fr(String d8210fr) {
		this.d8210fr = d8210fr;
	}

	public String getD8220() {
		return d8220;
	}

	public void setD8220(String d8220) {
		this.d8220 = d8220;
	}

	public String getD8220fr() {
		return d8220fr;
	}

	public void setD8220fr(String d8220fr) {
		this.d8220fr = d8220fr;
	}

	public String getD8020() {
		return d8020;
	}

	public void setD8020(String d8020) {
		this.d8020 = d8020;
	}

	public String getD8020fr() {
		return d8020fr;
	}

	public void setD8020fr(String d8020fr) {
		this.d8020fr = d8020fr;
	}

	public String getD8692() {
		return d8692;
	}

	public void setD8692(String d8692) {
		this.d8692 = d8692;
	}

	public String getD8692fr() {
		return d8692fr;
	}

	public void setD8692fr(String d8692fr) {
		this.d8692fr = d8692fr;
	}

	public String getImplantsCPercentage() {
		return implantsCPercentage;
	}

	public void setImplantsCPercentage(String implantsCPercentage) {
		this.implantsCPercentage = implantsCPercentage;
	}

	public String getDoesExamShareFreq() {
		return doesExamShareFreq;
	}

	public void setDoesExamShareFreq(String doesExamShareFreq) {
		this.doesExamShareFreq = doesExamShareFreq;
	}
    

	public String getD511020Percentage() {
		return d511020Percentage;
	}

	public void setD511020Percentage(String d511020Percentage) {
		this.d511020Percentage = d511020Percentage;
	}

	public String getD513040Percentage() {
		return d513040Percentage;
	}

	public void setD513040Percentage(String d513040Percentage) {
		this.d513040Percentage = d513040Percentage;
	}

	public String getD5810CPercentage() {
		return d5810CPercentage;
	}

	public void setD5810CPercentage(String d5810cPercentage) {
		d5810CPercentage = d5810cPercentage;
	}

	public String getD9310() {
		return d9310;
	}

	public void setD9310(String d9310) {
		this.d9310 = d9310;
	}

	public String getD9310fr() {
		return d9310fr;
	}

	public void setD9310fr(String d9310fr) {
		this.d9310fr = d9310fr;
	}

	public String getD6011() {
		return d6011;
	}

	public void setD6011(String d6011) {
		this.d6011 = d6011;
	}

	public String getD6011fr() {
		return d6011fr;
	}

	public void setD6011fr(String d6011fr) {
		this.d6011fr = d6011fr;
	}

	public String getD5862() {
		return d5862;
	}

	public void setD5862(String d5862) {
		this.d5862 = d5862;
	}

	public String getD5862fr() {
		return d5862fr;
	}

	public void setD5862fr(String d5862fr) {
		this.d5862fr = d5862fr;
	}

	public String getD7311Select() {
		return d7311Select;
	}

	public void setD7311Select(String d7311Select) {
		this.d7311Select = d7311Select;
	}

	public String getPolicy21() {
		return policy21;
	}

	public void setPolicy21(String policy21) {
		this.policy21 = policy21;
	}

	public String getShareFr2() {
		if (shareFr2==null) shareFr2="";
		return shareFr2;
	}

	public void setShareFr2(String shareFr2) {
		if (shareFr2==null) shareFr2="";
		this.shareFr2 = shareFr2;
	}

	
	
	
}

