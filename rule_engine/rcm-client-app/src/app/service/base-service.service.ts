import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, mergeMap, switchMap, catchError, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { TokenStorageService } from '../service/token-storage.service';
import { Router } from '@angular/router';
import Utils from '../util/utils';
import { of } from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class BaseService {

  httpUrl = {
    'getTeams': "/master/getteams",
    'getOffices': "/master/getoffices",
    'getCompany': "/getOrganization",
    'getAllUsers': "/getAllUsers",
    'officeByCompany': "/officeByCompany",
    'rolesByCompanyName': "/master/defaultRolesByCname",
    'rolesByTeam': "/master/rolesByTeamId",
    'fetchclaims': "/api/fetch-claims",
    'freshclaimlogs': "/api/fetch-fresh-claims-logs",
    'fetchclaimsFromSource': "/api/fetch-claims-from-source",
    'fetchclaimsAssignmentData': "/api/fetch-claims-log-assign",
    'fetchAssociateClaimLogs': "/api/fetch-billing-claims",
    'fetchAssociateClaimDet': "/api/fetch-fresh-claims-det",
    'fetchLeadClaimDet': "/api/fetch-fresh-claims-det-lead",
    'registerUser': "/register",
    'findUserByEmail': "/finduser",
    'changePassword': "/resetpassword",
    'updateStatus': "/resetstatus",
    'editOfficeName': "/editOffice",
    'addNewOffice': "/addOffice",
    'userByTeamId': "/users/team",
    'assignOffice': "/assignOffice",
    'findAllUser': '/getAllUsers/',
    'getClientsName': "/api/allclients",
    'getAllClients': "/getClientDetails",
    'addClients': '/addClient',
    'editClient': '/editClient',
    'fetchBillingClaimsByUuid': "/api/fetchindclaim",
    'issueclaim': "/api/issueClaims",
    'ivfdata': "/api/ivfdata",
    'claimRuleData': "/api/rules-data",
    'updatePass': "/updatepassword",
    'productionData': "/api/bill/claim-production",
    'claimNotes': "/api/fetch-claim-notes",
    'saveClaim': "/api/save-full-claim",
    'servicelevelval': "/api/fetchservicecodeval",
    'submissionDetails': '/api/fetch-claim-sub-det',
    'ruleclaimdata': "/api/fetch-claim-rule-val-data",
    'ruleRemark': "/api/fetch-claim-rule-remarks",
    'autorules': "/api/run-auto-rules",
    'ivdetails': "/api/ivfdata",
    'editRoles': "/editRole",
    'getOfficesByUuid': "/getOffices",
    'other_user_team': "/user/other_teams",
    'tl_user': "/user/users_by_role/tl",
    'other_team_remark': "/api/remarks-other",
    'rolesByEmail': '/user/roles',
    'claimStatus': "/claim_assign_to_user",
    'getRoles': '/master/getroles',
    'getClients': '/master/getClients',
    'allpendency': '/api/allpendency',
    'assigntotl': '/api/assign_to_tl',
    'findUserByDetail': '/finduserbydetail',
    'fetchTpData': '/tp-link-data',
    'fetchIssueClaimCounts': '/issue-claim-counts',
    'googleSheetLink': '/gsheet-link',
    'updateIv': '/api/updateivfid',
    'removeIVTP': '/api/updateivfid/delete',
    'claimAttachment': '/api/claim-attachment',
    'final-attachments-data': '/api/final-attachments-data',
    'assign-claim-with-remark': '/api/assign-claim-with-remark',
    'save-archive-claims': '/api/save-archive-claims',
    'fetchArchiveClaims': '/api/archiveClaims',
    'fetchArchiveClaimsCount': '/archive-claim-counts',
    'get-attachments': '/api/get-attachments',
    'remove-claim-attachment': '/api/remove-claim-attachment',
    'get-attachments-count': '/api/get-attachments-count',
    'download-attachment': '/api/download-attachment-file',
    'alluserclients': '/api/alluserclients',
    //For PDF CSV
    'listOfClaim': '/api/list-of-claim/d',
    'issueClaimsPdf': '/api/issue-claim/d',
    'pendancyPdf': '/api/pendancy/d',
    'claimDetailsPdf': '/api/claim-details/d',
    'productionPdf': '/api/production/d',
    'allPendancy': '/api/allPendancy/d',
    'tPlan': '/api/tp-link/d',
    'Ivf': '/api/ivf/d',
    'othersTeam': '/api/other-teams-work/d',
    //
    'editUserInfo': '/edit-user-info',
    'editUserRole': '/edit-user-role',
    'editUserClient': '/edit-user-client',
    'editUserTeam': '/edit-user-team',
    'existingTLInfo': '/find-tl-by-client',
    'userClientsWithOffices': '/get-clients-with-offices',
    'archiveclaim': '/api/archiveunsub',
    'unarchiveclaim': '/api/unarchivesub',
    'save-unarchive-claims': '/api/save-unarchive-claims',
    'search-claims': '/api/search-claims',
    'search-params': '/api/searchparams',
    'search-claims-pdf': '/api/search-claim/d',
    'unarchive-claims': '/api/unarchive-claims',
    'others-teams-tl-exit': '/api/others-teams-tl-exit',
    'search-claim-pdf': '/api/search-claims/pdf',
    'section-list': '/master/get-sections',

    'fetch-manage-client-list': '/api/claim-client-with-section',
    'save-manage-client-list': '/api/manage-claim-client-section',
    'fetch-manage-user-section': '/api/claim-user-with-section',
    'save-manage-user-section': '/api/manage-claim-user-section',
    'fetch-user-claim-section-permission': '/api/claim/user-section-permission',
    
    'save-section-info': '/api/save-section-info',
    'get-claim-level-info': '/api/get-claim-level-info',
    'get-appeal-level-info': "/api/get-appeal-level-info",
    'get-insurance-payment-info': "/api/get-insurance-payment-info",
    'get-eob-info': "/api/get-eob-info",
    'remove-eob-data': "/api/remove-eob-data",
    'vieweoblink': "/api/vieweoblink",
    'get-service_level_info': "/api/get-service-level-info",
  }

  constructor(public router: Router, public http: HttpClient, public tokenStorage: TokenStorageService) {
  }

  generateRefreshToken(refreshToken?: boolean) {
    if (refreshToken == undefined) refreshToken = true;
    if (refreshToken) return this.http.get(environment.API_URL + '/refresh');
    else return of({});
  }

  generateRefreshTokenCB(callback: any) {
    return this.http.get(environment.API_URL + '/refresh')
      .subscribe((data) => {
        callback((<any>data));
      },
        (error: any) => {
          console.log(error)
          if (error.status == 401) {
            Utils.logout();
          }
          if (error.status == 500) {
            callback(error);
          }
        },
        () => {
        })
  }
  postData(d: any, url: string, callback: any) {
    this.generateRefreshToken().pipe(switchMap(data => {
      Utils.setRefreshToken(data);
      return this.http.post(environment.API_URL + url, d);
    },
    )
    ).subscribe((data) => {
      callback((<any>data));
    },
      (error: any) => {
        console.log(error)
        if (error.status == 401) {
          Utils.logout();
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
      })

      ;

  }

  postDataPdf(d: any, url: string, callback: any) {
    let headers = new HttpHeaders();
    headers = headers.append('Accept', 'application/pdf; charset=utf-8');
    this.generateRefreshToken().pipe(switchMap(data => {
      Utils.setRefreshToken(data);
      return this.http.post(environment.API_URL + url, d, {
        headers: headers,
        observe: 'response',
        responseType: 'arraybuffer'
      });
    },
    )
    ).subscribe((data) => {
      callback((<any>data));
    },
      (error: any) => {
        console.log(error)
        if (error.status == 401) {
          Utils.logout();
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
      })

      ;

  }


  getData(d: any, url: string, callback: any, refreshToken?: boolean) {
    this.generateRefreshToken(refreshToken).pipe(switchMap(data => {
      if (refreshToken == undefined) refreshToken = true;
      if (refreshToken) Utils.setRefreshToken(data);
      return this.http.get(environment.API_URL + url, d);
    },
    )
    ).subscribe({
      next: (data) => {
        callback((<any>data));
      },
      //complete: () => console.info('complete'),
      error: (error) => {
        console.log(error);
        if (error.status == 401) {
          Utils.logout();
        }
        if (error.status == 500) {
          callback(error);
        }
      }

    })


  }
  /*
  getData1(d: any, url: string, callback: any, refreshToken?: boolean) {
    debugger;
    this.generateRefreshToken().pipe(switchMap(data => {
      Utils.setRefreshToken(data);
      return this.http.get(environment.API_URL + url, d);
    },
    )
    ).subscribe((data: any) => {
      callback((<any>data));
    },
      (error: any) => {
        console.log(error);
        if (error.status == 401) {
          Utils.logout();
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
      })

      ;

  }
  */

  getDataWithoutRefreshToken(d: any, url: string, callback: any) {
    return this.http.get(environment.API_URL + url, d)
      .subscribe(
        (data) => {
          callback((<any>data));
        },
        (error) => {
          // Handle API call error here.
        }
      );

  }

  getDataFiles(d: any, url: string, callback: any) {
    let headers = new HttpHeaders();
    headers = headers.append('Accept', 'application/octet-stream; charset=utf-8');
    this.generateRefreshToken().pipe(switchMap(data => {
      Utils.setRefreshToken(data);
      return this.http.post(environment.API_URL + url, d, {
        headers: headers,
        observe: 'response',
        responseType: 'arraybuffer'
      });
    },
    )
    ).subscribe((data) => {
      callback((<any>data));
    },
      (error: any) => {
        console.log(error)
        if (error.status == 401) {
          Utils.logout();
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
      })

      ;

  }

  getDataNoAuth(d: any, url: string, callback: any): void {
    this.http.get(environment.API_URL + url).subscribe({
      next: data => { callback((<any>data)); },
      error: error => {
        callback((<any>error));
      }
    })
  };

  get staticUtil() {
    return Utils;
  }
}
