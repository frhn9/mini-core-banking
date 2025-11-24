package org.fd.mcb.modules.transaction.service;

import org.fd.mcb.modules.transaction.dto.request.TransferCaptureRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferCaptureResponse;

public interface TransferCaptureService {

    TransferCaptureResponse captureTransfer(TransferCaptureRequest request);

}
