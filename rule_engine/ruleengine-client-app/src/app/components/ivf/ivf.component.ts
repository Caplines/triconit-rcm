import {Component, OnInit, ViewEncapsulation, Input} from '@angular/core';
import {IVFModel} from "../../model/model.ivf";
import {UserInputModel} from "../../model/model.userinput";
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute } from "@angular/router";
import {ClaimTreatmentTextModel} from "../../model/model.claimtreatmenttext"; 
import Utils from '../../util/utils';

@Component({
  selector: 'app-ivf',
  templateUrl: './ivf.component.html',
  styleUrls: ['./ivf.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class IVFComponent implements OnInit {
  @Input() treatmentPlanId:any;
  @Input() officeId:any;
  ivfm: IVFModel = new IVFModel();
  uim: UserInputModel = new UserInputModel();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
  showPopup: boolean = false;
  showLoading: boolean = false;
  showPopupInput: boolean = false;
  showIvfData: boolean = false;
  questionData:any;
  ut:string="1";
  //claim and Treatment Text 
  hd1:string="";
  hd2:string="";
  //hd3:string="";
  
  
  
  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute) {
	  
	//console.log(this.route.snapshot.data['offs'].data);
	this.offices =this.route.snapshot.data['offs'].data;
	
	/*  
      this.applicationService.getOffices((result) => {
          this.offices=result;
        });
    */
	this.ut =Utils.fetchUserTypeFromLocalStorage();
	if (this.ut=='1'){
		this.hd1=ClaimTreatmentTextModel.treatmentPlan;
		this.hd2=ClaimTreatmentTextModel.treatmentPlanId;
	}else{
		this.hd1=ClaimTreatmentTextModel.claim;
		this.hd2=ClaimTreatmentTextModel.claimId;
	}
  }

  ngOnInit() {
	this.ivfm.treatmentPlanId = this.treatmentPlanId;
	//console.log(this.officeId);
	if(this.officeId)this.ivfm.officeId = this.officeId;
	
  }

  validateIVF() {
	if(this.ivfm.officeId) {
		
		
		this.showLoading = true;
		if (this.ivfm.inputModeD){
		    this.uim.treatmentPlanId =this.ivfm.treatmentPlanId ;
		    this.uim.officeId =this.ivfm.officeId ;
		    this.uim.ivfId=this.ivfm.ivfId;
		    this.uim.inputMode=this.ivfm.inputModeD;
			//this.showLoading = true;
		    this.uim.status=this.ivfm.status;
		    //this.applicationService.validateIVF(this.ivfm, this.ivfValidateName, (result) => {
		    this.applicationService.validateIVF(this.uim, 'validateTreatmentPlan', (result) => {		
				if (result.status=='OK'){
				    this.showPopupInput = true;
					this.questionData = result.data;
					this.showIvfData=true;
				}
			  });
		    
		    
		}else{
			this.showPopup=true;
		}
	}
  }
  
  receiveChildrenEmitter(event) {
	if(event['action'] == "showLoading" ) {
		this.showLoading = event['value'];
	}else if(event['action']=="showQuestionData" ) {
		this.showIvfData = event['value'];
		this.showLoading = event['value'];
		if(!this.showIvfData) {
			this.showPopupInput = false;
		}
	} else if(event['action'] == "showValidation") {//From User input Screen
		this.showPopup=true;
		let ths=this;
		this.showLoading = true;
		this.ivfm.inputMode=false;
		this.showPopupInput = false;
		this.showIvfData=false;
		
	} else if(event['action'] == "showIvfData") {
		this.showIvfData = event['value'];
		if(!this.showIvfData) {
			this.showPopup = false;
		}
	}else if(event['action'] == "callAgain") {
		this.showIvfData=false;
		this.showPopup=false;
		this.showLoading = false;
		this.showPopupInput = false;
		setTimeout(() => {
			this.validateIVF();	
		}, 500);
		
	}
  }
  
  
}
