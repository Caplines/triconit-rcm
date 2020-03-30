import {StatusModel} from '../model/model.status';

export  class StatusList {//{extends StatusModel{
	
	statses: any=[];//Completed, Accepted, Proposed, Rejected, Referred
    status: string="";

    constructor(){
    	//super("","");
    	this.setUpStatus();
    }
    
    setUpStatus(){
    	
    	this.statses.push(new StatusModel("All","All"));
    	this.statses.push(new StatusModel("W","Post to Walkout"));
    	this.statses.push(new StatusModel("Others","Others"));
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
