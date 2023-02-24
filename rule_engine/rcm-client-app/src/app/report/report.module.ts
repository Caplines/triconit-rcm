import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from "../service/auth-service.service";
import { TokenInterceptor } from '../auth/token.interceptor';
//import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { FormsModule } from '@angular/forms';
import { HeaderModule } from '../header/header.module';
import { ReportComponent } from './report.component';
import { ReportRoutingModule } from './report.routing.module';
import { NgDatepickerComponent, NgDatepickerModule } from 'ng2-datepicker';
import { ApplicationService } from '../service/application.service';


@NgModule({
  declarations: [
    ReportComponent
  ],
  imports: [
    CommonModule,
    ReportRoutingModule, FormsModule,
    HeaderModule,
    NgDatepickerModule

  ],
  providers: [AuthService,ApplicationService],
  exports:[ReportComponent]
    //OfficeResolve,
   /* {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }]*/
})
export class ReportModule { }
