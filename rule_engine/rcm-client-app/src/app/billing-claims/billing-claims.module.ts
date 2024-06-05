import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BillingClaimsRoutingModule } from './billing-claims-routing.module';
import { BillingClaimsComponent } from './billing-claims.component';
import { FormsModule } from '@angular/forms';
import { ApplicationServiceService } from '../service/application-service.service';
import { ClaimService } from '../service/claim.service';
import { DownLoadService } from '../service/download.service';
import { PipesModule } from '../pipe/pipe-module';
import { AttachFileModule } from '../shared/attach-file/attach-file.module';
import { DatePipe } from '@angular/common';

import { PdfViewerModule } from "ng2-pdf-viewer";
import { MultiSelectDropdownModule } from "../shared/multi-select-dropdown/multi-select-dropdown/multi-select-dropdown.module";
import { FeedbackModule } from 'src/app/shared/feedback/feedback.module';
import { HeaderComponent } from '../header/header-component/header.component';
import { DatepickerModule } from 'ng2-datepicker';
import { RcmDatePickerModule } from '../shared/date-picker/date-picker/rcm-date-picker.module';

@NgModule({
  declarations: [BillingClaimsComponent],
  imports: [
    CommonModule,
    BillingClaimsRoutingModule,
    FormsModule,
    PipesModule,
    AttachFileModule,
    PdfViewerModule,
    MultiSelectDropdownModule,
    FeedbackModule,
    HeaderComponent,
    DatepickerModule,
    RcmDatePickerModule
  ],
  providers: [ApplicationServiceService, ClaimService, DownLoadService, DatePipe],
  exports: [BillingClaimsComponent],
  schemas: [
    CUSTOM_ELEMENTS_SCHEMA
  ],
})
export class BillingClaimsModule { }
