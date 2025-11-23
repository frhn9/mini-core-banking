package org.fd.mcb.modules.transaction.controller;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.transaction.dto.request.DepositWithdrawReq;
import org.fd.mcb.modules.transaction.dto.request.TransferAuthRequest;
import org.fd.mcb.modules.transaction.dto.request.TransferCancellationRequest;
import org.fd.mcb.modules.transaction.dto.request.TransferCaptureRequest;
import org.fd.mcb.modules.transaction.dto.request.TransferSettlementRequest;
import org.fd.mcb.modules.transaction.dto.response.AccountResponse;
import org.fd.mcb.modules.transaction.dto.response.TransferAuthResponse;
import org.fd.mcb.modules.transaction.dto.response.TransferCancellationResponse;
import org.fd.mcb.modules.transaction.dto.response.TransferCaptureResponse;
import org.fd.mcb.modules.transaction.dto.response.TransferSettlementResponse;
import org.fd.mcb.modules.transaction.service.AccountService;
import org.fd.mcb.modules.transaction.service.TransferAuthorizationService;
import org.fd.mcb.modules.transaction.service.TransferCancellationService;
import org.fd.mcb.modules.transaction.service.TransferCaptureService;
import org.fd.mcb.modules.transaction.service.TransferSettlementService;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.response.template.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final ResponseHelper responseHelper;
    private final AccountService accountService;
    private final TransferAuthorizationService transferAuthorizationService;
    private final TransferCaptureService transferCaptureService;
    private final TransferSettlementService transferSettlementService;
    private final TransferCancellationService transferCancellationService;

    @PostMapping("/deposit")
    public ResponseEntity<ResponseData<AccountResponse>> deposit(
            @RequestBody DepositWithdrawReq request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                accountService.deposit(request)
        );
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<ResponseData<AccountResponse>> withdrawal(
            @RequestBody DepositWithdrawReq request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                accountService.withdrawal(request)
        );
    }

    @PostMapping("/transfer/authorize")
    public ResponseEntity<ResponseData<TransferAuthResponse>> authorizeTransfer(
            @RequestBody TransferAuthRequest request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                transferAuthorizationService.authorizeTransfer(request)
        );
    }

    @PostMapping("/transfer/capture")
    public ResponseEntity<ResponseData<TransferCaptureResponse>> captureTransfer(
            @RequestBody TransferCaptureRequest request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                transferCaptureService.captureTransfer(request)
        );
    }

    @PostMapping("/transfer/settle")
    public ResponseEntity<ResponseData<TransferSettlementResponse>> settleTransfer(
            @RequestBody TransferSettlementRequest request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                transferSettlementService.settleTransfer(request)
        );
    }

    @PostMapping("/transfer/cancel")
    public ResponseEntity<ResponseData<TransferCancellationResponse>> cancelTransfer(
            @RequestBody TransferCancellationRequest request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                transferCancellationService.cancelTransfer(request)
        );
    }

}
