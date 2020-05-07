import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {DiagnosticModel} from "../../model/model.diagnostic";
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-diagnostic',
  templateUrl: './diagnostic.component.html',
  styleUrls: ['./diagnostic.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class DiagnosticComponent implements OnInit {
  diagm: DiagnosticModel = new DiagnosticModel();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
  showPopup: boolean = false;
  showLoading: boolean = false;
  showDiagData: boolean = false;

  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.offices.push({"name":"All OFFICES","uuid":"All offices"});
  }

  ngOnInit() {
  }

  doDiagnosticCheck() {
	if(this.diagm.officeId) {
		this.showPopup=true;
		this.showLoading = true;
	}
  }
  
  receiveChildrenEmitter(event) {
	if(event['action'] == "showLoading") {
		this.showLoading = event['value'];
	} else if(event['action'] == "showDiagData") {
		this.showDiagData = event['value'];
		if(!this.showDiagData) {
			this.showPopup = false;
		}
	}else if(event['action'] == "callAgain") {
		this.showDiagData=false;
		this.showPopup=false;
		this.showLoading = false;
		setTimeout(() => {
			this.doDiagnosticCheck();	
		}, 500);
		
	}
  }
  
  
}
