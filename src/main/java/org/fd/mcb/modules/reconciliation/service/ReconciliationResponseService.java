package org.fd.mcb.modules.reconciliation.service;

import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;

public interface ReconciliationResponseService {

    /**
     * Handle a discrepancy using the hybrid tiered response strategy
     * @param discrepancy The discrepancy to handle
     */
    void handleDiscrepancy(ReconciliationDiscrepancy discrepancy);

    /**
     * Unblock an account after manual review
     * @param accountId The account ID to unblock
     * @param justification The justification for unblocking
     */
    void unblockAccount(Long accountId, String justification);
}
