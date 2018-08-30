import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {TreatmentPlanModel} from "../../model/model.treatmentplan";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-treatmentplan',
  templateUrl: './treatmentplan.component.html',
  styleUrls: ['./treatmentplan.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TreatmentPlanComponent implements OnInit {
  treatmentplan: TreatmentPlanModel = new TreatmentPlanModel();
  errorMessage: string;
  offices:any;
  treatmentplanData: any;
  arrayOfKeys: any;
  showLoading: boolean = false;
  showTreatmentPlanData: boolean = false;
  treatmentPlanId: any;
  selectedIndex: any;
  
  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute) {
	  this.offices =this.route.snapshot.data['offs'].data;
  }

  ngOnInit() {
	
  }

  
  generateTreatmentPlanId() {
	if(this.treatmentplan.officeId) {
		this.showLoading = true;
		this.accountService.generateTreatmentPlanId(this.treatmentplan,(result) => {
			this.showLoading = false;
			if (result.status=='OK' && result.data){
				this.treatmentplanData = result.data;
				this.arrayOfKeys = Object.keys(this.treatmentplanData);
				this.showTreatmentPlanData = true;
				if (this.isEmpty(this.treatmentplanData)){
					alert("No Data Found.");
				}
			} else {
				this.showTreatmentPlanData = false;
			}
		});
	}
  }
  
  isEmpty(obj) {
    for(var key in obj) {
      if(obj.hasOwnProperty(key))
        return false;
    }
    return true;
  }
  
  getTreatmentPlanData(value, index) {
	this.treatmentPlanId = value;
	this.selectedIndex = index;
  }
  
  closeTreatmentPlanDataModal() {
	this.selectedIndex = null;
	this.showTreatmentPlanData=false;
  }


}
