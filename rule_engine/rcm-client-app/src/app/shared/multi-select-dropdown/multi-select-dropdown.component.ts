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
  
  checkedList : any[];

constructor(private appService: ApplicationServiceService) {
  this.checkedList = [];
  
 }

 ngOnInit(){
  if(this.isFromUserSetting && this.isClient){
    this.list = this.list.map((e:any)=>{
          if(this.userClientData.some((k:any)=>e.name == k)){
            e['checked']=!e['checked'];
            this.checkedList.push(e)
          }
          return e;
    })
    this.shareCheckedlist('client');
  }
  else if(this.isFromUserSetting && this.isTeam){
    this.list = this.list.map((e:any)=>{
      if(this.userTeamData.some((k:any)=>e.teamId == k)){
        e['checked']=true;
        this.checkedList.push(e);
      }
      return e;
  })
  this.shareCheckedlist('team');
  }
  }
     getSelectedValue(status:Boolean,value:any,type:String){
      if(type === 'client'){
        if(status){
          this.checkedList.push(value);  
        }else{
          this.checkedList.forEach((e:any,idx:any)=>{
            if(e.uuid == value.uuid){
              this.checkedList.splice(idx,1);
            }
          })
        }
      this.shareCheckedlist('client');
      }
      else if(type === 'userSettingClient'){
        if(status){
          this.checkedList.push(value);  
        }else{
          this.checkedList.forEach((e:any,idx:any)=>{
            if(e.id == value.id  ){
              this.checkedList.splice(idx,1);
            }
          })
        }
      this.shareCheckedlist('client');
      }
      else if(type === 'team'){
        if(status){
          this.checkedList.push(value);  
        }else{
          this.checkedList.forEach((e:any,idx:any)=>{
           if(e.teamId == value.teamId){
             this.checkedList.splice(idx,1);
           }
          });
        }
        this.shareCheckedlist('team');
      }
      else if(type === 'userSettingTeam'){
        if(status){
          this.checkedList.push(value);  
        }else{
          this.checkedList.forEach((e:any,idx:any)=>{
           if(e.teamId == value.teamId){
             this.checkedList.splice(idx,1);
           }
          });
        }
        this.shareCheckedlist('team');
      }
  }
  shareCheckedlist(action:any){
       this.shareCheckedList.emit({'action':action,value:this.checkedList});
  }
}
