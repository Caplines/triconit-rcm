import { Component, OnInit } from '@angular/core';
import { Validators, FormBuilder } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import Utils from '../util/utils';

@Component({
  selector: 'app-user-setting',
  templateUrl: './user-setting.component.html',
  styleUrls: ['./user-setting.component.scss']
})
export class UserSettingComponent implements OnInit {

  user: any = { 'email': '', 'showChangePassword': false, 'showStatus': false, 'changedPassword': '', 'uuid': '', 'showEditUser': false };
  showActionPopup: boolean = false;
  userRole: any;
  userName: any;
  userStatusArray: any = { 'userActiveStatus': [] }
  alert: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  userRolesByEmail: any = [];

  editedUserDetails: any;
  teamData: any = [];
  companyData: any = [];
  userRoleData: any = [];
  showLoader: boolean = false;
  activeUserClients: any = [];
  teamId: any = [];
  clientId: any = [];
  filteredUsers: any = [];
  selectedFromDropDown: boolean = false;
  editRoless:string='';
  manageSectionConfig:any={};

  constructor(public appService: ApplicationServiceService, private title: Title, private fb: FormBuilder) {
    title.setTitle(Utils.defaultTitle + "User Setting");
    this.editedUserDetails = this.fb.group({
      'firstName': ['', [Validators.required, Validators.minLength(3), Validators.maxLength(25), Validators.pattern("[a-zA-Z]*")]],
      'lastName': ['', [Validators.minLength(3), Validators.maxLength(25), Validators.pattern("[a-zA-Z]*")]],
      'companyUuid': ['', Validators.required],
      'teamId': ['', Validators.required],
      'role': ['', Validators.required],
    })

  }

  ngOnInit(): void {
    this.userRole = localStorage.getItem("roles");
    this.userName = localStorage.getItem("currentUser")
    this.appService.setPaddingRightContainer();
    this.activeUserClients = this.appService.getActiveClients();
    this.getRoles();
    this.getTeamsData();
    scrollTo(0, 0);
  }
  logout() {
    localStorage.clear();
    window.location.href = "/"
  }


  findUser() {
    this.hideBox();
    this.filteredUsers = [];
    this.selectedFromDropDown = true;
    if (this.user.email !== '') {
      this.appService.findUser({ "email": this.user.email }, (callback: any) => {
        if (callback.status == 200 && callback.data) {
          this.showAlertPopup(callback);
          this.showActionPopup ? this.showActionBox() : this.showActionPopup = true;
          this.user = callback.data;
          this.editedUserDetails.controls.firstName.setValue(this.user.firstName);
          this.editedUserDetails.controls.lastName.setValue(this.user.lastName);
          this.editedUserDetails.controls.role.setValue(this.user.roles.roleId)
          this.selectUserRole({'target':{'value':this.user.roles.roleId}});    //method is called to update validators as per the Role ID.
          console.log(this.editedUserDetails.controls);
        } else {
          this.showActionPopup = false;
          this.showAlertPopup(callback);
        }
      })
    } else {
      this.alert.showAlertPopup = true;
      setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
      this.alert.isError = true;
      this.alert.alertMsg = "Field Cannot Be Empty";
    }
  }

  showActionBox() {
    let select: any = document.getElementById("actionSelectBox");
    select.selectedIndex = 0;
    this.showActionPopup = true;
  }

  changePassword() {
    this.appService.changePassword({ "uuid": this.user.uuid, "password": this.user['changedPassword'] }, (callback: any) => {
      if (callback.status == 200) {
        console.log(callback)
        this.showAlertPopup(callback);
        localStorage.clear();
        window.location.href = "/"
      }
    })
  }

  userAction(event: any) {
    if (event.target.value === 'changePass') {
      this.user['showChangePassword'] = true;
      this.user['showStatus'] = false;
      this.user['showEditUser'] = false;
      this.user['showEditSection'] = false;
    } else if (event.target.value === 'status') {
      this.user['showStatus'] = true;
      this.user['showChangePassword'] = false;
      this.user['showEditUser'] = false;
      this.user['showEditSection'] = false;
    } else if (event.target.value === 'editUser') {
      this.user['showStatus'] = false;
      this.user['showChangePassword'] = false;
      this.user['showEditUser'] = true;
      this.user['showEditSection'] = false;
      // this.getUserRolesByEmail();
    } else if(event.target.value === 'editSec'){
      this.user['showEditSection'] = true;
      this.user['showChangePassword'] = false;
      this.user['showStatus'] = false;
      this.user['showEditUser'] = false;
      this.getManageSectionDetails();
    }
  }

