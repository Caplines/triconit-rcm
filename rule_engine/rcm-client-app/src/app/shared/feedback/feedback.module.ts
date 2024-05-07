import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeedbackComponent } from './feedback.component';
import { DatePipe } from '@angular/common';


@NgModule({
  declarations: [FeedbackComponent],
  imports: [
    CommonModule,
  ],
  exports: [FeedbackComponent],
  providers: [DatePipe]
})
export class FeedbackModule { }
