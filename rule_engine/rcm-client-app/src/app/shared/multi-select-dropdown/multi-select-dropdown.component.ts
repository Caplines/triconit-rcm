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
  teams:any=this.constants.teamData;
  searchClaimsConfig:any={'clients':[],'offices':[],"teams":[],'insuranceNames':[],'insuranceTypes':[],'providerNames':[],'providerTypes':[],'ageCategory':[],'claimStatus':[],'locOffice':[],'locInsuranceName':[],'locInsuranceType':[]};
  searchText:any='';
  filteredOptions:any={'insuranceNames':[],'insuranceTypes':[],'providerNames':[],'providerTypes':[]};
  showSelectedData:any={};
  isAllSelected:any={'clients':false,'offices':false,'ageCategory':false,'claimStatus':false,'teams':false};


constructor(private _service:ApplicationServiceService,private constants:AppConstants) {
  this.clientCheckedList=this.teamCheckedList=[];
  
  this._service.subscribeOnValueChange('MultiSelect',(event:any)=>{
    if (event.action === 'selectedClientsOffices') {
      this.searchClaimsConfig.offices = [];
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && this.inputConfig.subType == 'offices') {
        this.inputConfig.officeData = JSON.parse(JSON.stringify(event.value));
        this.inputConfig.officeData = this._service.sortByAlphabet(this.inputConfig.officeData, 'name');
        this.isAllSelected['offices'] = false;
        this.showSelectedData = {};
        this.showSelectedData['offices'] = false;
      }
    }
    else if (event.action === 'selectDefaultAgeCategory') {
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && this.inputConfig.subType == 'ageCategory') {
        this._service.emitOnValueChange({ action: 'clearAllDefaultValuesInsurance' });
        event.value.forEach((e: any) => {
          this.getSelectedValue(e.checked, e, 'searchClaimAge', 'ageCategory');
        });
      }
    }
    else if (event.action === 'selectDefaultClaimStatus') {
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && this.inputConfig.subType == 'claimStatus') {
        event.value.forEach((e: any) => {
          this.getSelectedValue(e.checked, e, 'searchClaimStatus', 'claimStatus');
        })
      }
    }
    else if (event.action === 'filterUnbilledMedicaid') {
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && this.inputConfig.subType == 'insuranceTypes') {
          this._service.emitOnValueChange({ action: 'clearAllDefaultValuesAge' });
        this.searchClaimsConfig.insuranceTypes = [];
        event.value.forEach((e: any) => {
          this.getSelectedValue(e.checked, e, 'insuranceTypes', 'insuranceTypes');
        })
        let value = { 'name': 'Unbilled', 'checked': true };
        this._service.emitOnValueChange({ action: 'setDefaultfilterClaimStatus', value: value });
      }
    }
    else if (event.action === 'filterUnbilledNonMedicaid') {
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && this.inputConfig.subType == 'insuranceTypes') {
          this._service.emitOnValueChange({ action: 'clearAllDefaultValuesAge' });

        this.searchClaimsConfig.insuranceTypes = [];
        event.value.forEach((e: any) => {
          this.getSelectedValue(e.checked, e, 'insuranceTypes', 'insuranceTypes');
        })
        let value = { 'name': 'Unbilled', 'checked': true };
        this._service.emitOnValueChange({ action: 'setDefaultfilterClaimStatus', value: value });
      }
    }
    else if (event.action === 'setDefaultfilterClaimStatus') {
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && this.inputConfig.subType == 'claimStatus') {
        this.inputConfig.claimStatus.find((e:any)=>e.name === 'Unbilled' ? e.checked = true: '');
        setTimeout(() => {
          this.getSelectedValue(event.value.checked, event.value, 'searchClaimStatus', 'claimStatus');
        }, 0);
      }
    }
    else if (event.action === 'clearAllDefaultValuesInsurance') {
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && (this.inputConfig.subType == 'claimStatus' || this.inputConfig.subType == 'insuranceTypes')) {

          if(this.inputConfig.claimStatus?.length>0){
            this.inputConfig.claimStatus.forEach((e:any)=>e.checked=false);     
          }
          this.searchClaimsConfig.insuranceTypes.forEach((e:any)=>e.checked=false);
        this.searchClaimsConfig.claimStatus.forEach((e: any) => e.checked = false);
        this.searchClaimsConfig.insuranceTypes = [];
        this.searchClaimsConfig.claimStatus = [];
        setTimeout(() => {
          this._service.emitOnValueChange({ action: 'getinsuranceTypes', value: [] });
          this._service.emitOnValueChange({ action: 'getSelectedClaimStatus', value: [] });
        }, 0);
        console.log(this.searchClaimsConfig.insuranceTypes);
      }
    }
    else if (event.action === 'clearAllDefaultValuesAge') {
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined
        && (this.inputConfig.subType == 'ageCategory' || this.inputConfig.subType == 'claimStatus')) {
        this.searchClaimsConfig.ageCategory.forEach((e:any)=>e.checked=false);
        this.searchClaimsConfig.ageCategory = [];
        this.searchClaimsConfig.claimStatus.forEach((e:any)=>e.checked=false);
        this.searchClaimsConfig.claimStatus = [];
        this._service.emitOnValueChange({ action: 'getSelectedAge', value: [] });
        this._service.emitOnValueChange({ action: 'getSelectedClaimStatus', value: [] });
      }
    } 
    else if(event.action === 'resetAllField'){
      if (this.inputConfig != undefined && this.inputConfig.subType != undefined){
        this.searchClaimsConfig[this.inputConfig.subType].forEach((e:any)=>e.checked=false);
        this.searchClaimsConfig[this.inputConfig.subType] = [];
        if(this.inputConfig.subType == 'claimStatus'){
          console.log(4);
          this.searchClaimsConfig[this.inputConfig.subType].forEach((e:any)=>e.checked=false);
        }
        if(this.inputConfig?.subType === 'clients'){
          this._service.emitOnValueChange({ action: 'selectedClientsOffices', value: [] });
        }
        setTimeout(() => {
          this._service.emitOnValueChange({ action: this.inputConfig['emitAction'], value: [] });
        }, 0);
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

  
  ngAfterViewInit(){
    if(this.inputConfig?.isSearchClaims && this.inputConfig?.clients && this.inputConfig?.clients?.length == 1){
      setTimeout(()=>{
        this.defaultSelectSingleClient();
      },0)
  }
  }

getSelectedValue(status: Boolean, value: any, type: String,filterProperty?:string) {
  if (type === 'client') {
    if (status) {
      this.clientCheckedList.push(value);
    } else {
      this.clientCheckedList.forEach((e: any, idx: any) => {
        if (value.uuid == e.uuid) {
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
    else if (this.teams.length === this.teamCheckedList.length){
      this.selectAllChecked=true;
    } else{
      this.selectAllChecked=false;
    }
    this.shareCheckedlist('team');
  }
  else if (type === 'searchClaimClient') {
    if (status) {
      this.searchClaimsConfig.clients.push(value);
      this._service.emitOnValueChange({ action: 'getSelectClientName', value: this.searchClaimsConfig.clients });
    } else {
      this.searchClaimsConfig.clients.forEach((e: any, idx: any) => {
        if (e.clientUuid == value.clientUuid) {
          this.searchClaimsConfig.clients.splice(idx, 1);
          this._service.emitOnValueChange({ action: 'getSelectClientName', value: this.searchClaimsConfig.clients });
        }
      });

    }
    console.log(45);

    if (this.searchClaimsConfig.clients.length == this.inputConfig.clients.length) {
      this.isAllSelected[filterProperty] = true;
    } else {
      this.isAllSelected[filterProperty] = false;
    }
    setTimeout(() => {
      this.addOfficessCrossClient();
    }, 0);
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

    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig.officeData.length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedOffices', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedOffices', value: this.searchClaimsConfig[filterProperty] });
    }
  }
  else if (type === 'insuranceNames'){
    if (status) {
      this.searchClaimsConfig.insuranceNames.push(value);
      this._service.emitOnValueChange({action:'getinsuranceNames',value:this.searchClaimsConfig.insuranceNames});
    } else {
      this.searchClaimsConfig.insuranceNames.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.insuranceNames.splice(idx, 1);
          this._service.emitOnValueChange({action:'getinsuranceNames',value:this.searchClaimsConfig.insuranceNames});
        }
      });
    }
  }

  else if(type === 'selectAll'){
    this.filteredOptions[filterProperty].forEach((item:any)=>{
      if(!item.checked){
        item.checked=true;
        this.searchClaimsConfig[filterProperty].push(item);
        this.searchText= '';
      } 
    })
  }

  else if (type === 'selectAllClient') {
    if (status) {
      this.inputConfig.clients.forEach((item: any) => {
        if (!item.checked) {
          item.checked = true;
          this.searchClaimsConfig[filterProperty].push(item);
        }
      })
    } else {
      this.inputConfig.clients.forEach((item: any) => item.checked = false)
      this.searchClaimsConfig[filterProperty] = [];
    }
    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig.clients.length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectClientName', value: this.searchClaimsConfig.clients });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectClientName', value: this.searchClaimsConfig.clients });
    }
    setTimeout(() => {
      this.addOfficessCrossClient();
    }, 0);
  }

  else if (type === 'selectAllOffice') {
    if (status) {
      this.inputConfig.officeData.forEach((item: any) => {
        if (!item.checked) {
          item.checked = true;
          this.searchClaimsConfig[filterProperty].push(item);
        }
      })
    } else {
      this.inputConfig.officeData.forEach((item: any) => item.checked = false)
      this.searchClaimsConfig[filterProperty] = [];
    }
    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig.officeData.length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedOffices', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedOffices', value: this.searchClaimsConfig[filterProperty] });
    }
  }

  else if (type === 'insuranceTypes'){
    if (status) {
      this.searchClaimsConfig.insuranceTypes.push(value);
      this._service.emitOnValueChange({action:'getinsuranceTypes',value:this.searchClaimsConfig.insuranceTypes});
    } else {
      this.searchClaimsConfig.insuranceTypes.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.insuranceTypes.splice(idx, 1);
          this._service.emitOnValueChange({action:'getinsuranceTypes',value:this.searchClaimsConfig.insuranceTypes});
        }
      });
      
    }
  }
  else if (type === 'providerNames'){
    if (status) {
      this.searchClaimsConfig.providerNames.push(value);
      this._service.emitOnValueChange({action:'getproviderNames',value:this.searchClaimsConfig.providerNames});
    } else {
      this.searchClaimsConfig.providerNames.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.providerNames.splice(idx, 1);
          this._service.emitOnValueChange({action:'getproviderNames',value:this.searchClaimsConfig.providerNames});
        }
      });
      
    }
  }
  else if (type === 'providerTypes'){
    if (status) {
      this.searchClaimsConfig.providerTypes.push(value);
      this._service.emitOnValueChange({action:'getproviderTypes',value:this.searchClaimsConfig.providerTypes});
    } else {
      this.searchClaimsConfig.providerTypes.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.providerTypes.splice(idx, 1);
          this._service.emitOnValueChange({action:'getproviderTypes',value:this.searchClaimsConfig.providerTypes});
        }
      });
      
    }
  }
  else if (type === 'searchClaimTeam'){
    if (status) {
      this.searchClaimsConfig.teams.push(value);
      this._service.emitOnValueChange({action:'getSelectedTeams',value:this.searchClaimsConfig.teams});
    } else {
      this.searchClaimsConfig.teams.forEach((e: any, idx: any) => {
        if (e.teamName == value.teamName) {
          this.searchClaimsConfig.teams.splice(idx, 1);
          this._service.emitOnValueChange({action:'getSelectedTeams',value:this.searchClaimsConfig.teams});
        }
      });
      
    }

    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig.teams.length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedTeams', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedTeams', value: this.searchClaimsConfig[filterProperty] });
    }
    
  }

  else if (type === 'selectAllTeams') {
    if (status) {
      this.inputConfig.teams.forEach((item: any) => {
        if (!item.checked) {
          item.checked = true;
          this.searchClaimsConfig[filterProperty].push(item);
        }
      })
    } else {
      this.inputConfig.teams.forEach((item: any) => item.checked = false)
      this.searchClaimsConfig[filterProperty] = [];
    }

    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig.teams.length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedTeams', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedTeams', value: this.searchClaimsConfig[filterProperty] });
    }
  }

  else if (type === 'searchClaimAge'){
    if (status) {
      let isExist = this.searchClaimsConfig.ageCategory.some((ele:any)=>ele.name == value.name && ele.checked == value.checked);
          if(!isExist){
            this.searchClaimsConfig.ageCategory.push(value)
            this._service.emitOnValueChange({action:'getSelectedAge',value:this.searchClaimsConfig.ageCategory});
          }
    } else {
      this.searchClaimsConfig.ageCategory.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.ageCategory.splice(idx, 1);
          this._service.emitOnValueChange({action:'getSelectedAge',value:this.searchClaimsConfig.ageCategory});
        }
      });
    }

    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig[filterProperty].length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedAge', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedAge', value: this.searchClaimsConfig[filterProperty] });
    }
    
  }

  else if (type === 'selectAllAge') {
    if (status) {
      this.inputConfig[filterProperty].forEach((item: any) => {
        if (!item.checked) {
          item.checked = true;
          this.searchClaimsConfig[filterProperty].push(item);
        }
      })
    } else {
      this.inputConfig[filterProperty].forEach((item: any) => item.checked = false)
      this.searchClaimsConfig[filterProperty] = [];
    }

    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig[filterProperty].length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedAge', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedAge', value: this.searchClaimsConfig[filterProperty] });
    }
  }

  else if (type === 'searchClaimStatus'){
    if (status) {
      let isExist = this.searchClaimsConfig.claimStatus.some((ele:any)=>ele.name.toUpperCase() == value.name.toUpperCase() && ele.checked == value.checked);
      if(!isExist){
        this.searchClaimsConfig.claimStatus.push(value);
        this._service.emitOnValueChange({action:'getSelectedClaimStatus',value:this.searchClaimsConfig.claimStatus});
      }
    } else {
      this.searchClaimsConfig.claimStatus.forEach((e: any, idx: any) => {
        if (e.name == value.name) {
          this.searchClaimsConfig.claimStatus.splice(idx, 1);
          this._service.emitOnValueChange({action:'getSelectedClaimStatus',value:this.searchClaimsConfig.claimStatus});
        }
      });
      
    }

    if (this.inputConfig[filterProperty]?.length && this.searchClaimsConfig[filterProperty]?.length && this.searchClaimsConfig[filterProperty].length == this.inputConfig[filterProperty].length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedClaimStatus', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedClaimStatus', value: this.searchClaimsConfig[filterProperty] });
    }
    
  }

  else if (type === 'selectAllClaimStatus') {
    if (status) {
      this.inputConfig[filterProperty].forEach((item: any) => {
        if (!item.checked) {
          item.checked = true;
          this.searchClaimsConfig[filterProperty].push(item);
        }
      })
    } else {
      this.inputConfig[filterProperty].forEach((item: any) => item.checked = false)
      this.searchClaimsConfig[filterProperty] = [];
    }

    if (this.searchClaimsConfig[filterProperty].length == this.inputConfig[filterProperty].length) {
      this.isAllSelected[filterProperty] = true;
      this._service.emitOnValueChange({ action: 'getSelectedClaimStatus', value: this.searchClaimsConfig[filterProperty] });
    } else {
      this.isAllSelected[filterProperty] = false;
      this._service.emitOnValueChange({ action: 'getSelectedClaimStatus', value: this.searchClaimsConfig[filterProperty] });
    }
  }

  else if(type === 'locOffice'){
    this.emitToParent.emit({action:'filterByOffice'});
  }

  else if(type === 'locInsuranceName'){
    this.emitToParent.emit({action:'filterByInsuranceName'});
  }

  else if(type === 'locInsuranceType'){
    this.emitToParent.emit({action:'filterByInsuranceType'});
  }

    this.showSelectedData[filterProperty] =true;

}
  shareCheckedlist(action: any) {
    console.log(this.clientCheckedList);
    
    this.shareCheckedList.emit({ 'action': action, value: action == 'team' ? this.teamCheckedList : this.clientCheckedList });
  }

  selectAll(event:any,from:any){
    console.log(435);
    
    this.selectAllChecked=!this.selectAllChecked;
    let isChecked:boolean=event.target.checked;
    if (from === "client") {
      if (isChecked) {
        this.list.forEach((e: any) => {
          let isClientExist = this.clientCheckedList.some((ele: any) => e.uuid == ele.uuid);
          if (!isClientExist) {
            this.clientCheckedList.push({ ...e, 'checked': true });
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
    else if(from === "teams"){
      if (isChecked) {
        this.teams.forEach((e: any) => {
          let isTeamExist = this.teamCheckedList.some((ele: any) => e.teamId == ele.teamId);
          if (!isTeamExist) {
            this.teamCheckedList.push({ ...e, 'checked': true })
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

   
    filterOptions(filterProperty:any,sortBy?:any) {
      console.log(12);
      
      this.inputConfig[filterProperty] = this._service.sortByAlphabet(this.inputConfig[filterProperty], sortBy ? sortBy :'name');
      this.filteredOptions[filterProperty] = this.inputConfig[filterProperty].filter((option: any, idx: any) => {
        if (this.searchClaimsConfig[filterProperty].length > 0) {
          return option[sortBy ? sortBy : 'name'].toLowerCase().includes(this.searchText.toLowerCase()) && !this.searchClaimsConfig[filterProperty].some((item: any) => item[sortBy ? sortBy : 'name'].toLowerCase() == option[sortBy ? sortBy : 'name'].toLowerCase())
        } else {
          return option[sortBy ? sortBy : 'name'].toLowerCase().includes(this.searchText.toLowerCase());
        }
      }
      )

      console.log(this.filteredOptions[filterProperty]);
      

    }

    clearAll(value:string,actions:string){
       this.searchClaimsConfig[value].forEach((e:any)=>e.checked=false);
       this.inputConfig[value].forEach((e:any)=>e.checked=false);
       this.searchClaimsConfig[value]= [];
       this.filteredOptions[value].forEach((e:any)=>e.checked=false);
       this._service.emitOnValueChange({action:actions,value:this.searchClaimsConfig[value]});
       if(value === 'clients'){
        this.addOfficessCrossClient();
       }
    }

    toggleSelectedItem(field:any){
        this.showSelectedData[field] = !this.showSelectedData[field];
    }

    defaultSelectSingleClient(){
      this.filteredOptions['clients'] = this.inputConfig.clients;
      this.inputConfig.clients[0].checked = true; 
      this.getSelectedValue(this.inputConfig.clients[0].checked,this.inputConfig.clients[0],'searchClaimClient','clients');
    }

}
