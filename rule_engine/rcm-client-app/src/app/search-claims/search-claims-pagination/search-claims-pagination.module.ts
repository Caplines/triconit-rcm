import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchClaimsPaginationComponent } from './search-claims-pagination.component';
import { FormsModule } from '@angular/forms';
import { PipesModule } from 'src/app/pipe/pipe-module';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { DownLoadService } from 'src/app/service/download.service';
import { RouterModule } from '@angular/router';



@NgModule({
  declarations: [SearchClaimsPaginationComponent],
  imports: [
    CommonModule,
    FormsModule,
    PipesModule,
    RouterModule
  ],
  exports:[SearchClaimsPaginationComponent],
  providers:[ApplicationServiceService,DownLoadService]
})
export class SearchClaimsPaginationModule { }
