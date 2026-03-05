# Finding "SEDATION 9245" (D9245) in EagleSoft for Rule 6 – Percent Coverage Match

Rule 6 compares coverage **percentages** on the IVF form to the **Employer Master / Benefits** data in EagleSoft. The rule looks up each coverage category by **name** in the list of benefits for the patient’s **employer/plan**.  
The error **"ES Names in Employer Master not found - SEDATION 9245-D9245_Sedation_%"** means the Rule Engine did not find a benefit row whose **service type description** is exactly **"SEDATION 9245"** for that plan.

In the Rule Engine, this data comes from the **employer**, **benefits**, and **service_type** tables in EagleSoft. The text the rule matches is the **description** from **service_type** (one row per benefit category, e.g. "Major", "Crowns", "SEDATION 9245").

---

## Step-by-step: where to look in EagleSoft

EagleSoft’s menus can vary by version and configuration. Use these as a guide and adjust to your screens.

### 1. Identify the patient’s plan (employer)

- Open the **patient** in EagleSoft (e.g. **Patient** or **Family** module).
- Note the **Primary** (and if needed **Secondary**) **insurance/plan**.
- Note the **Employer** and **Group #** if shown (this often identifies the “employer”/plan in the database).

You need to work with the **same** employer/plan that the Rule Engine uses for that patient (usually primary for pre-batch validation).

---

### 2. Open Employer / Plan setup

In EagleSoft, benefits and percentages are usually under one of:

- **Insurance** or **Reference** → **Employer** (or **Employers**)
- **Insurance** → **Coverage** / **Benefits** / **Plan setup**
- **Reference** → **Insurance** → **Employer** then **Benefits** or **Coverage**
- Or a **Benefits** / **Coverage Book** type screen tied to the employer/plan

Open the **employer** (or plan) that matches the patient’s insurance.

---

### 3. Find the list of benefit categories (service types)

Once you’re in that employer/plan:

- Look for a **list of benefit categories** or **coverage types** with **percentages** (e.g. Major 50%, Preventive 100%, Crowns 50%).
- This list often has:
  - A **name/description** for each row (e.g. "Major", "Crowns", "Preventive", "Nitrous", "IV Sedation", etc.)
  - A **percentage** (and sometimes deductible, maximum, etc.)

That list corresponds to the **benefits** tied to the **service_type** table; the **name/description** you see is what the Rule Engine matches as **"service type description"**.

---

### 4. Check for D9245 / IV Sedation (SEDATION 9245)

- In that list, look for a row that is clearly **IV Sedation** or **D9245** or similar.
- Possible labels in EagleSoft might be:
  - **SEDATION 9245**
  - **IV Sedation**
  - **Sedation 9245**
  - **D9245**
  - Or another wording your practice uses.

**What you need to note:**

- **Is there any row** for IV Sedation / D9245?
  - **If NO** → That’s why Rule 6 fails. You need to **add** this benefit category for that employer/plan in EagleSoft (and set the correct percentage), **or** remove/adjust the D9245 percentage on the IVF so the rule doesn’t expect it.
  - **If YES** → Note the **exact** text of that row (the “service type description”) as it appears in EagleSoft.

---

### 5. Make the name match what Rule 6 expects

Rule 6 currently looks for the **exact** string **"SEDATION 9245"** (case-insensitive in code, but spacing must match).

- **Option A – Change EagleSoft**
  - In the benefit/service type setup for that employer/plan, set the **description** for the D9245 row to exactly **SEDATION 9245** (same spelling and spaces as in the rule).  
  - Then re-export or re-sync so the Rule Engine gets the updated data.

- **Option B – Change the Rule Engine**
  - If EagleSoft uses a different label (e.g. "IV Sedation" or "Sedation D9245"), you can change the rule so it looks for that label instead of "SEDATION 9245".  
  - In the codebase, the label is set in **RuleBook.java** in the Rule 6 section, in the line that adds the D9245 row, e.g.  
    `new Rule6Dto("D9245_Sedation_%", ivf.getiVSedationD9245Percentage(), "SEDATION 9245")`  
  - The **third argument** is the string matched to EagleSoft’s **service type description**. Change it to the **exact** text from EagleSoft (e.g. `"IV Sedation"` or `"Sedation D9245"`).

---

## If you have database access to EagleSoft

The Rule Engine uses data that comes from the **employer**, **benefits**, and **service_type** tables. You can see what the Rule Engine sees by running the equivalent of:

```sql
SELECT emp.employer_id, emp.name AS employername, emp.group_number,
       ser.service_type_id, ser.description AS servicetypedescription, ben.percentage
FROM employer emp
JOIN benefits ben ON ben.employer_id = emp.employer_id
JOIN service_type ser ON ben.service_type_id = ser.service_type_id
WHERE emp.employer_id = '<patient_employer_id>';
```

Replace `<patient_employer_id>` with the employer ID for the patient’s plan (you can get that from the patient/insurance data in ES).

- Look at the **servicetypedescription** column: that is what Rule 6 matches.
- If there is **no row** with a description like "SEDATION 9245" or "IV Sedation", you need to add that benefit in EagleSoft (or in **service_type** + **benefits** if you manage it via DB).
- If there **is** a row but with a different description (e.g. "IV Sedation"), then either:
  - Change that description in ES to **SEDATION 9245**, or  
  - Change the Rule 6 code to use that exact description instead of "SEDATION 9245".

---

## Summary

| What you need | Where in EagleSoft |
|---------------|---------------------|
| **Employer/plan** for the patient | Patient → Insurance (Primary/Secondary) |
| **List of benefit categories and %** | Insurance/Reference → Employer → Benefits or Coverage (or similar) |
| **Exact name for D9245 row** | The “description” / “service type” text for the IV Sedation row in that list |

Once that exact name matches **"SEDATION 9245"** (or you change the rule to match the name you see), Rule 6 should find the row and only then check that the **percentage** matches the IVF.
