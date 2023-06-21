<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
   version="1.0" >
    <xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
    	<xsl:template match="/issueClaimDownloadDto">
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
            .number-of-claims{
    font-size: 14px;
    background: #00c290;
    padding: 3px 10px;
    border-radius: 15px;
    color: #fff;
    margin-left: 5px;
  }
        </style>
    </head>
    <body>
        <form>
              <h4 style="color: black; margin-bottom:12px;">Upload Errors<span class="number-of-claims">Number of Claims Not Uploaded:<xsl:value-of select="issueClaimCounts"/></span></h4>
     <table class="table" vertical-align="top">
         <tr>
             <td class="bgWhite">
                 <table class="inner-table">

                     <tr class="bgWhite">
                          <td colspan="7" class="tableHeading">Issue Claims (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Office Name</td>
                         <td>Claim ID</td>
                         <td>upload Date</td>
                         <td>Issue due to which claim could not be uploaded</td>
                         <td>Source</td>

                     </tr>
                     <xsl:for-each select="data/data">
                     <tr class="whiteBg">
                         <td><xsl:value-of select="officeName"/></td>
                         <td><xsl:value-of select="claimId"/></td>
                         <td>
                       <xsl:variable name="month" select="substring(createdDate, 6, 2)" />
                       <xsl:variable name="day" select="substring(createdDate, 9, 2)" />
                       <xsl:variable name="year" select="substring(createdDate, 1, 4)" />
                       <xsl:variable name="hour" select="substring(createdDate, 12, 2)" />
                       <xsl:variable name="minute" select="substring(createdDate, 15, 2)" />
                       <xsl:variable name="second" select="substring(createdDate, 18, 2)" />
                       <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year, ' ', $hour, ':', $minute, ':', $second)" />
                         </td>
                         <td><xsl:value-of select="issue"/></td>
                         <td><xsl:value-of select="source"/></td>
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