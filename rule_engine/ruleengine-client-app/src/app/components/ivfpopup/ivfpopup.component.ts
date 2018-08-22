import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {AccountService} from "../../services/account.service";

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
	
	constructor(public accountService: AccountService) { }

	ngOnInit() {
		this.validateIVF();
	}
	
	validateIVF() {  
		this.accountService.validateIVF(this.ivfm, this.ivfValidateName, (result) => { 
			this.emitToParent.emit({action: "showLoading", value: false});
			if (result.status=='OK'){
				if (result.data){
				this.ivfmData = result.data;
				console.log(this.ivfmData);
				this.arrayOfKeys = Object.keys(this.ivfmData);
				this.emitToParent.emit({action: "showIvfData", value: true});
				if (this.isEmpty(this.ivfmData)){
					alert("No Data Found.");
				}
			 }else{
				this.emitToParent.emit({action: "showIvfData", value: false});
			 }
			} else {
				this.emitToParent.emit({action: "showIvfData", value: false});
			}
		});
	}
	
	closePopup() {
		this.emitToParent.emit({action: "showIvfData", value: false});
	}
	
	isEmpty(obj) {
	  for(var key in obj) {
		if(obj.hasOwnProperty(key))
		  return false;
		}
	  return true;
	}
	
}