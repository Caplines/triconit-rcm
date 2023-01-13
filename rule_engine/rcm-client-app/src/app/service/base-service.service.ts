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
    'getTeams':environment.API_URL+"/master/getteams",
    'getOffices':environment.API_URL+"/master/getoffices",
    'registerUser':environment.API_URL+"/register",
    'findUserByUserName':environment.API_URL+"/finduser",
    'changePassword':environment.API_URL+"/resetpassword",
    'getAllUsers':environment.API_URL+"/getAllUsers",
    'updateStatus':environment.API_URL+"/resetstatus",
    'getUserRoles':environment.API_URL+"/master/getroles",
    'forgotPassword':environment.API_URL+"/forgotPassword",
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

  getTeamsData(callback:any){
    return this.http.get(this.httpUrl['getTeams']).pipe(map((data:any)=>{
			data;
			return data;
		})).subscribe(
			(data:any) => { callback({'status':true,'result':data}); 
		});
  }

  getOfficeData(callback:any){
    return this.http.get(this.httpUrl['getOffices']).pipe(map((data:any)=>{
			return data;
		})).subscribe(
			(data:any) => { callback({'status':true,'result':data}); 
		});
  }

  getUserRoleData(callback:any){
    return this.http.get(this.httpUrl['getUserRoles']).pipe(map((data:any)=>{
			return data;
		})).subscribe(
			(data:any) => { callback({'status':true,'result':data}); 
		});
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

  findAllUser(callback:any){
    return this.http.get(this.httpUrl['getAllUsers']).pipe(map((data:any)=>{
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

}
