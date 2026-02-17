# Patient Not Found Rule Analysis

## Rule Information

**Rule Name:** `"no rule"`  
**Rule ID:** `-1`  
**Message Key:** `rule.patient.notfound.espatient`  
**Message Template:** `Patient Details - {0} Not found in EagleSoft Fee Schedule`

## Message Format

The actual message displayed is:
```
Patient Details - Patient Details not found in Patient Sheet({patient_id}-{patient_dob}) Not found in EagleSoft Fee Schedule
```

Where:
- `{patient_id}` = Patient ID from IVF form (`ivf.getPatientId()`)
- `{patient_dob}` = Patient Date of Birth from IVF form (`ivf.getPatientDOB()`)

## Where This Rule is Triggered

### 1. In `validateTreatmentPlanPreBatch` Method
**File:** `TreatmentPlanServiceImpl.java`  
**Line:** 5101  
**Context:** When patient data is not found in `espatients` map

```java
} else {
    dtoRL = rb.patientNotFound((IVFTableSheet) ivfMap.get(ivx).get(0), messageSource);
    list.addAll(dtoRL);
}
```

**Condition:** `if (espatients != null && espatients.size() > 0)` evaluates to `false`

### 2. In Rule4 Methods (Coverage Book & Fee Schedule)
**File:** `RuleBook.java`  
**Lines:** 505-515, 644-653

**Method:** `Rule4_B` and `Rule4_A`

```java
} else {
    RuleEngineLogger.generateLogs(clazz, "Patient Details not found in Patient Sheet -"
            + ivf.getPatientName() + "-" + ivf.getPatientDOB(), Constants.rule_log_debug, bw);
    dList.add(new TPValidationResponseDto(rule.getId(), rule.getName() + " & Fee", 
            messageSource.getMessage("rule.patient.notfound.espatient",
                    new Object[] { "Patient Details not found in Patient Sheet(" 
                            + Constants.errorMessOPen + ivf.getPatientName() + "-" 
                            + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
                    locale), Constants.FAIL, String.join(",", surfaces), 
                    String.join(",", teethC), String.join(",", fcodes)));
    pass = false;
}
```

**Condition:** `if (espatients != null && espatients.get(0) != null)` evaluates to `false`

### 3. Helper Method: `patientNotFound()`
**File:** `RuleBook.java`  
**Lines:** 19342-19349

```java
public List<TPValidationResponseDto> patientNotFound(IVFTableSheet ivf, MessageSource messageSource) {
    List<TPValidationResponseDto> dList = new ArrayList<>();
    dList.add(new TPValidationResponseDto(-1, "no rule", 
            messageSource.getMessage("rule.patient.notfound.espatient",
                    new Object[] { "Patient Details not found in Patient Sheet(" 
                            + Constants.errorMessOPen + ivf.getPatientName() + "-" 
                            + ivf.getPatientDOB() + Constants.errorMessClose + ")" },
                    locale), Constants.FAIL, "", "", ""));
    return dList;
}
```

## Comparison Parameters

### Patient Lookup Process

1. **Patient Key Construction:**
   - **Location:** `TreatmentPlanServiceImpl.java` line 4938
   - **Format:** `officeName + "_" + ivfId`
   - **Code:** `String patKey = off.getName() + "_" + ivx;`
   - Where:
     - `off.getName()` = Office name
     - `ivx` = IVF ID (from `ids[y]`)

2. **Patient Data Retrieval:**
   - **Method:** `EagleSoftDBAccessService.getPatientData()`
   - **Location:** `EagleSoftDBAccessServiceImpl.java` lines 81-193
   - **Query:** Uses SQL query to fetch patient data from EagleSoft database
   - **Query Parameters:**
     - Patient IDs from IVF forms: `ivfSheet.getPatientId()`
     - Insurance Type: Primary or Secondary
   - **SQL Query:** 
     ```sql
     SELECT pat.patient_id, pat.first_name, pat.last_name, pat.birth_date, 
            pat.social_security, pat.prim_member_id, pat.status, ...
     FROM patient pat, employer emp
     WHERE pat.patient_id IN (?) 
       AND (pat.prim_employer_id=emp.employer_id OR pat.sec_employer_id=emp.employer_id)
     ```

