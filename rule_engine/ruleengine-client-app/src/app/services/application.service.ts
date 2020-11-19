import { Http, Headers, RequestOptions,Response} from '@angular/http';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { Injectable , isDevMode} from '@angular/core';
import {User} from "../model/model.user";
import {IVFModel} from "../model/model.ivf";
import {IVDumpModel} from "../model/model.ivdump";
import {DiagnosticModel} from "../model/model.diagnostic";
import {IVFBatchPreModel} from "../model/model.ivfbatchpre";
import {IVFBatchModel} from "../model/model.ivfbatch";
import {ReportModel} from "../model/model.report";
import {EnReportsModel} from "../model/model.enreports";
import {UserInputModel} from "../model/model.userinput";
import {TreatmentPlanModel} from "../model/model.treatmentplan";
import {ScrapModel} from "../model/model.scrap";
import {AuthHeader} from "../util/auth.header";
import {AppComponent} from "../app.component";
import { map,flatMap,mergeMap,switchMap,catchError,timeout } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
//import {  timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable()
export class ApplicationService {
    
   authHeader= new  AuthHeader(); 
  constructor(public http: HttpClient,public htt: Http,private router:Router) {
	   
	  
  }

  createAccount(user:User,callback){
      
      this.generateRefreshToken().pipe(switchMap(data => {
        localStorage.setItem("token", (<any>data).token);
          return  this.http.post(environment.API_URL+'/admin/register',user);
        })).subscribe(data => {
              // console.log(data['results']);
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
		 * this.generateRefreshToken().pipe(flatMap( (result) => {
		 * localStorage.setItem("token", result.token); return
		 * this.http.post(environment.API_URL+'/admin/register',user,AuthHeader.createAuthHeader())
		 * .pipe(map(resp=>resp.json())); } )).subscribe(result => {
		 * callback(result.data); }, error => {
		 * this.router.navigate(['/logout']); callback(error); }, () => {
		 * 
		 * });
		 */  
  }

  getOffices(callback){
      return this.htt.get(environment.API_URL+'/open/getoffices')
       .pipe(map(resp=>resp.json())).subscribe(result => {
           callback(result.data);
       },
       error => {
           callback(error);
    
       },
       () => {

       });
    }
  
  getSdDetails(callback,uuid,st){
      this.generateRefreshToken().pipe(switchMap(data => {
          localStorage.setItem("token", (<any>data).token);
            return  this.http.get(environment.API_URL+'/scrapsiteud/'+st+"/"+uuid)
          })).subscribe(data => {
                // console.log(data['results']);
              callback((<any>data));
          },
          error => {
              callback(error);
              // this.router.navigate(['/logout']);
          },
          () => {
          }
          
          );
    }
  /*
	 * return this.http.post(newsUrl, news).pipe( map( (dataFromApi) =>
	 * dataFromApi ), catchError( (err) => Observable.throw(err.json().error) ) )
	 */
  getOfficesPrior(){
      return this.htt.get(environment.API_URL+'/open/getoffices').pipe(
    	map( (resp=>resp.json() ),
       catchError( (err) => Observable.throw(err.json().error) )));
    }
  validateIVF(ivf:IVFModel,ivfValidateName,callback){
      this.generateRefreshToken().pipe(switchMap(data => {
          localStorage.setItem("token", (<any>data).token);
            return  this.http.post(environment.API_URL+'/'+ivfValidateName,ivf);
          },
          )    
      ).subscribe(data => {
              callback((<any>data));
          },
          error => {
              if (error.status==401){
              this.router.navigate(['/logout']);
              }
              if (error.status==500){
                  alert("Some un-Wanted Changes Done to Google Sheets");
                  callback(error);
              }
          },
          () => {
          }
          
          );
              
  }
  
  doDiagCheck(diagm:DiagnosticModel,callback){
      this.generateRefreshToken().pipe(switchMap(data => {
          localStorage.setItem("token", (<any>data).token);
            return  this.http.post(environment.API_URL+'/diagnosticcheck',diagm);
          },
          )    
      ).subscribe(data => {
              // console.log(data);
              callback((<any>data));
          },
          error => {
              // console.log(33);
              if (error.status==401){
              this.router.navigate(['/logout']);
              }
              if (error.status==500){
                  alert("Some Service Error- Contact Admin..");
                  callback(error);
              }
          },
          () => {
              // console.log(111);
          }
          
          );
              
  }
  
  validateReport(report:ReportModel,ur:string,callback){
		this.generateRefreshToken().pipe(switchMap(data => {
			localStorage.setItem("token", (<any>data).token);
			return  this.http.post(environment.API_URL+ur,report);
		})
		).subscribe(data => {
			// console.log(data['results']);
			callback((<any>data));
		},
		error => {  
			if (error.status==401){ 
				this.router.navigate(['/logout']);
			}
			else if (error.status==500){
				alert("Some un-Wanted Changes Done to Google Sheets");
				callback(error);
            }
        },
        () => {   
			// console.log(111);
			}
		);   
	}
     // fields, officeId,startDate,endDate,pid,tpId,reportType
  validateEnReport(enreports:EnReportsModel,ur:string,callback){
		this.generateRefreshToken().pipe(switchMap(data => {
			localStorage.setItem("token", (<any>data).token);
			return  this.http.post(environment.API_URL+ur,enreports);
		})
		).subscribe(data => {
			callback((<any>data));
		},
		error => {  
			if (error.status==401){ 
				this.router.navigate(['/logout']);
			}
			else if (error.status==500){
				alert("Some Technical issues.");
				callback(error);
          }
      },
      () => {        
			// console.log(111);
			}
		);   
	}
	
  callPostAPI(mod:any,ur:string,callback){
		this.generateRefreshToken().pipe(switchMap(data => {
			localStorage.setItem("token", (<any>data).token);
			return  this.http.post(environment.API_URL+ur,mod);
		})
		).subscribe(data => {
			callback((<any>data));
		},
		error => {  
			if (error.status==401){ 
				this.router.navigate(['/logout']);
			}
			else if (error.status==500){
				alert("Some Technical issues.");
				callback(error);
        }
    },
    () => {        
			// console.log(111);
			}
		);   
	}

  generateTreatmentPlanId(treatment:TreatmentPlanModel, callback) {
		this.generateRefreshToken().pipe(switchMap(data => {
			localStorage.setItem("token", (<any>data).token);
			return  this.http.post(environment.API_URL+'/generateTreatmentId',treatment);
		})
		).subscribe(data => {
			// console.log(data['results']);
			callback((<any>data));
		},
		error => {  
			// console.log(33);
			if (error.status==401){ 
				this.router.navigate(['/logout']);
			}
            if (error.status==500){
				alert("Some Technical issues..");
				callback(error);
            }
        },
        () => {        
			// console.log(111);
			}
		);  
	}
	
	  getUserInputs(uim:UserInputModel,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	          // console.log((<any>data).token);
	          localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+'/getUserInputQuestionsAns',uim);
	          },
	          )    
	      ).subscribe(data => {
	              // console.log(data);
	              callback((<any>data));
	          },
	          error => {
	              // console.log(33);
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some Technical issues");
	                  callback(error);
	              }
	          },
	          () => {
	              // console.log(111);
	          }
	          
	          );
	              
	  }
	  
	  saveUserInput(answerData:any,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	          // console.log((<any>data).token);
	          localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+'/saveUserInput',answerData);
	          },
	          )    
	      ).subscribe(data => {
	              // console.log(data);
	              callback((<any>data));
	          },
	          error => {
	              // console.log(33);
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Database");
	                  callback(error);
	              }
	          },
	          () => {
	              // console.log(111);
	          }
	          
	          );
	              
	  }
	  
	  findUserByUserName(userName:string,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	           localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+'/admin/finduserbyusername',userName);
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Database");
	                  callback(error);
	              }
	          },
	          () => {
	              // console.log(111);
	          }
	          
	          );
	              
	  }
	  
	  updatepassword(password:string,uuid:string,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	           localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+'/resetpassword',{'uuid':uuid,'password':password});
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Database");
	                  callback(error);
	              }
	          },
	          () => {
	              // console.log(111);
	          }
	          
	          );
	              
	  }	  

	  updateStatus(status:number,uuid:string,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	           localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+'/admin/resetstatus',{'uuid':uuid,'status':status});
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Database");
	                  callback(error);
	              }
	          },
	          () => {
	              // console.log(111);
	          }
	          
	          );
	              
	  }	  

	  resetRight(status:number,uuid:string,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	           localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+'/admin/resetclaimtreatment',{'uuid':uuid,'status':status});
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Database");
	                  callback(error);
	              }
	          },
	          () => {
	              // console.log(111);
	          }
	          
	          );
	              
	  }	  

	  scrapSite(scrap:ScrapModel,scrapsite,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	          // console.log((<any>data).token);
	          localStorage.setItem("token", (<any>data).token);
	          // console.log("token is set");
	            return  this.http.post(environment.API_URL+'/'+scrapsite,scrap)
	            /*
				 * . pipe( timeout(60 * 1000 * 30), catchError(e => { // do
				 * something on a timeout alert("Data will written in Sheet.
				 * Shortly"); return null; }) );
				 */
	          },
	          )    
	      ).subscribe(data => {
	              // console.log(data);
	              callback((<any>data));
	          },
	          error => {
	              // console.log(33);
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some Techincal issues");
	                  callback(error);
	              }
	          },
	          () => {
	              // console.log(111);
	          }
	          
	          );
	              
	  }
	  
	  downloadIVFPDF(dataS:any, callback) {
		  let headers = new HttpHeaders();
		    headers = headers.append('Accept', 'application/pdf; charset=utf-8');
			this.generateRefreshToken().pipe(switchMap(data => {
				localStorage.setItem("token", (<any>data).token);
				return  this.http.post(environment.API_URL+'/genereatePdf',dataS,{
					 headers: headers,
				      observe: 'response',
				      responseType: 'arraybuffer'
				});
			})
			).subscribe(data => {
				callback((<any>data));
			},
			error => {  
				// console.log(33);
				if (error.status==401){ 
					this.router.navigate(['/logout']);
				}
	            if (error.status==500){
					alert("Some Technical issues..");
					callback(error);
	            }
	        },
	        () => {        
				}
			);  
		}
	 
	  
	  dumpIVFOlDData(ivd:IVDumpModel,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	          localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+'/dumpOldIVFData',ivd);
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Google Sheets");
	                  callback(error);
	              }
	          },
	          () => {
	          }
	          
	          );
	              
	  }

	  
	  getAllUserNames(callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	          localStorage.setItem("token", (<any>data).token);
	            return  this.http.get(environment.API_URL+'/getAllUsers');
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Google Sheets");
	                  callback(error);
	              }
	          },
	          () => {
	          }
	          
	          );
	              
	  }

	  getAllScrappingFullDataSites(callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	          localStorage.setItem("token", (<any>data).token);
	            return  this.http.get(environment.API_URL+'/getsitenametoparsefulldata');
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted Changes Done to Google Sheets");
	                  callback(error);
	              }
	          },
	          () => {
	          }
	          
	          );
	              
	  }

	  postData(d:any,url:string,callback){
	      this.generateRefreshToken().pipe(switchMap(data => {
	          localStorage.setItem("token", (<any>data).token);
	            return  this.http.post(environment.API_URL+url,d);
	          },
	          )    
	      ).subscribe(data => {
	              callback((<any>data));
	          },
	          error => {
	              if (error.status==401){
	              this.router.navigate(['/logout']);
	              }
	              if (error.status==500){
	                  alert("Some un-Wanted ..");
	                  callback(error);
	              }
	          },
	          () => {
	          }
	          
	          );
	              
	  }

	  
	  generateRefreshToken(){
      return this.http.get(environment.API_URL+'/refresh');
  }
}
