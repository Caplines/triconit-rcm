import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {IVFBatchPreModel} from "../../model/model.ivfbatchpre";
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-ivfbatchpre',
  templateUrl: './ivfbatchpre.component.html',
  styleUrls: ['./ivfbatchpre.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class IVFBatchPreComponent implements OnInit {
  ivfm: IVFBatchPreModel = new IVFBatchPreModel();
  errorMessage: string;
  offices:any;
  showPopup: boolean = false;
  showLoading: boolean = false;
  showIvfData: boolean = false;
  past:any;

  constructor(public accountService: AccountService, public router: Router) {
      this.accountService.getOffices((result) => {
          this.offices=result;
        });
  }

  ngOnInit() {
  }
  
  validateIVF() {
	if(this.ivfm.officeId) {
		this.showPopup=true;
	}
  }
  
  receiveChildrenEmitter(event) {
	if(event['action'] == "showLoading") {
		this.showLoading = event['value'];
	} else if(event['action'] == "showIvfData") {
		this.showIvfData = event['value'];
		if(!this.showIvfData) {
			this.showPopup = false;
		}
	}
  }
 
  onPaste(evt) {
		let content = '';
		  console.log(2);
		if (evt.clipboardData && evt.clipboardData.getData) {
			content = evt.clipboardData.getData('text/plain');
		} 
		let words = content.replace(/\n/g, "");
		this.past = words.replace(/\s/g, ",");
		setTimeout(() => {
			this.ivfm.ivfId =this.past;
		  }, 0);
		
		
	  }
  
  getDataFromPasteEvent(evt) {
	  // console.log(3);
	  // console.log(evt);
	  // console.log();
	  // console.log(this.past);
	  // this.ivfm.ivfId=this.ivfm.ivfId+this.past;
      // this.past="";
  
}
}
