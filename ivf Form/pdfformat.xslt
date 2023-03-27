<?xml version="1.0" encoding="ISO-8859-1"?>
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
		td {
         overflow-wrap: anywhere;
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
				overflow-wrap: anywhere;
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
                <td class="red blackClr"><xsl:value-of select="policy15"/></td>
                <td>Pre- Auth Required</td>
                <td class="red">Medicaid, Adult Medicaid &#38; Medicare Benefits (RD0703)__ASK__</td>
                <td></td>
                <td></td>
                <td>Claims Filing Limit</td>
                <td><xsl:value-of select="basicInfo15"/></td>
                <td class="lightBrown">CRA Required</td>
                <td class="red blackClr"><xsl:if test="policy17 = 'Yes'"><a href="" target="_blank"><xsl:value-of select="basicInfo15"/></a></xsl:if><xsl:if test="policy17 != 'Yes'"><xsl:value-of select="policy17"/></xsl:if></td>
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
                <td class="width-15 red">Waiting Period</td>
                <td colspan="2" class="red width-17"><xsl:value-of select="waitingPeriod4"/>	</td>
                <td class="width-7 borderNone"></td>
                <td class="red width-13">Waiting Period</td>
                <td class="red width-14" colspan="2"><xsl:value-of select="waitingPeriod1"/>	</td>
                <td class="width-7 borderNone"></td>
                <td class="red width-15">Waiting Period</td>
                <td class="red width-12" colspan="2"><xsl:value-of select="waitingPeriod2"/>	</td>
            </tr>
            <tr>
                <td class="red">Subject to Deductible</td>
                <td colspan="2" class="red width-16"><xsl:value-of select="percentages13"/>		</td>
                <td class="borderNone"></td>
                <td class="red">Subject to Deductible</td>
                <td class="red" colspan="2"><xsl:value-of select="percentages2"/>		</td>
                <td class="borderNone"></td>
                <td class="red">Subject to Deductible</td>
                <td class="red" colspan="2"><xsl:value-of select="percentages4"/>		</td>
            </tr>
            <tr>
                <td class="rowHeading">Exams</td>
                <td class="rowHeading width-7">%	</td>
                <td class="rowHeading width-10">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Perio(Non-Surgical)</td>
                <td class="rowHeading width-7">%	</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="red">Missing tooth clause</td>
                <td class="red" colspan="2"><xsl:value-of select="prosthetics1"/>	</td>
            </tr>
            <tr>
                <td class="dullBlue">Exams Shares Frequency</td>
                <td class="dullBlue"><xsl:value-of select="pano2"/></td>
                <td class="dullBlue"></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4341 (SRP)</td>
                <td class="dullYellow align-right"><xsl:value-of select="pano2"/></td>
                <td class="dullYellow" >1x36MoSSSSasdadadassadasdasdasdasd</td>
                <td class="borderNone"></td>
                <td class="red">Replacement Clause</td>
                <td class="red" colspan="2"><xsl:value-of select="prosthetics2"/>	</td>
            </tr>
            <tr>
                <td class="dullBlue">D0120 ( Oral Evaluation))</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2X1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">Quads per Day</td>
                <td class="dullYellow align-right">4</td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Buidup and Crown</td>
                <td class="rowHeading width-7">%	</td>
                <td class="rowHeading">Freq	</td>
            </tr>
            <tr>
                <td class="dullBlue">D0150 (Comprehensive)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2X1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">Days B/W Quads</td>
                <td class="dullYellow">NA</td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2950 (Core Buidup)</td>
                <td class="dullRed width-7 align-right">50</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="dullBlue">D0140 (Emergency)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2X1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4355(FMD)</td>
                <td class="dullYellow align-right">50</td>
                <td class="dullYellow">1xLT</td>
                <td class="borderNone"></td>
                <td class="dullRed">Same day as crown</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="dullBlue">D0160(Limited)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2X1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4346( Gingivitis)</td>
                <td class="dullYellow align-right">100</td>
                <td class="dullYellow">2x1CY</td>
                <td class="borderNone"></td>
                <td class="dullRed">Crown paid prep/seat date</td>
                <td class="dullRed">Seat</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="dullBlue">D0145 (Regular for Child)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2X1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4910(Perio Main)</td>
                <td class="dullYellow align-right">100</td>
                <td class="dullYellow">2x1CY</td>
                <td class="borderNone"></td>
                <td class="dullRed">D2740(Porcelain Crown)</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="rowHeading">X-Rays</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4910 Alt with Prophy (D1110)</td>
                <td class="dullYellow">Yes</td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">D2750 (Noble Metal)</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="dullBlue">D0220 (Periapical)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">No Frequency</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Perio Surgery</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed">Downgrading applicable</td>
                <td class="dullRed">Yes</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D0230 (Additional PA)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">No Frequency</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4381(Arestin)</td>
                <td class="dullYellow"></td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">Downgraded Code</td>
                <td class="dullRed">D2791</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D0210 (FMX)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1x3CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4249(Crown Lengthening)</td>
                <td class="dullYellow"></td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">How many Crown can be done in a year?</td>
                <td class="dullRed">NA</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D0330 (Pano)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1x3CY</td>
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
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1x1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D0431</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullRed">D2930/D2934(Primary Tooth)</td>
                <td class="dullRed">50</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="dullBlue">D0350 (Oral/Facial Photo images)</td>
                <td class="dullBlue align-right">0</td>
                <td class="dullBlue align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4999</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullRed">D2931(Permanent Tooth)</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="dullBlue">Does FMX/Pano share Freq?</td>
                <td class="dullBlue">Yes</td>
                <td class="dullBlue"></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9910</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow align-right">0</td>
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
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullRed">D6245/D6740</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">1x5CY</td>
            </tr>
            <tr>
                <td class="dullBlue">Roll Age</td>
                <td class="dullBlue align-right">13</td>
                <td class="dullBlue"></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4921</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullRed">Downgrading applicable</td>
                <td class="dullRed">Yes</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D1120 (Prophy- Child)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2x1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D4266</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullRed">Which code</td>
                <td class="dullRed">PRED</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D1110 (Prophy- Adult)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2x1CY</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Fillings</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Dentures</td>
                <td class="rowHeading align-right">50</td>
                <td class="rowHeading">1x5CY</td>
            </tr>
            <tr>
                <td class="rowHeading">Flouride</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D2391 ( Composites)</td>
                <td class="dullYellow align-right">80</td>
                <td class="dullYellow">1x12Mo</td>
                <td class="borderNone"></td>
                <td class="dullRed">D5110/D5120 (Complete)</td>
                <td class="dullRed">Yes</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">Age Limit</td>
                <td class="dullBlue align-right">18</td>
                <td class="dullBlue">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">Downgraded to D2140</td>
                <td class="dullYellow">Yes</td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">D5211/D5212/D5213/D5214 (Partial)</td>
                <td class="dullRed">Yes</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D1206 (Fluoride)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1x1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">How many Fillings can be done in a year?</td>
                <td class="dullYellow">NA</td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">D5130/D5140 (Immediate)</td>
                <td class="dullRed">No</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D1208 (Varnish)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1x1CY</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Extractions</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed"><strong>D5820 (Interim)</strong></td>
                <td class="dullRed">No</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="rowHeading">Oral Hygine</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7111, D7140(Minor)</td>
                <td class="dullYellow align-right">50</td>
                <td class="dullYellow">NF</td>
                <td class="borderNone"></td>
                <td class="dullRed">D5225/D5226 (Partial Denture)</td>
                <td class="dullRed">Yes</td>
                <td class="dullRed"></td>
            </tr>
            <tr>
                <td class="dullBlue">D1330</td>
                <td class="dullBlue align-right">0</td>
                <td class="dullBlue align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7210/D7220/D7230/D7240 (Major)</td>
                <td class="dullYellow align-right">50</td>
                <td class="dullYellow">NF</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Implants</td>
                <td class="rowHeading" colspan="2">Yes</td>
            </tr>
            <tr>
                <td class="rowHeading">Sealant</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D7250( Wisdom Tooth)</td>
                <td class="dullYellow align-right">50</td>
                <td class="dullYellow">NF</td>
                <td class="borderNone"></td>
                <td class="dullRed">Coverage %</td>
                <td class="dullRed align-right" colspan="2">50</td>
            </tr>
            <tr>
                <td class="dullBlue">D1351</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1xLT</td>
                <td class="borderNone"></td>
                <td class="dullYellow">
                    How many Extractions can be done in a year?
                </td>
                <td class="dullYellow"></td>
                <td class="dullYellow">NA</td>
                <td class="borderNone"></td>
                <td class="dullRed">Frequency</td>
                <td class="dullRed" colspan="2">1x5CY	</td>
            </tr>
            <tr>
                <td class="dullBlue">Age Limit</td>
                <td class="dullBlue align-right">18</td>
                <td class="dullBlue"></td>
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
                <td class="red blackClr">No</td>
                <td class="dullBlue"></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D3220 (Pulpotomy)</td>
                <td class="dullYellow"></td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">D7310</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">NF</td>
            </tr>
            <tr>
                <td class="dullBlue">Pre Molars</td>
                <td class="red blackClr">No</td>
                <td class="dullBlue"></td>
                <td class="borderNone"></td>
                <td class="dullYellow">D3330 (RCT)</td>
                <td class="dullYellow"></td>
                <td class="dullYellow"></td>
                <td class="borderNone"></td>
                <td class="dullRed">D7311</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">NF</td>
            </tr>
            <tr>
                <td class="dullBlue">Per Molars</td>
                <td class="red blackClr">No</td>
                <td class="dullBlue"></td>
                <td class="borderNone"></td>
                <td class="rowHeading">Labial Veneer</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed">D7953 (Bone Graft)</td>
                <td class="dullRed align-right">50</td>
                <td class="dullRed">NF</td>
            </tr>
            <tr>
                <td class="rowHeading">Space Maintainer</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D2962</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow align-right">0</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">1-3 Teeth/Quads D7311 covered with Ext.	</td>
                <td class="dullRed">Yes</td>
            </tr>
            <tr>
                <td class="dullBlue">D1510/D1516/D1517 (Placement)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1xLT</td>
                <td class="borderNone"></td>
                <td class="rowHeading">Sedation</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">4 Teeth/Quads D7310 covered with Ext.</td>
                <td class="dullRed">Yes</td>
            </tr>
            <tr>
                <td class="dullBlue">D1520/D1526/D1527 (Removal)</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">1xLT</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9230 (Nitrous)</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow">NF</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">D7953 is covered with Implants?	</td>
                <td class="dullRed">Yes</td>
            </tr>
            <tr>
                <td class="rowHeading">Consultation</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9243 (Intravenous)</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow">NF</td>
                <td class="borderNone"></td>
                <td class="rowHeading text-center" colspan="3">Ortho Criteria		</td>
            </tr>
            <tr>
                <td class="dullBlue">D9310</td>
                <td class="dullBlue align-right">100</td>
                <td class="dullBlue">2x1CY</td>
                <td class="borderNone"></td>
                <td class="dullYellow">D9248 (Non-Intravenous)</td>
                <td class="dullYellow align-right">0</td>
                <td class="dullYellow">NF</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">Ortho Coverage % (D8080,D8070,D8090)	</td>
                <td class="dullRed align-right">50</td>
            </tr>
            <tr>
                <td colspan="4" class="borderNone"></td>
                <td class="rowHeading">Night Gaurds</td>
                <td class="rowHeading">%</td>
                <td class="rowHeading">Freq</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">Ortho Maximum</td>
                <td class="dullRed align-right">1000</td>
            </tr>
            <tr>
                <td colspan="4" class="borderNone"></td>
                <td class="dullYellow">D9944/D9945</td>
                <td class="dullYellow align-right">50</td>
                <td class="dullYellow">1x3CY</td>
                <td class="borderNone"></td>
                <td class="dullRed" colspan="2">Ortho Maximum remaining	</td>
                <td class="dullRed align-right">1000</td>
            </tr>
            <tr>
                <td colspan="8" class="borderNone"></td>
                <td class="dullRed" colspan="2">Ortho Ded</td>
                <td class="dullRed align-right">50</td>
            </tr>
        </table>
        <br />
        <br />
        <table  style="border:0px;width:100%" cellpadding="0" cellspacing="0">
				<tr>
					<td vertical-align="top" style="vertical-align:top;">
					<table  style="width:100%;border-right:1px solid #000; page-break:avoid" class="border-btn" cellspacing="0">
                <tbody>
			    <tr class="">
			    <th class="main-heading-11 br1px br_but colourfreq" style="width:20%">History</th>
				<th class="main-heading-11 br1px br_but" style="width:24%">ADA Code</th>
				<th class="main-heading-11 br1px br_but" style="width:28%">Tooth No.</th>
				<th class="main-heading-11 br1px br_but" style="width:28%">DOS</th>
				</tr>
				<xsl:for-each select="hdto1/hisall1">
				<tr class="">
				<td class="br1px"><span class="sub-heading1"></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyCode"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyTooth"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyDos"/></span></td>
				</tr>
				</xsl:for-each>
             </tbody>
            </table></td>
					
			<td vertical-align="top" style="vertical-align:top;">
			<table style="width:100%;border-right:1px solid #000;  margin-left:-1px;"  class="border-btn" cellspacing="0" >
             <tbody>
			    <tr>
			    <th class="main-heading-11 br1px br_but" style="width:25%">ADA Code</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">Tooth No.</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">DOS</th>
				</tr>
				<xsl:for-each select="hdto2/hisall2">
				<tr>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyCode"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyTooth"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyDos"/></span></td>
				</tr>
				</xsl:for-each>
				
             </tbody>	
            </table>
					</td>
					<td vertical-align="top" style="vertical-align:top;">
				<table style="width:100%;border-right:1px solid #000; margin-left:-1px;" class="border-btn last-r-border" cellspacing="0">
             <tbody>
			    <tr>
			    <th class="main-heading-11 br1px br_but" style="width:25%">ADA Code</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">Tooth No.</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">DOS</th>
				</tr>
				<xsl:for-each select="hdto3/hisall3">
				<tr>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyCode"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyTooth"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyDos"/></span></td>
				</tr>
				</xsl:for-each>
             </tbody>
            </table>
					</td>
				</tr>
			</table>
			<table>
            <tr>
                <td class="borderNone" colspan="11"></td>
            </tr>
            <tr>
                <td class="dullGreen">Benefits Verified by</td>
                <td class="lightGray"><xsl:value-of select="benefits"/></td>
                <td class="dullGreen">Submission Date</td>
                <td class="lightGray"><xsl:if test="string-length(date) &gt; 9"><xsl:value-of select="concat(substring(date,9,2),'/',substring(date,6,2),'/',substring(date,1,4))" /></xsl:if></td>
                <td class="borderNone" colspan="7"></td>
            </tr>
        </table>
		
    </form>

</body>
</html>
</xsl:template>
</xsl:stylesheet>