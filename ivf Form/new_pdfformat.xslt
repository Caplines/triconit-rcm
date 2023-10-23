<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
   version="1.0" >
	<xsl:output method="html" indent="yes" encoding="UTF-8"  version="1.0"  />
<xsl:template match="/caplineIVFFormDto">

<html>
<head>
    <meta charset="utf-8" />
    <title></title>
    <style>
        body {
            font-family: helvetica;
            font-size: 12px;
        }

        .tableHeading {
            font-size: 14px;
            background-color: #3d85c6;
            color: #fff;
            text-align: center;
            font-family: sans-serif;
            font-weight: bold;
        }


        .sub-heading {
            font-size: 12px;
            color: #000;
            text-align: center;
            font-weight: bold;
        }
        .sub-heading1 {font-family:helvetica;font-size:12px;}

        .rowHeading {
            background-color: #666666;
            color: #fff;
            font-size: 14px;
        }

        .table {
            border-collapse: collapse;
            width: 100%
        }

            .table td {
                border: 1px solid #000;
                border-collapse: collapse;
                padding: 2px 4px;
                vertical-align: top;
            }

        .lightGray {
            background-color: #f3f3f3;
        }

        .lightBrown {
            background-color: #f9cb9c;
        }

        .yellow {
            background-color: #ff0;
        }

        .red {
            background-color: #f00;
            color: #fff;
        }

        .white {
            background-color: #fff;
        }

        .dullBlue {
            background-color: #EDE3FF ;
            color: #000;
        }

        .dullYellow {
            background-color: #E6E6E3;
            color: #000;
        }

        .dullRed {
            background-color: #DBEBFF;
            color: #000;
        }

        .dullGreen {
            background-color: #b6d7a8;
        }

        .text-white {
            color: #fff;
        }

        .width-5 {
            width: 5%;
            border: 0px;
        }

        .width-7 {
            width: 7%;
        }

        .width-8 {
            width: 8%;
        }

        .width-10 {
            width: 10%;
        }

        .width-13 {
            width: 13%;
        }

        .width-14 {
            width: 14%;
        }

        .width-15 {
            width: 15%;
        }

        .width-16 {
            width: 16%;
        }

        .width-17 {
            width: 17%;
        }

        .text-left {
            text-align: left;
        }

        .text-center {
            text-align: center;
        }

        .table td.borderNone {
            border: 0px;
            background:#fff;
        }
        .blackClr {
            color:#000;
        }
        .align-right {
            text-align:right;
        }
		.table td.innerTableBox {
            padding: 0;
        }
        .innerTable {
            border-collapse: collapse;
            float: left;
            width: 33.333%;
        }
        .innerTable tr td:first-child {
            border-left:0px;
        }
            .innerTable:last-child tr td:last-child {
                border-right: 0px;
            }
            .innerTable tr td {
                border-top: 0px;
            }
            .innerTable tr:last-child td {
                border-bottom: 0px;
            }
            .historyBackground{

                 background-color:  #FFFFFF

            }
            .main-heading-11 {font-family:helvetica;font-weight:regular;font-size:13px;margin-left:-3px;}
            .red-clr{
            color: #842029;
            background-color: #f8d7da;
            padding:5px 10px;
            }
            .historyCounts{
            color: #0f5132;
           background-color: #d1e7dd;
           border-color: #badbcc;

          display: inline-block;

          padding:5px 10px;
            }
    </style>
