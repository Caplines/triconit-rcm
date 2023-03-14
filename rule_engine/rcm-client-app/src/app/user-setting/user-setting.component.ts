import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import Utils from '../util/utils';

@Component({
  selector: 'app-user-setting',
  templateUrl: './user-setting.component.html',
  styleUrls: ['./user-setting.component.scss']
})
export class UserSettingComponent implements OnInit {

  user: any = { 'email': '', 'showChangePassword': false, 'showStatus': false, 'changedPassword': '', 'uuid': '','showRole':false };
  allUser: any = [];
  showActionPopup: boolean = false;
  userRole: any;
  userName:any;
  userStatusArray:any={'userActiveStatus':[]}
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};
  filteredUserRole:any=[];
  selectedRoles:any = [];

  constructor(public appService: ApplicationServiceService, private title: Title) { 
    title.setTitle("User-Setting");
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

  findUser() {
    if(this.user.email !== ''){
      this.appService.findUser({ "email": this.user.email }, (callback: any) => {
        if (callback.status == 200 && callback.data) {
        this.showAlertPopup(callback);
        this.showActionPopup = true;
        this.user = callback.data;
        console.log(this.user)
        for(let i =0;i<this.user.roles.length;i++){
          this.user.roles[i] = this.user.roles[i].split("_")[2];
        }
        this.selectedRoles  = [...this.user.roles];
        
      } else {
        this.showActionPopup = false;
        this.showAlertPopup(callback);
      }
    })
  }else{
    this.alert.showAlertPopup = true;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
    this.alert.isError=true;
    this.alert.alertMsg = "Field Cannot Be Empty";
  }
}

  changePassword() {
    this.appService.changePassword({ "uuid": this.user.uuid, "password": this.user['changedPassword'] }, (callback: any) => {
      if (callback.status == 200) {
        console.log(callback)
        this.showAlertPopup(callback);
        localStorage.clear();
        window.location.href= "/"
      }
    })
  }

    userAction(event: any) {
        if (event.target.value === 'changePass') {
            this.user['showChangePassword'] = true;
            this.user['showStatus'] = false;
        } else if (event.target.value === 'status') {
            this.user['showStatus'] = true;
            this.user['showChangePassword'] = false;
          } else if(event.target.value === 'role'){
            this.user['showStatus'] = false;
            this.user['showChangePassword'] = false;
            this.user['showRole'] =true;
          }
    }

  updateSingleUserStatus(status:any){
    this.userStatusArray.userActiveStatus.push({'userId':this.user.uuid,'status':status})
    console.log(this.userStatusArray)
    this.appService.updateUserStatus(this.userStatusArray, (callback: any) => {
      if (callback.status == 200) {
        this.userStatusArray.userActiveStatus=[];
        this.showAlertPopup(callback);
        this.user={};
        this.showActionPopup=false;

      } else {
        console.log(callback)
      }
    })
  }

  changeUserStatus(userId:any,status:any){
    if (this.userStatusArray.userActiveStatus.length != 0) {
      let uuidFoundStatus = this.userStatusArray.userActiveStatus.some((el: any) => el.userId === userId ? el.status = status : '');
      if (!uuidFoundStatus) {
        let uuidAlreadyExistStatus = this.userStatusArray.userActiveStatus.some((el: any) => el.userId === userId)
        if (!uuidAlreadyExistStatus) {
          this.userStatusArray.userActiveStatus.push({ 'userId': userId, 'status': status });
        }
      }
    } else {
      this.userStatusArray.userActiveStatus.push({ 'userId': userId, 'status': status });
    }
  }

  onCheckboxChange(event:any, role: string) {
    if (event.target.checked) {
      if (!this.selectedRoles.includes(role)) {
        this.selectedRoles.push(role);
      }
    } else {
      const index = this.selectedRoles.indexOf(role);
      if (index !== -1) {
        this.selectedRoles.splice(index, 1);
      }
    }
    console.log(this.selectedRoles)
  }

  editRole(){
    let params:object= {
      'uuid':this.user.uuid,
      'roles':this.selectedRoles
    }
    this.appService.isClaimStatusActive((res:any)=>{
      if(res.status && res.data.status == 1){
        console.log(res)
        this.appService.editRole(params,(res:any)=>{
          if(res.status){
            console.log(res)
            // this.showAlertPopup(res)
          }
        })
      }
    })
    
  }

  showAlertPopup(res:any){
    this.alert.showAlertPopup = true;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
    res.status==400 ? this.alert.isError=true : this.alert.isError=false;
    this.alert.alertMsg = res.message ? res.message : res.result.message;
    scrollTo(0,0);
  }

  isAdmin(){
   return Utils.checkAdmin();
  }
}
