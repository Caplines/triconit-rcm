import { Component, Input} from '@angular/core';
import { DatePipe } from '@angular/common';
import Utils from '../../util/utils';
import { AppConstants } from 'src/app/constants/app.constants';

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.scss']
})

export class FeedbackComponent {
  @Input() emailUrl: any;
  clientName: string;
  userName: string;
  team: string;
  date: string;
  toAddress: string = 'rcmhelp@caplineservices.com';

  constructor(private datePipe: DatePipe, public appConstants: AppConstants) {
  }

  ngOnInit(): void {
    this.clientName = Utils.getSelectedClientName();
    this.userName = Utils.getLoggedInUserFirstAndLastName();
    this.team = this.getCurrentTeamName();
  }

  ngAfterViewChecked(): void {
    this.date = this.datePipe.transform(new Date(), 'EEE, dd MMM yyyy');
    this.emailUrl = window.location.href;
  }

  getCurrentTeamName(): string {
    let team = this.appConstants.teamData.find((e: any) => e.teamId == Utils.selectedTeam());
    if (team){
      return team.teamName
    } else {
      return '';
    }
  }
}