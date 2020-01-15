import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpXsrfTokenExtractor} from '@angular/common/http';
import {AuthHeader} from '../util/auth.header';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(private tokenExtractor: HttpXsrfTokenExtractor) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
	//let token = this.tokenExtractor.getToken() as string;
	//console.log(token);  
    request = request.clone({
      setHeaders: {
        Authorization: 'Bearer '+AuthHeader.getToken()
       //, 'x-xsrf-token':'token'
      }
    });
    /*
    request = request.clone({
        withCredentials: true
      });
    console.log(request);
    */
    return next.handle(request);
  }
}