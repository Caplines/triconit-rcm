import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-usersettingspopupup',
  templateUrl: './usersettingspopupup.component.html',
  styleUrls: ['./usersettingspopupup.css'],
  encapsulation: ViewEncapsulation.None
  })
  
export class UserSettingsPopupupComponent implements OnInit {
  
  @Input() userData:any;
  @Input() showusersettingPopup:boolean;
  @Output() emitToParent = new EventEmitter<any>();
  actionType: number=0;
  password:string="";
  passwordAgain:string="";
  enable:boolean=false;
  right:boolean=false;
  p_s:boolean=false;
  
  constructor(public applicationService: ApplicationService) {}
  
  ngOnInit() {
	  this.showusersettingPopup=true;
  }
  
  closePopup() {
	  this.emitToParent.emit({action: "showuserdata", value: false});
  }
  
  showAction(actionType:number){
	  
	  this.actionType=actionType;
	  this.p_s=false;
	  this.passwordAgain='';
	  this.password='';
	  console.log(this.userData.status);
	  if (this.userData.status=='1') {
		  this.enable=true;
	  }else{
		  this.enable=false;
	  }
	  if (this.userData.type=='1') {
		  this.right=true;
	  }else{
		  this.right=false;
	  }
	  
  }
  
  
  updateStatus(){
		  let x=2;
		  if (this.right) x=1;
			this.applicationService.updateStatus(x,this.userData.uuid,(data) => {
			    console.log(data);
			    if(data.message == "User status updated Successfully.") {
			    	this.p_s=true;
	            } else {
	                //this.errorMessage = data.message;
	            }	    
			})
  }

  resetRight(){
	  let x=0;
	  if (this.right) x=1;
		this.applicationService.resetRight(x,this.userData.uuid,(data) => {
		    console.log(data);
		    if(data.message == "User status updated Successfully") {
		    	this.p_s=true;
            } else {
                //this.errorMessage = data.message;
            }	    
		})
}

}
