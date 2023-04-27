<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
   version="1.0" >
	<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
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
            background-color: #a2c4c9;
            color: #000;
        }

        .dullYellow {
            background-color: #ffe599;
            color: #000;
        }

        .dullRed {
            background-color: #dd7e6b;
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
    </style>
</head>
<body>
    <form>
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="10" class="tableHeading">Subscriber and Insurance Details</td>
                <td class="width-5 borderNone"></td>
            </tr>
            <tr>
                <td class="width-15">Office Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo1"/></td>
                <td class="width-10">Patient Name </td>
                <td class="width-7"><xsl:value-of select="basicInfo21"/></td>
                <td class="width-13">Insurance Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo3"/></td>
                <td class="width-7">Provider Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo19"/></td>
                <td class="width-15">Secondary Provider Name, if Any</td>
                <td class="width-7"><xsl:value-of select="secProviderName"/></td>
                <td class="width-5 borderNone"></td>
            </tr>
            <tr class="lightGray">
                <td>Tax ID</td>
                <td><xsl:value-of select="basicInfo4"/></td>
                <td>Patient DOB</td>
                <td class="white"><xsl:if test="string-length(basicInfo6) &gt; 9"><xsl:value-of select="concat(substring(basicInfo6,9,2),'/',substring(basicInfo6,6,2),'/',substring(basicInfo6,1,4))" /></xsl:if></td>
                <td>Insurance Contact</td>
                <td><xsl:value-of select="basicInfo7"/></td>
                <td>Provider Network</td>
                <td class="yellow"><xsl:value-of select="policy3"/></td>
                <td>Secondary Provider Network</td>
                <td><xsl:value-of select="secProvNetwork"/></td>
                <td class="borderNone"></td>
            </tr>
            <tr>
                <td>ES/Patient ID</td>
                <td><xsl:value-of select="basicInfo21"/></td>
                <td>Policy Holder Name</td>
                <td><xsl:value-of select="basicInfo5"/></td>
                <td>Appointment Type</td>
                <td><xsl:value-of select="basicInfo11"/></td>
                <td>Does Plan have OON Benefits?</td>
                <td class="yellow"><xsl:value-of select="oonbenfits"/></td>
                <td>Patient Assigned To Office</td>
                <td class="red blackClr"><xsl:value-of select="yesNoAssignToffice"/></td>
                <td class="borderNone"></td>
            </tr>
            <tr class="lightGray">
                <td>Member ID/SSN</td>
                <td><xsl:value-of select="basicInfo16"/></td>
                <td>Policy Holder DOB</td>
                <td class="white"><xsl:if test="string-length(basicInfo9) &gt; 9"><xsl:value-of select="concat(substring(basicInfo9,9,2),'/',substring(basicInfo9,6,2),'/',substring(basicInfo9,1,4))" /></xsl:if></td>
                <td>Appointment Date</td>
                <td class="white"><xsl:if test="string-length(basicInfo17) &gt; 9"><xsl:value-of select="concat(substring(basicInfo17,9,2),'/',substring(basicInfo17,6,2),'/',substring(basicInfo17,1,4))" /></xsl:if></td>
                <td>Source</td>
                <td><xsl:value-of select="basicInfo8"/></td>
                <td>Ref #</td>
                <td><xsl:value-of select="basicInfo12"/></td>
                <td class="borderNone"></td>
            </tr>

        </table>
        <br />
        <br />

        <table class="table" vertical-align="top">
            <tr>
                <td colspan="10" class="tableHeading">Policy Plan Info</td>
                <td class="width-5 borderNone"></td>               
            </tr>

            <tr>
                <td class="width-15">Plan Type</td>
                <td class="width-7"><xsl:value-of select="policy1"/></td>
                <td class="width-10">Group/Employer Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo10"/></td>
                <td class="width-13">Group No</td>
                <td class="width-7"><xsl:value-of select="basicInfo14"/></td>
                <td class="width-7">Effective Date</td>
                <td class="width-7"><xsl:if test="string-length(policy5) &gt; 9"><xsl:value-of select="concat(substring(policy5,9,2),'/',substring(policy5,6,2),'/',substring(policy5,1,4))" /></xsl:if></td>
                <td class="width-15">Term Date</td>
                <td class="width-7"><xsl:if test="string-length(policy2) &gt; 9"><xsl:value-of select="concat(substring(policy2,9,2),'/',substring(policy2,6,2),'/',substring(policy2,1,4))" /></xsl:if></td>
                <td class="width-5 borderNone"></td>
            </tr>
            <tr class="lightGray">
                <td>Annual Max</td>
                <td><xsl:value-of select="policy7"/></td>
                <td>Individual Deductible</td>
                <td><xsl:value-of select="policy9"/></td>
                <td>Fee Schedule</td>
                <td><xsl:value-of select="policy4"/></td>
                <td>Allowed Amount of D0120</td>
                <td><xsl:value-of select="policy18"/></td>
                <td>Benefit Period</td>
                <td class="white"><xsl:value-of select="policy6"/></td>
                <td class="borderNone"></td>
            </tr>
            <tr>

                <td>Annual Max Remaining</td>
                <td><xsl:value-of select="policy8"/></td>
                <td>Individual Ded Remaining</td>
                <td><xsl:value-of select="policy10"/></td>
                <td>Coverage Book</td>
                <td><xsl:value-of select="policy16"/></td>
                <td>Allowed Amount of D2391</td>
                <td><xsl:value-of select="policy19"/></td>
                <td>COB Status</td>
                <td><xsl:value-of select="basicInfo15"/></td>
                <td class="borderNone"></td>
            </tr>
            <tr class="lightGray">
                <td>Assignment of benefits Accepted?</td>
                
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
						  <td class="red"><a href="https://docs.google.com/spreadsheets/d/13C7ph9Hal1mDdU5nioWWX2ndof6Ls2IYbVw03uRDiVM/edit#gid=2067143248" style="text-decoration:none" traget="_blank" >Refer Medicaid &#38; Medicare Benefits(RD0703)</a></td>
						  </xsl:when>
				          <xsl:otherwise>
						   <td class="">No</td>
						  </xsl:otherwise>
					 </xsl:choose>
					
					 </xsl:otherwise>
				</xsl:choose>
				
                
                <td></td>
                <td></td>
                <td>Claims Filing Limit</td>
                <td><xsl:value-of select="percentages12"/></td>
                <td class="lightBrown">CRA Required</td>
				<xsl:choose>
					      <xsl:when test="policy17 = 'Refer to CRA Info for Medicaid Plans(RD2405)'">
						  <td class="red blackClr"><a href="https://docs.google.com/spreadsheets/d/1kjq3Q2r3eRoC0Ygi9ODXczFfJPExBmo6mbeQYQLlooo/edit#gid=0" style="text-decoration:none" traget="_blank" >CRA Information for Medicaid Plans(RD2405)</a></td>
						  </xsl:when>
				          <xsl:otherwise>
						   <td class="">No</td>
				          </xsl:otherwise>
				</xsl:choose>		  
                <td class="borderNone"></td>
            </tr>

        </table>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr class="lightGray">
                <td class="width-15 red tableHeading text-left">Special Remarks for Office and LC3</td>
                <td colspan="10"><xsl:value-of select="comments"/></td>
            </tr>
        </table>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="11" class="tableHeading">Plan Details</td>
            </tr>

            <tr>
                <td colspan="3" class="sub-heading dullBlue">Preventive Services</td>
                <td class="width-7 borderNone"></td>
                <td colspan="3" class="sub-heading dullYellow">Basic Services</td>
                <td class="width-7  borderNone"></td>
                <td colspan="3" class="sub-heading dullRed">Major Services</td>
            </tr>
            <tr>
			
			    <xsl:choose>
				     <xsl:when test="waitingPeriod4 = 'No' ">
					 <td class="width-15">Waiting Period</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red width-15">Waiting Period</td>
					 </xsl:otherwise>
				</xsl:choose>	
                
                <xsl:choose>
				     <xsl:when test="waitingPeriod4 = 'No' ">
					  <td colspan="2" class="width-17"><xsl:value-of select="waitingPeriod4"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-17"><xsl:value-of select="waitingPeriod4"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
				
                <td class="width-7 borderNone"></td>
				<xsl:choose>
				     <xsl:when test="waitingPeriod1 = 'No' ">
					 <td class="width-13">Waiting Period</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red width-13">Waiting Period</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="waitingPeriod1 = 'No' ">
					  <td colspan="2" class="width-14"><xsl:value-of select="waitingPeriod1"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-14"><xsl:value-of select="waitingPeriod1"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
                <td class="width-7 borderNone"></td>
				<xsl:choose>
				     <xsl:when test="waitingPeriod2 = 'No' ">
					 <td class="width-15">Waiting Period</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red width-15">Waiting Period</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="waitingPeriod2 = 'No' ">
					  <td colspan="2" class="width-12"><xsl:value-of select="waitingPeriod2"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-12"><xsl:value-of select="waitingPeriod2"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
			   <xsl:choose>
				     <xsl:when test="percentages13 = 'No' ">
					 <td class="">Subject to Deductible</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red">Subject to Deductible</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="percentages13 = 'No' ">
					  <td colspan="2" class="width-16"><xsl:value-of select="percentages13"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-16"><xsl:value-of select="percentages13"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>	 
			   
                <td class="borderNone"></td>
                <xsl:choose>
				     <xsl:when test="percentages2 = 'No' ">
					 <td class="">Subject to Deductible</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red">Subject to Deductible</td>
					 </xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
				     <xsl:when test="percentages2 = 'No' ">
					  <td colspan="2" class="width-16"><xsl:value-of select="percentages2"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-16"><xsl:value-of select="percentages2"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
                <td class="borderNone"></td>
                <xsl:choose>
				     <xsl:when test="percentages4 = 'No' ">
					 <td class="">Subject to Deductible</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red">Subject to Deductible</td>
					 </xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
				     <xsl:when test="percentages4 = 'No' ">
					  <td colspan="2" class="width-16"><xsl:value-of select="percentages4"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red width-16"><xsl:value-of select="percentages4"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
                <td class="rowHeading">Exams</td>
                <td class="rowHeading width-7">%&#160;&#160;&#160;&#160;&#160;	</td>
                <td class="rowHeading width-10">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Perio(Non-Surgical)</td>
                <td class="rowHeading width-7">%	</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
				<xsl:choose>
				     <xsl:when test="prosthetics1 = 'No' ">
					 <td class="">Missing tooth clause</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red">Missing tooth clause</td>
					 </xsl:otherwise>
				</xsl:choose>
                
				<xsl:choose>
				     <xsl:when test="prosthetics1 = 'No' ">
					  <td colspan="2"><xsl:value-of select="prosthetics1"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red"><xsl:value-of select="prosthetics1"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
                <td class="dullBlue">Exams Shares Frequency</td>
                <td class="dullBlue" colspan="2"><xsl:value-of select="pano2"/></td>
               
                <td class="borderNone"></td>
                <td class="dullYellow">D4341 (SRP)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perio1"/></td>
                <td class="dullYellow"><xsl:value-of select="perio2"/></td>
                <td class="borderNone"></td>
				<xsl:choose>
				     <xsl:when test="prosthetics2 = 'No' ">
					 <td class="">Replacement Clause</td>
					 </xsl:when>
				     <xsl:otherwise>
					 <td class="red">Replacement Clause</td>
					 </xsl:otherwise>
				</xsl:choose>
               
				<xsl:choose>
				     <xsl:when test="prosthetics2 = 'No' ">
					  <td colspan="2"><xsl:value-of select="prosthetics2"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td colspan="2" class="red"><xsl:value-of select="prosthetics2"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
                
            </tr>
            <tr>
                <td class="dullBlue">D0120 ( Oral Evaluation))</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0120"/></td>
                <td class="dullBlue"><xsl:value-of select="exams1"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">Quads per Day</td>
                <td class="dullYellow align-right" colspan="2"><xsl:value-of select="perio3"/></td>
               
                <td class="borderNone"></td>
                <td class="rowHeading">Buidup and Crown</td>
                <td class="rowHeading width-7">%	</td>
                <td class="rowHeading">Freq	</td>
            </tr>
            <tr>
                <td class="dullBlue">D0150 (Comprehensive)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0150"/></td>
                <td class="dullBlue"><xsl:value-of select="exams4"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">Days B/W Quads</td>
                <td class="dullYellow" colspan="2"><xsl:value-of select="perio4"/></td>
                
                <td class="borderNone"></td>
                <td class="dullRed">D2950 (Core Buidup)</td>
                <td class="dullRed width-7 align-right"><xsl:value-of select="posterior10"/></td>
                <td class="dullRed"><xsl:value-of select="posterior11"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D0140 (Emergency)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0140"/></td>
                <td class="dullBlue"><xsl:value-of select="exams2"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4355(FMD)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioMnt4"/></td>
                <td class="dullYellow"><xsl:value-of select="perioMnt5"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Same day as crown</td>
                <td class="dullRed align-right" colspan="2"><xsl:value-of select="posterior12"/></td>
               
            </tr>
            <tr>
                <td class="dullBlue">D0160(Limited)</td>
                <td class="dullBlue align-right"><xsl:value-of select="pedo1"/></td>
                <td class="dullBlue"><xsl:value-of select="d0160Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4346( Gingivitis)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioMnt6"/></td>
                <td class="dullYellow"><xsl:value-of select="perioMnt7"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Crown paid prep/seat date</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="prosthetics3"/></td>
               
            </tr>
            <tr>
                <td class="dullBlue">D0145 (Regular for Child)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0145"/></td>
                <td class="dullBlue"><xsl:value-of select="exams3"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4910(Perio Main)</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioMnt1"/></td>
                <td class="dullYellow"><xsl:value-of select="perioMnt2"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2740(Porcelain Crown)</td>
                <td class="dullRed align-right"><xsl:value-of select="posterior4"/></td>
                <td class="dullRed"><xsl:value-of select="posterior5"/></td>
            </tr>
            <tr>
                <td class="rowHeading">X-Rays</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4910 Alt with Prophy (D1110)</td>
                <td class="dullYellow" colspan="2"><xsl:value-of select="perioMnt3"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2750 (Noble Metal)</td>
                <td class="dullRed align-right"><xsl:value-of select="d2750"/></td>
                <td class="dullRed"><xsl:value-of select="d2750fr"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D0220 (Periapical)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0220"/></td>
                <td class="dullBlue"><xsl:value-of select="d0220Freq"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Perio Surgery</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed">Downgrading applicable</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="posterior6"/></td>
               
            </tr>
            <tr>
                <td class="dullBlue">D0230 (Additional PA)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0230"/></td>
                <td class="dullBlue"><xsl:value-of select="xrays3"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4381(Arestin)</td>
                <td class="dullYellow"><xsl:value-of select="d4381"/></td>
                <td class="dullYellow"><xsl:value-of select="d4381Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Downgraded Code</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="posterior17"/></td>
               
            </tr>
            <tr>
                <td class="dullBlue">D0210 (FMX)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0210"/></td>
                <td class="dullBlue"><xsl:value-of select="d0210Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4249(Crown Lengthening)</td>
                <td class="dullYellow"><xsl:value-of select="oral1"/></td>
                <td class="dullYellow"><xsl:value-of select="oral2"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">How many Crown can be done in a year?</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="crn1"/></td>
               
            </tr>
            <tr>
                <td class="dullBlue">D0330 (Pano)</td>
                <td class="dullBlue align-right"><xsl:value-of select="pano1"/></td>
                <td class="dullBlue"><xsl:value-of select="d0330Freq"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">VAPs</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">SSC Crown</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
            </tr>
            <tr>
                <td class="dullBlue">D0272/D0274 (BWX)</td>
                <td class="dullBlue align-right"><xsl:value-of select="bwx"/></td>
                <td class="dullBlue"><xsl:value-of select="bwxFreq"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D0431</td>
                <td class="dullYellow align-right"><xsl:value-of select="d0431"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d0431fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2930/D2934(Primary Tooth)</td>
                <td class="dullRed"><xsl:value-of select="d2930"/></td>
                <td class="dullRed"><xsl:value-of select="ssc1"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D0350 (Oral/Facial Photo images)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d0350"/></td>
                <td class="dullBlue align-right"><xsl:value-of select="d0350Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4999</td>
                <td class="dullYellow align-right"><xsl:value-of select="d4999"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d4999fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2931(Permanent Tooth)</td>
                <td class="dullRed align-right"><xsl:value-of select="d2931"/></td>
                <td class="dullRed"><xsl:value-of select="ssc2"/></td>
            </tr>
            <tr>
                <td class="dullBlue">Does FMX/Pano share Freq?</td>
                <td class="dullBlue"  colspan="2"><xsl:value-of select="pano2"/></td>
               
                <td class="borderNone"></td>
                <td class="dullYellow">D9910</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioD9910"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d9910Frequency"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Bridge</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
            </tr>
            <tr>
                <td class="rowHeading">Prophylaxis</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9630</td>
                <td class="dullYellow align-right"><xsl:value-of select="d9630"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d9630fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D6245/D6740</td>
                <td class="dullRed align-right"><xsl:value-of select="bridges1"/></td>
                <td class="dullRed"><xsl:value-of select="bridges2"/></td>
            </tr>
            <tr>
                <td class="dullBlue">Roll Age</td>
                <td class="dullBlue align-right" colspan="2"><xsl:value-of select="fluroide2"/></td>
                
                <td class="borderNone"></td>
                <td class="dullYellow">D4921</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioD4921"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d4921Frequency"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Downgrading applicable</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="cdowngrade"/></td>
                
            </tr>
            <tr>
                <td class="dullBlue">D1120 (Prophy- Child)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1120"/></td>
                <td class="dullBlue"><xsl:value-of select="prophy2"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4266</td>
                <td class="dullYellow align-right"><xsl:value-of select="perioD4266"/></td>
                <td class="dullYellow align-right"><xsl:value-of select="d4266Frequency"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Which code</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="bWhichCode"/></td>
               
            </tr>
            <tr>
                <td class="dullBlue">D1110 (Prophy- Adult)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1110"/></td>
                <td class="dullBlue"><xsl:value-of select="prophy1"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Fillings</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Dentures</td>
                <td class="rowHeading align-right"><xsl:value-of select="den5225"/></td>
                <td class="rowHeading"><xsl:value-of select="denf5225"/></td>
            </tr>
            <tr>
                <td class="rowHeading">Flouride</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D2391 ( Composites)</td>
                <td class="dullYellow align-right"><xsl:value-of select="posterior1"/></td>
                <td class="dullYellow"><xsl:value-of select="posterior2"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D5110/D5120 (Complete)</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="d5110_20"/></td>
                
            </tr>
            <tr>
                <td class="dullBlue">Age Limit</td>
                <td class="dullBlue align-right"  colspan="2"><xsl:value-of select="fluroide2"/></td>
                
                <td class="borderNone"></td>
                <td class="dullYellow">Downgraded to D2140</td>
                <td class="dullYellow"  colspan="2"><xsl:value-of select="posterior3"/></td>
                
                <td class="borderNone"></td>
                <td class="dullRed">D5211/D5212/<br/>D5213/D5214 (Partial)</td>
                <td class="dullRed"  colspan="2"><xsl:value-of select="d5111_12_13_14"/></td>
                
            </tr>
            <tr>
                <td class="dullBlue">D1206 (Fluoride)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1206"/></td>
                <td class="dullBlue"><xsl:value-of select="fluroide1"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">How many Fillings can be done in a year?</td>
                <td class="dullYellow"  colspan="2"><xsl:value-of select="fill1"/></td>
                <td class="borderNone"></td>
               
                <td class="dullRed">D5130/D5140<br/> (Immediate)</td>
                <td class="dullRed"  colspan="2"><xsl:value-of select="d5130_40"/></td>
               
            </tr>
            <tr>
                <td class="dullBlue">D1208 (Varnish)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1208"/></td>
                <td class="dullBlue"><xsl:value-of select="fluroide3"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Extractions</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed"><strong>D5820 (Interim)</strong></td>
                <td class="dullRed"  colspan="2"><xsl:value-of select="d5810_c"/></td>
                
            </tr>
            <tr>
                <td class="rowHeading">Oral Hygine</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7111, D7140(Minor)</td>
                <td class="dullYellow align-right"><xsl:value-of select="extractions1"/></td>
                <td class="dullYellow"><xsl:value-of select="extractions1fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D5225/D5226 (Partial Denture)</td>
                <td class="dullRed"  colspan="2"><xsl:value-of select="d5225_26_c"/></td>
                
            </tr>
            <tr>
                <td class="dullBlue">D1330</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1330"/></td>
                <td class="dullBlue align-right"><xsl:value-of select="d1330Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7210/D7220/D7230<br/>/D7240 (Major)</td>
                <td class="dullYellow align-right"><xsl:value-of select="extractions2"/></td>
                <td class="dullYellow"><xsl:value-of select="extractions2fr"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Implants</td>
                <td class="rowHeading" colspan="2"><xsl:value-of select="implantsC"/></td>
            </tr>
            <tr>
                <td class="rowHeading">Sealant</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7250( Wisdom Tooth)</td>
                <td class="dullYellow align-right"><xsl:value-of select="d7250"/></td>
                <td class="dullYellow"><xsl:value-of select="d7250fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Coverage %</td>
                <td class="dullRed align-right" colspan="2"><xsl:value-of select="implants1"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D1351</td>
                <td class="dullBlue align-right"><xsl:value-of select="sealantsD"/></td>
                <td class="dullBlue"><xsl:value-of select="sealants1"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">
                    How many Extractions can be done in a year?
                </td>
                <td class="dullYellow"></td>
                <td class="dullYellow"><xsl:value-of select="extr1"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">Frequency</td>
                <td class="dullRed" colspan="2"><xsl:value-of select="implants5"/>	</td>
            </tr>
            <tr>
                <td class="dullBlue">Age Limit</td>
                <td class="dullBlue align-right" colspan="2"><xsl:value-of select="sealants2"/></td>
                
                <td class="borderNone"></td>
                <td class="rowHeading">Endodontics</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Oral Surgery</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
            </tr>
            <tr>
                <td class="dullBlue">Primary Molars</td>
                <td class="red blackClr" colspan="2"><xsl:value-of select="sealants3"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D3220 (Pulpotomy)</td>
                <td class="dullYellow"><xsl:value-of select="d3220"/></td>
                <td class="dullYellow"><xsl:value-of select="d3220Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D7310</td>
                <td class="dullRed align-right"><xsl:value-of select="d7310"/></td>
                <td class="dullRed"><xsl:value-of select="oral6"/></td>
            </tr>
            <tr>
                <td class="dullBlue">Pre Molars</td>
                <td class="red blackClr" colspan="2"><xsl:value-of select="sealants4"/></td>
                
                <td class="borderNone"></td>
                <td class="dullYellow">D3330 (RCT)</td>
                <td class="dullYellow"><xsl:value-of select="d3330"/></td>
                <td class="dullYellow"><xsl:value-of select="d3330Freq"/></td>
                <td class="borderNone"></td>
                <td class="dullRed">D7311</td>
                <td class="dullRed align-right"><xsl:value-of select="d7311"/></td>
                <td class="dullRed"><xsl:value-of select="oral4"/></td>
            </tr>
            <tr>
                <td class="dullBlue">Per Molars</td>
                <td class="red blackClr" colspan="2"><xsl:value-of select="sealants5"/></td>
               
                <td class="borderNone"></td>
                <td class="rowHeading">Labial Veneer</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed">D7953 (Bone Graft)</td>
                <td class="dullRed align-right"><xsl:value-of select="d7953"/></td>
                <td class="dullRed"><xsl:value-of select="dentures6"/></td>
            </tr>
            <tr>
                <td class="rowHeading">Space Maintainer</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D2962</td>
                <td class="dullYellow align-right"><xsl:value-of select="d2962"/></td>
                <td class="dullYellow"><xsl:value-of select="d2962fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">1-3 Teeth/Quads D7311<br/> covered with Ext.	</td>
                <td class="dullRed"><xsl:value-of select="oral3"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D1510/D1516/D1517 (Placement)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1510"/></td>
                <td class="dullBlue"><xsl:value-of select="d1510Freq"/></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Sedation</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">4 Teeth/Quads D7310<br/> covered with Ext.</td>
                <td class="dullRed"><xsl:value-of select="oral5"/></td>
            </tr>
            <tr>
                <td class="dullBlue">D1520/D1526/D1527 (Removal)</td>
                <td class="dullBlue align-right"><xsl:value-of select="d1520_26_27"/></td>
                <td class="dullBlue"><xsl:value-of select="d1520_26_27_fr"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9230 (Nitrous)</td>
                <td class="dullYellow align-right"><xsl:value-of select="sedations1"/></td>
                <td class="dullYellow"><xsl:value-of select="sedations1fr"/></td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">D7953 is covered with Implants?	</td>
                <td class="dullRed"><xsl:value-of select="dentures5"/></td>
            </tr>
            <tr>
                <td class="rowHeading">Consultation</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9243 (Intravenous)</td>
                <td class="dullYellow align-right"><xsl:value-of select="sedations2"/></td>
                <td class="dullYellow"><xsl:value-of select="sedations2fr"/></td>
                <td class="borderNone"></td>
				
				<td class="dullRed" colspan="2">D7953 is covered with Extractions? </td>
                <td class="dullRed"><xsl:value-of select="d7953Extraction"/></td>
				
				
               
            </tr>
            <tr>
                <td class="dullBlue">D9310</td>
                <td class="dullBlue align-right"><xsl:value-of select="posterior8"/></td>
                <td class="dullBlue"><xsl:value-of select="posterior9"/></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9248 (Non-Intravenous)</td>
                <td class="dullYellow align-right"><xsl:value-of select="sedations3"/></td>
                <td class="dullYellow"><xsl:value-of select="sedations3fr"/></td>
                <td class="borderNone"></td>
				
				 <td class="rowHeading text-center" colspan="3">Ortho Criteria		</td>
				 
				 
            </tr>
            <tr>
                <td colspan="4" class="borderNone"></td>
                <td class="rowHeading">Night Gaurds</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
				
                <td class="dullRed" colspan="2">Ortho Coverage % (D8080,D8070,D8090)	</td>
                <td class="dullRed align-right"><xsl:value-of select="ortho1"/></td>
                
            </tr>
            <tr>
                <td colspan="4" class="borderNone"></td>
                <td class="dullYellow">D9944/D9945</td>
                <td class="dullYellow align-right"><xsl:value-of select="posterior7"/></td>
                <td class="dullYellow"><xsl:value-of select="posterior19"/></td>
                <td class="borderNone"></td>
				<td class="dullRed" colspan="2">Ortho Maximum</td>
                <td class="dullRed align-right"><xsl:value-of select="ortho2"/></td>
               
            </tr>
            <tr>
                <td colspan="8" class="borderNone"></td>
				 <td class="dullRed" colspan="2">Ortho Maximum remaining	</td>
                <td class="dullRed align-right"><xsl:value-of select="ortho5"/></td>
				
              
            </tr>
			<tr>
                <td colspan="8" class="borderNone"></td>
			
                <td class="dullRed" colspan="2">Ortho Ded</td>
                <td class="dullRed align-right"><xsl:value-of select="ortho4"/></td>
            </tr>
        </table>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="11" class="tableHeading">History</td>
            </tr>
            <tr>
                <td colspan="11" class="innerTableBox">
                    <table class="innerTable" vertical-align="top">
                        <tr>
                            <td class="rowHeading width-15">ADA Code</td>
                            <td class="rowHeading width-7">Tooth No</td>
                            <td class="rowHeading width-10">DOS</td>
                        </tr>
						<xsl:for-each select="hdto1/hisall1">
						 <tr>
                            <td class="dullBlue"><xsl:value-of select="historyCode"/></td>
                            <td class="dullBlue"><xsl:value-of select="historyTooth"/></td>
                            <td class="dullBlue align-right"><xsl:if test="string-length(historyDos) &gt; 9"><xsl:value-of select="concat(substring(historyDos,9,2),'/',substring(historyDos,6,2),'/',substring(historyDos,1,4))" /></xsl:if></td>
                        </tr>
						</xsl:for-each>
                       
                    </table>

                    <table class="innerTable" vertical-align="top">
                        <tr>
                            <td class="rowHeading width-15">ADA Code</td>
                            <td class="rowHeading width-7">Tooth No</td>
                            <td class="rowHeading width-10">DOS</td>
                        </tr>
                       
                       <xsl:for-each select="hdto2/hisall2">
						 <tr>
                            <td class="dullBlue"><xsl:value-of select="historyCode"/></td>
                            <td class="dullBlue"><xsl:value-of select="historyTooth"/></td>
                            <td class="dullBlue align-right"><xsl:if test="string-length(historyDos) &gt; 9"><xsl:value-of select="concat(substring(historyDos,9,2),'/',substring(historyDos,6,2),'/',substring(historyDos,1,4))" /></xsl:if></td>
                        </tr>
						</xsl:for-each>
                    </table>
                    <table class="innerTable" vertical-align="top">
                        <tr>
                            <td class="rowHeading width-15">ADA Code</td>
                            <td class="rowHeading width-7">Tooth No</td>
                            <td class="rowHeading width-10">DOS</td>
                        </tr>
                        <xsl:for-each select="hdto3/hisall3">
						 <tr>
                            <td class="dullBlue"><xsl:value-of select="historyCode"/></td>
                            <td class="dullBlue"><xsl:value-of select="historyTooth"/></td>
                            <td class="dullBlue align-right"><xsl:if test="string-length(historyDos) &gt; 9"><xsl:value-of select="concat(substring(historyDos,9,2),'/',substring(historyDos,6,2),'/',substring(historyDos,1,4))" /></xsl:if></td>
                        </tr>
						</xsl:for-each>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="borderNone" colspan="11"></td>
            </tr>
            <tr>
                <td class="dullGreen width-15">Benefits Verified by</td>
                <td class="lightGray width-15"><xsl:value-of select="benefits"/></td>
                <td class="dullGreen width-15">Submission Date</td>
                <td class="lightGray width-15"><xsl:if test="string-length(date) &gt; 9"><xsl:value-of select="concat(substring(date,9,2),'/',substring(date,6,2),'/',substring(date,1,4))" /></xsl:if></td>
                <td class="borderNone" colspan="7"></td>
            </tr>
        </table>
		
    </form>

</body>
</html>
</xsl:template>
</xsl:stylesheet>