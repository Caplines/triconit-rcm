import { Component } from '@angular/core';
import { ApplicationServiceService } from '../service/application-service.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-treatment-plan',
  templateUrl: './treatment-plan.component.html',
  styleUrls: ['./treatment-plan.component.scss']
})
export class TreatmentPlanComponent {

  claimUuid:any;
  tpData:any=[];
  showLoader:boolean=false;

  constructor(private _service: ApplicationServiceService,private router:Router) {
    this._service.isTpIvfPage({'page':'tp',value:true});
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
        
      }
    })
  }


}
