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
  ivfmData: any;
  arrayOfKeys:any;
  showPopup: boolean = false;
  showLoading: boolean = false;
  past:any;

  constructor(public accountService: AccountService, public router: Router) {
      this.accountService.getOffices((result) => {
          this.offices=result;
        });
  }

  ngOnInit() {
  }
  validateIVF() {
	  this.showLoading = true;
 	  if(this.ivfm.officeId) {
          this.accountService.validateIVFPreBatch(this.ivfm,(result) => {
              this.showLoading = false;
              console.log(result);
              if (result.status=='OK'){
            	    this.ivfmData = result.data;
            	    this.arrayOfKeys = Object.keys(this.ivfmData);
            	    this.showPopup=true;
              }
        });
	  }else{
	      this.showLoading = false;

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
