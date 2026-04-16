<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
version="1.0" >
<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
<xsl:template match="/ivfDownloadDto">
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta charset="utf-8" />
        <title></title>
        <style>
        .colorred
{
	color:#ff0000;
}
.newff{
	color:#0000ff;
}

.yellow{
	background:#efff00;
}
table#data-scroll-t th, table#data-scroll-tre th{
  position: -webkit-sticky;
  position: sticky;
  top: 0;
  line-height: 32px;
  text-align: center;
  background: #BBDEFB;
  z-index:1;
  width:10%;
  border:1px solid;
  
}
table#data-scroll-t tr, table#data-scroll-tre tr{
  border:1px solid;
  
}
table#data-scroll-t td, table#data-scroll-tre td{
  border:1px solid;
  
}
#data-scroll-t .data-scroll-thd ,#data-scroll-tre .data-scroll-thdtre{
  font-family: 'Roboto', sans-serif;
  font-size: 13px;
 font-weight: 300;
}

table#data-scroll-t,table#data-scroll-tre {
  border-collapse: collapse;
  width:100%
}

th#table#data-scroll-thd ,th#table#data-scroll-thdtre {
  background-color: #1976D2;
  color: #fff;
}

th#table#data-scroll-thd,
td#table#data-scroll-thdtre {
  padding: 1.1em 1.5em;
}

table#data-scroll-t tr,table#data-scroll-tre tr {
  color: #212121;
}

table#data-scroll-t tr:nth-child(odd) {
  background-color: #BBDEFB;
}

table#data-scroll-t1 th, table#data-scroll-t1re th{
  position: -webkit-sticky;
  position: sticky;
  top: 0;
  line-height: 32px;
  text-align: center;
  background: #BBDEFB;
  z-index:1;
  width:10%;
  border:1px solid;
  
}
table#data-scroll-t1 tr, table#data-scroll-t1re tr{
  border:1px solid;
  
}
table#data-scroll-t1 td, table#data-scroll-t1re td{
  border:1px solid;
  
}
#data-scroll-t1 .data-scroll-t1hd ,#data-scroll-t1re .data-scroll-t1hdtre{
  font-family: 'Roboto', sans-serif;
  font-size: 13px;
 font-weight: 300;
}

table#data-scroll-t1,table#data-scroll-t1re {
  border-collapse: collapse;
  width:100%
}

th#table#data-scroll-t1hd ,th#table#data-scroll-t1hdtre {
  background-color: #1976D2;
  color: #fff;
}

th#table#data-scroll-t1hd,
td#table#data-scroll-t1hdtre {
  padding: 1.1em 1.5em;
}

table#data-scroll-t1 tr,table#data-scroll-t1re tr {
  color: #212121;
}

table#data-scroll-t1 tr:nth-child(odd) {
  background-color: #BBDEFB;
}


.table-container {
	background: #fff;
    padding: 20px 20px;
    position: relative;
    font-size: 14px;
	text-align:left;
	color: #494949;
}

.sticky-top {
  height: 50px !important;
}

.backBtn {
  color: white;
  float: left;
  margin-right: 10px;
  background: #4789ff;
  padding: 2px 10px;
  border-radius: 20px;
  cursor: pointer;
}
.right-container {
    min-height: 680px;
    margin-left: 0px !important;
    padding-top: 70px  !important;
}
.table {
  width: 100%;
  max-width: 100%;
  margin-bottom: 1rem;
  background-color: transparent;
  border-collapse: collapse;
}

.table-striped tbody tr:nth-of-type(odd) {
  background-color: rgba(0, 0, 0, 0.05);
}

