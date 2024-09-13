import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { unopenedclaimComponent } from './unopenedclaim.component';
import { unopenedclaimRoutingModule } from './unopenedclaim-routing.module';
import { FormsModule } from '@angular/forms';
import { PipesModule } from '../pipe/pipe-module';

@NgModule({
  declarations: [unopenedclaimComponent],
  imports: [
    CommonModule,
    unopenedclaimRoutingModule,
    FormsModule,
    PipesModule
  ],
  providers: [],
  exports: [unopenedclaimComponent]
})
export class unopenedclaimModule { }
