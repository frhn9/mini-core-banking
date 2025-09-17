package org.fd.mcb.modules.transaction.controller;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.transaction.dto.request.DepositRequest;
import org.fd.mcb.modules.transaction.dto.response.DepositResponse;
import org.fd.mcb.modules.transaction.service.DepositService;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.response.template.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class DepositController {

    private final ResponseHelper responseHelper;
    private final DepositService depositService;

    @PostMapping("/{bankAccountId}/deposits")
    public ResponseEntity<ResponseData<DepositResponse>> deposit(
            @PathVariable Long bankAccountId,
            @RequestBody DepositRequest request
    ) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                depositService.deposit(bankAccountId, request)
        );
    }
}
