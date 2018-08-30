import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgDatepickerModule } from 'ng2-datepicker';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { FormsModule } from '@angular/forms';
import { AuthService } from "./services/auth.service";
import { OfficeResolve } from "./resolver/office_resolver";

import {HttpModule} from "@angular/http";
import {AccountService} from "./services/account.service";
import { ProfileComponent } from './components/profile/profile.component';
import { IVFComponent } from './components/ivf/ivf.component';
import { IVFBatchPreComponent } from './components/ivfbatchpre/ivfbatchpre.component';
import { IVFBatchComponent } from './components/ivfbatch/ivfbatch.component';
import { ReportComponent } from './components/report/report.component';
import { LogoutComponent } from './components/logout/logout.component';
import { HeaderComponent } from './components/header/header.component';
import { IVFPopupComponent } from './components/ivfpopup/ivfpopup.component';
import {TreatmentPlanComponent} from "./components/treatmentplan/treatmentplan.component";

import {routing} from "./app.routing";
import {UrlPermission} from "./urlPermission/url.permission";
import {UrlAdminPermission} from "./urlPermission/url.adminpermission";
import {UrlLoggedInCheck} from "./urlPermission/url.checkloginstate";
import { HttpClientModule,HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './auth/token.interceptor';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    IVFComponent,
    IVFBatchComponent,
    IVFBatchPreComponent,
    ReportComponent,
    LogoutComponent,
    HeaderComponent,
    IVFPopupComponent,
	TreatmentPlanComponent
  ],
  imports: [
    BrowserModule,HttpModule,FormsModule,routing,HttpClientModule,NgDatepickerModule
  ],
  providers: [AuthService,AccountService,UrlPermission,UrlAdminPermission,UrlLoggedInCheck,
	  OfficeResolve,
       {
          provide: HTTP_INTERCEPTORS,
          useClass: TokenInterceptor,
          multi: true
      }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
