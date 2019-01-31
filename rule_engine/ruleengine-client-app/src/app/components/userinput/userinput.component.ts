import {Component, OnInit, ViewEncapsulation, Input} from '@angular/core';
import {UserInputModel} from "../../model/model.userinput";
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-userinput',
  templateUrl: './userinput.component.html',
  styleUrls: ['./userinput.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class UserInputComponent implements OnInit {
  @Input() officeId:any;	
  uim: UserInputModel = new UserInputModel();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
  showPopup: boolean = false;
  showLoading: boolean = false;
  showIvfData: boolean = false;

  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute) {
	  
	this.offices =this.route.snapshot.data['offs'].data;
  }

  ngOnInit() {
	if(this.officeId)this.uim.officeId = this.officeId;
	
  }

  getuserQuestions(){
	  this.accountService.getUserInputs(this.uim,(result) => {
		  
		  
	  });
  }
  
  receiveChildrenEmitter(event) {
	if(event['action'] == "showLoading") {
	
	}else if(event['action'] == "callAgain") {
		
	}
  }
  
  
}
