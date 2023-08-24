import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { AppConstants } from 'src/app/constants/app.constants';

@Component({
  selector: 'app-attach-file',
  templateUrl: './attach-file.component.html',
  styleUrls: ['./attach-file.component.scss'],
})
export class AttachFileComponent {

  showModal:boolean=false;
  selectedFiles:any=[];
  totalFile:number=0;
  loader:boolean=false;
  formData:any = new FormData();
  @Input() inputConfig:any={};
  @Output() emitToParent:any= new EventEmitter();
  attachmentTypeId:any;

  errorMessage:any;
  constructor(public constant:AppConstants){}

  openModal() {
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.totalFile = this.selectedFiles.length;
  }

  onFileChange(event: any) {
    this.formData = new FormData();
    for (let i = 0; i < event.target.files.length; i++) {
      let params:any = {
        'claimUuid':this.inputConfig.claimUuid,
        'attachmentTypeId':'',
        'file':event.target.files[i]
      }
      this.selectedFiles.unshift(params);
    }
  }

  removeItem(file: File) {
    const index = this.selectedFiles.findIndex((e:any)=>e.file == file);
    if (index !== -1) {
      this.selectedFiles.splice(index, 1);
      this.totalFile = this.selectedFiles.length;
    }
  }

  uploadItem(claimUuid:any) {
    let fileNameExist:Boolean =  this.isFileNameExist();
    let isEmptyAttachment:Boolean = this.isEmptyAttachmentType();
    if(!fileNameExist && !isEmptyAttachment){
      this.totalFile = this.selectedFiles.length;
      this.emitToParent.emit({action:'fileSelected',value:this.selectedFiles,claimUuid:claimUuid});
      this.errorMessage='';
      this.closeModal();
    } else{
      fileNameExist ?  this.errorMessage = "Same File Already Exist" : this.errorMessage = "Please Select Attachment Type";
    }
  }

  isFileNameExist(){
    return this.selectedFiles.some((obj:any, index:any) =>
    this.selectedFiles.slice(index + 1).some((innerObj:any) => innerObj.file.name === obj.file.name));
 }

  isEmptyAttachmentType(){
     return this.selectedFiles.some((item:any)=>item.attachmentTypeId == '');
 }

  

}
