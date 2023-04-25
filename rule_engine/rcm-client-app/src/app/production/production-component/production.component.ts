import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { TokenStorageService } from '../../service/token-storage.service';
import Utils from '../../util/utils';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { Title } from '@angular/platform-browser';


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
  log : any=[];
  constructor(private appService: ApplicationServiceService,private title:Title) { 
     this.log = [];
     title.setTitle("Production")
    }

  ngOnInit(): void {
   
  }

 save(startDate:string,endDate:string){
  this.total=0;
  this.days=0;
  if(startDate !=='' && endDate !==''){
  this.appService.saveProductionData({ "startDate": startDate,"endDate":endDate }, (callback: any) => {
    if (callback.status == 200 && callback.data) {
      this.log=callback.data;
   this. productionData = callback.data;
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
       // (<HTMLInputElement>document.getElementById("sDate")).value='';
       // (<HTMLInputElement>document.getElementById("eDate")).value='';
 }
 saveToPdf(divName: any) {
  let m:any=document.querySelector(".table-wrapper-scroll-y");
  m.classList.remove('table-wrapper-scroll-y');
  m.classList.remove('table-inner-scrollbar');
  html2canvas(<any>document.getElementById(divName)).then(canvas => {
    const content = canvas.toDataURL('image/png');
    let pdf = new jsPDF('p', 'mm', 'a4');
    let width = pdf.internal.pageSize.getWidth();
    let height = canvas.height * width / canvas.width;
    pdf.addImage(content, "PNG", 0, 0, width, height)
    this.date = new Date();
    this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    pdf.save(`${localStorage.getItem("selected_clientName")}_Production_${this.date}`);
    m.classList.add('table-wrapper-scroll-y');
    m.classList.add('table-inner-scrollbar');
  });

}

exportToCsv() {
  let options: any = {
    showLabels: true,
    headers: ["Associate Name","Total Production", "Average Production (Per Day)"]
  }
  let excelData: any;
  excelData = [...this.log];
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
      "Associate Name":e.fullName,
      "Total Production":e.total,
      "Average Production (Per Day)":e.days,
    }
  })
  this.date = new Date();
  this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
  
  new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_Production_${this.date}`, options);

}
}



