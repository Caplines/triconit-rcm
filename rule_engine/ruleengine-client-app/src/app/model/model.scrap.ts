import { ScrapUserDataModel } from "./model.scrapuserdata";

export class ScrapModel {
	listUd = new Array() as Array<ScrapUserDataModel>;
	officeId:string="";
    scrapType:string ="";
    onlyDisplay:boolean=false;
	isdataFromUi:boolean=false;
	username:string="";
	password:string="";
	start:string="A";
	end:string="Z";
	
}