.table-striped td, .table-striped th {
  border: 1px solid #dee2e6;
  padding: 0.75rem;
}



        </style>
    </head>
    <body>
        <div class="right-container">
	<div class="inner-bg">
         		<div class="table-container">
				<span>
					<p style="text-align: center">Smilepoint - Insurance
						Verification Form</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Office Name: <span class="newff"><xsl:value-of select="data/basicInfo1"/></span></td>
						<td>Patient Name: <span class="newff"><xsl:value-of select="data/basicInfo2"/></span></td>
						<td>Insurance Name: <span class="newff"><xsl:value-of select="data/basicInfo3"/></span></td>
						<td>Tax ID: <span class="newff"><xsl:value-of select="data/basicInfo4"/></span></td>
					</tr>
					<tr>
						<td>Policy Holder: <span class="newff"><xsl:value-of select="data/basicInfo5"/></span></td>
						<td>Patient DOB: <span class="newff"><xsl:value-of select="data/basicInfo6"/></span></td>
						<td>Insurance Contact: <span class="newff"><xsl:value-of select="data/basicInfo7"/></span></td>
						<td>CSR Name: <span class="newff"><xsl:value-of select="data/basicInfo8"/></span></td>
					</tr>
					<tr>
						<td>Policy Holder DOB: <span class="newff"><xsl:value-of select="data/basicInfo9"/></span></td>
						<td>Employer Name: <span class="newff"><xsl:value-of select="data/basicInfo10"/></span></td>
						<td>Continued/Recall/NP: <span class="newff"><xsl:value-of select="data/basicInfo11"/></span></td>
						<td>REF #: <span class="newff"><xsl:value-of select="data/basicInfo12"/></span></td>
					</tr>
					<tr>
						<td>Member SSN: <span class="newff"><xsl:value-of select="data/basicInfo13"/></span></td>
						<td>Group #: <span class="newff"><xsl:value-of select="data/basicInfo14"/></span></td>
						<xsl:if test="data/basicInfo15='Secondary'">
						<td style="color:white;background-color:red">COB
							Status: <span><xsl:value-of select="data/basicInfo15"/></span></td>
						</xsl:if>
						<xsl:if test="data/basicInfo15!='Secondary'">
						<td>COB Status: <span
								class="newff"><xsl:value-of select="data/basicInfo15"/></span></td>
						</xsl:if>
						<td>Patient ID: <span class="newff"><xsl:value-of select="data/basicInfo21"/></span></td>
					</tr>
					<tr>
						<td>Member ID: <span class="newff"><xsl:value-of select="data/basicInfo16"/></span></td>
						<td>Appointment: <span class="newff"><xsl:value-of select="data/basicInfo17"/></span></td>
						<td>Payer ID: <span class="newff"><xsl:value-of select="data/basicInfo18"/></span></td>
						<xsl:if test="data/basicInfo19='change provider'">
						<td class="yellow">Provider
							Last Name: <span class="newff"><xsl:value-of select="data/basicInfo19"/></span></td>
						</xsl:if>
						<xsl:if test="data/basicInfo19!='change provider'">
						<td>Provider Last Name:
							<span class="newff"><xsl:value-of select="data/basicInfo19"/></span></td>
						</xsl:if>
					</tr>
					<tr>
						<td colspan="4">Insurance Address:
							<span class="newff"><xsl:value-of select="data/basicInfo20"/></span>
						</td>

					</tr>
					<tr>
						<td>Special remarks for Office: <span class="newff"><xsl:value-of select="data/sRemarks"/></span></td>
						<td>Updated Prim Ins in ES as BCBS Federal: <span class="newff"><xsl:value-of select="data/esBcbs"/></span>
						</td>
						<td>Need to obtain Medical Policy No: <span class="newff"><xsl:value-of select="data/obtainMPN"/></span>
						</td>
						<td></td>
					</tr>

				</table>



				<span>
					<p style="text-align: center">Policy/Plan Information</p>
					<p style="text-align: center; color: red"><xsl:value-of select="data/outNetworkMessage"/></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Plan Type: <span class="newff"><xsl:value-of select="data/policy1"/></span></td>
						<td>D0120: <span class="newff"><xsl:value-of select="data/policy18"/></span></td>
						<td>D2391: <span class="newff"><xsl:value-of select="data/policy19"/></span></td>
						<td>Find FS: <span class="newff"><xsl:value-of select="data/policy20"/></span></td>
					</tr>
					<tr>
						<td>CRA Req: <span class="newff"><xsl:value-of select="data/policy17"/></span></td>
						<td>Termed Date: <span class="newff"><xsl:value-of select="data/policy2"/></span></td>
						<td>Network: <span class="newff"><xsl:value-of select="data/policy3"/></span></td>
						<td>Fee Schedule: <span class="newff"><xsl:value-of select="data/policy4"/></span></td>
					</tr>
					<tr>
						<td>Effective Date: <span class="newff"><xsl:value-of select="data/policy5"/></span></td>
						<td>Cal.Yr/Fiscal Yr/Plan Yr.: <span class="newff"><xsl:value-of select="data/policy6"/></span></td>
						<td colspan="2"></td>
					</tr>
					<tr>
						<td>Annual Max: <span class="newff"><xsl:value-of select="data/policy7"/></span></td>
						<td>Ann. Max Rem: <span class="newff"><xsl:value-of select="data/policy8"/></span></td>
						<td>Ind. Ded: <span class="newff"><xsl:value-of select="data/policy9"/></span></td>
						<td>Ind. Ded Rem: <span class="newff"><xsl:value-of select="data/policy10"/></span></td>
					</tr>
					<tr>
						<td>Dependents Covered to age: <span class="newff"><xsl:value-of select="data/policy11"/></span></td>
						<td>Pre-D Mandatory.: <span class="newff"><xsl:value-of select="data/policy12"/></span></td>
						<td colspan="2"></td>
					</tr>
					<tr>
						<td>Non-Duplicate clause? <span class="newff"><xsl:value-of select="data/policy13"/></span></td>
						<td>Full Time Student Status Required?:
							<span class="newff"><xsl:value-of select="data/policy14"/></span>
						</td>
						<td>Assignment of Benefits Accepted?:
							<span class="newff"><xsl:value-of select="data/policy15"/></span>
						</td>
						<td>Coverage Book: <span class="newff"><xsl:value-of select="data/policy16"/></span></td>
					</tr>
					<!-- 
					<tr>
						<td>Dependents Covered to age: <span class="newff"><xsl:value-of select="data/policy11"/></span></td>
						<td>Pre-D Mandatory.: <span class="newff"><xsl:value-of select="data/policy12"/></span></td>
						<td colspan="2"></td>
					</tr>

                   -->

				</table>

				<span>
					<p style="text-align: center">Percentages Covered</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Basic(%): <span class="newff"><xsl:value-of select="data/percentages1"/></span></td>
						<td>Subject to ded: <span class="newff"><xsl:value-of select="data/percentages2"/></span></td>
						<td>Major(%): <span class="newff"><xsl:value-of select="data/percentages3"/></span></td>
						<td>Subject to ded: <span class="newff"><xsl:value-of select="data/percentages4"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>Endo(%): <span class="newff"><xsl:value-of select="data/percentages5"/></span></td>
						<td>Subject to ded: <span class="newff"><xsl:value-of select="data/percentages6"/></span></td>
						<td>Perio Surgery(%): <span class="newff"><xsl:value-of select="data/percentages7"/></span></td>
						<td>Subject to ded: <span class="newff"><xsl:value-of select="data/percentages8"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>Preventative(%): <span class="newff"><xsl:value-of select="data/percentages9"/></span></td>
						<td>Subject to ded: <span class="newff"><xsl:value-of select="data/percentages13"/></span></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td>Diagnostics(%): <span class="newff"><xsl:value-of select="data/percentages10"/></span></td>
						<td>Subject to ded: <span class="newff"><xsl:value-of select="data/percentages14"/></span></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>

					<tr>
						<td>PA(%): <span class="newff"><xsl:value-of select="data/percentages11"/></span></td>
						<td>Subject to ded : <span class="newff"><xsl:value-of select="data/percentages15"/></span></td>
						<td>FMX(%) : <span class="newff"><xsl:value-of select="data/percentages16"/></span></td>
						<td>Subject to ded: <span class="newff"><xsl:value-of select="data/fmxSubjectToDed"/></span></td>
						<td>Claims Timely Fillings: <span class="newff"><xsl:value-of select="data/percentages12"/></span></td>

					</tr>

					<tr>
						<td>Missing tooth clause: <span class="newff"><xsl:value-of select="data/prosthetics1"/></span></td>
						<td>Replacement Clause: <span class="newff"><xsl:value-of select="data/prosthetics2"/></span></td>
						<td>Paid Prep/Seat Date: <span class="newff"><xsl:value-of select="data/prosthetics3"/></span></td>
						<td>Night Gaurds (D9940) Frequency: <span class="newff"><xsl:value-of select="data/prosthetics4"/></span>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td>Night Gaurds(D9944)FR: <span class="newff"><xsl:value-of select="data/posterior19"/></span></td>
						<td>Night Gaurds(D9945)FR: <span class="newff"><xsl:value-of select="data/posterior20"/></span></td>
						<td></td>
					</tr>

				</table>


				<span>
					<p style="text-align: center">Waiting Periods (In Months)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Preventive: <span class="newff"><xsl:value-of select="data/waitingPeriod4"/></span></td>
						<td>Basic: <span class="newff"><xsl:value-of select="data/waitingPeriod1"/></span></td>
						<td>Major: <span class="newff"><xsl:value-of select="data/waitingPeriod2"/></span></td>
						<td>D0120 and D0150 share Frequency: <span class="newff"><xsl:value-of select="data/shareFr"/></span></td>
					</tr>

				</table>
				<span>
					<p style="text-align: center">SSC</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D2930: <span class="newff"><xsl:value-of select="data/ssc1"/></span></td>
						<td>D2931: <span class="newff"><xsl:value-of select="data/ssc2"/></span></td>
						<td>D0160: <span class="newff"><xsl:value-of select="data/pedo1"/></span></td>
						<td>D2934: <span class="newff"><xsl:value-of select="data/pedo2"/></span></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td><span class="newff"><xsl:value-of select="data/ssc1"/></span></td>
						<td><span class="newff"><xsl:value-of select="data/ssc2"/></span></td>
						<td>Frequency <span class="newff"><xsl:value-of select="data/d0160Freq"/></span></td>
						<td>Frequency <span class="newff"><xsl:value-of select="data/freqD2934"/></span></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td>D0330: <span class="newff"><xsl:value-of select="data/pano1"/></span></td>
						<td>Does Pano and FMX shares the frequency? <span class="newff"><xsl:value-of select="data/pano2"/></span>
						</td>
						<td>D4381: <span class="newff"><xsl:value-of select="data/d4381"/></span></td>
						<td>D3330: <span class="newff"><xsl:value-of select="data/d3330"/></span></td>
	          <td></td>
	          <td></td>
					</tr>
					<tr>
						<td></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d0330Freq"/></span></td>
						<td></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4381Freq"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d3330Freq"/></span></td>
	          <td></td>
					</tr>
				</table>

				<span>
					<p style="text-align: center">Exams(Frequency)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D0120: <span class="newff"><xsl:value-of select="data/exams1"/></span></td>
						<td>D0140: <span class="newff"><xsl:value-of select="data/exams2"/></span></td>
						<td>D0145: <span class="newff"><xsl:value-of select="data/exams3"/></span></td>
						<td>D0150: <span class="newff"><xsl:value-of select="data/exams4"/></span></td>
					</tr>

				</table>
				<span>
					<p style="text-align: center">X-Rays(Frequency)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>

						<td>BWX(0274): <span class="newff"><xsl:value-of select="data/xrays1"/></span></td>
						<td>PA(0220): <span class="newff"><xsl:value-of select="data/xrays2"/></span></td>
						<td>D0230: <span class="newff"><xsl:value-of select="data/xrays3"/></span></td>
						<td>FMX(0210): <span class="newff"><xsl:value-of select="data/xrays4"/></span></td>



					</tr>

					<tr>
						<td>Denture(%)(D5225): <span class="newff"><xsl:value-of select="data/den5225"/></span></td>
						<td>Denture(fr)(D5225): <span class="newff"><xsl:value-of select="data/denf5225"/></span></td>
						<td colspan="3"></td>
					</tr>
					<tr>
						<td>Denture(%)(D5226): <span class="newff"><xsl:value-of select="data/den5226"/></span></td>
						<td>Denture(fr)(D5226): <span class="newff"><xsl:value-of select="data/denf5226"/></span></td>
						<td colspan="3"></td>
					</tr>
					<tr>
						<td>Bundling: <span class="newff"><xsl:value-of select="data/xrays5"/></span></td>
						<td colspan="4"></td>
					</tr>
					<tr>
						<td>Bridges(%)</td>
						<td colspan="4"></td>
					</tr>
					<tr>
						<td>(D6245 &amp; D6740): <span class="newff"><xsl:value-of select="data/bridges1"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/bridges2"/></span></td>
						<td>Will downgrade applicable? :<span class="newff"><xsl:value-of select="data/cdowngrade"/></span></td>
						<td colspan="2"></td>
					</tr>

				</table>
				<span>
					<p style="text-align: center">Fluroide(D1208)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>

						<td>Frequency: <span class="newff"><xsl:value-of select="data/fluroide1"/></span></td>
						<td>Age Limit: <span class="newff"><xsl:value-of select="data/fluroide2"/></span></td>

					</tr>

				</table>

				<span>
					<p style="text-align: center">Varnish(D1206)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/fluroide3"/></span></td>
						<td>Age Limit: <span class="newff"><xsl:value-of select="data/fluroide4"/></span></td>

					</tr>

				</table>
				<span>
					<p style="text-align: center"></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Sealants(D1351): <span class="newff"><xsl:value-of select="data/sealantsD"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/sealants1"/></span></td>
						<td>Age Limit: <span class="newff"><xsl:value-of select="data/sealants2"/></span></td>

					</tr>

				</table>
				<span>
					<p style="text-align: center">Coverage</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Primary Molars: <span class="newff"><xsl:value-of select="data/sealants3"/></span></td>
						<td>Pre-Molars: <span class="newff"><xsl:value-of select="data/sealants4"/></span></td>
						<td>Perm-Molars: <span class="newff"><xsl:value-of select="data/sealants5"/></span></td>
					</tr>

				</table>
				<span>
					<p style="text-align: center">Prophy</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/prophy1"/></span></td>
						<td>D1120:: <span class="newff"><xsl:value-of select="data/prophy2"/></span></td>

					</tr>

				</table>
				<span>
					<p style="text-align: center"></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>1120/1110 Roll age: <span class="newff"><xsl:value-of select="data/rollage"/></span></td>
						<td>Fillings(Bundle): <span class="newff"><xsl:value-of select="data/fillings"/></span></td>
					</tr>

				</table>
				<span>
					<p style="text-align: center">Perio</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>SRP(D4341)%: <span class="newff"><xsl:value-of select="data/perio1"/></span></td>
						<td> SRP(D4341)Basic/Major%: <span class="newff"><xsl:value-of select="data/srpd4341"/></span></td>

						<td>Frequency: <span class="newff"><xsl:value-of select="data/perio2"/></span></td>
						<td>Quads Per Day: <span class="newff"><xsl:value-of select="data/perio3"/></span></td>
						<td>Days b/w Quad: <span class="newff"><xsl:value-of select="data/perio4"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>D4921: <span class="newff"><xsl:value-of select="data/perioD4921"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4921Frequency"/></span></td>

						<td>D4266: <span class="newff"><xsl:value-of select="data/perioD4266"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4266Frequency"/></span></td>

						<td>D9910: <span class="newff"><xsl:value-of select="data/perioD9910"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d9910Frequency"/></span></td>

					</tr>

				</table>
				<span>
					<p style="text-align: center">PerioMNT</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>(D4910) %: <span class="newff"><xsl:value-of select="data/perioMnt1"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/perioMnt2"/></span></td>
						<td colspan="2">Alt. with Prophy(D1110):
							<span class="newff"><xsl:value-of select="data/perioMnt3"/></span>
						</td>

					</tr>
					<tr>
						<td>FMD(D4355)%: <span class="newff"><xsl:value-of select="data/perioMnt4"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/perioMnt5"/></span></td>
						<td>Gingivitis(D4346)%: <span class="newff"><xsl:value-of select="data/perioMnt6"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/perioMnt7"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center">Sedations</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Nitrous (D9230): <span class="newff"><xsl:value-of select="data/sedations1"/></span></td>
						<td>IV Sedation (D9243): <span class="newff"><xsl:value-of select="data/sedations2"/></span></td>
					<td colspan="2">IV Sedation (D9245): <span class="newff"><xsl:value-of select="data/sedations4"/></span>
					</td>

					</tr>
				</table>
				<span>
					<p style="text-align: center">Extractions(%)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Minor(D7111,D7140): <span class="newff"><xsl:value-of select="data/extractions1"/></span></td>
						<td>Major (D7210,D7220, D7230,D7240): <span class="newff"><xsl:value-of select="data/extractions2"/></span>
						</td>
						<td>D7250(%): <span class="newff"><xsl:value-of select="data/d7250"/></span></td>

						<td>MajorExt. Basic/Major: <span class="newff"><xsl:value-of select="data/majord72101"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center">Oral Surgery</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Crown Lengthening(D4249)%: <span class="newff"><xsl:value-of select="data/oral1"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/oral2"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center">Alveolplasty</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>1-3 Teeth/Qd(D7311): Covered w. Ext: <span class="newff"><xsl:value-of select="data/oral3"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/oral4"/></span></td>
					</tr>
					<tr>
						<td>4 Teeth/Qd (D7310): Covered w. Ext: <span class="newff"><xsl:value-of select="data/oral5"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/oral6"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center">Dentures (Frequency)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Complete (D5110/D5120): <span class="newff"><xsl:value-of select="data/dentures1"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>Partial (D5211/D5212/D5213/D5214/D5225/D5226): <span
								class="newff"><xsl:value-of select="data/dentures3"/></span></td>
						<td></td>
					</tr>

				</table>
				<span>
					<p style="text-align: center">Implants Coverage (%)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Implants D6010: <span class="newff"><xsl:value-of select="data/implants1"/></span></td>
						<td>Implants D6057: <span class="newff"><xsl:value-of select="data/implants2"/></span></td>
					</tr>
					<tr>
						<td>Implants D6190: <span class="newff"><xsl:value-of select="data/implants3"/></span></td>
						<td>Implants Supported Porc./Ceramic (D6065): <span
								class="newff"><xsl:value-of select="data/implants4"/></span></td>
					</tr>
					<tr>
						<td>Implants Fr(D6010): <span class="newff"><xsl:value-of select="data/implants5"/></span></td>
						<td>Implants Fr(D6057): <span class="newff"><xsl:value-of select="data/implants6"/></span></td>
					</tr>
					<tr>
						<td>Implants Fr(D6065): <span class="newff"><xsl:value-of select="data/implants7"/></span></td>
						<td>Implants Fr(D6190): <span class="newff"><xsl:value-of select="data/implants8"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center">Posterior</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td></td>
						<td colspan="2">Frequency: <span class="newff"><xsl:value-of select="data/posterior2"/></span></td>
					</tr>
					<tr>
						<td>Composites (D2391) %: <span class="newff"><xsl:value-of select="data/posterior1"/></span></td>
						<td colspan="2">Frequency: <span class="newff"><xsl:value-of select="data/d2391Freq"/></span></td>
					</tr>
					<tr>
						<td colspan="3">Downgraded to Amalgam (D2140): <span
								class="newff"><xsl:value-of select="data/posterior3"/></span></td>
					</tr>
					<tr>
						<td>Crowns (D2740 &amp; D2750)%: <span class="newff"><xsl:value-of select="data/posterior4"/></span></td>
						<td colspan="2">Crowns(D2740 &amp; D2750) Frequency: <span
								class="newff"><xsl:value-of select="data/posterior5"/></span></td>
					</tr>
					<tr>
						<td colspan="1">Will Crown Downgrade or not?: <span
								class="newff"><xsl:value-of select="data/posterior6"/></span></td>
						<td colspan="2">Which code? <span class="newff"><xsl:value-of select="data/posterior17"/></span></td>
					</tr>
					<tr>
						<td>Night Gaurds (D9944) %: <span class="newff"><xsl:value-of select="data/posterior7"/></span></td>
						<td>Night Gaurds (D9945) %: <span class="newff"><xsl:value-of select="data/posterior18"/></span></td>
						<td></td>
					</tr>
					<tr>

						<td>Consult (D9310)%: <span class="newff"><xsl:value-of select="data/posterior8"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/posterior9"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>Build-up (D2950) %: <span class="newff"><xsl:value-of select="data/posterior10"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/posterior11"/></span></td>
						<td>Same day as Crown: <span class="newff"><xsl:value-of select="data/posterior12"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center">Ortho(%)</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D8080, D8090, D8070: <span class="newff"><xsl:value-of select="data/ortho1"/></span></td>
						<td>Ortho Max: <span class="newff"><xsl:value-of select="data/ortho2"/></span></td>
						<td>Age Limit: <span class="newff"><xsl:value-of select="data/ortho3"/></span></td>
						<td>Subject to Deductible: <span class="newff"><xsl:value-of select="data/ortho4"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>Ortho Remaing: <span class="newff"><xsl:value-of select="data/ortho5"/></span></td>
						<td>Ortho Waiting Period(In Months): <span class="newff"><xsl:value-of select="data/waitingPeriod3"/></span>
						</td>
						<td>Consult (D9310)% :<span class="newff"><xsl:value-of select="data/posterior8"/></span></td>
						<td>Frequency :<span class="newff"><xsl:value-of select="data/posterior9"/></span></td>
						<td></td>
					</tr>
				</table>
					<p style="text-align: center">Space maintainer</p>
					<table class="table table-bordered table-striped">
					<tr>
						<td>D1510(%): <span class="newff"><xsl:value-of select="data/d1510"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d1510Freq"/></span></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td>D1516(%): <span class="newff"><xsl:value-of select="data/d1516"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d1516Freq"/></span></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td>D1517(%): <span class="newff"><xsl:value-of select="data/d1517"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d1517Freq"/></span></td>
						<td>Space Maintainer Age Limit: <span class="newff"><xsl:value-of select="data/smAgeLimit"/></span></td>
						<td></td>
						<td></td>
					</tr>
				</table>
					<p style="text-align: center">Pulpotomy</p>
					<table class="table table-bordered table-striped">
					<tr>
						<td>D3220(%): <span class="newff"><xsl:value-of select="data/d3220"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d3220Freq"/></span></td>
						<td>D2962(%): <span class="newff"><xsl:value-of select="data/d2962"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d2962fr"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>D0431(%): <span class="newff"><xsl:value-of select="data/d0431"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d0431fr"/></span></td>
						<td>D4999(%): <span class="newff"><xsl:value-of select="data/d4999"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d4999fr"/></span></td>
						<td></td>
					</tr>
					<tr>
						<td>D7953(%): <span class="newff"><xsl:value-of select="data/d7953"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/dentures6"/></span></td>
						<td>Bone Graft (D7953) Covered w.Implants: <span
								class="newff"><xsl:value-of select="data/dentures5"/></span>
						</td>

            <td></td>
           <td></td>

					</tr>

					<tr>
						<td>D9630(%): <span class="newff"><xsl:value-of select="data/d9630"/></span></td>
						<td colspan="1">Frequency: <span class="newff"><xsl:value-of select="data/d9630fr"/></span></td>
						<td></td>



						<td></td>
						<td></td>
					</tr>

					<tr>
						<td>How many fillings can be done in a year?: <span class="newff"><xsl:value-of select="data/fill1"/></span>
						</td>
						<td>How many Extractions can be done in a year?: <span
								class="newff"><xsl:value-of select="data/extr1"/></span></td>
						<td>How many crowns can be done in a year? :<span class="newff"><xsl:value-of select="data/crn1"/></span>
						</td>
						<td></td>
						<td></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center"></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Exams</td>
						<td>Xrays</td>
					</tr>
					<tr>
						<td>D0120:<span class="newff"><xsl:value-of select="data/ckD0120"/></span></td>
						<td>D210:<span class="newff"><xsl:value-of select="data/ckD210"/></span></td>
					</tr>
					<tr>
						<td>D0140:<span class="newff"><xsl:value-of select="data/ckD0140"/></span></td>
						<td>D220:<span class="newff"><xsl:value-of select="data/ckD220"/></span></td>
					</tr>
					<tr>
						<td>D0145:<span class="newff"><xsl:value-of select="data/ckD0145"/></span></td>
						<td>D230:<span class="newff"><xsl:value-of select="data/ckD230"/></span></td>
					</tr>
					<tr>
						<td>D0150:<span class="newff"><xsl:value-of select="data/ckD0150"/></span></td>
						<td>D330:<span class="newff"><xsl:value-of select="data/ckD330"/></span></td>
					</tr>
					<tr>
						<td>D0160:<span class="newff"><xsl:value-of select="data/ckD0160"/></span></td>
						<td>D274:<span class="newff"><xsl:value-of select="data/ckD274"/></span></td>
					</tr>

				</table>
				<span>
					<p style="text-align: center">Complete History (<b>Total Count</b>- <xsl:value-of select="data/historyCount"/>)
					</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<th>ADA Code</th>
						<th>Tooth No.- Surface</th>
						<th>DOS</th>
					</tr>
					 <xsl:for-each select="data/hdto">
					<tr>
						<td><span class="newff"><xsl:value-of select="historyCode"/></span></td>
						<td><span class="newff"><xsl:value-of select="historyTooth"/></span></td>
						<td><span class="newff"><xsl:value-of select="historyDos"/></span></td>
					</tr>
				</xsl:for-each>
				</table>

				<span>
					<p style="text-align: center"></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Comments :<span class="newff"><xsl:value-of select="data/comments"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center"></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Benefits Verified by : <span class="newff"><xsl:value-of select="data/benefits"/></span></td>
						<td>Date : <span class="newff"><xsl:value-of select="data/date"/></span></td>
					</tr>
				</table>
			</div>
			<xsl:if test="data/ivFormTypeId=2">
			<div class="table-container">
				<h3>IVF DATA- Details</h3>
				<span>
					<p style="text-align: center">Smilepoint -OS Insurance Verification Form</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Office Name: <span class="newff"><xsl:value-of select="data/basicInfo1"/></span></td>
						<td>Patient ID : <span class="newff"><xsl:value-of select="data/basicInfo21"/></span></td>
						<td>Suscriber's Name: <span class="newff"><xsl:value-of select="data/basicInfo5"/></span></td>
						<td>Suscriber's DOB: <span class="newff"><xsl:value-of select="data/basicInfo9"/></span></td>
						<td>Patient's Name: <span class="newff"><xsl:value-of select="data/basicInfo2"/></span></td>
						<td>Patient's DOB: <span class="newff"><xsl:value-of select="data/basicInfo6"/></span></td>
					</tr>
					<tr>
						<td>CSR Name: <span class="newff"><xsl:value-of select="data/basicInfo8"/></span></td>
						<td>Ref#: <span class="newff"><xsl:value-of select="data/basicInfo12"/></span></td>
						<td>Provider's Name: <span class="newff"><xsl:value-of select="data/basicInfo19"/></span></td>
						<td>Tax ID: <span class="newff"><xsl:value-of select="data/basicInfo4"/></span></td>
						<td>NPI: <span class="newff"><xsl:value-of select="data/npi"/></span></td>
						<td>Licence #: <span class="newff"><xsl:value-of select="data/licence"/></span></td>
					</tr>
					<tr>
						<td>Plan Type: <span class="newff"><xsl:value-of select="data/osPlanType"/></span></td>
						<td>Network: <span class="newff"><xsl:value-of select="data/policy3"/></span></td>
						<td>SSN#: <span class="newff"><xsl:value-of select="data/basicInfo13"/></span></td>
						<td>Member ID: <span class="newff"><xsl:value-of select="data/basicInfo16"/></span></td>
						<td>Effective Date: <span class="newff"><xsl:value-of select="data/policy5"/></span></td>
						<td>CY/FY: <span class="newff"><xsl:value-of select="data/policy6"/></span></td>
					</tr>
					<tr>
						<td>Employer's Name: <span class="newff"><xsl:value-of select="data/basicInfo10"/></span></td>
						<td>Group Number: <span class="newff"><xsl:value-of select="data/basicInfo14"/></span></td>
						<td>Insurance Name: <span class="newff"><xsl:value-of select="data/basicInfo3"/></span></td>
						<td>Insurance Telephone: <span class="newff"><xsl:value-of select="data/basicInfo7"/></span></td>
						<td>Insurance Address: <span class="newff"><xsl:value-of select="data/basicInfo20"/></span></td>
					</tr>
					<tr>
						<td>Fee Schedule: <span class="newff"><xsl:value-of select="data/policy4"/></span></td>
						<td>Dependents covered upto age?: <span class="newff"><xsl:value-of select="data/policy11"/></span></td>
						<td>Coordination of benefits: <span class="newff"><xsl:value-of select="data/corrdOfBenefits"/></span></td>
						<td>Payor ID: <span class="newff"><xsl:value-of select="data/basicInfo18"/></span></td>
					</tr>
					<tr>
						<td>What is allowable amount for D7210: <span
								class="newff"><xsl:value-of select="data/whatAmountD7210"/></span></td>
						<td>Maximum $: <span class="newff"><xsl:value-of select="data/policy7"/></span></td>
						<td>Deductible: <span class="newff"><xsl:value-of select="data/policy9"/></span></td>
					</tr>
					<tr>
						<td>What is allowable amount for D7240: <span
								class="newff"><xsl:value-of select="data/allowAmountD7240"/></span></td>
						<td>Remaining benefits $: <span class="newff"><xsl:value-of select="data/policy8"/></span></td>
						<td>Remaining Deductible: <span class="newff"><xsl:value-of select="data/policy10"/></span></td>
					</tr>
					<tr>
						<td>Is there a MTC? <span class="newff"><xsl:value-of select="data/radio3"/></span></td>
						<td>Eligible for D3330? <span class="newff"><xsl:value-of select="data/radio4"/></span></td>
						<td>Is there any waiting period? <span class="newff"><xsl:value-of select="data/radio5"/></span></td>
						<td>Out of Network Benefits: <span class="newff"><xsl:value-of select="data/radio1"/></span></td>
						<td>Do you file OS under medical first? <span class="newff"><xsl:value-of select="data/radio2"/></span></td>
					</tr>
					<tr>
						<td>Waiting Period Duration <span class="newff"><xsl:value-of select="data/waitingPeriodDuration"/></span>
						</td>

					</tr>
				</table>



				<span>
					<p style="text-align: center">Extractions:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D7210(%): <span class="newff"><xsl:value-of select="data/d7210"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7210fr"/></span></td>
						<td>D7220(%): <span class="newff"><xsl:value-of select="data/d7220"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7220fr"/></span></td>
						<td>D7230(%): <span class="newff"><xsl:value-of select="data/d7230"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7230fr"/></span></td>
					</tr>
					<tr>
						<td>D7240(%): <span class="newff"><xsl:value-of select="data/d7240"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7240fr"/></span></td>
						<td>D7250(%): <span class="newff"><xsl:value-of select="data/d7250"/></span></td>
						<td>Frequency(%): <span class="newff"><xsl:value-of select="data/d7250fr"/></span></td>
						<td>D7310(%): <span class="newff"><xsl:value-of select="data/d7310"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7310fr"/></span></td>
					</tr>
					<tr>
						<td>D7311(%): <span class="newff"><xsl:value-of select="data/d7311"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7311fr"/></span></td>
						<td>D7320(%): <span class="newff"><xsl:value-of select="data/d7320"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7320fr"/></span></td>
						<td>D7321(%): <span class="newff"><xsl:value-of select="data/d7321"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7321fr"/></span></td>
					</tr>
					<tr>
						<td>D7473(%): <span class="newff"><xsl:value-of select="data/d7473"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7473fr"/></span></td>
					</tr>
				</table>

				<span>
					<p style="text-align: center">Sedation:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D9230(%): <span class="newff"><xsl:value-of select="data/sedations1"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/sedations1fr"/></span></td>
						<td>D9245(%): <span class="newff"><xsl:value-of select="data/sedations4"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/sedations4fr"/></span></td>
						<td>D9239(%): <span class="newff"><xsl:value-of select="data/d9239"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d9239fr"/></span></td>
					</tr>
					<tr>
						<td>D9243(%): <span class="newff"><xsl:value-of select="data/sedations2"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/sedations2fr"/></span></td>
					</tr>
				</table>

				<span>
					<p style="text-align: center">Bone Grafts:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D4263(%): <span class="newff"><xsl:value-of select="data/d4263"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4263fr"/></span></td>
						<td>D4264(%): <span class="newff"><xsl:value-of select="data/d4264"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4264fr"/></span></td>
						<td>D6104(%): <span class="newff"><xsl:value-of select="data/d6104"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d6104fr"/></span></td>
					</tr>
					<tr>
						<td>D7953(%): <span class="newff"><xsl:value-of select="data/d7953"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7953fr"/></span></td>
					</tr>
				</table>

				<span>
					<p style="text-align: center">Endo:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D3310(%): <span class="newff"><xsl:value-of select="data/d3310"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d3310fr"/></span></td>
						<td>D3320(%): <span class="newff"><xsl:value-of select="data/d3320"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d3320fr"/></span></td>
						<td>D3330(%): <span class="newff"><xsl:value-of select="data/d3330"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d3330Freq"/></span></td>
					</tr>
					<tr>
						<td>D3346(%): <span class="newff"><xsl:value-of select="data/d3346"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d3346fr"/></span></td>
						<td>D3347(%): <span class="newff"><xsl:value-of select="data/d3347"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d3347fr"/></span></td>
						<td>D3348(%): <span class="newff"><xsl:value-of select="data/d3348"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d3348fr"/></span></td>
					</tr>
				</table>

				<span>
					<p style="text-align: center">Implants:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D6010(%): <span class="newff"><xsl:value-of select="data/implants1"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/implants5"/></span></td>
						<td>D6057(%): <span class="newff"><xsl:value-of select="data/implants2"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/implants6"/></span></td>
						<td>D6058(%): <span class="newff"><xsl:value-of select="data/d6058"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d6058fr"/></span></td>
					</tr>
					<tr>
						<td>D6190(%): <span class="newff"><xsl:value-of select="data/implants3"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/implants8"/></span></td>
					</tr>
				</table>

				<span>
					<p style="text-align: center">Perio:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>D4249(%): <span class="newff"><xsl:value-of select="data/oral1"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/oral1fr"/></span></td>
						<td>D7951(%): <span class="newff"><xsl:value-of select="data/d7951"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7951fr"/></span></td>
						<td>D9310(%): <span class="newff"><xsl:value-of select="data/posterior8"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/posterior9"/></span></td>
					</tr>
					<tr>
						<td>D4266(%): <span class="newff"><xsl:value-of select="data/d4266"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4266fr"/></span></td>
						<td>D4267(%): <span class="newff"><xsl:value-of select="data/d4267"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4267fr"/></span></td>
						<td>D4341(%): <span class="newff"><xsl:value-of select="data/perio1"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/perio1fr"/></span></td>
					</tr>
					<tr>
						<td>D4273(%): <span class="newff"><xsl:value-of select="data/d4273"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d4273fr"/></span></td>
						<td>D6065(%): <span class="newff"><xsl:value-of select="data/implants4"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/implants7"/></span></td>
						<td>D7251(%): <span class="newff"><xsl:value-of select="data/d7251"/></span></td>
						<td>Frequency: <span class="newff"><xsl:value-of select="data/d7251fr"/></span></td>
					</tr>

				</table>

				<span>
					<p style="text-align: center">Guidelines for IV Sedation:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td><xsl:value-of select="data/ivSedation"/></td>
					</tr>
				</table>

				<span>
					<p style="text-align: center">:</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Assignment of Benefits : <xsl:value-of select="data/policy15"/></td>
					</tr>
				</table>

