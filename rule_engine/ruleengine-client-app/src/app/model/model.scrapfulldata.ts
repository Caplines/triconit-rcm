import {ScrapFullPatientDataModel}  from './model.scrapfull.patientdata';

export class ScrapFullDataModel extends ScrapFullPatientDataModel{
	officeId:string="";
    userName:string="";
	password:string="";
    siteDetailId:string="";
    //sheetId:string="";
	locationProvider:string="";
	siteId:string="";
    //sheetSubId:string="";
    siteName:string="";
    siteUrl:string="";
    otpValue:string="";
    processId:string=null;
    dto = new Array() as Array<ScrapFullPatientDataModel>;
}