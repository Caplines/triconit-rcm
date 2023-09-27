import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { ActivatedRoute, Router } from '@angular/router';
import { ClaimService  } from '../service/claim.service';
import { ApplicationServiceService  } from '../service/application-service.service';
import { DownLoadService } from '../service/download.service';
@Component({
	selector: 'app-report',
	templateUrl: './report.component.html',
	styleUrls: ['./report.component.scss'],
	encapsulation: ViewEncapsulation.None,
})
export class ReportComponent implements OnInit {
	reportDataInd: any;
	ivId:string="";
	message:string="";
	claimUuid:any;
	loader:any={exportPDFLoader:false};

	constructor(public appService: ApplicationServiceService,private route: ActivatedRoute,private title : Title,
		private claimService: ClaimService,private router:Router,private downloadService:DownLoadService) {
		title.setTitle("RCM tool - IV Details");
	}

	ngOnInit() {

		this.claimUuid = this.router.url.split("/")[2];   //Claim Uuid from url
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
			if (res.data==null){
                 this.message="IV Not Found.";
			}
		  }
		 
		});
		

	}
	goToClaimDetailPage() {
		window.location.href = "/billing-claims/"+ this.claimUuid+"";
		window.close();
	  }

	  downloadPdf() {
		this.loader.exportPDFLoader = true;
		let data = { "fileName": "Ivf", "data": this.reportDataInd,"claimUuid":this.claimUuid};
		this.appService.IvfPdfDownload(data, "pdf", (res: any) => {
		  if (res.status === 200) {
			this.downloadService.saveBolbData(res.body, "Ivf.pdf");
			this.loader.exportPDFLoader = false;
		  } else {
			console.log("something went wrong");
			this.loader.exportPDFLoader = false;
		  }
		})
	  }

}