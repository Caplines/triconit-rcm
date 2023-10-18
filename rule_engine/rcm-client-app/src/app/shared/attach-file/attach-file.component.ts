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
  totalFile: number = 0;
  loader: boolean = false;
  formData: any = new FormData();
  @Input() inputConfig: any = {};
  @Output() emitToParent: any = new EventEmitter();
  attachmentTypeId: any;
  attachedFiles: any = [];
  hasAttachmentFileData: boolean = false;
  errorMessage: any;

  removeAttachmentFiles: any = [];
  removeClaimAttachmentId: any = [];
  userEmail:any='';

  constructor(public constant: AppConstants, private appService: ApplicationServiceService, private downloadService: DownLoadService) {
   }

   ngOnInit(){
    this.userEmail = Utils.currentUserEmail();
   }

  openModal() {
    this.showModal = true;
    if (this.inputConfig['isDetailPage'] || (this.inputConfig.attachmentCount > 0 && !this.hasAttachmentFileData && this.removeAttachmentFiles.length == 0)) {
      this.getAttachmentFile();
      console.log(this.userEmail);
      
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
      }
    })
  }

  closeModal() {
    this.showModal = false;
    this.totalFile = this.selectedFiles.length;
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
      this.totalFile = this.selectedFiles.length;
    }
  }

  uploadItem(claimUuid: any) {
    let fileNameExist: Boolean = this.isFileNameExist();
    let isEmptyAttachment: Boolean = this.isEmptyAttachmentType();
    if (!fileNameExist && !isEmptyAttachment) {
      this.totalFile = this.selectedFiles.length;
      this.loopThroughData(this.selectedFiles, 0);
      this.errorMessage = '';
    } else {
      fileNameExist ? this.errorMessage = "Same File Already Exist" : this.errorMessage = "Please Select Attachment Type";
    }
  }

  loopThroughData(dataArray: any[], currentIndex: number) {
    if (currentIndex >= dataArray.length) {
      this.emitToParent.emit({action:'fileUploadedSuccess',value:this.errorMessage,hasAttachedFiles:true})
      this.closeModal();
      return;
    }
    const currentData = dataArray[currentIndex];
    let formData: any = new FormData();
    formData.append("claimUuid", currentData?.claimUuid ? currentData.claimUuid : this.inputConfig.claimUuid);
    formData.append("attachmentTypeId", currentData?.attachmentTypeId ? currentData.attachmentTypeId : 0);
    formData.append("file", currentData?.file ? currentData.file : new File([""], "filename"));
    this.appService.submitFilesToAssignedClaims(formData, (res: any) => {
      if (res.data.status) {
        this.errorMessage = res.data.message;
        this.loopThroughData(dataArray, currentIndex + 1);
      } else {
        this.errorMessage = res.data.message;
      }
    })
}

  isFileNameExist() {
    return this.selectedFiles.some((obj: any, index: any) =>
      this.selectedFiles.slice(index + 1).some((innerObj: any) => innerObj.file.name === obj.file.name));
  }

  isEmptyAttachmentType() {
    return this.selectedFiles.some((item: any) => item.attachmentTypeId == '');
  }

  removePreSelectedFile(file: any) {
      const index = this.attachedFiles.findIndex((e: any) => e.file.name == file.file.name);
      if (index !== -1) {
        this.attachedFiles.splice(index, 1);
        this.removeClaimAttachmentId.push(file.id);
        let params: any = {
          "claimAttachmentId": this.removeClaimAttachmentId,
          "claimUuid": this.inputConfig.claimUuid
        };
        this.removeAttachmentFiles = params;
        this.removeAttachmentFile()
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

  removeAttachmentFile(){
    this.appService.removeAttachmentFile(this.removeAttachmentFiles,(res:any)=>{
      if(res.status == 200){
        console.log("434");
        
        if(!false){
          this.errorMessage  = res.data.message;
        }
      } else{
        this.errorMessage  = res.data.message;
      }
    })
  }

}
