import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-access-icon',
  templateUrl: './access-icon.component.html',
  styleUrls: ['./access-icon.component.scss']
})
export class AccessIconComponent implements OnInit {

  // access: any;
  // @Input() sectionId: any;
  // @Input() checkForSectionAccess: (sectionid: number, accessType: string) => boolean;
  // @Input() allowEdit: boolean;

  constructor() { }

  ngOnInit() {

    // const hasEditAccess = this.checkForSectionAccess(this.sectionId, 'edit');
    // const hasViewAccess = this.checkForSectionAccess(this.sectionId, 'view');

    // if (hasEditAccess && this.allowEdit) {
    //   this.access = "Edit";
    // } else if (hasViewAccess) {
    //   this.access = "Read";
    // }
  }

}
