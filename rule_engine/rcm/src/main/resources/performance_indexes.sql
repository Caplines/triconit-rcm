-- ============================================================
-- Performance indexes to resolve sorting/filtering latency
-- Run once against the target database (dev / docker / prod).
-- Each statement is wrapped in a duplicate-check guard so it
-- is safe to run more than once.
-- ============================================================

-- 1. rcm_claims: composite index covering the WHERE clause used by all
--    UUID-only queries (current_team_id, current_state, current_status)
--    plus the two default-sort columns.  MySQL can satisfy the entire
--    WHERE + ORDER BY from this index without touching the table heap.
-- ----------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claims_team_state_status_sort
    ON rcm_claims (current_team_id, current_state, current_status, sort_priority, prime_sec_submitted_total);

-- 2. rcm_claims: index for the office-join filter (office_id) so the
--    INNER JOIN to the office table can use a ref lookup.
-- ----------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claims_office_id
    ON rcm_claims (office_id);

-- 3. rcm_claims: index covering first_worked_team_id and last_work_team_id
--    used in fresh-claim WHERE conditions.
-- ----------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claims_worked_teams
    ON rcm_claims (first_worked_team_id, last_work_team_id);

-- 4. rcm_claim_cycle: composite index on (claim_id, status_updated) used by
--    the correlated subquery inside fetchClaimDataByUuids:
--      SELECT claim_id, MIN(id) FROM rcm_claim_cycle
--      WHERE status_updated = '...' AND claim_id IN (...)
--      GROUP BY claim_id
-- ----------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claim_cycle_claim_status
    ON rcm_claim_cycle (claim_id, status_updated);

-- 5. rcm_claim_assignment: index on (claim_id, active) to speed up
--    every LEFT/INNER JOIN on rca.claim_id = claims.claim_uuid AND rca.active = 1.
-- ----------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claim_assignment_claim_active
    ON rcm_claim_assignment (claim_id, active);

-- 6. rcm_claim_assignment: index for the "last comment" query used by
--    populateLastRemarks (claim_id, current_team_id).
-- ----------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rcm_claim_assignment_claim_team
    ON rcm_claim_assignment (claim_id, current_team_id);
