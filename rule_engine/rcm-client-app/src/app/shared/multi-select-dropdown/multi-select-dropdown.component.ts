import { Component, EventEmitter, Input, Output } from '@angular/core';
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
  
  clientCheckedList : any[];
  teamCheckedList : any[];
  clients:any=[]
  selectAllChecked:boolean=false;
  teamData:any=[];

constructor(private _service:ApplicationServiceService) {
  this.clientCheckedList=this.teamCheckedList=[];
  this.clients= JSON.parse(localStorage.getItem("clients"));
  this.getTeamsData();
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
}
  shareCheckedlist(action: any) {
    this.shareCheckedList.emit({ 'action': action, value: action == 'team' ? this.teamCheckedList : this.clientCheckedList });
  }

  getTeamsData(){
    this._service.fetchTeamsNameData((res:any)=>{
      if(res.status){
        this.teamData = res.data
      }
    })
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
}
