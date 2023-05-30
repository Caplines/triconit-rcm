import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {IssueClaimRoutingModule } from './issue-claims-routing.module';
import { IssueClaimComponent } from '../issue-claims.component';


@NgModule({
  declarations: [IssueClaimComponent],
  imports: [
    CommonModule,
    IssueClaimRoutingModule
  ],
  exports:[IssueClaimComponent]
})
export class IssueClaimCModule { }
