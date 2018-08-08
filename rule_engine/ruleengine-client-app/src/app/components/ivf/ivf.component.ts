import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {IVFModel} from "../../model/model.ivf";
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-ivf',
  templateUrl: './ivf.component.html',
  styleUrls: ['./ivf.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class IVFComponent implements OnInit {
  ivfm: IVFModel = new IVFModel();
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
	  //var xx={"message":"","data":{"IVF ID(110) Treatment Plan ID (686711)":[{"ruleId":3,"ruleName":"IVF Details not matched","message":"<b class='error-message-api'>No Data Found in IVF Sheet.</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF Details not matched","message":"<b class='error-message-api'>Invalid Treatment Plan</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF Details not matched","message":"<b class='error-message-api'>No Data Found in IVF Sheet.</b>","resultType":"FAIL"}],"IVF ID(2) Treatment Plan ID (6867)":[{"ruleId":3,"ruleName":"IVF Details not matched","message":"<b class='error-message-api'>No Data Found in IVF Sheet.</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF Details not matched","message":"<b class='error-message-api'>Invalid Treatment Plan</b>","resultType":"FAIL"},{"ruleId":3,"ruleName":"IVF Details not matched","message":"<b class='error-message-api'>No Data Found in IVF Sheet.</b>","resultType":"FAIL"}]},"status":"OK"};
	  //this.dummyData =  this.ivfJsonData.data;
	  
	  console.log(this.ivfmData);
	  //return ;
      //this.errorMessage = "DDD";
	  if(this.ivfm.officeId) {
          this.accountService.validateIVF(this.ivfm,(result) => {
              this.showLoading = false;
              console.log(result);
              if (result.status=='OK'){
                this.ivfmData = result.data;
          	    this.arrayOfKeys = Object.keys(this.ivfmData);
			    this.showPopup=true;
              }
			  //this.setUser();
        });
	  }else{
	      this.showLoading = false;

	  }
  }
  
  
}
