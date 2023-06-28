<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
version="1.0" >
<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
<xsl:template match="/allPendancySortedDownloadDto">
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
                      <xsl:variable name="cTeamId" select="currentTeamId" />
                         <tr class="bgWhite">
                              <td colspan="13" class="tableHeading">Pendancy- Other Teams (<xsl:value-of select="clientName"/>)</td>
                         </tr>
                      <xsl:if test="tabSwitch='withoutDos'"> 
                             <tr class="tableView">
                                 <th style="border:1px solid black;">Office</th>
                                 <xsl:for-each select="teamsData">
                                 <th style="border:1px solid black;">
                                     <xsl:value-of select="."/>
                                 </th>
                                </xsl:for-each>
                             </tr>
                    <tr style="background-color:d#A9A9A9;">
                        <td >Total</td>
                            <xsl:for-each select="sortedTotalCount">
                    <xsl:if test="$currentTeam!= teamName">
                        <td>           
                          <xsl:value-of select="count"/>
                        </td>
                    </xsl:if>
                    </xsl:for-each>
                       
                         </tr> 
                <xsl:for-each select="data/data">
                       <tr class="whiteBg">
                           <td>
                           <xsl:value-of select="officeName"/>
                           </td>
                             <xsl:for-each select="sortedCounts1/entry">
                             <xsl:variable name="currentKey" select="key" />
                               <xsl:if test="$currentTeam!= $currentKey">
                             <td>
                             <xsl:value-of select="value"/>
                             </td>
                               </xsl:if>
                            </xsl:for-each>
                       </tr>
              </xsl:for-each>
                    </xsl:if>

                    <xsl:if test="tabSwitch='withDOS'"> 
                        <tr class="tableView">
                                 <th style="border:1px solid black;">Office</th>
                                 <xsl:for-each select="teamsData">
                                 <th style="border:1px solid black;">
                                     <xsl:value-of select="."/>
                                 </th>
                                </xsl:for-each>
                             </tr>
                             <xsl:for-each select="data/data">    
                        <tr class="whiteBg">
                           <td>
                           <xsl:value-of select="officeName"/>
                           </td>
                             <xsl:for-each select="sortedDates1/entry">
                             <xsl:variable name="currentKey" select="key" />
                               <xsl:if test="$currentTeam!= $currentKey">
                             <td>
                             <xsl:choose>
                                     <xsl:when test="string-length(value) &gt; 0">
                                     <xsl:variable name="month" select="substring(value, 6, 2)" />
                           <xsl:variable name="day" select="substring(value, 9, 2)" />
                           <xsl:variable name="year" select="substring(value, 1, 4)" />
                           <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" />
                       </xsl:when>
                       <xsl:otherwise>-</xsl:otherwise>
                   </xsl:choose>
                             </td>
                               </xsl:if>
                            </xsl:for-each>
                       </tr>
                   </xsl:for-each>
                             
                    </xsl:if>
                     <xsl:if test="tabSwitch='withDOP'"> 
                            <tr class="tableView">
                                 <th style="border:1px solid black;">Office</th>
                                 <xsl:for-each select="teamsData">
                                 <th style="border:1px solid black;">
                                     <xsl:value-of select="."/>
                                 </th>
                                </xsl:for-each>
                            </tr>
                             <xsl:for-each select="data/data">    
                        <tr class="whiteBg">
                           <td>
                           <xsl:value-of select="officeName"/>
                           </td>
                             <xsl:for-each select="sortedPending/entry">
                             <xsl:variable name="currentKey" select="key" />
                               <xsl:if test="$currentTeam!= $currentKey">
                             <td>
                             <xsl:choose>
                                    <xsl:when test="string-length(value) &gt; 0">
                                    <xsl:variable name="month" select="substring(value, 6, 2)" />
                           <xsl:variable name="day" select="substring(value, 9, 2)" />
                           <xsl:variable name="year" select="substring(value, 1, 4)" />
                           <xsl:value-of select="concat(substring('JanFebMarAprMayJunJulAugSepOctNovDec', $month * 3 - 2, 3), ' ', $day, ', ', $year)" />
                       </xsl:when>
                       <xsl:otherwise>-</xsl:otherwise>
                   </xsl:choose>
                             </td>
                               </xsl:if>
                            </xsl:for-each>
                    </tr>
                   </xsl:for-each>
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