package org.fd.mcb.modules.reconciliation.enums;

public enum AccountTier {
    NORMAL,            // No restrictions
    TIER_2_RESTRICTED, // Minor discrepancy: max withdrawal $5000, daily limit $10000
    TIER_3_LIMITED,    // Medium discrepancy: max withdrawal $2000, max transfer $2000, daily limit $5000
    TIER_4_MINIMAL,    // High discrepancy: max withdrawal $500, daily limit $1000
    TIER_5_BLOCKED     // Critical: Hard block, no transactions allowed
}
