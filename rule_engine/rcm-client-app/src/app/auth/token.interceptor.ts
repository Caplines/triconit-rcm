import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpXsrfTokenExtractor } from '@angular/common/http';
import { AuthHeader } from '../util/auth.header';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(private tokenExtractor: HttpXsrfTokenExtractor) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    //let token = this.tokenExtractor.getToken() as string;
    //console.log(token); 
    let ls:any=localStorage;
    if (ls.getItem("selected_teamId")==null){
      request = request.clone({
        setHeaders: {
          Authorization: 'Bearer ' + AuthHeader.getToken()
          //t : ls.getItem("selected_teamId"),
          //c : ls.getItem("selected_clientName"),
         // r : ls.getItem("selected_roleName")
          
  
          // , 'x-xsrf-token' :this.getCookie('XSRF-TOKEN')//Enable for Protection.
        }
      });
    }
    else { 
       request = request.clone({
      setHeaders: {
        Authorization: 'Bearer ' + AuthHeader.getToken(),
        t : ls.getItem("selected_teamId"),
        c : ls.getItem("selected_clientName"),
       r : ls.getItem("selected_roleName")
        

        // , 'x-xsrf-token' :this.getCookie('XSRF-TOKEN')//Enable for Protection.
      }
    });
  }
    /*
    request = request.clone({
        withCredentials: true
      });
    console.log(request);
    */
    return next.handle(request);
  }


  getCookie(cname: any) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
      var c = ca[i];
      while (c.charAt(0) == ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
        return c.substring(name.length, c.length);
      }
    }
    return "";
  }
}