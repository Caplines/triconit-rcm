import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { ApplicationServiceService } from '../service/application-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import Utils from '../util/utils';

@Component({
  selector: 'app-issue-claims',
  templateUrl: './issue-claims.component.html',
  styleUrls: ['./issue-claims.component.scss']
})
export class IssueClaimComponent {
  currentTeamId:any;
  issueClaimsCount: number = 0;
  issueClientName:any='';
  issueCl : any=[];
  userInfo: any = { 'currentClientName': '', 'currentTeamId': '' }
  issueClaimPageNum:any=0;
  totalPages:number;
  clientUuid:string="-1";

  @ViewChild('modalElement')modalElementRef!:ElementRef;
  
  constructor(private appSer: ApplicationServiceService,private router:Router,private title:Title) {
    title.setTitle(Utils.defaultTitle + "Issue Claims")
  }

  ngOnInit() {
    this.userInfo.currentClientName = localStorage.getItem("selected_clientName");
    this.issueClaim();
    this.fetchIssueClaims();
  }

  issueClaim(){
    this.appSer.fetchIssueClaimCounts((res:any)=>{
      if(res.status==200){
         this.issueClaimsCount = res.data;
      }
      else{
        //ERROR
      }
    });
  }

  fetchIssueClaims(){
    let cName = JSON.parse(localStorage.getItem('clients'));
    cName.find((ele:any)=>{
      if(ele.name == this.userInfo.currentClientName){
        this.clientUuid = ele.id;
      }
    });
    if(this.clientUuid){
      this.appSer.fetchIssueClaims(this.clientUuid+`/${this.issueClaimPageNum}`, (res: any) => {
       if (res.status === 200 && res.data) {
          this.totalPages = res.data[0].totalPages;
         if (this.issueClaimPageNum == 0) {
           this.issueCl = res.data[0].data;
         }
         if (this.issueClaimPageNum != 0 && res.data[0].totalPages != this.issueClaimPageNum) {
           this.issueCl.push.apply(this.issueCl, res.data[0].data);
         }
          //this.modal();
        }
      });
    }
  }

  loadMore(){
    ++this.issueClaimPageNum;
    if(this.issueClaimPageNum < this.totalPages){
      this.fetchIssueClaims();
    }
}

  onModalScroll(){
    const modalElement = this.modalElementRef.nativeElement;
    const scrollTop = modalElement.scrollTop;
    const scrollHeight = modalElement.scrollHeight;
    const clientHeight = modalElement.clientHeight;
    if (scrollTop + clientHeight >= scrollHeight) {
      this.loadMore();
    }
  }

  goToClaimDetailPage() {
    window.location.href = "/tool-update";
    window.close();
  }
}
