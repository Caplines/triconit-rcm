import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import Utils from '../util/utils';

@Component({
  selector: 'app-users-status',
  templateUrl: "../users-status/users-status.component.html",
  styleUrls: ["../users-status/users-status.component.scss"]
})
export class UserStatusComponent implements OnInit {

  allUser: any = [];
  userRole: any;
  userName:any;
  pageNumber:number = 0;
  hasNext:boolean=false;
  userStatusArray:any={'userActiveStatus':[]}
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};
  companyName:any='';
  companyData:any=[];
  showLoader:boolean=false;
  isFindUserBtnDisable:boolean=false;
  companyUuid:any='';
  roleData:any=[];

  constructor(public appService: ApplicationServiceService, private title: Title) { 
    title.setTitle("User-Status");

  }

  ngOnInit(): void {
    this.userRole = localStorage.getItem("roles");
    this.userName = localStorage.getItem("currentUser")
    this.getcompanyData();
    this.appService.setPaddingRightContainer();
    this.getRoles();
  }
  logout() {
    localStorage.clear();
    window.location.href = "/"
  }

  getRoles(){
    this.appService.fetchRoles((res:any)=>{
      if(res.status){
        this.roleData = res.data;
        console.log(this.roleData);
        
      }
    })
  }

  findAllUser(pageNumber:any) {
    this.hasNext=false;
   this.companyData.find((ele:any) =>  {
    if(this.companyName == ele.name)
      this.companyUuid = ele.companyUuid;
    else if(this.companyName == 'All')
      this.companyUuid = 'All';
    });
    this.appService.fetchAllUser(pageNumber,this.companyUuid ,(callback: any) => {
      if (callback.status == 200 && callback.data) {
        if(this.pageNumber== -1){
          this.allUser = callback.data;
        }
        if(callback.data[0].hasNextElement){
          this.pageNumber = this.pageNumber+1;
        }
        if(callback.data[0].data){
          this.allUser.push.apply(this.allUser,callback.data[0].data)
        }
        this.hasNext = callback.data[0].hasNextElement;
        this.isFindUserBtnDisable=true;
      } else if(!callback.data){
        this.allUser=[];
        this.isFindUserBtnDisable=true;
        this.alert.showAlertPopup = true;
        setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
        this.alert.isError=true;
        this.alert.alertMsg = "No Data Found";
      }
    })
  }

  updateAllUserStatus(){
      this.appService.updateUserStatus(this.userStatusArray, (callback: any) => {
        if (callback.status == 200) {
          this.showAlertPopup(callback);
          this.allUser=[];
          this.userStatusArray.userActiveStatus=[];
          this.pageNumber=0;
          this.getcompanyData();
          this.companyName='';
          this.isFindUserBtnDisable=false;
        } else {
          this.showAlertPopup(callback);
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
  
  selectCompany(event:any){
    this.companyName = event.target.value;
    this.allUser=[];
    this.isFindUserBtnDisable=false;
    this.pageNumber= 0;
}

getcompanyData(){
  this.appService.fetchCompanyNameData((callback:any)=>{
    if(callback.status){
      this.companyData = callback.data;
      console.log(callback)
    }
  })
}

loadMoreData(){
  if(this.hasNext){
    this.findAllUser(this.pageNumber)
  }
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
 
 isSuperAdmin(){
  return Utils.checkSuperAdmin();
 }
 
 commaSeperatedRoles(role:any){
  let k:any = [];
  role.split(",").forEach((role:any) => {
    const word = role.replace("ROLE_", "");
    k.push(word);
  });
        k = this.roleData.filter((role:any) => k.includes(role.roleId)).map((matchedRole:any) => matchedRole.roleName);
        k = k.join(", ");
        return k;
    }

}
