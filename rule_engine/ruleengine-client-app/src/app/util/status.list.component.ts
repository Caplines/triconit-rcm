import {StatusModel} from '../model/model.status';

export  class StatusList {//{extends StatusModel{
	
	statses: any=[];//Completed, Accepted, Proposed, Rejected, Referred
    status: string="";
    insTypes: any=[];//Primary, Secondary
    insType: string="";

    constructor(){
    	//super("","");
    	this.setUpStatus();
    	this.setUpInsurance();
    }
    
    setUpStatus(){
    	
    	this.statses.push(new StatusModel("All","All"));
    	this.statses.push(new StatusModel("W","Post to Walkout"));
    	//this.statses.push(new StatusModel("Others","Others"));
    	this.statses.push(new StatusModel("P","Proposed"));
    	this.statses.push(new StatusModel("C","Complete"));
    	this.statses.push(new StatusModel("A","Accepted"));
    	this.statses.push(new StatusModel("R","Rejected"));
    	this.statses.push(new StatusModel("F","Referred"));
    	
    }
    setUpInsurance(){
    	
    	this.insTypes.push(new StatusModel("Primary","Primary"));
    	this.insTypes.push(new StatusModel("Secondary","Secondary"));
    	
    	
    }

/*
P = Proposed
C = Complete
A= Accepted
W = post to Walkout
R = Rejected
F = Referred
*/


}
