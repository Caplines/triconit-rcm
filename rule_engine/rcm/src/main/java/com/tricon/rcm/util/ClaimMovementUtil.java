package com.tricon.rcm.util;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.enums.RcmTeamEnum;

/**
 * Not used now
 * @author Deepak.Dogra
 *
 */
public class ClaimMovementUtil {

	public static final String nextAction4 = ClaimStatusEnum.Statement.getType();
	//public static final String nextAction5 = ClaimStatusEnum.Claim_Closed.getType();
	public static final String nextAction6 = ClaimStatusEnum.Bill_to_Secondary.getType();

	public static final String Not_settled_in_time = "Not settled in time";

	public static String getNextStatus(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) throws Exception {

		String status = null;
		if (currentClaimTeamId == RcmTeamEnum.BILLING.getId()) {
			status = getBillingNextStatus(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);

		} else if (currentClaimTeamId == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
			status = getInternalAuditNextStatus(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);
		} else if (currentClaimTeamId == RcmTeamEnum.PAYMENT_POSTING.getId()) {
			status = getPaymentPostingNextStatus(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);
		}

		return status;
	}

	public static int getNextTeam(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		int nextTeam = -1;
		if (currentClaimTeamId == RcmTeamEnum.BILLING.getId()) {
			nextTeam = getBillingNextTeam(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);

		} else if (currentClaimTeamId == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
			nextTeam = getInternalAuditNextTeam(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);
		} else if (currentClaimTeamId == RcmTeamEnum.PAYMENT_POSTING.getId()) {
			nextTeam = getPaymentPostingNextTeam(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);
		}

		return nextTeam;
	}

	public static ClaimStatusEnum getNextAction(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		ClaimStatusEnum action = null;
		if (currentClaimTeamId == RcmTeamEnum.BILLING.getId()) {
			action = getBillingNextAction(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);

		} else if (currentClaimTeamId == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
			action = getInternalAuditNextAction(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);
		} else if (currentClaimTeamId == RcmTeamEnum.PAYMENT_POSTING.getId()) {
			action = getPaymentPostingNextAction(currentClaimTeamId, claim, secondaryClaim, checkForPrimary);
		}

		return action;
	}

	// Internal Audit- START
	public static String getInternalAuditNextStatus(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		if (currentClaimTeamId == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
			return ClaimStatusEnum.Need_to_Audit.getType();
		}
		return null;
	}

	public static int getInternalAuditNextTeam(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		if (currentClaimTeamId == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
			return RcmTeamEnum.BILLING.getId();
		}
		return -1;
	}

	public static ClaimStatusEnum getInternalAuditNextAction(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		if (currentClaimTeamId == RcmTeamEnum.INTERNAL_AUDIT.getId()) {
			return ClaimStatusEnum.Review;
		}
		return null;
	}

	// Internal Audit- END
	// Billing- START
	public static String getBillingNextStatus(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) throws Exception {

		//boolean isPrimary = claim != null ? true : false;
		//boolean isSecondaryPresent = secondaryClaim != null ? true : false;
		if (currentClaimTeamId != RcmTeamEnum.BILLING.getId()) {
			return null;
		}
		return ClaimStatusEnum.Billing.getType();
		/*
		if (isPrimary && isSecondaryPresent) {

			return ClaimStatusEnum.Primary_Closed.getType();// PC
		}

		if (!isPrimary) {
			return ClaimStatusEnum.Primary_Closed.getType();
		}

		return ClaimStatusEnum.Awaiting_Insurance_Settlement.getType();
        */
	}

	public static int getBillingNextTeam(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		if (currentClaimTeamId == RcmTeamEnum.BILLING.getId()) {
			return RcmTeamEnum.PAYMENT_POSTING.getId();
		}
		return -1;
	}

	public static ClaimStatusEnum getBillingNextAction(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		return ClaimStatusEnum.Need_to_Bill;

	}
	// Billing- END

	// Payment Posting- START
	public static String getPaymentPostingNextStatus(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) throws Exception {

		return null;
	}

