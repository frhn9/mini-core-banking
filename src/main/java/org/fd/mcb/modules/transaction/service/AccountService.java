package org.fd.mcb.modules.transaction.service;

import org.fd.mcb.modules.transaction.dto.request.DepositWithdrawReq;
import org.fd.mcb.modules.transaction.dto.response.AccountResponse;

import java.util.concurrent.ExecutionException;

public interface AccountService {

    AccountResponse deposit(DepositWithdrawReq request);

    AccountResponse withdrawal(DepositWithdrawReq request);

}
