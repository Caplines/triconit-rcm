import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "NA", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client', '/tool-update'], "defaultpath": "/register"
        }],
        [2, {
            "name": "Admin", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client'], "defaultpath": "/register"
        }],
        [3, {
            "name": "Internal Audit Team", "paths": ["/list-of-claims", "/claim-assignment", "/update-pass", "/all-pendency", "/tool-update","/production"], "defaultpath": "/claim-assignment"
        }],
        [4, {
            "name": "Lc3", "paths": ["/update-pass"], "defaultpath": "/update-pass"
        }],
        [5, {
            "name": "Office", "paths": ["/update-pass"], "defaultpath": "/update-pass"
        }],
        [6, {
            "name": "Patient Calling", "paths": ["/update-pass"], "defaultpath": "/update-pass"
        }],
        [7, {
            "name": "Billing Team", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass"], "defaultpath": "/claim-assignment"
        }],

        // [9,{
        //     "name":"Reporting","paths":["/update-pass"],"defaultpath":"/update-pass"
        // }],

    ]);

    public BILLING_ID = 1;
    public RE_BILLING_ID = 2;
    public INTERNAL_AUDIT_TEAM = 3;
    public BILLING_TEAM = 7;


    public TEAMS_ID_CONFIG = new Map<number, string>([
        [2, "ADMIN"],
        [3, "INTERNAL_AUDIT"],
        [4, "LC3"],
        [5, "OFFICE"],
        [6, "PATIENT_CALLING"],
        [7, "BILLING"],
        // [9,"REPORTING"],

    ]);

    public teamData: any = [{ "teamName": "Internal Audit", "teamId": 3 }, { "teamName": "Lc3", "teamId": 4 }, { "teamName": "Office", "teamId": 5 }, { "teamName": "Patient Calling", "teamId": 6 }, { "teamName": "Billing", "teamId": 7 }];

}