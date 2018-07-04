import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {IVFModel} from "../../model/model.ivf";
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-ivf',
  templateUrl: './ivf.component.html',
  styleUrls: ['./ivf.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class IVFComponent implements OnInit {
  ivfm: IVFModel = new IVFModel();
  errorMessage: string;
  offices:any;
  constructor(public accountService: AccountService, public router: Router) {
  }

  ngOnInit() {
      this.accountService.getOffices((result) => {
        console.log(result);
        this.offices=result;
      });
       
  }

  validateIVF() {
      console.log(this.ivfm);
      this.errorMessage = "sdff";
      if( this.ivfm.officeId) {
          this.accountService.validateIVF(this.ivfm,(result) => {
              console.log(result);
        });
      }
  }
}
