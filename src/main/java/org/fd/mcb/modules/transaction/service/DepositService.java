package org.fd.mcb.modules.transaction.service;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.transaction.dto.request.DepositRequest;
import org.fd.mcb.modules.transaction.dto.response.DepositResponse;
import org.fd.mcb.modules.transaction.handler.TransactionHandler;
import org.fd.mcb.shared.util.TransactionUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final TransactionHandler transactionHandler;

    public DepositResponse deposit(Long bankAccountId, DepositRequest request) {
        TransactionUtil.validateInvalidAmount(request.getAmount());
        return transactionHandler.deposit(bankAccountId, request.getAmount());
    }
}
