import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { ngxCsv } from 'ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import Utils from '../../util/utils';
@Component({
  selector: 'all-pendency',
  templateUrl: './all-pendency.component.html',
  styleUrls: ['./all-pendency.component.scss']
})
export class AllPendencyComponent {

  pendencyData:any=[];
  teamData:any=[];
  currentTeamId:any;
  showLoader:any={'loader':false,'exportPDFLoader':false,'exportCSVLoader':false};
  date:any;
  totalCount:any=[{"teamId":3,'count':0},{"teamId":4,"count":0},{"teamId":5,"count":0},{"teamId":6,"count":0},{"teamId":7,"count":0}];
  clientName:string='';

  constructor(private _service:ApplicationServiceService,private title:Title){
    title.setTitle(Utils.defaultTitle + "All Pendency")
  }
  ngOnInit(): void {
    this.teamData=[{"teamName":"Internal Audit","teamId":3},{"teamName":"Aging","teamId":4},{"teamName":"Posting","teamId":5},{"teamName":"Quality","teamId":6},{"teamName":"Billing","teamId":7}];
    this.getAllPendencyDetails();
    this.currentTeamId = localStorage.getItem("selected_teamId");
    this.clientName = localStorage.getItem("selected_clientName");
  }
  getAllPendencyDetails(){
    this.showLoader.loader=true;
    this._service.fetchAllPendency((res:any)=>{
      if(res.status==200){
        this.showLoader.loader=false;

          this.pendencyData = res.data.offices.map((office:any) => {
          const countObj = res.data.count.find((obj:any) => obj.officeName === office.name && obj.teamId != this.currentTeamId);
          const dateObj= res.data.dateCount.find((e:any)=> e.officeName === office.name && e.teamId != this.currentTeamId) ;
          return {
            ...office,
            count: countObj ? countObj.count : null,
            teamName:countObj ? countObj.teamName : null,
            minDate:dateObj? dateObj.minDate: null,
            teamId:countObj? countObj.teamId:null
          };
        });    //loops are used to merge count data (count) and DateCount data (minDate) into offices array with corresponding Team ID.
      }
    })
  }

  total(data:any){
        data.forEach((e:any)=>{
          this.totalCount.forEach((ele:any)=>{
            if(ele.teamId === e.teamId){
              if(!e.count){
                e.count = 0;
                ele.count = ele.count + e.count;
              }else{
                ele.count = ele.count + e.count;
              }
            }
          })
        })
        console.log(this.totalCount);
        
  }

  saveToPdf(divName:any){
    this.showLoader.exportPDFLoader=true;
    let m:any=document.querySelectorAll(".table-wrapper-scroll-y");
    m.forEach((e:any)=>{
      e.classList.remove('table-wrapper-scroll-y');
      e.classList.remove('table-inner-scrollbar');
    })
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      // Insert office name
      pdf.setFontSize(10);  // Adjust the font size as needed
      pdf.text(`RCM Tool-${this.clientName}`, 3, 10);
      pdf.addImage(content, "PNG", 0, 15, width, height);
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_All_Pendency_${this.date}`);
      this.showLoader.exportPDFLoader=false;
       m.forEach((e:any)=>{
      e.classList.add('table-wrapper-scroll-y');
      e.classList.add('table-inner-scrollbar');
    })
    });
  }

  exportToCsv(fromTable:any){
    this.showLoader.exportCSVLoader=true;
    let headers:any=[];
    headers.push("Office Name");
    this.teamData.forEach((e:any)=>{
      if(e.teamId != this.currentTeamId){
        headers.push(`Pendency with ${e.teamName}`);
      }
    })

    let options: any = {
      showLabels: true,
      headers: headers
    }
    let excelData: any;
    excelData = [...this.pendencyData];
    excelData = excelData.map((e: any) => {
      if (!e.count && fromTable == 'table') {
        e = { ...e, count: `0` };
      }
      if(!e.minDate && fromTable == 'dos-table'){
        e = {...e,minDate:'-'};
      }
      return e;
    })

    if(fromTable == 'table'){
      excelData = excelData.map((e:any)=>{
        this.teamData.map((ele:any,idx:any)=>{
          if(ele.teamId != this.currentTeamId && ele.teamId === e.teamId && this.teamData.length != idx){
            e[`PendencyWith${ele.teamName}`] = e.count;
          }else if(ele.teamId != this.currentTeamId){
            e[`PendencyWith${ele.teamName}`] = "0";
          }
        })
        return e;
      })
    }
    else if(fromTable == 'dos-table'){
      excelData = excelData.map((e:any)=>{
        this.teamData.map((ele:any,idx:any)=>{
          if(ele.teamId != this.currentTeamId && ele.teamId === e.teamId){
            let date: Date = new Date(e.minDate);
             e = { ...e, minDate: `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}` }
            e[`PendencyWith${ele.teamName}`] = e.minDate ;
          }else if(ele.teamId != this.currentTeamId){
            e[`PendencyWith${ele.teamName}`] = "-";
          }
        })
        return e;
      })
    }
    
    excelData = excelData.map(
      ({ key, uuid, teamId, count,teamName,minDate, ...newClaimData }: any) => newClaimData);

      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      console.log(excelData);
      
    new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_All_Pendency_${this.date}`, options);
    this.showLoader.exportCSVLoader=false;
  }
}
