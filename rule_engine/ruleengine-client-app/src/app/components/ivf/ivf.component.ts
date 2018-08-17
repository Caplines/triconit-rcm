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
  showPopup: boolean = false;
  showLoading: boolean = false;
  showIvfData: boolean = false;

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
		this.showLoading = true;
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
  
  
}
