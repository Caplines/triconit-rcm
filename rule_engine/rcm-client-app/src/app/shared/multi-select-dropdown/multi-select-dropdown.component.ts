import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AppConstants } from 'src/app/constants/app.constants';
import { ApplicationServiceService } from 'src/app/service/application-service.service';

@Component({
  selector: 'multi-select-dropdown',
  templateUrl: './multi-select-dropdown.component.html',
  styleUrls: ['./multi-select-dropdown.component.scss']
})
export class MultiSelectDropdownComponent {
  @Input() list:any=[]; 
  showDropDown:boolean=false;
  @Output() shareCheckedList = new EventEmitter();
  @Output() shareIndividualCheckedList = new EventEmitter();
  @Input() isTeam:boolean=false;
  @Input() isClient:boolean=false;
  @Input() isFromUserSetting:boolean=false;
  @Input() userClientData:any=[];
  @Input() userTeamData:any=[];
  @Input() inputConfig:any;
  @Output() emitToParent:any = new EventEmitter();
  
  clientCheckedList : any[];
  teamCheckedList : any[];
  clients:any=[]
  selectAllChecked:boolean=false;
  teamData:any=this.constants.teamData;
  searchClaimsConfig:any={'clients':[],'offices':[],"teams":[],'insuranceNames':[],'insuranceTypes':[],'providerNames':[],'providerTypes':[]};

constructor(private _service:ApplicationServiceService,private constants:AppConstants) {
  this.clientCheckedList=this.teamCheckedList=[];
  this._service.subscribeOnValueChange('MultiSelect',(event:any)=>{
    if (event.action === 'selectedClientsOffices') {
        this.searchClaimsConfig.offices=[];      
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && this.inputConfig.subType == 'office') {
          this.inputConfig.officeData = JSON.parse(JSON.stringify(event.value))
        } 
      }
      else return;
    })
 }

 ngOnInit(){
  if(this.isFromUserSetting && this.isClient){
    this.list = this.list.map((e:any)=>{
          if(this.userClientData && this.userClientData.some((k:any)=>e.name == k)){
            e['checked']=!e['checked'];
            this.clientCheckedList.push(e)
          }
          return e;
    })
    this.shareCheckedlist('client');
  }
  else if(this.isFromUserSetting && this.isTeam){
    this.list = this.list.map((e:any)=>{
      if(this.userTeamData && this.userTeamData.some((k:any)=>e.teamId == k)){
        e['checked']=true;
        this.teamCheckedList.push(e);
      }
      return e;
  })
  this.shareCheckedlist('team');
  }
  
  }

