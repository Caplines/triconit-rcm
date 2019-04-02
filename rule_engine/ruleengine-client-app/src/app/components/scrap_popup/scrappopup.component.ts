import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-scrappopup',
  templateUrl: './scrappopup.component.html',
  styleUrls: ['./scrappopup.css'],
  encapsulation: ViewEncapsulation.None
})
export class ScrapPopupComponent implements OnInit {
  @Output() emitToParent = new EventEmitter<any>();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;

  ngOnInit() {
  }
  

}
