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
                    .rowHeading {
            background-color: #666666;
            color: #fff;
            font-size: 14px;
        }
                    .historyBackground{

                 background-color:  #FFFFFF

            }
         @page{
            size: landscape;
            }
        
    </style>
</head>
<body>
    <form>
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="12" class="tableHeading">
                    Oral Surgery IV Form
                </td>
            </tr>
            <tr>
                <td colspan="12" class="tableHeading">Subscriber and Insurance Details</td>
            </tr>
            <tr>
                <td class="width-10">Office Name</td>
                <td class="width-8"><xsl:value-of select="basicInfo1"/></td>
                <td class="width-8">Patient ID</td>
                <td class="width-8"><xsl:value-of select="basicInfo21"/></td>
                <td class="width-10">Subscriber's Name</td>
                <td class="width-8"><xsl:value-of select="basicInfo5"/></td>
                <td class="width-8">Subscriber's DOB </td>
                <td class="width-8"><xsl:value-of select="basicInfo9"/></td>
                <td class="width-8">Patient's Name</td>
                <td class="width-8"><xsl:value-of select="basicInfo2"/></td>
                <td class="width-8">Patient's DOB</td>
                <td class="width-8"><xsl:value-of select="basicInfo6"/></td>
            </tr>
            <tr>
                <td>CSR Name </td>
                <td><xsl:value-of select="basicInfo8"/></td>
                <td>Ref# </td>
                <td><xsl:value-of select="basicInfo12"/></td>
                <td>Provider's Name</td>
                <td><xsl:value-of select="basicInfo19"/></td>
                <td>Tax ID </td>
                <td><xsl:value-of select="basicInfo4"/></td>
                <td>NPI</td>
                <td><xsl:value-of select="npi"/></td>
                <td>Licence#</td>
                <td><xsl:value-of select="licence"/></td>
            </tr>
            <tr>
                <td>Plan Type</td>
                <td><xsl:value-of select="osPlanType"/></td>
                <td>Network</td>
                <td><xsl:value-of select="policy3"/></td>
                <td>SSN#</td>
                <td><xsl:value-of select="basicInfo13"/></td>
                <td>Member ID</td>
                <td><xsl:value-of select="basicInfo16"/></td>
                <td>Effective Date</td>
                <td><xsl:value-of select="policy5"/></td>
                <td>CY/FY</td>
                <td><xsl:value-of select="policy6"/></td>
            </tr>
            <tr>
                <td>Employer's Name</td>
                <td><xsl:value-of select="basicInfo10"/></td>
                <td>Group Number</td>
                <td><xsl:value-of select="basicInfo14"/></td>
                <td>Insurance Name</td>
                <td ><xsl:value-of select="basicInfo3"/></td>
                <td>Insurance Telephone</td>
                <td><xsl:value-of select="basicInfo7"/></td>
                <td>Insurance address</td>
                <td><xsl:value-of select="basicInfo20"/></td>
                <td colspan="2"></td>
            </tr>
            <tr>
                <td>Fee Schedule</td>
                <td ><xsl:value-of select="policy4"/></td>
                <td>Dependents covered upto age?</td>
                <td><xsl:value-of select="policy11"/></td>
                <td>Coordination of benefits</td>
                <td><xsl:value-of select="corrdOfBenefits"/></td>
                <td>Payor Id</td>
                <td><xsl:value-of select="basicInfo18"/></td>
                <td colspan="4"></td>
            </tr>
            <tr>
                <td>What is allowed amount for D7210</td>
                <td><xsl:value-of select="whatAmountD7210"/></td>
                <td>Maximum $</td>
                <td><xsl:value-of select="policy7"/></td>
                <td>Deductible</td>
                <td><xsl:value-of select="policy9"/></td>
                <td> Appointment Date</td>
                <td><xsl:value-of select="basicInfo17"/></td>
                <td colspan="4"></td>
            </tr>
            <tr>
                <td>What is allowed amount for D7240</td>
                <td><xsl:value-of select="allowAmountD7240"/></td>
                <td>Remaining benefits $</td>
                <td><xsl:value-of select="policy8"/></td>
                <td>Remaining Deductible</td>
                <td><xsl:value-of select="policy10"/></td>
                <td>Is there a MTC?</td>
                <td><xsl:value-of select="radio3"/></td>
               <td colspan="4"></td>
            </tr>
            <tr>
                <td>Eligible for D3330?</td>
                <td><xsl:value-of select="radio4"/></td>
                <td>Is there any waiting period?</td>
                <td><xsl:value-of select="radio5"/></td>
                <td>Waiting period Duration</td>
                <td><xsl:value-of select="waitingPeriodDuration"/></td>
                <td>Out of network benefits</td>
                <td><xsl:value-of select="radio1"/></td>
                <td>Do you file OS under medical first?</td>
                <td><xsl:value-of select="radio2"/></td>
                <td colspan="2"></td>
            </tr>
            <tr>
                <td colspan="12" class="borderNone"></td>
            </tr>
            <tr>
                <td colspan="12" class="borderNone"></td>
            </tr>
            <tr>
                <td colspan="12" class="tableHeading">Coverage &amp; Frequency Information</td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Exams:</strong></td>
            </tr>
            <tr>
                <td>D0140(%)</td>
                <td><xsl:value-of select="d0140"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="exams2"/></td>
                <td> D9310(%)</td>
                <td><xsl:value-of select="posterior8"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="posterior9"/></td>
                <td colspan="4"></td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Extractions</strong></td>
            </tr>

            <tr>
                <td>D7210/D7220/D7230(%)</td>
                <td><xsl:value-of select="d7210"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7210fr"/></td>
                <td>D7240(%)</td>
                <td><xsl:value-of select="d7240"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7240fr"/></td>
                <td>D7250(%)</td>
                <td><xsl:value-of select="d7250"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7250fr"/></td>
            </tr>
            <tr>
                <td>D7251(%)</td>
                <td><xsl:value-of select="d7251"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7251fr"/></td>
                <td>D7285/D7286(%)</td>
                <td><xsl:value-of select="d7285"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7285fr"/></td>
                <td>D7951/D7952(%)</td>
                <td><xsl:value-of select="d7951"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7951fr"/></td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Sedation</strong></td>
            </tr>
            <tr>
                <td>D9230(%) </td>
                <td><xsl:value-of select="sedations1"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="sedations1fr"/></td>
                <td>D9248(%) </td>
                <td><xsl:value-of select="sedations3"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="sedations3fr"/></td>
                <td>D9239(%)</td>
                <td><xsl:value-of select="d9239"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d9239fr"/></td>
            </tr>
            <tr>
                <td>D9243(%) </td>
                <td ><xsl:value-of select="sedations2"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="sedations2fr"/></td>
                <td colspan="8"></td>

            </tr>
            <tr>
                <td colspan="12" class=""><strong>Endo</strong></td>
            </tr>
            <tr>
                <td>D3310/D3320/D3330(%)</td>
                <td ><xsl:value-of select="d3310"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d3310fr"/></td>
                <td colspan="8"></td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Implants:</strong></td>
            </tr>
            <tr>
                <td>D6010(%)</td>
                <td><xsl:value-of select="implants1"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="implants5"/></td>
                <td>D6057(%)</td>
                <td><xsl:value-of select="implants2"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="implants6"/></td>
                <td>D6065/D6068(%)</td>
                <td><xsl:value-of select="implants4"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="implants7"/></td>
            </tr>
            <tr>
                <td>D6190(%)</td>
                <td ><xsl:value-of select="implants3"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="implants8"/></td>
                <td>D6011(%)</td>
                <td><xsl:value-of select="d6011"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d6011fr"/></td>
                <td>D5862(%)</td>
                <td><xsl:value-of select="d5862"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d5862fr"/></td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Perio:</strong></td>
            </tr>
            <tr>
                <td>D4341(%)</td>
                <td ><xsl:value-of select="perio1"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="perio1fr"/></td>
                <td>D4249(%)</td>
                <td><xsl:value-of select="oral1"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="oral1fr"/></td>
                <td>D4266/D4267(%)</td>
                <td><xsl:value-of select="d4266"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d4266fr"/></td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Dentures:</strong></td>
            </tr>
            <tr>
                <td>D5110/D5120(%)</td>
                <td><xsl:value-of select="d5110"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d5110fr"/></td>
                <td>D5130/D5140(%)</td>
                <td ><xsl:value-of select="d5130"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d5130fr"/></td>
                <td>D5860/D5865(%)</td>
                <td><xsl:value-of select="d5860"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d5860fr"/></td>
            </tr>
            <tr>
                <td>D6114/D6115(%)</td>
                <td ><xsl:value-of select="d6114"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d6114fr"/></td>
                <td colspan="8"></td>

            </tr>
            <tr>
                <td colspan="12" class=""><strong>Oral Surgery:</strong></td>
            </tr>
            <tr>
                <td>D7310(%) </td>
                <td ><xsl:value-of select="d7310"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7310fr"/></td>
                <td>D7311(%) </td>
                <td><xsl:value-of select="d7311"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d7311fr"/></td>
                <td>D7320(%)</td>
                <td ><xsl:value-of select="d7320"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7320fr"/></td>
            </tr>
            <tr>
                <td>D7321(%)</td>
                <td ><xsl:value-of select="d7321"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d7321fr"/></td>
                <td>D7472/D7473(%)</td>
                <td><xsl:value-of select="d7472"/></td>
                <td>Frequency</td>
                <td class="width-8"><xsl:value-of select="d7472fr"/></td>
                <td colspan="4"></td>
            </tr>
            <tr>
                <td>D7280(%)</td>
                <td><xsl:value-of select="d7280"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7280fr"/></td>
                <td>D7282(%)</td>
                <td ><xsl:value-of select="d7282"/></td>
                <td>Frequency</td>
                <td><xsl:value-of select="d7282fr"/></td>
                <td>D7283(%)</td>
                <td><xsl:value-of select="d7283"/></td>
                <td>Frequency</td>
                <td ><xsl:value-of select="d7283fr"/></td>
            </tr>
            <tr>
                <td>
                    D7311-7320 Can be combined with D7210-
                    D7240?
                </td>
                <td><xsl:value-of select="d7311Select"/></td>
                <td colspan="10"></td>
            </tr>
             <tr>
                <td colspan="12" class="borderNone"></td>
            </tr>
            <tr>
                <td colspan="12" class="borderNone"></td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Guielines for IV Sedations?</strong></td>
            </tr>
            <tr>
                 <td colspan="12"><xsl:value-of select="ivSedation"/></td>
            </tr>
            <tr>
                <td class=""><strong>Assignment of Benefits:</strong></td>
            </tr>
            <tr>
                 <td><xsl:value-of select="policy15"/></td>
            </tr>
            <tr>
                <td colspan="12" class="borderNone"></td>
            </tr>
            <tr>
                <td colspan="12" class="borderNone"></td>
            </tr>
            <!-- <tr>
                <td class=""><strong>History:</strong></td>
                <td colspan="11"></td>
            </tr>
            <tr>
                <td>ADA Code </td>
                <td></td>
                <td>Tooth No</td>
                <td></td>
                <td>DOS</td>
                <td>mm/dd/yyyy</td>
                <td>ADA Code</td>
                <td></td>
                <td>Tooth No. </td>
                <td></td>
                <td>DOS</td>
                <td>mm/dd/yyyy</td>
            </tr> -->
            <tr>

              <td colspan="12"><strong>History:</strong></td>
            </tr>
            <tr>
                <td colspan="12" class="innerTableBox">
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
                <td colspan="12" class="borderNone"></td>
            </tr>
            <tr>
                <td colspan="12" class=""><strong>Comments:</strong></td>
            </tr>
            <tr>
                <td colspan="12"><xsl:value-of select="comments"/></td>
            </tr>
            <tr>
                <td><strong>Benefits Verified by:</strong></td>
                <td><xsl:value-of select="benefits"/></td>
                <td colspan="7" class="borderNone"></td>
                <td>Date: </td>
                <td colspan="2"><xsl:value-of select="date"/></td>
            </tr>


        </table>
        <br />
        <br />

    </form>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
