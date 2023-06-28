<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
version="1.0" >
<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
<xsl:template match="/claimDetailsDownloadDto">
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
             min-width: 100%;
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
        .marginBottom {
            margin-bottom:20px;
        }
        .remark-text {
            border:1px solid #eee;
            min-height:40px;
            min-width:150px;
            max-width:400px;
        }
        .failed{
        color:red;
        }
         .passed{
        color:green;
        }
         .notRun{
        color:#E4A11B;
        }
        .error-message-api {
    color: red;
}

.success-message-api {
    color: green;
}
.passedNum {
    color: #ffffff;
    font-weight: 700;
    font-size: 14px;
    margin: 0 1px;
    background: #009f76;
    padding: 0 7px;
    margin-left: 2px;
    margin-right: 2px;
}

.passedNum.fail {
    background: #dc3545;
}
.passedNum.alert {
    background: #E4A11B;
}
.sub-content-td {
    <!-- margin-left: 90px;
    width: calc(100% - 90px); -->
    width:100%;
    border:0px;
}
    </style>
</head>
<body>
    <form>
        <h2 class="maineHeading">Claim Details(<xsl:value-of select="clientName"/>)</h2>
 <table class="table" vertical-align="top">
     <tr>
          <xsl:variable name="cNotes" select="data/data/claimNotes/value" />     
            <xsl:variable name="primaryInsCode" select="data/data/primaryInsCode" />
            <xsl:variable name="secondaryInsCode" select="data/data/secondaryInsCode" /> 
            <xsl:variable name="primry" select="data/data/primary" /> 
            <xsl:variable name="relatedTo300" select="relatedTo_300" />  
           
         <td class="bgWhite">
             <table class="inner-table">
                 <xsl:for-each select="data/data">   
                 <tr>
                     <td class="textBold">Office:</td>
                    <td> <xsl:value-of select="officeName"/></td>
                     <td class="textBold">Insurance Type:</td>
                      <td> <xsl:choose>
                           <xsl:when test="primary='true'">
                             <xsl:value-of select="primaryInsType"/>
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:value-of select="secondaryInsType"/>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>
                     <td class="textBold">Primary Insurance:</td>
                    <td><xsl:value-of select="primInsurance"/></td>
                     <td class="textBold">Regenerated Claim?:</td>
                     <td class="na">N/A</td>
                 </tr>
                 <tr class="whiteBg">
                     <td class="textBold">Billing Status:</td>
                         <td> <xsl:choose>
                           <xsl:when test="claimStatus=1">
                            <xsl:text>Fresh</xsl:text>
                           </xsl:when>
                           <xsl:otherwise>
                          <xsl:text>Re-Billing</xsl:text>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>
                     <td class="textBold">Insurance Member ID:</td>
                       <td> <xsl:value-of select="secMemberId"/></td>
                     <td class="textBold">Primary EOB:</td>
                         <td> <xsl:value-of select="primaryEob"/></td>
                     <td class="textBold">Linked Claim IDs:</td>
                         <td><xsl:value-of select="linkedClaims"/></td>
                 </tr>
                 <tr>
                     <td class="textBold">Client :</td>
                     <td><xsl:value-of select="clientName"/></td>
                     <td class="textBold">Patient ID:</td>
                     <td><xsl:value-of select="patientId"/></td>
                     <td class="textBold">Secondary Insurance:</td>
                     <td><xsl:value-of select="secInsurance"/></td>
                     <td class="textBold">Provider Type:</td>
                     <td><xsl:value-of select="claimType"/></td>
                 </tr>
                 <tr class="whiteBg">
                     <td class="textBold">Claim ID:</td>
                     <td> <xsl:value-of select="substring-before(claimId, '_')"/></td>
                     <td class="textBold">Patient Name:</td>
                    <td><xsl:value-of select="patientName"/></td>
                     <td class="textBold">Treating Provider:</td>
                    <td><xsl:value-of select="providerOnClaimFromSheet"/></td>
                     <td class="textBold">Claim Assigned To:</td>
                      <td><xsl:value-of select="assignedToName"/></td>
                 </tr>
                 <tr>
                     <td class="textBold">DOS:</td>
                        <td><xsl:variable name="month" select="substring(dos, 6, 2)" />
                       <xsl:variable name="day" select="substring(dos, 9, 2)" />
                       <xsl:variable name="year" select="substring(dos, 1, 4)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" /></td>
                     <td class="textBold">Patient DOB:</td>
                    <td><xsl:variable name="month" select="substring(patientDob, 6, 2)" />
                       <xsl:variable name="day" select="substring(patientDob, 9, 2)" />
                       <xsl:variable name="year" select="substring(patientDob, 1, 4)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" /></td>
                     <td class="textBold">Provider on Claim:</td>
                     <td><xsl:value-of select="providerOnClaim"/></td>
                     <td class="textBold">Team Assigned:</td>
                     <td><xsl:value-of select="currentTeam"/></td>
                 </tr>
                 <tr class="whiteBg">
                     <td class="textBold">Billed Amount:</td>
                     <td><xsl:value-of select="concat('$',format-number(submittedTotal,'0.00'))"/></td>
                     <td class="textBold">Subscriber Name :</td>
                     <td> <xsl:choose>
                           <xsl:when test="primary='true'">
                             <xsl:value-of select="primePolicyHolder"/>
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:value-of select="secPolicyHolder"/>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>
                     <td class="textBold"></td>
                     <td></td>
                     <td class="textBold">Claim Staus:</td>
                     <td> 
                 <xsl:choose>
                              <xsl:when test="pending = 'true'">
                               <xsl:text>Pending</xsl:text>
                             </xsl:when>
                    <xsl:otherwise>
                          <xsl:text>Submitted</xsl:text>
                    </xsl:otherwise>
              </xsl:choose>
                       </td>
                 </tr>
                 <tr>
                     <td class="textBold">Estimated Amount :</td>
                     <td> <xsl:choose>
                           <xsl:when test="primary='true'">
                             <xsl:value-of select="concat('$',format-number(primeSecSubmittedTotal,'0.00'))"/>
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:value-of select="concat('$',format-number(secSubmittedTotal,'0.00'))"/>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>
                     <td class="textBold">Subscriber DOB:</td>
                     <td> 

                       <xsl:choose>
                          <xsl:when test="string-length(primePolicyHolderDob) &gt; 0">
                        <xsl:choose>
                           <xsl:when test="primary='true'">
                            <xsl:variable name="month" select="substring(primePolicyHolderDob, 6, 2)" />
                       <xsl:variable name="day" select="substring(primePolicyHolderDob, 9, 2)" />
                       <xsl:variable name="year" select="substring(primePolicyHolderDob, 1, 4)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" />
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:variable name="month" select="substring(secPolicyHolderDob, 6, 2)" />
                       <xsl:variable name="day" select="substring(secPolicyHolderDob, 9, 2)" />
                       <xsl:variable name="year" select="substring(secPolicyHolderDob, 1, 4)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" />
                           </xsl:otherwise>
                           </xsl:choose>
                       </xsl:when>
                      <xsl:otherwise>

                      </xsl:otherwise>
                  </xsl:choose>
                       </td>
                     <td class="textBold"></td>
                     <td></td>
                     <td class="textBold">Assigned Team Role:</td>
                     <td><xsl:value-of select="assignedToRoleName"/></td>
                 </tr>

                  <xsl:if test="clientName='Smilepoint'">   
                     <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Links to Related Documents</h2></td>
                 </tr>
                 <tr>
                     <td colspan="2" class="textBold">IV ID:</td>
                      <td colspan="2">
                      <xsl:choose>
                          <xsl:when test="string-length(ivfId) &gt; 0">
                        <xsl:value-of select="ivfId"/>
                    </xsl:when>
                    <xsl:otherwise>
                          <xsl:text>IV Not Found</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
                     <td class="textBold">Date of IV:</td>
                       <td colspan="2">
                          <xsl:choose>
                          <xsl:when test="string-length(ivDos) &gt; 0">
                          <xsl:variable name="month" select="substring(ivDos, 6, 2)" />
                          <xsl:variable name="day" select="substring(ivDos, 9, 2)" />
                          <xsl:variable name="year" select="substring(ivDos, 1, 4)" />
                          <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" />
                         </xsl:when>
                   <xsl:otherwise>
                   <xsl:text>IV Not Found</xsl:text>
                   </xsl:otherwise>
                  </xsl:choose>
                  </td>

                     <td><a href="#" class="pageLink">IV Link &#10148;</a></td>
                 </tr>
                 <tr class="whiteBg">
                     <td colspan="2" class="textBold">Signed Tx Plan ID:</td>
                      <td colspan="2">
                          <xsl:choose>
                          <xsl:when test="string-length(tpId) &gt; 0">
                          <xsl:value-of select="tpId"/>
                      </xsl:when>
                      <xsl:otherwise>
                          <xsl:text>Tx Plan Not Found</xsl:text>
                      </xsl:otherwise>
                  </xsl:choose>
              </td>
                         <td class="textBold">Date of Tx. Plan:</td>
                        <td colspan="2">
                          <xsl:choose>
                          <xsl:when test="string-length(tpDos) &gt; 0">
                          <xsl:variable name="month" select="substring(tpDos, 6, 2)" />
                          <xsl:variable name="day" select="substring(tpDos, 9, 2)" />
                          <xsl:variable name="year" select="substring(tpDos, 1, 4)" />
                          <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" />
                         </xsl:when>
                   <xsl:otherwise>
                   <xsl:text>Tx Plan Not Found</xsl:text>
                   </xsl:otherwise>
                  </xsl:choose>
                  </td>
                     <td><a href="#" class="pageLink">Treatment Plan Link &#10148;</a></td>
                 </tr>
             </xsl:if>
                 </xsl:for-each> 
             </table>

                        <table class="inner-table">

                 <!--////////////////////////////////////////-->

                 <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Remarks by Other RCM Teams:</h2></td>
                 </tr>
                 <tr class="tableView">
                     <td>S.No.</td>
                     <td colspan="2">Team Name</td>
                     <td colspan="2">Date of Remarks</td>
                     <td colspan="3">Remarks</td>
                 </tr>
                  <xsl:for-each select="otherTeamsRemark">   
                 <tr class="whiteBg">
                    <td><xsl:number value="position()" format="1"/></td>
                     <td  colspan="2"><xsl:value-of select="teamName"/></td>
                         <td colspan="2"><xsl:variable name="month" select="substring(cd, 6, 2)" />
                       <xsl:variable name="day" select="substring(cd, 9, 2)" />
                       <xsl:variable name="year" select="substring(cd, 1, 4)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" /></td>
                     <td  colspan="3"><xsl:value-of select="comment"/></td>
                 </tr>
             </xsl:for-each>
            
                 <!--////////////////////////////////////////-->

          <!--       <tr class="whiteBg">
                     <td colspan="7" class="maineHeadingBox"><h2 class="maineHeading">Additional Requirements for Rebilled Claims:</h2></td>
                 </tr>
                 <tr class="tableView">
                     <td>S.No.</td>
                     <td>Team Name</td>
                     <td>Date</td>
                     <td>Requirement</td>
                     <td>Requirment Details</td>
                     <td>Done?</td>
                     <td>Remarks</td>
                 </tr> -->

                 <!--////////////////////////////////////////-->

    <xsl:if test="clientName='Smilepoint'">   
                 <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Claim Level Validations (Automated):</h2><span class="passedNum">Pass:<xsl:value-of select="countA/pass"/></span> <span
                                            class="passedNum fail">Fail:<xsl:value-of select="countA/fail"/></span></td>
                 </tr>
                 <tr class="tableView">
                     <td>S.No.</td>
                     <td colspan="3">Validation Name</td>
                     <td>Passed/Failed</td>
                     <td>Details</td>
                     <td colspan="2">Remarks</td>
                 </tr>
                      <xsl:for-each select="claimRules">   
                        <xsl:if test="manualAuto = 'AUTO' and ruleType = 'C' ">
                 <tr class="whiteBg">
                    <td><xsl:number value="position()-1" format="1"/></td>
                     <td colspan="3"><xsl:value-of select="ruleName"/></td>
                     <td>
                         <xsl:if test="messageType=1">  
                           <div class="error-message-api "><b><xsl:text>Fail</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=2">  
                            <div class="success-message-api"><b><xsl:text>Pass</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=0">  
                            <div class="notRun "><b><xsl:text>Not Run</xsl:text></b></div>
                       </xsl:if>

                     </td>
                    <td>
                        <div>
                           <xsl:value-of select="message" disable-output-escaping="yes" />
                    </div>
                    </td>

                     <td colspan="2"><xsl:value-of select="remark"/></td>
                 </tr>
             </xsl:if>
             </xsl:for-each>
             </xsl:if>

                 <!--////////////////////////////////////////-->

                  <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Claim Level Validations (Manual):</h2></td>
                 </tr>
                  <tr class="tableView">
                     <td>S.No.</td>
                     <td colspan="4">Validation Name</td>
                     <td>Status</td>
                     <td colspan="2">Remarks</td>
                 </tr>
        <xsl:for-each select="claimRules">   
            <xsl:if test="manualAuto='MANUAL'">
                 <xsl:variable name="currentRuleId" select="ruleId" />
                <xsl:if test="($currentRuleId = 318 or $currentRuleId = 319 or $currentRuleId = 321 or $currentRuleId = 322) and
              (($primry and ($primaryInsCode = 'CMC' or $primaryInsCode = 'AMC' or $primaryInsCode = 'MCR' or $primaryInsCode = 'CHIP')) or
              (not($primry) and ($secondaryInsCode = 'CMC' or $secondaryInsCode = 'AMC' or $secondaryInsCode = 'MCR' or $secondaryInsCode = 'CHIP')))">
         <tr class="whiteBg">
                          <xsl:if test="$currentRuleId=318">
                         <td>
                             <xsl:text>1</xsl:text>

                         </td>
                     </xsl:if>
                      <xsl:if test="$currentRuleId=319">
                         <td>
                            <xsl:text>2</xsl:text> 

                         </td>
                     </xsl:if>
                      <xsl:if test="$currentRuleId=321">
                         <td>
                             <xsl:text>3</xsl:text>

                         </td>
                     </xsl:if>
                      <xsl:if test="$currentRuleId=322">
                         <td>                         
                          <xsl:text>4</xsl:text>
                         </td>
                     </xsl:if>
                       <td colspan="4"><xsl:value-of select="ruleName"/></td>
                   <td>
                         <xsl:if test="messageType=1">  
                           <div class="error-message-api"><b><xsl:text>Incorrect</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=2">  
                            <div class="success-message-api"><b><xsl:text>Complete</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=3">  
                            <div class="notRun "><b><xsl:text>Not Available</xsl:text></b></div>
                       </xsl:if>

                     </td>
                
                       <td colspan="2"><xsl:value-of select="remark"/></td>
         </tr>
               </xsl:if> 
               </xsl:if>   
        </xsl:for-each>           
       <xsl:for-each select="claimRules">   
             <xsl:if test="manualAuto='MANUAL'">
                 <xsl:variable name="currentRuleId" select="ruleId" />
                <xsl:if test="$currentRuleId = 320 and
              (($primry and ($primaryInsCode = 'CMC' or $primaryInsCode = 'CHIP')) or
              (not($primry) and ($secondaryInsCode = 'CMC' or $secondaryInsCode = 'CHIP')))">
             <tr class="whiteBg">
                         <td>         
                             <xsl:value-of select="srNo"/>            
                         </td>
                       <td colspan="4"><xsl:value-of select="ruleName"/></td>
                   <td>

                        <xsl:if test="messageType=3">  
                            <div class="success-message-api"><b><xsl:text>Yes</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=2">  
                            <div class="notRun "><b><xsl:text>No</xsl:text></b></div>
                       </xsl:if>

                     </td>
                     <td colspan="2"></td>
                      </tr>
               </xsl:if> 
               </xsl:if>       
    </xsl:for-each>   
             <xsl:for-each  select="claimRules"> 

             <xsl:if test="manualAuto='MANUAL'">
                 <xsl:if test="ruleId=300"> 
                     <tr class="whiteBg">
                         <td><xsl:value-of select="srNo"/></td>
                       <td colspan="4"><xsl:value-of select="ruleName"/></td>
                   <td>

                         <xsl:if test="messageType=1">  
                           <div class="error-message-api"><b><xsl:text>Incorrect</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=2">  
                            <div class="success-message-api "><b><xsl:text>Complete</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=3">  
                            <div class="notRun "><b><xsl:text>Not Available</xsl:text></b></div>
                       </xsl:if>

                     </td>
                     <td colspan="2"><xsl:value-of select="remark"/></td>
                 </tr>
               </xsl:if> 
               </xsl:if>  
               </xsl:for-each>
               <tr class="whiteBg">
                    <td></td>
                     <td colspan="4">Copy and Paste Provider Notes: <span class="rule_remark"><xsl:value-of select="$cNotes"/></span></td>
                     <td></td>
                     <td colspan="2"></td>
                     
                 </tr>
               <xsl:for-each  select="claimRules">            
            <xsl:if test="manualAuto='MANUAL'">
                 <xsl:variable name="currentRuleId" select="ruleId" />
                <xsl:if test="$relatedTo300='true' and
              (($primry and ($primaryInsCode = 'CMC' or $primaryInsCode = 'AMC' or $primaryInsCode = 'MCR' or $primaryInsCode = 'CHIP')) or
              (not($primry) and ($secondaryInsCode = 'CMC' or $secondaryInsCode = 'AMC' or $secondaryInsCode = 'MCR' or $secondaryInsCode = 'CHIP'))) and
              ($currentRuleId = 311 or $currentRuleId = 312 or $currentRuleId = 313 or $currentRuleId = 314 or $currentRuleId = 315 or $currentRuleId = 316 or $currentRuleId = 317)">
                     <tr class="whiteBg">   
                      <xsl:if test="$currentRuleId=311">                       

                         <td>
                             <xsl:text>a</xsl:text>

                         </td>
                     </xsl:if>
                      <xsl:if test="$currentRuleId=312">
                         <td>
                            <xsl:text>b</xsl:text> 

                         </td>
                     </xsl:if>
                      <xsl:if test="$currentRuleId=313">
                         <td>
                             <xsl:text>c</xsl:text>

                         </td>
                     </xsl:if>
                      <xsl:if test="$currentRuleId=314">
                         <td>                         
                          <xsl:text>d</xsl:text>
                         </td>
                     </xsl:if>
                     <xsl:if test="$currentRuleId=315">
                         <td>                         
                          <xsl:text>e</xsl:text>
                         </td>
                     </xsl:if>
                     <xsl:if test="$currentRuleId=316">
                         <td>                         
                          <xsl:text>f</xsl:text>
                         </td>
                     </xsl:if>
                     <xsl:if test="$currentRuleId=317">
                         <td>                         
                          <xsl:text>g</xsl:text>
                         </td>
                     </xsl:if>
                       <td colspan="4"><xsl:value-of select="ruleName"/></td>
                   <td>
                         <xsl:if test="messageType=1">  
                           <div class="error-message-api"><b><xsl:text>Not Available</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=2">  
                            <div class="success-message-api"><b><xsl:text>Available</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType=3">  
                            <div class="notRun "><b><xsl:text>Not Needed</xsl:text></b></div>
                       </xsl:if>

                     </td>
                       <td colspan="2"><xsl:value-of select="remark"/></td>
                   </tr>
               </xsl:if>  
               </xsl:if>  
               </xsl:for-each>

               <xsl:for-each  select="claimRules">            
            <xsl:if test="manualAuto='MANUAL'">
                 <xsl:variable name="currentRuleId" select="ruleId" />
                <xsl:if test="$relatedTo300='true' and
              (($primry and ($primaryInsCode = 'CMC' or $primaryInsCode = 'AMC' or $primaryInsCode = 'MCR' or $primaryInsCode = 'CHIP')) or
              (not($primry) and ($secondaryInsCode = 'CMC' or $secondaryInsCode = 'AMC' or $secondaryInsCode = 'MCR' or $secondaryInsCode = 'CHIP'))) and
              $currentRuleId= 317">

                <tr class="whiteBg" >
                    <td colspan="8">
                      <table class="table sub-content-td">
                        <tr>
                        <td colspan="8" style="border: none;"><xsl:text>a) Filling (tooth# + surface)</xsl:text></td>
                       </tr>
      <tr>
        <td colspan="8" style="border: none;"><xsl:text>b) Extraction - which side, tooth and how is it done, instrument used</xsl:text></td>
      </tr>
      <tr>
        <td colspan="8" style="border: none;"><xsl:text>c) Crown - tooth#</xsl:text></td>
      </tr>
      <tr>
        <td colspan="8" style="border: none;"><xsl:text>d) Denture - Impression taken</xsl:text></td>
      </tr>
      <tr>
        <td colspan="8" style="border: none;"><xsl:text>e) RCT - Steps and how it is done and which file is used to open access to roots and canal</xsl:text></td>
      </tr>
      <tr>
        <td colspan="8" style="border: none;"><xsl:text>f) Material, Brand name of buildup, crown and all</xsl:text></td>
      </tr>
      <tr>
        <td colspan="8" style="border: none;"><xsl:text>g) Findings of exam - service specific</xsl:text></td>
      </tr>
      <tr>
        <td colspan="8" style="border: none;"><xsl:text>h) Sealant - tooth# and surface should be there</xsl:text></td>
      </tr>
                      </table>
                    </td>              
                </tr>

               </xsl:if>   
               </xsl:if>           
                
            </xsl:for-each>

                 

                 <!--////////////////////////////////////////-->

                 <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Service Code Level Validations(Manual):</h2></td>
                 </tr>
                 <tr class="tableView">
                       <td>S.No.</td>
                       <td colspan="2">Validation Name</td>
                       <td>Service Codes</td>
                       <td>Description</td>
                       <td>Status</td>
                       <td colspan="2">Remarks</td>
                 </tr>
            <xsl:for-each select="serviceLevelCodeManual/dto">   
                 <xsl:if test="manualAuto = 'Manual' ">
                 <tr class="whiteBg">
                     <td><xsl:number value="position()" format="1"/></td>
                    <td colspan="2"><xsl:value-of select="name"/></td>
                    <td><xsl:value-of select="serviceCode"/></td>
                    <td><xsl:value-of select="description"/></td>
                     <td>
                         <xsl:if test="answer='Incorrect'">  
                           <div class="error-message-api"><b><xsl:text>Incorrect</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="answer='Complete'">  
                            <div class="success-message-api"><b><xsl:text>Complete</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="answer='Not Available'">  
                            <div class="notRun "><b><xsl:text>Not Available</xsl:text></b></div>
                       </xsl:if>

                     </td>
                        <td colspan="2"><xsl:value-of select="remark"/></td>
                 </tr>
             </xsl:if>
             </xsl:for-each>

             <!--////////////////////////////////-->
        <xsl:if test="clientName='Smilepoint'">  
             <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Service Code Level Validations(Automated):</h2><span class="passedNum">Pass:<xsl:value-of select="countAS/pass"/></span> <span
                                            class="passedNum fail">Fail:<xsl:value-of select="countAS/fail"/></span></td>
                 </tr>
                 <tr class="tableView">
                       <td>S.No.</td>
                       <td colspan="2">Validation Name</td>
                       <td>Service Codes</td>
                       <td>Description</td>
                       <td>Messages</td>
                       <td>Passed/Failed</td>
                       <td>Remarks</td>
                 </tr> 
                <xsl:for-each select="serviceLevelCodeManual/dto">   
                 <xsl:if test="manualAuto = 'Automated' ">
                 <tr class="whiteBg">
                     <td><xsl:number value="position()" format="1"/></td>
                    <td colspan="2"><xsl:value-of select="name"/></td>
                    <td><xsl:value-of select="serviceCode"/></td>
                    <td><xsl:value-of select="description"/></td>
                     <td>
                        <div>
                           <xsl:value-of select="message" disable-output-escaping="yes" />
                        </div>
                    </td>
                      <td>
                         <xsl:if test="messageType = 1">  
                           <div class="error-message-api"><b><xsl:text>Failed</xsl:text></b></div>
                       </xsl:if>

                        <xsl:if test="messageType = 2">  
                            <div class="success-message-api"><b><xsl:text>Passed</xsl:text></b></div>
                       </xsl:if>
                     </td>
                        <td><xsl:value-of select="remark"/></td>
                 </tr>
             </xsl:if>
             </xsl:for-each>
         </xsl:if>
          <xsl:if test="clientName='Smilepoint' and teamId!=3">
                  <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Rule Engine Validations:</h2><span class="passedNum">Pass:<xsl:value-of select="count/pass"/></span> <span
                                            class="passedNum fail">Fail:<xsl:value-of select="count/fail"/></span>
                                        <span
                                            class="passedNum alert">Alert:<xsl:value-of select="count/alert"/></span></td>
                 </tr>
                 <tr class="tableView">
                     <td colspan="2">Rule Name</td>
                     <td>Service Code</td>
                     <td>Tooth#</td>
                     <td>Surface#</td>
                     <td>Messages</td>
                     <td  colspan="2">Remarks</td>
                 </tr>
                <xsl:for-each select="ruleEngineReport">      
                 <tr class="whiteBg">
                    <td colspan="2"><xsl:value-of select="ruleName"/></td>
                    <td style="max-width:100%"><xsl:value-of select="codes"/></td>
                    <td><xsl:value-of select="tooth"/></td>
                    <td><xsl:value-of select="surface"/></td>
                     <td>
                        <div>
                           <xsl:value-of select="message" disable-output-escaping="yes" />
                        </div>
                    </td>
                        <td  colspan="2"><xsl:value-of select="remark"/></td>
                 </tr>
             </xsl:for-each>
             </xsl:if>

                 <!--////////////////////////////////////////-->
          <xsl:if test="teamId!=3">
                 <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"><h2 class="maineHeading">Enter Claim Submission Details:</h2></td>
                 </tr>
                 <tr class="tableView">
                     <td>S.No.</td>
                     <td colspan="3">Description</td>
                     <td colspan="4">Details</td>
                 </tr>
                 <tr class="whiteBg">
                     <td>01</td>
                     <td colspan="3">Date &amp; Claim Processing in Eaglesoft</td>
                     <td colspan="4">
                        <xsl:if test="string-length(claimSubmissionDto/esDate) &gt; 0">
                        <xsl:variable name="year" select="substring(claimSubmissionDto/esDate, 1, 4)"/>
                         <xsl:variable name="month" select="substring(claimSubmissionDto/esDate, 6, 2)"/>
                       <xsl:variable name="day" select="substring(claimSubmissionDto/esDate, 9, 2)"/>
                      <xsl:value-of select="concat($day, '/', $month, '/', $year)"/>
       </xsl:if>
            </td>
                 </tr>
                 <tr class="whiteBg">
                     <td>02</td>
                     <td colspan="3">Channel used to submit the Claim Insurance (Submission type)</td>
                     <td colspan="4">
                        <xsl:if test="claimSubmissionDto/channel='RemoteLite'">
                            <xsl:text>Clearing House (Remotelite)</xsl:text>
                        </xsl:if>
                        <xsl:if test="claimSubmissionDto/channel='Fax'">
                            <xsl:text>Fax</xsl:text>
                        </xsl:if>
                         <xsl:if test="claimSubmissionDto/channel='Mail'">
                            <xsl:text>Mail</xsl:text>
                        </xsl:if>
                        <xsl:if test="claimSubmissionDto/channel='Portal'">
                            <xsl:text>Portal, <b>ClaimId:</b><xsl:value-of select="claimSubmissionDto/claimNumber"/></xsl:text>
                        </xsl:if>
                     </td>
                 </tr>

                 <tr class="whiteBg">
                     <td>03</td>
                     <td colspan="3">Attachments sent with the claim?</td>
                     <td colspan="4">
                        <xsl:if test="claimSubmissionDto/attachmentSend='true'">
                            <xsl:text>Yes</xsl:text>
                        </xsl:if>
                        <xsl:if test="claimSubmissionDto/attachmentSend='false'">
                            <xsl:text>No</xsl:text>
                        </xsl:if>
                     </td>
                 </tr>
                 <tr class="whiteBg">
                     <td>04</td>
                     <td colspan="3">Pre-Auth submitted with the claim?</td>
                     <td colspan="4">
                        <xsl:if test="claimSubmissionDto/preauth='true'">
                            <xsl:text>Yes, <b>Remarks:</b><xsl:value-of select="claimSubmissionDto/preauthNo"/></xsl:text>

                        </xsl:if>
                        <xsl:if test="claimSubmissionDto/preauth='false'">
                            <xsl:text>No</xsl:text>
                        </xsl:if>
                     </td>
                 </tr>
                 <tr class="whiteBg">
                     <td>05</td>
                     <td colspan="3">Provider Change Reference Number Needed?</td>
                     <td colspan="4">
                        <xsl:if test="claimSubmissionDto/refferalLetter='true'">
                            <xsl:text>Yes, <b>Remarks:</b><xsl:value-of select="claimSubmissionDto/providerRefNo"/></xsl:text>
                        </xsl:if>
                        <xsl:if test="claimSubmissionDto/refferalLetter='false'">
                            <xsl:text>No</xsl:text>
                        </xsl:if>
                     </td>
                 </tr>

                 <!--////////////////////////////////////////-->

                 <tr class="whiteBg">
                     <td colspan="8" class="maineHeadingBox"></td>
                 </tr>
                 <tr class="whiteBg">
                     <td colspan="8"><strong>Remarks if needed:</strong><div class="remark-text"><xsl:value-of select="data/data/claimRemarks"/></div></td>
                 </tr>
    </xsl:if>
             </table>
         </td>
     </tr>
 </table>
           </form>
           </body>
        </html>

</xsl:template>
</xsl:stylesheet>