	public static int getPaymentPostingNextTeam(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		boolean isPrimary = claim != null ? true : false;
		boolean isSecondaryPresent = secondaryClaim != null ? true : false;
		String type = claim.getClaimType();
		if (type == null)
			type = "";
		if (currentClaimTeamId != RcmTeamEnum.PAYMENT_POSTING.getId()) {
			return -1;
		}
		if (type.toLowerCase().contains("ortho")) {
			if (checkForPrimary) {
				if (claim.getPaymentReceived() != claim.getPrimeSecSubmittedTotal()) {
					if (isSecondaryPresent) {
						ClaimStatusEnum en = ClaimStatusEnum.getById(secondaryClaim.getCurrentStatus());
						if (en != null && en.getType().equalsIgnoreCase("Closed")) {
							return RcmTeamEnum.PATIENT_STATEMENT.getId();
						}
					}
				}
			} else if (isPrimary && isSecondaryPresent) {
				float pr = secondaryClaim.getPaymentReceived() + claim.getPaymentReceived();
				float st = secondaryClaim.getSecSubmittedTotal() + claim.getPrimeSecSubmittedTotal();
				if (pr != st) {
					return RcmTeamEnum.PATIENT_STATEMENT.getId();
				}
			}
		}
		// Other
		if (checkForPrimary) {
			if (claim.getBtp() > 0) {
				if (isSecondaryPresent) {
					ClaimStatusEnum en = ClaimStatusEnum.getById(secondaryClaim.getCurrentStatus());
					if (en != null && en.getType().equalsIgnoreCase("Closed")) {
						return RcmTeamEnum.PATIENT_STATEMENT.getId();
					}
				}
			}

		} else if (isPrimary && isSecondaryPresent) {
			float btp = claim.getBtp() + secondaryClaim.getBtp();
			if (btp > 0) {
				return RcmTeamEnum.PATIENT_STATEMENT.getId();
			}
		}

		return -1;

	}

	public static ClaimStatusEnum getPaymentPostingNextAction(int currentClaimTeamId, RcmClaims claim, RcmClaims secondaryClaim,
			boolean checkForPrimary) {

		// boolean isPrimary = claim!=null?true:false;
		boolean isSecondaryPresent = secondaryClaim != null ? true : false;

		String type = claim.getClaimType();
		if (type == null)
			type = "";
		if (currentClaimTeamId != RcmTeamEnum.PAYMENT_POSTING.getId()) {
			return null;
		}
		if (type.toLowerCase().contains("ortho")) {
			if (checkForPrimary) {
				if (claim.getPaymentReceived() != claim.getPrimeSecSubmittedTotal()) {
					return ClaimStatusEnum.Need_to_followup;
				}
			} else if (isSecondaryPresent) {
				float pr = secondaryClaim.getPaymentReceived() + claim.getPaymentReceived();
				float st = secondaryClaim.getSecSubmittedTotal() + claim.getPrimeSecSubmittedTotal();
				if (pr != st) {
					return ClaimStatusEnum.Need_to_followup;
				}
			} /*
				 * else if (isPrimary && isSecondaryPresent) { if (claim.getPaymentReceived() !=
				 * claim.getPrimeSecSubmittedTotal()) { return
				 * ClaimStatusEnum.Need_to_followup_Close.getType(); } } else if (!isPrimary)
				 * {// Secondary float pr = secondaryClaim.getPaymentReceived() +
				 * claim.getPaymentReceived(); float st = secondaryClaim.getSecSubmittedTotal()
				 * + claim.getPrimeSecSubmittedTotal(); if (pr != st) { return
				 * ClaimStatusEnum.Need_to_followup.getType(); } } else if (isPrimary) {// only
				 * primary if (claim.getPaymentReceived() != claim.getPrimeSecSubmittedTotal())
				 * { return ClaimStatusEnum.Need_to_followup.getType(); } }
				 */
		}
		// Other
		if (checkForPrimary) {
			if (claim.getPaymentReceived() != claim.getPrimeSecSubmittedTotal()) {
				return ClaimStatusEnum.Need_to_followup;
			}
		} else if (isSecondaryPresent) {
			float btp = claim.getBtp() + secondaryClaim.getBtp();
			if (btp > 0) {
				return ClaimStatusEnum.Statement;
			}
		} /*
			 * if (isPrimary && isSecondaryPresent) { if (claim.getBtp() > 0) { return
			 * ClaimStatusEnum.Statement_CLOSE.getType(); } } else if (!isPrimary) {//
			 * Secondary float btp = claim.getBtp() + secondaryClaim.getBtp(); if (btp > 0)
			 * { return ClaimStatusEnum.Statement.getType(); } } else if (isPrimary) {//
			 * only primary if (claim.getPaymentReceived() !=
			 * claim.getPrimeSecSubmittedTotal()) { return
			 * ClaimStatusEnum.Statement.getType(); } }
			 */

		return ClaimStatusEnum.Close_The_claim;
	}
	// Payment Posting- END

}
