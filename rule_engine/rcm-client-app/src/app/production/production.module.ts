import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from "../service/auth-service.service";
//import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { FormsModule } from '@angular/forms';
import { ProductionComponent } from './production-component/production.component';
import { ProductionRoutingModule } from './production-routing.module';
import { DateRangeModule } from '../shared/date-range-picker/date-range-picker.module';


@NgModule({
  declarations: [
    ProductionComponent
  ],
  imports: [
    CommonModule,
    ProductionRoutingModule, FormsModule,
    DateRangeModule

  ],
  providers: [AuthService],
  exports:[ ProductionComponent]
})
export class ProductionModule { }
