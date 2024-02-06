import { Pipe, PipeTransform } from '@angular/core';
import { TeamsM } from '../models/claim-rcm-data-model';


@Pipe({
    name: 'ClaimSubmissionTeamPipe'
})
export class ClaimSubmissionTeamPipe implements PipeTransform {
    transform(myobjects: Array<TeamsM>, args?: Array<any>): any {
        let returnobjects: any = [];
        let pending = args[0]["pending"];
        myobjects.forEach(function (v) {
            if (pending) {
                if (v.canWorkBeforeSubmssion) returnobjects.push(v);
            } else returnobjects.push(v);

        });
        return returnobjects;

    }
}