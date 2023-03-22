import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "NA", "paths": ["/tool-update"], "defaultpath": "/claim-assignment"
        }],
        [2, {
        "name":"Admin","paths":["/register",'/user-setting','/manage-office','/users-status','/manage-client'],"defaultpath":"/register"
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
            "name": "Billing Team", "paths": ["/claim-assignment","/tool-update"], "defaultpath": "claim-assignment"
        }],
        [8,{
        "name":"IV Team","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [9,{
        "name":"Client","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [10,{
            "name":"Aging","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [11,{
            "name":"Posting","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [12,{
            "name":"Quality","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [13,{
            "name":"Upload Claims","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],
        [14,{
            "name":"Account Manager","paths":["/user-setting"],"defaultpath":"/user-setting"
        }],

    ]);

    public  BILLING_ID=1;
    public  RE_BILLING_ID=2;
    
    
    public TEAMS_ID_CONFIG= new Map<number,string>([
        [2,"ADMIN"],
        [3,"PATIENT_CALLING"],
        [4,"OFFICE"],
        [5,"INTERNAL_AUDIT"],
        [6,"IV_TEAM"],
        [7,"BILLING"],
        [8,"LC3"],
        [9,"CLIENT_MANAGER"],

     ]);


}