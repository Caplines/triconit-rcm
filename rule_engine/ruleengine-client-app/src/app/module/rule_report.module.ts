import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { RuleReportComponent} from '../components/rulereport/rule_report.component';
import { ApplicationService} from "../services/application.service";
import { NgDatepickerModule } from 'ng2-datepicker';

import { AuthService } from "../services/auth.service";
import { OfficeResolve } from "../resolver/office_resolver";
import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { HttpClientXsrfModule} from "@angular/common/http";
import {RuleReportRoutingModule} from '../components/rulereport/rule_report.routing';
import { TokenInterceptor } from '../auth/token.interceptor';
import { FormsModule } from '@angular/forms';
import { CommonModule} from '@angular/common';

@NgModule({
    imports: [ CommonModule ,FormsModule, HttpClientModule,NgDatepickerModule, RuleReportRoutingModule],
    declarations: [RuleReportComponent],
    bootstrap: [RuleReportComponent],
    providers: [AuthService,ApplicationService,
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
 ]
})

export class RuleReportAppModule { }