  hideBox() {
    this.showActionPopup = false;
    this.user['showStatus'] = false;
    this.user['showChangePassword'] = false;
    this.user['showEditUser'] = false;

  }

  updateSingleUserStatus(status: any) {
    this.userStatusArray.userActiveStatus.push({ 'userId': this.user.uuid, 'status': status })
    console.log(this.userStatusArray)
    this.appService.updateUserStatus(this.userStatusArray, (callback: any) => {
      if (callback.status == 200) {
        this.userStatusArray.userActiveStatus = [];
        this.showAlertPopup(callback);
        this.user = {};
        this.showActionPopup = false;

      } else {
        console.log(callback)
      }
    })
  }

  changeUserStatus(userId: any, status: any) {
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

  editRole() {
    this.editedUserDetails.value = Object.assign(this.editedUserDetails.value, { 'uuid': this.user.uuid });
    // console.log(this.editedUserDetails.value)
    // this.appService.isClaimStatusActive(this.user.uuid, (res: any) => {
    //   if (res.status == 200 && res.data.status == 1) {
    //     console.log(res);
        this.appService.editUser(this.editedUserDetails.value, (res: any) => {
          if (res.status == 200) {
            console.log(res);
            this.showAlertPopup(res);
            this.user['showEditUser'] = false;
            location.reload();
          } else {
            this.showAlertPopup(res);
          }
        })
  }

  showAlertPopup(res: any) {
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
    res.status == 400 ? this.alert.isError = true : this.alert.isError = false;
    this.alert.alertMsg = res.message ? res.message : res.data.msg;
    scrollTo(0, 0);
  }
  getUserRolesByEmail() {
    this.appService.fetchRolesByEmail(this.user.email, (res: any) => {
      if (res.status) {
        console.log(res)
        this.userRolesByEmail = res.data;
      }
    })
  }

  getTeamsData() {
    this.showLoader = true;
    this.appService.fetchTeamsNameData((callback: any) => {
      if (callback.status) {
        this.showLoader = false;
        this.teamData = callback.data;
      }
    })
  }

  isAdmin() {
    return Utils.checkAdmin();
  }

  isSuperAdmin() {
    return Utils.checkRoleSuperAdmin();
  }

  getRoles() {
    this.appService.fetchRoles((callback: any) => {
      if (callback.status) {
        this.userRoleData = callback.data;
      }
    })
  }

  selectUserRole(event: any) {
    if (event.target.value == "ADMIN" || event.target.value == "REPORTING") {
      this.editedUserDetails.controls.teamId.clearValidators();
      this.editedUserDetails.controls.teamId.updateValueAndValidity();
      this.editedUserDetails.controls.teamId.setValue([]);
      this.editedUserDetails.controls.role.setValue(event.target.value);
    }
    else if (event.target.value == "SUPER_ADMIN") {
      this.editedUserDetails.controls.teamId.clearValidators();
      this.editedUserDetails.controls.teamId.updateValueAndValidity();
      this.editedUserDetails.controls.teamId.setValue([]);
      this.editedUserDetails.controls.companyUuid.clearValidators();
      this.editedUserDetails.controls.companyUuid.updateValueAndValidity();
      this.editedUserDetails.controls.companyUuid.setValue([]);
      this.editedUserDetails.controls.role.setValue(event.target.value);
    } else {

      this.editedUserDetails.controls.teamId.setValidators(Validators.required);
      this.editedUserDetails.controls.companyUuid.setValidators(Validators.required);
      this.editedUserDetails.controls.companyUuid.updateValueAndValidity();
      this.editedUserDetails.controls.teamId.updateValueAndValidity();

      this.editedUserDetails.controls.role.setValue(event.target.value);
      this.editedUserDetails.controls.teamId.setValue(this.teamId);
      this.editedUserDetails.controls.companyUuid.setValue(this.clientId);
    }
    console.log(this.editedUserDetails.value);
  }

  shareCheckedList(event: any) {
    if (event.action == 'team') {
      this.teamId = event.value.map(({ teamName, checked, ...newData }: any) => newData);
      this.teamId = [].concat(...this.teamId.map((team: any) => team.teamId));
      this.editedUserDetails.controls.teamId.setValue(this.teamId);
    }
    else if (event.action == 'client') {
      this.clientId = event.value.map(({ checked, name, ...newData }: any) => newData);
      this.clientId = [].concat(...this.clientId.map((client: any) => client.id));
      this.editedUserDetails.controls.companyUuid.setValue(this.clientId);
    }
  }

  searchUserByName(name: any) {
    if (name.length >= 3 && name) {
      this.appService.fetchUserByDetail(name, (res: any) => {
        if (res.status) {
          this.selectedFromDropDown = false;
          this.filteredUsers = res.data;
        }
      })

    } else {
      this.filteredUsers = [];
    }
  }

  selectUser(email: any) {
    this.user.email = email;
    this.filteredUsers = [];
    this.selectedFromDropDown = true;
    this.hideBox();
    this.findUser();
  }

  ngOnDestroy() {
    console.log("destri");

  }

  editUserPersonalInfo() {
    const firstName = this.editedUserDetails.controls['firstName'].value;
    const lastName = this.editedUserDetails.controls['lastName'].value;
    const uuid = this.user.uuid;
    if(firstName.length!=0){
    this.appService.editUserInfo({ "uuid": uuid, "firstName": firstName, "lastName": lastName }, (ress: any) => {
      if (ress.status == 200 ||ress.status==400) {
        this.alert.showAlertPopup = true;
        ress.status == 400 ? this.alert.isError = true : this.alert.isError = false;
        setTimeout(() => { this.alert.showAlertPopup = false; }, 6000);
        this.alert.alertMsg = ress.message ? ress.message : ress.data;
        scrollTo(0, 0);
      }
    })
  }else{
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
    this.alert.isError = true ;
    this.alert.alertMsg = 'Resource is empty';
    scrollTo(0, 0);
  }
  }

  editRoles() {
    const role = this.editedUserDetails.controls['role'].value;
    const uuid = this.user.uuid;
    this.appService.editUserRole({ "uuid": uuid, "role": role }, (ress: any) => {
      if (ress.status == 200) {
        if(ress.data.status==true){
          this.editRoless=ress.data.roleName;
        }      
        this.alert.showAlertPopup = true;
        setTimeout(() => { this.alert.showAlertPopup = false; }, 6000);
        ress.status == 400 ? this.alert.isError = true : this.alert.isError = false;
        this.alert.alertMsg = ress.message ? ress.message: ress.data.message;
        scrollTo(0, 0);
      }
      if(ress.status==400){
        this.alert.showAlertPopup = true;
        setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
        this.alert.isError = true;
        this.alert.alertMsg = ress.message ? ress.message: ress.data;
        scrollTo(0, 0);
      }
    })
  }

  editClient() {
    const clientUuid = this.editedUserDetails.controls['companyUuid'].value;
    if(clientUuid.length!=0){
    const uuid = this.user.uuid;
    let role="";
    if(this.editRoless=="" ||this.editRoless==null){
       role=this.user.roles.roleId;
    }else{
      role= this.editRoless;
    }
   // 
    this.appService.editUserClient({ "uuid": uuid,"role": role,"companyUuid":clientUuid }, (ress: any) => {
      if (ress.status == 200 || ress.status==400) {
        this.alert.showAlertPopup = true;
        setTimeout(() => { this.alert.showAlertPopup = false; }, 6000);
        ress.status == 400 ? this.alert.isError = true : this.alert.isError = false;
        this.alert.alertMsg = ress.message ? ress.message: ress.data;
        scrollTo(0, 0);
      }
    })
  }
  else{
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
    this.alert.isError = true ;
    this.alert.alertMsg = 'Please select client';
    scrollTo(0, 0);
  }
}

  editTeam() {
    const teamId = this.editedUserDetails.controls['teamId'].value;
    if(teamId.length!=0){
    const uuid = this.user.uuid;
    let role="";
    if(this.editRoless=="" || this.editRoless==null){
       role=this.user.roles.roleId;
    }else{
      role= this.editRoless;
    }
    this.appService.editUserTeam({ "uuid": uuid,"role": role,"teamId":teamId }, (ress: any) => {
      if (ress.status == 200 || ress.status==400) {
        this.alert.showAlertPopup = true;
        setTimeout(() => { this.alert.showAlertPopup = false; }, 6000);
        ress.status == 400 ? this.alert.isError = true : this.alert.isError = false;
        this.alert.alertMsg = ress.message ? ress.message: ress.data;
        scrollTo(0, 0);
      }
    })
  }else{
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
    this.alert.isError = true ;
    this.alert.alertMsg = 'Please select team';
    scrollTo(0, 0);
  }
}

getManageSectionDetails(){
  this.manageSectionConfig['uuid'] = this.user.uuid;
  this.manageSectionConfig['isEditSection'] = true;
}

}
