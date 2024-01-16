import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-reconciliation',
  standalone: true,
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './reconciliation.component.html',
  styleUrls: ['./reconciliation.component.scss']
})
export class ReconciliationComponent {

  private title = inject(Title);

  reconcilData:any=[];
  loader:boolean=false;

  constructor(){
    this.title.setTitle("RCM TOOL - Reconciliation");
  }

  ngOnInit(){

    this.loader =true;
    setTimeout(()=>{
      this.reconcilData = [
        {
          title:"Primary & Secondary Unbilled",
          claimsES:100,
          claimsRCM:200,
          discrepancies:0 
        },
        {
          title:"Primary Open & Secondary Unbilled",
          claimsES:100,
          claimsRCM:200,
          discrepancies:0 
        },
        {
          title:"Primary Closed & Secondary Unbilled",
          claimsES:100,
          claimsRCM:200,
          discrepancies:0 
        },
        {
          title:"Primary Closed & Secondary Open",
          claimsES:100,
          claimsRCM:200,
          discrepancies:0 
        },
        {
          title:"Primary & Secondary Closed",
          claimsES:100,
          claimsRCM:200,
          discrepancies:0 
        },
      ];
      this.loader=false;
  },3000
    )
        
}

}
