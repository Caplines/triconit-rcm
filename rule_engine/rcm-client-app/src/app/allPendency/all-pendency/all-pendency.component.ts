import { Component } from '@angular/core';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { ngxCsv } from 'ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';

@Component({
  selector: 'all-pendency',
  templateUrl: './all-pendency.component.html',
  styleUrls: ['./all-pendency.component.scss']
})
export class AllPendencyComponent {

  pendencyData:any=[];
  teamData:any=[];
  currentTeamId:any;
  showLoader:boolean=false;
  date:any;

  constructor(private _service:ApplicationServiceService){}
  ngOnInit(): void {
    this.teamData=[{"teamName":"Internal Audit","teamId":3},{"teamName":"Aging","teamId":4},{"teamName":"Posting","teamId":5},{"teamName":"Quality","teamId":6},{"teamName":"Billing","teamId":7}];
    this.getAllPendencyDetails();
    this.currentTeamId = localStorage.getItem("selected_teamId");
  }
  getAllPendencyDetails(){
    this.showLoader=true;
    this._service.fetchAllPendency((res:any)=>{
      if(res.status==200){
        this.showLoader=false;

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
        console.log(this.pendencyData);
        
      }
    })
  }

  saveToPdf(e:any){
    html2canvas(<any>document.getElementById(e)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      pdf.addImage(content, "PNG", 0, 0, width, height)
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_All_Pendency_${this.date}`);
    });
  }

  exportToCsv(fromTable:any){

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
        e = {...e,minDate:''};
      }
      return e;
    })

    excelData = excelData.map(
      ({ key, uuid, teamId, teamName, ...newClaimData }: any) => newClaimData);
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      console.log(excelData);
      
    new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_All_Pendency_${this.date}`, options);
  }
}
