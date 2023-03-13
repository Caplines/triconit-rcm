import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from "../service/auth-service.service";
import { FormsModule } from '@angular/forms';
import { ReportComponent } from './report.component';
import { ReportRoutingModule } from './report.routing.module';
import { HeaderComponent } from '../header/header-component/header.component';


@NgModule({
  declarations: [
    ReportComponent
  ],
  imports: [
    CommonModule,
    ReportRoutingModule, FormsModule,
    HeaderComponent,

  ],
  providers: [AuthService],
  exports:[ReportComponent]

})
export class ReportModule { }