</head>
<body>
    <form>
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="10" class="tableHeading">Subscriber and Insurance Details</td>
                <!-- <td class="width-5 borderNone"></td> -->
            </tr>
            <tr>
                <td class="width-15">Office Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo1"/></td>
                <td class="width-10">Patient Name </td>
                <td class="width-7"><xsl:value-of select="basicInfo2"/></td>
                <td class="width-13">Insurance Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo3"/></td>
                <td class="width-7">Provider Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo19"/></td>
                <td class="width-15">Sec Provider Name</td>
                <td class="width-7"><xsl:value-of select="secProviderName"/></td>
                <!-- <td class="width-5 borderNone"></td> -->
            </tr>
            <tr class="lightGray">
                <td>Tax ID</td>
                <td><xsl:value-of select="basicInfo4"/></td>
                <td>Patient DOB</td>
                <td class="white"><xsl:if test="string-length(basicInfo6) &gt; 9"><xsl:value-of select="concat(substring(basicInfo6,6,2),'/',substring(basicInfo6,9,2),'/',substring(basicInfo6,1,4))" /></xsl:if></td>
                <td>Insurance Contact</td>
                <td><xsl:value-of select="basicInfo7"/></td>
                <td>Provider Network</td>
                <xsl:choose>
                     <xsl:when test="translate(policy3, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'out'">
                     <td class="red blackClr"><xsl:value-of select="policy3"/></td>
                     </xsl:when>
                     <xsl:otherwise>
                     <td class="blackClr"><xsl:value-of select="policy3"/></td>
                     </xsl:otherwise>
                </xsl:choose>   
                <td>Sec Provider Network</td>
                <td><xsl:value-of select="secProvNetwork"/></td>
                <!-- <td class="borderNone"></td> -->
            </tr>
            <tr>
                <td>ES/Patient ID</td>
                <td><xsl:value-of select="basicInfo21"/></td>
                <td>Policy Holder Name</td>
                <td><xsl:value-of select="basicInfo5"/></td>
                <td>Appointment Type</td>
                <td><xsl:value-of select="basicInfo11"/></td>
                <td>Plan w/ OON Benefits</td>
                <xsl:choose>
                     <xsl:when test="oonbenfits = 'No' ">
                     <td class="red blackClr"><xsl:value-of select="oonbenfits"/></td>
                     </xsl:when>
                     <xsl:otherwise>
                     <td class="blackClr"><xsl:value-of select="oonbenfits"/></td>
                     </xsl:otherwise>
                </xsl:choose>   
                <td>Pt Assigned To Office</td>
                 <xsl:choose>
                     <xsl:when test="yesNoAssignToffice = 'No' ">
                     <td class="red blackClr"><xsl:value-of select="yesNoAssignToffice"/></td>
                     </xsl:when>
                     <xsl:otherwise>
                     <td class="blackClr"><xsl:value-of select="yesNoAssignToffice"/></td>
                     </xsl:otherwise>
                </xsl:choose>   
                <!-- <td class="borderNone"></td> -->
            </tr>
            <tr class="lightGray">
                <td>Member ID/SSN</td>
                <td><xsl:value-of select="basicInfo16"/></td>
                <td>Policy Holder DOB</td>
                <td class="white"><xsl:if test="string-length(basicInfo9) &gt; 9"><xsl:value-of select="concat(substring(basicInfo9,6,2),'/',substring(basicInfo9,9,2),'/',substring(basicInfo9,1,4))" /></xsl:if></td>
                <td>Appointment Date</td>
                <td class="white"><xsl:if test="string-length(basicInfo17) &gt; 9"><xsl:value-of select="concat(substring(basicInfo17,6,2),'/',substring(basicInfo17,9,2),'/',substring(basicInfo17,1,4))" /></xsl:if></td>
                <td>Source</td>
                <td><xsl:value-of select="basicInfo8"/></td>
                <td>Ref #</td>
                <td><xsl:value-of select="basicInfo12"/></td>
                <!-- <td class="borderNone"></td> -->
            </tr>

        </table>
        <br />
        <br />

        <table class="table" vertical-align="top">
            <tr>
                <td colspan="10" class="tableHeading">Policy Information </td>
                <!-- <td class="width-5 borderNone"></td>                -->
            </tr>

            <tr>
                <td class="width-15">Plan Type</td>
                <td class="width-7"><xsl:value-of select="policy1"/></td>
                <td class="width-12">Group/Emp Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo10"/></td>
                <td class="width-13">Group <br/>Number <br/></td>
                <td class="width-7"><xsl:value-of select="basicInfo14"/></td>
                <td class="width-7">Effective Date</td>
                <td class="width-7"><xsl:if test="string-length(policy5) &gt; 9"><xsl:value-of select="concat(substring(policy5,6,2),'/',substring(policy5,9,2),'/',substring(policy5,1,4))" /></xsl:if></td>
                <td class="width-15">Termination Date</td>
                <td class="width-7"><xsl:if test="string-length(policy2) &gt; 9"><xsl:value-of select="concat(substring(policy2,6,2),'/',substring(policy2,9,2),'/',substring(policy2,1,4))" /></xsl:if></td>
                <!-- <td class="width-5 borderNone"></td> -->
            </tr>
            <tr class="lightGray">
                <td>Annual Max</td>
                <td><xsl:value-of select="policy7"/></td>
                <td>Individual Deductible</td>
                <td><xsl:value-of select="policy9"/></td>
                <td>Fee Schedule</td>
                <td><xsl:value-of select="policy4"/></td>
                <td>Fee of D0120</td>
                <td><xsl:value-of select="policy18"/></td>
                <td>Benefit Period Year</td>
                <td class="white"><xsl:value-of select="policy6"/></td>
                <!-- <td class="borderNone"></td> -->
            </tr>
            <tr>

                <td>Annual Max Remaining</td>
                 <xsl:choose>
                    <xsl:when test="number(policy8) &lt; 100">
                    <td class="red blackClr"><xsl:value-of select="policy8"/></td>
                    </xsl:when>
                     <xsl:when test="number(policy8) &gt;= 100 and number(policy8) &lt;=400" >
                     <td class="yellow blackClr"><xsl:value-of select="policy8"/></td>
                     </xsl:when>
                     <xsl:otherwise>
                     <td class="blackClr"><xsl:value-of select="policy8"/></td>
                     </xsl:otherwise>
                </xsl:choose>   
                <td>Ind Ded Remaining</td>
                <td><xsl:value-of select="policy10"/></td>
                <td>Coverage Book</td>
                <td><xsl:value-of select="policy16"/></td>
                <td>Fee of D2391</td>
                <td><xsl:value-of select="policy19"/></td>
                <td>COB Status</td>
                <xsl:choose>
                     <xsl:when test="translate(basicInfo3, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'geha' or translate(basicInfo3, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') ='fep' ">
                     <td class="blackClr"><xsl:text>Secondary</xsl:text></td>
                     </xsl:when>
                     <xsl:otherwise>
                     <td class="blackClr"><xsl:value-of select="basicInfo15"/></td>
                     </xsl:otherwise>
                </xsl:choose>   
                <!-- <td class="borderNone"></td> -->
            </tr>
            <tr class="lightGray">
                <td>AOB Accepted</td>
                
				<xsl:choose>
				     <xsl:when test="policy15 = 'No' ">
					 <td class="red blackClr"><xsl:value-of select="policy15"/></td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="blackClr"><xsl:value-of select="policy15"/></td>
					 </xsl:otherwise>
				</xsl:choose>	 
						  
                <td>Pre- Auth Required</td>
				<xsl:choose>
				     <xsl:when test="policy15 = 'No' ">
					 <td><xsl:value-of select="policy12"/></td>
					 </xsl:when>
				     <xsl:otherwise>
					 <xsl:choose>
					      <xsl:when test="policy12 = 'Refer Medicaid &#38; Medicare Benefits(RD0703)'">
						  <td class="blackClr"><a href="https://docs.google.com/spreadsheets/d/13C7ph9Hal1mDdU5nioWWX2ndof6Ls2IYbVw03uRDiVM/edit#gid=2067143248" style="text-decoration:none;color: red;" traget="_blank" >Refer Medicaid &#38; Medicare Benefits(RD0703)</a></td>
						  </xsl:when>
				          <xsl:otherwise>
						   <td class="">No</td>
						  </xsl:otherwise>
					 </xsl:choose>
					
					 </xsl:otherwise>
				</xsl:choose>
				 <td>Dep Covered till Age</td>
                 <td><xsl:value-of select="policy11"/></td>
               
                <td>Claims Filing Limit</td>
                <td><xsl:value-of select="percentages12"/></td>
                <td class="lightBrown">CRA Required</td>
				<xsl:choose>
					      <xsl:when test="policy17 = 'Refer to CRA Info for Medicaid Plans(RD2405)'">
						  <td class="blackClr" colspan="1"><a href="https://docs.google.com/spreadsheets/d/1kjq3Q2r3eRoC0Ygi9ODXczFfJPExBmo6mbeQYQLlooo/edit#gid=0" style="text-decoration:none;color:red;" traget="_blank" >CRA Information for Medicaid Plans(RD2405)</a></td>
						  </xsl:when>
				          <xsl:otherwise>
						   <td class="" colspan="1">No</td>
				          </xsl:otherwise>
				</xsl:choose>					
            </tr>

        </table>
        <br />
        <br />
        <xsl:if test="(translate(basicInfo3, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'geha' or translate(basicInfo3, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') ='fep') and translate(basicInfo15, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') ='secondary' ">
        <div class="red-clr">
        <strong>Alert-</strong> Patient have BCBS Medical policy as primary &amp; we can directly bill them even if we do not have insurance details.
        </div>
        </xsl:if>
        <xsl:if test="(translate(basicInfo3, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'guardian')">
        <div class="red-clr">
        <strong>Alert-</strong> D0140 only Covered with x rays, If performed with other treatment amounts to be collected from patient and WO to insurance at $0.
        </div>
        </xsl:if>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr class="lightGray">
                <td class="width-15 red tableHeading text-left">Remarks for Office/LC3</td>
                <td colspan="10"><xsl:value-of select="comments"/></td>
            </tr>
        </table>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="11" class="tableHeading">Coverage &amp; Frequency Information</td>
            </tr>

            <tr>
                <td colspan="3" class="sub-heading dullBlue">Preventive Services</td>
                <td class="width-4 borderNone"></td>
                <td colspan="3" class="sub-heading dullYellow">Basic Services</td>
                <td class="width-4  borderNone"></td>
                <td colspan="3" class="sub-heading dullRed">Major Services</td>
            </tr>
            <tr>
                <td class="width-15 dullBlue">Preventive Services % </td>
                <td colspan="2" class="width-17 dullBlue"><xsl:value-of select="percentages9"/></td>

                 <td class="width-4 borderNone"></td>

                <td class="width-15 dullYellow">Basic Services % </td>
                <td colspan="2" class="width-17 dullYellow"><xsl:value-of select="percentages1"/></td>

                <td class="width-4 borderNone"></td>

                <td class="width-15 dullRed">Major Services %</td>
                <td colspan="2" class="width-17 dullRed"><xsl:value-of select="percentages3"/></td>
            </tr>
            <tr>
			 
			    <xsl:choose>
				     <xsl:when test="waitingPeriod4 = 'No' ">
					 <td class="width-15 dullBlue">Waiting Period</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red width-15 dullBlue">Waiting Period</td>
					 </xsl:otherwise>
				</xsl:choose>	
                
                <xsl:choose>
				     <xsl:when test="waitingPeriod4 != 'No' ">
					  <td colspan="2" class="width-17 red blackClr"><xsl:value-of select="waitingPeriod4"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-15 dullBlue"><xsl:value-of select="waitingPeriod4"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
				
                <td class="width-4 borderNone"></td>
                
				<xsl:choose>
				     <xsl:when test="waitingPeriod1 = 'No' ">
					 <td class="width-13 dullYellow">Waiting Period</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red width-13 dullYellow">Waiting Period</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="waitingPeriod1 != 'No' ">
					  <td colspan="2" class="width-14 red blackClr"><xsl:value-of select="waitingPeriod1"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-13 dullYellow"><xsl:value-of select="waitingPeriod1"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
                <td class="width-4 borderNone"></td>
                 
				<xsl:choose>
				     <xsl:when test="waitingPeriod2 = 'No' ">
					 <td class="width-15 dullRed">Waiting Period</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red width-15 dullRed">Waiting Period</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="waitingPeriod2 != 'No' ">
					  <td colspan="2" class="width-12 red blackClr"><xsl:value-of select="waitingPeriod2"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-15 dullRed"><xsl:value-of select="waitingPeriod2"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
			   <xsl:choose>
				     <xsl:when test="percentages13 = 'No' ">
					 <td class="dullBlue">Subject to Ded</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red dullBlue">Subject to Ded</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="percentages13 = 'Yes' ">
					  <td colspan="2" class="width-16 red blackClr"><xsl:value-of select="percentages13"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-16 dullBlue"><xsl:value-of select="percentages13"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>	 
			   
                <td class="borderNone"></td>
                <xsl:choose>
				     <xsl:when test="percentages2 = 'No' ">
					 <td class="dullYellow">Subject to Ded</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red dullYellow">Subject to Ded</td>
					 </xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
				     <xsl:when test="percentages2 = 'Yes' ">
					  <td colspan="2" class="width-16 red blackClr"><xsl:value-of select="percentages2"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-16 dullYellow"><xsl:value-of select="percentages2"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
                <td class="borderNone"></td>
                <xsl:choose>
				     <xsl:when test="percentages4 = 'No' ">
					 <td class="dullRed">Subject to Ded</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red dullRed">Subject to Ded</td>
					 </xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
				     <xsl:when test="percentages4 = 'Yes' ">
					  <td colspan="2" class="width-16 red blackClr"><xsl:value-of select="percentages4"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-16 dullRed"><xsl:value-of select="percentages4"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
                <td class="rowHeading">Oral Evaluations</td>
                <td class="rowHeading width-7">Pct.</td>
                <td class="rowHeading width-10">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Perio(Non-Surgical)</td>
                <td class="rowHeading width-7">Pct.	</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
				<xsl:choose>
				     <xsl:when test="prosthetics1 = 'No' ">
					 <td class="dullRed">Missing tooth clause</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red dullRed">Missing tooth clause</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="prosthetics1 = 'Yes' ">
					  <td colspan="2" class="red blackClr"><xsl:value-of select="prosthetics1"/></td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red dullRed"><xsl:value-of select="prosthetics1"/></td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
                <td class="dullBlue">D0120 (POE)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0120"/></td>
                <td class="dullBlue"><xsl:value-of select="exams1"/></td> 
                <td class="borderNone"></td>
                <td class="dullYellow">D4341 (SRP)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perio1"/></td>
                <td class="dullYellow"><xsl:value-of select="perio2"/></td>
                <td class="borderNone"></td>
				<xsl:choose>
				     <xsl:when test="prosthetics2 = 'No' ">
					 <td class="dullRed">Replacement Clause</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red dullRed">Replacement Clause</td>
					 </xsl:otherwise>
				</xsl:choose>
               
				<xsl:choose>
				     <xsl:when test="prosthetics2 = 'No' ">
					  <td colspan="2" class="dullRed"><xsl:value-of select="prosthetics2"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red dullRed"><xsl:value-of select="prosthetics2"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
                <td class="dullBlue">D0140 (LOE)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0140"/></td>
                <td class="dullBlue"><xsl:value-of select="exams2"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">Quads Allowed / SRP </td>
                <td class="dullYellow align-right" colspan="2"><xsl:value-of select="perio3"/></td>
               
                <td class="borderNone"></td>
                <td class="rowHeading">Buildups &amp; Crowns</td>
                <td class="rowHeading width-7">Pct.	</td>
                <td class="rowHeading">Freq	</td>
            </tr>
            <tr>
                <td class="dullBlue">D0150 (COE)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0150"/></td>
                <td class="dullBlue"><xsl:value-of select="exams4"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">Rest Recommended B/W Quads</td>
                <td class="dullYellow" colspan="2"><xsl:value-of select="perio4"/></td>
                
                <td class="borderNone"></td>
                <td class="dullRed">D2950 (Core Buidup)</td>
                <td class="dullRed width-7 align-right"><xsl:value-of select="posterior10"/></td>
                <td class="dullRed"><xsl:value-of select="posterior11"/></td>
            </tr>
            <tr>
                
                <td class="dullBlue">Does Exams Share Freq? </td>
                <td class="dullBlue" colspan="2"><xsl:value-of select="shareFr"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4346(Gingivitis)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioMnt6"/></td>
                <td class="dullYellow"><xsl:value-of select="perioMnt7"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Crown Paid on Date</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="prosthetics3"/></td>
               
            </tr>
            <tr>
                 <td class="rowHeading">Radiographs</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4910(Perio Main)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioMnt1"/></td>
                <td class="dullYellow"><xsl:value-of select="perioMnt2"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2740 (Porcelain)</td>
                <td class="dullRed align-right"><xsl:value-of select="posterior4"/></td>
                <td class="dullRed"><xsl:value-of select="posterior5"/></td>
               
            </tr>
            <tr>
               <td class="dullBlue">D0220/30 (PAs)</td>
                <td class="dullBlue align-right"><xsl:value-of select="percentages11"/></td>
                <td class="dullBlue"><xsl:value-of select="xrays2"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Perio (Surgical)</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed">D2750 (Noble Metal)</td>
                <td class="dullRed align-right"><xsl:value-of select="d2750"/></td>
                <td class="dullRed"><xsl:value-of select="d2750fr"/></td>
            </tr>
            <tr>
                  <td class="dullBlue">D0210 (FMX)</td>
                <td class="dullBlue align-right"><xsl:value-of select="percentages16"/></td>
                <td class="dullBlue"><xsl:value-of select="xrays4"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4381(Arestin)</td>
                <td class="dullYellow"><xsl:value-of select="d4381"/></td>
                <td class="dullYellow"><xsl:value-of select="d4381Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Applicable to Downgrading</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="posterior6"/></td>
            </tr>
            <tr>
               <td class="dullBlue">D0330 (Pano)</td>
                <td class="dullBlue align-right"><xsl:value-of select="pano1"/></td>
                <td class="dullBlue"><xsl:value-of select="d0330Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4249(Crown Len)</td>
                <td class="dullYellow"><xsl:value-of select="oral1"/></td>
                <td class="dullYellow"><xsl:value-of select="oral2"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Downgraded to Code</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="posterior17"/></td>
                
               
            </tr>
            <tr>
                 <td class="dullBlue">D0272/74 (BWXs)</td>
                <td class="dullBlue align-right"><xsl:value-of select="bwx"/></td>
                <td class="dullBlue"><xsl:value-of select="xrays1"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">VAPs</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed">No. of Crowns / Year </td>
                <td class="dullRed" colspan="2"><xsl:value-of select="crn1"/></td>
               
               
            </tr>
            <tr>
                <td class="dullBlue">Does FMX &amp; PANO share Freq?</td>
                <td class="dullBlue"  colspan="2"><xsl:value-of select="pano2"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D0431 (Cancer SCRN)</td>
                <td class="dullYellow align-right"><xsl:value-of select="d0431"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d0431fr"/></td>
                <td class="borderNone"></td>
                 <td class="rowHeading">SSC Crowns</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                
               
            </tr>
            <tr>
                 <td class="rowHeading">Prophylaxis</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4999 (Unsp. Perio)</td>
                <td class="dullYellow align-right"><xsl:value-of select="d4999"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d4999fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2930/34 (Primary)</td>
                <td class="dullRed"><xsl:value-of select="d2930"/></td>
                <td class="dullRed"><xsl:value-of select="ssc1"/></td>
            </tr>
            <tr>
                <td class="dullBlue">Roll Age for Prophylaxis</td>
                <td class="dullBlue align-right" colspan="2"><xsl:value-of select="rollage"/></td>
                <td class="borderNone"></td>
                 <td class="dullYellow">D9630 (Peridex)</td>
                <td class="dullYellow align-right"><xsl:value-of select="d9630"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d9630fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2931(Permanent)</td>
                <td class="dullRed align-right"><xsl:value-of select="d2931"/></td>
                <td class="dullRed"><xsl:value-of select="ssc2"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D1120 (Child)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1120"/></td>
                <td class="dullBlue"><xsl:value-of select="prophy2"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4921 (Gingival Irrig.)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioD4921"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d4921Frequency"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Bridges</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
            </tr>
            <tr>
                 <td class="dullBlue">D1110 (Adult)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1110"/></td>
                <td class="dullBlue"><xsl:value-of select="prophy1"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4266 (Tissue Regen)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioD4266"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d4266Frequency"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D6245/D6740</td>
                <td class="dullRed align-right"><xsl:value-of select="bridges1"/></td>
                <td class="dullRed"><xsl:value-of select="bridges2"/></td>
            </tr>
            <tr>
               <td class="rowHeading">Flouride</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                 <td class="rowHeading">Fillings</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Dentures</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
            </tr>
            <tr>
               
                 <td class="dullBlue">Fluoride Covered till Age</td>
                <td class="dullBlue align-right"  colspan="2"><xsl:value-of select="fluroide2"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D2391 (Composites)</td>
                <td class="dullYellow align-right"><xsl:value-of select="posterior1"/></td>
                <td class="dullYellow"><xsl:value-of select="posterior2"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D5110/20 (Complete)</td>
                <td class="dullRed" colspan="1"><xsl:value-of select="d511020Percentage"/></td>
                <td class="dullRed" colspan="1"><xsl:value-of select="dentures1"/></td>
                
            </tr>
            <tr>
                 <td class="dullBlue">D1206 (w/ Varnish)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1206"/></td>
                <td class="dullBlue"><xsl:value-of select="fluroide1"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">Downgraded to Amalgam (D2140)</td>
                <td class="dullYellow"  colspan="2"><xsl:value-of select="posterior3"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D5130/40<br/> (Immediate)</td>
                <td class="dullRed" colspan="1"><xsl:value-of select="d513040Percentage"/></td>
                <td class="dullRed"  colspan="2"><xsl:value-of select="d5130_40"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D1208 (w/o Varnish)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1208"/></td>
                <td class="dullBlue"><xsl:value-of select="fluroide3"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">No. of Fillings / Year </td>
                <td class="dullYellow"  colspan="2"><xsl:value-of select="fill1"/></td>
                <td class="borderNone"></td>
                <td class="dullRed"><strong>D5820/21 (Interim)</strong></td>
                <td class="dullRed" colspan="1"><xsl:value-of select="d5810CPercentage"/></td>
                <td class="dullRed"  colspan="2"><xsl:value-of select="d5810_c"/></td>
            </tr>
            <tr>
                 
               <td class="rowHeading">Oral Hygiene</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Extractions</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Implants</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                
            </tr>
            <tr>
                <td class="dullBlue">D1330 (Instructions)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1330"/></td>
                <td class="dullBlue align-right"><xsl:value-of select="d1330Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7111/40 (Minor)</td>
                <td class="dullYellow align-right"><xsl:value-of select="extractions1"/></td>
                <td class="dullYellow"><xsl:value-of select="extractions1fr"/></td>
                <td class="borderNone"></td>
                 <td class="dullRed">D6010</td>
                <td class="dullRed align-right" colspan="1"><xsl:value-of select="implants1"/></td>
                <td class="dullRed align-right" colspan="1"><xsl:value-of select="implants5"/></td>
            </tr>
            <tr>
                <td class="rowHeading">Sealants</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7210-40 (Major)</td>
                <td class="dullYellow align-right"><xsl:value-of select="extractions2"/></td>
                <td class="dullYellow"><xsl:value-of select="extractions2fr"/></td>
                <td class="borderNone"></td>
               <td class="rowHeading">Oral Surgery</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
            </tr>
            <tr>
                <td class="dullBlue">D1351 (Per Tooth)</td>
                <td class="dullBlue align-right"><xsl:value-of select="sealantsD"/></td>
                <td class="dullBlue"><xsl:value-of select="sealants1"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7250 (Wis Tooth)</td>
                <td class="dullYellow align-right"><xsl:value-of select="d7250"/></td>
                <td class="dullYellow"><xsl:value-of select="d7250fr"/></td>
                <td class="borderNone"></td>
                 <td class="dullRed">D7310 (4 &gt;= Tooth/Quad)</td>
                <td class="dullRed align-right"><xsl:value-of select="d7310"/></td>
                <td class="dullRed"><xsl:value-of select="oral6"/></td>
            </tr>
            <tr>
               <td class="dullBlue">Sealants Covered till Age</td>
                <td class="dullBlue align-right" colspan="2"><xsl:value-of select="sealants2"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">
                   Max.# of Ext./Year
                </td>
                <!-- <td class="dullYellow"></td> -->
                <td class="dullYellow" colspan="2"><xsl:value-of select="extr1"/></td>
                <td class="borderNone"></td>
                 <td class="dullRed">D7311 (1-3 Tooth/Qaud)</td>
                <td class="dullRed align-right"><xsl:value-of select="d7311"/></td>
                <td class="dullRed"><xsl:value-of select="oral4"/></td>  
            </tr>
            <tr>
                 <td class="dullBlue">Covered on Primary Molars</td>
                <td class="red blackClr" colspan="2"><xsl:value-of select="sealants3"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Endodontics</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                 <td class="dullRed">D7953 (Bone Graft)</td>
                <td class="dullRed align-right"><xsl:value-of select="d7953"/></td>
                <td class="dullRed"><xsl:value-of select="dentures6"/></td>
            </tr>
            <tr>
                <td class="dullBlue">Covered on Pre-Molars</td>
                <td class="red blackClr" colspan="2"><xsl:value-of select="sealants4"/></td> 
                <td class="borderNone"></td>
                <td class="dullYellow">D3310 (Ant Teeth)</td>
                <td class="dullYellow"><xsl:value-of select="d3310"/></td>
                <td class="dullYellow"><xsl:value-of select="d3310fr"/></td>
                <td class="borderNone"></td>
               <td class="dullRed" colspan="2">Bone Graft Covered with</td>
                <td class="dullRed"><xsl:value-of select="dentures5"/></td>
            </tr>
            <tr>
                 <td class="dullBlue">Covered on Permanent-Molars</td>
                <td class="red blackClr" colspan="2"><xsl:value-of select="sealants5"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D3320 (Pre-molar Tooth)</td>
                <td class="dullYellow"><xsl:value-of select="d3320"/></td>
                <td class="dullYellow"><xsl:value-of select="d3320fr"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading text-center" colspan="3">Ortho Criteria</td>
                 
            </tr>
            <tr>
                 <td class="rowHeading">Consultation</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Night Gaurds</td>
                <td class="rowHeading">Pct.</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">Ortho Coverage (D8070/80/90)</td>
                <td class="dullRed align-right"><xsl:value-of select="ortho1"/></td> 

                
            </tr>
            <tr>
                <td class="dullBlue">D9310</td>
                <td class="dullBlue align-right"><xsl:value-of select="posterior8"/></td>
                <td class="dullBlue"><xsl:value-of select="posterior9"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9944/45 (Hard/Soft)</td>
                <td class="dullYellow align-right"><xsl:value-of select="posterior7"/></td>
                <td class="dullYellow"><xsl:value-of select="posterior19"/></td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">Ortho Maximum</td>
                <td class="dullRed align-right"><xsl:value-of select="ortho2"/></td> 
            </tr>
            <tr>
                <td colspan="8" class="borderNone"></td> 
                
            </tr>
        </table>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="11" class="tableHeading">Treatment History of the Patient</td>
            </tr>
            <tr>
                <td colspan="11" class="innerTableBox">
                    <table class="innerTable" vertical-align="top">
                        <tr>
                            <td class="rowHeading width-15">ADA Code</td>
                            <td class="rowHeading width-7">T/S/Q/A</td>
                            <td class="rowHeading width-10">DOS</td>
                        </tr>
						<xsl:for-each select="hdto1/hisall1">
						 <tr>
                            <td class="historyBackground"><xsl:value-of select="historyCode"/></td>
                            <td class="historyBackground"><xsl:value-of select="historyTooth"/></td>
                            <td class="historyBackground align-right"><xsl:if test="string-length(historyDos) &gt; 9"><xsl:value-of select="concat(substring(historyDos,6,2),'/',substring(historyDos,9,2),'/',substring(historyDos,1,4))" /></xsl:if></td>
                        </tr>
						</xsl:for-each>
                       
                    </table>

                    <table class="innerTable" vertical-align="top">
                        <tr>
                            <td class="rowHeading width-15">ADA Code</td>
                            <td class="rowHeading width-7">T/S/Q/A</td>
                            <td class="rowHeading width-10">DOS</td>
                        </tr>
                       
                       <xsl:for-each select="hdto2/hisall2">
						 <tr>
                            <td class="historyBackground"><xsl:value-of select="historyCode"/></td>
                            <td class="historyBackground"><xsl:value-of select="historyTooth"/></td>
                            <td class="historyBackground align-right"><xsl:if test="string-length(historyDos) &gt; 9"><xsl:value-of select="concat(substring(historyDos,6,2),'/',substring(historyDos,9,2),'/',substring(historyDos,1,4))" /></xsl:if></td>
                        </tr>
						</xsl:for-each>
                    </table>
                    <table class="innerTable" vertical-align="top">
                        <tr>
                            <td class="rowHeading width-15">ADA Code</td>
                            <td class="rowHeading width-7">T/S/Q/A</td>
                            <td class="rowHeading width-10">DOS</td>
                        </tr>
                        <xsl:for-each select="hdto3/hisall3">
						 <tr>
                            <td class="historyBackground"><xsl:value-of select="historyCode"/></td>
                            <td class="historyBackground"><xsl:value-of select="historyTooth"/></td>
                            <td class="historyBackground align-right"><xsl:if test="string-length(historyDos) &gt; 9"><xsl:value-of select="concat(substring(historyDos,6,2),'/',substring(historyDos,9,2),'/',substring(historyDos,1,4))" /></xsl:if></td>
                        </tr>
						</xsl:for-each>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="borderNone" colspan="11"></td>
            </tr>  
            <tr>
                <td class="historyBackground width-15">Benefits Verified by</td>
                <td class="lightGray width-15"><xsl:value-of select="benefits"/></td>
                <td class="historyBackground width-15">Verfied on Date</td>
                <td class="lightGray width-15"><xsl:if test="string-length(date) &gt; 9"><xsl:value-of select="concat(substring(date,6,2),'/',substring(date,9,2),'/',substring(date,1,4))" /></xsl:if></td>
                <td class="borderNone" colspan="7"></td>
            </tr>  
        </table>
        <br/>
        <div class="historyCounts">
                <strong>History Count:</strong><xsl:value-of select="historyCount"/>  
                    
        </div>
		
    </form>

</body>
</html>
</xsl:template>
<xsl:template match="text()">
   <xsl:param name="text" select="."/>

   <xsl:variable name="startText" select="substring-before(concat($text,'&#10;'),'&#10;')" />
   <xsl:variable name="nextText" select="substring-after($text,'&#10;')"/>
   <xsl:if test="normalize-space($startText)">
       <xsl:value-of select="$startText"/>
      <xsl:if test="normalize-space($nextText)">
         <br />
      </xsl:if>
   </xsl:if>

   <xsl:if test="contains($text,'&#10;')">
      <xsl:apply-templates select=".">
         <xsl:with-param name="text" select="$nextText"/>
      </xsl:apply-templates>
   </xsl:if>
</xsl:template>
</xsl:stylesheet>