<!-- 				<span>
					<p style="text-align: center">Complete History</p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<th>ADA Code</th>
						<th>Tooth No.- Surface</th>
						<th>DOS</th>
					</tr>
					 <xsl:for-each select="data/hdto">
					<tr>
						<td><span class="newff"><xsl:value-of select="historyCode"/></span></td>
						<td><span class="newff"><xsl:value-of select="historyTooth"/></span></td>
						<td><span class="newff"><xsl:value-of select="historyDos"/></span></td>
					</tr>
				</xsl:for-each>
				</table> -->

				<span>
					<p style="text-align: center"></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Comments :<span class="newff"><xsl:value-of select="data/comments"/></span></td>
					</tr>
				</table>
				<span>
					<p style="text-align: center"></p>
				</span>
				<table class="table table-bordered table-striped">
					<tr>
						<td>Benefits Verified by : <span class="newff"><xsl:value-of select="data/benefits"/></span></td>
						<td>Date : <span class="newff"><xsl:value-of select="data/date"/></span></td>
					</tr>
				</table>
			</div>
		</xsl:if>

		</div>
    <xsl:if test="reportDataInd = ''">
		<div style="display: flex;justify-content: center;" class="text-light bg-secondary">
			<xsl:value-of select="this.message"/></div>
		</xsl:if>
		<!--showReportData DIV  -->
	</div>

    </body>
    </html>
    </xsl:template>
    </xsl:stylesheet>
