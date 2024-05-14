import { CommonModule } from '@angular/common';
import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../service/auth-service.service';
import { SwitchAccountModel } from '../../models/switch.account.model';
import Utils from '../../util/utils';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { AppConstants } from 'src/app/constants/app.constants';
import { FeedbackModule } from 'src/app/shared/feedback/feedback.module';

@Component({
  selector: 'app-header-component',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FeedbackModule],
  providers: [AuthService],
})
export class HeaderComponent implements OnInit {
  form: any = {
    username: null,
    password: null,
  };
  loggedInUserRole: any;
  loggedInUserName: any;
  modelTitle = "";

  showPopup: boolean = false;

  modelElement: any = { 'modal': '', 'span': '' };
  cwModel: SwitchAccountModel;
  userInfo: any = { 'currentClientName': '', 'currentRoleName': '', 'currentTeamId': '' }
  //https://www.bezkoder.com/angular-13-jwt-auth/
  selectedTeam: any = '';
  selectedClient: any = '';
  selectedRole: any = '';
  roleData: any = [];
  loginUserType: any = '';
  btnDisabled: boolean = true;
  teamData: any = [];
  currentTeamId: any;
  issueClaimsCount: number = 0;
  clientUuid: string = "-1";
  ele: any = { 'modal': '', 'span': '' }
  issueClientName: any = '';
  issueCl: any = [];
  isSingleRole: boolean = false;

  issueClaimPageNum: any = 0;
  totalPages: number;
  emailUrl: any;

  @ViewChild('modalElement') modalElementRef!: ElementRef;
  @Input() isClaimDetailPage: boolean = false;
  isLoggedInAdmin: boolean = false;

  constructor(private appSer: ApplicationServiceService, private router: Router, public appConstants: AppConstants) {

    this.cwModel = {};
  }

