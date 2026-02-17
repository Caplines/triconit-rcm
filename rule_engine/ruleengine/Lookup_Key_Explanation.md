# Lookup Key Explanation

## What is the Lookup Key?

The **lookup key** (also called `patKey`) is a **composite string key** used to uniquely identify and retrieve patient data from the `espatients` map. It combines two pieces of information to create a unique identifier.

## Format

```
patKey = officeName + "_" + ivfId
```

**Example:**
- Office Name: `"SmilePoint Dental"`
- IVF ID: `"12345"`
- Lookup Key: `"SmilePoint Dental_12345"`

## Code Location

**File:** `TreatmentPlanServiceImpl.java`  
**Line:** 4938

```java
String ivx = ids[y];  // IVF ID from the input array
String patKey = off.getName() + "_" + ivx;
```

## Why This Format?

### 1. **Uniqueness Across Offices**
- IVF IDs might be reused across different offices
- Example: Office A might have IVF ID "100", and Office B might also have IVF ID "100"
- By including office name, we ensure uniqueness: `"OfficeA_100"` vs `"OfficeB_100"`

### 2. **Matching with Database Return Map**
- When patient data is fetched from EagleSoft database, it's stored in a map
- The map uses `ivfSheet.getUniqueID()` as the key
- The `uniqueID` field in IVFTableSheet has the same format: `officeName + "_" + ivfId`
- This ensures the lookup key matches the key used in the database return map

## How It's Used

### 1. **Patient Data Lookup**

```java
// Line 4941: Check if patient exists
if (espatients != null && espatients.get(patKey) != null 
        && espatients.get(patKey).get(0) != null) {
    // Patient found - proceed with rules
    empMasterKey = espatients.get(patKey).get(0).getEmployerId();
} else {
    // Patient not found - trigger "no rule" message
    dtoRL = rb.patientNotFound((IVFTableSheet) ivfMap.get(ivx).get(0), messageSource);
}
```

### 2. **Accessing Patient Data for Rules**

```java
// Line 4988: Pass patient data to Rule4
dtoRL = rb.Rule4_A(ivfMap.get(ivx).get(0), messageSource, rule,
        espatients.get(patKey),  // <-- Using lookup key here
        preferanceFeeSchedules==null?null:preferanceFeeSchedules.get(patKey), 
        null, userName);

// Line 5005: Pass patient data to Rule5
dtoRL = rb.Rule5(null, ivfMap.get(ivx).get(0), messageSource, rule, 
        espatients.get(patKey),  // <-- Using lookup key here
        null, type, iType);
```

## Data Flow

### Step 1: IVF Form Processing
```
Input: IVF ID = "12345"
Office: "SmilePoint Dental"
```

### Step 2: Lookup Key Construction
```java
String patKey = "SmilePoint Dental" + "_" + "12345";
// Result: "SmilePoint Dental_12345"
```

### Step 3: Patient Data Retrieval
```java
// getPatientData() queries EagleSoft database
// Returns: Map<String, List<EagleSoftPatient>>
// Key in map: ivfSheet.getUniqueID() = "SmilePoint Dental_12345"
// Value: List of EagleSoftPatient objects
```

### Step 4: Patient Data Lookup
```java
// Use lookup key to retrieve patient data
List<EagleSoftPatient> patients = espatients.get("SmilePoint Dental_12345");
```

### Step 5: Rule Validation
```java
// Pass patient data to rules for validation
if (patients != null && patients.size() > 0) {
    // Execute rules with patient data
} else {
    // Patient not found - show error
}
```

## Key Components

### 1. Office Name (`off.getName()`)
- **Source:** Office entity from database
- **Purpose:** Identifies which office the IVF form belongs to
- **Example:** `"SmilePoint Dental"`, `"ABC Dental Clinic"`

### 2. IVF ID (`ivx`)
- **Source:** From input array `ids[y]` in `validateTreatmentPlanPreBatch`
- **Purpose:** Identifies the specific IVF form
- **Format:** Usually a numeric string like `"12345"`, `"67890"`
- **Note:** Can also be extracted from `uniqueID` using `split("_")[1]`

### 3. Separator (`"_"`)
- **Purpose:** Separates office name and IVF ID
- **Why underscore?:** Standard separator that's unlikely to appear in office names or IDs

## Relationship with UniqueID

The lookup key format matches the `uniqueID` field in `IVFTableSheet`:

```java
// IVFTableSheet.uniqueID format
uniqueID = officeName + "_" + ivfId

// Example: "SmilePoint Dental_12345"

// When stored in database return map
returnMap.put(ivfSheet.getUniqueID(), list);  
// Key: "SmilePoint Dental_12345"

// When looking up
espatients.get(patKey)  
// patKey: "SmilePoint Dental_12345"
// Matches the key in the map!
```

## Important Notes

1. **Case Sensitivity:** The lookup is case-sensitive. `"OfficeA_100"` ≠ `"officea_100"`

2. **Key Mismatch:** If the lookup key doesn't match the key used in the database return map, patient data won't be found, even if the patient exists in the database.

3. **Comment in Code:** There's an important comment at line 4936:
   ```java
   // IMPORTANT --SEE THIS --WHY DONE in IVF sheet need to match Officename_IVFID
   ```
   This emphasizes that the IVF sheet's uniqueID must match the format `officeName_IVFID` for the lookup to work.

4. **Multiple Patients:** The map stores `List<EagleSoftPatient>`, so multiple patients can be associated with the same IVF form (though typically there's one).

## Example Scenario

```
Scenario: Processing IVF form for patient validation

1. Input:
   - Office: "SmilePoint Dental"
   - IVF ID: "12345"
   - Patient ID from IVF: "P001"

2. Lookup Key Construction:
   patKey = "SmilePoint Dental_12345"

3. Database Query:
   - Query EagleSoft for patient with ID "P001"
   - If found, store in map with key = "SmilePoint Dental_12345"

4. Lookup:
   - espatients.get("SmilePoint Dental_12345")
   - If found: Patient data retrieved successfully
   - If not found: "Patient not found" error triggered

5. Rule Execution:
   - If patient found: Execute rules (Rule4, Rule5, Rule76, etc.)
   - If patient not found: Show "no rule" error message
```

## Summary

The lookup key is a **composite identifier** that:
- Uniquely identifies a patient record for a specific IVF form from a specific office
- Matches the format used in the database return map (`uniqueID`)
- Enables efficient retrieval of patient data from the `espatients` map
- Is essential for rule validation to proceed

Without a matching lookup key, the system cannot find the patient data, resulting in the "Patient Details not found" error message.
