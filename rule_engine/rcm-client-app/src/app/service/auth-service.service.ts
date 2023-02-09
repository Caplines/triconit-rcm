import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable ,map} from 'rxjs';
import { environment } from '../../environments/environment';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(username: string, password: string, callback: any): void {
    this.http.post(environment.API_URL + '/account/login',
      { "username": username, "password": password }).subscribe
      (data => {
        callback((<any>data));
      },(error: any) => {
        console.log(error);
        callback((<any>error));
      }
      );

  }

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post(environment.API_URL + 'signup', {
      username,
      email,
      password
    }, httpOptions);
  }

   forgotPassword(params:any,callback:any){
    let token = localStorage.getItem("token");
    let cpHeaders:Object = new Headers({'X-Authorization': 'Bearer '+ token});
    return this.http.post(environment.API_URL + '/forgotPassword',params,cpHeaders).pipe(map(data=>{
      return data;
    })).subscribe(
      (data:any) => { callback((<any>data)); 
    });
  }
}
