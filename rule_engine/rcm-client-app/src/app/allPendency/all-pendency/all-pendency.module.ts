import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { AllPendencyRoutingModule } from './all-pendency-routing.module';
import { AllPendencyComponent } from './all-pendency.component';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../header/header-component/header.component'



@NgModule({
  declarations: [
    AllPendencyComponent
  ],
  imports: [
    CommonModule,
    AllPendencyRoutingModule,
    FormsModule,
    HeaderComponent
  ],
  providers:[DatePipe]
})
export class AllPendencyModule { }
