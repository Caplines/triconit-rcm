import { Component, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import Utils from '../util/utils';
import { AppConstants } from '../constants/app.constants';
import { SearchParamModel } from '../models/search_param_model';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-search-claims',
  templateUrl: './search-claims.component.html',
  styleUrls: ['./search-claims.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SearchClaimsComponent {

  loader: any = {};
  clients: any = [];
  offices: any = [];
  teamData: any = this.constants.teamData;
  searchParamModel: SearchParamModel;
  searchClaimConfig: any = {
    "clientUuid": [],
    "officeUuid": [],
    "claimId": "",
    "patientId": "",
    "startDate": "",
    "endDate": "",
    "ageCategory": [],
    "claimStatus": [],
    "insuranceName": [],
    "insuranceType": [],
    "providerName": [],
    "providerType": [],
    "responsibleTeam": [],
    "showArchive": false,
    "pageNumber": 1
  };

  listOfClaimsData: any = [];
  totalPages: any;
  pageNumber: any;
  activeFilter:number=0;

  constructor(public appService: ApplicationServiceService, private title: Title, public constants: AppConstants,
    private datePipe: DatePipe) {
    title.setTitle(Utils.defaultTitle + "Search Claims");
    this.appService.subscribeOnValueChange('FromMultiSelect', (event: any) => {
      if (event['action'] == 'getSelectClientName') {
        this.searchClaimConfig['clientUuid'] = event.value.map(({ checked, clientName, offices, ...newClient }: any) => newClient.clientUuid);   //removing unused propertieds
        this.searchClaimConfig['officeUuid'] = [];
      } else if (event['action'] == 'getSelectedOffices') {
        this.searchClaimConfig['officeUuid'] = event.value.map(({ active, checked, key, name, ...newOffice }: any) => newOffice.uuid);   //removing unused propertieds
      } else if (event['action'] == 'getinsuranceNames') {
        this.searchClaimConfig['insuranceName'] = event.value.map(({ checked, ...newInsName }: any) => newInsName.name);//removing unused propertieds
      } else if (event['action'] == 'getinsuranceTypes') {
        this.searchClaimConfig['insuranceType'] = event.value.map(({ checked, ...newInsType }: any) => newInsType.name);//removing unused propertieds
      } else if (event['action'] == 'getproviderNames') {
        this.searchClaimConfig['providerName'] = event.value.map(({ checked, ...newProviderName }: any) => newProviderName.name);//removing unused propertieds
      } else if (event['action'] == 'getproviderTypes') {
        this.searchClaimConfig['providerType'] = event.value.map(({ checked, ...newProviderType }: any) => newProviderType.name);//removing unused propertieds
      } else if (event['action'] == 'getSelectedTeams') {
        this.searchClaimConfig['responsibleTeam'] = event.value.map(({ checked, count, teamName, unFormatedName, ...newTeam }: any) => newTeam.teamId);//removing unused propertieds
      } else if (event['action'] == 'getSelectedAge') {
        this.searchClaimConfig['ageCategory'] = event.value.map(({ checked, name, ...newAge }: any) => newAge.value);//removing unused propertieds
      } else if (event['action'] == 'getSelectedClaimStatus') {
        this.searchClaimConfig['claimStatus'] = event.value.map(({ checked, ...newClaimAge }: any) => newClaimAge.name);//removing unused propertieds
      }
      console.log(this.searchClaimConfig);
    })
  }

  ngOnInit(): void {
    this.getcompanyData();
    this.getSerachParams();
    // this.setDefaultDate();
  }


  getcompanyData() {
    this.appService.fetchClientsByUser((callback: any) => {
      if (callback.status) {
        this.clients = callback.data;

      }
    })
  }

  getSerachParams() {
    this.appService.getSerachParams((callback: any) => {
      if (callback.status) {
        this.searchParamModel = callback.data;
        this.addCheckedAndNameProperty(this.searchParamModel);
      }
    })
  }

  addCheckedAndNameProperty(data: any) {
    data.insuranceNames = data.insuranceNames.map((item: any) => ({ name: item, checked: false }));       //adding two properties from single value for Multiselect component
    data.insuranceTypes = data.insuranceTypes.map((item: any) => ({ name: item, checked: false }));       //adding two properties from single value for Multiselect component
    data.providerNames = data.providerNames.map((item: any) => ({ name: item, checked: false }));        //adding two properties from single value for Multiselect component
    data.providerTypes = data.providerTypes.map((item: any) => ({ name: item, checked: false }));         //adding two properties from single value for Multiselect component
  }

  searchClaims() {
    this.loader = true;
    this.listOfClaimsData = [];
    this.appService.searchClaims(this.searchClaimConfig, (res: any) => {
      if (res.status && res?.data[0]?.data) {
        this.loader = false;
        this.totalPages = res.data[0].totalPages;
        this.pageNumber = res.data[0].pageNumber;
        this.listOfClaimsData = res.data[0].data;
        this.searchClaimConfig.pageNumber = 1;
      }
      else {
        this.loader = false;
      }
    })
  }

  receiveChildrenEvent(event: any) {
    if (event['action'] == 'getSelectedDateRange') {
      //debugger;
      if (event.value == 'Invalid Date' || event.value.startDate == null) {
        this.searchClaimConfig['startDate'] = null;
        this.searchClaimConfig['endDate'] = null;

      }
      else {
        this.searchClaimConfig['startDate'] = event.value.startDate;
        this.searchClaimConfig['endDate'] = event.value.endDate;
      }
    }
    else if (event['action'] == 'changePage') {
      this.searchClaimConfig.pageNumber = event.value;
      this.searchClaims();
    }
  }

  setDefaultDate() {
    //let today = new Date();
    //this.searchClaimConfig.startDate = this.searchClaimConfig.endDate = this.datePipe.transform(today, 'yyyy-MM-dd').toString();
  }

  quickFilter(activeTab:number){
      this.activeFilter = activeTab;
      if(activeTab == 1 ){
          this.constants.ageCategory.forEach((e:any)=>{
              if(e.value>=3){
                e.checked =true;
                this.appService.emitOnValueChange({action:'selectDefaultAgeCategory',value:e});
              }
          })
          this.constants.claimStatus.forEach((e:any)=>{
            if(e.name.toUpperCase() === "BILLED"){
                e.checked =true;
                this.appService.emitOnValueChange({action:'selectDefaultClaimStatus',value:e});
            }
          })
          if(this.searchClaimConfig.clientUuid.length>0){
                this.searchClaims();
          }
      }
  }

}
