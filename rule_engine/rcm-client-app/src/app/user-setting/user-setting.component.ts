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
  userRolesByEmail:any=[];

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
        this.user.roles =  this.removeAdminAndUploadClaimsRole(this.user.roles)
        this.selectedRoles  = [...this.user.roles];
        this.addClaimKeywordPrefix();
        console.log(this.selectedRoles);

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
            this.getUserRolesByEmail()
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
        if(role == 'TL' && !this.selectedRoles.includes("ASSO")){
          this.selectedRoles.push(role);
          this.selectedRoles.push("ASSO");
        }else{
          this.selectedRoles.push(role);
        }
      }
    } else {
      const index = this.selectedRoles.indexOf(role);
      if (index !== -1) {
        if(role == "TL"){
          this.selectedRoles.splice(index, 1);
          let idxAsso = this.selectedRoles.indexOf("ASSO");
          idxAsso != -1 ? this.selectedRoles.splice(idxAsso,1) : '';
        }else{
          this.selectedRoles.splice(index, 1);
        }
      }
    }
    console.log(this.selectedRoles)
  }

  editRole(){
    if (this.selectedRoles.includes("CLAIMS")){
      const index = this.selectedRoles.indexOf("CLAIMS");
      this.selectedRoles.splice(index, 1);
      this.selectedRoles.push("UPLOAD_CLAIMS");
    }
    let params:object= {
      'uuid':this.user.uuid,
      'roles':this.selectedRoles
    }
    this.appService.isClaimStatusActive(this.user.uuid,(res:any)=>{
      if(res.status && res.data.status == 1){
        console.log(res)
        this.appService.editRole(params,(res:any)=>{
          if(res.status){
            console.log(res)
            this.showAlertPopup(res)
            this.showActionPopup=false;
            this.user['showRole'] =false;
          }
        })
      }else { 
        this.alert.showAlertPopup = true;
        this.alert.isError = true;
        this.alert.alertMsg = res.data.msg;
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

  removeAdminAndUploadClaimsRole(role:any){
    let idxAdmin = role.indexOf("ADMIN")
    let idxClaims = role.indexOf('CLAIMS');
    if(idxAdmin != -1){
      this.user.roles.splice(idxAdmin,1)
    }
    if(idxClaims != -1 && idxAdmin != -1){
      this.user.roles.splice(idxClaims,1)

    }
    if (role.length==1 && idxClaims != -1){
      this.user.roles.splice(idxClaims,1)
    }
    return this.user.roles;
  }

  getUserRolesByEmail(){
    this.appService.fetchRolesByEmail(this.user.email,(res:any)=>{
      if(res.status){
        console.log(res)
        this.userRolesByEmail  = res.data;
      }
    })
  }

  addClaimKeywordPrefix(){
    if(this.selectedRoles.includes("VIEW_ONLY") || this.selectedRoles.includes("MANAGER")){
      let idxView = this.selectedRoles.indexOf("VIEW_ONLY");
      idxView !== -1 ? this.selectedRoles[idxView] =  "CLIENT_"+this.selectedRoles[idxView] : '';
      let idxManager = this.selectedRoles.indexOf("MANAGER");
      idxManager !== -1 ?  this.selectedRoles[idxManager] = "CLIENT_"+this.selectedRoles[idxManager]: '';
      return;
    }
  }

  isAdmin(){
   return Utils.checkAdmin();
  }

  selectRole(role:any):boolean{
    console.log('Roles',role);
    console.log('SelectedRoles',this.selectedRoles);
    if  (role.roleId === 'UPLOAD_CLAIMS'){
      let r =role;
      r.roleId= "CLAIMS";
      return   r.roleId.includes(this.selectedRoles);
    }
    return this.selectedRoles.includes(role.roleId)  ;
  }
}
