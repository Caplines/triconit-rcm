import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {IssueClaimRoutingModule } from './issue-claims-routing.module';
import { IssueClaimComponent } from '../issue-claims.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [IssueClaimComponent],
  imports: [
    CommonModule,
    IssueClaimRoutingModule,
    FormsModule
  ],
  exports:[IssueClaimComponent]
})
export class IssueClaimCModule { }
