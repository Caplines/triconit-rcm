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
		this.emitToParent.emit({action: "showLoading", value: true});
		this.accountService.validateIVF(this.ivfm, this.ivfValidateName, (result) => { 
			if (result.status=='OK'){
				this.ivfmData = result.data;
				this.arrayOfKeys = Object.keys(this.ivfmData);
				this.emitToParent.emit({action: "showIvfData", value: true});
			} else {
				this.emitToParent.emit({action: "showIvfData", value: false});
			}
			this.emitToParent.emit({action: "showLoading", value: false});
		});
	}
	
	closePopup() {
		this.emitToParent.emit({action: "showIvfData", value: false});
	}
	
	
}