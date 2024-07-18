<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
   version="1.0" >
    <xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
    	<xsl:template match="/productionDownloadDto">
  <html lang="en" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta charset="utf-8" />
        <title></title>
        <style>
            body,html {
                font-family: helvetica;
                font-size: 12px;
                padding: 0px;
                margin:0px;
            }
            .table {
                border-collapse: collapse;
                width: 100%
            }

                .table td {
                    border-collapse: collapse;
                    vertical-align: top;
                    background: #f8f8f9;
                    padding: 0px;
                    word-wrap: break-word;
                    max-width: 100px;
                }
            .maineHeading {
                font-size: 14px;
                margin:0px;
                margin-bottom:5px;
            }
            .bgWhite {
                background-color:#ffffff;
            }
            .inner-table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom:2px;
            }
                .inner-table td {
                    padding: 2px 5px;
                    border: 1px solid #a5a5a5;
                }
            .width-25 {
                width:25%
            }
            .whiteBg td{
                background: #ffffff;
            }
            .textBold {
                font-weight:bold;
                border-right:0px!important;
            }
            td:nth-child(even) {
                border-left: 0px;
            }
            .inner-table td.maineHeadingBox {
                border: 0px;
                padding: 10px 0px 0px 0px;
            }
            .na {
                color: #d88627;
                font-weight: bold;
            }
            .yes {
                color: #0da076;
                font-weight: bold;
            }
            .pageLink {
                font-weight: bold;
                color: #007bff;
                text-decoration:none;
            }
            .tableView td:nth-child(even) {
                border-left: 1px solid #a5a5a5;
            }
            .tableView td {
                font-weight:bold;
            }
            .remark-text {
                border:1px solid #eee;
                min-height:40px;
                min-width:150px;
                max-width:400px;
            }
            .tableHeading {
                font-size: 14px;
                color:black;
                text-align: center;
                font-family: sans-serif;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <form>
     <xsl:variable name="currentTeam" select="currentTeamName"/> 
     <xsl:if test="$currentTeam !='PAYMENT_POSTING' and $currentTeam !='PATIENT_CALLING' and $currentTeam !='PATIENT_STATEMENT' and $currentTeam !='AGING' and $currentTeam !='CDP'">
     <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">

                     <tr class="bgWhite">
                          <td colspan="7" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Client Name</td>
                         <td>Associate Name</td>
                         <td>Total Production</td>
                         <td>Average Productivity</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td></td>
                    <td><xsl:value-of select="sum(data/data/total)"/></td>
                    <td><xsl:value-of select="format-number(sum(data/data/days),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="data/data">
                     <tr class="whiteBg">
                         <td><xsl:value-of select="companyName"/></td>
                         <td><xsl:value-of select="concat(fname,' ',lname)"/></td>
                         <td><xsl:value-of select="total"/></td>
                         <td><xsl:value-of select="format-number(days,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
  </xsl:if>
  <xsl:if test="$currentTeam ='PATIENT_CALLING'">
          <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="8" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Office Name</td>
                         <td>Payment Promised</td>
                         <td>Payment Made</td>
                         <td>Wrong No</td>
                         <td>Not Ready To Pay</td>
                         <td>Statement Requested</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td><xsl:value-of select="format-number(sum(patientCalling/paymentPromised),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(patientCalling/paymentMade),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(patientCalling/wrongNo),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(patientCalling/notReadyToPay),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(patientCalling/statementRequested),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="patientCalling">
                     <tr class="whiteBg">
                         <td><xsl:value-of select="officeName"/></td>
                         <td><xsl:value-of select="format-number(paymentPromised,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(paymentMade,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(wrongNo,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(notReadyToPay,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(statementRequested,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
 </xsl:if>
   <xsl:if test="$currentTeam ='PATIENT_STATEMENT'">
          <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="10" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                      <tr class="tableView">
                         <td>Client Name</td>
                         <td>Associate Name</td>
                         <td>Total Production</td>
                         <td>Average Productivity</td>
                         <td>Statement Type1</td>
                         <td>Statement Type2</td>
                         <td>Statement Type3</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td></td>
                    <td><xsl:value-of select="sum(patientStatement/total)"/></td>
                    <td><xsl:value-of select="format-number(sum(patientStatement/days),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(patientStatement/statementType/statementType1),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(patientStatement/statementType/statementType2),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(patientStatement/statementType/statementType3),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="patientStatement">
                     <tr class="whiteBg">
                          <td><xsl:value-of select="clientName"/></td>
                          <td><xsl:value-of select="concat(fname,' ',lname)"/></td>
                          <td><xsl:value-of select="format-number(total,'0.0')"/></td>
                          <td><xsl:value-of select="format-number(days,'0.0')"/></td>
                          <td><xsl:value-of select="format-number(statementType/statementType1,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(statementType/statementType2,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(statementType/statementType3,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
 </xsl:if>

    <xsl:if test="$currentTeam ='PAYMENT_POSTING'">
     <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="10" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Client Name</td>
                         <td>Associate Name</td>
                         <td>Total Production</td>
                         <td>Average Productivity</td>
                         <td>Amount Posted</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td></td>
                    <td><xsl:value-of select="sum(paymentPosting/total)"/></td>
                    <td><xsl:value-of select="format-number(sum(paymentPosting/days),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(paymentPosting/amountPosted),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="paymentPosting">
                     <tr class="whiteBg">
                         <td><xsl:value-of select="companyName"/></td>
                         <td><xsl:value-of select="concat(fname,' ',lname)"/></td>
                         <td><xsl:value-of select="total"/></td>
                         <td><xsl:value-of select="format-number(days,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(totalAmountReceivedInBank,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
  </xsl:if>
    <xsl:if test="$currentTeam ='AGING' and  tabSwitchForAging='ageWise'">
          <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="8" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Office Name</td>
                         <td>0-30</td>
                         <td>30-60</td>
                         <td>60-90</td>
                         <td>90+</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfAgeWiseData/countForAgeRange1),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfAgeWiseData/countForAgeRange2),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfAgeWiseData/countForAgeRange3),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfAgeWiseData/countForAgeRange4),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="agingPdfDto/listOfAgeWiseData">
                     <tr class="whiteBg">
                         <td><xsl:value-of select="officeName"/></td>
                         <td><xsl:value-of select="format-number(countForAgeRange1,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(countForAgeRange2,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(countForAgeRange3,'0.0')"/></td>
                         <td><xsl:value-of select="format-number(countForAgeRange4,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
 </xsl:if>
 <xsl:if test="$currentTeam ='AGING' and  tabSwitchForAging='claimWise'">
          <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="10" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                        <td>Associate Name</td>
                        <td>Billed Count</td>
                        <td>Closed Count</td>
                        <td>Pending For Billing Count</td>
                        <td>Pending For Review Count</td>
                        <td>Re Billing Count</td>
                        <td>Reviewed Count</td>
                        <td>Submitted Count</td>
                        <td>Voided Count</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/billedCount),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/closedCount),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/PendingForBillingCount),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/pendingForReviewCount),'0.0')"/></td>
                     <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/reBillingCount),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/reviewedCount),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/submittedCount),'0.0')"/></td>
                    <td><xsl:value-of select="format-number(sum(agingPdfDto/listOfCurrentStatusWiseData/voidedCount),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="agingPdfDto/listOfCurrentStatusWiseData">
                     <tr class="whiteBg">
                    <td><xsl:value-of select="associateName"/></td>
                    <td><xsl:value-of select="format-number(billedCount,'0.0')"/></td>
                    <td><xsl:value-of select="format-number(closedCount,'0.0')"/></td>
                    <td><xsl:value-of select="format-number(pendingForBillingCount,'0.0')"/></td>
                    <td><xsl:value-of select="format-number(pendingForReviewCount,'0.0')"/></td>
                    <td><xsl:value-of select="format-number(reBillingCount,'0.0')"/></td>
                    <td><xsl:value-of select="format-number(reviewedCount,'0.0')"/></td>
                    <td><xsl:value-of select="format-number(submittedCount,'0.0')"/></td>
                    <td><xsl:value-of select="format-number(voidedCount,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
 </xsl:if>

 <xsl:if test="$currentTeam ='CDP' and  tabSwitchForCDP='followUp'">
     <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="10" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Client Name</td>
                         <td>Associate Name</td>
                         <td>Total Production</td>
                         <td>Average Productivity</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td></td>
                    <td><xsl:value-of select="sum(cdpPdfDto/cdpForInsuranceFollowUp/total)"/></td>
                    <td><xsl:value-of select="format-number(sum(cdpPdfDto/cdpForInsuranceFollowUp/days),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="cdpPdfDto/cdpForInsuranceFollowUp">
                     <tr class="whiteBg">
                         <td><xsl:value-of select="companyName"/></td>
                         <td><xsl:value-of select="concat(fname,' ',lname)"/></td>
                         <td><xsl:value-of select="total"/></td>
                         <td><xsl:value-of select="format-number(days,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
  </xsl:if>
  <xsl:if test="$currentTeam ='CDP' and  tabSwitchForCDP='appeal'">
     <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="10" class="tableHeading">Production (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Client Name</td>
                         <td>Associate Name</td>
                         <td>Total Production</td>
                         <td>Average Productivity</td>
                     </tr>
                    <tr style="background-color:#A9A9A9;">
                    <td >Total</td>
                    <td></td>
                    <td><xsl:value-of select="sum(cdpPdfDto/cdpForAppeal/total)"/></td>
                    <td><xsl:value-of select="format-number(sum(cdpPdfDto/cdpForAppeal/days),'0.0')"/></td>
                     </tr>
                      <xsl:for-each select="cdpPdfDto/cdpForAppeal">
                     <tr class="whiteBg">
                         <td><xsl:value-of select="companyName"/></td>
                         <td><xsl:value-of select="concat(fname,' ',lname)"/></td>
                         <td><xsl:value-of select="total"/></td>
                         <td><xsl:value-of select="format-number(days,'0.0')"/></td>
                     </tr>
                    </xsl:for-each>

                 </table>
             </td>
         </tr>
     </table>
  </xsl:if>
        </form>

    </body>
    </html>
    </xsl:template>
    </xsl:stylesheet>
