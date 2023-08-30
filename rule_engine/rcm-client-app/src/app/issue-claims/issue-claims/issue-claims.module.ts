import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {IssueClaimRoutingModule } from './issue-claims-routing.module';
import { IssueClaimComponent } from '../issue-claims.component';
import { FormsModule } from '@angular/forms';
import {PaginationModule} from '../../shared/pagination/pagination/pagination.module'


@NgModule({
  declarations: [IssueClaimComponent],
  imports: [
    CommonModule,
    IssueClaimRoutingModule,
    FormsModule,
    PaginationModule
  ],
  exports:[IssueClaimComponent]
})
export class IssueClaimCModule { }
