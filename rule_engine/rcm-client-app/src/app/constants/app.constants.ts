import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "Billing Team", "paths": ["/fetch-claims"], "defaultpath": "register"
        }
        ],
        [2, {
        "name":"LC3 Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [3,{
        "name":"Patient Calling Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [4,{
        "name":"Office Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [5,{
        "name":"Internal Audit Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [5,{
        "name":"IV Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],

    ]);


}