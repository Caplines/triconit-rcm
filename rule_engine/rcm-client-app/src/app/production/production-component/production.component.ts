import { Component, Input, OnInit } from '@angular/core';
import Utils from '../../util/utils';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { Title } from '@angular/platform-browser';
import { DownLoadService } from 'src/app/service/download.service';


@Component({
  selector: 'app-production-component',
  templateUrl: './production.component.html',
  styleUrls: ['./production.component.css']
})
export class ProductionComponent implements OnInit {

  productionData:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':''}
  total:any=0;
  days:any=0;
  date:any;
  selectedDate:any={'startDate':'','endDate':''};
  loader:any= {'showLoader':false,'exportPDFLoader':false,'exportCSVLoader':false,'fetch':false};
  isDataAvailable:boolean=false;
  clientName:string='';
  fetchbtnDisable=true;
  constructor(private appService: ApplicationServiceService,private title:Title,private downloadService:DownLoadService) { 
     title.setTitle(Utils.defaultTitle + "Production")
    }

  ngOnInit(): void {
   
    this.clientName = localStorage.getItem("selected_clientName");
  }

 save(){
  this.fetchbtnDisable=false;
  this.loader.showLoader=true;
  this.loader.fetch = true;
  this.total=0;
  this.days=0;
  this.appService.saveProductionData({ "startDate": this.selectedDate.startDate,"endDate": this.selectedDate.endDate }, (callback: any) => {
    if (callback.status == 200 && callback.data) {
      this.loader.showLoader=false;
      this.loader.fetch = false;
      this.fetchbtnDisable=false;
   this.productionData = callback.data;
   if( this.productionData.length==0){
    this.loader.showLoader = true;
    this.isDataAvailable= true;
   }
   this.productionData.forEach((x:any) =>{
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

 selectDate(event:any,from:any){
  if(from == 'start') this.selectedDate.startDate = event.target.value ;
  if(from=='end') this.selectedDate.endDate = event.target.value;
  this.fetchbtnDisable=true;
  console.log(this.selectedDate.startDate,this.selectedDate.endDate );
  
  
 }

exportToCsv() {
  this.loader.exportCSVLoader=true;
  let options: any = {
    showLabels: true,
    headers: ["Client","Associate Name","Total Production", "Average Production (Per Day)"]
  }
  let excelData: any;
  excelData = [...this.productionData];
  excelData = excelData.map((e: any) => {
    if (e.cd) {
      let date: Date = new Date(e.cd);
      e = { ...e, cd: `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` }
    }
    else {
      e = {...e,cd : ''};
    }
    if(e.fname){
      e['fullName'] = e.fname ? e.fname+ " "+ e.lname : "-";
    }
    return e;
  })
  excelData = excelData.map(({uuid,fname,lname,cd,...excelData }: any) => excelData) //to remove required properties in excel
  excelData = excelData.map((e:any)=>{
    return{
      "Client":e.companyName,
      "Associate Name":e.fullName,
      "Total Production":e.total,
      "Average Production (Per Day)":e.days,
    }
  })
  excelData.unshift(                                        //method is used to show Total Row in CSV.
  {
    "OfficeName":'Total',
    "ClientNme":'-',
    "Total Production":this.total,
    "Average Production (Per Day)":this.days
  }
) 
  this.date = new Date();
  this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
  
  new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_Production_${this.date}`, options);
  this.loader.exportCSVLoader=false;

}

downloadPdf(){
  this.loader.exportPDFLoader = true;
  if(this.productionData.length!=0){
  let data = {"fileName":"Production","data": this.productionData,"clientName": this.clientName};
  this. appService.productionPdfDownload(data,"pdf",(res: any) => {
    if (res.status === 200){
      console.log(res.body);
      this.downloadService.saveBolbData(res.body, "Production.pdf");
      this.loader.exportPDFLoader = false;
    }else{
      console.log("something went wrong");
      this.loader.exportPDFLoader = false;
    }
  })
}
}
}



