import { Component, OnInit } from '@angular/core';
import { BaseService } from '../service/base-service.service';

@Component({
  selector: 'app-user-setting',
  templateUrl: './user-setting.component.html',
  styleUrls: ['./user-setting.component.scss']
})
export class UserSettingComponent implements OnInit {

  user: any = { 'email': '', 'showChangePassword': false, 'showStatus': false, 'changedPassword': '', 'uuid': '' };
  allUser: any = [];
  showActionPopup: boolean = false;
  userRole: any;
  userName:any;
  pageNumber:number = 0;
  hasNext:boolean=false;
  userStatusArray:any={'userActiveStatus':[]}
  alert:any={'showAlertPopup':false,'alertMsg':''}

  constructor(private _baseService: BaseService) { }

  ngOnInit(): void {
    this.userRole = localStorage.getItem("roles");
    this.userName = localStorage.getItem("currentUser")
  }
  logout() {
    localStorage.clear();
    window.location.href = "/"
  }

  findUser() {
    if(this.user.email !== ''){
      this._baseService.findUser({ "email": this.user.email }, (callback: any) => {
        if (callback.result.status == 200 && callback.result.data) {
        this.showActionPopup = true;
        this.user = callback.result.data;
        console.log(this.user)
        
      } else {
        this.showActionPopup = false;
        this.alert.showAlertPopup = true;
        this.alert.alertMsg = callback.result.message;
      }
    })
  }else{
    this.alert.showAlertPopup = true;
    this.alert.alertMsg = "Field Cannot Be Empty";
  }
}

  changePassword() {
    this._baseService.changePassword({ "uuid": this.user.uuid, "password": this.user['changedPassword'] }, (callback: any) => {
      if (callback.result.status == 200) {
        console.log(callback)
        this.alert.showAlertPopup = true;
        this.alert.alertMsg = callback.result.message;
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
        }
    }

  // findAllUser(pageNumber:any) {
  //   this.hasNext=false;
  //   this._baseService.findAllUser(pageNumber,(callback: any) => {
  //     if (callback.result.status == 200 && callback.result.data) {
  //       if(this.pageNumber== -1){
  //         this.allUser = callback.result.data;
  //       }
  //       if(callback.result.data[0].hasNextElement){
  //         this.pageNumber = this.pageNumber+1;
  //       }
  //       if(callback.result.data[0].data){
  //         this.allUser.push.apply(this.allUser,callback.result.data[0].data)
  //       }
  //       this.hasNext = callback.result.data[0].hasNextElement;
  //     }
  //   })
  // }

  // updateAlUserStatus(){
  //     this._baseService.updateUserStatus(this.userStatusArray, (callback: any) => {
  //       if (callback.result.status == 200) {
  //         this.alert.showAlertPopup = true;
  //         this.alert.alertMsg = callback.result.message;
  //         this.allUser=[];
  //         this.userStatusArray.userActiveStatus=[];
  //         this.pageNumber=0;
  //       } else {
  //         console.log(callback.result)
  //       }
  //     })
  // }

  updateSingleUserStatus(status:any){
    this.userStatusArray.userActiveStatus.push({'userId':this.user.uuid,'status':status})
    console.log(this.userStatusArray)
    this._baseService.updateUserStatus(this.userStatusArray, (callback: any) => {
      if (callback.result.status == 200) {
        this.userStatusArray.userActiveStatus=[];
        this.alert.showAlertPopup = true;
        this.alert.alertMsg = callback.result.message;
        this.user={};
        this.showActionPopup=false;

      } else {
        console.log(callback.result)
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
   

  // loadMoreData(){
  //   if(this.hasNext){
  //     this.findAllUser(this.pageNumber)
  //   }
  // }
}
