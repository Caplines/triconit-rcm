import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { ActivatedRoute } from '@angular/router';
import { ClaimService  } from '../service/claim.service';

@Component({
	selector: 'app-report',
	templateUrl: './report.component.html',
	styleUrls: ['./report.component.scss'],
	encapsulation: ViewEncapsulation.None,
})
export class ReportComponent implements OnInit {
	reportDataInd: any;
	ivId:string="";

	constructor(private route: ActivatedRoute,private title : Title,
		private claimService: ClaimService) {
		title.setTitle("RCM tool - IV Details");
	 
	}

	ngOnInit() {

		this.route.paramMap.subscribe(params => {
			//this.ivId=params.get('ivid') || '';
			 this.fetchIvDetails(params.get('uuid'));
			});
		  
	}

	fetchIvDetails(clamId:string){

		let ths=this;
		ths.claimService.fetchIvDetails(clamId,"",(res:any)=>{
		  if (res.status=== 200){
			this.reportDataInd = res.data;
		  }
		 
		});
		

	}

}