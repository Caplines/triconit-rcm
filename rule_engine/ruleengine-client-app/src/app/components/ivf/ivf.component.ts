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
  showPopup: boolean = false;
  showLoading: boolean = false;

  constructor(public accountService: AccountService, public router: Router) {
      this.accountService.getOffices((result) => {
          this.offices=result;
        });
  }

  ngOnInit() {
	this.setUser();
  }

  validateIVF() {
	  this.showLoading = true;
      //this.errorMessage = "DDD";
	  if(this.ivfm.officeId) {
          this.accountService.validateIVF(this.ivfm,(result) => {
              this.showLoading = false;
              console.log(result);
              if (result.status=='OK'){this.ivfmData = result.data;
			  this.showPopup=true;
              }
			  //this.setUser();
        });
	  }else{
	      this.showLoading = false;

	  }
  }
  
  setUser() {
	this.userName = localStorage.getItem('currentUser');
	this.userType = localStorage.getItem('roles').indexOf("ROLE_ADMIN")>0;
  }
  
}
