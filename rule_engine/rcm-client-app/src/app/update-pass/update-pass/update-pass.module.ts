import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {FormsModule} from "@angular/forms"
import { HeaderModule } from 'src/app/header/header.module';
import { UpdatePasswordComponent,} from '../update-pass.component';
import { UpdatePasswordRoutingModule} from './update-pass-routing.module';


@NgModule({
  declarations: [UpdatePasswordComponent],
  imports: [
    CommonModule,
    UpdatePasswordRoutingModule,
    FormsModule,
    HeaderModule
  ]
})
export class UpdatePasswordModule { }
