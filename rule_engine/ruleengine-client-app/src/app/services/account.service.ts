import { Http, Headers, RequestOptions,Response} from '@angular/http';
import { Injectable } from '@angular/core';
import {User} from "../model/model.user";
import {IVFModel} from "../model/model.ivf";

import {AppComponent} from "../app.component";
import { map } from 'rxjs/operators';

@Injectable()
export class AccountService {
  constructor(public http: Http) { }

  createAccount(user:User){
    return this.http.post(AppComponent.API_URL+'/account/register',user)
     .pipe(map(resp=>resp.json()));
  }

  getOffices(callback){
      return this.http.get(AppComponent.API_URL+'/open/getoffices')
       .pipe(map(resp=>resp.json())).subscribe(result => {
           callback(result.data);
       },
       error => {
           callback(error);
       },
       () => {

       });
    }
  
  validateIVF(ivf:IVFModel,callback){
      let headers = new Headers();
      headers.append('Accept', 'application/json');
      headers.append('Content-Type', 'application/json');
      // creating base64 encoded String from user name and password
      var base64Credential: string = btoa( "sdf@ss.com"+ ':' + "123456");
      headers.append("Authorization", "Basic " + base64Credential);

      let options = new RequestOptions();
      options.headers=headers;

      return this.http.post(AppComponent.API_URL+'/validateTreatmentPlan',ivf)
       .pipe(map(resp=>resp.json())).subscribe(result => {
           callback(result.data);
       },
       error => {
           callback(error);
       },
       () => {

       });
    }
}
