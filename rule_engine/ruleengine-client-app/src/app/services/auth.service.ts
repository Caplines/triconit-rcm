import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions,Response} from '@angular/http';
import { Observable } from "rxjs";
import {User} from "../model/model.user";
import { map } from 'rxjs/operators';
import {AppComponent} from "../app.component";
import { Subject } from 'rxjs';
import Utils from '../util/utils';
@Injectable()
export class AuthService {


  private emitChangeSource = new Subject<any>();
  
  constructor(public http: Http) { }

  public logIn(user: User){
    let headers = new Headers();
    return this.http.post(AppComponent.API_URL+"/account/login" ,{"username":user.email,"password":user.password})
    .pipe(map((response: Response) => {
        //console.log(response);
        let data=response.json().data;
        //console.log(data.token);
      // login successful if there's a jwt token in the response
      let token = data.token;
      if (token) {
          Utils.setLocalStorage(data, token);
      }
    }));
  }

  logOut(callback) {
      
      Utils.resetLocalStorage();
       if (callback && typeof callback =="function" ) callback();
  }
  
  changeEmitted$ = this.emitChangeSource.asObservable();
  
  emitChange(change: any) {
     this.emitChangeSource.next(change);
  }
  
}
