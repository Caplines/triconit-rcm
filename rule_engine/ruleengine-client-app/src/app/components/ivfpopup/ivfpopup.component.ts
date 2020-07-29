import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {ApplicationService} from "../../services/application.service";

@Component({
  selector: 'app-ivfpopup',
  templateUrl: './ivfpopup.component.html',
  styleUrls: ['./ivfpopup.component.css'],
  encapsulation: ViewEncapsulation.None
})

export class IVFPopupComponent implements OnInit {
	@Input() ivfm:any;
	@Input() ivfValidateName:any;
	@Input() showIvfData:boolean;
	@Output() emitToParent = new EventEmitter<any>();
	ivfmData: any;
	arrayOfKeys: any;
	countP:number =0;
	countF:number =0;
	countA:number =0;
	countE:number =0;
	activeClP:boolean=false;
	activeClF:boolean=false;
	activeClE:boolean=false;
	activeClA:boolean=false;
	
	filterType:any=['All'];
	
	
	constructor(public applicationService: ApplicationService) { }

	ngOnInit() {
		this.validateIVF();
		}
	
	validateIVF() {
	    let ths=this;	
		ths.countP=0;
		ths.countF=0;
		ths.countA=0;
		ths.countE=0;
		this.ivfmData=[];
		this.arrayOfKeys=[];
		this.applicationService.validateIVF(this.ivfm, this.ivfValidateName, (result) => { 
			this.emitToParent.emit({action: "showLoading", value: false});
			if (result.status=='OK' && result.data){
				this.ivfmData = result.data;
				//console.log(this.ivfmData);
				this.arrayOfKeys = Object.keys(this.ivfmData);
				Object.keys(ths.ivfmData).forEach(function(key) {
				    ths.countP=0;
					ths.countF=0;
					ths.countA=0;
					ths.countE=0;
					ths.ivfmData[key].countP=ths.countP;
					ths.ivfmData[key].countA=ths.countA;
					ths.ivfmData[key].countF=ths.countF;
					ths.ivfmData[key].countE=ths.countE;
				    
					
				    ths.ivfmData[key].forEach(function(a,i) {
				    	
				    	if (a.resultType.toLowerCase().indexOf("pass")>=0
				    		|| a.resultType.toLowerCase().indexOf("not applicable")>=0	){
				    		ths.countP= ths.countP+1;
				    		ths.ivfmData[key].countP=ths.countP;
				    	}
				    	if (a.resultType.toLowerCase().indexOf("alert")>=0){
				    		ths.countA=ths.countA+1;
				    		ths.ivfmData[key].countA=ths.countA;
				    	}
				    	if (a.resultType.toLowerCase().indexOf("fail")>=0){
				    		ths.countF=ths.countF+1;
				    		ths.ivfmData[key].countF=ths.countF;
				    	}
				      	if (a.resultType.toLowerCase().indexOf("exit")>=0){
				    		ths.countE=ths.countE+1;
				    		ths.ivfmData[key].countE=ths.countE;
				    	}
				    	});
				    ths.toggleResultFAILALERT();
				    
				});
				this.emitToParent.emit({action: "showIvfData", value: true});
				if (this.isEmpty(this.ivfmData)){
					alert("No Data Found.");
				}
			} else {
				if (!result.data){
					alert("No Data found.");
				}
			
				this.emitToParent.emit({action: "showIvfData", value: false});
			}
		});
	}
	
	closePopup() {
		this.emitToParent.emit({action: "showIvfData", value: false});
	}
	

	runAgain(){
		this.emitToParent.emit({action: "callAgain", value: true});
	}
	
	isEmpty(obj) {
	  for(var key in obj) {
		if(obj.hasOwnProperty(key))
		  return false;
		}
	  return true;
	}
	
	
	toggleResult(result:string){
		this.activeClP=this.activeClE=this.activeClA=this.activeClF=false;
		debugger;
		if (this.filterType && this.filterType.length==2) this.filterType=[];
		//this.filterType!=result    alert,pass
		if ((result=='fail' ||result=='pass' || result=='alert' || result=='exit')
			 && this.filterType.indexOf(result)<0){
	       this.filterType=[result];
	       
	        if (result=='fail') this.activeClF=true;
	        else if (result=='pass') this.activeClP=true;
	        else if (result=='alert') this.activeClA=true;
	        else if (result=='exit') this.activeClE=true;
	       
		}else{
		   this.filterType=['All'];
	     }
	}
	toggleResultFAILALERT(){
		this.activeClP=this.activeClE=this.activeClA=this.activeClF=false;
		 this.filterType=["fail","alert"];
	     this.activeClF=true;
	     this.activeClA=true;
	        
		
	}
	
}