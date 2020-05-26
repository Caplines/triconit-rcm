import {Component, OnInit, ViewEncapsulation, Input} from '@angular/core';
import {IVDumpModel} from "../../model/model.ivdump";
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute } from "@angular/router";
import Utils from '../../util/utils';

@Component({
  selector: 'app-ivfdump',
  templateUrl: './ivfdump.component.html',
  styleUrls: ['./ivfdump.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class IVFDumpComponent implements OnInit {
	
  @Input() officeId:any;
  ivfd: IVDumpModel = new IVDumpModel();
  errorMessage: string;
  offices:any;
  showLoading: boolean = false;
  messageERR:string ="";
  
  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute) {
	  
	this.offices =this.route.snapshot.data['offs'].data;
	this.ivfd.newColumns=true;
  }

  ngOnInit() {
	if(this.officeId)this.ivfd.officeId = this.officeId;
	this.ivfd.sheetId="1POWJC8as3b3MvhN8EtLacUwLu3ABpI88JUECMQ2Ts10 - GOOGLE Sheet id";
	
  }
  
  setSheetName(e){
	  //this.ivfd.sheetName=e.target.options[e.target.selectedIndex].text+ " Database";
	  this.ivfd.sheetName="Database";
  }

  dumpData() {
	  this.messageERR="";
	if(this.ivfd.officeId) {
		
		
		this.showLoading = true;
		this.applicationService.dumpIVFOlDData(this.ivfd, (result) => {		
				if (result.status=='OK'){
					//alert(result.data);
					this.messageERR=result.data;
				    this.showLoading = false;
					
				}else{
					alert('Error Please contact admin');
					this.showLoading = false;
				}
			  });
	   }
  
  
   }
  
  
}
