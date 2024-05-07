<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
version="1.0" >
<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
    	<xsl:template match="/pendancyDownloadDto">
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
            <xsl:variable name="cTeamId" select="currentTeamId" />   
             <td class="bgWhite">
                 <table class="inner-table">
                     <tr class="bgWhite">
                          <td colspan="8" class="tableHeading">Pendancy (<xsl:value-of select="clientName"/>)</td>
                     </tr>
                     <tr class="tableView">
                         <td>Client</td>
                         <td>Office</td>
                         <td>User Assignment</td>
                         <td>Days Since Oldest Pending Claim (Upload Date)</td>
                         <td>Days Since Oldest Pending Claim (DOS)</td>
                         <td># of Claims to be <xsl:value-of select="currentTeamName"/></td>
                         <xsl:if test="$cTeamId=7">
                         <td># of RemoteLite Rejections</td>
                         </xsl:if>
                         <xsl:if test="$cTeamId=7">
                         <td>Total Pendency</td>
                         </xsl:if>
                     </tr>   
                    <tr style="background-color:d#A9A9A9;">
                    <td >Total</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td><xsl:value-of select="totalCount"/></td>
                    <xsl:if test="$cTeamId=7">
                    <td><xsl:value-of select="totalRemLiteReject"/></td>
                    </xsl:if>
                      <xsl:if test="$cTeamId=7">
                    <td><xsl:value-of select="totalcountAndRemLiteReject"/></td>
                     </xsl:if>
                     </tr> 
                      <xsl:for-each select="data/data">   
                     <tr class="whiteBg">   
                         <td><xsl:value-of select="companyName"/></td>
                         <td><xsl:value-of select="officeName"/></td>
                         <td><xsl:value-of select="concat(fname,' ',lname)"/></td>
                         <td><xsl:value-of select="opdtd"/></td>
                         <td><xsl:value-of select="opdosd"/></td>
                         <td><xsl:value-of select="count"/></td> 
                         <xsl:if test="$cTeamId=7">
                         <td><xsl:value-of select="remoteLiteRejections"/></td>
                         </xsl:if>
                         <xsl:if test="$cTeamId=7">
                         <td><xsl:value-of select="remoteLiteRejections+count"/></td>
                         </xsl:if>           
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