  ngOnInit(): void {
    // if(!this.loggedInUserRole){
    this.userInfo.currentClientName = localStorage.getItem("selected_clientName");
    this.userInfo.currentRoleName = localStorage.getItem("selected_roleName");
    this.userInfo.currentTeamId = localStorage.getItem("selected_teamId");
    this.currentTeamId = this.userInfo.currentTeamId;
    this.loginUserType = localStorage.getItem("loginAs");
    //  }
    this.loggedInUserName = localStorage.getItem("name");
    this.isLoggedInAdmin = Utils.checkAdminLoginRole();
    if (this.roleData.length == 0) {
      this.getRoles();
    }
      
      if(Utils.getRolesFromLS()[0] === 'ROLE_ADMIN' && !this.userInfo.currentRoleName){
          this.selectedClient= 'Smilepoint';
          this.selectedRole = 'Admin';
          this.btnDisabled = false;

          // this.checkBtnDisabled();
          setTimeout(() => {
            this.switchAccount();
          }, 1000);
    }

    // if(this.userInfo.currentClientName && this.userInfo.currentTeamId != "-1"){
    //   this.issueClaim();
    // }
    this.checkClientExist();
    window.addEventListener("click", (event: any) => {
      if (!event.target.matches('.dropbtn')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
          var openDropdown = dropdowns[i];
          if (openDropdown.classList.contains('show')) {
            openDropdown.classList.remove('show');
          }
        }
      }
    })
    this.emailUrl = window.location.href;
  }

  getRoles() {
    this.appSer.fetchRoles((res: any) => {
      if (res.status) {
        this.roleData = res.data;
        this.openPopUp(false);
        this.setRoleName();
        this.setTeamName();
      }
    })
  }

  setRoleName() {
    this.userInfo.currentRoleName = this.roleData.find((e: any) => {                  //Returns Role Name from Curernt Role ID.
      if (e.roleId == this.userInfo.currentRoleName) {
        this.userInfo.currentRoleName = e.roleName;
        return e.roleName;
      }
    })
  }

  setTeamName() {
    this.userInfo.currentTeamId = this.appConstants.teamData.find((e: any) => {
      if (e.teamId == this.userInfo.currentTeamId) {
        this.userInfo.currentTeamId = e.teamName;
        return e.teamName;
      }
    })
  }

  logout() {
    Utils.logout();
  }

  get staticUtil() {
    return Utils;
  }

  closeModal() {
    this.loginUserType = localStorage.getItem('loginAs');
    this.modelElement.modal.style.display = "none";
    this.showPopup = false;
    let adminRadioBox = (<HTMLInputElement>document.getElementById('admin'));
    adminRadioBox ? adminRadioBox.checked = false : '';
    let normalRadioBox = (<HTMLInputElement>document.getElementById('normal'));
    normalRadioBox ? normalRadioBox.checked = false : '';
  }

  switchAccount() {

    localStorage.setItem("loginAs", this.loginUserType);
    Utils.clearLastPageVisited();
    if (this.cwModel.roles != undefined && this.cwModel.roles.length != 1 && this.cwModel.companies.length != 1 && this.cwModel.teams.length != 1)
      this.modelElement.modal.style.display = "none";
    if (this.selectedTeam == '') {
      this.selectedTeam = '-1'
    }
    this.staticUtil.setLocalStoragePartial(this.selectedClient, this.selectedRole, this.selectedTeam);
    this.showPopup = false;

    if (this.selectedTeam != -1 && this.selectedRole != "ASSO") {
      window.location.href = "/claim-assignment";
    }
    else if (this.selectedRole == "ASSO") {
      window.location.href = "/list-of-claims";
    }
    else {
      window.location.href = "/register";
    }
  }

  openPopUp(open: boolean) {
    if (!open) {
      this.modelTitle = "Select Profile";

    }
    else {
      this.modelTitle = "Switch Profile";
    }
    if (open || this.staticUtil.isAccountpopupNeeded()) {
      this.cwModel.companies = this.staticUtil.getClientsFromLS();
      this.cwModel.teams = this.staticUtil.getTeamsFromLS();
      this.cwModel.roles = this.staticUtil.getRolesFromLS();

      this.sortData(this.cwModel.companies, 'name', 'asc', 'string')
      this.cwModel.roles = this.cwModel.roles.map((roleId: any) => {
        let foundRole = this.roleData.find((role: any) => role.roleId === roleId.substring(5));
        return foundRole ? { roleName: foundRole.roleName, roleId: foundRole.roleId } : null;
      }).filter((role: any) => role !== null);    //removes ROLE_ prefix from localstorage: roles and then retrived matched role name from role Data.

      if (this.cwModel.roles.length == 1) {
        this.selectedRole = this.cwModel.roles[0].roleId;
      }
      if (this.cwModel.companies.length == 1) {
        this.selectedClient = this.cwModel.companies[0].name;
      }
      if (this.cwModel.teams.length == 1) {
        this.selectedTeam = this.cwModel.teams[0].id;
      }

      if (this.cwModel.roles.length == 1 && this.cwModel.companies.length == 1 && (localStorage.getItem("roles") == "ROLE_ADMIN" || localStorage.getItem("roles") == "ROLE_REPORTING" || this.cwModel.teams.length == 1)) {
        this.isSingleRole = true;
        this.switchAccount();
      }

      if (!this.isSingleRole) {
        this.modelElement.modal = document.getElementById("switch-modal");
        this.modelElement.modal.style.display = "block";
        this.showPopup = true;
      }
      this.checkClientExist();
    }
  }

  selectLoginType(event: any) {
    this.loginUserType = event.target.value;
    if (this.loginUserType == 'Normal') {
      this.cwModel.teams = this.appConstants.teamData;
      this.sortData(this.cwModel.teams, 'teamName', 'asc', 'string')
      this.btnDisabled = true;
    } else if (this.loginUserType == 'Admin') {
      this.selectedTeam = '';
      this.selectedClient = 'Smilepoint';
      this.checkValidationSuperAdmin();
    }
  }

  checkBtnDisabled() {
    if (this.selectedRole == 'SUPER_ADMIN') {
      this.checkValidationSuperAdmin();
    }
    else if (this.selectedRole == 'ADMIN' || this.selectedRole == 'REPORTING') {
      this.checkValidationAdminReporting();
    }
    else if (this.selectedRole != 'SUPER_ADMIN' || this.selectedRole != 'ADMIN' || this.selectedRole != 'REPORTING') {
      this.checkValidationNormal();
    }
    else {
      this.btnDisabled = true;
    }
  }

  checkValidationSuperAdmin() {
    if (this.cwModel?.companies?.length > 0 && this.cwModel?.roles?.length > 0 && this.selectedClient && this.selectedRole && this.loginUserType) {
      if (this.loginUserType == "Normal" && this.selectedTeam) {
        this.btnDisabled = false;
      } else if (this.loginUserType == "Admin") {
        this.btnDisabled = false;
      } else {
        this.btnDisabled = true;
      }
    } else {
      this.btnDisabled = true;
    }

  }

  checkValidationAdminReporting() {
    if (this.cwModel?.companies?.length > 0 && this.cwModel?.roles?.length > 0 && this.selectedClient && this.selectedRole) {
      this.btnDisabled = false;
      this.loginUserType = '';
    } else {
      this.btnDisabled = true;
    }
  }

  checkValidationNormal() {

    if (this.cwModel?.companies?.length > 0 && this.cwModel?.roles?.length > 0 && this.cwModel?.teams?.length > 0 && this.selectedClient && this.selectedRole && this.selectedTeam) {
      this.btnDisabled = false;
      this.loginUserType = '';
    } else {
      this.btnDisabled = true;
    }
  }

  checkClientExist() {
    let getClient = localStorage.getItem('clients');
    if (!getClient || getClient.length == 0) {
      setInterval(() => {
        this.logout()
      }, 1000);
    }
  }

  // issueClaim(){
  //   this.appSer.fetchIssueClaimCounts((res:any)=>{
  //     if(res.status==200){
  //        this.issueClaimsCount = res.data;
  //     }
  //     else{
  //       //ERROR
  //     }
  //   });
  // }

  fetchIssueClaims() {
    let cName = JSON.parse(localStorage.getItem('clients'));
    cName.find((ele: any) => {
      if (ele.name == this.userInfo.currentClientName) {
        this.clientUuid = ele.id;
      }
    });
    if (this.clientUuid) {
      this.appSer.fetchIssueClaims(this.clientUuid + `/${this.issueClaimPageNum}`, (res: any) => {
        if (res.status === 200 && res.data) {
          this.totalPages = res.data[0].totalPages;
          if (this.issueClaimPageNum == 0) {
            this.issueCl = res.data[0].data;
          }
          if (this.issueClaimPageNum != 0 && res.data[0].totalPages != this.issueClaimPageNum) {
            this.issueCl.push.apply(this.issueCl, res.data[0].data);
          }
          this.modal();
        }
      });
    }
  }

  loadMore() {
    ++this.issueClaimPageNum;
    if (this.issueClaimPageNum < this.totalPages) {
      this.fetchIssueClaims();
    }
  }

  modal() {
    this.issueClientName = localStorage.getItem("selected_clientName");
    this.ele.modal = document.getElementById("myModal");
    this.ele.span = document.getElementsByClassName("close")[0];
    this.ele.modal.style.display = "block";
  }

  onModalScroll() {
    const modalElement = this.modalElementRef.nativeElement;
    const scrollTop = modalElement.scrollTop;
    const scrollHeight = modalElement.scrollHeight;
    const clientHeight = modalElement.clientHeight;

    if (scrollTop + clientHeight >= scrollHeight) {
      this.loadMore();
    }
  }

  closeModalIC() {
    this.ele.modal.style.display = "none";
    this.issueClaimPageNum = 0;
    this.issueCl = [];
  }


  openHelp() {
    if (window.location.pathname === "/tool-update") {   //curerntly help link is only available for Tool To Update Page.
      window.open(
        "https://docs.google.com/document/d/1VjkBGvwpUPlhQG0JAprO8moTleo0cEbFPCmaYIYa2CM/edit#heading=h.lfah6ew7mnj1",
        "_blank");
    }
  }

  /* When the user clicks on the button, 
  toggle between hiding and showing the dropdown content */
  myFunction() {
    document.getElementById("myDropdown").classList.toggle("show");
  }

  // Close the dropdown if the user clicks outside of it

  sortData(data: any, sortProp: string, order: any, sortType: string) {
    this.appSer.sortData(data, sortProp, order, sortType);
  }

  goToListofClaimsPage() {
    window.location.href = "/list-of-claims";
  }
}
