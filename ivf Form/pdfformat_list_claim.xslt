<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
   version="1.0" >
	<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
	<xsl:template match="/claimListDownloadDto">
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
        
    </style>
</head>
<body>
    <form>
        <table class="table" vertical-align="top">
            <tr>
                <td colspan="8" class="tableHeading">
                    <xsl:value-of select="fileName"/>
                </td>
            </tr>
            <tr>
                <td colspan="8"></td>
            </tr>
            
            </table>

    
   
       
    </form>

</body>

</html>
</xsl:template>
</xsl:stylesheet>
