<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
version="1.0" >
<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
    	<xsl:template match="/othersTeamWorkDownloadDto">
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
     <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">
                 <xsl:variable name="currentTeam" select="currentTeamName" /> 
                     <tr class="bgWhite">
                          <td colspan="13" class="tableHeading">List_Of_Claims (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Office</td>
                         <td>Patient ID</td>
                         <td>Patient Name</td>
                         <td>DOS</td>
                         <td>Insurance Name</td>
                         <td>Insurance Type</td>
                         <td>Claim Type</td>
                         <td>Est.Amount</td>
                         <td>Last Team that Worked on this claim</td>
                         <td>Last Team's Remarks</td>
                         <td>Pending since</td>
                         <td>Current Team</td>

                     </tr>    
                      <xsl:for-each select="data/data">   

                     <tr class="whiteBg">   

                         <td><xsl:value-of select="officeName"/></td>
                         <td><xsl:value-of select="patientId"/></td>
                         <td><xsl:value-of select="patientName"/></td>
                         <td><xsl:variable name="month" select="substring(dos, 6, 2)" />
                       <xsl:variable name="day" select="substring(dos, 9, 2)" />
                       <xsl:variable name="year" select="substring(dos, 1, 4)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" /></td>
                       <td> <xsl:choose>
                           <xsl:when test="substring(claimId, string-length(claimId) - 1) = '_P'">
                             <xsl:value-of select="primaryInsurance"/>
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:value-of select="secondaryInsurance"/>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>
                       <td> <xsl:choose>
                             <xsl:when test="substring(claimId, string-length(claimId) - 1) = '_P'">
                             <xsl:value-of select="prName"/>
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:value-of select="secName"/>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>
                        <td>
                           <xsl:choose>
                           <xsl:when test="substring(claimId, string-length(claimId) - 1) = '_P'">
                           <xsl:text>Primary</xsl:text>
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:text>Secondary</xsl:text>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>
                       <td><xsl:choose>
                            <xsl:when test="substring(claimId, string-length(claimId) - 1) = '_P'">
                             <xsl:value-of select="concat('$', format-number(primeSecSubmittedTotal, '0'))"/>
                           </xsl:when>
                           <xsl:otherwise>
                           <xsl:value-of select="concat('$', format-number(secTotal,'0'))"/>
                           </xsl:otherwise>
                           </xsl:choose>
                       </td>            
                       <td><xsl:value-of select="lastTeam"/></td>
                       <td><xsl:value-of select="lastTeamRemark"/></td>    
                       <td><xsl:variable name="month" select="substring(dos, 6, 2)" />
                       <xsl:variable name="day" select="substring(dos, 9, 2)" />
                       <xsl:variable name="year" select="substring(dos, 1, 4)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" /></td>   
                       <td><xsl:value-of select="$currentTeam"/></td>                     
                       </tr>            
                    </xsl:for-each> 
                 </table>
             </td>
         </tr>
     </table>
        </form>
    </body>
    </html>
    </xsl:template>
    </xsl:stylesheet>