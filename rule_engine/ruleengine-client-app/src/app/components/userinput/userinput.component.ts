import {Component, OnInit, ViewEncapsulation, Input} from '@angular/core';
import {UserInputModel} from "../../model/model.userinput";
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
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
  showLoading: boolean = false;
  showQuestionData: boolean = false;
  questionData:any;

  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute) {
	  
	this.offices =this.route.snapshot.data['offs'].data;
  }

  ngOnInit() {
	if(this.officeId)this.uim.officeId = this.officeId;
	
  }

  getuserQuestions(){
	this.showLoading = true;
	  this.applicationService.getUserInputs(this.uim,(result) => {		
		this.showLoading = false;
		if (result.status=='OK'){
			this.showQuestionData = true;
			this.questionData = result.data;	
		}
	  });
  }  
  
  receiveChildrenEmitter(event) {
	if(event['action'] == "showQuestionData") {
		this.showQuestionData = event['value']
	}
  }
  
  
}
