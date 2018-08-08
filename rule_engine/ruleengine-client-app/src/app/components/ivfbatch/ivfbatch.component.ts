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
  userName: any;
  userType: any;
  ivfmData: any;
  arrayOfKeys: any;
  showPopup: boolean = false;
  showLoading: boolean = false;

  constructor(public accountService: AccountService, public router: Router) {
      this.accountService.getOffices((result) => {
          this.offices=result;
        });
  }

  ngOnInit() {
  }

  validateIVF() {
	  this.showLoading = true;
	  // var xx={"message":"","data":{"IVF ID(110) Treatment Plan ID
		// (686711)":[{"ruleId":3,"ruleName":"IVF Details not
		// matched","message":"<b class='error-message-api'>No Data Found in IVF
		// Sheet.</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF Details
		// not matched","message":"<b class='error-message-api'>Invalid
		// Treatment Plan</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF
		// Details not matched","message":"<b class='error-message-api'>No Data
		// Found in IVF Sheet.</b>","resultType":"FAIL"}],"IVF ID(2) Treatment
		// Plan ID (6867)":[{"ruleId":3,"ruleName":"IVF Details not
		// matched","message":"<b class='error-message-api'>No Data Found in IVF
		// Sheet.</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF Details
		// not matched","message":"<b class='error-message-api'>Invalid
		// Treatment Plan</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF
		// Details not matched","message":"<b class='error-message-api'>No Data
		// Found in IVF Sheet.</b>","resultType":"FAIL"}]},"status":"OK"};
	  // this.dummyData = this.ivfJsonData.data;
	  
	  console.log(this.ivfmData);
	  // return ;
      // this.errorMessage = "DDD";
	  if(this.ivfm.officeId) {
          this.accountService.validateIVF(this.ivfm,(result) => {
              this.showLoading = false;
              console.log(result);
              if (result.status=='OK'){
                this.ivfmData = result.data;
          	    this.arrayOfKeys = Object.keys(this.ivfmData);
			    this.showPopup=true;
              }
			  // this.setUser();
        });
	  }else{
	      this.showLoading = false;

	  }
  }
  
  onPaste(evt) {
		let content = '';
		let clipRows:any;
		let field1data: any = [];
		let field2data: any = [];
        let ths=this;
			if (evt.clipboardData && evt.clipboardData.getData) {
				content = evt.clipboardData.getData('text/plain');
			} 
			clipRows = content.split("\n");
			setTimeout(() => {
			for (let i=0; i<clipRows.length; i++) {
		        clipRows[i] = clipRows[i].split(String.fromCharCode(9));
		    }
			clipRows.forEach(function(value){
				if(value && value != ''){
					if(value && value != ''){
						console.log(value[1]);
						field1data.push(value[1].trim().replace("\n", ""));
						field2data.push(value[0].trim().replace("\n", ""));
					}
				}
			});
			
			ths.ivfm.ivfId=field1data.toString().trim().replace("\n", "");
			ths.ivfm.treatmentPlanId=field2data.toString().trim().replace("\n", "");
			console.log(ths.ivfm.ivfId);
			}, 0);
		
		
	  }
  
}
