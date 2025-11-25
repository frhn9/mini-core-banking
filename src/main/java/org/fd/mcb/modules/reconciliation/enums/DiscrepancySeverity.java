package org.fd.mcb.modules.reconciliation.enums;

public enum DiscrepancySeverity {
    LOW,      // < $10
    MEDIUM,   // $10 - $100
    HIGH,     // $100 - $1000
    CRITICAL  // > $1000
}
