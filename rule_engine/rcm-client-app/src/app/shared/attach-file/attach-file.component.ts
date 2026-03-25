import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { AppConstants } from 'src/app/constants/app.constants';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { DownLoadService } from 'src/app/service/download.service';
import Utils from 'src/app/util/utils';

@Component({
  selector: 'app-attach-file',
  templateUrl: './attach-file.component.html',
  styleUrls: ['./attach-file.component.scss'],
})
export class AttachFileComponent {

  showModal: boolean = false;
  selectedFiles: any = [];
  selectedFilesClaimDetail: any = [];
  totalFile: number = 0;
  loader: boolean = false;
  formData: any = new FormData();
  @Input() inputConfig: any = {};
  @Output() emitToParent: any = new EventEmitter();
  attachmentTypeId: any;
  attachedFiles: any = [];
  attachFileClaimDetail: any = [];
  hasAttachmentFileData: boolean = false;
  errorMessage: any;

  removeAttachmentFiles: any = [];
  removeClaimAttachmentId: any = [];
  userEmail:any='';
  isAttachedBySameUser:boolean=false;
  fileloader:boolean = false;
  uploadButton:boolean=false;
  smilePoint: boolean = false;
  

  constructor(public constant: AppConstants, private appService: ApplicationServiceService, private downloadService: DownLoadService) {
   }

   ngOnInit(){
    this.smilePoint = Utils.isSmilePoint();
    this.errorMessage = '';
    this.userEmail = Utils.currentUserEmail();
    if(this.inputConfig['isDetailPage']){
      this.getAttachmentFile();
    }
   }

  openModal() {
    this.fileloader= false;
    this.uploadButton=false;
    this.showModal = true;
    if (this.inputConfig['isDetailPage'] || (this.inputConfig.attachmentCount > 0 && !this.hasAttachmentFileData && this.removeAttachmentFiles.length == 0)) {
      this.getAttachmentFile();
    }
  }

  getAttachmentFile() {
    this.removeAttachmentFiles = [];
    this.selectedFiles =[]; 
    this.removeClaimAttachmentId= [];
    this.appService.getAttachmentFile(this.inputConfig.claimUuid, (res: any) => {
      if (res.status == 200) {
        console.log(res);
        this.attachedFiles = res.data;
        this.emitToParent.emit({action:'clearAttachmentAndRemovedFiles'});
        this.attachFileClaimDetail = res.data;
        this.isFileAttachedBySameUser();
      }
    })
  }

  closeModal() {
    this.showModal = false;
    this.totalFile = this.attachedFiles.length + this.selectedFiles.length;
    this.errorMessage = '';
    this.removeAttachmentFiles=[];
  }

  onFileChange(event: any) {
    this.formData = new FormData();
    for (let i = 0; i < event.target.files.length; i++) {
      let params: any = {
        'claimUuid': this.inputConfig.claimUuid,
        'attachmentTypeId': '',
        'file': event.target.files[i]
      }
      this.selectedFiles.unshift(params);
    }
  }

  removeItem(file: File) {
    const index = this.selectedFiles.findIndex((e: any) => e.file == file);
    if (index !== -1) {
      this.selectedFiles.splice(index, 1);
      this.totalFile = this.attachedFiles.length;
    }
  }

  uploadItem(claimUuid: any) {
    let fileNameExist: Boolean = this.isFileNameExist();
    let isEmptyAttachment: Boolean = this.isEmptyAttachmentType();
    if (!fileNameExist && !isEmptyAttachment) {
      this.totalFile = this.attachedFiles.length;
      this.fileloader= true;
      this.uploadButton=true;
      this.loopThroughData(this.selectedFiles, 0);
      this.errorMessage = '';
    } else {
      fileNameExist ? this.errorMessage = "Same File Already Exist" : this.errorMessage = "Please Select Attachment Type";
    }
  }

  loopThroughData(dataArray: any[], currentIndex: number) {
   
    if (currentIndex >= dataArray.length) {
      this.emitToParent.emit({action:'fileUploadedSuccess',value:this.errorMessage,hasAttachedFiles:this.isAttachedBySameUser})
      this.errorMessage='';
      this.closeModal();
      if(this.inputConfig['isDetailPage']){
        this.selectedFiles=[];
        this.getAttachmentFile();
        setTimeout(() => {
          this.errorMessage = '';
        }, 1000);
      }
      this.fileloader= false;
      this.uploadButton=false;
      return;
    }
    const currentData = dataArray[currentIndex];
    let formData: any = new FormData();
    formData.append("claimUuid", currentData?.claimUuid ? currentData.claimUuid : this.inputConfig.claimUuid);
    formData.append("attachmentTypeId", currentData?.attachmentTypeId ? currentData.attachmentTypeId : 0);
    formData.append("file", currentData?.file ? currentData.file : new File([""], "filename"));
    this.appService.submitFilesToAssignedClaims(formData, (res: any) => {
      if (res?.data.status) {
        this.errorMessage = res.data.message;
        this.loopThroughData(dataArray, currentIndex + 1);
      } else {
        this.errorMessage = res.data.message ? res.data.message : res.messages;
      }
    })
  }

  isFileAttachedBySameUser() {
    this.isAttachedBySameUser = this.attachedFiles.some((item: any) => item.uploadedByUserUuid.toUpperCase() === this.userEmail.toUpperCase());
    if (this.isAttachedBySameUser) {
      this.emitToParent.emit({ action: 'hasAttachedFileForSameUser', hasAttachedFiles: this.isAttachedBySameUser });
    } else {
      this.emitToParent.emit({ action: 'hasAttachedFileForSameUser', hasAttachedFiles: this.isAttachedBySameUser });
    }
  }

  isFileNameExist() {
    return this.selectedFiles.some((obj: any, index: any) =>
      this.selectedFiles.slice(index + 1).some((innerObj: any) => innerObj.file.name === obj.file.name));
  }

  isEmptyAttachmentType() {
    return this.selectedFiles.some((item: any) => item.attachmentTypeId == '');
  }

  removePreSelectedFile(file: any) {
    this.loader=true;
    let params: any = {
      "claimAttachmentId": [file.id],
      "claimUuid": this.inputConfig.claimUuid
    };
    this.removeAttachmentFiles = params;
    this.appService.removeAttachmentFile(this.removeAttachmentFiles,(res:any)=>{
      if(res.data?.fileResponseStatus && res.status == 200){
            this.loader=false;
            this.isFileAttachedBySameUser();
          this.errorMessage  = res.data.message;
          this.removeAttachedFileFromList(file);
          if(this.inputConfig['isDetailPage']){
            setTimeout(() => {
              this.errorMessage = '';
            }, 1000);
          }
      } else{
        this.loader=false;
        this.errorMessage  = res.data.message ? res.data.message : res.message;
      }
    })
  }
  
  removeAttachedFileFromList(file:any){
    const index = this.attachedFiles.findIndex((e: any) => e.file.name == file.file.name);
    if (index !== -1) {
      this.attachedFiles.splice(index, 1);
    }
  }

  downloadAttachment(file: any) {
    this.appService.downloadAttachments({ 'fileId': file.id }, (res: any) => {
      if (res.status == 200) {
        console.log(res);
        this.downloadService.saveBolbData(res.body, file.file.name)
      }
    })
  }

  get allowUpload(): boolean {
    //False = u can upload
    // console.log(this.inputConfig.allowUpload);
    if (this.inputConfig.allowUpload != undefined && this.inputConfig.allowUpload) {
      return this.inputConfig.allowUpload;
    } else {
      return false;
    }
  }


}
