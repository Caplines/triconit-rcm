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
  }

  ngOnInit() {
	this.setUser();
      /*
      this.accountService.getOffices((result) => {
        console.log(result);
        this.offices=result;
      });
      */
  }

  validateIVF() {
	  this.showLoading = true;
      this.errorMessage = "DDD";
          this.accountService.validateIVF(this.ivfm,(result) => {
              console.log(result);
			  this.ivfmData = result.data[0];
			  this.showPopup=true;
			  this.showLoading = false;			  
			  //this.setUser();
        });
  }
  
  setUser() {
	this.userName = localStorage.getItem('currentUser');
	this.userType = localStorage.getItem('roles').indexOf("ROLE_ADMIN")>0;
  }
  
}
