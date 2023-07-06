import { Component } from '@angular/core';
import { ApplicationServiceService } from '../service/application-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import Utils from '../util/utils';
import { DownLoadService } from '../service/download.service';

@Component({
  selector: 'app-treatment-plan',
  templateUrl: './treatment-plan.component.html',
  styleUrls: ['./treatment-plan.component.scss']
})
export class TreatmentPlanComponent {

  claimUuid:any;
  tpData:any=[];
  showLoader:boolean=false;
  count:any={'Fee':0,'Ins':0,'Pat':0};

  constructor(private _service: ApplicationServiceService,private router:Router,private title:Title,private downloadService:DownLoadService) {
    title.setTitle(Utils.defaultTitle + "Treament Plan")
  }


  ngOnInit() {
    this.claimUuid = this.router.url.split("/")[2];   //Claim Uuid from url
    this.getTpData();

  }

  getTpData(){
    this.showLoader=true;
    this._service.fetchTpData(this.claimUuid,(res:any)=>{
      if(res.status){
      this.showLoader=false;
        console.log(res);
        this.tpData=res.data;
        this.totalFee(this.tpData);
        this.totalIns(this.tpData);
        this.totalPat(this.tpData);
        
      }
    })
  }

  totalFee(data:any){
      this.count.Fee == data.forEach((e:any)=>{
        this.count.Fee = this.count.Fee + e.fee;
      })
  }

  totalIns(data:any){
    this.count.Ins == data.forEach((e:any)=>{
      this.count.Ins = this.count.Ins + e.ins;
    })
  }

  totalPat(data:any){
    this.count.Fee == data.forEach((e:any)=>{
      this.count.Pat = this.count.Pat + e.pat;
    })
  }

  goToClaimDetailPage() {
    window.location.href = "/billing-claims/"+ this.claimUuid+"";
    window.close();
  }

  downloadPdf() {
    if(this.tpData.length!=0){
    let data = { "fileName": "TPlan_Link", "data": this.tpData,"fee":this.count.Fee,"ins":this.count.Ins,"pat":this.count.Pat,"claimUuid":this.claimUuid};
    this._service.TplanPdfDownload(data, "pdf", (res: any) => {
      if (res.status === 200) {
        this.downloadService.saveBolbData(res.body, "TPlan_Link.pdf");
      } else {
        console.log("something went wrong");
      }
    })
  }
}
}
