package org.fd.mcb.modules.transaction.service;

import org.fd.mcb.modules.transaction.dto.request.TransferCancellationRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferCancellationResponse;

public interface TransferCancellationService {

    TransferCancellationResponse cancelTransfer(TransferCancellationRequest request);

}
