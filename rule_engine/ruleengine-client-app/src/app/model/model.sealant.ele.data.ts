import {SealantElegPatientDataModel}  from './model.sealant.eleg.patientdata';

export class SealantEleDataModel extends SealantElegPatientDataModel{
	officeId:string="";
    userName:string="";
	password:string="";
    siteDetailId:string="";
    //sheetId:string="";
	//sheetSubId:string="";
	locationProvider:string="";
	siteId:string="";
    //sheetSubId:string="";
    siteName:string="";
    location:string="";
	listUd = new Array() as Array<SealantElegPatientDataModel>;
}