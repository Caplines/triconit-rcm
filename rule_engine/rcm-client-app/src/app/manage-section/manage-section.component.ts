import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApplicationServiceService } from '../service/application-service.service';
import { AppConstants } from '../constants/app.constants';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-manage-section',
  standalone: true,
  imports: [CommonModule,RouterModule,FormsModule],
  templateUrl: './manage-section.component.html',
  styleUrls: ['./manage-section.component.scss'],
})
export class ManageSectionComponent {

  clients:any=[];
  selectedClient:string='';
  sectionList:any=[];
  teamsData:any=this.constants.teamData;
  mainData:any=[];
  loader:boolean=false;
  selectedData:any=[];

  constructor(private _service:ApplicationServiceService,public constants:AppConstants){

  }

  ngOnInit(){
    // this.fetchClients();
    this.fetchSectionData();
  }

  fetchSectionData(){
    this.loader = true;
    this._service.fetchManageSectionData((res:any)=>{
      if(res){
        console.log(res);
        this.mainData = res.data;
        this.loader = false;

      }
    })
  }
  

  updateSelectedData(event:any,clientUuid: string, teamId: number, sectionId: number,viewAccess:any,editAccess:any,type:any) {
    const clientIndex = this.mainData.findIndex((client:any) => client.clientUuid === clientUuid);
  
    if (clientIndex === -1) {
      this.mainData.push({
        clientUuid,
        teamsWithSections: [{
          teamId,
          sectionData: [ { sectionId, viewAccess, editAccess }]
        }]
      });
    } else {
      const teamIndex = this.mainData[clientIndex].teamsWithSections.findIndex((team:any) => team.teamId === teamId);
  
      if (teamIndex === -1) {
        this.mainData[clientIndex].teamsWithSections.push({
          teamId,
          sectionData: [ { sectionId, viewAccess, editAccess }]
        });
      } else {
        const sectionIndex = this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData.findIndex((section:any) => section.sectionId === sectionId);
        if (sectionIndex === -1) {
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData.push({ sectionId, viewAccess, editAccess });
        } else {
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = viewAccess;
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = editAccess;

          if (viewAccess && type === 'view') {
            this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = viewAccess;
        } else if(!viewAccess && type === 'view'){
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = false;
        }
        else if(editAccess && type === 'edit'){
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = true;
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = editAccess;
        }
         else if(!editAccess && type === 'edit'){
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = false;
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = false;
         }
      }
    }
    }
    console.log(viewAccess,editAccess); 
  }
  
  
  saveManageSecData(idx:any){
        let params:any = this.mainData[idx];
        console.log(params);
  
        this._service.saveManageSectionData([params],(res:any)=>{
          if(res){
            console.log(res);
            
          }
        })
        
  }
  
  trackByFn(index:any, item:any) {
    return item.id; // Use a unique identifier for the item
}
  // fetchClients(){
  //   this._service.getClientsName((res:any)=>{
  //     if(res.data && res.status == 200){
  //       this.clients = res.data;
  //       this.fetchSectionList();

  //     }
  //   })
  // }

  //  fetchSectionList(){
  
  //     this._service.fetchSectionList((res:any)=>{
  //     if(res.data && res.status == 200){
  //        this.sectionList =  res.data;
  //         this.addClientSectionTeamInArray();
  //     }
  //   })
  // }

  // selectClient(event:any){
  //     this.selectedClient = event.target.value;
  //     // let obj = [
  //     //   {
  //     //     client:'uuid',
  //     //     team:[
  //     //       {
  //     //         'name':'billing ',
  //     //         'section':[
  //     //           {
  //     //             id:34,
  //     //             access:['view','edit']
  //     //           }
  //     //         ]
  //     //       },
  //     //       {
  //     //         'name':'inetnal audit ',
  //     //         'section':[
  //     //           {
  //     //             id:34,
  //     //             access:['view','edit']
  //     //           },
  //     //           {
  //     //             id:87,
  //     //             access:['view']
  //     //           },
  //     //         ]
  //     //       }
  //     //     ],
  //     //     clientuuid:'sadsad',
  //     //     team:[
  //     //       {
  //     //         'name':'Internla audit ',
  //     //         'section':[
  //     //           {
  //     //             id:34,
  //     //             access:['view','edit']
  //     //           }
  //     //         ]
  //     //       }
  //     //     ],
  //     //   }
  //     // ]

  // }

  // addClientSectionTeamInArray(){
  //   this.loader=true;
  //       let m:any=[];
  //       console.log(323);
  //         this.clients.forEach((ele:any,idx:any)=>{
  //                   m.push({
  //                         'clientName':ele.clientName,
  //                         'team': this.teamsData,
  //                         'section':this.sectionList,
  //                         'clientUuid':ele.uuid
  //                   })
                 
  //       })
  //       this.mainData  = m;
  //       this.loader=false;

  // }

  // selectView(event:any,client:any,teamId:any,section:any,type:any){
  //   let teamsWith:any = [
  //     {
  //       teamId:teamId,
  //       sectionData:[
  //               {
  //                 "editAccess": type == "edit" ? event.target.checked : false,
  //                 "viewAccess": type == "edit" ? event.target.checked : true ,
  //                 "sectionId": section.sectionId 
  //               }
  //             ]
  //     }
  //   ];
  //   let params:any = 
  //     {
  //       clientUuid:client.clientUuid,
  //       teamsWithSections:teamsWith
  //     };
  //   this.params.push(params)
  // }





// {
//   "clientUuid": "614c4beb-7df2-11e8-8432-8c16451459cd",
//   "teamsWithSections": [
//       {
//           "teamId": 7,
//           "sectionData": [
//               {
//                   "editAccess": false,
//                   "viewAccess": false,
//                   "sectionId": 5
//               },
//               {
//                   "editAccess": false,
//                   "viewAccess": true,
//                   "sectionId": 4
//               },
//               {
//                   "editAccess": true,
//                   "viewAccess": false,
//                   "sectionId": 3
//               }
//           ]
//       },]
//     }



}
