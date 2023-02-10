import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, mergeMap, switchMap, catchError, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';

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
    'findAllUser':'/getAllUsers/'
  }

  constructor(public http: HttpClient) {
   }

  generateRefreshToken() {
    return this.http.get(environment.API_URL + '/refresh');
  }

  postData(d: any, url: string, callback: any) {
    this.generateRefreshToken().pipe(switchMap(data => {
      localStorage.setItem("token", (<any>data).token);
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
      localStorage.setItem("token", (<any>data).token);
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
