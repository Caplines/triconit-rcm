import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from "../service/auth-service.service";
import { FormsModule } from '@angular/forms';
import { HeaderModule } from '../header/header.module';
import { ReportComponent } from './report.component';
import { ReportRoutingModule } from './report.routing.module';


@NgModule({
  declarations: [
    ReportComponent
  ],
  imports: [
    CommonModule,
    ReportRoutingModule, FormsModule,
    HeaderModule,

  ],
  providers: [AuthService],
  exports:[ReportComponent]
    //OfficeResolve,
   /* {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }]*/
})
export class ReportModule { }
