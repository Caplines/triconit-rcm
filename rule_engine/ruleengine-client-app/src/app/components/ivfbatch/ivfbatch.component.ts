import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {IVFBatchModel} from "../../model/model.ivfbatch";
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-ivfbatch',
  templateUrl: './ivfbatch.component.html',
  styleUrls: ['./ivfbatch.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class IVFBatchComponent implements OnInit {
  ivfm: IVFBatchModel = new IVFBatchModel();
  errorMessage: string;
  offices:any;
  ivfmData: any;
  showPopup: boolean = false;
  showLoading: boolean = false;
  past:any;

  constructor(public accountService: AccountService, public router: Router) {
      this.accountService.getOffices((result) => {
          this.offices=result;
        });
  }


  validateIVF() {
	  this.showLoading = true;
 	  if(this.ivfm.officeId) {
          this.accountService.validateIVFBatch(this.ivfm,(result) => {
              this.showLoading = false;
              console.log(result);
              if (result.status=='OK'){this.ivfmData = result.data;
			  this.showPopup=true;
              }
        });
	  }else{
	      this.showLoading = false;

	  }
  }
 
  onPaste(evt) {
		let content = '';
		if (evt.clipboardData && evt.clipboardData.getData) {
			content = evt.clipboardData.getData('text/plain');
		} 
		let words = content.replace(/\n/g, "");
		this.past = words.replace(/\s/g, ",");
		console.log(this.past);
		
		
		
		
	  }
  
  getDataFromPasteEvent(evt) {
	  
	  this.ivfm.ivfId=this.past;

  
}
