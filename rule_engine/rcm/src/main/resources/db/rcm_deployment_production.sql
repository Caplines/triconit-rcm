-- =============================================================================
-- RCM production deployment — schema + indexes
-- Run against the target MySQL database before or after deploying the RCM WAR.
-- Review MySQL version: CREATE INDEX IF NOT EXISTS requires MySQL 8.0.29+.
-- On older MySQL, run each CREATE INDEX without IF NOT EXISTS and ignore
-- "Duplicate key name" errors, or drop indexes manually first.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 0. Column: sort_priority on rcm_claims (required by UUID list ORDER BY)
--    Skip if you already have the column (Error 1060 Duplicate column).
-- -----------------------------------------------------------------------------
-- ALTER TABLE rcm_claims
--   ADD COLUMN sort_priority INT NOT NULL DEFAULT 0
--   AFTER prime_sec_submitted_total;

-- -----------------------------------------------------------------------------
-- 1. rcm_claims: composite for UUID queries (WHERE + ORDER BY sort columns)
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claims_team_state_status_sort
    ON rcm_claims (current_team_id, current_state, current_status, sort_priority, prime_sec_submitted_total);

-- -----------------------------------------------------------------------------
-- 2. rcm_claims: office join / filters
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claims_office_id
    ON rcm_claims (office_id);

-- -----------------------------------------------------------------------------
-- 3. rcm_claims: fresh-claim team predicates
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claims_worked_teams
    ON rcm_claims (first_worked_team_id, last_work_team_id);

-- -----------------------------------------------------------------------------
-- 4. rcm_claims: supports secondary-filter EXISTS (office_id + patient_id match)
--    Safe to add; helps prim lookup used in SQL_EXCLUDE_HIDDEN_SECONDARY_*.
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claims_office_patient
    ON rcm_claims (office_id, patient_id);

-- -----------------------------------------------------------------------------
-- 5. rcm_claim_cycle: MIN(id) / status_updated subqueries + claim linkage
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claim_cycle_claim_status
    ON rcm_claim_cycle (claim_id, status_updated);

-- -----------------------------------------------------------------------------
-- 6. rcm_claim_assignment: joins on claim_id + active
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claim_assignment_claim_active
    ON rcm_claim_assignment (claim_id, active);

-- -----------------------------------------------------------------------------
-- 7. rcm_claim_assignment: last-comment batch (populateLastRemarks)
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claim_assignment_claim_team
    ON rcm_claim_assignment (claim_id, current_team_id);
