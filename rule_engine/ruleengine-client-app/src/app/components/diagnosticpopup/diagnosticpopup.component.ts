import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {ApplicationService} from "../../services/application.service";

@Component({
  selector: 'app-diagnosticpopup',
  templateUrl: './diagnosticpopup.component.html',
  styleUrls: ['./diagnosticpopup.component.css'],
  encapsulation: ViewEncapsulation.None
})

export class DiagnosticPopupComponent implements OnInit {
	@Input() diagm:any;
	@Input() showDiagData:boolean;
	@Output() emitToParent = new EventEmitter<any>();
	diagData: any;
	arrayOfKeys: any;
	countP:number =0;
	countF:number =0;
	countA:number =0;
	countE:number =0;
	
	
	constructor(public applicationService: ApplicationService) { }

	ngOnInit() {
		this.doDiagCheck();
		}
	
	doDiagCheck() {
	    let ths=this;	
		this.diagData="";
		this.applicationService.doDiagCheck(this.diagm, (result) => { 
			this.emitToParent.emit({action: "showLoading", value: false});
			console.log(result);
			if (result.status=='OK' && result.data){
				this.diagData = result.data;
				
				this.emitToParent.emit({action: "showDiagData", value: true});
				if (this.isEmpty(this.diagData)){
					alert("No Data Found.");
				}
			} else {
				if (!result.data){
					alert("No Data found.");
				}
			
				this.emitToParent.emit({action: "showDiagData", value: false});
			}
		});
	}
	
	closePopup() {
		this.emitToParent.emit({action: "showDiagData", value: false});
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
	
}