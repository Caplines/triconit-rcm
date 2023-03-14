import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, mergeMap, switchMap, catchError, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { TokenStorageService } from '../service/token-storage.service';
import Utils from '../util/utils';

@Injectable({
  providedIn: 'root'
})
export class BaseService {

  httpUrl = {
    'getTeams':"/master/getteams",
    'getOffices':"/master/getoffices",
    'getCompany':"/getOrganization",
    'getAllUsers':"/getAllUsers",
    'officeByCompany':"/officeByCompany",
    'rolesByCompanyName': "/master/defaultRolesByCname",
    'rolesByTeam':  "/master/rolesByTeamId",
    'fetchclaims':"/api/fetch-claims",
    'freshclaimlogs':"/api/fetch-fresh-claims-logs",
    'fetchclaimsFromSource':"/api/fetch-claims-from-source",
    'fetchclaimsAssignmentData':"/api/fetch-claims-log-assign",
    'fetchAssociateClaimLogs':"/api/fetch-billing-claims",
    'fetchAssociateClaimDet':"/api/fetch-fresh-claims-det",
    'registerUser':"/register",
    'findUserByEmail':"/finduser",
    'changePassword':"/resetpassword",
    'updateStatus':"/resetstatus",
    'editOfficeName': "/editOffice",
    'addNewOffice': "/addOffice",
    'userByTeamId':"/users/team",
    'assignOffice':"/assignOffice",
    'findAllUser':'/getAllUsers/',
    'getClientsName':"/api/allclients",
    'getAllClients':"/getClientDetails",
    'addClients':'/addClient',
    'editClient':'/editClient',
    'fetchBillingClaimsByUuid':"/api/fetchindclaim",
    'issueclaim':"/api/issueClaims",
    'ivfdata':"/api/ivfdata",
    'claimRuleData':"/api/rules-data",
    'updatePass':"/updatepassword",
    'productionData':"/api/bill/claim-production",
    'claimNotes':"/api/fetch-claim-notes",
    'saveClaim':"/api/save-full-claim",
    'servicelevelval':"/api/fetchservicecodeval",
    'submissionDetails':'/api/fetch-claim-sub-det',
    'ruleclaimdata':"/api/fetch-claim-rule-val-data",
    'ruleRemark':"/api/fetch-claim-rule-remarks",
    'autorules':"/api/run-auto-rules",
    'ivdetails':"/api/ivfdata"
  }

  constructor(public http: HttpClient, private tokenStorage: TokenStorageService) {
   }

  generateRefreshToken() {
    return this.http.get(environment.API_URL + '/refresh');
  }

  postData(d: any, url: string, callback: any) {
    this.generateRefreshToken().pipe(switchMap(data => {
     
      Utils.setRefreshToken(data);
      return this.http.post(environment.API_URL + url, d);
    },
    )
    ).subscribe((data) => {
      callback((<any>data));
    }),
      (error: any) => {
        console.log(error)
        if (error.status == 401) {
          // this.router.navigate(['/logout']);
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
        console.log(`done`);
      }

      ;

  }


  getData(d: any, url: string, callback: any) {
    this.generateRefreshToken().pipe(switchMap(data => {
      Utils.setRefreshToken(data);
      return this.http.get(environment.API_URL + url, d);
    },
    )
    ).subscribe((data) => {
      callback((<any>data));
    }),
      (error: any) => {
        console.log(error);
        if (error.status == 401) {
          // this.router.navigate(['/logout']);
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
        console.log(`done`);
      }

      ;

  }
  
}
