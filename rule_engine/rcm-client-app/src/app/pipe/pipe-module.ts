import { NgModule } from '@angular/core';
import { ClaimTypeFilter } from './claim-type-pipe';
import { ClaimSubmissionTeamPipe } from './claim-submission-team-pipe';
import { ClaimIdPipe } from './claim-id-pipe';
import { MessageTypePipe } from './message-type.pipe';
import { GeneralDataFilter } from './general-filter';

@NgModule({
  imports: [
    // dep modules
  ],
  declarations: [
    ClaimTypeFilter,
    ClaimIdPipe,
    MessageTypePipe,
    GeneralDataFilter,
    ClaimSubmissionTeamPipe
  ],
  exports: [
    ClaimTypeFilter,
    ClaimIdPipe,
    MessageTypePipe,
    GeneralDataFilter,
    ClaimSubmissionTeamPipe

  ]
})
export class PipesModule { }
