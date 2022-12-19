import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.module';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "Billing Team", "paths": ["/fetch-claims"], "defaultpath": "fetch-claims"
        }
        ]
        //, [2, {}]
    ]);


}