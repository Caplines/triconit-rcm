import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthService } from './service/auth-service.service';
import { CheckUserLoggedInState } from './urlPermission/url.checkloginstate';
import { UrlPermission } from './urlPermission/url.permission';

import {TokenInterceptor} from './auth/token.interceptor';
import { HttpClientModule,HTTP_INTERCEPTORS } from '@angular/common/http';
import { FetchClaimsModule } from './fetch-claims/fetch-claims.module';
//import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,HttpClientModule
  ],
  providers: [AuthService,CheckUserLoggedInState,UrlPermission,
	  //,OfficeAndIVFormTypeResolve,
       {
          provide: HTTP_INTERCEPTORS,
          useClass: TokenInterceptor,
          multi: true
      }],
  bootstrap: [AppComponent]
})
export class AppModule { }
