import { Injectable } from '@angular/core';
import { BaseService } from './base-service.service';
import { HttpClient } from '@angular/common/http';
import { FreshClaimPullModel } from '../models/fresh.claim.pull.model';
import { ClaimAssignmentPullModel } from '../models/claim-assignment-pull-model';
import { TokenStorageService } from '../service/token-storage.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class ApplicationServiceService extends BaseService {
  setPaddingContainer: boolean = false;
  private seviceEventEmitter = new Subject<any>();
  private seviceEventEmitterReference: any = {};

  constructor(router: Router, http: HttpClient, tokenStorage: TokenStorageService) {
    super(router, http, tokenStorage);
  }

  fetchClaimData(callback: any) {

    this.getData({}, this.httpUrl['fetchclaims'], callback);

  }

  fetchLatesClaimLLogs(cName: any, callback: any) {
    this.getData({}, this.httpUrl['freshclaimlogs'] + "/" + cName, callback);
  }


  pullFreshClaims(model: FreshClaimPullModel, callback: any) {

    this.postData(model, this.httpUrl['fetchclaimsFromSource'], callback);

  }

  fetchAssociateClaimBillLogs(type: number, callback: any) {

    this.getData({}, this.httpUrl['fetchAssociateClaimLogs'] + "/" + type, callback);
  }


  fetchClaimAssignments(model: ClaimAssignmentPullModel, callback: any) {
    this.postData(model, this.httpUrl['fetchclaimsAssignmentData'], callback);
  }


  fetchAssociateClaimDet(teamId: number, subtype: string, callback: any) {
    this.getData({}, this.httpUrl['fetchAssociateClaimDet'] + "/" + teamId + "/" + subtype, callback);
  }

  fetchLeadClaimDet(teamId: number, subtype: string, callback: any) {
    this.getData({}, this.httpUrl['fetchLeadClaimDet'] + "/" + teamId + "/" + subtype, callback);
  }
  fetchCompanyNameData(callback: any) {
    this.getData({}, this.httpUrl['getCompany'], callback)
  }

  fetchTeamsNameData(callback: any) {
    this.getData({}, this.httpUrl['getTeams'], callback)
  }

  fetchOfficeData(callback: any) {
    this.getData({}, this.httpUrl['getOffices'], callback)
  }

  fetchOfficeByCompany(params: any, callback: any) {
    this.getData({}, this.httpUrl['officeByCompany'] + `/${params}`, callback)
  }

  fetchRolesByCompany(companyName: any, callback: any) {
    this.getData({}, this.httpUrl['rolesByCompanyName'] + `/${companyName}`, callback)
  }

  fetchRolesByTeam(callback: any) {
    this.getData({}, this.httpUrl['rolesByTeam'], callback)
  }

  fetchClients(callback: any) {
    this.getData({}, this.httpUrl['getClients'], callback)
  }

  getActiveClients() {
    let activeUserClients = JSON.parse(localStorage.getItem("clients"));
    return activeUserClients;
  }

  fetchRoles(callback: any) {
    this.getDataNoAuth({}, this.httpUrl['getRoles'], callback)
  }

  fetchAllUser(page: any, companyName: any, callback: any) {
    this.getData({}, this.httpUrl['getAllUsers'] + `/${companyName ? companyName : 'All'}/${page}`, callback)
  }

  fetchUserByTeamId(callback: any) {
    this.getData({}, this.httpUrl['userByTeamId'], callback)
  }

  fetchArchiveClaims(uuid: any, callback: any) {
    this.getData({}, this.httpUrl['fetchArchiveClaims'] + "/" + uuid, callback)
  }

  fetchArchiveClaimsCount(callback: any) {
    this.getData({}, this.httpUrl['fetchArchiveClaimsCount'], callback)
  }

  assignOffice(params: any, callback: any) {
    this.postData(params, this.httpUrl['assignOffice'], callback)
  }

  AssignClaimWithRemark(params: any, callback: any) {
    this.postData(params, this.httpUrl['assign-claim-with-remark'], callback)
  }

  registerUser(params: any, callback: any) {
    this.postData(params, this.httpUrl['registerUser'], callback)
  }

  findUser(username: any, callback: any) {
    this.postData(username, this.httpUrl['findUserByEmail'], callback)
  }

  changePassword(params: any, callback: any) {
    this.postData(params, this.httpUrl['changePassword'], callback)
  }

  updatepassword(params: any, callback: any) {
    this.postData(params, this.httpUrl['updatePass'], callback)
  }

  updateUserStatus(params: any, callback: any) {
    this.postData(params, this.httpUrl['updateStatus'], callback)
  }

  editOfficeName(params: any, callback: any) {
    this.postData(params, this.httpUrl['editOfficeName'], callback)
  }

  addNewOffice(params: any, callback: any) {
    this.postData(params, this.httpUrl['addNewOffice'], callback)
  }

  fetchClient(callback: any) {
    this.getData({}, this.httpUrl['getAllClients'], callback)
  }

  addNewClients(params: any, callback: any) {
    this.postData(params, this.httpUrl['addClients'], callback)
  }

  editClient(params: any, callback: any) {
    this.postData(params, this.httpUrl['editClient'], callback)
  }

  submitFilesToAssignedClaims(params: any, callback: any) {
    this.postData(params, this.httpUrl['claimAttachment'], callback)
  }

  saveArchiveClaims(params: any, callback: any) {
    this.postData(params, this.httpUrl['save-archive-claims'], callback)
  }

  sortData(data: any, sortBy: any, order: any, sortType: string, teamName?: any) {
    if (sortType === 'string') {
      order === 'asc' ? data.sort((a: any, b: any) => {
        if (a[sortBy] === null || a[sortBy] === "null") {
          return 1;
        } else if (b[sortBy] === null || b[sortBy] === "null") {
          return -1;
        } else {
          return a[sortBy].localeCompare(b[sortBy], undefined, { numeric: true });
        }
      }) : data.sort((a: any, b: any) => {
        if (a[sortBy] === null || a[sortBy] === "null") {
          return 1;
        } else if (b[sortBy] === null || b[sortBy] === "null") {
          return -1;
        } else {
          return b[sortBy].localeCompare(a[sortBy], undefined, { numeric: true });
        }
      })

    } else if (sortType === 'number') {

      order === 'asc' ? data.sort((a: any, b: any) => {
        return a[sortBy] - b[sortBy];
      }) : data.sort((a: any, b: any) => {
        return b[sortBy] - a[sortBy];
      });

    } else if (sortType === 'date') {

      order === 'asc' ? data.sort((a: any, b: any) => {
        if (a[sortBy] === null && b[sortBy] === null) return 0;
        else if (a[sortBy] === null) return 1;
        else if (b[sortBy] === null) return -1;
        else return <any>new Date(a[sortBy]).getTime() - <any>new Date(b[sortBy]).getTime();
      }) : data.sort((a: any, b: any) => {
        if (a[sortBy] === null && b[sortBy] === null) return 0;
        else if (a[sortBy] === null) return 1;
        else if (b[sortBy] === null) return -1;
        else return <any>new Date(a[sortBy]).getTime() - <any>new Date(b[sortBy]).getTime();
      });

    }
    else if (sortType === 'dateOnly') {

      order === 'asc' ? data.sort((a: any, b: any) => {
        if (a[sortBy] === null && b[sortBy] === null) return 0;
        else if (a[sortBy] === null) return 1;
        else if (b[sortBy] === null) return -1;
        else return <any>new Date(a[sortBy]) - <any>new Date(b[sortBy]);
      }) : data.sort((a: any, b: any) => {
        if (a[sortBy] === null && b[sortBy] === null) return 0;
        else if (a[sortBy] === null) return 1;
        else if (b[sortBy] === null) return -1;
        else return <any>new Date(b[sortBy]) - <any>new Date(a[sortBy]);
      });

    }

    else if (sortType === 'nestedCount') {
      order === 'asc' ? data.sort((a: any, b: any) => {
        return a.counts1[teamName.toUpperCase()] - b.counts1[teamName.toUpperCase()];
      }) : data.sort((a: any, b: any) => {
        return b.counts1[teamName.toUpperCase()] - a.counts1[teamName.toUpperCase()];
      })
    }
    else if (sortType === 'nestedDate') {
      order === 'asc' ? data.sort((a: any, b: any) => {
        if (a.dates1[teamName.toUpperCase()] === null && b.dates1[teamName.toUpperCase()] === null) return 0;
        else if (a.dates1[teamName.toUpperCase()] === null) return 1;
        else if (b.dates1[teamName.toUpperCase()] === null) return -1;
        else return <any>new Date(a.dates1[teamName.toUpperCase()]) - <any>new Date(b.dates1[teamName.toUpperCase()]);
      }) : data.sort((a: any, b: any) => {
        if (a.dates1[teamName.toUpperCase()] === null && b.dates1[teamName.toUpperCase()] === null) return 0;
        else if (a.dates1[teamName.toUpperCase()] === null) return 1;
        else if (b.dates1[teamName.toUpperCase()] === null) return -1;
        else return <any>new Date(b.dates1[teamName.toUpperCase()]) - <any>new Date(a.dates1[teamName.toUpperCase()]);
      })
    }
  }

  sortDataOld(data: any, sortBy: any, sortingColm: any, order: any, sortingType: any, sortType: string) {
    if (sortType === 'string') {
      if (sortingColm == 'officeAssigned') {
        order === 'asc' ? data.sort((a: any, b: any) => {
          if (a[sortBy] === null || a[sortBy] === "null") {
            return 1;
          } else if (b[sortBy] === null || b[sortBy] === "null") {
            return -1;
          } else {
            return a[sortBy].localeCompare(b[sortBy]);
          }
        }) : data.sort((a: any, b: any) => {
          if (a[sortBy] === null || a[sortBy] === "null") {
            return -1;
          } else if (b.fname === null || b[sortBy] === "null") {
            return 1;
          } else {
            return b[sortBy].localeCompare(a[sortBy]);
          }
        })
      }
      else if (sortingColm == 'officeName') {
        order === 'asc' ? data.sort((a: any, b: any) => {
          return a.officeName.localeCompare(b.officeName)
        }) : data.sort((a: any, b: any) => {
          if (a.officeName > b.officeName) {
            return -1;
          }
          if (a.officeName < b.officeName) {
            return 1;
          }
          return 0;
        });
      }
    } else if (sortingType === 'number') {
      if (sortingColm == 'count') {
        order === 'asc' ? data.sort((a: any, b: any) => {
          return a.count - b.count;
        }) : data.sort((a: any, b: any) => {
          return b.count - a.count;
        });
      }
      else if (sortingColm === 'remoteLiteRejections') {
        order === 'asc' ? data.sort((a: any, b: any) => {
          return a.remoteLiteRejections - b.remoteLiteRejections;
        }) : data.sort((a: any, b: any) => {
          return b.remoteLiteRejections - a.remoteLiteRejections;
        });
      }
      else if (sortingColm === 'countAndRemLiteReject') {
        // order === 'asc' ?  data.sort((p1:any, p2:any) => ((p1.remoteLiteRejections+p1.count) < (p2.remoteLiteRejections+p2.count)) ? -1 : ((p1.remoteLiteRejections+p1.count) < (p2.remoteLiteRejections+p2.count)) ? 1 : 0) : data.sort((p1:any, p2:any) => ((p1.remoteLiteRejections+p1.count) > (p2.remoteLiteRejections+p2.count)) ? -1 : ((p1.remoteLiteRejections+p1.count) > (p2.remoteLiteRejections+p2.count)) ? 1 : 0);
        order === 'asc' ? data.sort((a: any, b: any) => {
          return (a.count + a.remoteLiteRejections) - (b.count + b.remoteLiteRejections)
        }) : data.sort((a: any, b: any) => {
          return (b.count + b.remoteLiteRejections) - (a.count + a.remoteLiteRejections)
        })
      }
    } else if (sortingType === 'date') {
      if (sortingColm === 'opdt') {
        order === 'asc' ? data.sort((a: any, b: any) => {
          if (a.opdt === null && b.opdt === null) return 0;
          else if (a.opdt === null) return 1;
          else if (b.opdt === null) return -1;
          else return <any>new Date(a.opdt).getTime() - <any>new Date(b.opdt).getTime();
        }) : data.sort((a: any, b: any) => {
          if (a.opdt === null && b.opdt === null) return 0;
          else if (a.opdt === null) return 1;
          else if (b.opdt === null) return -1;
          else return <any>new Date(b.opdt).getTime() - <any>new Date(a.opdt).getTime();
        });
      }
      else if (sortingColm === 'opdos') {
        order === 'asc' ? data.sort((a: any, b: any) => {
          if (a.opdos === null && b.opdos === null) return 0;
          else if (a.opdos === null) return 1;
          else if (b.opdos === null) return -1;
          else return <any>new Date(a.opdos).getTime() - <any>new Date(b.opdos).getTime();
        }) : data.sort((a: any, b: any) => {
          if (a.opdos === null && b.opdos === null) return 0;
          else if (a.opdos === null) return 1;
          else if (b.opdos === null) return -1;
          else return <any>new Date(b.opdos).getTime() - <any>new Date(a.opdos).getTime();
        });
      }
    }


  }

  getAllClients(callback: any) {
    this.getData({}, this.httpUrl['getAllClients'], callback)
  }

  getClientsName(callback: any) {
    this.getData({}, this.httpUrl['getClientsName'], callback)
  }

  fetchIssueClaims(cid: string, callback: any) {
    this.getData({}, this.httpUrl['issueclaim'] + "/" + cid, callback)
  }
  // fetchIssueClaims(page: any,cid: string, callback: any) {
  //   this.getData({}, this.httpUrl['issueclaim'] + "/" + cid+ "/" + page, callback)
  // }

  fetchivfDataForClaim(cuuid: string, callback: any) {
    this.getData({}, this.httpUrl['ivfdata'] + "/" + cuuid, callback)
  }


  saveProductionData(params: any, callback: any) {
    this.postData(params, this.httpUrl['productionData'], callback)
  }

  editUser(params: any, callback: any) {
    this.postData(params, this.httpUrl['editRoles'], callback)
  }

  fetchOfficeByUuid(callback: any) {
    this.getData({}, this.httpUrl['getOfficesByUuid'], callback)
  }

  setPaddingRightContainer() {
    let m: any = document.getElementsByClassName("gray-bar");
    if (m[0].clientHeight > 55) {
      return this.setPaddingContainer = true;
    }
  }

  fetchTLUsers(callback: any) {
    this.getData({}, this.httpUrl['tl_user'], callback)
  }

  fetchOtherTeams(callback: any) {
    this.getData({}, this.httpUrl['other_user_team'], callback)
  }

  isClaimStatusActive(uuid: any, callback: any) {
    this.getData({}, this.httpUrl[`claimStatus`] + "/" + uuid, callback)
  }

  fetchRolesByEmail(email: any, callback: any) {
    this.getData({}, this.httpUrl['rolesByEmail'] + "/" + email, callback)
  }

  fetchAllPendency(callback: any) {
    this.getData({}, this.httpUrl['allpendency'], callback)
  }

  fetchUserByDetail(query: any, callback: any) {
    this.getData({}, this.httpUrl['findUserByDetail'] + "/" + query, callback)
  }

  fetchTpData(claimUuid: any, callback: any) {
    this.getData({}, this.httpUrl['fetchTpData'] + "/" + claimUuid, callback)
  }

  fetchIssueClaimCounts(callback: any) {
    this.getData({}, this.httpUrl['fetchIssueClaimCounts'], callback)
  }

  fetchGSheet(callback: any) {
    this.getData({}, this.httpUrl['googleSheetLink'], callback);
  }

  fetchAttachmentCount(claimUuid: any, callback: any) {
    this.getData({}, this.httpUrl['get-attachments-count'] + "/" + claimUuid, callback)
  }

  fetchAllUserClients(callback: any) {
    this.getData({}, this.httpUrl['alluserclients'], callback)
  }

  fetchClientNamebyUuid(uuid: any, callback: any) {
    this.getData({}, this.httpUrl['allpendency'] + "/" + uuid, callback)
  }

  getAttachmentFile(uuid: any, callback: any) {
    this.getData({}, this.httpUrl['get-attachments'] + "/" + uuid, callback);
  }

  removeAttachmentFile(params: any, callback: any) {
    this.postData(params, this.httpUrl['remove-claim-attachment'], callback);
  }

  updateIvId(params: any, callback: any) {
    this.postData(params, this.httpUrl['updateIv'], callback)
  }

  removeIVTP(params: any, callback: any) {
    this.postData(params, this.httpUrl['removeIVTP'], callback)
  }

  issueClaimPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['issueClaimsPdf'] + "/" + pdfcsv, callback);
  }
  pendancyPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['pendancyPdf'] + "/" + pdfcsv, callback);
  }
  claimDetailsPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['claimDetailsPdf'] + "/" + pdfcsv, callback);
  }
  productionPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['productionPdf'] + "/" + pdfcsv, callback);
  }
  lisOfClaimsPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['listOfClaim'] + "/" + pdfcsv, callback);
  }
  downloadAttachments(params: any, callback: any) {
    this.getDataFiles(params, this.httpUrl['download-attachment'], callback);
  }
  allPendancyPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['allPendancy'] + "/" + pdfcsv, callback);
  }

  TplanPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['tPlan'] + "/" + pdfcsv, callback);
  }

  IvfPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['Ivf'] + "/" + pdfcsv, callback);
  }
  othersTeamPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['othersTeam'] + "/" + pdfcsv, callback);
  }

  emitOnValueChange(data: any) {
    this.seviceEventEmitter.next(data);
  }

  subscribeOnValueChange(name: any, callback: any) {
    this.seviceEventEmitterReference[name] = this.seviceEventEmitter.subscribe((header) => {
      callback(header)
    });
  }
  editUserInfo(params: any, callback: any) {
    this.postData(params, this.httpUrl['editUserInfo'], callback);
  }
  editUserRole(params: any, callback: any) {
    this.postData(params, this.httpUrl['editUserRole'], callback);
  }

  editUserClient(params: any, callback: any) {
    this.postData(params, this.httpUrl['editUserClient'], callback);
  }

  editUserTeam(params: any, callback: any) {
    this.postData(params, this.httpUrl['editUserTeam'], callback);
  }

  existingTLInfo(params: any, callback: any) {
    this.postData(params, this.httpUrl['existingTLInfo'], callback);
  }
  fetchClientsByUser(callback: any) {
    this.getData({}, this.httpUrl['userClientsWithOffices'], callback)
  }

  saveUnarchiveClaims(params: any, callback: any) {
    this.postData(params, this.httpUrl['save-unarchive-claims'], callback)
  }

  searchClaims(params: any, callback: any) {
    this.postData(params, this.httpUrl['search-claims'], callback)
  }

  getSerachParams(callback: any) {
    this.getData({}, this.httpUrl['search-params'], callback)
  }

  seacrhClaimsPdfDownload(params: any, pdfcsv: string, callback: any) {
    this.postDataPdf(params, this.httpUrl['search-claims-pdf'] + "/" + pdfcsv, callback);
  }
}
