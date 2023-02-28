import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { TokenStorageService } from '../../service/token-storage.service';
import Utils from '../../util/utils';
import { ApplicationServiceService } from 'src/app/service/application-service.service';


@Component({
  selector: 'app-production-component',
  templateUrl: './production.component.html',
  styleUrls: ['./production.component.css']
})
export class ProductionComponent implements OnInit {

  productionData:any=[{"fname":"Amit","total":1,"days":7},{"fname":"Ram","total":2,"days":5}];
  alert:any={'showAlertPopup':false,'alertMsg':''}
  total:any=0;
  days:any=0;
  constructor(private appService: ApplicationServiceService) { }

  ngOnInit(): void {
   
  }

 save(startDate:string,endDate:string){
  this.total=0;
  this.days=0;
  if(startDate !=='' && endDate !==''){
  this.appService.saveProductionData({ "startDate": startDate,"endDate":endDate }, (callback: any) => {
    if (callback.status == 200 && callback.data) {
   //this. productionData = callback.data;
   this. productionData.forEach((x:any) =>{
            Object.entries(x).forEach(([key, value])=>{
             if(key=='total')
                this.total=this.total+value;            
             if(key=='days')
              this.days=this.days+value;           
            })
   });
  } else this.alert.alertMsg = callback.message ? callback.message :'Something went wrong';
    });
  }
       //clear date field
        (<HTMLInputElement>document.getElementById("sDate")).value='';
        (<HTMLInputElement>document.getElementById("eDate")).value='';
 }
}



