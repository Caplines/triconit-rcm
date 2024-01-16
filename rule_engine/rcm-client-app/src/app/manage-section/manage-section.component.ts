import { ChangeDetectionStrategy, Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
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
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};

  // itemsPerPage: number = 1;
  // currentPage: number = 1;
  // filteredValue: any = [];
  // filterItems: any = [];
  @Input() inputConfig:any;
  @Output() emitToParent = new EventEmitter();
  @ViewChild("editSection")editSection!:ElementRef;

  constructor(private _service:ApplicationServiceService,public constants:AppConstants){

  }

  ngOnInit(){
    if(this.inputConfig && this.inputConfig?.isEditSection){
      this.fetchUserIdSectionData();
    } else{
      this.fetchSectionData();
    }
  }

  fetchSectionData(){
    this.loader = true;
    this._service.fetchManageSectionData((res:any)=>{
      if(res){
        console.log(res);
        this.mainData = res.data;
        // this.displayPage(1);
        this.loader = false;

      }
    })
  }
  

  updateSelectedData(event:any,clientUuid: string, teamId: number, sectionId: number,viewAccess:any,editAccess:any,type:any) {
    const clientIndex = this.mainData.findIndex((client:any) => client.clientUuid === clientUuid);

    if (clientIndex === -1) {

      if (this.inputConfig && this.inputConfig?.isEditSection) {
        this.mainData.push({
          clientUuid,
          userUuid: this.inputConfig?.uuid,
          teamsWithSections: [{
            teamId,
            sectionData: [{ sectionId, viewAccess, editAccess }]
          }]
        });
      } else {
        this.mainData.push({
          clientUuid,
          teamsWithSections: [{
            teamId,
            sectionData: [{ sectionId, viewAccess, editAccess }]
          }]
        });
      }

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
              if(this.inputConfig && this.inputConfig?.isEditSection){
                  //  this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = false;
                } else{
                 this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = false;
              }
          this.mainData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = false;

          // if(this.inputConfig && this.inputConfig?.isEditSection){
          //       this.editSection.nativeElement.disabled = true;
          // } 

         }
      }
    }
    }
  }
  
  
  saveManageSecData(idx:any){
      
        let params:any = this.mainData[idx];
        this._service.saveManageSectionData([params],(res:any)=>{
          if(res){
            this.showAlertPopup(res);
          }
        })
        
  }

  saveManageUserSecData(idx:any){

        let params:any = this.mainData[idx];
        this._service.saveUserManageSectionData([params],(res:any)=>{
          if(res){
            this.showAlertPopup(res);
          }
        })
        
  }
  
  trackByFn(index:any, item:any) {
    return item.id; 
}

// displayPage(page: number) {
//   this.loader = true;
//   this.currentPage = page;
//   let startIndex = (page - 1) * this.itemsPerPage;
//   let endIndex = startIndex + this.itemsPerPage;
//   this.filteredValue = this.mainData.slice(startIndex, endIndex);
//   this.filterItems = this.filteredValue;
//   this.loader = false;
//   window.scrollTo(0, 0);
// }

  showAlertPopup(res: any) {
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
    res.status == 400 ? this.alert.isError = true : this.alert.isError = false;
    this.alert.alertMsg = res.message ? res.message :  "Success";
    setTimeout(() => {
      scrollTo(0, 0);
    }, 0);
  }

  fetchUserIdSectionData(){
    this.loader = true;
    this._service.fetchUserManageSectionData(this.inputConfig.uuid,(res:any)=>{
      if(res){
        this.mainData = res.data;
        this.loader = false;
      }
    })
  }



}
