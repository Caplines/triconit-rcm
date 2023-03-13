import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';

@Component({
  selector: 'app-update-pass',
  templateUrl: './update-pass.component.html',
  styleUrls: ['./update-pass.component.scss']
})
export class UpdatePasswordComponent implements OnInit {

  user: any = { 'changedPassword': '', 'uuid': '' ,'confirmPass':''};
  userRole: any;
  userName:any;
  pageNumber:number = 0;
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};

  constructor(public appService: ApplicationServiceService, private title: Title) { 
    title.setTitle("Update-Password");
  }

  ngOnInit(): void {
    this.userRole = localStorage.getItem("roles");
    this.userName = localStorage.getItem("currentUser")
    this.appService.setPaddingRightContainer();
  }
  logout() {
    localStorage.clear();
    window.location.href = "/"
  }

  changePassword() {
    if(this.user.changedPassword === this.user.confirmPass && this.user.changedPassword!='' && this.user.confirmPass!=''){
      this.appService.updatepassword({ "uuid": this.user.uuid, "password": this.user['changedPassword'] }, (callback: any) => {
        if (callback.status == 200) {
        console.log(callback)
        this.showAlertPopup(callback);
        localStorage.clear();
        window.location.href= "/"
      } else if(callback.status==400){
        this.showAlertPopup(callback);
      }
    })
  }else { 
    this.alert.showAlertPopup = true;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
    this.alert.isError=true;
    this.alert.alertMsg = "Please Check Field Again !";
  }
  }

  showAlertPopup(res:any){
    this.alert.showAlertPopup = true;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
    res.status==400 ? this.alert.isError=true : this.alert.isError=false;
    this.alert.alertMsg = res.message ? res.message : res.result.message;
  }
}
