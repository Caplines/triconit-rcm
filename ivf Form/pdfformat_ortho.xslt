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

        .dullGreen {
            background-color: #b6d7a8;
        }

        .text-white {
            color: #fff;
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

        .width-15 {
            width: 15%;
        }
        .width-17 {
            width: 17%;
        }
        .width-32 {
            width: 32%;
        }
		.width-41 {
            width: 41%;
        }
        .width-20 {
            width: 20%;
        }
        .width-21 {
            width: 21%;
        }
        .white-22 {
            width: 22%;
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
        .notCover {
            display: inline-block;
            background: #ffcdc7;
            border-radius: 5px;
            padding: 3px 5px;
            color: #d80000;
        }
        .quaterly {
            display: inline-block;
            background: #e7eaed;
            border-radius: 5px;
            padding: 3px 5px;
            color: #000;
        }
        
    </style>
</head>
<body>
    <form>
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="6" class="tableHeading">
                    Smilepoint Ortho Form
                </td>
            </tr>
            <tr>
                <td colspan="8"></td>
            </tr>
            <tr>
                <td colspan="6" class="tableHeading">Subscriber and Insurance Details</td>
            </tr>
            <tr>
                <td class="width-7">Office Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo1"/></td>
                <td class="width-8">Patient Name </td>
                <td class="width-7"><xsl:value-of select="basicInfo2"/></td>
                <td class="width-8">Policy Holder Name</td>
                <td class="width-7"><xsl:value-of select="basicInfo5"/></td>
                
            </tr>
            <tr class="lightGray">
                <td>Tax ID</td>
                <td><xsl:value-of select="basicInfo4"/></td>
                <td>Patient DOB</td>
                <td class="white"><xsl:if test="string-length(basicInfo6) &gt; 9"><xsl:value-of select="concat(substring(basicInfo6,6,2),'/',substring(basicInfo6,9,2),'/',substring(basicInfo6,1,4))" /></xsl:if></td>
                <td>Policy Holder DOB</td>
                <td><xsl:if test="string-length(basicInfo9) &gt; 9"><xsl:value-of select="concat(substring(basicInfo9,6,2),'/',substring(basicInfo9,9,2),'/',substring(basicInfo9,1,4))" /></xsl:if></td>
               
            </tr>
            <tr>
                <td>ES/Patient ID</td>
                <td><xsl:value-of select="basicInfo21"/></td>
                <td>Insurance Name</td>
                <td><xsl:value-of select="basicInfo3"/></td>
                <td>Provider Name</td>
                <td><xsl:value-of select="basicInfo19"/></td>
              
            </tr>
            <tr class="lightGray">
                <td>Member ID/SSN</td>
                <td><xsl:value-of select="basicInfo16"/></td>
                <td>Insurance Contact</td>
                <td class="white"><xsl:value-of select="basicInfo7"/></td> 
                <td>Provider Network</td>
                <td class="white"><xsl:value-of select="policy3"/></td>
               
            </tr>
            <tr>
                <td>Appointment Date</td>
                <td class=""><xsl:if test="string-length(basicInfo17) &gt; 9"><xsl:value-of select="concat(substring(basicInfo17,6,2),'/',substring(basicInfo17,9,2),'/',substring(basicInfo17,1,4))" /></xsl:if></td>
                <td>Appointment Type</td>
                <td class=""><xsl:value-of select="apptype"/></td>
                <td class="">Payor ID</td>
                <td colspan="1" class=""><xsl:value-of select="basicInfo18"/></td>
            </tr>
			<tr>
                <td>Source</td>
                <td class=""><xsl:value-of select="basicInfo8"/></td>
                <td>Ref #</td>
                <td class=""><xsl:value-of select="basicInfo12"/></td>
                <td class="">Insurance Address</td>
                <td colspan="1" class=""><xsl:value-of select="basicInfo20"/></td>
            </tr>
			<tr>
                <td>Fee Schedule</td>
                <td colspan="2"><xsl:value-of select="policy4"/></td>
                <td colspan="3"></td>
                
            </tr>
        </table>
        <br />
        <br />

        <table class="table" vertical-align="top">
            <tr>
                <td colspan="6" class="tableHeading">Policy Plan Info</td>             
            </tr>

            <tr>
                <td class="width-15">Plan Type</td>
                <td class="width-7"><xsl:value-of select="policy1"/></td>
                <td class="width-10">Effective Date</td>
                <td class="width-7"><xsl:if test="string-length(policy5) &gt; 9"><xsl:value-of select="concat(substring(policy5,6,2),'/',substring(policy5,9,2),'/',substring(policy5,1,4))" /></xsl:if></td>
                <td class="width-13">Term Date</td>
                <td class="width-7"><xsl:if test="string-length(policy2) &gt; 9"><xsl:value-of select="concat(substring(policy2,6,2),'/',substring(policy2,9,2),'/',substring(policy2,1,4))" /></xsl:if></td>
            </tr>
            <tr class="lightGray">
                <td>Group/Employer Name</td>
                <td><xsl:value-of select="basicInfo10"/></td>
                <td>Ortho Max</td>
                <td><xsl:value-of select="ortho2"/></td>
                <td>Individual Deductible</td>
                <td><xsl:value-of select="policy9"/></td>
            </tr>
            <tr>

                <td>Group No</td>
                <td><xsl:value-of select="basicInfo14"/></td>
                <td>Ortho Max Remaining</td>
                <td><xsl:value-of select="ortho5"/></td>
                <td>Individual Ded Remaining</td>
                <td><span class="quaterly"><xsl:value-of select="policy10"/></span></td>
               
            </tr>
            <tr class="lightGray">
                <td>Work In Progress</td>
                <td><span class="notCover"><xsl:value-of select="wip"/></span></td>
                <td>Insurance Billing cycle</td>
                <td><span class="quaterly"><xsl:value-of select="insBillingC"/></span></td>
                <td>Benefit Period</td>
                <td><xsl:value-of select="benefitPeriod"/></td>
                
            </tr>
			
			<tr class="lightGray">
                <td>Dependent Covered Up To age</td>
                <td><xsl:value-of select="policy11"/></td>
                <td>Age Limit For ortho</td>
                <td><xsl:value-of select="ortho3"/></td>
                 <td>Timely Filing Limit</td>
                <td><xsl:value-of select="percentages12"/></td>
                
            </tr>
			
			<tr class="lightGray">
                <td>COB Status</td>
                <td><xsl:value-of select="basicInfo15"/></td>
                <td>Waiting Period</td>
               <xsl:choose>
				     <xsl:when test="waitingPeriod = 'No' ">
					  <td><xsl:value-of select="waitingPeriod"/>		</td>
					 </xsl:when>
				     <xsl:otherwise>
					  <td class="red blackClr"><xsl:value-of select="waitingPeriod"/>		</td>
					 </xsl:otherwise>
				</xsl:choose>
				  <td colspan="2"></td>
                
            </tr>

        </table>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="8" class="tableHeading">Plan Details</td>
            </tr>
            <tr>
                <td colspan="3" class="width-32 rowHeading">Ortho</td>
                <td colspan="2" class="width-20 rowHeading">%</td>
                <td colspan="3" class="width-21 rowHeading">Frequency</td>
            </tr>
			
			 <tr>
                <td colspan="3" class="dullBlue">D8020</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8020"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8020fr"/></td>
            </tr>
            <tr>
                <td colspan="3" class="dullBlue">D8070</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8070"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8070fr"/></td>
            </tr>
            <tr>
                <td colspan="3" class="dullBlue">D8080</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8080"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8080fr"/></td>
            </tr>
            <tr>
                <td colspan="3" class="dullBlue">D8090</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8090"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8090fr"/></td>
            </tr>
			<tr>
                <td colspan="3" class="dullBlue">D8210</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8210"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8210fr"/></td>
            </tr>
			<tr>
                <td colspan="3" class="dullBlue">D8220</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8220"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8220fr"/></td>
            </tr>
			<tr>
                <td colspan="3" class="dullBlue">D8660</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8660"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8660fr"/></td>
            </tr>
            <tr>
                <td colspan="3" class="dullBlue">D8670</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8670"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8670fr"/></td>
            </tr>
            <tr>
                <td colspan="3" class="dullBlue">D8680</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8680"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8680fr"/></td>
            </tr>
            <tr>
                <td colspan="3" class="dullBlue">D8690</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8690"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8690fr"/></td>
            </tr>
			 <tr>
                <td colspan="3" class="dullBlue">D8692</td>
                <td colspan="2" class="dullBlue"><xsl:value-of select="d8692"/></td>
                <td colspan="3" class="dullBlue"><xsl:value-of select="d8692fr"/></td>
            </tr>

        </table>
        <br />
        <br />
        <table class="table" vertical-align="top">
            <tr>
                <td class="dullGreen width-15">Benefits Verified by</td>
                <td colspan="2" class="width-17"><xsl:value-of select="benefits"/></td>
                <td class="dullGreen width-7">Submission Date</td>
                <td class="width-4"><xsl:if test="string-length(date) &gt; 9"><xsl:value-of select="concat(substring(date,6,2),'/',substring(date,9,2),'/',substring(date,1,4))" /></xsl:if></td>
                <td class="width-21 borderNone" colspan="3"></td>
            </tr>
        </table>
		<br />
		<table class="table" vertical-align="top">
            <tr>
               <td class="dullGreen width-15">Comments</td>
               <td colspan="3"  style="width: 63%"><xsl:value-of select="comments"/></td>
			   <td class="width-21 borderNone" colspan="3"></td>
               
               
               
            </tr>
        </table>
    </form>

</body>

</html>
</xsl:template>
</xsl:stylesheet>
