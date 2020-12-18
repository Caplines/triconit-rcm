package com.tricon.ruleengine.dto;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class CaplineIVFFormDto {

	private String createdDate;
	private String status;
	
	//password extra layer check
	private int patDid;

	private int pidDB;
	
	//private String insuranceType;
	
	
	public int getPatDid() {
		return patDid;
	}

	public void setPatDid(int patDid) {
		this.patDid = patDid;
	}

	public int getPidDB() {
		return pidDB;
	}

	public void setPidDB(int pidDB) {
		this.pidDB = pidDB;
	}

	private String passwordRE;
	
	//id (unquie)
	private BigInteger id;
    
	//office Name
	private String basicInfo1;

	//Patient Name
	private String basicInfo2;
	
    //insurance name
	private String basicInfo3;
	
	//tax id
	private String basicInfo4;
	
	//policy holder Name
	private String basicInfo5;
	
	//patient DOB
	private String basicInfo6;
	
	//Insurance Contact
	private String basicInfo7;
	
    //Csr Name
	private String basicInfo8;
	
	//Policy holder Date of birth
	private String basicInfo9;
	
	//employer name
	private String basicInfo10;
	
	//Continued Recall (Continuing Treatment,New Patient,Recall)
	private String basicInfo11;
	
	//RC Ref No.
	private String basicInfo12;
	
	//Member SSN
	private  String basicInfo13;
	
	//Group # 
	private String basicInfo14;
	
	//COB Status
	private String basicInfo15;
	
	//Patient ID
	private String basicInfo21;
	
	//Member Id
	private String basicInfo16;
	
	//Appointment Date
	private String basicInfo17;
	
	//Payer ID
	private String basicInfo18;
	
	//Provider last Name
	private String basicInfo19;
	
	//Insurance Address
	private String basicInfo20;
	
	//Plan Type
	private String policy1;
	
	//D120
	private String policy18;
	
	//D2319
	private String policy19;
	
	//Find FS
	private String policy20;
	
	//CRA Req. (Yes/No)
	private String policy17;
	
	//termed Date
	private String policy2;
	
	//Network
	private String policy3;
	
	//Fee schdule
	private String policy4;
	
	//Effective Date
	private String policy5;
	
	//Cal.Yr/Fiscal Yr/Plan Yr. :(CY/FY/PY)
    private String policy6;
	
	//Annual Max
    private String policy7;
    
    
    //Annual Mam remain
    private String policy8;
    
    
    //Indiv Ded.
    private String policy9;
    
    //Indiv Ded Remain
    private String policy10;
    
    //Dependents Covered to age:
    private String policy11;
    
    //Pre-D Mandatory: Yes/No
    private String policy12;
    
    //Non Duplicate (Yes/No)
    private String policy13;
    
    //Full Time Student Status Required? (Yes/No)
    private String policy14;
    
    
    //Assignment of Benefits Accepted?
    private String policy15;
    
    //Coverage Book
    private String policy16;
    
    //Basic(%)
    private String percentages1;
    
    //Subject to ded (Yes/No)
    private String percentages2;
    
    //Major(%)
    private String percentages3;
    
    //Subject to ded (Yes/No)
    private String percentages4;
    
    //Endo %
    private String percentages5;
    
    //Subject to ded (Yes/No)
    private String percentages6;
    
    //Perio Surgery(%)
    private String percentages7;
    
    //Subject to ded (Yes/No)
    private String percentages8;
    
    //Preventative(%)
    private String percentages9;
    
    //Diagnostics(%)
    private String percentages10;
    
    
    //PA X-rays(%)
    private String percentages11;
    
    
   //FMX(%)
    private String percentages16;
    
    //Claims Timely Fillings
    private String percentages12;
    
    //Prosthetics
    
    //Missing tooth clause (Yes/No)
    private String prosthetics1;
    
    //Replacement Clause (Yes/No)
    private String prosthetics2;
    
    //Paid Prep/Seat Date (Prep/Seat/Either Or)  -- Crowns_D2750_D2740_PaysPrepSeatDate
    private String prosthetics3;
    
    //Night Gaurds (D9940) Frequency   -- NightGuards_D9940_FL
    private String prosthetics4;
    
    //Waiting Periods (In Months)
    
    //Basic
    private String waitingPeriod1;
    
    //Major
    private String waitingPeriod2;
    
    //SSC
    
    //D2930:
    private String ssc1;
    
    //D2931:
    private String ssc2;
    
    //Exams(Frequency):
      
    //D0120
    private String exams1;
    
    //D0140
    private String exams2;
    
    //D0145
    private String exams3;
    
    //D0150
    private String exams4;
    
    //X-Rays(Frequency)
    
    //BWX(0274)     --  XRaysBWS_FL
    private String xrays1;

    //PA(0220)     --  XRaysPA_D0220_FL
    private String xrays2;
    
    //D0230       -- XRaysPA_D0230_FL
    private String xrays3;
    
    //FMX(0210)         -- XRaysFMX_FL
    private String xrays4;
    
    //Bundling (Yes/No/NA)
    private String xrays5;
    
    //Fluroide(D1208)
    
    //Frequency
    private String fluroide1;
    
    // Age Limit
    private String fluroide2;
    
    //Varnish(D1206)
    
    //Frequency
    private String fluroide3;
    
    
    //Age Limit 
    private String fluroide4;

    // Sealants(D1351)  -- 
    private String sealantsD;
    
    //Frequency
    private  String sealants1;
    
    
    //Age Limit 
    private String sealants2;
    
    //Coverage:
        
    //Primary Molars
    private String sealants3;
    
    //Pre-Molars
    private String sealants4;
    
    
    //Perm-Molars
    private String sealants5;
    
    //Prophy (Frequency):   -- ProphyD1110_FL
    private String prophy1;
    
    //D1120:          --- ProphyD1120_FL
    private  String prophy2;    
    
    //1120/1110 Roll age
    private  String rollage;
    
    //Perio    
    //SRP(D4341)%
    
    private  String perio1;
    
    //Frequency
    private String perio2;
    
    //Quads Per Day (2/4)
    private String perio3;
    
    //Days b/w Quad
    private String perio4;
    
    //Perio Mnt.
    //(D4910) %:
    private String perioMnt1;
    
    //Frequency
    private String perioMnt2;
     
    //Alt. with Prophy(D1110)  -- PerioMaintenance_D4910_Alt w/prophy (D0110)
    private String perioMnt3;

    //Fillings(Bundle) (Yes/No/NA)
    private String fillings;
    
    
    //FMD(D4355)%             -- FMD_D4355_%
    private String perioMnt4;
    
    //Frequency              -- FMD_D4355_FL
    private String perioMnt5;
    
    //Gingivitis(D4346)%
    private String perioMnt6;
    
    //Frequency
    private String perioMnt7;
    
    //Sedations
    
    //Nitrous (D9230)
    private String sedations1;
    
    //IV Sedation (D9243)
    private String sedations2;
    
    //IV Sedation (D9248)
    private String sedations3;
    
    //Extractions(%)
    
    //Minor(D7111,D7140)       -- Extractions_Minor_%
    private  String extractions1;
    
    //Major (D7210,D7220, D7230,D7240)  -- Extractions_Major_%
    private String extractions2;
    
    //Oral Surgery
    
    //Crown Lengthening(D4249)%   -- CrownLength_D4249_%
    private String oral1; 
    
    //Frequency                  -- CrownLength_D4249_FL
    private String oral2;
    
    //Alveolplasty
    
    //1-3 Teeth/Qd(D7311): Covered w. Ext (Yes/No) -- Alveo_D7311_CoveredWithEXT
    private String oral3;
    
    //Frequency                                    -- Alveo_D7311_FL
    private String oral4;
    
    //4 Teeth/Qd (D7310): Covered w. Ext Ext (Yes/No) --Alveo_D7310_CoveredWithEXT
    private String oral5;
    
    //Frequency                                    -- Alveo_D7310_FL
    private String oral6;
    
    //Dentures (Frequency)
    
    //Complete (D5110/D5120)  --CompleteDentures_D5110_D5120_FL
    private String dentures1;  
    
    //Immediate (D5130/D5140   --ImmediateDentures_D5130_D5140_FL 
    private String dentures2;
    
    //Partial (D5211/D5212/D5213/D5214/D5225/D5226)  --PartialDentures_D5213_D5214_FL
    private String dentures3;
    
    //Interim Partial (D5820)  -- interimPartialDentures_D5214_FL
    private String dentures4;
    
    //Bone Graft (D7953) Covered w. Ext (Yes/No) --BoneGrafts_D7953_CoveredWithEXT
    private String dentures5;
    
    //Frequency                                -- BoneGrafts_D7953_FL
    private String dentures6;
    
    //Implants Coverage (%)
    
    //Implants D6010          --ImplantCoverage_D6010_% 
    private String implants1;
 
    //Implants D6057          --ImplantCoverage_D6057_%
    private String implants2;
    
    //Implants D6190            --ImplantCoverage_D6190_%
    private String implants3;
    
    //Implants Supported Porc./Ceramic (D6065)  --ImplantSupportedPorcCeramic_D6065_%
    private String implants4;
    
    
    //Posterior
    //Composites (D2391) %             -- PostComposites_D2391_%
    private String posterior1;
    
    //Frequency                         --PostComposites_D2391_FL
    private String posterior2;
    
    //Downgraded to Amalgam (D2140) (Yes/No/Pre-D)  -PosteriorComposites_D2391_Downgrade
    private String posterior3;
    
    
    //Crowns (D2740 & D2750)%      ---Crowns_D2750_D2740_%
    private String posterior4;
    
    //Crowns(D2740 & D2750) Frequency  --Crowns_D2750_D2740_FL
    private String posterior5;
    
    //Are Crowns Downgraded (D2791) (Yes/No/Pre-D) --Crowns_D2750_D2740_Downgrade
    private String posterior6;
    
    //Night Gaurds (D9940) %             --NightGuards_D9940_%
    private String posterior7;
    
    //Consult (D9310)%             --D9310_%
    private String posterior8;
    
    //Frequency                      --D9310_FL
    private String posterior9;
    
    //Build-up (D2950) %              --BuildUps_D2950_Covered
    private String posterior10;
    
    //Frequency                      --BuildUps_D2950_FL
    private String posterior11;
    
    //Same day as Crown (Yes/No)         --BuildUps_D2950_SameDayCrown
    private String posterior12;
    
    //Ortho(%)
    //D8080, D8090, D8070              --Ortho_%
    private String ortho1;
    
    //Ortho Max                       --Ortho_Max
    private String ortho2;
    
    //Age Limit                         --Ortho_AgeLimit
    private String ortho3;
    	
    //Subject to Deductible (Yes/no)      --Ortho_SubjectDeductible
    private String ortho4;
    
    //NOT in Google Sheet
    private String den5225;
    private String denf5225;
    private String den5226;
    private String denf5226;
    private String bridges1;
    private String bridges2;
    private String cdowngrade;
    
    private String implants5;
    private String implants6;
    private String implants7;
    private String implants8;
    private String ortho5;
    private String waitingPeriod3;
    //private String posterior16;
    private String posterior17;
    
    private String percentages13;//
    private String percentages14;//
    private String percentages15;//
    private String posterior18;//
    
    private String posterior19;//
    private String posterior20;//
    
    private String fill1;//
    private String extr1;//
    private String crn1;//
	private String waitingPeriod4;// waitingPeriod4 //MB T25
	private String shareFr;// shareFr //MC T26

	private String pedo1;// pedo1
	private String pedo2;// pedo2
    
	private String pano1;// pano1
	private String pano2;// pano2
	private String d4381;// d4381
	
	
	private String ckD0120;//ckD0120
	private String ckD0140;//ckD0140
	private String ckD0145;//ckD0145
	private String ckD0150;//ckD0150
	private String ckD0160;//ckD0160
	private String ckD210;//ckD210
	private String ckD220;//ckD220
	private String ckD230;//ckD230
	private String ckD330;//ckD330
	private String ckD274;//ckD274
	
	
	private String d0160Freq;//d0160Freq
	private String d2391Freq;//d2391Freq
	private String d0330Freq;//d0330Freq
	private String d4381Freq;//d4381Freq

    private String d3330;//d3330
	private String d3330Freq;// d3330Freq 
    private String freqD2934;//freqD2934
	
	
    private String npi;
    private String licence;
    private String radio3;
    private String radio4;
    private String radio5;
    private String radio1;
    private String radio2;
    private String corrdOfBenefits;
    private String whatAmountD7210;
    private String allowAmountD7240;
    private String ivSedation;

    private String d7210;
    private String d7220;
    private String d7230;
    private String d7240;
    private String d7250;
    private String d7310;
    private String d7311;
    private String d7320;
    private String d7321;
    private String d7473;

    private String d9239;
    private String d4263;
    private String d4264;
    private String d6104;
    private String d7953;
    private String d3310;
    private String d3320;
    private String d33300;
    private String d3346;
    private String d3347;
    private String d3348;
    private String d6058;
    private String d7951;
    private String d4266;
    private String d4267;
    private String d4273;
    private String d7251;
    
	
	private Integer ivFormTypeId;
	
    //END
    
    public String getFill1() {
		return fill1;
	}

	public void setFill1(String fill1) {
		this.fill1 = fill1;
	}

	public String getExtr1() {
		return extr1;
	}

	public void setExtr1(String extr1) {
		this.extr1 = extr1;
	}

	public String getCrn1() {
		return crn1;
	}

	public void setCrn1(String crn1) {
		this.crn1 = crn1;
	}

	private String comments;
    
    private int commentsRows;
    
    private String benefits;
    
    @XmlElementWrapper
    @XmlElement(name="his") 
    private List<String> history;
    
    private String date;
    
    @XmlElementWrapper
    @XmlElement(name="hisall") 
    private List<ToothHistoryDto> hdto;

    @XmlElementWrapper
    @XmlElement(name="hisall1") 
    private List<ToothHistoryDto> hdto1;

    @XmlElementWrapper
    @XmlElement(name="hisall2") 
    private List<ToothHistoryDto> hdto2;

    @XmlElementWrapper
    @XmlElement(name="hisall3") 
    private List<ToothHistoryDto> hdto3;

    
	public List<ToothHistoryDto> getHdto1() {
		return hdto1;
	}

	public void setHdto1(List<ToothHistoryDto> hdto1) {
		this.hdto1 = hdto1;
	}

	public List<ToothHistoryDto> getHdto2() {
		return hdto2;
	}

	public void setHdto2(List<ToothHistoryDto> hdto2) {
		this.hdto2 = hdto2;
	}

	public List<ToothHistoryDto> getHdto3() {
		return hdto3;
	}

	public void setHdto3(List<ToothHistoryDto> hdto3) {
		this.hdto3 = hdto3;
	}

	public String getComments() {
		
		if (comments==null) comments="";
		
		this.setCommentsRows(comments.length());
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getBenefits() {
		return benefits;
	}

	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}

	public String getBasicInfo1() {
		return basicInfo1;
	}

	public void setBasicInfo1(String basicInfo1) {
		this.basicInfo1 = basicInfo1;
	}

	public String getBasicInfo2() {
		return basicInfo2;
	}

	public void setBasicInfo2(String basicInfo2) {
		this.basicInfo2 = basicInfo2;
	}

	public String getBasicInfo3() {
		return basicInfo3;
	}

	public void setBasicInfo3(String basicInfo3) {
		this.basicInfo3 = basicInfo3;
	}

	public String getBasicInfo4() {
		return basicInfo4;
	}

	public void setBasicInfo4(String basicInfo4) {
		this.basicInfo4 = basicInfo4;
	}

	public String getBasicInfo5() {
		return basicInfo5;
	}

	public void setBasicInfo5(String basicInfo5) {
		this.basicInfo5 = basicInfo5;
	}

	public String getBasicInfo6() {
		return basicInfo6;
	}

	public void setBasicInfo6(String basicInfo6) {
		this.basicInfo6 = basicInfo6;
	}

	public String getBasicInfo7() {
		return basicInfo7;
	}

	public void setBasicInfo7(String basicInfo7) {
		this.basicInfo7 = basicInfo7;
	}

	public String getBasicInfo8() {
		return basicInfo8;
	}

	public void setBasicInfo8(String basicInfo8) {
		this.basicInfo8 = basicInfo8;
	}

	public String getBasicInfo9() {
		return basicInfo9;
	}

	public void setBasicInfo9(String basicInfo9) {
		this.basicInfo9 = basicInfo9;
	}

	public String getBasicInfo10() {
		return basicInfo10;
	}

	public void setBasicInfo10(String basicInfo10) {
		this.basicInfo10 = basicInfo10;
	}

	public String getBasicInfo11() {
		return basicInfo11;
	}

	public void setBasicInfo11(String basicInfo11) {
		this.basicInfo11 = basicInfo11;
	}

	public String getBasicInfo12() {
		return basicInfo12;
	}

	public void setBasicInfo12(String basicInfo12) {
		this.basicInfo12 = basicInfo12;
	}

	public String getBasicInfo13() {
		return basicInfo13;
	}

	public void setBasicInfo13(String basicInfo13) {
		this.basicInfo13 = basicInfo13;
	}

	public String getBasicInfo14() {
		return basicInfo14;
	}

	public void setBasicInfo14(String basicInfo14) {
		this.basicInfo14 = basicInfo14;
	}

	public String getBasicInfo15() {
		return basicInfo15;
	}

	public void setBasicInfo15(String basicInfo15) {
		this.basicInfo15 = basicInfo15;
	}

	public String getBasicInfo21() {
		return basicInfo21;
	}

	public void setBasicInfo21(String basicInfo21) {
		this.basicInfo21 = basicInfo21;
	}

	public String getBasicInfo16() {
		return basicInfo16;
	}

	public void setBasicInfo16(String basicInfo16) {
		this.basicInfo16 = basicInfo16;
	}

	public String getBasicInfo17() {
		return basicInfo17;
	}

	public void setBasicInfo17(String basicInfo17) {
		this.basicInfo17 = basicInfo17;
	}

	public String getBasicInfo18() {
		return basicInfo18;
	}

	public void setBasicInfo18(String basicInfo18) {
		this.basicInfo18 = basicInfo18;
	}

	public String getBasicInfo19() {
		return basicInfo19;
	}

	public void setBasicInfo19(String basicInfo19) {
		this.basicInfo19 = basicInfo19;
	}

	public String getBasicInfo20() {
		return basicInfo20;
	}

	public void setBasicInfo20(String basicInfo20) {
		this.basicInfo20 = basicInfo20;
	}

	public String getPolicy1() {
		return policy1;
	}

	public void setPolicy1(String policy1) {
		this.policy1 = policy1;
	}

	public String getPolicy18() {
		return policy18;
	}

	public void setPolicy18(String policy18) {
		this.policy18 = policy18;
	}

	public String getPolicy19() {
		return policy19;
	}

	public void setPolicy19(String policy19) {
		this.policy19 = policy19;
	}

	public String getPolicy20() {
		return policy20;
	}

	public void setPolicy20(String policy20) {
		this.policy20 = policy20;
	}

	public String getPolicy17() {
		return policy17;
	}

	public void setPolicy17(String policy17) {
		this.policy17 = policy17;
	}

	public String getPolicy2() {
		return policy2;
	}

	public void setPolicy2(String policy2) {
		this.policy2 = policy2;
	}

	public String getPolicy3() {
		return policy3;
	}

	public void setPolicy3(String policy3) {
		this.policy3 = policy3;
	}

	public String getPolicy4() {
		return policy4;
	}

	public void setPolicy4(String policy4) {
		this.policy4 = policy4;
	}

	public String getPolicy5() {
		return policy5;
	}

	public void setPolicy5(String policy5) {
		this.policy5 = policy5;
	}

	public String getPolicy6() {
		return policy6;
	}

	public void setPolicy6(String policy6) {
		this.policy6 = policy6;
	}

	public String getPolicy7() {
		return policy7;
	}

	public void setPolicy7(String policy7) {
		this.policy7 = policy7;
	}

	public String getPolicy8() {
		return policy8;
	}

	public void setPolicy8(String policy8) {
		this.policy8 = policy8;
	}

	public String getPolicy9() {
		return policy9;
	}

	public void setPolicy9(String policy9) {
		this.policy9 = policy9;
	}

	public String getPolicy10() {
		return policy10;
	}

	public void setPolicy10(String policy10) {
		this.policy10 = policy10;
	}

	public String getPolicy11() {
		return policy11;
	}

	public void setPolicy11(String policy11) {
		this.policy11 = policy11;
	}

	public String getPolicy12() {
		return policy12;
	}

	public void setPolicy12(String policy12) {
		this.policy12 = policy12;
	}

	public String getPolicy13() {
		return policy13;
	}

	public void setPolicy13(String policy13) {
		this.policy13 = policy13;
	}

	public String getPolicy14() {
		return policy14;
	}

	public void setPolicy14(String policy14) {
		this.policy14 = policy14;
	}

	public String getPolicy15() {
		return policy15;
	}

	public void setPolicy15(String policy15) {
		this.policy15 = policy15;
	}

	public String getPolicy16() {
		return policy16;
	}

	public void setPolicy16(String policy16) {
		this.policy16 = policy16;
	}

	public String getPercentages1() {
		return percentages1;
	}

	public void setPercentages1(String percentages1) {
		this.percentages1 = percentages1;
	}

	public String getPercentages2() {
		return percentages2;
	}

	public void setPercentages2(String percentages2) {
		this.percentages2 = percentages2;
	}

	public String getPercentages3() {
		return percentages3;
	}

	public void setPercentages3(String percentages3) {
		this.percentages3 = percentages3;
	}

	public String getPercentages4() {
		return percentages4;
	}

	public void setPercentages4(String percentages4) {
		this.percentages4 = percentages4;
	}

	public String getPercentages5() {
		return percentages5;
	}

	public void setPercentages5(String percentages5) {
		this.percentages5 = percentages5;
	}

	public String getPercentages6() {
		return percentages6;
	}

	public void setPercentages6(String percentages6) {
		this.percentages6 = percentages6;
	}

	public String getPercentages7() {
		return percentages7;
	}

	public void setPercentages7(String percentages7) {
		this.percentages7 = percentages7;
	}

	public String getPercentages8() {
		return percentages8;
	}

	public void setPercentages8(String percentages8) {
		this.percentages8 = percentages8;
	}

	public String getPercentages9() {
		return percentages9;
	}

	public void setPercentages9(String percentages9) {
		this.percentages9 = percentages9;
	}

	public String getPercentages10() {
		return percentages10;
	}

	public void setPercentages10(String percentages10) {
		this.percentages10 = percentages10;
	}

	public String getPercentages11() {
		return percentages11;
	}

	public void setPercentages11(String percentages11) {
		this.percentages11 = percentages11;
	}

	public String getPercentages12() {
		return percentages12;
	}

	public void setPercentages12(String percentages12) {
		this.percentages12 = percentages12;
	}

	public String getProsthetics1() {
		return prosthetics1;
	}

	public void setProsthetics1(String prosthetics1) {
		this.prosthetics1 = prosthetics1;
	}

	public String getProsthetics2() {
		return prosthetics2;
	}

	public void setProsthetics2(String prosthetics2) {
		this.prosthetics2 = prosthetics2;
	}

	public String getProsthetics3() {
		return prosthetics3;
	}

	public void setProsthetics3(String prosthetics3) {
		this.prosthetics3 = prosthetics3;
	}

	public String getProsthetics4() {
		return prosthetics4;
	}

	public void setProsthetics4(String prosthetics4) {
		this.prosthetics4 = prosthetics4;
	}

	public String getWaitingPeriod1() {
		return waitingPeriod1;
	}

	public void setWaitingPeriod1(String waitingPeriod1) {
		this.waitingPeriod1 = waitingPeriod1;
	}

	public String getWaitingPeriod2() {
		return waitingPeriod2;
	}

	public void setWaitingPeriod2(String waitingPeriod2) {
		this.waitingPeriod2 = waitingPeriod2;
	}

	public String getSsc1() {
		return ssc1;
	}

	public void setSsc1(String ssc1) {
		this.ssc1 = ssc1;
	}

	public String getSsc2() {
		return ssc2;
	}

	public void setSsc2(String ssc2) {
		this.ssc2 = ssc2;
	}

	public String getExams1() {
		return exams1;
	}

	public void setExams1(String exams1) {
		this.exams1 = exams1;
	}

	public String getExams2() {
		return exams2;
	}

	public void setExams2(String exams2) {
		this.exams2 = exams2;
	}

	public String getExams3() {
		return exams3;
	}

	public void setExams3(String exams3) {
		this.exams3 = exams3;
	}

	public String getExams4() {
		return exams4;
	}

	public void setExams4(String exams4) {
		this.exams4 = exams4;
	}

	public String getXrays1() {
		return xrays1;
	}

	public void setXrays1(String xrays1) {
		this.xrays1 = xrays1;
	}

	public String getXrays2() {
		return xrays2;
	}

	public void setXrays2(String xrays2) {
		this.xrays2 = xrays2;
	}

	public String getXrays3() {
		return xrays3;
	}

	public void setXrays3(String xrays3) {
		this.xrays3 = xrays3;
	}

	public String getXrays4() {
		return xrays4;
	}

	public void setXrays4(String xrays4) {
		this.xrays4 = xrays4;
	}

	public String getXrays5() {
		return xrays5;
	}

	public void setXrays5(String xrays5) {
		this.xrays5 = xrays5;
	}

	public String getFluroide1() {
		return fluroide1;
	}

	public void setFluroide1(String fluroide1) {
		this.fluroide1 = fluroide1;
	}

	public String getFluroide2() {
		return fluroide2;
	}

	public void setFluroide2(String fluroide2) {
		this.fluroide2 = fluroide2;
	}

	public String getFluroide3() {
		return fluroide3;
	}

	public void setFluroide3(String fluroide3) {
		this.fluroide3 = fluroide3;
	}

	public String getFluroide4() {
		return fluroide4;
	}

	public void setFluroide4(String fluroide4) {
		this.fluroide4 = fluroide4;
	}

	public String getSealantsD() {
		return sealantsD;
	}

	public void setSealantsD(String sealantsD) {
		this.sealantsD = sealantsD;
	}

	public String getSealants1() {
		return sealants1;
	}

	public void setSealants1(String sealants1) {
		this.sealants1 = sealants1;
	}

	public String getSealants2() {
		return sealants2;
	}

	public void setSealants2(String sealants2) {
		this.sealants2 = sealants2;
	}

	public String getSealants3() {
		return sealants3;
	}

	public void setSealants3(String sealants3) {
		this.sealants3 = sealants3;
	}

	public String getSealants4() {
		return sealants4;
	}

	public void setSealants4(String sealants4) {
		this.sealants4 = sealants4;
	}

	public String getSealants5() {
		return sealants5;
	}

	public void setSealants5(String sealants5) {
		this.sealants5 = sealants5;
	}

	public String getProphy1() {
		return prophy1;
	}

	public void setProphy1(String prophy1) {
		this.prophy1 = prophy1;
	}

	public String getProphy2() {
		return prophy2;
	}

	public void setProphy2(String prophy2) {
		this.prophy2 = prophy2;
	}

	public String getRollage() {
		return rollage;
	}

	public void setRollage(String rollage) {
		this.rollage = rollage;
	}

	public String getPerio1() {
		return perio1;
	}

	public void setPerio1(String perio1) {
		this.perio1 = perio1;
	}

	public String getPerio2() {
		return perio2;
	}

	public void setPerio2(String perio2) {
		this.perio2 = perio2;
	}

	public String getPerio3() {
		return perio3;
	}

	public void setPerio3(String perio3) {
		this.perio3 = perio3;
	}

	public String getPerio4() {
		return perio4;
	}

	public void setPerio4(String perio4) {
		this.perio4 = perio4;
	}

	public String getPerioMnt1() {
		return perioMnt1;
	}

	public void setPerioMnt1(String perioMnt1) {
		this.perioMnt1 = perioMnt1;
	}

	public String getPerioMnt2() {
		return perioMnt2;
	}

	public void setPerioMnt2(String perioMnt2) {
		this.perioMnt2 = perioMnt2;
	}

	public String getPerioMnt3() {
		return perioMnt3;
	}

	public void setPerioMnt3(String perioMnt3) {
		this.perioMnt3 = perioMnt3;
	}

	public String getFillings() {
		return fillings;
	}

	public void setFillings(String fillings) {
		this.fillings = fillings;
	}

	public String getPerioMnt4() {
		return perioMnt4;
	}

	public void setPerioMnt4(String perioMnt4) {
		this.perioMnt4 = perioMnt4;
	}

	public String getPerioMnt5() {
		return perioMnt5;
	}

	public void setPerioMnt5(String perioMnt5) {
		this.perioMnt5 = perioMnt5;
	}

	public String getPerioMnt6() {
		return perioMnt6;
	}

	public void setPerioMnt6(String perioMnt6) {
		this.perioMnt6 = perioMnt6;
	}

	public String getPerioMnt7() {
		return perioMnt7;
	}

	public void setPerioMnt7(String perioMnt7) {
		this.perioMnt7 = perioMnt7;
	}

	public String getSedations1() {
		return sedations1;
	}

	public void setSedations1(String sedations1) {
		this.sedations1 = sedations1;
	}

	public String getSedations2() {
		return sedations2;
	}

	public void setSedations2(String sedations2) {
		this.sedations2 = sedations2;
	}

	public String getSedations3() {
		return sedations3;
	}

	public void setSedations3(String sedations3) {
		this.sedations3 = sedations3;
	}

	public String getExtractions1() {
		return extractions1;
	}

	public void setExtractions1(String extractions1) {
		this.extractions1 = extractions1;
	}

	public String getExtractions2() {
		return extractions2;
	}

	public void setExtractions2(String extractions2) {
		this.extractions2 = extractions2;
	}

	public String getOral1() {
		return oral1;
	}

	public void setOral1(String oral1) {
		this.oral1 = oral1;
	}

	public String getOral2() {
		return oral2;
	}

	public void setOral2(String oral2) {
		this.oral2 = oral2;
	}

	public String getOral3() {
		return oral3;
	}

	public void setOral3(String oral3) {
		this.oral3 = oral3;
	}

	public String getOral4() {
		return oral4;
	}

	public void setOral4(String oral4) {
		this.oral4 = oral4;
	}

	public String getOral5() {
		return oral5;
	}

	public void setOral5(String oral5) {
		this.oral5 = oral5;
	}

	public String getOral6() {
		return oral6;
	}

	public void setOral6(String oral6) {
		this.oral6 = oral6;
	}

	public String getDentures1() {
		return dentures1;
	}

	public void setDentures1(String dentures1) {
		this.dentures1 = dentures1;
	}

	public String getDentures2() {
		return dentures2;
	}

	public void setDentures2(String dentures2) {
		this.dentures2 = dentures2;
	}

	public String getDentures3() {
		return dentures3;
	}

	public void setDentures3(String dentures3) {
		this.dentures3 = dentures3;
	}

	public String getDentures4() {
		return dentures4;
	}

	public void setDentures4(String dentures4) {
		this.dentures4 = dentures4;
	}

	public String getDentures5() {
		return dentures5;
	}

	public void setDentures5(String dentures5) {
		this.dentures5 = dentures5;
	}

	public String getDentures6() {
		return dentures6;
	}

	public void setDentures6(String dentures6) {
		this.dentures6 = dentures6;
	}

	public String getImplants1() {
		return implants1;
	}

	public void setImplants1(String implants1) {
		this.implants1 = implants1;
	}

	public String getImplants2() {
		return implants2;
	}

	public void setImplants2(String implants2) {
		this.implants2 = implants2;
	}

	public String getImplants3() {
		return implants3;
	}

	public void setImplants3(String implants3) {
		this.implants3 = implants3;
	}

	public String getImplants4() {
		return implants4;
	}

	public void setImplants4(String implants4) {
		this.implants4 = implants4;
	}

	public String getPosterior1() {
		return posterior1;
	}

	public void setPosterior1(String posterior1) {
		this.posterior1 = posterior1;
	}

	public String getPosterior2() {
		return posterior2;
	}

	public void setPosterior2(String posterior2) {
		this.posterior2 = posterior2;
	}

	public String getPosterior3() {
		return posterior3;
	}

	public void setPosterior3(String posterior3) {
		this.posterior3 = posterior3;
	}

	public String getPosterior4() {
		return posterior4;
	}

	public void setPosterior4(String posterior4) {
		this.posterior4 = posterior4;
	}

	public String getPosterior5() {
		return posterior5;
	}

	public void setPosterior5(String posterior5) {
		this.posterior5 = posterior5;
	}

	public String getPosterior6() {
		return posterior6;
	}

	public void setPosterior6(String posterior6) {
		this.posterior6 = posterior6;
	}

	public String getPosterior7() {
		return posterior7;
	}

	public void setPosterior7(String posterior7) {
		this.posterior7 = posterior7;
	}

	public String getPosterior8() {
		return posterior8;
	}

	public void setPosterior8(String posterior8) {
		this.posterior8 = posterior8;
	}

	public String getPosterior9() {
		return posterior9;
	}

	public void setPosterior9(String posterior9) {
		this.posterior9 = posterior9;
	}

	public String getPosterior10() {
		return posterior10;
	}

	public void setPosterior10(String posterior10) {
		this.posterior10 = posterior10;
	}

	public String getPosterior11() {
		return posterior11;
	}

	public void setPosterior11(String posterior11) {
		this.posterior11 = posterior11;
	}

	public String getPosterior12() {
		return posterior12;
	}

	public void setPosterior12(String posterior12) {
		this.posterior12 = posterior12;
	}

	public String getOrtho1() {
		return ortho1;
	}

	public void setOrtho1(String ortho1) {
		this.ortho1 = ortho1;
	}

	public String getOrtho2() {
		return ortho2;
	}

	public void setOrtho2(String ortho2) {
		this.ortho2 = ortho2;
	}

	public String getOrtho3() {
		return ortho3;
	}

	public void setOrtho3(String ortho3) {
		this.ortho3 = ortho3;
	}

	public String getOrtho4() {
		return ortho4;
	}

	public void setOrtho4(String ortho4) {
		this.ortho4 = ortho4;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	
    public List<String> getHistory() {
		return history;
	}

	public void setHistory(List<String> history) {
		this.history = history;
	}

	
	public List<ToothHistoryDto> getHdto() {
		return hdto;
	}

	public void setHdto(List<ToothHistoryDto> hdto) {
		this.hdto = hdto;
	}

	public String getPasswordRE() {
		return passwordRE;
	}

	public void setPasswordRE(String passwordRE) {
		this.passwordRE = passwordRE;
	}

	public int getCommentsRows() {
		return commentsRows;
	}

	public void setCommentsRows(int commentsRows) {
		this.commentsRows = commentsRows;
	}

	public String getDen5225() {
		return den5225;
	}

	public void setDen5225(String den5225) {
		this.den5225 = den5225;
	}

	public String getDenf5225() {
		return denf5225;
	}

	public void setDenf5225(String denf5225) {
		this.denf5225 = denf5225;
	}

	public String getDen5226() {
		return den5226;
	}

	public void setDen5226(String den5226) {
		this.den5226 = den5226;
	}

	public String getDenf5226() {
		return denf5226;
	}

	public void setDenf5226(String denf5226) {
		this.denf5226 = denf5226;
	}

	public String getBridges1() {
		return bridges1;
	}

	public void setBridges1(String bridges1) {
		this.bridges1 = bridges1;
	}

	public String getBridges2() {
		return bridges2;
	}

	public void setBridges2(String bridges2) {
		this.bridges2 = bridges2;
	}

	public String getCdowngrade() {
		return cdowngrade;
	}

	public void setCdowngrade(String cdowngrade) {
		this.cdowngrade = cdowngrade;
	}

	public String getImplants5() {
		return implants5;
	}

	public void setImplants5(String implants5) {
		this.implants5 = implants5;
	}

	public String getImplants6() {
		return implants6;
	}

	public void setImplants6(String implants6) {
		this.implants6 = implants6;
	}

	public String getImplants7() {
		return implants7;
	}

	public void setImplants7(String implants7) {
		this.implants7 = implants7;
	}

	public String getImplants8() {
		return implants8;
	}

	public void setImplants8(String implants8) {
		this.implants8 = implants8;
	}

	public String getOrtho5() {
		return ortho5;
	}

	public void setOrtho5(String ortho5) {
		this.ortho5 = ortho5;
	}

	public String getWaitingPeriod3() {
		return waitingPeriod3;
	}

	public void setWaitingPeriod3(String waitingPeriod3) {
		this.waitingPeriod3 = waitingPeriod3;
	}

	
	public String getPosterior17() {
		return posterior17;
	}

	public void setPosterior17(String posterior17) {
		this.posterior17 = posterior17;
	}

	public String getPercentages13() {
		return percentages13;
	}

	public void setPercentages13(String percentages13) {
		this.percentages13 = percentages13;
	}

	public String getPercentages14() {
		return percentages14;
	}

	public void setPercentages14(String percentages14) {
		this.percentages14 = percentages14;
	}

	public String getPercentages15() {
		return percentages15;
	}

	public void setPercentages15(String percentages15) {
		this.percentages15 = percentages15;
	}

	public String getPosterior18() {
		return posterior18;
	}

	public void setPosterior18(String posterior18) {
		this.posterior18 = posterior18;
	}

	public String getPercentages16() {
		return percentages16;
	}

	public void setPercentages16(String percentages16) {
		this.percentages16 = percentages16;
	}

	public String getPosterior19() {
		return posterior19;
	}

	public void setPosterior19(String posterior19) {
		this.posterior19 = posterior19;
	}

	public String getPosterior20() {
		return posterior20;
	}

	public void setPosterior20(String posterior20) {
		this.posterior20 = posterior20;
	}

	public String getWaitingPeriod4() {
		return waitingPeriod4;
	}

	public void setWaitingPeriod4(String waitingPeriod4) {
		this.waitingPeriod4 = waitingPeriod4;
	}

	public String getShareFr() {
		return shareFr;
	}

	public String getPedo1() {
		return pedo1;
	}

	public void setPedo1(String pedo1) {
		this.pedo1 = pedo1;
	}

	public String getPedo2() {
		return pedo2;
	}

	public void setPedo2(String pedo2) {
		this.pedo2 = pedo2;
	}

	public void setShareFr(String shareFr) {
		this.shareFr = shareFr;
	}

	public String getPano1() {
		return pano1;
	}

	public void setPano1(String pano1) {
		this.pano1 = pano1;
	}

	public String getPano2() {
		return pano2;
	}

	public void setPano2(String pano2) {
		this.pano2 = pano2;
	}

	public String getD4381() {
		return d4381;
	}

	public void setD4381(String d4381) {
		this.d4381 = d4381;
	}

	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCkD0120() {
		return ckD0120;
	}

	public void setCkD0120(String ckD0120) {
		this.ckD0120 = ckD0120;
	}

	public String getCkD0140() {
		return ckD0140;
	}

	public void setCkD0140(String ckD0140) {
		this.ckD0140 = ckD0140;
	}

	public String getCkD0145() {
		return ckD0145;
	}

	public void setCkD0145(String ckD0145) {
		this.ckD0145 = ckD0145;
	}

	public String getCkD0150() {
		return ckD0150;
	}

	public void setCkD0150(String ckD0150) {
		this.ckD0150 = ckD0150;
	}

	public String getCkD0160() {
		return ckD0160;
	}

	public void setCkD0160(String ckD0160) {
		this.ckD0160 = ckD0160;
	}

	public String getCkD210() {
		return ckD210;
	}

	public void setCkD210(String ckD210) {
		this.ckD210 = ckD210;
	}

	public String getCkD220() {
		return ckD220;
	}

	public void setCkD220(String ckD220) {
		this.ckD220 = ckD220;
	}

	public String getCkD230() {
		return ckD230;
	}

	public void setCkD230(String ckD230) {
		this.ckD230 = ckD230;
	}

	public String getCkD330() {
		return ckD330;
	}

	public void setCkD330(String ckD330) {
		this.ckD330 = ckD330;
	}

	public String getCkD274() {
		return ckD274;
	}

	public void setCkD274(String ckD274) {
		this.ckD274 = ckD274;
	}

	public Integer getIvFormTypeId() {
		return ivFormTypeId;
	}

	public void setIvFormTypeId(Integer ivFormTypeId) {
		//if (ivFormTypeId==null) ivFormTypeId=0;
		this.ivFormTypeId = ivFormTypeId;
	}

	public String getD0160Freq() {
		return d0160Freq;
	}

	public void setD0160Freq(String d0160Freq) {
		this.d0160Freq = d0160Freq;
	}

	public String getD2391Freq() {
		return d2391Freq;
	}

	public void setD2391Freq(String d2391Freq) {
		this.d2391Freq = d2391Freq;
	}

	public String getD0330Freq() {
		return d0330Freq;
	}

	public void setD0330Freq(String d0330Freq) {
		this.d0330Freq = d0330Freq;
	}

	public String getD4381Freq() {
		return d4381Freq;
	}

	public void setD4381Freq(String d4381Freq) {
		this.d4381Freq = d4381Freq;
	}

	public String getD3330() {
		return d3330;
	}

	public void setD3330(String d3330) {
		this.d3330 = d3330;
	}

	public String getD3330Freq() {
		return d3330Freq;
	}

	public void setD3330Freq(String d3330Freq) {
		this.d3330Freq = d3330Freq;
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

	public String getIvSedation() {
		return ivSedation;
	}

	public void setIvSedation(String ivSedation) {
		this.ivSedation = ivSedation;
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

	public String getD33300() {
		return d33300;
	}

	public void setD33300(String d33300) {
		this.d33300 = d33300;
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

	public String getFreqD2934() {
		return freqD2934;
	}

	public void setFreqD2934(String freqD2934) {
		this.freqD2934 = freqD2934;
	}


	
}
