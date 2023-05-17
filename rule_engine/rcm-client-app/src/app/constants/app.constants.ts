import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "NA", "paths": ["/register",'/user-setting','/manage-office','/users-status','/manage-client','/tool-update'], "defaultpath": "/register"
        }],
        [2, {
        "name":"Admin","paths":["/register",'/user-setting','/manage-office','/users-status','/manage-client'],"defaultpath":"/register"
        }],
        [3,{
        "name":"Internal Audit Team","paths":["/list-of-claims","/claim-assignment","/update-pass","/all-pendency","/tool-update"],"defaultpath":"/claim-assignment"
        }],
        [4,{
            "name":"Aging","paths":["/update-pass"],"defaultpath":"/update-pass"
        }],
        [5,{
            "name":"Posting","paths":["/update-pass"],"defaultpath":"/update-pass"
        }],
        [6,{
            "name":"Quality","paths":["/update-pass"],"defaultpath":"/update-pass"
        }],
        [7, {
            "name": "Billing Team", "paths": ["/claim-assignment","/tool-update","/list-of-claims","/fetch-claims","/production","/all-pendency","/update-pass"], "defaultpath": "/claim-assignment"
        }],

        // [9,{
        //     "name":"Reporting","paths":["/update-pass"],"defaultpath":"/update-pass"
        // }],

    ]);

    public  BILLING_ID=1;
    public  RE_BILLING_ID=2;
    
    
    public TEAMS_ID_CONFIG= new Map<number,string>([
        [2,"ADMIN"],
        [3,"INTERNAL_AUDIT"],
        [4,"AGING"],
        [5,"POSTING"],
        [6,"QUALITY"],
        [7,"BILLING"],
        // [9,"REPORTING"],

     ]);

    public teamData:any= [{ "teamName": "Internal Audit", "teamId": 3 }, { "teamName": "Aging", "teamId": 4 }, { "teamName": "Posting", "teamId": 5 }, { "teamName": "Quality", "teamId": 6 }, { "teamName": "Billing", "teamId": 7 }];

}