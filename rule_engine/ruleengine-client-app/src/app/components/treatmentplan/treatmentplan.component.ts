import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {TreatmentPlanModel} from "../../model/model.treatmentplan";
import {TPCalimDetailsModel} from "../../model/model.tpclaimDetail";
import {IgnoreDataModel} from "../../model/model.ignoredata";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
import {ClaimTreatmentTextModel} from "../../model/model.claimtreatmenttext"; 
import Utils from '../../util/utils';

@Component({
  selector: 'app-treatmentplan',
  templateUrl: './treatmentplan.component.html',
  styleUrls: ['./treatmentplan.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TreatmentPlanComponent implements OnInit {
  treatmentplan: TreatmentPlanModel = new TreatmentPlanModel();
  detData: TPCalimDetailsModel= new TPCalimDetailsModel();
  ignoreDataArray : Array<IgnoreDataModel>= [];
  errorMessage: string;
  offices:any;
  treatmentplanData: any;
  tpclDetail: any;

  arrayOfKeys: any;
  showLoading: boolean = false;
  showTreatmentPlanData: boolean = false;
  mainServiceSelection: boolean=false;
  treatmentPlanId: any;
  officeId: any;

  selectedIndex: any;
  selectedSubIndex: any;
  //claim and Treatment Text 
  hd1:string="";
  hd2:string="";
  hd3:string="";
  loadingSubdata=false;
  showStep3=false;
  vd=true;

  
  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  let ut =Utils.fetchUserTypeFromLocalStorage();
	  if (ut=='1'){
			this.hd1=ClaimTreatmentTextModel.treatmentTXPIDs;
			this.hd3=ClaimTreatmentTextModel.treatmentPlanId;
			this.hd2=ClaimTreatmentTextModel.treatmentPlan;
		}else{
			this.hd1=ClaimTreatmentTextModel.claimIds;
			this.hd3=ClaimTreatmentTextModel.claimId;
			this.hd2=ClaimTreatmentTextModel.claim;
			
		}
  }

  ngOnInit() {
	
  }

  
  generateTreatmentPlanId() {
	if(this.treatmentplan.officeId) {
		this.showLoading = true;
		this.applicationService.generateTreatmentPlanId(this.treatmentplan,(result) => {
			this.showLoading = false;
			
			if (result.status=='OK' && result.data){
				this.treatmentplanData = result.data;
				this.arrayOfKeys = Object.keys(this.treatmentplanData);
				this.showTreatmentPlanData = true;
				//console.log(this.treatmentplanData);
                //console.log(this.isEmpty(this.treatmentplanData));
				if (this.isEmpty(this.treatmentplanData)){
					alert("The Patient ID was NOT found. Check Patient ID and ensure that Treatment Plan has been processed for the Patient");
				}
			} else {
				if (!result.data){
					alert("The Patient ID was NOT found. Check Patient ID and ensure that Treatment Plan has been processed for the Patient");
				}
				this.showTreatmentPlanData = false;
			}
		});
	}
  }
  
  isEmpty(obj) {
    for(var key in obj) {
      if(obj.hasOwnProperty(key))
        return false;
    }
    return true;
  }
  
  
  getTPorClaimData(value, index) {
	    let ths=this;
	    ths.ignoreDataArray=[];
	  
	    //console.log(ths.tpclDetail[ths.treatmentPlanId]);
	    if (typeof ths.tpclDetail != 'undefined')ths.tpclDetail[ths.treatmentPlanId]=[];
	    ths.selectedIndex = -1;
	    ths.treatmentPlanId = value;
	    ths.officeId=this.treatmentplan.officeId;
		let ut =Utils.fetchUserTypeFromLocalStorage();
		ths.detData.dataId=value;
		ths.detData.officeId=this.treatmentplan.officeId;
		ths.detData.type=ut;
		ths.loadingSubdata=true;
		ths.selectedSubIndex=index;
		ths.mainServiceSelection=false;
		ths.applicationService.postData(ths.detData,"/getTreatmentClaimData",(result) => {
			ths.vd=true;
			ths.tpclDetail={};
			ths.tpclDetail[value]=result.data[value];
			ths.initializeIgnoreData(result.data[value],value);
			ths.getTreatmentPlanData(value, index);
			ths.loadingSubdata=false;
			
			
		});
		
  }
  
  initializeIgnoreData(d,v){
	  let ths=this;
	  ths.ignoreDataArray=[];
	  d.forEach( (element,i) => {
		  let ig:IgnoreDataModel = new IgnoreDataModel();
			  ig.serviceCode=element.serviceCode;
			  ig.description=element.treatmentPlanDetails.description;
			  ig.tooth=element.tooth;
			  ig.surface=element.surface;
			  ig.selected=true;
			  ig.index=i;
			  ths.ignoreDataArray.push(ig);
		});
	  ths.tpclDetail[v].forEach( (element,i) => {
		  element.selected=true;
	  });
  }
  
  getTreatmentPlanData(value, index) {
	this.treatmentPlanId = value;
	this.officeId=this.treatmentplan.officeId;
	this.selectedIndex = index;
  }
  
  closeTreatmentPlanDataModal() {
	this.selectedIndex = null;
	this.selectedSubIndex=null;
	this.showTreatmentPlanData=false;
	this.loadingSubdata=false;
	this.showStep3=false;
	this.ignoreDataArray=[];
  }

  selectDeselectData(d,k,event){
	  let ths=this;
	  ths.showStep3=false;
	  if (event.target.checked){
		  ths.ignoreDataArray.forEach( (element,i) => {
			  element.selected=true;
		  });
		  
		  ths.tpclDetail[k].forEach( (e,i) => {
			  e.selected=true;
		  });
	  }else{
		  ths.ignoreDataArray.forEach( (element,i) => {
			  element.selected=false;
		  });
		  ths.tpclDetail[k].forEach( (e,i) => {
			  e.selected=false;
		  });
	  }
  }
  
  perPairIngoreData(d,k,i,event){
	  let ths=this;
	  
	  ths.ignoreDataArray[i].selected=event.target.checked;
	  ths.tpclDetail[k][i].selected=event.target.checked;
	  //ignoreDataArray;
	  
	  let m=false;
	  ths.ignoreDataArray.forEach( (element,i) => {
		  if (!element.selected)m=true;
	  });
	  let el:any=document.getElementById("sd"+k);
	 if (m){
		 el.checked=false;
	 }else{
		 el.checked=true;
	 }
	  
  }
  
  showNextStep(){
	  this.showStep3=true;
  }
  
  checkFinalArrayLength():boolean{
	  let vis:boolean=false;
	  this.ignoreDataArray.forEach( (element,i) => {
		  if (element.selected)vis=true;
	  });
	  return vis;
  }
  
  makeMainServiceSelection(v){
	  this.mainServiceSelection=v;
	  this.showStep3=v;
  }
  
  receiveChildrenEmitter(event) {
	  if(event['action'] == "vrun") {
			this.vd=false;
		}
  }
}
