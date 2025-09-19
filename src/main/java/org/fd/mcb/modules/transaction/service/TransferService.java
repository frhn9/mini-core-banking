package org.fd.mcb.modules.transaction.service;

import org.fd.mcb.modules.transaction.dto.request.TransferRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferResponse;

public interface TransferService {
    TransferResponse authPayment(TransferRequest request);
}
