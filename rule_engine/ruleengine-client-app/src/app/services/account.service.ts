import { Http, Headers, RequestOptions,Response} from '@angular/http';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {User} from "../model/model.user";
import {IVFModel} from "../model/model.ivf";
import {IVFBatchPreModel} from "../model/model.ivfbatchpre";
import {IVFBatchModel} from "../model/model.ivfbatch";
import {ReportModel} from "../model/model.report";
import {AuthHeader} from "../util/auth.header";
import {AppComponent} from "../app.component";
import { map,flatMap,mergeMap,switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class AccountService {
    
   authHeader= new  AuthHeader(); 
  constructor(public http: HttpClient,public htt: Http,private router:Router) { }

  createAccount(user:User,callback){
      
      this.generateRefreshToken().pipe(switchMap(data => {
        localStorage.setItem("token", (<any>data).token);
          return  this.http.post(AppComponent.API_URL+'/admin/register',user);
        })).subscribe(data => {
              //console.log(data['results']);
            callback((<any>data));
        },
        error => {
            callback(error);
            this.router.navigate(['/logout']);
        },
        () => {
        }
        
        );
      
      /*
      this.generateRefreshToken().pipe(flatMap(
              (result) => {
                  localStorage.setItem("token", result.token);
                  return this.http.post(AppComponent.API_URL+'/admin/register',user,AuthHeader.createAuthHeader())
                  .pipe(map(resp=>resp.json()));
                  }
                )).subscribe(result => {
                    callback(result.data);
                },
                error => {
                    this.router.navigate(['/logout']);
                    callback(error);
                },
                () => {

                });     
        */  
  }

  getOffices(callback){
      return this.htt.get(AppComponent.API_URL+'/open/getoffices')
       .pipe(map(resp=>resp.json())).subscribe(result => {
           callback(result.data);
       },
       error => {
           callback(error);
       },
       () => {

       });
    }
  
  validateIVF(ivf:IVFModel,ivfValidateName,callback){
      this.generateRefreshToken().pipe(switchMap(data => {
          console.log((<any>data).token);
          localStorage.setItem("token", (<any>data).token);
          console.log("token is set");
            return  this.http.post(AppComponent.API_URL+'/'+ivfValidateName,ivf);
          },
          )    
      ).subscribe(data => {
              console.log(data);
              callback((<any>data));
          },
          error => {
              console.log(33);
              if (error.status==401){
              this.router.navigate(['/logout']);
              }
              if (error.status==500){
                  alert("Some un-Wanted Chnages Done to Google Sheets");
                  callback(error);
              }
          },
          () => {
              console.log(111);
          }
          
          );
              
  }
  
  /* validateIVFPreBatch(ivf:IVFBatchPreModel,callback){
      this.generateRefreshToken().pipe(switchMap(data => {
          console.log((<any>data).token);
          localStorage.setItem("token", (<any>data).token);
          console.log("token is set");
            return  this.http.post(AppComponent.API_URL+'/validateTreatmentPlanPreBatch',ivf);
          },
          )    
      ).subscribe(data => {
              console.log(data);
              callback((<any>data));
          },
          error => {
              console.log(33);
              if (error.status==401){
              this.router.navigate(['/logout']);
              }
              if (error.status==500){
                  alert("Some un-Wanted Chnages Done to Google Sheets");
                  callback(error);
              }
          },
          () => {
              console.log(111);
          }
          
          );
              
  }


  validateIVFBatch(ivf:IVFBatchModel,callback){
      this.generateRefreshToken().pipe(switchMap(data => {
          console.log((<any>data).token);
          localStorage.setItem("token", (<any>data).token);
          console.log("token is set");
            return  this.http.post(AppComponent.API_URL+'/validateTreatmentPlanBatch',ivf);
          },
          )    
      ).subscribe(data => {
              console.log(data);
              callback((<any>data));
          },
          error => {
              console.log(33);
              if (error.status==401){
              this.router.navigate(['/logout']);
              }
              if (error.status==500){
                  alert("Some un-Wanted Chnages Done to Google Sheets");
                  callback(error);
              }
          },
          () => {
              console.log(111);
          }
          
          );
              
  } */
  
  validateReport(report:ReportModel,callback){
		this.generateRefreshToken().pipe(switchMap(data => {
			localStorage.setItem("token", (<any>data).token);
			return  this.http.post(AppComponent.API_URL+'/admin/report',report);
		})
		).subscribe(data => {
			//console.log(data['results']);
			callback((<any>data));
		},
		error => {  
			console.log(33);
			if (error.status==401){ 
				this.router.navigate(['/logout']);
			}
            if (error.status==500){
				alert("Some un-Wanted Chnages Done to Google Sheets");
				callback(error);
            }
        },
        () => {        
			console.log(111);   
			}
		);   
	}
  
  generateRefreshToken(){
      return this.http.get(AppComponent.API_URL+'/refresh');
  }
      /*
      return this.http.get(AppComponent.API_URL+'/refresh',AuthHeader.createAuthHeader())
       .pipe(map(resp=>resp.json()));
    */ 
}
