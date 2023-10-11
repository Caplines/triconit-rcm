import { Component, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import Utils from '../util/utils';
import { AppConstants } from '../constants/app.constants';
import { SearchParamModel } from '../models/search_param_model';
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
  }

  constructor(public appService: ApplicationServiceService, private title: Title, private constants: AppConstants) {
    title.setTitle(Utils.defaultTitle + "Search Claims");
    this.appService.subscribeOnValueChange('FromMultiSelect',(event:any)=>{
      if (event['action'] == 'getSelectClientName') {
        this.searchClaimConfig['clientUuid'] = event.value.map(({ checked, clientName, offices, ...newClient }: any) => newClient.clientUuid);
        this.searchClaimConfig['officeUuid'] = [];
      } else if (event['action'] == 'getSelectedOffices') {
        this.searchClaimConfig['officeUuid'] = event.value.map(({ active, checked, key, name, ...newOffice }: any) => newOffice.uuid);
      }
      console.log(this.searchClaimConfig);
    })
  }

  ngOnInit(): void {
    this.getcompanyData();
    this.getSerachParams();

  }


  getcompanyData() {
    this.appService.fetchClientsByUser((callback: any) => {
      if (callback.status) {
        this.clients = callback.data;
      
      }
    })
  }

  addCheckedAndNameProperty(data:any){
   data.insuranceNames =  data.insuranceNames.map((item:any) => ({ name: item, checked: false }));
   data.insuranceTypes=  data.insuranceTypes.map((item:any) => ({ name: item, checked: false }));
   data.providerNames = data.providerNames.map((item:any) => ({ name: item, checked: false }));
   data.providerTypes = data.providerTypes.map((item:any) => ({ name: item, checked: false }));


  }

  getSerachParams() {
    this.appService.getSerachParams((callback: any) => {
      if (callback.status) {
        this.searchParamModel = callback.data;
        this.addCheckedAndNameProperty(this.searchParamModel);
      }
    })
  }

  searchClaims() {
    this.appService.searchClaims(this.searchClaimConfig, (res: any) => {
      if (res.status) {
        console.log(res);
      }
    })
  }

  receiveChildrenEvent(event: any) {
   
  }
  

}
