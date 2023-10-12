import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SearchClaimsRoutingModule } from './search-claims-routing.module';
import { SearchClaimsComponent } from './search-claims.component';
import { MultiSelectDropdownModule } from '../shared/multi-select-dropdown/multi-select-dropdown/multi-select-dropdown.module';
import { DateRangeModule } from '../shared/date-range-picker/date-range-picker.module';
import { FormsModule } from '@angular/forms';
import { SearchClaimsPaginationModule } from './search-claims-pagination/search-claims-pagination.module';

@NgModule({
  declarations: [SearchClaimsComponent],
  imports: [
    CommonModule,
    SearchClaimsRoutingModule,
    MultiSelectDropdownModule,
    DateRangeModule,
    FormsModule,
    SearchClaimsPaginationModule
  ],
  exports:[SearchClaimsComponent]
})
export class SearchClaimsModule { }
