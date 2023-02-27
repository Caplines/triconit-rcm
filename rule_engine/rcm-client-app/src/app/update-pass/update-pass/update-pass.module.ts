import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {FormsModule} from "@angular/forms"
import { UpdatePasswordComponent,} from '../update-pass.component';
import { UpdatePasswordRoutingModule} from './update-pass-routing.module';
import { HeaderComponent } from 'src/app/header/header-component/header.component';


@NgModule({
  declarations: [UpdatePasswordComponent],
  imports: [
    CommonModule,
    UpdatePasswordRoutingModule,
    FormsModule,
    HeaderComponent
  ]
})
export class UpdatePasswordModule { }
