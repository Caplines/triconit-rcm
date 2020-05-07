import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgDatepickerModule } from 'ng2-datepicker';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { FormsModule } from '@angular/forms';
import { AuthService } from "./services/auth.service";
import { OfficeResolve } from "./resolver/office_resolver";

import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { HttpClientXsrfModule} from "@angular/common/http";

import { ApplicationService} from "./services/application.service";
import { ProfileComponent } from './components/profile/profile.component';
import { IVFComponent } from './components/ivf/ivf.component';
import { IVFBatchPreComponent } from './components/ivfbatchpre/ivfbatchpre.component';
import { IVFBatchComponent } from './components/ivfbatch/ivfbatch.component';
import { ReportComponent } from './components/report/report.component';
import { LogoutComponent } from './components/logout/logout.component';
import { HelpComponent } from './components/help/help.component';
import { HeaderComponent } from './components/header/header.component';
import { IVFPopupComponent } from './components/ivfpopup/ivfpopup.component';
import { DiagnosticPopupComponent } from './components/diagnosticpopup/diagnosticpopup.component';
import { TreatmentPlanComponent} from "./components/treatmentplan/treatmentplan.component";
import { DiagnosticComponent} from "./components/diagnostic/diagnostic.component";
import { UserInputComponent} from "./components/userinput/userinput.component";
import { QuestionformPopupComponent } from './components/questionformpopup/questionformpopup.component';
import { EnReportsComponent} from "./components/enhanced_reports/enreports.component";
import { EnReportspopupComponent } from './components/enhanced_reportspopup/enreportspopup.component';
import { ScrapComponent} from "./components/scrap/scrap.component";
import { ScrapFullDataComponent} from "./components/scrapfulldata/scrapfulldata.component";
import { ScrapPopupComponent } from './components/scrap_popup/scrappopup.component';
import {UserSettingsComponent} from "./components/usersettings/usersettings.component";
import {UserSettingsPopupupComponent} from "./components/usersettingspopupup/usersettingspopupup.component";
import {ResetPasswordComponent} from "./components/resetpassword/resetpassword.component";
import {IVFDumpComponent} from "./components/ivfdump/ivfdump.component";
import { ResultTypeFilterPipe} from './filter/result_type_filter';
import { routing} from "./app.routing";
import { UrlPermission} from "./urlPermission/url.permission";
import { UrlAdminPermission} from "./urlPermission/url.adminpermission";
import { UrlLoggedInCheck} from "./urlPermission/url.checkloginstate";
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
    HelpComponent,
    HeaderComponent,
    IVFPopupComponent,
    DiagnosticPopupComponent,
	TreatmentPlanComponent,
	DiagnosticComponent,
	UserInputComponent,
	QuestionformPopupComponent,
	ResultTypeFilterPipe,
	EnReportsComponent,
	EnReportspopupComponent,
	ScrapComponent,
	ScrapFullDataComponent,
	ScrapPopupComponent,
	UserSettingsComponent,
	UserSettingsPopupupComponent,
	ResetPasswordComponent,
	IVFDumpComponent
  ],
  imports: [
    BrowserModule,HttpModule,FormsModule,routing,HttpClientModule,NgDatepickerModule
    /*
    ,HttpClientXsrfModule.withOptions({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-CSRF-TOKEN'
      })*/
  ],
  providers: [AuthService,ApplicationService,UrlPermission,UrlAdminPermission,UrlLoggedInCheck,
	  OfficeResolve,
       {
          provide: HTTP_INTERCEPTORS,
          useClass: TokenInterceptor,
          multi: true
      }
      /*,
      {
          provide: XSRFStrategy,
          useValue: new CookieXSRFStrategy('XSRF-TOKEN', 'X-XSRF-TOKEN')
         
      }
      */
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
