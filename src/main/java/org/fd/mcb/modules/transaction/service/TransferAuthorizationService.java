package org.fd.mcb.modules.transaction.service;

import org.fd.mcb.modules.transaction.dto.request.TransferAuthRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferAuthResponse;

public interface TransferAuthorizationService {

    TransferAuthResponse authorizeTransfer(TransferAuthRequest request);

    void releaseHold(String authCode);

    void expireHolds();

}
