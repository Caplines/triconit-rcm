import { NgModule } from '@angular/core';
import { ClaimTypeFilter } from './claim-type-pipe';
import { ClaimIdPipe } from './claim-id-pipe';
import { MessageTypePipe } from './message-type.pipe';
import {GeneralDataFilter} from './general-filter';

@NgModule({
  imports: [
    // dep modules
  ],
  declarations: [ 
    ClaimTypeFilter,
    ClaimIdPipe,
    MessageTypePipe,
    GeneralDataFilter
  ],
  exports: [
    ClaimTypeFilter,
    ClaimIdPipe,
    MessageTypePipe,
    GeneralDataFilter

  ]
})
export class PipesModule {}
