import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
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
    'registerUser':environment.API_URL+"/register",
    'findUserByUserName':environment.API_URL+"/finduser",
    'changePassword':environment.API_URL+"/resetpassword",
    'updateStatus':environment.API_URL+"/resetstatus",
    'forgotPassword':environment.API_URL+"/forgotPassword",
    'editOfficeName': environment.API_URL+"/editOffice",
    'addNewOffice': environment.API_URL+"/addOffice",
    'userByTeamId':"/users/team",
    'assignOffice':"/assignOffice"
  }
  token:any;

  constructor(public http: HttpClient) {
    this.token = localStorage.getItem("token")
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

  registerUser(params:any,callback:any){
    let token:any = localStorage.getItem("token")
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ token});
    return this.http.post(this.httpUrl['registerUser'],params,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback({'status':true,'result':data}); 
    });
  }

  findUser(username:any,callback:any){
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ this.token});
    return this.http.post(this.httpUrl['findUserByUserName'],username,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback({'status':true,'result':data}); 
    });
  }
  changePassword(params:any,callback:any){
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ this.token});
    return this.http.post(this.httpUrl['changePassword'],params,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback({'status':true,'result':data}); 
    });
  }

  updateUserStatus(params:any,callback:any){
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ this.token});
    return this.http.post(this.httpUrl['updateStatus'],params,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback({'status':true,'result':data}); 
    });
  }

  forgotPassword(params:any,callback:any){
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ this.token});
    return this.http.post(this.httpUrl['forgotPassword'],params,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback({'status':true,'result':data}); 
    });
  }
  
  editOfficeName(params:any,callback:any){
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ this.token});
    return this.http.post(this.httpUrl['editOfficeName'],params,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback({'status':true,'result':data}); 
    });
  }

  addNewOffice(params:any,callback:any){
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ this.token});
    return this.http.post(this.httpUrl['addNewOffice'],params,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback({'status':true,'result':data}); 
    });
  }
}
