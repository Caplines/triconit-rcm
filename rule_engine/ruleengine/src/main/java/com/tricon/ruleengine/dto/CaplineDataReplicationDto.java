package com.tricon.ruleengine.dto;

public class CaplineDataReplicationDto
{
    private String officeNameDB;
    private String passwordRE;
    private String selectcolumns;
    private String gndatebet;
    private String queryName;
    public String getQueryName() {
           return queryName;
    }

    public void setQueryName(String queryName) {
           this.queryName = queryName;
    }

    private int columnCount;
    
    public int getColumnCount() {
           return columnCount;
    }

    public void setColumnCount(int columnCount) {
           this.columnCount = columnCount;
    }

    public CaplineDataReplicationDto() {}

    public String getOfficeNameDB() {
           return officeNameDB;
    }

    public void setOfficeNameDB(String officeNameDB) {
           this.officeNameDB = officeNameDB;
    }

    public String getPasswordRE() {
           return passwordRE;
    }

    public void setPasswordRE(String passwordRE) {
           this.passwordRE = passwordRE;
    }

    public String getSelectcolumns() {
           return selectcolumns;
    }

    public void setSelectcolumns(String selectcolumns) {
           this.selectcolumns = selectcolumns;
    }

    public String getGndatebet() {
           return gndatebet;
    }

    public void setGndatebet(String gndatebet) {
           this.gndatebet = gndatebet;
    }

    @Override
    public String toString() {
           return "CaplineDataReplicationDto [officeNameDB=" + officeNameDB + ", passwordRE=" + passwordRE
                         + ", selectcolumns=" + selectcolumns + ", gndatebet=" + gndatebet + ", queryName=" + queryName
                         + ", columnCount=" + columnCount + "]";
    } 
}
