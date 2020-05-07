import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-resetpassword',
  templateUrl: './resetpassword.component.html',
  styleUrls: ['./resetpassword.css'],
  encapsulation: ViewEncapsulation.None
  })
  
export class ResetPasswordComponent implements OnInit {
  
  @Input() uuid:string;
  @Output() emitToParent = new EventEmitter<any>();
  password:string="";
  passwordAgain:string="";
  p_s:boolean=false;
  
  constructor(public applicationService: ApplicationService) {}
  
  ngOnInit() {
  }
  
  closePopup() {
	  this.emitToParent.emit({action: "showuserdata", value: false});
  }
  
  showAction(actionType:number){
	  
	  this.p_s=false;
	  this.passwordAgain='';
	  this.password='';
	  
  }
  
  updatePassword(){
	  if(this.password==this.passwordAgain) {
			this.applicationService.updatepassword(this.password,this.uuid,(data) => {
			    if(data.message == "User password updated Successfully") {
			    	this.p_s=true;
	            } else {
	               // this.errorMessage = data.message;
	            }	    
			}
			)//
		  }

  }


}
