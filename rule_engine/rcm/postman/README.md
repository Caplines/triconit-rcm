# RCM API – Postman collection

Use this folder to call the **rule_engine/rcm** Spring Boot APIs from Postman.

## Import in Postman

1. **Collection**  
   - In Postman: **File → Import** (or drag and drop).  
   - Select: `RCM-API.postman_collection.json`  
   - All RCM API requests are grouped in folders (Auth, RCM - Claims, User, Admin, Master, etc.).

2. **Environment (optional)**  
   - **File → Import** → select `RCM-API.postman_environment.json`.  
   - In the top-right environment dropdown, choose **RCM Local** (or the name you gave the env).  
   - Edit the environment to set `base_url`, `token`, `c`, `r`, `t` as needed.

## Variables

| Variable   | Description |
|-----------|-------------|
| `base_url` | Server base URL (e.g. `http://localhost:8081`) |
| `token`    | JWT token from login (set after calling **Auth → Login**) |
| `c`        | Client name (header for API context) |
| `r`        | Role (e.g. TL, ADMIN, SUPER_ADMIN) |
| `t`        | Team ID (number) |

## How to hit the APIs

1. Start the RCM backend (e.g. run `RcmApplication` or `mvn spring-boot:run` in `rule_engine/rcm`).
2. **Login**  
   - Use **Auth → Login (no captcha - testing)** (`POST /account/l_abc`) with body:  
     `{"username": "your_user", "password": "your_pass"}`  
   - Or **Auth → Login (with captcha)** (`POST /account/login`) with `username`, `password`, and `token` (reCAPTCHA token).
3. Copy the JWT from the login response into the collection/environment variable **token** (or use a Postman test script to set it automatically).
4. Set **c**, **r**, **t** in the environment to match your client, role, and team.
5. Call any other request; the collection is configured to send **Authorization: Bearer {{token}}** and headers **c**, **r**, **t** where required.

## Folder summary

- **Auth** – Login, refresh token, forgot password, bootstrap SUPER_ADMIN (no token).
- **RCM - Claims** – Fetch/assign/archive claims, pendency, reconciliation, sections, rules, etc.
- **User** – Update password, offices, teams, issue/archive counts.
- **Admin** – Register, reset password, users, clients, offices, roles, teams.
- **Master** – Offices, teams, roles, clients, sections (no auth in current backend).
- **Manage Office** – Assign office, users by team.
- **Search** – Clients with offices, search claims, search claims PDF.
- **Claim Sections** – Client/user section mapping, claim/appeal/EOB/rebilling info.
- **Attachments** – Upload/get/remove claim attachments.
- **Download** – PDF downloads (list of claims, issue claim, pendancy, production, etc.).
- **Other** – View EOB link, test SV Sheet.

Path for import: **`rule_engine/rcm/postman/`**
