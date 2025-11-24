package org.fd.mcb.modules.transaction.service;

import org.fd.mcb.modules.transaction.dto.request.TransferSettlementRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferSettlementResponse;

public interface TransferSettlementService {

    TransferSettlementResponse settleTransfer(TransferSettlementRequest request);

}
