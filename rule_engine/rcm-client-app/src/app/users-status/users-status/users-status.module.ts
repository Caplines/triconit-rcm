import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {FormsModule} from "@angular/forms"
import { UserStatusComponent } from '../users-status.component';
import { UserStatusRoutingModule } from './users-status-routing.module';
import { HeaderComponent } from 'src/app/header/header-component/header.component';


@NgModule({
  declarations: [UserStatusComponent],
  imports: [
    CommonModule,
    UserStatusRoutingModule,
    FormsModule,
    HeaderComponent
  ]
})
export class UserStatusModule { }
