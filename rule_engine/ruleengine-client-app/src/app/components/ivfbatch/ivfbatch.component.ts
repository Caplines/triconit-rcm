import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {IVFBatchModel} from "../../model/model.ivfbatch";
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";

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
  showPopup: boolean = false;
  showLoading: boolean = false;
  showIvfData: boolean = false;

  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute) {
	  this.offices =this.route.snapshot.data['offs'].data;
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
						if (value.length>0){
							field2data.push(value[0].trim().replace("\n", ""));
								
						}else{
							field2data.push("");
							
						}
						if (value.length>1){
							field1data.push(value[1].trim().replace("\n", ""));
								
						}else{
							field1data.push("");
							
						}
						console.log(value[1]);
						}
				}
			});
			
			ths.ivfm.ivfId=field1data.toString().trim().replace("\n", "");
			ths.ivfm.treatmentPlanId=field2data.toString().trim().replace("\n", "");
			console.log(ths.ivfm.ivfId);
			}, 0);
		
		
	  }
  
}
