import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "Billing Team", "paths": ["/register"], "defaultpath": "register"
        }],
        [2, {
        "name":"Admin","paths":["/register"],"defaultpath":"/register"
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
        [6,{
        "name":"IV Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [7, {
            "name": "Billing Team", "paths": ["/claim-assignment"], "defaultpath": "claim-assignment"
        }],
        [8,{
        "name":"IV Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [9,{
        "name":"IV Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],

    ]);

    public  BILLING_ID=1;
    public  RE_BILLING_ID=2;
    



}