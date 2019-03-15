import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-enreportspopup',
  templateUrl: './enreportspopup.component.html',
  styleUrls: ['./enreportspopup.css'],
  encapsulation: ViewEncapsulation.None
})
export class EnReportspopupComponent implements OnInit {
  @Input() enreportData:any;
  @Input() reportType:any;
  @Input() showEnReportPopup:boolean;
  @Output() emitToParent = new EventEmitter<any>();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
 
  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.offices.push({"name":"All OFFICES","uuid":"All offices"});
  }

  ngOnInit() {
  }
  
  closePopup() {
	  this.emitToParent.emit({action: "showEnReportPopup", value: false});
  }
}
