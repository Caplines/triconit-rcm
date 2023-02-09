import { Component, OnInit } from '@angular/core';
import {FormBuilder,Validators} from "@angular/forms";
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';

@Component({
  selector: 'app-register-new-user',
  templateUrl: './register-new-user.component.html',
  styleUrls: ['./register-new-user.component.css']
})
export class RegisterNewUserComponent implements OnInit {

  userDetails:any;
  teamData:any=[];
  officeData:any=[];
  defaulUserRoleData:any=[];
  companyData:any=[];
  userRoles:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':''}
  showLoader:boolean=false;
  isRegister:boolean=true;
  userRoleByTeam:any=[];
  constructor(private fb : FormBuilder, private appService: ApplicationServiceService,private title : Title) {
    title.setTitle("Register New User");
    this.userDetails = this.fb.group({
      'firstName' : ['',[Validators.required,Validators.minLength(3)]],
      'lastName' : ['',[Validators.required,Validators.minLength(3)]],
      'email' : ['',Validators.email],
      'password' : ['',[Validators.required,Validators.minLength(6)]],
      'companyName' : ['',Validators.required],
      'teamId' : ['',Validators.required],
      'userRole' : ['',Validators.required],
    })
   }

  ngOnInit(): void {
    this.getcompanyData();
    this.userDetails.reset();
  }

  registerNewUser(){
    this.appService.registerUser(this.userDetails.value,(callback:any)=>{
      if(callback.status == 200){
        console.log(callback)
        this.alert.showAlertPopup = true;
        this.alert.alertMsg = callback.message;
        this.companyData = this.userRoles = this.defaulUserRoleData = this.officeData = this.teamData = this.userRoleByTeam = [];
        this.userDetails.reset();
        this.getcompanyData();
      } else if(callback.status == 400) { 
          this.alert.showAlertPopup = true;
          this.alert.alertMsg = callback.message;
      }
    })
    }
  
  getTeamsData(event:any){
    this.showLoader=true;
    this.appService.fetchTeamsNameData(event.target.value,(callback:any)=>{
      if(callback.status){
        this.showLoader = false;
        this.teamData = callback.data;
        this.userRoleByTeam=this.userRoles=[];
      }
    })
  }

  getOfficeData(){
    this.showLoader=true;
    this.appService.fetchOfficeData((callback:any)=>{
      if(callback.status){
        this.showLoader = false;
        this.officeData = callback.data
      }
    })
  }

  getcompanyData(){
    this.appService.fetchCompanyNameData((callback:any)=>{
      if(callback.status){
        this.companyData = callback.data.data;
      }
    })
  }

  selectUserRole(event:any){
    let alreadyExist = this.userRoles.some((e:any)=> e == event.target.id);
    if(!alreadyExist){
      this.userRoles.push(event.target.id)
      if(event.target.id === "TL" && (!this.userRoles.includes("ASSO"))){
        this.userRoles.push("ASSO");
      }
    } else{
      this.userRoles.find((e:any,index:any)=>{
            if(e == event.target.id){
              this.userRoles.splice(index,1)
              if(e === "TL"){
               let k =  this.userRoles.indexOf("ASSO")
               k !== -1 ? this.userRoles.splice(k,1) : '';
              }
            }
          })
    }
      this.userDetails.controls.userRole.setValue(this.userRoles);
      this.changeTeamMandatoryStatus(event.target.id);
      console.log(this.userRoles)
  }

  
  changeTeamMandatoryStatus(role: any) {
    let k = this.defaulUserRoleData;
    k.find((e: any) => {
      if (e.roleId === role) {
        if (e.teamMandatory && (this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
          this.userDetails.controls.teamId.setValidators([Validators.required]);
          this.userDetails.controls.teamId.updateValueAndValidity();
        } else if (!(this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
          this.userDetails.controls.teamId.setValidators();
          this.userDetails.controls.teamId.updateValueAndValidity();
        } else {
          this.userDetails.controls.teamId.setValidators([Validators.required]);
          this.userDetails.controls.teamId.updateValueAndValidity();
        }

      }
    })

  }

  selectCompany(e:any){
    this.userDetails.controls.companyName.setValue(e.target.value);
  }

  // selectOffice(e:any){
  //   this.userDetails.controls.officeId.setValue(e.target.value);
  // }

  selectTeamName(e:any){
    this.userDetails.controls.teamId.setValue(e.target.value);
  }

  // getOfficesByCompany(event:any){
  //     this.companyData.find((e:any)=>{
  //       if(e.name === event.target.value){
  //         this.appService.fetchOfficeByCompany(e.companyUuid,(callback:any)=>{
  //             if(callback.status){
  //               // this.userDetails.controls.officeId.setValue('');
  //               this.userDetails.controls.teamId.setValue('');
  //               this.officeData = callback.data.data;
  //               this.getTeamsData(event.target.value);
  //               this.userRoles= [];
  //               this.userRoleByTeam=[];
  //             }
  //         })
  //       }
  //     })
  // }

  getRolesByCompany(event:any){
    this.appService.fetchRolesByCompany(event.target.value,(callback:any)=>{
      if(callback.status){
        this.userRoles =[];
        this.userDetails.controls.userRole.setValue('');
        this.defaulUserRoleData = callback.data;
      }
    })
  }

  getRolesByTeam(event:any){
    if(event.target.value != ''){
      this.appService.fetchRolesByTeam(event.target.value,(callback:any)=>{
        if(callback.status){
          this.userRoleByTeam = callback.data;
          if(this.userRoles.includes("ADMIN")){
          this.userRoles=[];
          this.userRoles.push("ADMIN")
        } else{
          this.userRoles=[];
        }
      }
    })
  }
  }

}