getSelectedValue(status: Boolean, value: any, type: String) {
  if (type === 'client') {
    if (status) {
      this.clientCheckedList.push(value);
    } else {
      this.clientCheckedList.forEach((e: any, idx: any) => {
        if (e.id === value.uuid || e.id === value.id) {
          this.clientCheckedList.splice(idx, 1);
        }
      })
    }
    if (this.clientCheckedList.length == 0) {
      this.selectAllChecked = false;
    }
    else if (this.list.length === this.clientCheckedList.length){
      this.selectAllChecked=true;
    }
    else{
      this.selectAllChecked=false;
    }
    this.shareCheckedlist('client');
  }
  else if (type === 'userSettingClient') {
    if (status) {
      this.clientCheckedList.push(value);
    } else {
      this.clientCheckedList.forEach((e: any, idx: any) => {
        if (e.id == value.id) {
          this.clientCheckedList.splice(idx, 1);
        }
      })
    }
    this.shareCheckedlist('client');
  }
  else if (type === 'team') {
    if (status) {
      this.teamCheckedList.push(value);
    } else {
      this.teamCheckedList.forEach((e: any, idx: any) => {
        if (e.teamId == value.teamId) {
          this.teamCheckedList.splice(idx, 1);
        }
      });
    }
  
    this.shareCheckedlist('team');
  }
  else if (type === 'userSettingTeam') {
    if (status) {
      this.teamCheckedList.push(value);
    } else {
      this.teamCheckedList.forEach((e: any, idx: any) => {
        if (e.teamId == value.teamId) {
          this.teamCheckedList.splice(idx, 1);
        }
      });
    }
    if(this.teamCheckedList.length==0){
      this.selectAllChecked=false;
    }
    else if (this.teamData.length === this.teamCheckedList.length){
      this.selectAllChecked=true;
    } else{
      this.selectAllChecked=false;
    }
    this.shareCheckedlist('team');
  }
  else if (type === 'searchClaimClient'){
    if (status) {
      this.searchClaimsConfig.clients.push(value);
      this._service.emitOnValueChange({action:'getSelectClientName',value:this.searchClaimsConfig.clients});
    } else {
      this.searchClaimsConfig.clients.forEach((e: any, idx: any) => {
        if (e.clientUuid == value.clientUuid) {
          this.searchClaimsConfig.clients.splice(idx, 1);
          this._service.emitOnValueChange({action:'getSelectClientName',value:this.searchClaimsConfig.clients});
        }
      });
      
    }
    this.addOfficessCrossClient();
  }
  else if (type === 'searchClaimOffices'){
    if (status) {
      this.searchClaimsConfig.offices.push(value);
      this._service.emitOnValueChange({action:'getSelectedOffices',value:this.searchClaimsConfig.offices});
    } else {
      this.searchClaimsConfig.offices.forEach((e: any, idx: any) => {
        if (e.uuid == value.uuid) {
          this.searchClaimsConfig.offices.splice(idx, 1);
          this._service.emitOnValueChange({action:'getSelectedOffices',value:this.searchClaimsConfig.offices});
        }
      });
      
    }
  }
  else if (type === 'insuranceNames'){
    if (status) {
      this.searchClaimsConfig.insuranceNames.push(value);
      this.emitToParent.emit({action:'getinsuranceNames',value:this.searchClaimsConfig.insuranceNames});
    } else {
      this.searchClaimsConfig.insuranceNames.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.insuranceNames.splice(idx, 1);
          this.emitToParent.emit({action:'getinsuranceNames',value:this.searchClaimsConfig.insuranceNames});
        }
      });
    }
  }
  else if (type === 'insuranceTypes'){
    if (status) {
      this.searchClaimsConfig.insuranceTypes.push(value);
      this.emitToParent.emit({action:'getinsuranceTypes',value:this.searchClaimsConfig.insuranceTypes});
    } else {
      this.searchClaimsConfig.insuranceTypes.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.insuranceTypes.splice(idx, 1);
          this.emitToParent.emit({action:'getinsuranceTypes',value:this.searchClaimsConfig.insuranceTypes});
        }
      });
      
    }
  }
  else if (type === 'providerNames'){
    if (status) {
      this.searchClaimsConfig.providerNames.push(value);
      this.emitToParent.emit({action:'getproviderNames',value:this.searchClaimsConfig.providerNames});
    } else {
      this.searchClaimsConfig.providerNames.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.providerNames.splice(idx, 1);
          this.emitToParent.emit({action:'getproviderNames',value:this.searchClaimsConfig.providerNames});
        }
      });
      
    }
  }
  else if (type === 'providerTypes'){
    if (status) {
      this.searchClaimsConfig.providerTypes.push(value);
      this.emitToParent.emit({action:'getproviderTypes',value:this.searchClaimsConfig.providerTypes});
    } else {
      this.searchClaimsConfig.providerTypes.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.providerTypes.splice(idx, 1);
          this.emitToParent.emit({action:'getproviderTypes',value:this.searchClaimsConfig.providerTypes});
        }
      });
      
    }
  }
}
  shareCheckedlist(action: any) {
    this.shareCheckedList.emit({ 'action': action, value: action == 'team' ? this.teamCheckedList : this.clientCheckedList });
  }

  selectAll(event:any,from:any){
    this.selectAllChecked=!this.selectAllChecked;
    let isChecked:boolean=event.target.checked;
    if (from === "client") {
      if (isChecked) {
        this.clients.forEach((e: any) => {
          let isClientExist = this.clientCheckedList.some((ele: any) => e.id == ele.id || e.id == ele.uuid);
          if (!isClientExist) {
            this.clientCheckedList.push(e)
          }
        })
        this.list = this.clientCheckedList.map((e: any) => ({ ...e, 'checked': true }));
        this.shareCheckedlist('client');
      } else {
        this.list.forEach((e: any) => {
          e.checked = false;
        })
        this.clientCheckedList = [];
        this.shareCheckedlist('client');
      }
    } 
    else if(from === "team"){
      if (isChecked) {
        this.teamData.forEach((e: any) => {
          let isTeamExist = this.teamCheckedList.some((ele: any) => e.teamId == ele.teamId);
          if (!isTeamExist) {
            this.teamCheckedList.push(e)
          }
        })
        this.list = this.teamCheckedList.map((e: any) => ({ ...e, 'checked': true }));
        this.shareCheckedlist('team');
      } else {
        this.list.forEach((e: any) => {
          e.checked = false;
        })
        this.teamCheckedList = [];
        this.shareCheckedlist('team');
        
      }
    }
    }

    addOfficessCrossClient(){
      let offices:any=[];
      this.searchClaimsConfig.clients.forEach((ele:any)=>{
        if(ele.offices.length>0){ 
          ele.offices.forEach((item:any)=>{
                  offices.push(item);
          })
        }
      })
      this._service.emitOnValueChange({action:'selectedClientsOffices',value:offices});
   
    }
}
