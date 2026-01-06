package com.tricon.rcm.util;

public class ReconCategoryContext {
    public String typePattern;     // %_P or %_S
    public boolean billingPending; // submitted or not
    public String esStatus;       // OPEN / CLOSED / null
    public boolean primarySide;    // P or S
}
