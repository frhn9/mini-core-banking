package org.fd.mcb.modules.transaction.controller;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.transaction.dto.request.DepositWithdrawReq;
import org.fd.mcb.modules.transaction.dto.request.TransferRequest;
import org.fd.mcb.modules.transaction.dto.response.AccountResponse;
import org.fd.mcb.modules.transaction.dto.response.TransferResponse;
import org.fd.mcb.modules.transaction.service.TransferService;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.response.template.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final ResponseHelper responseHelper;
    private final TransferService transferService;

    @PostMapping("/payment/auth")
    public ResponseEntity<ResponseData<TransferResponse>> authPayment(
            @RequestBody TransferRequest request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                transferService.authPayment(request)
        );
    }

}