3. **Patient Matching Logic:**
   - **Location:** `EagleSoftDBAccessServiceImpl.java` line 159
   - **Comparison:**
     ```java
     if ((pat.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId()))) {
         // Match found - add to return map
     }
     ```
   - **Parameters Compared:**
     - **From EagleSoft Database:** `pat.getPatientId()` (trimmed, case-insensitive)
     - **From IVF Form:** `ivfSheet.getPatientId()` (from IVF sheet)

4. **Return Map Key:**
   - **Location:** `EagleSoftDBAccessServiceImpl.java` line 173
   - **Key Format:** `ivfSheet.getUniqueID()`
   - **UniqueID Format:** `officeName + "_" + ivfId`
   - **Example:** `"OfficeName_123"`

5. **Patient Lookup Check:**
   - **Location:** `TreatmentPlanServiceImpl.java` line 4941
   - **Check:**
     ```java
     if (espatients != null && espatients.get(patKey) != null 
             && espatients.get(patKey).get(0) != null) {
         // Patient found - proceed with rules
     } else {
         // Patient not found - trigger "no rule" message
     }
     ```

## Comparison Summary

### Primary Comparison
**Parameter 1:** Patient ID from IVF Form  
- **Source:** `IVFTableSheet.getPatientId()`
- **Location:** IVF Google Sheet or Database

**Parameter 2:** Patient ID from EagleSoft Database  
- **Source:** `EagleSoftPatient.getPatientId()`
- **Location:** EagleSoft database query result

**Comparison Method:** 
- `String.equalsIgnoreCase()` after trimming
- Case-insensitive string comparison

### Secondary Information Used
- **Patient Name:** `ivf.getPatientName()` - Used in error message display
- **Patient DOB:** `ivf.getPatientDOB()` - Used in error message display
- **Office Name:** `off.getName()` - Used in patient key construction
- **IVF ID:** `ivx` - Used in patient key construction

## When the Message is Triggered

The message is triggered when:

1. **Patient ID Mismatch:**
   - Patient ID from IVF form does not match any patient ID in EagleSoft database
   - The SQL query returns no matching records

2. **Patient Not in Database:**
   - Patient exists in IVF form but does not exist in EagleSoft patient table
   - Patient ID is invalid or incorrect

3. **Insurance Type Mismatch:**
   - Patient exists but doesn't have the specified insurance type (Primary/Secondary)
   - Query filters by `prim_employer_id` or `sec_employer_id` based on insurance type

4. **Key Mismatch:**
   - Patient exists in database but the lookup key (`officeName + "_" + ivfId`) doesn't match
   - This could happen if IVF ID or office name is incorrect

## Data Flow

```
1. IVF Form Data
   └─> Patient ID: "12345"
   └─> Patient Name: "John Doe"
   └─> Patient DOB: "01/01/1990"
   └─> Office Name: "Office1"
   └─> IVF ID: "100"

2. Patient Key Construction
   └─> patKey = "Office1_100"

3. Database Query
   └─> Query: SELECT * FROM patient WHERE patient_id IN ('12345')
   └─> Insurance Type: Primary/Secondary

4. Patient Matching
   └─> Compare: pat.getPatientId() == ivfSheet.getPatientId()
   └─> If match: Add to map with key = ivfSheet.getUniqueID()
   └─> If no match: Patient not found

5. Lookup Check
   └─> espatients.get("Office1_100")
   └─> If null: Trigger "no rule" message
   └─> If not null: Proceed with rule validation
```

## Related Constants

- `Constants.INSURANCE_TYPE_PRI = "Primary"`
- `Constants.INSURANCE_TYPE_SEC = "Secondary"`
- `Constants.errorMessOPen` - HTML formatting for error messages
- `Constants.errorMessClose` - HTML formatting for error messages
- `Constants.FAIL` - Result type for failed validation

## Related Rules

This "no rule" message is related to:
- **Rule4:** "Compare Coverage Book" - Requires patient data to compare coverage book and fee schedule
- **Rule5:** "Remaining Deductible" - Requires patient data to check remaining deductible
- **Rule76:** Patient Name validation
- **Rule77:** Patient DOB validation
- **Rule78:** Patient address validation
- **Rule83:** Policy holder validation

All these rules require patient data from EagleSoft, so if patient is not found, they cannot execute.
