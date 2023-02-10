import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {FormsModule} from "@angular/forms"
import { HeaderModule } from 'src/app/header/header.module';
import { UserStatusComponent } from '../users-status.component';
import { UserStatusRoutingModule } from './users-status-routing.module';


@NgModule({
  declarations: [UserStatusComponent],
  imports: [
    CommonModule,
    UserStatusRoutingModule,
    FormsModule,
    HeaderModule
  ]
})
export class UserStatusModule { }
