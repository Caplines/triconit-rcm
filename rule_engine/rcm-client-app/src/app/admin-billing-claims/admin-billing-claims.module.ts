import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminBillingClaimsComponent } from './admin-billing-claims.component';
import { PipesModule } from '../pipe/pipe-module';
import { FormsModule } from '@angular/forms';
import { ApplicationServiceService } from '../service/application-service.service';
import { DownLoadService } from '../service/download.service';
import { MultiSelectDropdownModule } from '../shared/multi-select-dropdown/multi-select-dropdown/multi-select-dropdown.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        PipesModule,
        MultiSelectDropdownModule,
    ],
    providers: [ApplicationServiceService, DownLoadService],
    declarations: [AdminBillingClaimsComponent],
    exports: [AdminBillingClaimsComponent]
})
export class AdminBillingClaimsModule { }
